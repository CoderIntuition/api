package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.config.AppProperties;
import com.coderintuition.CoderIntuition.enums.ERole;
import com.coderintuition.CoderIntuition.models.CheckoutSession;
import com.coderintuition.CoderIntuition.models.Role;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.pojos.response.CheckoutSessionResponse;
import com.coderintuition.CoderIntuition.repositories.CheckoutSessionRepository;
import com.coderintuition.CoderIntuition.repositories.RoleRepository;
import com.coderintuition.CoderIntuition.repositories.UserRepository;
import com.coderintuition.CoderIntuition.security.CurrentUser;
import com.coderintuition.CoderIntuition.security.UserPrincipal;
import com.stripe.Stripe;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stripe")
public class StripeController {
    @Autowired
    AppProperties appProperties;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CheckoutSessionRepository checkoutSessionRepository;

    @PostMapping("/checkout-session")
    @PreAuthorize("hasRole('ROLE_USER')")
    public CheckoutSessionResponse createCheckoutSession(@CurrentUser UserPrincipal userPrincipal) throws Exception {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow();
        Role plusRole = roleRepository.findByName(ERole.ROLE_PLUS).orElseThrow();
        if (user.getRoles().contains(plusRole)) {
            throw new Exception("You are already on the Intuition+ plan");
        }

        Stripe.apiKey = appProperties.getStripe().getTestKey();
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl("https://coderintuition.com/checkout-success")
                        .setCancelUrl("https://coderintuition.com/plus")
                        .setCustomerEmail(user.getEmail())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("usd")
                                                        .setUnitAmount(1200L)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Intuition+")
                                                                        .build())
                                                        .build())
                                        .build())
                        .build();

        Session session = Session.create(params);
        CheckoutSession checkoutSession = new CheckoutSession();
        checkoutSession.setSessionId(session.getId());
        checkoutSession.setUser(user);
        checkoutSessionRepository.save(checkoutSession);

        return new CheckoutSessionResponse(session.getId());
    }

    @PostMapping("/hook")
    public void hook(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        }
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                System.out.println("Payment for " + paymentIntent.getAmount() + " succeeded.");
                // Then define and call a method to handle the successful payment intent.
                // handlePaymentIntentSucceeded(paymentIntent);
                break;
            case "payment_method.attached":
                PaymentMethod paymentMethod = (PaymentMethod) stripeObject;
                // Then define and call a method to handle the successful attachment of a PaymentMethod.
                // handlePaymentMethodAttached(paymentMethod);
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
                break;
        }
    }
}
