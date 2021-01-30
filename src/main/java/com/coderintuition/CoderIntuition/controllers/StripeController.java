package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.config.AppProperties;
import com.coderintuition.CoderIntuition.enums.ERole;
import com.coderintuition.CoderIntuition.models.CheckoutSession;
import com.coderintuition.CoderIntuition.models.Role;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.pojos.request.CheckoutSessionRequest;
import com.coderintuition.CoderIntuition.pojos.response.CheckoutSessionResponse;
import com.coderintuition.CoderIntuition.pojos.response.GetCheckoutSessionResponse;
import com.coderintuition.CoderIntuition.pojos.response.PortalSessionResponse;
import com.coderintuition.CoderIntuition.repositories.CheckoutSessionRepository;
import com.coderintuition.CoderIntuition.repositories.RoleRepository;
import com.coderintuition.CoderIntuition.repositories.UserRepository;
import com.coderintuition.CoderIntuition.security.CurrentUser;
import com.coderintuition.CoderIntuition.security.UserPrincipal;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Set;

@RestController
@RequestMapping("/stripe")
public class StripeController {
    Logger logger = LoggerFactory.getLogger(StripeController.class);

    @Autowired
    AppProperties appProperties;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CheckoutSessionRepository checkoutSessionRepository;

    @GetMapping("/checkout-session/{sessionId}")
    public GetCheckoutSessionResponse getCheckoutSession(@PathVariable String sessionId) throws Exception {
        Stripe.apiKey = appProperties.getStripe().getTestKey();

        CheckoutSession checkoutSession = checkoutSessionRepository.findBySessionId(sessionId).orElseThrow();
        Session session = Session.retrieve(checkoutSession.getSessionId());
        if (session.getPaymentStatus().equals("unpaid")) {
            throw new Exception();
        }
        return new GetCheckoutSessionResponse(checkoutSession.getUser().getName().split(" ")[0]);
    }

    @PostMapping("/checkout-session")
    @PreAuthorize("hasRole('ROLE_USER')")
    public CheckoutSessionResponse createCheckoutSession(@CurrentUser UserPrincipal userPrincipal,
                                                         @RequestBody CheckoutSessionRequest checkoutSessionRequest) throws Exception {
        Stripe.apiKey = appProperties.getStripe().getTestKey();

        User user = userRepository.findById(userPrincipal.getId()).orElseThrow();
        Role plusRole = roleRepository.findByName(ERole.ROLE_PLUS).orElseThrow();
        if (user.getRoles().contains(plusRole)) {
            throw new Exception("You are already on the Intuition+ plan");
        }

        if (user.getStripeCustomerId() == null) {
            CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(user.getName())
                .setEmail(user.getEmail())
                .build();
            Customer customer = Customer.create(params);
            user.setStripeCustomerId(customer.getId());
        }

        SessionCreateParams params = new SessionCreateParams.Builder()
            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
            .setSuccessUrl("https://coderintuition.com/checkout-success")
            .setCancelUrl("https://coderintuition.com/plus")
            .setCustomerEmail(user.getEmail())
            .addLineItem(new SessionCreateParams.LineItem.Builder()
                .setQuantity(1L)
                .setPrice(checkoutSessionRequest.getPriceId())
                .build()
            )
            .build();

        Session session = Session.create(params);
        CheckoutSession checkoutSession = new CheckoutSession();
        checkoutSession.setSessionId(session.getId());
        checkoutSession.setUser(user);
        checkoutSessionRepository.save(checkoutSession);
        return new CheckoutSessionResponse(session.getId());
    }

    @PostMapping("/customer-portal")
    @PreAuthorize("hasRole('ROLE_USER')")
    public PortalSessionResponse customerPortal(@CurrentUser UserPrincipal userPrincipal) throws Exception {
        Stripe.apiKey = appProperties.getStripe().getTestKey();

        User user = userRepository.findById(userPrincipal.getId()).orElseThrow();

        com.stripe.param.billingportal.SessionCreateParams params = new com.stripe.param.billingportal.SessionCreateParams.Builder()
            .setReturnUrl("https://coderintuition.com/settings/membership")
            .setCustomer(user.getStripeCustomerId())
            .build();

        com.stripe.model.billingportal.Session portalSession = com.stripe.model.billingportal.Session.create(params);
        return new PortalSessionResponse(portalSession.getUrl());
    }

    @PostMapping(value = "/webhook", consumes = "text/plain")
    public void webhook(@RequestHeader("Stripe-Signature") String sigHeader, String payload) throws SignatureVerificationException {
        Stripe.apiKey = appProperties.getStripe().getTestKey();

        logger.info(payload);
        logger.info(sigHeader);
        Event event = Webhook.constructEvent(payload, sigHeader, appProperties.getStripe().getWebhookSecret());

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                // Payment is successful and the subscription is created
                // You should provision the subscription
                Session session = (Session) stripeObject;
                CheckoutSession checkoutSession = checkoutSessionRepository.findBySessionId(session.getId()).orElseThrow();
                User user = checkoutSession.getUser();
                setUserAsPlus(user);
                break;
            case "invoice.paid":
                // Continue to provision the subscription as payments continue to be made.
                Invoice invoice = (Invoice) stripeObject;
                user = userRepository.findByStripeCustomerId(invoice.getCustomer()).orElseThrow();
                setUserAsPlus(user);
                break;
            case "invoice.payment_failed":
                // The payment failed or the customer does not have a valid payment method.
                // The subscription becomes past_due. Notify your customer and send them to the
                // customer portal to update their payment information.
                invoice = (Invoice) stripeObject;
                user = userRepository.findByStripeCustomerId(invoice.getCustomer()).orElseThrow();
                removeUserFromPlus(user);
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
                break;
        }
    }

    private void setUserAsPlus(User user) {
        Role plusRole = roleRepository.findByName(ERole.ROLE_PLUS).orElseThrow();
        Set<Role> newRoles = user.getRoles();
        newRoles.add(plusRole);
        user.setRoles(newRoles);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 31);
        user.setPlusExpirationDate(calendar.getTime());
        userRepository.save(user);
    }

    private void removeUserFromPlus(User user) {
        Role plusRole = roleRepository.findByName(ERole.ROLE_PLUS).orElseThrow();
        Set<Role> newRoles = user.getRoles();
        newRoles.remove(plusRole);
        user.setRoles(newRoles);
        userRepository.save(user);
    }
}
