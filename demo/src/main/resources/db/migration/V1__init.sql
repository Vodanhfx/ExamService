CREATE TABLE exam (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE question (
    id BIGSERIAL PRIMARY KEY,
    exam_id BIGINT NOT NULL REFERENCES exam(id) ON DELETE CASCADE,
    text VARCHAR(1000) NOT NULL,
    correct_answer_index INTEGER NOT NULL
);

CREATE TABLE question_choices (
    question_id BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
    choice TEXT NOT NULL
);

CREATE TABLE exam_attempt (
    id BIGSERIAL PRIMARY KEY,
    exam_id BIGINT NOT NULL REFERENCES exam(id) ON DELETE CASCADE,
    user_id VARCHAR(255) NOT NULL,
    tries INTEGER NOT NULL DEFAULT 1,
    score INTEGER NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE attempt_answers (
    attempt_id BIGINT NOT NULL REFERENCES exam_attempt(id) ON DELETE CASCADE,
    answer INTEGER NOT NULL
);