ALTER TABLE ONLY public.replies ALTER COLUMN message TYPE text;

-- CREATE NEW TABLES

CREATE TABLE public.answer_details (
    id integer NOT NULL,
    answer_details_type character varying(32) DEFAULT 'multiple_choice'::character varying NOT NULL,
    question_answer_id integer NOT NULL,
    option_id integer
);

--
-- Name: answer_details_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.answer_details_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: answer_details_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.answer_details_id_seq OWNED BY public.answer_details.id;


--
-- Name: question_details; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.question_details (
    id integer NOT NULL,
    question_type character varying(32) DEFAULT 'multiple_choice'::character varying NOT NULL,
    question_id integer
);

--
-- Name: question_details_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.question_details_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: question_details_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.question_details_id_seq OWNED BY public.question_details.id;


--
-- Name: answer_details id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.answer_details ALTER COLUMN id SET DEFAULT nextval('public.answer_details_id_seq'::regclass);


--
-- Name: question_details id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_details ALTER COLUMN id SET DEFAULT nextval('public.question_details_id_seq'::regclass);

--
-- Name: answer_details_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.answer_details_id_seq', 1, false);


--
-- Name: question_details_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.question_details_id_seq', 1, false);


--
-- Name: answer_details answer_details_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.answer_details
    ADD CONSTRAINT answer_details_pkey PRIMARY KEY (id);


--
-- Name: question_details question_details_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_details
    ADD CONSTRAINT question_details_pkey PRIMARY KEY (id);


--
-- Name: answer_details uk_ngo3s0f06jd83whwfy2y9s1rl; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.answer_details
    ADD CONSTRAINT uk_ngo3s0f06jd83whwfy2y9s1rl UNIQUE (question_answer_id);


--
-- Name: answer_details fk58i179vrdh43q7q6hux2jgry2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.answer_details
    ADD CONSTRAINT fk58i179vrdh43q7q6hux2jgry2 FOREIGN KEY (option_id) REFERENCES public.options(id);


--
-- Name: answer_details fkjs7xvm2mm65ov3axubj7ipaee; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.answer_details
    ADD CONSTRAINT fkjs7xvm2mm65ov3axubj7ipaee FOREIGN KEY (question_answer_id) REFERENCES public.question_answers(id) ON DELETE CASCADE;


--
-- Name: question_details fkp450m4lxfc5bjdek0glon56j1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_details
    ADD CONSTRAINT fkp450m4lxfc5bjdek0glon56j1 FOREIGN KEY (question_id) REFERENCES public.questions(id) ON DELETE CASCADE;


--**********************************************
-- Moving data
--**********************************************

-- All questions should have a question_detail

INSERT INTO public.question_details(question_id)
SELECT id 
FROM public.questions;

-- All answers should have an answer_detail

INSERT INTO public.answer_details (question_answer_id, option_id)
SELECT id, option_id
FROM public.question_answers
WHERE option_id is not NULL;

-- OptionId should reference question details

ALTER TABLE ONLY public.options
	DROP CONSTRAINT fk5bmv46so2y5igt9o9n9w4fh6y;

ALTER TABLE ONLY public.options
    ADD question_details_id integer null;

UPDATE public.options o
SET question_details_id = qd.id
FROM public.question_details qd 
WHERE qd.question_id = o.question_id;

-- Remove unused

ALTER TABLE public.question_answers
	DROP COLUMN option_id;
