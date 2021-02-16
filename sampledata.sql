USE coderintuition;

INSERT INTO coderintuition.return_type(type, underlying_type, underlying_type_2, order_matters)
VALUES ('LIST', 'INTEGER', 'NONE', true);

SET @return_type_id = LAST_INSERT_ID();

INSERT INTO coderintuition.problem (return_type_id, name, url_name, plus_only, category, difficulty, description,
                                    python_code, java_code,
                                    javascript_code, created_at, updated_at, deleted)
VALUES (@return_type_id, 'Sort Array', 'sort-array', false, 'ARRAYS', 'BEGINNER',
        'Given an array of integers, sort the array in non-decreasing order.',
        'def sort_array(nums):\n\treturn nums',
        'class Solution {\n\tList<Integer> sortArray(List<Integer> nums) {\n\t\treturn nums;\n\t}\n}',
        'sortArray(nums) {\n\treturn nums;\n}', '2020-08-29 00:00:00', '2020-08-29 00:00:00', 0);

SET @problem1_id = LAST_INSERT_ID();

INSERT INTO coderintuition.reading (name, url_name, plus_only, is_quiz, content)
VALUES ('Test Reading', 'test-reading', false, false, 'I am a meenie test');

INSERT INTO coderintuition.problem_step (problem_id, step_num, name, type, content, time, created_at, updated_at)
VALUES (@problem1_id, 1, 'Understand the Problem', 'TEXT', 'Read the problem and understand it.', 5,
        '2020-08-29 00:00:00', '2020-08-29 00:00:00');

INSERT INTO coderintuition.problem_step (problem_id, step_num, name, type, content, time, created_at, updated_at)
VALUES (@problem1_id, 2, 'Problem Understanding Quiz', 'QUIZ', '
    [{
        "question": "How can you access the state of a component from inside of a member function?",
        "questionType": "text",
        "answerSelectionType": "single",
        "answers": [
            "this.getState()",
            "this.prototype.stateValue",
            "this.state",
            "this.values"
        ],
        "correctAnswer": "3",
        "messageForCorrectAnswer": "Correct answer. Good job.",
        "messageForIncorrectAnswer": "Incorrect answer. Please try again.",
        "explanation": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
        "point": "1"
    },
    {
        "question": "ReactJS is developed by _____?",
        "questionType": "text",
        "answerSelectionType": "single",
        "answers": [
            "Google Engineers",
            "Facebook Engineers"
        ],
        "correctAnswer": "2",
        "messageForCorrectAnswer": "Correct answer. Good job.",
        "messageForIncorrectAnswer": "Incorrect answer. Please try again.",
        "explanation": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
        "point": "1"
    }]
', 5, '2020-08-29 00:00:00', '2020-08-29 00:00:00');

INSERT INTO coderintuition.test_case(problem_id, test_case_num, name, is_default, input, time_limit,
                                     memory_limit, stack_limit, created_at, updated_at)
VALUES (@problem1_id, 1, 'Test 1', true, '[1, 2, 3, 4]', 10, 10, 10, '2020-08-29 00:00:00',
        '2020-08-29 00:00:00');

INSERT INTO coderintuition.test_case(problem_id, test_case_num, name, is_default, input, time_limit,
                                     memory_limit, stack_limit, created_at, updated_at)
VALUES (@problem1_id, 2, 'Test 1', false, '[3, 1, 4, 2]', 10, 10, 10, '2020-08-29 00:00:00',
        '2020-08-29 00:00:00');

INSERT INTO coderintuition.solution(problem_id, solution_num, name, is_primary, python_code, java_code,
                                    javascript_code, description, created_at, updated_at)
VALUES (@problem1_id, 1, 'Manually sort the array', false, 'def sort_array(nums):\n    return []',
        '', '', 'Go through each element and compare', '2020-08-29 00:00:00', '2020-08-29 00:00:00');

INSERT INTO coderintuition.solution(problem_id, solution_num, name, is_primary, python_code, java_code,
                                    javascript_code, description, created_at, updated_at)
VALUES (@problem1_id, 2, 'Use built-in sorted', true,
        'def sort_array(nums):\n    return sorted(nums)',
        'class ActualSolution {\n\tList<Integer> sortArray(List<Integer> nums) {\n\t\tCollections.sort(nums);\n\t\treturn nums\n\t}\n}',
        'function sortArray(nums) {\n\tnums.sort();\n\treturn nums;\n}',
        'Use the built-in function', '2020-08-29 00:00:00', '2020-08-29 00:00:00');

INSERT INTO coderintuition.argument(problem_id, argument_num, type, underlying_type, underlying_type_2)
VALUES (@problem1_id, 1, 'LIST', 'INTEGER', 'NONE');

INSERT INTO role(name)
VALUES ('ROLE_USER');
SET @user_id = LAST_INSERT_ID();
INSERT INTO role(name)
VALUES ('ROLE_PLUS');
INSERT INTO role(name)
VALUES ('ROLE_MODERATOR');
SET @moderator_id = LAST_INSERT_ID();
INSERT INTO role(name)
VALUES ('ROLE_ADMIN');
SET @admin_id = LAST_INSERT_ID();

INSERT INTO user(name, email, password, language, auth_provider, verified, username)
VALUES ('David Zhang', 'davidhqr@gmail.com', '$2a$10$ygD6H1LrE7erEV8FrLrQfeVnNYCDtQSgi8GYDhV3lkBKgIw98VHMm', 'PYTHON',
        'LOCAL', false, 'davidhqr');

SET @user1_id = LAST_INSERT_ID();

INSERT INTO user_role(user_id, role_id)
VALUES (@user1_id, @user_id);
INSERT INTO user_role(user_id, role_id)
VALUES (@user1_id, @moderator_id);
INSERT INTO user_role(user_id, role_id)
VALUES (@user1_id, @admin_id);
