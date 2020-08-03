--
-- PostgreSQL database dump
--

-- Dumped from database version 10.12 (Ubuntu 10.12-0ubuntu0.18.04.1)
-- Dumped by pg_dump version 10.12 (Ubuntu 10.12-0ubuntu0.18.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: assessments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.assessments (
    id integer NOT NULL,
    number integer,
    status character varying(255),
    title character varying(255),
    course_execution_id integer
);


--
-- Name: assessments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.assessments_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: assessments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.assessments_id_seq OWNED BY public.assessments.id;


--
-- Name: course_executions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.course_executions (
    id integer NOT NULL,
    academic_term character varying(255),
    acronym character varying(255),
    status character varying(255),
    course_id integer,
    type character varying(255)
);


--
-- Name: course_executions_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.course_executions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: course_executions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.course_executions_id_seq OWNED BY public.course_executions.id;


--
-- Name: courses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.courses (
    id integer NOT NULL,
    name character varying(255),
    type character varying(255)
);


--
-- Name: courses_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.courses_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: courses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.courses_id_seq OWNED BY public.courses.id;


--
-- Name: images; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.images (
    id integer NOT NULL,
    url character varying(255),
    width integer,
    question_id integer
);


--
-- Name: images_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.images_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: images_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.images_id_seq OWNED BY public.images.id;


--
-- Name: options; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.options (
    id integer NOT NULL,
    content text,
    correct boolean DEFAULT false,
    question_id integer,
    sequence integer
);


--
-- Name: options_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.options_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: options_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.options_id_seq OWNED BY public.options.id;


--
-- Name: question_answers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.question_answers (
    id integer NOT NULL,
    time_taken integer,
    option_id integer,
    quiz_answer_id integer,
    quiz_question_id integer,
    sequence integer
);


--
-- Name: question_answers_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.question_answers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: question_answers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.question_answers_id_seq OWNED BY public.question_answers.id;


--
-- Name: questions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.questions (
    id integer NOT NULL,
    content text,
    number_of_answers integer DEFAULT 0,
    number_of_correct integer DEFAULT 0,
    title character varying(255),
    status character varying(255),
    creation_date timestamp without time zone,
    course_id integer,
    key integer
);


--
-- Name: questions_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.questions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: questions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.questions_id_seq OWNED BY public.questions.id;


--
-- Name: quiz_answers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.quiz_answers (
    id integer NOT NULL,
    answer_date timestamp without time zone,
    completed boolean NOT NULL,
    quiz_id integer,
    user_id integer,
    used_in_statistics boolean
);


--
-- Name: quiz_answers_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.quiz_answers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: quiz_answers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.quiz_answers_id_seq OWNED BY public.quiz_answers.id;


--
-- Name: quiz_questions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.quiz_questions (
    id integer NOT NULL,
    sequence integer,
    question_id integer,
    quiz_id integer
);


--
-- Name: quiz_questions_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.quiz_questions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: quiz_questions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.quiz_questions_id_seq OWNED BY public.quiz_questions.id;


--
-- Name: quizzes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.quizzes (
    id integer NOT NULL,
    available_date timestamp without time zone,
    conclusion_date timestamp without time zone,
    creation_date timestamp without time zone,
    scramble boolean DEFAULT false,
    series integer,
    title character varying(255) NOT NULL,
    type character varying(255),
    version character varying(255),
    course_execution_id integer,
    key integer
);


--
-- Name: quizzes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.quizzes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: quizzes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.quizzes_id_seq OWNED BY public.quizzes.id;


--
-- Name: topic_conjunctions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.topic_conjunctions (
    id integer NOT NULL,
    assessment_id integer
);


--
-- Name: topic_conjunctions_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.topic_conjunctions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: topic_conjunctions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.topic_conjunctions_id_seq OWNED BY public.topic_conjunctions.id;


--
-- Name: topics; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.topics (
    id integer NOT NULL,
    name character varying(255),
    parent_topic_id integer,
    course_id integer
);


--
-- Name: topics_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.topics_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: topics_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.topics_id_seq OWNED BY public.topics.id;


--
-- Name: topics_questions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.topics_questions (
    topics_id integer NOT NULL,
    questions_id integer NOT NULL
);


--
-- Name: topics_topic_conjunctions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.topics_topic_conjunctions (
    topics_id integer NOT NULL,
    topic_conjunctions_id integer NOT NULL
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id integer NOT NULL,
    creation_date timestamp without time zone,
    name character varying(255),
    role character varying(255),
    username character varying(255),
    number_of_correct_teacher_answers integer,
    number_of_student_quizzes integer,
    number_of_teacher_answers integer,
    number_of_teacher_quizzes integer,
    enrolled_courses_acronyms character varying(255),
    last_access timestamp without time zone,
    key integer
);


--
-- Name: users_course_executions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users_course_executions (
    users_id integer NOT NULL,
    course_executions_id integer NOT NULL
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: assessments id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.assessments ALTER COLUMN id SET DEFAULT nextval('public.assessments_id_seq'::regclass);


--
-- Name: course_executions id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course_executions ALTER COLUMN id SET DEFAULT nextval('public.course_executions_id_seq'::regclass);


--
-- Name: courses id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.courses ALTER COLUMN id SET DEFAULT nextval('public.courses_id_seq'::regclass);


--
-- Name: images id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.images ALTER COLUMN id SET DEFAULT nextval('public.images_id_seq'::regclass);


--
-- Name: options id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.options ALTER COLUMN id SET DEFAULT nextval('public.options_id_seq'::regclass);


--
-- Name: question_answers id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_answers ALTER COLUMN id SET DEFAULT nextval('public.question_answers_id_seq'::regclass);


--
-- Name: questions id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.questions ALTER COLUMN id SET DEFAULT nextval('public.questions_id_seq'::regclass);


--
-- Name: quiz_answers id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quiz_answers ALTER COLUMN id SET DEFAULT nextval('public.quiz_answers_id_seq'::regclass);


--
-- Name: quiz_questions id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quiz_questions ALTER COLUMN id SET DEFAULT nextval('public.quiz_questions_id_seq'::regclass);


--
-- Name: quizzes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quizzes ALTER COLUMN id SET DEFAULT nextval('public.quizzes_id_seq'::regclass);


--
-- Name: topic_conjunctions id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topic_conjunctions ALTER COLUMN id SET DEFAULT nextval('public.topic_conjunctions_id_seq'::regclass);


--
-- Name: topics id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topics ALTER COLUMN id SET DEFAULT nextval('public.topics_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: assessments; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.assessments (id, number, status, title, course_execution_id) FROM stdin;
6	6	AVAILABLE	Second mini-test	11
7	7	AVAILABLE	Third mini-test	11
8	8	AVAILABLE	Fourth mini-test	11
9	9	AVAILABLE	Fifth mini-test	11
10	10	AVAILABLE	First mini-test	11
\.


--
-- Data for Name: course_executions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.course_executions (id, academic_term, acronym, status, course_id, type) FROM stdin;
11	1st Semester	DemoCourse	ACTIVE	2	TECNICO
12	2º Semestre 2019/2020	ESof96451113264	ACTIVE	3	TECNICO
\.


--
-- Data for Name: courses; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.courses (id, name, type) FROM stdin;
2	Demo Course	TECNICO
3	Engenharia de Software	TECNICO
\.


--
-- Data for Name: images; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.images (id, url, width, question_id) FROM stdin;
142	demo-454.png	100	1318
143	demo-800.png	130	1323
144	demo-395.png	100	1333
145	demo-1046.png	120	1335
146	demo-537.png	100	1338
147	demo-949.png	100	1350
148	demo-1059.png	120	1367
149	demo-712.png	140	1371
150	demo-884.png	120	1383
151	demo-1283.png	\N	1398
152	demo-558.png	100	1401
153	demo-959.png	100	1407
154	demo-950.png	100	1409
155	demo-713.png	70	1410
156	demo-966.png	100	1439
157	demo-1058.png	60	1449
158	demo-550.png	100	1478
159	demo-470.png	100	1482
160	demo-705.png	120	1531
161	demo-1010.png	95	1505
162	demo-856.png	140	1507
163	demo-1006.png	95	1508
164	demo-1026.png	95	1540
165	demo-650.png	80	1542
166	demo-1273.png	\N	1550
167	demo-409.png	80	1565
168	demo-402.png	80	1567
169	demo-404.png	80	1574
170	demo-407.png	100	1589
171	demo-412.png	80	1590
172	demo-934.png	120	1593
173	demo-476.png	80	1594
174	demo-1028.png	95	1598
175	demo-846.png	120	1600
176	demo-650.png	80	1605
177	demo-480.png	80	1608
178	demo-1274.png	\N	1611
179	demo-525.png	120	1613
180	demo-419.png	80	1639
181	demo-636.png	100	1617
182	demo-1251.png	\N	1620
183	demo-383.png	100	1632
184	demo-414.png	80	1636
185	demo-417.png	100	1637
186	demo-639.png	80	1661
187	demo-462.png	150	1668
188	demo-967.png	100	1672
189	demo-566.png	100	1685
190	demo-388.png	120	1729
191	demo-398.png	100	1735
192	demo-790.png	120	1893
193	demo-626.png	100	1785
194	demo-657.png	80	1787
195	demo-214.png	100	1829
196	demo-929.png	120	1849
197	demo-293.png	120	1853
198	demo-299.png	70	1855
199	demo-931.png	60	1867
200	demo-940.png	120	1872
201	demo-779.png	80	1873
202	demo-936.png	120	1878
203	demo-778.png	140	1881
204	demo-921.png	120	1887
205	demo-894.png	100	1889
206	demo-895.png	120	1891
207	demo-956.png	100	1901
208	demo-1039.png	100	1902
209	demo-1016.png	95	1904
210	demo-1036.png	120	1906
211	demo-1050.png	120	1909
212	demo-1091.png	\N	1928
213	demo-1136.png	\N	1938
214	demo-517.png	140	1971
215	demo-786.png	100	1973
\.


--
-- Data for Name: options; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.options (id, content, correct, question_id, sequence) FROM stdin;
5278	Given that the change is on execution aspects, the change manifests itself only through the modification of components and connectors on the system	f	1408	0
5279	This change in the way of how web applications run does not correspond to any change in its architecture, because at the architectural level we still have the same components	f	1408	1
5280	The only architectural change is on the Deployment view, because the components and connectors remain the same, but execute in different places	f	1408	2
5283	The *Peer-to-Peer* style	f	1501	3
5284	*Decomposition* and *Layers* views	f	1362	0
5285	*Decomposition* and *Work assignment* views	f	1362	1
5286	*Decomposition* and *Generalization* views	f	1362	3
5287	*Decomposition* and *Implementation* views	f	1490	0
5288	*Component-and-connector* views	f	1490	2
5289	 None, given that to perform black-box testing you do not need to know the code or the internal structure of the application to be tested	f	1490	3
5290	The *Shared data* style	f	1547	0
5291	The *Repository* style	f	1547	1
5294	The *Deployment* and *Uses* styles	f	1354	1
5295	The *Client-Server* and *Generalization* styles	f	1354	2
5296	Views of the Component-and-Connector viewtype	f	1400	0
5297	Views of the Allocation viewtype	f	1400	2
5298	All of the above	f	1400	3
5299	To allow the existence of more than one layer of presentation logic for the same application (to provide for example, an interface to web services)	f	1646	1
5300	To expose different interfaces of the domain logic layer so that it allows the implementation of different layers for the presentation logic	f	1646	2
5301	To facilitate the use of the data access layer by the presentation logic layer	f	1646	3
5302	To implement each of the services that are executed whenever the client makes a request to the server	f	1647	0
5303	To improve the server performance by maintaining a cache of the objects most accessed during the processing of a client request	f	1647	1
5304	To split the computation required to process each request made by the client in smaller units of work that are parallelizable	f	1647	2
5305	To improve the performance of the application server because it maintains a cache of entities that reduces the number of operations made on the database	f	1484	0
5306	To avoid loading a lot of data from the database when an entity that has many relationships with other entities is loaded	f	1484	1
5307	To map each entity loaded by the server to the identity of that entity in the database, so that the server is able to update the database later, if needed	f	1484	3
5308	Stakeholders requirements do not emphasize performance as the most important issue	f	1814	0
5309	The Hadoop small development team is highly competent and skilled	f	1814	1
5310	The Hadoop system implementation uses complex distributed algorithms for scalability	f	1814	3
5311	There is a conflict between availability and performance qualities	f	1448	0
5312	There is no conflict between availability and performance qualities	f	1448	2
5313	Availability and performance qualities are ensured at deployment time only	f	1448	3
5314	Active replication and passive replication	f	1899	0
5315	Active replication, passive replication, and spare	f	1899	1
5316	Quorum, active replication, and passive replication	f	1899	3
5317	Shadow operation	f	1772	0
5318	Checkpoint/Rollback	f	1772	2
5319	All of the above	f	1772	3
5320	Authenticate users, authorize users, and limit exposure	f	1964	1
5321	Usability scenario.	f	1730	3
5322	Authenticate users, authorize users, and limit access	f	1964	2
5323	Authenticate users, authorize users, limit access, and maintain integrity	f	1964	3
5324	Because it has a well-defined interface	f	1752	0
5325	That aggregates modules according to the uses relationship	f	1752	1
5326	Because it has a well-defined interface and hides the internal behaviour	f	1752	2
5327	Because it is a natural extension of the use cases concept	f	1650	0
5328	But it requires additional information on the modules internal structure	f	1650	1
5329	And contains all the information required to assess effectively the impact	f	1650	3
5330	The call's results may not have impact on the correct execution of the callee module	f	1826	1
5331	The call may not transfer data between the modules	f	1826	2
5332	The uses relationship requires calls to return control to the caller module	f	1826	3
5333	We should always satisfy in the first place the requirements of more important stakeholders (such as the client)	f	1737	0
5334	If no order was established among them, we would not know from where should we start the design process	f	1737	1
5335	If one of the stakeholders complains that his requirement is not satisfied, we may explain to him that there were other more important requirements first	f	1737	2
5336	The Shared data style	f	1745	0
5337	The Pipes-and-filters style	f	1745	1
5338	The Client-Server style	f	1745	3
5339	A Client-Server architecture, where the NameNode is the Client and the DataNode is the Server	f	1760	1
5340	A Peer-to-Peer architecture	f	1760	2
5341	A Communicating Processes architecture	f	1760	3
5342	That view will always be incomplete without the NameNode, because the HDFS Client needs to interact with it	f	1739	0
5343	That view will always be incomplete without the NameNode, because the DataNode needs to interact with it	f	1739	1
5344	The view does not need to include the NameNode, but in that case it will not be possible to reason about the availability of the system	f	1739	2
5345	The Shared Data style	f	1727	0
5346	The Deployment style	f	1727	2
5347	The Peer-to-Peer style	f	1727	3
5348	To allow the existence of more than one interface to the domain logic layer (to provide, for example, an interface to web services)	f	1736	1
7842	Omission.	t	1331	0
5349	To allow the existence of different presentation logic layers	f	1736	2
5350	To facilitate the use of the data access layer by the presentation logic layer	f	1736	3
5680	Decomposition	f	1334	0
5351	That should not be allowed because all interactions among components must be made through the Repository	f	1481	0
5352	That is an acceptable solution if we want to reduce the dependencies among the various components of the system	f	1481	1
5353	That interaction cannot be represented in this view, but it may in another view of the system's architecture	f	1481	3
5354	Essential to ensure the system scalability	f	1645	0
5355	Essential to ensure the system portability	f	1645	2
5356	Essential to facilitate the integration with legacy systems	f	1645	3
5357	Allows the creation of checkpoints but it is necessary to request all the information from the *NameNode* whenever a new checkpoint creation begins	f	1463	0
5358	Does not allow the creation of checkpoints	f	1463	1
5359	Allows the creation of checkpoints without requiring any kind of information from the *NameNode*	f	1463	3
5360	Performance qualities only	f	1453	1
5361	Availability qualities only	f	1453	2
5362	Performance and security qualities	f	1453	3
5363	Allows *DataNodes* to decide which replicas they have	f	1796	1
6474	Limit event response	f	1534	1
5364	Increases the system modifiability whenever it is necessary to change the the deployment structure	f	1796	2
5365	Allow several replicas to be located in different *DataNodes*	f	1796	3
5366	Because this tactic does not overload the *NameNode*	f	1959	0
5367	But an exceptions tactic could be used as well	f	1959	2
5368	To notify other *DataNodes* that they are available	f	1959	3
5369	This script is part of the system deployment module	f	1643	0
5370	This script is a module that implements a security tactic	f	1643	1
5371	This script cannot be considered a module because it is only a script	f	1643	3
5372	Availability, security, and performance	f	1972	0
5373	Availability only	f	1972	1
5374	Availability and performance	f	1972	2
5375	Means that it is possible to implement the system according to an incremental development process	f	1602	1
5376	Means that the modules that are part of the loop should be implemented first	f	1602	2
5377	Gives a hint to replace the uses relations by is-a relations	f	1602	3
5378	Is that the *Uses* relation can happen only among modules belonging to the same layer	f	1958	1
5379	Is that the *Allowed to Use* relation does not imply that the correctness of the upper layer depends on the correct implementation of its nearest lower layer	f	1958	2
5380	Is that the *Allowed to Use* relation is a *Uses* relation between layers	f	1958	3
5381	It does not make sense to use an architectural view for this, because this is an implementation detail that does affect the system's qualities	f	1352	0
5382	Using a view of the module viewtype that shows the interfaces available for the client to do the write	f	1352	1
5383	Using a view of the architectural style Deployment	f	1352	3
5384	The component-and-connector view must, necessarily, be changed to include the components *NameNode* and *DataNode*, with which the web application has to interact to access its data	f	1651	0
5385	The layered view of the web application will have to include a new layer corresponding to the Hadoop MapReduce framework	f	1651	1
5386	The Deployment view must be changed to include the racks needed to run the HDFS system	f	1651	3
5387	The Pipes-and-filters style	f	1648	0
5388	The Publish-Subscribe style	f	1648	1
5389	The Peer-to-Peer style	f	1648	2
5390	Affects only the domain logic layer	f	1502	0
5391	Does not affect the presentation logic layer because it cannot use it	f	1502	2
5392	Active Redundancy and Increase Resources Efficiency	f	1749	1
5393	Does not affect the data access layer because the data access layer does not use the domain logic layer	f	1502	3
5394	A machine may execute only one component, but a component may execute in more than one machine	f	1580	0
5395	A component may execute in only one machine, but a machine may execute more than one component	f	1580	1
5396	Each component executes in only one machine and each machine executes only one component	f	1580	2
5397	Only views of the component-and-connector viewtype are needed	f	1597	0
5398	Only views of the Deployment style are needed	f	1597	1
5399	We always need views of the component-and-connector viewtype and of the Deployment style	f	1597	3
5400	The UK government, because it funded the project	f	1624	0
5401	The researchers, because they invented the Haskell programming language	f	1624	1
5402	The UK government, because it intended that the system could be used to develop the British software industry	f	1624	2
5403	As components of the system	f	1644	1
5404	The compiler and the RTS as components and the boot libraries as a module	f	1644	2
5405	The compiler as a component and the other two as modules	f	1644	3
5406	Made the type-checking much simpler	f	1533	0
5407	Satisfied performance requirements of the system	f	1533	1
5408	Made the desugaring simpler	f	1533	2
5409	The performance of the compiler, because the RTS is written as very efficient C code	f	1653	0
5410	The performance of the compiled programs, because the RTS is written as very efficient C code	f	1653	1
5411	The modifiability of the compiled programs, because we may change their behavior by changing only the RTS	f	1653	3
5412	To allow the parallel execution of the several compilation phases, thereby improving the compiler performance	f	1654	0
5413	To allow the compilation of very large programs, because wach phase may execute incrementally without loading the entire program into memory at once	f	1654	1
5414	All other options	f	1654	3
5415	It is a system with a Peer-to-Peer architecture	f	1655	0
5416	It is a system with a Client-Server architecture	f	1655	1
5417	It allows the development of systems with a Peer-to-Peer architecture	f	1655	2
5418	The Tiers style	f	1656	0
5419	The Communicating Processes style	f	1656	1
5420	The Work Assignment style	f	1656	3
5421	Increases the portability of the system for other operating systems	f	1657	0
5422	Makes the system more scalable	f	1657	2
5423	Facilitates the reuse of the messaging patterns	f	1657	3
5424	To reduce the latency of sending a message when the system is overloaded	f	1658	0
5425	To reduce the amount of memory needed to send a large number of messages	f	1658	2
5426	To reduce the CPU usage when the system has just a few messages to send	f	1658	3
5427	The Pipe-and-Filter style	f	1659	0
5428	The Shared data style	f	1659	1
5429	The Client-Server style	f	1659	3
5430	To have better throughput than Apache	f	1329	0
5431	To have a lower latency in the processing of a request than Apache	f	1329	1
5432	To be more modifiable than Apache	f	1329	2
5433	The code is easier to develop because it is not concurrent	f	1667	0
5434	Event-driven programs are easier to change	f	1667	2
5435	In an event-driven system each component may function independently of the others	f	1667	3
5436	To make the system more modifiable	f	1670	0
5437	To make the system more portable to different operating systems	f	1670	1
5438	To make the system more fault tolerant	f	1670	3
5439	The Shared-data style	f	1671	0
5440	The Peer-to-Peer style	f	1671	2
5441	The Client-Server style	f	1671	3
5442	Increases the availability	f	1664	0
5443	Increases the capacity	f	1664	1
5444	Increases the capacity but decreases the availability	f	1664	2
5445	Only in the Deployment view, because only the number of machines storing images was changed	f	1548	0
5446	In the Uses view, because each *Image Write/Retrieval Service* is going to use a different *Image File Storage*	f	1548	1
5447	In the Decomposition view, because we need more modules to represent the split of images by different elements of the architecture	f	1548	3
5448	Increasing the performance of the *Upload* operation	f	1678	0
5449	Increasing the performance of the *Retrieval* operation	f	1678	1
5450	Increasing the scalability of the system	f	1678	2
5451	We now have four layers, where each layer is executed in the corresponding tier, as before	f	1406	0
5452	Each one of the two middle tiers executes the previously existing three layers, and no change is needed on the layers view	f	1406	1
5453	There is no relation between the tiers and the layers, so the layers architecture is not changed	f	1406	2
5454	The domain logic layer was implemented with the Domain Model pattern	f	1324	0
5455	The domain logic layer was implemented with the Service Layer pattern	f	1324	2
5456	The domain logic layer was implemented with a rich domain model, on top of which there was a thin service layer	f	1324	3
5457	The Decomposition style	f	1578	0
5458	The Generalization style	f	1578	1
5459	The SOA style	f	1578	3
5460	The Communicating Processes style	f	1348	0
5461	The Client-Server style	f	1348	1
5462	Any style of the component-and-connector viewtype	f	1348	2
5463	In the Decomposition view	f	1625	1
5464	In a view of the component-and-connector type	f	1625	2
5465	In the Aspects view	f	1625	3
5466	To create an abstraction layer between the architecture of the system and its functionalities, so that the architecture may be changed later without affecting the functionalities	f	1679	0
5467	To create an artifact that may be used to explain the system's software architecture to the various stakeholders	f	1679	1
5468	To facilitate the work assignment to the members of the development team that will implement the system's functionalities	f	1679	3
5469	A component	f	1500	1
5470	Does not change the existing modules of the system, because they are determined by the system's Decomposition, which is not changed	f	1581	0
5471	Adds restrictions to the dependency relationships that exist between modules and that are represented using other styles, as with the layers style	f	1581	1
5472	Introduces only a new type of relation among the existing modules of the system, which resulted from other styles of the module viewtype	f	1581	3
5473	To process all of the requests to send messages with a single thread, to free the remaining cores for the user threads	f	1680	0
5474	To launch a worker thread for each user thread to guarantee that each user thread may send messages independently of what the others are doing	f	1680	1
5475	To launch a worker thread to process the sending of a new message, to guarantee maximal parallelism in message sending	f	1680	3
5476	Only module views	f	1681	0
5477	Only component-and-connector views	f	1681	1
5478	Only allocation views	f	1681	2
5479	It is less robust, because a fault in the *broker* causes a failure in the system	f	1682	0
5480	It is less modifiable, because all components depend on the *broker*	f	1682	1
5481	It is more expensive, because it forces the existence of additional hardware to execute the *broker*	f	1682	3
5482	Views of the Layers style	f	1690	1
5483	Views of the Decomposition style	f	1690	2
5484	Views of the Uses style	f	1690	3
5485	Modifiability	f	1691	1
5486	Performance	f	1691	2
5487	Security	f	1691	3
5488	The Publish-Subscribe style	f	1438	0
5489	The Client-Server style	f	1438	1
5490	The Peer-to-Peer style	f	1438	2
5491	The Layers style	f	1693	0
5492	The Uses style	f	1693	1
5493	The Communicating Processes style	f	1693	3
5494	A new *worker* is created whenever a new connection is established with the server, and that *worker* processes all of the requests for that connection, being destroyed at the end of the connection	f	1553	0
5495	There is a *pool* of *workers* that are reused between connections, but each *worker* processes only requests of a connection at a time	f	1553	1
5496	Each *worker* processes requests that it obtains from a *pool of requests* that is shared among all workers	f	1553	3
5497	By executing in parallel each of the phases of the pipeline corresponding to the processing of a request	f	1554	1
5498	By executing in parallel the processing of the various requests	f	1554	2
5499	By processing completely each request before moving to the next one, in a sequential process	f	1554	3
5500	Have a throughput higher than Apache	f	1440	0
5501	Be able to process each request faster than Apache	f	1440	1
5502	Be able to launch more simultaneous threads than Apache	f	1440	2
5503	The *Decomposition* style	f	1330	0
5504	The *Client-Server* style	f	1330	1
5505	The *Communicating Processes* style	f	1330	3
5506	To increase the performance of RTS	f	1441	0
5507	To allow changing the GC algorithm without affecting the rest of the system	f	1441	1
5508	To increase the performance of the programs compiled by the GHC	f	1441	2
5509	It does not manifest, as it corresponds only to an extension to the Haskell language that must be processed by the compiler	f	1694	0
5510	In a view of the Generalization style that includes a module defining an abstract interface that all *rewrite rules* must implement and of which all modules with the *rewrite rules* are specializations	f	1694	2
6083	Contain the business logic.	f	1622	2
5511	In a view of the Pipes-and-Filters style, corresponding to the process of compiling an Haskell program, to which is added a new filter whenever a new *rewrite rule* is defined	f	1694	3
5512	A diagram of the component-and-connector viewtype	f	1695	0
5513	A diagram of the deployment style	f	1695	2
5514	A diagram of the implementation style	f	1695	3
5515	A diagram of the module viewtype, showing a decomposition of the compiler in the various modules that are responsible by each of the compilation process steps	f	1696	1
5516	A diagram of the module viewtype, showing which modules use other modules	f	1696	2
5517	A layered diagram, where there is a layer responsible for the code generation	f	1696	3
5518	They are both modules	f	1711	0
5519	The *Request Node* is a component and the *Cache* is a module	f	1711	2
5520	The *Request Node* is a module and the *Cache* is a component	f	1711	3
5521	Increasing performance and availability	f	1697	0
5522	Increasing availability and decreasing performance	f	1697	1
5523	The *Layers* style	t	1330	2
5524	To make the RTS more portable to other operating systems	t	1441	3
5525	In the existence of a compiler component that is responsible for interpreting and applying the rewrite rules during the compilation of a program	t	1694	1
5526	A diagram of the module viewtype	t	1695	1
5527	A diagram of the component-and-connector viewtype, showing the data flow between the various compiler components	t	1696	0
5528	They are both components	t	1711	1
5529	Increasing performance and decreasing availability	t	1697	2
5530	Increasing scalability and availability	f	1697	3
5531	The Shared-Data style	f	1710	0
5532	The Client-Server style	f	1710	1
5533	The Communicating Processes style	f	1710	3
5534	The availability of the system's data decreases	f	1698	1
5535	The availability of the system's services decreases	f	1698	2
5536	The system is not affected in any way	f	1698	3
5537	To start using the Transaction Script pattern in the domain logic layer	f	1447	0
5538	To start using the Service Layer pattern in a new layer	f	1447	1
5539	To eliminate the service layer	f	1447	3
5540	Views of the Module viewtype	f	1443	0
5541	Views of the Component-and-Connector viewtype	f	1443	1
5542	Views of the Allocation viewtype	f	1443	2
5543	The Peer-to-Peer style	f	1566	0
5544	The Shared-Data style	f	1566	2
5545	The Publish-subscribe style	f	1566	3
5546	By using HTTPS in the communication between the browser and the web server	f	1573	0
5547	By using robust authentication mechanisms to identify the users of the system with confidence	f	1573	1
5548	By encrypting the information in the database with a password that is known only by the web server	f	1573	2
5549	The connector used to represent the interaction between the browser and the web server changed	f	1569	1
5550	The browser is now a component of a different type	f	1569	2
5551	That evolution did not have any consequences on the software architecture of a web application	f	1569	3
5552	A component	f	1386	0
5553	A module	f	1386	2
5554	A layer	f	1386	3
5555	Maintain Multiple Copies of Data	f	1356	1
5556	Passive Redundancy	f	1356	2
5557	Active Redundancy	f	1356	3
5558	Performance, availability, and testability	f	1733	1
5559	Reliability, performance, and usability	f	1733	2
5560	Performance and usability	f	1733	3
5561	Repository e Service Oriented Architecture	f	1461	1
5562	Client-Server, Repository, Communicating-Processes e Service Oriented Architecture	f	1461	2
5563	Client-Server, Repository e Communicating-Processes	f	1461	3
5564	By using a Retry tactic	f	1462	1
5565	Storing the information in the client using cookies	f	1462	2
5566	By using a Transactions tactic	f	1462	3
5567	May stop accepting reads	f	1464	0
5568	May stop accepting reads and writes	f	1464	2
5569	May need to add more servers to the cluster	f	1464	3
5570	Increase Resources	f	1740	1
5571	Prioritize Events	f	1740	2
5572	Maintain Multiple Copies of Data	f	1740	3
5573	Data Encryption	f	1403	0
5574	Intrusion Detection	f	1403	1
5575	Authorize Actors	f	1403	3
5576	Maintain Multiple Copies of Computation	f	1346	1
5577	Limit Exposition	f	1346	2
5578	Active Redundancy	f	1346	3
5579	Publication-Subscription	f	1577	0
5580	Pipes-and-Filters	f	1577	1
5581	Client-server	f	1577	3
5582	It can take advantage of concurrency	f	1776	0
5583	The *browser* needs to make more requests to the server	f	1776	1
5584	It uses machine learning techniques	f	1776	2
5681	Uses	f	1334	2
5585	It is always the same for all instances of Chrome	f	1956	0
5586	It is defined compile-time of Chrome code	f	1956	1
5587	It is defined during the initialization of each instance of Chrome	f	1956	2
5588	Maintain Task Model tactic	f	1365	1
5589	Maintain System Model tactic	f	1365	2
5590	Support User Initiative tactics	f	1365	3
5591	It is necessary to decompress the complete file, even though if only a small part of the information is needed	f	1741	0
5592	Pickle algorithm is not efficient	f	1741	1
5593	It is a Python specific solution	f	1741	2
5594	Increase the modifiability quality, because external applications stopped using the administrative functionalities	f	1732	1
5595	Increase the interoperability quality, because external applications can read and send messages to GNU Mailman	f	1732	2
5596	None of the previous options	f	1732	3
5597	A sequence of bytes to allow independence between filters	f	1742	0
5598	An object tree to allow the simultaneous execution of several filters	f	1742	1
6230	A Component-and-Connector view	f	1479	1
5599	A sequence of bytes to allow that the order of filters execution is not relevant	f	1742	2
5600	Effectively guarantees the FIFO delivery of messages and the queue runners do not need to synchronize	f	1743	0
5601	Guarantees the FIFO delivery of messages but the queue runners need to synchronize	f	1743	1
5602	Guarantees the FIFO delivery of messages because in each *queue* only are stored messages which arrived with a difference of more than one minute	f	1743	3
5604	Peer-to-Peer	f	1465	1
5605	Tiers	f	1465	3
5606	Each message does not need to be accessed concurrently by several processes	f	1712	0
5607	Pickle can efficiently write and read messages	f	1712	1
5608	Each message is stored as a file in a directory	f	1712	2
5609	Allocate modules to the file system	f	1606	0
5610	Are applied to completely distinct sets of files	f	1606	2
5611	Are applied to the same set of files	f	1606	3
5612	Interoperability e Performance	f	1726	0
5613	Performance and Availability	f	1726	1
5614	Easy Development and Performance	f	1726	2
5615	Passive Redundancy	f	1435	1
5616	Active Redundancy	f	1435	2
5617	Passive Redundancy and Active Redundancy	f	1435	3
5618	It is necessary to use a optimistic concurrency control policy because the transactions cannot be open for a long period	f	1751	0
5619	It is necessary to use a pessimistic concurrency control policy to avoid frequent conflicts in the transactions	f	1751	1
5620	Transactional management is the complete responsibility of the repository	f	1751	3
5621	Resist to the attack	f	1731	1
5622	React to the attack	f	1731	2
5623	Resist and React to the attack	f	1731	3
5624	Depend on the types of the publishers components	f	1538	0
5625	Are completely independent	f	1538	2
5626	It is necessary to support dynamic Defer Binding of components, publishers and subscribers, to the connector to be completely independent	f	1538	3
5627	They only concern the web designers	f	1669	0
5628	They are dependent on performance tactics	f	1669	1
5629	They are dependent on availability tactics	f	1669	2
5630	Performance	f	1766	0
5631	Reliability and Performance	f	1766	1
5632	Security	f	1766	3
5633	Only have a server for write requests	f	1684	0
5634	Store all the information statically	f	1684	1
5635	Use several levels of cache	f	1684	3
5636	Passive Redundancy and Increase Resources Efficiency	f	1749	0
5637	Active Redundancy and Maintain Multiple Copies of Computation	f	1749	2
5638	In the server	f	1723	0
5639	In the repository	f	1723	2
5640	In the client	f	1723	3
5641	Escalating Restart	f	1746	0
5642	Voting	f	1746	1
5643	Exception Handling	f	1746	3
5644	Prioritize Events	f	1753	0
5645	Increase Resources	f	1753	1
5646	Maintain Multiple Copies of Data	f	1753	3
5647	Security and Mobility	f	1777	0
5648	Performance, Security and Interoperability	f	1777	1
5649	Security, Performance, Usability, Interoperability and Mobility	f	1777	2
5650	Separates the Renderer process from the other processes	f	1773	0
5651	Applies machine learning techniques	f	1773	2
5652	Uses prefetching	f	1773	3
5653	Repository	f	1762	0
5654	Service-Oriented Architecture	f	1762	2
5655	Client-Server	f	1762	3
5656	The page is in cache	f	1790	1
5657	Uses the Maintain Multiple Copies of Computation tactic	f	1790	2
7843	Crash.	f	1331	1
5658	Uses the Maintain Multiple Copies of Data tactic	f	1790	3
5659	The new user interface started using the REST interface	f	1755	0
5660	External applications can read and send messages to GNU Mailman	f	1755	2
5661	The GNU Mailman interface became public	f	1755	3
5662	Communicating-Processes	f	1756	0
5663	Client-Server	f	1756	1
5664	Publish-Subscribe	f	1756	3
5665	Decomposition	f	1757	1
5666	Aspects	f	1757	2
5667	Pipes-and-Filters	f	1757	3
5668	The quality of Availability	f	1758	1
5669	The quality of Reliability	f	1758	2
5670	The FIFO delivery of messages	f	1758	3
5671	Security and Testability	f	1822	1
5672	Reliability and Modifiability	f	1822	2
5673	Reliability and Testability	f	1822	3
5674	Module	f	1824	0
5675	Component-and-Connector	f	1824	1
5676	Module, but only for the Decomposition architectural style	f	1824	3
5677	Performance and Interoperability	f	1825	1
5678	Easiness of Development and Performance	f	1825	2
5679	Interoperability	f	1825	3
5682	Data Model	f	1334	3
5683	User Model and Undo	f	1326	0
5684	User Model and System Model	f	1326	2
5685	System Model	f	1326	3
5686	It is not possible to support SQL searches in the application server	f	1833	0
5687	It is always necessary to search in the database before accessing an object	f	1833	1
5688	All accesses to objects should occur through their inter-references	f	1833	2
5689	Exception Handling	f	1965	0
5690	Increase Competence Set	f	1965	1
5691	Exception Prevention	f	1965	2
5692	The Model module uses the Observer module	f	1987	0
5693	The Model module uses the Observer module if data is sent in the notification	f	1987	1
5694	The Model module uses the Observer module if complex data is sent in the notification	f	1987	2
5695	The Uses views are designed first	f	1503	0
5696	The Layered view are designed first	f	1503	1
5697	Whenever there is at least one Uses view then a Layered view needs to be designed as well	f	1503	3
5698	Can only contain a single architectural style	f	1701	0
5699	May contain several architectural styles, but only if the are of the Component-and-Connector and Allocation viewtypes	f	1701	1
5700	May contain several architectural styles, if that is the best way to convey the information to a group of stakeholders	f	1701	3
5701	Can only be applied after the Decomposition view is finished	f	1831	0
5702	Can be applied before a Decomposition view is designed	f	1831	1
5703	Should be applied in at least a view of the system	f	1831	3
5704	It is not necessary to have transactional behavior in the business logic	f	1325	1
5705	The Component-and-Connector architecture needs to have three Tiers	f	1325	2
5706	The Module architecture needs to have three Layers	f	1325	3
5707	The Transaction Script pattern to help demarcate the business transactions.	f	1834	0
5708	The Data Access layer to be able to access the data that it needs in each service.	f	1834	2
5709	The Table Module pattern to hide the details of the table structure for the Presentation layer.	f	1834	3
5710	The Transaction Script pattern.	f	1835	0
5711	The Table Module pattern.	f	1835	1
5712	The Service Layer pattern.	f	1835	3
5713	The Requirements function is not part of the RulesSet module.	f	1829	1
5714	The Requirements function is part of the Objects module.	f	1829	2
5715	The Requirements function is part of the Dynamic Design module.	f	1829	3
5716	An Encrypt Data tactic for the Security quality.	f	1802	1
5717	A Verify Message Integrity tactic to React to Attacks for the Security quality.	f	1802	2
5718	An Exception Prevention tactic to Prevent Faults for the Availability quality.	f	1802	3
5719	The source of stimulus for scenarios of the Availability quality.	f	1764	0
5720	The stimulus for scenarios of the Availability quality.	f	1764	1
5721	The source of stimulus for scenarios of the Security quality.	f	1764	3
5722	The source of stimulus is the ruleset.	f	1836	0
5723	The ruleset designer is the stimulus.	f	1836	1
5724	The response is defer binding.	f	1836	3
5725	Schedule resources.	f	1837	0
5726	Condition monitoring.	f	1837	1
5727	Reduce overhead.	f	1837	2
5728	Use a classic 3-layer architecture with the following layers, from top to bottom: Presentation, Domain Logic, and Data Access.	f	1317	1
5729	Use an aspect-oriented architecture, where an aspect is used to generate a graphical interface.	f	1317	2
5730	An allocation element	f	1500	3
5731	Use two deployment views, each one allocating different components to different machines with different operating systems.	f	1317	3
5732	It is not necessary to use a ''Data Access'' layer because the information is simple.	f	1340	1
5733	We must identify a module for writing the scores in a Decomposition style.	f	1340	2
5734	We may assign the responsibility of writing the scores to another module that already has other responsibilities.	f	1340	3
5735	Client-server in both cases.	f	1445	0
5736	Peer-to-peer in both cases.	f	1445	2
5737	Peer-to-peer in the first case and Client-Server in the second.	f	1445	3
5738	Depends mostly on the system's functional requirements.	f	1721	0
5739	Depends more on the architect's experience than on anything else.	f	1721	1
5740	Should not depend on the skills of the developing team.	f	1721	2
5741	May be responsible for the Featuritis problems of architectures.	f	1761	0
5742	Is focused on creating common generalizations of several systems.	f	1761	2
5743	Is focused on the details of the architecture.	f	1761	3
5744	The current location is the source of the stimulus.	f	1841	0
5745	The traffic monitoring system is the environment.	f	1841	1
5746	The Google Map is the artefact.	f	1841	2
5747	A component-and-connector view using a shared-data style.	f	1499	0
5748	A data model view.	f	1499	1
5749	A service-oriented architecture view.	f	1499	2
5750	Peer-to-peer style.	f	1530	0
5751	Pipe-and-Filter style.	f	1530	1
5752	Publish-subscribe style.	f	1530	3
5753	The team did not know the FenixFramework.	f	1789	0
5754	A domain layer is absent from the architecture.	f	1789	2
5755	Most of the information is stored in the client.	f	1789	3
5756	It is necessary to design a single deployment view that contains all the variation, because only the hardware capabilities change.	f	1806	1
5757	Two different component-and-connector views are necessary to represent the same runtime behavior of the system.	f	1806	2
5758	The deployment options have a large impact on the work assignment view.	f	1806	3
5759	Passive redundancy for availability, because it is possible to recover from the commands log.	f	1817	0
5760	Undo tactic for usability, because the server can undo the snapshot.	f	1817	1
5761	Multiple copies of data tactic for performance, clients do not have to execute the commands.	f	1817	3
5762	The server propagates them to all the clients.	f	1818	0
5763	The server only propagates local commands to the clients and keeps cursor movements in a log and the snapshots in a repository.	f	1818	2
5764	The server propagates the snapshots and the cursor movements to the clients and store the local commands for the initialization of new clients.	f	1818	3
5765	Testability.	f	1819	0
5766	Modifiability.	f	1819	1
5767	Performance.	f	1819	3
5768	Requires a more skilled team, because the object-oriented paradigm is more complex than the procedural paradigm	f	1827	0
5769	Is typically used with more complex data access code	f	1827	1
5770	Requires that we write more code when we have only a couple of simple use cases	f	1827	2
5771	The Service Layer pattern	f	1851	0
5772	The Transaction Script pattern	f	1851	2
5773	The Data Mapper pattern	f	1851	3
6231	A Uses view	f	1479	2
5774	Heartbeat requires the availability monitor to confirm the reception of the signal	f	1779	1
5775	In Ping-and-echo the availability monitor should always send the same request	f	1779	2
5776	In Heartbeat, the monitored components can change the message rate	f	1779	3
5777	It is not possible to achieve this requirement. A non-architectural solution is to be careful when hiring system administrators	f	1842	0
5778	It is necessary to use the authenticate authors tactic to authenticate system administrators before they access to the database	f	1842	1
5779	It is necessary to use the encrypt data tactic to encrypt the information with a password that is in a configuration file	f	1842	2
5780	A scenario for performance associated with a multiple copies of computation tactic	f	1843	0
5781	A scenario for usability associated with a support system initiative tactic	f	1843	1
5782	A scenario for performance associated with a limit event response tactic	f	1843	2
5783	Active redundancy	f	1844	1
5784	Increase resource efficiency	f	1844	2
5785	All of the above	f	1844	3
5786	Data model view	f	1687	1
5787	Generalization view	f	1687	2
5788	Layered view	f	1687	3
5789	To analyse the source code of the system to see how it is built	f	1423	0
5790	To analyse the system's functional requirements to see what is the system supposed to do	f	1423	1
5791	To analyse the implemented set of features to see what is it that the system actually does	f	1423	2
5792	The architecture of a system cannot change	f	1455	0
5793	The main goal of an architect is to identify the quality attributes of system	f	1455	1
5794	The main goal of an architect is to design a detailed structure of the system that supports most of the requirements	f	1455	3
5795	It is not a good idea to consider performance when designing the architecture of the system	f	1845	0
5796	The performance of a system only depends on the global performance strategies	f	1845	1
5797	Testability and maintainability always conflict with performance	f	1845	2
5798	Have a view for each stakeholder	f	1846	1
5799	Have at least a view for each viewtype	f	1846	2
5800	Have a view for each group of interconnected components, and very often a system has several groups of interconnected components	f	1846	3
5801	We should always satisfy in the first place the requirements of the more important stakeholders (such as the client)	f	1791	0
5802	If no order was established among them, we would not know from where should we start the design process	f	1791	1
5803	If one of the stakeholders complains that her requirement is not satisfied, we may explain to her that there were other more important requirements first	f	1791	2
5804	A module	f	1500	0
5805	Peer-to-Peer to represent the communication between the components	f	1452	0
5806	Client-Server to represent the request the application makes to the different new sources	f	1452	1
5807	Layers to create a virtual machine that hides the internals of the application from its users interface code to allow the support of different user interfaces	f	1452	3
5808	Performance because there is an overhead of communication between the modules.	f	1750	0
5809	Install because most of the modules need to be assigned to the same executable file	f	1750	1
7844	Timing.	f	1331	2
5810	Availability because if a module fails the failure easily propagates to all the other modules	f	1750	3
5811	Modifiability, because the jsdom code can not be reused by several threads	f	1847	1
5812	Security, because it describes a "queue overflow" attack	f	1847	2
5813	Interoperability, because the REST API allow the exchange of information with external applications	f	1847	3
5814	Reduce overhead tactic	f	1848	0
5815	Increase resources tactic	f	1848	2
5816	Testability tactic	f	1848	3
5817	Overall costs, because of deployment	f	1850	0
5818	Availability, because of the interprocess communication	f	1850	1
5819	Performance, because there is not a significative improvement by using more CPUs	f	1850	3
5820	A publish-subscribe style	f	1853	0
5821	A peer-to-peer style	f	1853	1
5822	A client-server style	f	1853	2
5823	It is enough to show the load-balancer between the web clients machines and the servers machines using a deployment view	f	1454	0
5824	It is necessary to create a uses view to show how clients require the correct functioning of servers	f	1454	2
5825	It is necessary to change the component-and-connector view to show the communicating processes	f	1454	3
5826	Is an architectural pattern.	f	1360	2
5827	They have many different use cases, corresponding to many distinct user interfaces	f	1450	0
5828	They need to be able to process concurrent requests from the users	f	1450	2
5829	They have a very complex domain logic that requires much processing power for answering each request	f	1450	3
5830	An aspects architectural style	f	1854	1
5831	A data model architectural style	f	1854	2
5832	A shared-data architectural style	f	1854	3
5833	Subscribes to the same kind of events that the sub2 port	f	1855	0
5834	Subscribes to the same kind of events that the inputSub port	f	1855	1
5835	It is unnecessary in the diagram because the :TableEditor can use port sub2 through the :Sheet component	f	1855	3
5836	The communicating processes architectural style	f	1821	0
5837	The client-server architectural style	f	1821	1
5838	The deployment architectural style	f	1821	2
5839	Featuritis may result from a requirement of the technical context.	f	1629	0
5840	Featuritis requires the performance quality because the end user needs to execute the features.	f	1629	1
5841	Featuritis requires the modifiability quality to allow a the system to be easily modified to support new features.	f	1629	3
5842	The book definition does not consider relevant the externally visible properties.	f	1702	0
5843	The book definition also considers that the properties are externally visible because by definition an architectural property is externally visible.	f	1702	2
5844	The book definition is not correct, as pointed out in the errata.	f	1702	3
5845	This shared understanding is necessary to define precise requirements.	f	1525	1
5846	This shared understanding does not allow to define the architecture trade-offs because some of the stakeholders have their own goals.	f	1525	2
5847	This shared understanding does not allow to have a global perspective of the system, because stakeholders have different interests.	f	1525	3
5848	Frank Buschmann are considering performance and security as the most important qualities.	f	1634	1
5849	Frank Buschmann is referring that the consequences of a flexible system is poor performance and bad security.	f	1634	2
5850	Frank Buschmann is not considering modifiability as an important quality	f	1634	3
5851	Performance should be the last quality to be addressed because it is a local property of an architecture.	f	1700	0
5852	Modifiability, flexibility, should be the first quality to be addressed because it allows the delay of architectural decisions.	f	1700	1
5853	The lack of functionality results in a system without business value, therefore a rich set of features should be implemented first.	f	1700	2
5854	A component.	f	1689	0
5855	Both, a component and a module, depending on the perspective.	f	1689	2
5856	An external element.	f	1689	3
5857	Is unable to define a domain model of the system.	f	1665	0
5858	Is focused on the technology context of the architecture.	f	1665	1
5859	Is focused on the details of the architecture.	f	1665	3
5860	Such misunderstanding and mistrust occurs because the stakeholders have their own agendas	f	1511	0
5861	The cycle Frank Buschmann refers to is the Architectural Influence Cycle.	f	1511	1
5862	The cycle Frank Buschmann refers to allows the clarification of requirements.	f	1511	2
5863	Is a functional prototype, which tests the functionalities required by the business stakeholders.	f	1512	0
5864	Is an architecture that demonstrates that the system will support the qualities raised by the stakeholders.	f	1512	1
5865	Is a system decomposition.	f	1360	3
5866	Featuritis may be a result of a requirement of the business context.	t	1629	2
5867	The book definition also considers that the properties are externally visible because they are used for reasoning by the stakeholders.	t	1702	1
5868	A module.	t	1689	1
5869	Is an object-oriented framework, which integrates functional and non-functional requirements of the system.	f	1512	3
5870	A component.	f	1519	0
5871	Both, a component and a module, depending on the perspective.	f	1519	2
5872	An external element.	f	1519	3
5873	Are unable to understand the technology capabilities.	f	1563	0
5874	Are focused on the project context of the architecture.	f	1563	1
5875	Are focused on the business context of the architecture.	f	1563	3
5876	A flexible architecture occurs when it is not possible to identify all the requirements.	f	1509	0
5877	Performance uncertainty about the system should be dealt with more flexibility.	f	1509	2
5878	A solution to this problem is to reduce the level of flexibility of a system.	f	1509	3
5879	Project and Technical Contexts.	f	1510	0
5880	Project and Professional Contexts.	f	1510	1
5881	Business and Project Contexts.	f	1510	2
5882	Modifiability.	f	1604	0
5883	Testability.	f	1604	2
5884	Availability.	f	1604	3
5885	Tries to guarantee that the final system will have the qualities required by stakeholders.	f	1342	0
5886	Does not allow developers to define some of the design of the system	f	1342	2
5887	It requires automatic generation of code from the architecture.	f	1342	3
5888	Performance.	f	1421	0
5889	Reliability.	f	1421	2
5890	Fault-tolerance	f	1421	3
5891	Implements a tactic to recover from faults.	f	1315	0
5892	Implements a tactic to prevent faults.	f	1315	1
5893	Can be used in a non-concurrent system.	f	1315	3
5894	Is an aggregate design tactic.	f	1399	0
5895	Is a maintain user model design tactic.	f	1399	1
5896	Is a design tactic for a scenario where the source of stimulus is the graph owner user.	f	1399	3
5897	Detect intrusion.	f	1704	0
5898	Limit access.	f	1704	1
5899	Separate entities.	f	1704	3
5900	The stimulus is a system input.	f	1321	0
5901	The response can be omitted.	f	1321	1
5902	The artefact can be outside the system.	f	1321	2
5903	The quality addressed is performance.	f	1322	1
5904	The quality addressed is availability and a voting design tactic is required to solve the problem.	f	1322	2
5905	The quality addressed is performance and a maintain multiple copies of data design tactic is required to solve the problem.	f	1322	3
5906	Detect intrusion.	f	1705	0
5907	Detect service denial.	f	1705	1
5908	Detect message delay.	f	1705	3
5909	Increase resources.	f	1754	0
5910	Reduce overhead.	f	1754	2
5911	Performance.	f	1372	0
5912	Availability.	f	1372	1
5913	Testability.	f	1372	3
5914	Ignore faulty behavior.	f	1769	0
5915	Transactions.	f	1769	1
5916	Rollback.	f	1769	2
5917	A Maintain Multiple Copies of Computation design tactic in the WebApp such that reads do not compete with writes.	f	1744	1
5918	A Maintain Multiple Copies of Data design tactic in Carbon.	f	1744	2
5919	A Maintain Multiple Copies of Data design tactic in the WebApp such that reads do not compete with writes.	f	1744	3
5920	Limit access, to restrict the access to the database system.	f	1706	0
5921	Limit exposure, locate the database system in the intranet.	f	1706	1
5922	Change default settings, because default passwords are sensitive.	f	1706	3
5923	Introduce concurrence tactic.	f	1595	1
5924	Increase resource efficiency tactic.	f	1595	2
5925	Maintain task model tactic.	f	1595	3
5926	Is a mediator, an application of the mediator pattern, between the input stimulus and the output response.	f	1360	0
5928	This situation corresponds to the use of the removal from service availability tactic.	f	1707	1
5929	This situation corresponds to the use of the limit access security tactic.	f	1707	2
5930	This situation corresponds to the use of the limit exposure security tactic.	f	1707	3
5931	The quality addressed is availability.	f	1962	0
5932	The quality addressed is availability and an active redundancy design tactic is required to solve the problem.	f	1962	2
5933	The quality addressed is modifiability and an increase cohesion design tactic is required to solve the problem.	f	1962	3
5934	Detect and Resist.	f	1708	0
5935	Detect and React.	f	1708	1
5936	Resist and React.	f	1708	3
5937	Testability.	f	1709	0
5938	Reliability.	f	1709	1
5939	Availability.	f	1709	2
5940	Performance.	f	1456	1
5941	Availability.	f	1456	2
5942	Usability.	f	1456	3
5943	Availability scenario.	f	1425	1
5944	Modifiability scenario.	f	1425	2
5945	Usability scenario.	f	1425	3
5946	Performance.	f	1436	0
5947	Availability.	f	1436	2
5948	Usability.	f	1436	3
5949	Change default settings.	f	1714	0
5950	Limit access.	f	1714	1
5951	Support user initiative.	f	1714	2
5952	Results in a similar decomposition as if the criteria was not applied but some modules are identified to be outsourced.	f	1960	0
5953	Results in a decomposition where each module may be implemented by a single developer.	f	1960	1
5954	Allows to increase the overall calendar development time of the project because there is a communication overhead with external teams.	f	1960	2
5955	Only contains business qualities.	f	1339	0
5956	Cannot be defined for the security quality.	f	1339	1
5957	Contains the architectural tactics associated with architecturally significant requirements.	f	1339	2
5958	This ASR can easily be supported by the architecture.	f	1811	0
5959	This ASR should be supported by the architecture because of its high impact.	f	1811	1
5960	The architect should support this ASR after designing an architecture that supports all the ASRs with high business value.	f	1811	3
5961	Performance.	f	1716	0
5962	Reliability.	f	1716	1
5963	Usability.	f	1716	3
5964	Performance.	f	1717	0
5965	Interoperability.	f	1717	1
5966	Usability.	f	1717	3
5967	Availability scenario.	f	1730	1
5968	Modifiability scenario.	f	1730	2
5969	Persistence.	f	1718	1
5970	Retry.	f	1718	2
5971	Passive redundancy.	f	1718	3
5972	Applying the generalization style to identify child modules of a module in the loop chain.	f	1722	0
5973	Identifying which of the *uses* dependencies are actually generalization dependencies.	f	1722	2
5974	Decomposing a *uses* relation into different interfaces.	f	1722	3
5975	Limit access.	f	1719	1
5976	Authorize actors.	f	1719	2
5977	Separate entities.	f	1719	3
5978	The type of a connector does not depend on the type of its roles.	f	1579	0
5979	The type of a component does not depend on the type of its ports.	f	1579	1
5980	The attachment is a runtime relation which dynamically manages type compliance.	f	1579	2
5981	It is necessary design a CRUD matrix to show the dependencies between the persistent information.	f	1358	0
5982	It is enough to design a view of the Data Model architectural style at the conceptual level because Facebook information has a very simple structure.	f	1358	1
5983	It is not necessary to have any view of the Data Model architectural style because Facebook information has a very simple structure.	f	1358	2
5984	Schedule resources.	f	1720	0
5985	Maintain multiple copies of data.	f	1720	1
5986	Reduce overhead.	f	1720	3
5987	She encapsulates the connector qualities inside a higher level component.	f	1515	0
5988	She delays the complete specification of the connector for development time to have human resources to prototype and measure different implementations.	f	1515	1
5989	She does not want to clutter the view with details and trusts the development team to implement the connector according to the required quality level.	f	1515	2
5990	She has to use another architectural style to describe asynchronous communication.	f	1543	1
5991	She can use the request/reply connector but the server should not return results to the client.	f	1543	2
5992	She can define a variant of this style with asynchronous communication by allowing the server to have the initiative to initiate the interaction.	f	1543	3
5993	Allows the analysis of the impact of changes because if a module uses another it will necessarily have to change whenever the used module changes.	f	1713	0
5994	Improves testability because if a module uses another then it is only possible to test them together.	f	1713	1
5995	Improves testability because it informs the tester about which modules involved in circular use dependencies.	f	1713	3
5996	Is an extension of a view of the Data Model style.	f	1552	1
5997	Allows to avoid redundancy and inconsistency.	f	1552	2
5998	Describes the structure of the data used by the system.	f	1552	3
5999	Multiple copies of computation tactic.	f	1724	1
6000	Passive redundancy tactic.	f	1724	2
6001	Multiple copies of computation and Active redundancy tactics.	f	1724	3
6002	A module interface has to be attached to a single component port.	f	1734	0
6003	A module interface can be replicated but component ports cannot.	f	1734	1
6004	A module interface may be attached to several component ports.	f	1734	3
6005	It allows an undefined number of clients.	f	1524	0
6006	Servers can also be clients.	f	1524	2
6007	Rollback.	t	1718	0
6008	Servers can send a heartbeat to clients.	f	1524	3
6009	Sanity checking.	f	1460	0
6010	Exception detection.	f	1460	1
6011	Detect intrusion.	f	1460	2
6012	It is possible to change the repository schema without changing the data accessors.	f	1527	1
6013	The integration of a new data accessor only implies changes in the data accessors that access the same type of data.	f	1527	2
6014	The communication between data accessors does not occur through the repository.	f	1527	3
6015	This means that the modules inside a layer cannot be loosely coupled.	f	1968	0
6016	This means that this architectural style emphasizes the quality of performance.	f	1968	1
6017	This means that each module cannot use other modules inside the same layer.	f	1968	2
6018	Is a Client-Server style because consumers are clients and providers are servers.	f	1539	0
6019	Is a Peer-to-Peer style because consumers and providers are peers.	f	1539	1
6020	Is a Publish-subscriber style because consumers use an enterprise service bus.	f	1539	3
6021	In the view there are multiple instances of the `Writer` component.	f	1632	1
6022	In the view `Receiver` component's `client` port is not associated with an external port.	f	1632	2
6023	In the view the `produce` port of a `Receiver` component is attached to the `consume` port of a `Writer` component.	f	1632	3
6024	It encapsulates applications through well-defined interfaces.	f	1506	0
6025	It decouples the coordination of the interaction among applications from the applications themselves.	f	1506	1
6026	It improves transparency of location of service providers.	f	1506	2
6027	There is a message passing connector between the `read` port of `Queue` and the `data points access` port of `WebApp`.	f	1729	0
6028	There is a connector between the `producer` port of a `Queue` component and the `client` port of its `Carbon` component.	f	1729	2
6029	The `client` ports of `Carbon` and `WebApp` are connected to a `Client` component through the same connector instance.	f	1729	3
6030	Deployment style.	f	1536	0
6031	Install style.	f	1536	2
6032	Work assignment style.	f	1536	3
6033	It applies layers to tiers.	f	1535	0
6034	Is an extension of the Client-Server architectural style.	f	1535	2
6035	Defines tiers as components.	f	1535	3
6036	Deployment style.	f	1528	0
6037	Implementation style.	f	1528	1
6038	Work assignment style.	f	1528	3
6039	Memcached can be considered a sub-module of the Present Graphs module.	f	1333	1
6040	Memcached can be considered a direct sub-module of the top Graphite module.	f	1333	2
6041	Memcached is not a module.	f	1333	3
7845	Response.	f	1331	3
6042	Buffering can be considered a sub-module of the Present Graphs module.	f	1735	1
6043	Reliability and Security	f	1615	3
6044	Modifiability	f	1928	1
6045	Buffering can be considered a direct sub-module of the top Graphite module.	f	1735	2
6046	Buffering is not a module.	f	1735	3
6047	A work assignment view.	f	1635	0
6048	An install view.	f	1635	2
6049	An implementation view.	f	1635	3
6050	Google Chrome is more convenient for mobile devices because it has an optimized network stack that runs in any kind device.	f	1347	1
6051	Amazon Silk is more convenient for mobile devices because it customizes the number of threads that run in the device.	f	1347	2
6052	Google Chrome is more convenient for mobile devices because content delivery is optimized.	f	1347	3
6053	There is a ThousandParsec connector.	f	1567	0
6054	There is a Read/Write connector which encapsulates a redundant Repository.	f	1567	1
6055	There is a Read/Write connector which guarantees that only the turns of the last two players may be lost.	f	1567	3
6056	Supports asynchronous communication to deal with disconnected mode.	f	1568	0
6057	Implements an event bus that allows the server to inform the client about new order recommendations.	f	1568	1
6058	May loose some of the changes done on the client component.	f	1568	2
6059	The ConflictResolution module is part of the code executed by the : TableEditor component.	f	1574	0
6060	The code of the ConflictResolution module is executed by a broadcast connector that implements an eventbus between the SpreadSheet components.	f	1574	2
6061	The code of the ConflictResolution module is executed in a server component because it needs to be centralized.	f	1574	3
6062	An object oriented style is followed.	f	1621	0
6063	Row Data Gateway is the most suitable data source pattern.	f	1621	2
6064	A Service Layer should be used to provide an interface for the presentation layer.	f	1621	3
6065	In Amazon Silk a request for a web page corresponds to a peer-to-peer interaction between all the web components containing the resources.	f	1570	0
6066	In Google Chrome a request for a web page is accomplished by a single access to the internet.	f	1570	1
6067	In Google Chrome a request for a web page aggregates the page on the background before it is sent to the client.	f	1570	3
6068	Should be described as a submodule of the RulesSet module.	f	1589	0
6069	Should be described as a submodule of the Design module.	f	1589	2
6070	Should not be described as a module because it is a component.	f	1589	3
6071	The : TableEditor broadcasts the cursor position through the : Sheet.	f	1565	0
6072	Amazon Silk is more convenient for mobile devices because it does most of the computation in the cloud.	t	1347	0
6073	There is a Read/Write connector which guarantees that players turns are not lost.	t	1567	2
6074	Has reduced reliability qualities.	t	1568	3
6075	The ConflictResolution module is part of the code executed by the : Sheet component.	t	1574	1
6076	The business logic is organized around record sets.	t	1621	1
6077	In Amazon Silk a request for a web page corresponds to requesting a service from the amazon cloud.	t	1570	2
6078	Should be described as a submodule of but not included in the RulesSet subtree.	t	1589	1
6079	The : Sheet broadcasts the cursor position through the Pub port.	f	1565	2
6080	The : TableEditor broadcasts the cursor position through its : StatusCallback port.	f	1565	3
6081	Are responsible for loading the objects they refer to.	f	1622	0
6082	Are responsible for the management of transactions, begin and end of transactions.	f	1622	1
6084	Google Chrome uses a usability maintain system model tactic.	f	1587	1
6085	Amazon Silk predictions are constrained by the amount of information it can store about each user access.	f	1587	2
6086	Google Chrome predictions do not require storage in the client-side.	f	1587	3
6087	There is a ThousandParsec connector.	f	1590	0
6088	There is a Request/Reply connector.	f	1590	1
6089	There is an EventBus connector.	f	1590	3
6090	Was taken because Native applications provide better modifiability qualities.	f	1627	1
6091	Was taken because HTML5 provides better usability qualities.	f	1627	2
6092	Was taken because Native application provide better support for working offline.	f	1627	3
6093	The server implements the : Repository component.	f	1636	1
6094	The server implements the : Broadcast connector.	f	1636	2
6095	The server implements the SpreadSheet components	f	1636	3
6096	Row Data Gateway and Active Record.	f	1640	1
6097	Row Data Gateway and Data Mapper.	f	1640	2
6098	Active Record and Data Mapper.	f	1640	3
6099	Amazon Silk explicitly caches pages on the browser to optimize accesses.	f	1652	0
6100	Amazon Silk cache is not shared between different users of the service to support confidentiality.	f	1652	2
6101	Google Chrome cache is shared among the different users of a desktop machine.	f	1652	3
6102	As a specialization of the RulesSet module.	f	1637	0
6103	As a submodule of the RulesSet module.	f	1637	1
6104	As a specialization of the Design module.	f	1637	3
6105	A single bidirectional connector.	f	1638	1
6106	Three distinct unidirectional connectors.	f	1638	2
6107	A single unidirectional connector.	f	1638	3
6108	The Parser module is part of the code executed by the : TableEditor component.	f	1639	0
6109	The code of the Parser module is executed by a repository component, which is not represented in the view.	f	1639	2
6110	The code of the Parser module is executed by both, the : Sheet and the repository components (the latter is not visible in the view).	f	1639	3
6111	Table Data Gateway and Row Data Gateway.	f	1344	0
6112	Row Data Gateway and Active Record.	f	1344	1
6113	Row Data Gateway and Data Mapper.	f	1344	2
6114	The response is JUnit XML standard	f	1614	1
6115	The source of the stimulus is Sun	f	1614	2
6116	The measure of the response is a robust open-source community associated with it	f	1614	3
6117	Modifiability and Performance	f	1615	0
6118	Availability and Modifiability	f	1615	1
6119	In the Deployment view, because the presentation component is now executing in a different place	f	1349	0
6120	In the component-and-connector view, because the connector between the web client and the web server has to change	f	1349	1
6121	In the Layer view, because the order of the layers will have to change	f	1349	2
6122	The left part of the figure represents a three-layered architecture	f	1318	0
6123	The most relevant architectural style in the right part of the figure is shared-data	f	1318	1
6124	The system represented in the left part of the figure tends to be non-transactional	f	1318	2
6125	Service-oriented architecture to express how clients can access the services	f	1775	0
6126	Client-server to express how multiple clients can access the applications	f	1775	1
6127	Decomposition to express the different responsibilities assigned to each application	f	1775	3
6128	We should always satisfy in the first place the requirements of more important stakeholders (such as the client)	f	1359	0
6129	If no order was established among them, we would not know from where should we start the design process	f	1359	1
6130	If one of the stakeholders complains that his requirement is not satisfied, we may explain to him that there were other more important requirements first	f	1359	2
6131	Active redundancy	f	1415	1
6132	Ignore faulty behaviour	f	1415	2
6133	Ping/Echo	f	1415	3
6134	Consider the requirements not realistic	f	1623	0
6135	Apply tactics of defer binding to allow the addition of the new sources of information in initialization time	f	1623	1
6136	Consider this requirement as a non architecturally significant requirement	f	1623	3
6137	Increasing performance and availability	f	1668	0
6138	Increasing availability and decreasing performance	f	1668	1
6139	Increasing performance and decreasing availability	f	1668	2
6140	Due to its configuration strategy Apache has better performance	f	1626	0
6141	Performance was the main concern of the design of the configuration strategy in Nginx	f	1626	1
6142	Apache emphasizes the usability quality for system administrators by allowing to split the configuration by different files	f	1626	2
6143	It makes no sense to use views of the module viewtype, as they give only a static view of the system	f	1564	0
6144	You should use only views of the component-and-connector viewtype, which describe the dynamic aspects of the system	f	1564	1
6145	The only views that are relevant to performance requirements are views of the Deployment style	f	1564	3
6146	The Work Assignment style	f	1521	0
6147	The Client-Server style	f	1521	1
6148	The Deployment style	f	1521	2
6149	The Peer-to-Peer style	f	1466	0
6150	The Client-Server style	f	1466	2
6151	The Publish-subscribe style	f	1466	3
6152	Using threads ensures that the processing of each request is isolated from the remaining requests	f	1469	1
6153	With this approach they may use all of the available cores in multiprocessor machines	f	1469	2
6154	They are used for implementing enterprise applications that typically have complex domain logic and, by using threads, it is easier to reuse code from one request to another	f	1469	3
6155	An increase resource efficiency tactic	f	1482	0
6156	A schedule resources tactic	f	1482	1
6157	A manage sampling rate tactic	f	1482	3
6158	A decomposition view which represent the module for compare-and-set	f	1483	0
6472	Reliability	f	1874	3
6159	A client-server view with non-blocking connectors for the interaction between threads and core data structures	f	1483	1
6160	A deployment view which allocate threads to the multi-cores	f	1483	3
6161	The solution where the cache is responsible for the eviction has better availability	f	1674	1
6162	The solution where the application is responsible for the eviction has better modifiability	f	1674	2
6163	The solution where the cache is responsible for the eviction has better performance	f	1674	3
6165	Interoperability	f	1953	1
6166	Security	f	1953	3
6167	This law highlights the impact of the business on the architecture	f	1767	0
6168	This law states that architectures impact on the structure of the organization	f	1767	2
6169	This law does not apply to the design of architectures	f	1767	3
6170	Becomes unavailable for clients if there is a fault in the hardware of web server (srv-web)	f	1594	0
6171	Becomes unavailable for clients if there is a fault in the hardware of database server (srv-db)	f	1594	1
6172	Becomes unavailable for clients if there is a fault in the hardware of service server (srv-opc)	f	1594	2
6173	Performance is a quality that you have to address at the end of the development process	f	1683	0
6174	There is no system which can have good performance and be easily maintainable	f	1683	1
6175	The system performance quality has impact on the performance of the execution of tests	f	1683	3
6176	They are both modules	f	1608	0
6177	The *Request Node* is a component and the *Cache* is a module	f	1608	2
6178	The *Request Node* is a module and the *Cache* is a component	f	1608	3
6179	By executing in parallel each of the phases of the pipeline corresponding to the processing of a request	f	1630	1
6180	By executing in parallel the processing of the various requests	f	1630	2
6181	By processing completely each request before moving to the next one, in a sequential process	f	1630	3
7846	The mean time before failure.	f	1798	0
7847	The mean time to repair.	f	1798	1
6182	You need to design a client-server view representing the interaction between the DSL and the servers that execute its commands	f	1688	1
6183	You need to design an implementation view to allow system administrators configure the builds	f	1688	2
6184	You do not need to change the views because the DSL does not have any architectural impact	f	1688	3
6185	A uses view which represent modules for the externalizers	f	1768	0
6186	A client-server view which represent the byte stream for transmission across a network	f	1768	1
6187	A connector that has the serialization and de-serialization speed qualities	f	1768	2
6188	The library approach does not build a cluster.	f	1748	3
6189	The main quality of the system in the left part of the figure is scalability	f	1971	1
6190	The main quality of the system in the right part of the figure is ease of development	f	1971	2
6191	The main quality of the system in the left part of the figure is to promote cross-functional teams	f	1971	3
6192	Data model to express the stored data formats	f	1361	0
6193	Aspects to express the evolution of service interfaces	f	1361	2
6194	Publish-subscribe to express how data is shared between services	f	1361	3
6195	Performance because all requests will be processed faster	f	1677	0
6196	Availability because even if PartB1 is not available partB2 can be provided	f	1677	2
6197	Reliability because a single correct read is used to responde to several requests	f	1677	3
6198	Is useful only if done (even if only partially) before the system's implementation is concluded, given that the architecture is used for restricting the implementation	f	1774	0
6199	Is useful only if done (even if only partially) before the system's implementation is concluded, because if the system is already implemented, its implementation uniquely determines the architecture	f	1774	1
6200	Is useful only if done (even if only partially) before the system passes all of the acceptance tests by the client, given that no more requirements changes will take place after that time	f	1774	2
6201	The components that manage the communication between the remaining elements in the system	f	1771	1
6202	The stakeholders that drive the development of the system	f	1771	2
6203	The design of a reusable interface is the stimulus.	f	1353	2
6204	The tactics that satisfy the most important requirements for the system	f	1771	3
6205	Communicating processes	f	1613	0
6206	Communicating processes and shared-data	f	1613	1
6207	Communicating processes, shared-data and service-oriented architecture	f	1613	2
6208	A module may contain code from different components	f	1429	0
6209	A module may execute code from different components	f	1429	2
6210	A component may contain code from different modules	f	1429	3
6211	They describe general requirements that all systems should try to satisfy	f	1473	0
6288	It describes a performance scenario where the stimulus is the request of a custom graph.	f	1966	0
6374	Generalization.	f	1663	3
6212	They allow us to build a more robust architecture that satisfies less specific requirements, which address a wider range of situations that may happen in the system	f	1473	1
6213	They identify the most important requirements that the system should satisfy	f	1473	2
6214	There is a high level of communication between the several modules, and this will cause the system to have a low performance	f	1955	0
6215	It is not possible to install the system in more than one machine	f	1955	1
6216	It is very hard to explain what the system does, because we need to understand all the execution fluxes	f	1955	3
6217	The Peer-to-Peer style	f	1391	0
6218	The Client-Server style	f	1391	1
6219	The Publish-subscribe style	f	1391	3
6220	The Shared data style	f	1420	0
6221	The Pipes-and-filters style	f	1420	1
6222	The Peer-to-Peer style	f	1420	2
6223	Ping/Echo	f	1343	0
6224	Heartbeat	f	1343	1
6225	Removal from Service	f	1343	3
6226	Launch a new process for processing each request	f	1366	0
6227	Spawn a new thread for processing each request	f	1366	1
6228	Buy a server with high processing power	f	1366	3
6229	A Deployment view	f	1479	0
6232	Increasing performance and availability	f	1338	0
6233	Increasing availability and decreasing performance	f	1338	1
6234	Increasing scalability and availability	f	1338	3
6235	Make a business case for the system	f	1961	0
6236	The system design	f	1961	2
6237	Documenting and communicating the architecture	f	1961	3
6238	Modifiability	f	1781	0
6239	Availability	f	1781	1
6240	Testability	f	1781	2
6241	Performance	f	1858	0
6242	Usability	f	1858	2
6243	Security	f	1858	3
6244	The data input to the system is the stimulus.	f	1353	3
6245	Should be avoided because scenarios should describe very concrete situations.	f	1480	0
6246	Can omit some of the elements like, for instance, the environment, if they are not relevant for the general scenario.	f	1480	2
6247	Is a very reusable scenario that can be used in many different concrete situations.	f	1480	3
6248	Can be applied to any kind of availability scenario.	f	1815	0
6249	Guarantees that the system will not become unavailable.	f	1815	2
6250	Reduces the availability scenario response time because the request occurs twice.	f	1815	3
6251	The business aspects of the system are for business architects, not the software architects.	f	1485	0
6252	Dealing with the technological aspects of the system should be delayed to the implementation stage of development.	f	1485	1
6253	The modeling of a system is not part of the software architect duties.	f	1485	2
6254	Martin Fowler disagrees with this definition because we should delay the design decisions and focus on features first.	f	1561	0
6255	Martin Fowler complains about this definition because architecture should stress flexibility which can only be necessary later.	f	1561	2
6256	Martin Fowler disagrees with this definition because to design an architecture it is not necessary to make any decision.	f	1561	3
6257	This view highlights the availability of the system.	f	1478	0
6258	This view highlights the performance of the `Image File Storage`.	f	1478	1
6259	This view highlights the scalability of `upload` and `dowload` operations.	f	1478	3
6260	This is a case of an architectural influence cycle where the feedback cycle resulted in changes on the business.	f	1763	0
6261	This is a case of an architectural influence cycle where the feedback cycle resulted in changes on the project.	f	1763	1
6262	This is a case of an architectural influence cycle without feedback.	f	1763	3
6263	This view highlights the security of the system.	f	1401	0
6264	This view highlights the scalability of `upload` and `dowload` operations.	f	1401	1
6265	This view highlights the scalability of storage.	f	1401	2
6266	This shared understanding can be represented by a set of architectural views.	f	1782	0
6267	The system algorithms should be part of the shared understanding.	f	1782	2
6268	The shared understanding describes the system from a high-level perspective.	f	1782	3
6269	The request to adapt an interface is the stimulus.	f	1353	1
6270	This view highlights the availability of the `Image File Storage`.	f	1685	0
6271	This view highlights the performance of `upload` operations.	f	1685	2
6272	This view highlights the scalability of `upload` and `dowload` operations.	f	1685	3
6273	Design an architectural solution together with the stakeholders to be sure that everybody agrees on the resolution of conflits.	f	1793	0
6274	Solve the conflicts between requirements by deciding on the best trad-offs the system should support.	f	1793	1
6275	Design an architecture that supports all the conflicting requirements and present it to the stakeholders.	f	1793	3
6276	The set of structures is needed to support different levels of performance for the system.	f	1560	0
6277	The hardware is an example of a software element.	f	1560	2
6278	There isn't any relation between the properties of the software elements and the ability to reason about the system.	f	1560	3
6279	These tactics are used to prevent the occurence of a fault.	f	1812	1
6280	Spare guarantees immediate recover.	f	1812	2
6281	Passive redundancy does not work with non-deterministic behavior of request's execution.	f	1812	3
6282	It describes an availability scenario because the configuration allows to define redundant virtual servers.	f	1860	0
6283	It describes a scalability scenario because it is possible to increment the size of the configuration at a linear cost.	f	1860	1
6284	It describes a modifiability scenario because of the cost associated with maintaining the configuration.	f	1860	3
6285	The architect cannot backtrack the decomposition decisions she made.	f	1431	1
6286	During the design process the number of architecturally significant requirements cannot change.	f	1431	2
6287	Contraints cannot be used as requirements for the decomposition process.	f	1431	3
6289	The scenario is supported by a manage sampling rate tactic because several requests to the same graph return the same result.	f	1966	1
6290	It describes a usability scenario where the source of stimulus is a non-technical user.	f	1966	2
6291	Has as main goal the reduction of the modules' size.	f	1871	0
6292	Results in the creation of a third module that does not have to change when any of the original modules are changed.	f	1871	1
6293	Increases the cohesion between the two modules.	f	1871	2
6294	Introduce concurrency.	f	1862	0
6295	Increase resources.	f	1862	1
6296	Maintain multiple copies of computation.	f	1862	3
6297	This is a performance scenario because the stimulus is an input, *launches several instances of the system*.	f	1378	0
6298	This is not a modifiability scenario because the source of the stimulus cannot be a system administrator.	f	1378	2
6299	This is a modifiability scenario and its environment design time.	f	1378	3
6300	Bound execution times, and increase resources.	f	1988	1
6301	Manage sampling rate, bound queue sizes, and increase resources.	f	1988	2
6302	Bound queue sizes, and increase resources.	f	1988	3
6303	Only the scenarios that have high architectural impact and high business value should appear in the tree.	f	1416	0
6304	A scenario for a power outage should have a low business value because the fault is temporary.	f	1416	1
6305	A scenario for a 24 hours x 7 days availability of the system should appear as a leaf of the utility tree.	f	1416	2
6306	Manage sampling rate.	f	1434	0
6307	Limit event response.	f	1434	1
6308	Prioritize events.	f	1434	2
6309	This decision does not have any impact on the architecture.	f	1475	0
6310	This decision needs to be made concrete by an interoperability scenario.	f	1475	2
6311	It describes a reliability scenario because the data points for each metric will be split between a buffer and disk.	f	1976	0
6312	It describes a performance scenario for the execution of reads.	f	1976	1
6313	The tactic used to solve the problem is not manage sampling rate because there isn't any loss of data points.	f	1976	3
6314	Increase resource efficiency.	f	1692	0
6315	Increase resource efficiency and Increase resources.	f	1692	2
6316	A security scenario because it allows the introduction of filters to encrypt the messages.	f	1863	0
6317	A availability scenario because it allows the introduction of load balancers.	f	1863	1
6318	A usability scenario because developers can extend the system without having to modify the nginx core.	f	1863	3
6319	A low cost of change implies a low cost of development, because changing the code is part of development.	f	1808	1
6320	A high cost of change occurs if it is necessary to defer the binding of what needs to be changed.	f	1808	3
6321	A client-server style.	f	1470	0
6322	A shared-data style.	f	1470	1
6323	A blackboard style.	f	1470	3
6324	Passive redundancy.	f	1747	0
6325	Active redundancy.	f	1747	1
6326	Voting.	f	1747	2
6327	Its main goal is to establish the reusability qualities of the architecture.	f	1609	0
6328	Project managers are not interested in views that use this style because it lacks the necessary level of detail.	f	1609	1
6329	Incremental development is a criteria that drives the design of views of this type.	f	1609	2
6330	The library approach allows non-java applications.	f	1748	0
6331	The server approach implements a local cache.	f	1748	2
6332	A component cannot be decomposed into a set of components and connectors.	f	1489	0
6333	A connector cannot be decomposed into a set of components and connectors.	f	1489	1
6334	Performance	f	1928	2
6335	A component can only have a single type of port.	f	1489	3
6336	Both, client-server and shared-data styles.	t	1470	2
6337	Maintain multiples copies of computation.	t	1747	3
6338	There should be at least one view of the system using this architectural style.	t	1609	3
6339	Introduce concurrency.	f	1864	1
6340	Tailor interface.	f	1864	2
6341	Increase resources.	f	1864	3
6342	Modifiability.	f	1861	0
6343	Availability.	f	1861	1
6344	Performance.	f	1861	2
6345	The Merge component executes the module merge.	f	1785	0
6346	The module main is executed in the Merge component.	f	1785	2
6347	The Pipe connectors do not execute any module.	f	1785	3
6348	Availability.	f	1759	1
6349	Modifiability.	f	1759	2
6350	Reliability.	f	1759	3
6351	Modifiability, because the Data Accessors do not depend on the data model.	f	1471	0
6352	Scalability of write requests, because a transactional system will synchronize the writes among the several repositories.	f	1471	2
6353	Confidentially of data, because it can be replicated in several repositories.	f	1471	3
6354	Usability.	f	1820	1
6355	Performance.	f	1820	2
6356	Modifiability.	f	1820	3
6357	A decomposition view.	f	1617	0
6358	A view of the component-and-connector viewtype.	f	1617	1
6359	A view of the component-and-connector viewtype and a deployment view.	f	1617	2
6360	Peer-to-peer.	f	1660	1
6361	Master-slave.	f	1660	2
6362	Pipe-and-filter.	f	1660	3
6363	Peer-to-peer.	f	1661	0
6364	Shared-data where the Buildbot is the data accessor.	f	1661	1
6365	Client-server where the Buildbot is the server.	f	1661	3
6366	Decomposition.	f	1662	0
6367	Generalization.	f	1662	1
6368	Peer-to-peer.	f	1662	3
6369	It assigns modules to the hardware.	f	1518	0
6370	It cannot assign software elements to virtual servers because they are not hardware.	f	1518	1
6371	For each set of software elements there is a single possible assignment to hardwre.	f	1518	2
6372	Client-server.	f	1663	1
6373	Shared-date.	f	1663	2
6375	Client-server to represent performance.	f	1542	0
6376	Service-oriented architecture to represent interoperability.	f	1542	2
6377	Shared-data to represent modifiability.	f	1542	3
6378	The data-shared architectural style is not applied because data is encapsulated inside services.	f	1474	0
6379	Modifiability is not a concern of their architecture.	f	1474	2
6380	The decouple of data formats does not support scalability because of the transactional properties.	f	1474	3
6381	Peer-to-peer.	f	1787	0
6382	Shared-data where the Dashboard is the repository.	f	1787	1
6383	Client-server where the Dashboard is the client.	f	1787	2
6384	Client-server.	f	1865	0
6385	Peer-to-peer.	f	1865	2
6386	Shared-data.	f	1865	3
6387	Work Assignment views	f	1458	0
6388	Generalization views	f	1458	1
6389	Deployment views	f	1458	2
6390	Client-Server	f	1351	1
6391	Peer-to-Peer	f	1351	2
6392	Uses	f	1351	3
6473	Manage sampling rate	f	1534	0
6393	Different stakeholders are interested in different views of the system	f	1377	0
6394	Do not have a software architecture, because in agile methodologies there is no architectural design phase	f	1970	1
6395	Do not have a software architecture, because the practice of refactoring allows changing every part of the system easily	f	1970	2
6396	May have a software architecture, but that architecture is not known because it was neither designed nor documented	f	1970	3
6397	However, functional requirements do not have any impact on the architecture because the systemic qualities of an architecture are non-functional	f	1491	0
6398	The functional requirements have a large impact on the definition of views of the component-and-connector viewtype because each component executes a functionality	f	1491	1
6399	The functional requirements can be considered as constraints on the software architecture design	f	1491	3
6400	The stimulus is incorrect response	f	1954	0
6401	The artefact is the load balancer	f	1954	1
6402	The quality it addresses is interoperability	f	1954	3
6403	Usability	f	1703	1
6404	Availability	f	1703	2
6405	Modifiability	f	1703	3
6406	Limit event response	f	1468	1
6407	Reduce overhead	f	1468	2
6408	Bound execution times	f	1468	3
6409	Have high throughput	f	1426	0
6410	Have low latency	f	1426	1
6411	Allow many simultaneous users	f	1426	2
6412	Per-to-peer	f	1531	0
6413	Shared-data	f	1531	1
6414	Communicating processes	f	1531	2
6415	The stimulus is a crash and the tactic is retry	f	1376	1
6416	The stimulus is an incorrect timing and the tactic is ignore faulty behaviour	f	1376	2
6417	The stimulus is incorrect response and the tactic is voting	f	1376	3
6418	The quality being addressed is performance and the tactic multiple copies of data	f	1869	0
6419	The quality being addressed is performance and the tactic multiple copies of computation	f	1869	1
6420	The quality being addressed is availability and the tactic passive redundancy	f	1869	3
6421	Client-server style	f	1870	1
6422	Shared-data style	f	1870	2
6423	Pipe-and-filter style	f	1870	3
6424	The shared-data architectural style is not applied because data is encapsulated inside services	f	1575	0
6425	Modifiability is not a concern of their architecture	f	1575	2
6426	The decouple of data formats does not support scalability because of the transactional properties	f	1575	3
6427	Task Model	f	1783	0
6428	an ACID transaction occurs in all the involved applications	f	1371	0
6429	a two-phase commit protocol takes place between the involved applications	f	1371	1
6430	a ACID transaction occurs in each of the involved applications, but we can not infer which transaction occurs first	f	1371	2
6431	The access to two different aggregate instances in the context of the same request does not hinder scalability	f	1410	0
6432	This is the solution followed by Twitter client applications	f	1410	1
6433	It describes the typical behavior of a microservices system	f	1410	2
6434	We just have to show, through component-and-connector views, that the system maintains replicas of the data in different components	f	1394	1
6435	We just have to show, through Deployment views, that the *DataNode* component executes in more than one machine of the cluster	f	1394	2
6436	We just have to show, through Decomposition views, that there are modules responsible for the replication of file blocks	f	1394	3
6437	Active replication and passive replication	f	1942	0
6438	Active replication, passive replication, and spare	f	1942	1
6439	Quorum, active replication, and passive replication	f	1942	3
6440	Performance	t	1703	0
6441	Manage sampling rate	t	1468	0
6442	May be easily changed to increase their performance	t	1426	3
6443	Publish-subscribe	t	1531	3
6444	A single view would be too simplistic	f	1377	1
6445	The views describe different aspects of the system	f	1377	2
6446	The environment is build time	f	1497	1
6447	The response is 5 person/month	f	1497	2
6448	To implement the above scenario it is necessary to apply a runtime defer binding tactic	f	1497	3
6449	Split module	f	1786	0
6450	Restrict dependencies	f	1786	2
6451	Defer binding	f	1786	3
6452	Essential to ensure the system scalability	f	1936	0
6453	Essential to ensure the system portability	f	1936	2
6454	Essential to facilitate the integration with legacy systems	f	1936	3
6455	But they could have used the ping tactic instead without adding any overhead to the NameNode	f	1866	1
6456	But the exceptions tactic could have been used as well	f	1866	2
6457	To inform other DataNodes about their availability	f	1866	3
6846	Dynamic Reconfiguration.	f	1908	0
6458	Is a high-level view of the system with the purpose of understanding what are the system's goals and features	f	1418	0
6459	Is a set of guidelines that the developing team should follow in the development of the system	f	1418	2
6460	Is a set of diagrams that show the runtime elements of the system and their relationships	f	1418	3
6461	Describing a set of steps that a user of the system must perform to accomplish some task	f	1523	1
6462	Describing a use case for the system that makes clear what should be the system's responses to each of the user's inputs	f	1523	2
6463	Describing the system's features by way of different usage scenarios for it, in which users play the role of actors	f	1523	3
6464	The scenario is not correct	f	1981	0
6465	The scenario is correct but it is not clear what is the artefact	f	1981	2
6466	The scenario is not completely correct because it contains two responses	f	1981	3
6467	Only in the Deployment view	f	1388	0
6468	Only in the Decomposition view	f	1388	1
6469	Only in a component-and-connector view	f	1388	2
6470	Availability	f	1874	1
6471	Modifiability	f	1874	2
6475	Reduce overhead	f	1534	2
6476	The presentation logic layer, domain logic layer, and data access layer	f	1493	0
6477	The traditional web applications, the mashups, and the rich internet applications (RIAs)	f	1493	1
6478	The web services layer, the domain logic layer, and the data access layer	f	1493	3
6479	It enforces the use of a single implementation language among all applications	f	1877	0
6480	The orchestration is in charge of improving the transparent location of service providers	f	1877	1
6481	The enterprise service bus coordinates the execution of several services	f	1877	2
6482	Work assignment view	f	1880	0
6483	Implementation view	f	1880	2
6484	Deployment view	f	1880	3
6485	Write a single scenario on performance	f	1336	0
6486	Write a scenario on performance and a scenario on interoperability	f	1336	2
6487	Write a single scenario on interoperability	f	1336	3
6488	Module viewtype	f	1857	0
6489	Install architectural style of the allocation viewtype	f	1857	2
6490	It is not necessary to represent this behavior because it does not describe any qualities	f	1857	3
6491	A component-and-connector view	f	1859	1
6492	An allocation view	f	1859	2
6493	They are not represented by a view	f	1859	3
6494	The Decomposition and the Layers styles	f	1385	1
6495	The Decomposition and the Uses styles	f	1385	2
6496	The Decomposition and the SOA styles	f	1385	3
6497	The Decomposition style	f	1430	0
6498	The Deployment style	f	1430	1
6499	The Work-assignment style	f	1430	3
6500	The Uses style	f	1876	1
6501	The Layers style	f	1876	2
6502	The Aspects style	f	1876	3
6503	Use a passive redundancy tactic in the Consumer Web site	f	1875	1
6504	Use an active redundancy tactic in the OPC (Order Processing Center) 	f	1875	2
6505	Use an active redundancy tactic in the Consumer Web site	f	1875	3
6506	The uses view to represent how the mobile device uses the Catalog application	f	1882	1
6507	The layered view to include a layer for each type of device	f	1882	2
6508	The domain layer of the layered style to represent the types of devices	f	1882	3
6509	The file transfers follows the same path of nodes used to identify where the file was located	f	1504	0
6510	Repository and Publish-subscribe.	f	1918	1
6511	The peer initiating the request for a file needs to know where the file is located	f	1504	1
6512	If a peer providing a file crashes it is necessary to restart downloading the file from the begin	f	1504	2
6513	All functionalities can be transactional	f	1792	0
6514	It is not necessary to have transactional properties because all data is in memory	f	1792	2
6515	Only the isolation property of transactions is supported	f	1792	3
6516	When an event is published to the distributed log, the order of delivery to the different subscribing applications is predefined	f	1881	0
6517	Performance	t	1874	0
6518	When two events are published to the distributed log they are delivered to the different subscribing applications in the same order	f	1881	1
6519	The distributed log may not deliver some of the events that are published to their subscribers	f	1881	3
6520	It allows high scalability because the data model has only four entities	f	1873	0
6521	It allows high scalability because the only synchronized access is to the `ProductId`, so it requires a single contention point	f	1873	2
6522	It does not allow high scalability	f	1873	3
6523	Work assignment	f	1459	1
6524	Decomposition	f	1459	2
6525	None, because this description does not describe any architectural aspect of the system	f	1459	3
6526	When a new block is created, the first replica is written in the node where the writer is located, to improve availability	f	1973	0
6527	When a new block is created, the second replica is not stored in the same rack than the first replica to increase the availability when a Data Node fails	f	1973	1
6528	When a new block is created, the third replica is stored in the same rack than the second replica to improve the performance of reads	f	1973	2
6529	Depends mostly on the system's functional requirements	f	1487	0
6530	Depends more on the architect's experience than on anything else	f	1487	1
6531	Should not depend on the skills of the developing team	f	1487	2
6532	Describes a concrete quality that a particular system has to implement	f	1884	0
6533	Can omit some of the elements like, for instance, the environment, if they are not relevant for the general scenario	f	1884	2
6534	Is a very reusable scenario that can be effectively used in many different concrete situations	f	1884	3
6535	The writing of a tweet is a synchronous process where different users have a consistent view of the sequence of tweets	f	1885	0
6536	A tweet is written in each one of the Twitter's servers	f	1885	1
6537	The tweet content is written in the home timeline of each one of the writer's followers	f	1885	3
6538	This solution optimizes the performance in terms of the latency of each request	f	1893	0
6539	This solution allows an "infinite"increase of the number clients by allowing the inclusion of more Request Nodes	f	1893	1
6540	This solution continues to provide service even if a crash occurs in the Data server	f	1893	2
6541	The periodic rebuild of the checkpoint is done to increase the availability of the NameNode	f	1813	0
6542	The advantage of running the CheckpointNode in a different host is to not degrade the availability of the NameNode during checkpoint construction	f	1813	1
6543	The periodic rebuild of the checkpoint improves the performance of the NameNode during normal operation	f	1813	2
6544	A non-functional requirement a system has to achieve	f	1894	0
6545	What should be the system response in the occurrence of a stimulus	f	1894	2
6546	A decomposition of the system that fulfills an architectural quality	f	1894	3
6547	The search timeline is the most important business use case for Twitter	f	1886	0
6548	The Early Bird server contains the tweet content	f	1886	2
6549	The write in the Early Bird server is synchronous, only when it finishes does the user receives the feedback of a successful post	f	1886	3
6550	This is right because if you don't the project fails	f	1546	0
6551	This is wrong because you can easily change these decisions during the project lifetime	f	1546	1
6552	This is wrong because it is against the agile way of thinking the software development process	f	1546	3
6553	This solution assures a consistency view to the clients of the data that is written	f	1323	0
6554	In this solution the clients invocations have to be synchronous	f	1323	1
6555	In this solution the tasks in the queue need to be sequentially processed, only when a task is finished can another start to be processed	f	1323	2
6556	Performance	f	1686	0
6557	Interoperability	f	1686	1
6558	Security	f	1686	3
6559	Performance qualities only	f	1950	1
6560	Availability qualities only	f	1950	2
6561	Performance and security qualities	f	1950	3
6562	Can be applied to any kind stimulus in availability scenarios	f	1586	0
6563	Can guarantee that the system will not become unavailable	f	1586	2
6564	When applied it increases the latency of the availability scenario's response time	f	1586	3
6565	Have high throughput	f	1883	0
6566	Have low latency	f	1883	1
6567	Allow many simultaneous users	f	1883	2
6568	Availability (Reliability)	t	1686	2
6569	Performance and availability qualities	t	1950	0
6570	Is useful to support scenarios where the stimulus is an omission	t	1586	1
6571	May be easily changed to increase their storage capacity	t	1883	3
6572	Encapsulate the module such that the clients of the module should not be aware of the remote invocations	f	1345	0
6573	Refactor the common parts between the business logic and the remote invocation	f	1345	2
6574	Increase the semantic coherence between the business logic code and the remote invocation code	f	1345	3
6575	The system would respond faster to all the clients' requests	f	1584	0
6576	The performance of the system would not change	f	1584	1
6577	The system would respond faster to requests made by DataNodes to update the metadata	f	1584	3
6578	The Ping/Echo tactic	f	1486	0
6579	The Heartbeat tactic	f	1486	1
6580	The Removal from Service tactic	f	1486	3
6581	System Model	f	1783	1
6582	This means that in this software system it is not possible to modularize each responsibility in a cohesive module	f	1803	0
6583	She should define finer-grained modules where she splits the unassigned responsibility	f	1803	1
6584	She should try to use a view of the Layered style and assign this responsibility to a module in the bottom layer that can be used by all the other modules	f	1803	3
6585	A change to the uses view to represent that friends can use each other catalog	f	1895	0
6586	A change of the layered view to support different presentations, one for each friend	f	1895	1
6587	A change of the decomposition view to include a set of new modules with the responsibilities associated with the access control	f	1895	2
6588	It is not necessary to have any view of the Data Model architectural style because Facebook information has a very simple structure	f	1977	0
6589	It is enough to design a view of the Data Model architectural style at the conceptual level because Facebook information has a very simple structure	f	1977	1
6590	It is enough to design a view of the Data Model architectural style at the logical level because the information will be stored in a relational database	f	1977	2
6591	Service-oriented architecture, and Client-server	f	1600	0
6592	Service-oriented architecture, and Shared-data	f	1600	1
6593	Service-oriented architecture, Shared-data, and Peer-to-peer	f	1600	2
6594	Represent the hardware infrastructure that allows components to communicate with each other	f	1405	0
6595	Represent the dependency relations that exist among the various components	f	1405	2
6596	Represent the control flow during an execution of the system	f	1405	3
6598	Tiers, and Shared-data	f	1507	1
6599	Tiers, Shared-data, and Service-oriented architecture	f	1507	2
6600	It is always guaranteed that all the published events are received by their subscribing components	f	1363	1
6601	The events should be delivered by the same order they are sent	f	1363	2
6602	The set of events types are predefined at initialization time	f	1363	3
6603	Shared-data and Communicating-Processes	f	1890	0
6604	Communicating-Processes	f	1890	1
6605	Tiers	f	1890	2
6606	A Module viewtype view	f	1896	0
6607	A Allocation viewtype view	f	1896	1
6608	A Install view	f	1896	3
6609	Whenever complex connectors are used in architectural views it is necessary to also document their decomposition.	f	1591	0
6848	Install.	f	1908	3
6610	It is preferable to only design views that do not use complex connectors to increase understandability.	f	1591	1
6611	Whenever possible it should be avoided to use complex connectors because developers have difficult to know how to implement them.	f	1591	3
6612	Aspects.	f	1355	1
6613	Layered.	f	1355	2
6614	Data model.	f	1355	3
6615	This solution optimizes the performance in terms of the latency of each request.	f	1383	0
6616	This solution allows an "infinite"increase of the number clients by allowing the inclusion of more Request Nodes.	f	1383	1
6617	This solution continues to provide service even if a crash occurs in the Data server.	f	1383	2
6618	Incorporate in the organization's core business the goals of a software house.	f	1673	0
6619	Do in-house development.	f	1673	1
6620	Reimplement all the information systems of the organization	f	1673	3
6621	A failure.	f	1816	0
6622	An error.	f	1816	1
6623	An input.	f	1816	3
6624	Stochastic event.	f	1809	0
6625	Overload.	f	1809	1
6626	Change level of service.	f	1809	2
6627	Split module.	f	1451	0
6628	Use an intermediary.	f	1451	1
6629	Refactor.	f	1451	3
6630	Decomposition style.	f	1983	0
6631	Generalization style.	f	1983	2
6632	Layered style.	f	1983	3
6633	The Decomposition style.	f	1986	0
6634	The Decomposition and Uses styles.	f	1986	1
6635	The Layered style.	f	1986	2
6636	An aggregate can contain a large number of instances.	f	1794	0
6637	An aggregate has runtime references to other aggregates.	f	1794	2
6638	An aggregate is cluster of domain classes.	f	1794	3
6639	Each service can be developed and deployed independently	f	1393	0
6640	Easier to scale development	f	1393	1
6641	Eliminates any long-term commitment to a technology stack	f	1393	2
6642	Aggregate.	f	1888	0
6643	Maintain user model.	f	1888	1
6644	Maintain task model.	f	1888	2
6645	This view shows that the `Adventure Builder Catalog DB` and the `OPC` components should be deployed in the same hardware.	f	1889	1
6646	This view **does not** show that the `Adventure Builder Catalog DB` and the `OPC` components can execute behind a firewall.	f	1889	2
6647	This view **does not** show that the access to the `web tier` has some security qualities.	f	1889	3
6648	Pipe-and-filter.	f	1891	0
6649	Maintain multiple copies of data.	f	1891	1
6650	Introduce concurrency.	f	1891	3
6651	Ignore faulty behaviour tactic	f	1892	0
6652	Ping-and-echo tactic	f	1892	1
6653	Active redundancy tactic	f	1892	2
6654	Cycles in the uses relation between modules are a good sign, because it indicates that several modules should be tested together.	f	1738	0
6655	The uses relation should be applied to the coarse-grained modules, because it allows to identify circular dependences.	f	1738	2
6656	There isn't any relation with the layered architectural style because the allowed-to-use relation is more generic.	f	1738	3
6657	Planning incremental releases of the system.	f	1526	1
6658	Estimating the effort needed to implement the system.	f	1526	2
6659	Analysing the system's portability and reusability.	f	1526	3
6660	All layers are mapped to the application server component.	f	1823	0
6661	Estimating the effort needed to implement the system	f	1413	2
6662	Publish-subscribe and Repository.	f	1918	2
6663	Interoperability	f	1928	0
6664	The presentation and domain logic layers are mapped to the application server component and the data access layer to the repository component.	f	1823	1
6665	The presentation layer is mapped to the browser component and the other two layers are mapped to the application server component.	f	1823	2
6666	It would reduce the scalability for updates of different orders for the same customer.	f	1887	0
6667	Two users would conflict if they attempt to edit different orders for the same customer.	f	1887	1
6668	As the number of orders grows it will be increasingly expensive to load the aggregate.	f	1887	2
6669	Shadow.	f	1316	1
6670	Voting.	f	1316	2
6671	Ignore faulty behavior.	f	1316	3
6672	Increase resources.	f	1422	0
6673	Bound queue sizes.	f	1422	2
6674	Introduce concurrency.	f	1422	3
6675	We do not need a view of the module viewtype because it is about the runtime properties of the system.	f	1373	0
6676	We do not need a view of the allocation viewtype because deployment is automated.	f	1373	1
6677	The component-and-connector view should emphasize the performance qualities of systems following the microservices architecture.	f	1373	2
6678	Usability and Performance.	f	1770	0
6679	Performance.	f	1770	2
6680	Modifiability.	f	1770	3
6681	It assigns components and connectors to people and teams.	f	1541	0
6682	It does not consider the software that is outsourced.	f	1541	2
6683	It allows to estimate the cost of hardware.	f	1541	3
6684	Performance.	f	1849	0
6685	Availability for incorrect responses from the Image File Storage component.	f	1849	1
6686	Performance and Availability for incorrect responses from the Image File Storage component.	f	1849	3
6687	One.	f	1467	0
6688	Two.	f	1467	1
6689	Three.	f	1467	2
6690	The cost of the modification.	f	1867	0
6691	That the integration of a new source will not have any impact on the other modules of the Catalog of DVDs.	f	1867	1
6692	That the modification can occur at runtime.	f	1867	3
6693	The Decomposition style.	f	1414	0
6694	The Client-Server style.	f	1414	1
6695	The Communicating Processes style.	f	1414	3
6696	This view shows that the processing of orders is done synchronously.	f	1593	0
6697	This view shows that bank debits are done asynchronously.	f	1593	2
6698	This view shows that the responses from the providers are processed synchronously.	f	1593	3
6699	Pipe-and-filter and tiers.	f	1878	0
6700	Shared-data and publish-subscribe.	f	1878	1
6701	Pipe-and-filter and publish-subscribe.	f	1878	2
6702	Guarantees that the redundant data in the client and the server is always synchronized.	f	1879	0
6703	Implements an event bus that allows the server to inform the client about new order recommendations.	f	1879	1
6704	It completely hides the server faults from the Pad user.	f	1879	3
6705	Abstract common services.	f	1910	1
6706	Restrict dependencies.	f	1910	2
6707	Encapsulation.	f	1910	3
6708	The *config* module is not used in the implementation of any component.	f	1872	0
6709	The *main* module is used in the implementation of all components.	f	1872	1
6710	The *Split* component uses the *to_lower* module for its implementation	f	1872	3
6711	The result of decisions that are made at the "upper floors" of the organization	f	1897	0
6712	The sole decision of an architect	f	1897	1
7554	All the previous options.	t	1979	3
6713	A set of software elements and their relations	f	1897	3
6714	The source of stimulus is the FenixEDU system	f	1368	0
6715	The stimulus is periodic	f	1368	1
6716	The environment is overloaded	f	1368	2
6717	Commercial	f	1596	0
6718	Technical	f	1596	1
6719	Professional	f	1596	3
6720	Availability of the Image Write Service, whenever one of the Image Write Service components crashes	f	1350	0
6721	Availability of the Image File Storage, whenever the Image File Storage component crashes	f	1350	2
6722	Performance of the Image Write Service	f	1350	3
6723	Availability whenever the server running the tasks crashes, the tasks are restarted and eventually finished	f	1409	0
6724	Performance of the tasks execution, scheduling of tasks can be optimized for the particular context of the system	f	1409	1
6725	Performance of the services being executed by the clients, they can execute other actions while waiting for the response	f	1409	2
6726	The synchronous solution requires less memory than asynchronous solution	f	1901	0
6727	In the synchronous solution a task can be associated, during its execution, with different execution entities, e.g. thread	f	1901	2
6728	In the asynchronous solution a task is always associated, during its execution, with the same execution entity, e.g. thread	f	1901	3
6729	Interoperability is a quality that as lower priority than performance	f	1900	0
6730	Scalability should be the quality to be achieved first by any architecture	f	1900	1
6731	That the use of XML technology for interoperability is not a correct decision	f	1900	2
6732	Performance	f	1390	0
6733	Availability	f	1390	1
6734	Time to market	f	1390	3
6735	The number of Image Write Service components should be the same of the number Image Retrieval Service components	f	1407	0
6736	The hardware where of Image Write Service components execute should have the same capabilities of the hardware where Image Retrieval Service components run	f	1407	1
6737	Both components, the Image Write Service and the Image Retrieval Service, should be designed using an synchronous model of interactions, where a thread is associated with each request	f	1407	2
6738	Introduce concurrency	f	1797	0
6739	Maintain multiple copies of data	f	1797	2
6740	Schedule resources	f	1797	3
6741	In the synchronous solution only some of the tasks that are being executed are lost and they have to be resubmitted by the client	f	1439	0
6742	Analysing the system's portability and reusability	f	1413	3
6743	In the asynchronous solution the tasks that are being executed are lost and they have to be resubmitted by the client	f	1439	1
6744	In the synchronous solution the tasks being executed are finished without requiring the client to resubmitted them	f	1439	3
6745	Provides the quality of performance	f	1672	1
6746	Provides the quality of modifiability	f	1672	2
6747	Does not provide any additional quality	f	1672	3
6748	The need to use a two-phase commit protocol	f	1433	0
6749	The need to have a tight integration of the development teams	f	1433	1
6750	The need to deploy all the microservices simultaneously	f	1433	3
6751	Manage sampling rate	f	1828	0
6752	Bound execution times	f	1828	1
6753	Increase resource efficiency	f	1828	3
6754	Reduction of cost is the most important impact of cloud computing in an architecture	f	1903	1
6755	Cloud computing has impact on the business but it is not an architectural aspect	f	1903	2
6756	Using cloud computing we cannot delay some architectural decisions	f	1903	3
6757	Its main goal is to establish the reusability qualities of the architecture.	f	1327	0
6758	Project managers are not interested in views that use this style because it lacks the necessary level of detail.	f	1327	1
6759	Views of this type are mostly useful to guide the testing of the system.	f	1327	2
6760	Task Model tactics.	f	1628	0
6761	System Model tactics.	f	1628	1
6762	User Model tactics.	f	1628	3
6763	Has as main goal the reduction of the modules' size.	f	1442	0
6764	Results in the creation of a third module that makes the original modules independent.	f	1442	1
6765	Increases the cohesion between the two modules.	f	1442	2
6766	Results from a utility tree for performance.	f	1795	0
6767	Results from a single availability scenario.	f	1795	1
6768	Results from the application of a single ADD iteration.	f	1795	2
6769	Programming, if the components execute modules developed by different teams.	f	1979	0
6770	Hardware, if there is hardware redundancy.	f	1979	1
6771	Operating Systems, if redundant components execute on top of different operating systems..	f	1979	2
6772	This is a performance scenario and the measure of the response is 10 minutes latency.	f	1381	0
7009	Introduce concurrency tactic.	f	1932	2
6773	This is not a modifiability scenario because the source of the stimulus cannot be a system administrator.	f	1381	2
6774	This is a modifiability scenario and its environment design time.	f	1381	3
6775	Each view contains a single architectural style.	f	1555	0
6776	Views need to contain more than one architectural style.	f	1555	1
6777	A view may not contain any architectural style.	f	1555	2
6778	The quality addressed is performance and a limit event response is required to solve the problem.	f	1387	1
6779	The quality addressed is availability and a voting design tactic is required to solve the problem.	f	1387	2
6780	The quality addressed is performance and a maintain multiple copies of data design tactic is required to solve the problem.	f	1387	3
6781	A low cost of change implies a low cost of development, because changing the code is part of development.	f	1396	1
6782	There is no relation between the cost of change and the cost of development.	f	1396	2
6783	The cost of change is higher if it occurs at runtime.	f	1396	3
6784	Prioritize events	f	1947	3
6785	Service-oriented architecture, and Client-server.	f	1508	0
6786	Service-oriented architecture, and Shared-data.	f	1508	1
6787	Service-oriented architecture, Shared-data, and Client-server.	f	1508	2
6788	The Aspects style.	f	1982	0
6789	The Generalisation style.	f	1982	1
6790	The Shared-data style.	f	1982	3
6791	But when the filters are executed sequentially the composition power is reduced.	f	1913	0
6792	But the size of buffers may reduce the composition power.	f	1913	2
6793	And filters do not have to agree on the data formats.	f	1913	3
6794	The view does not address the scenario	f	1505	0
6795	The view addresses the scenario because it separates the `Consumer Website` module from the `OpcApp` module.	f	1505	1
6796	The view addresses the scenario because the `Consumer Website` module does not use the interfaces a new business partner has to implement.	f	1505	3
6797	If the OPC crashes the Consumer Website can continue to provide service in normal mode.	f	1904	1
6798	If the Adventure Catalog BD crashes the Consumer Website can continue to present the Adventure Builder offers.	f	1904	2
6799	If a Bank component is not available the OPC cannot continue to provide service.	f	1904	3
6800	All the published events are received by their subscribing components.	f	1498	1
6801	The events should be received by the same order they are sent.	f	1498	2
6802	The set of events types are predefined at initialization time.	f	1498	3
6803	The Decomposition style.	f	1725	0
6804	The Generalisation style.	f	1725	1
6805	The Aspects style.	f	1725	3
6806	The type of a connector does not depend on the type of its roles.	f	1911	0
6807	Components of different types may have ports of the same type.	f	1911	1
6808	The attachment is a runtime relation which dynamically manages type compliance.	f	1911	2
6809	Client-server.	f	1540	0
6810	Shared-data.	f	1540	2
6811	Peer-to-peer.	f	1540	3
6812	Layer 4.	f	1912	1
6813	In a layered architecture all layers are equally modifiable.	f	1912	2
6814	Modifiability is not made easier by a layered architecture.	f	1912	3
6815	The view addresses the scenario because the uses relation between the `Consumer Website` module and the `OpcApp` module has the require properties.	f	1598	1
6816	The view addresses the scenario because it separates the modules that represent the interfaces a new business partner has to implement.	f	1598	2
6817	The view addresses the scenario because the `Consumer Website` module uses the `gwt` and `waf` modules.	f	1598	3
6818	Client-server and Repository.	f	1918	0
6819	The file transfer has to follow the same path of nodes used to identify where the file was located.	f	1582	0
6820	The peer initiating the request for a file needs to know where the file is located.	f	1582	1
6821	If a peer providing a file crashes the file will not be downloaded.	f	1582	2
6822	Modifiability.	f	1532	0
6823	Testability and Modifiability.	f	1532	2
6824	Maintainability and Availability.	f	1532	3
6825	A performance scenario associated with the latency of writing data points to disk.	f	1906	1
6826	An availability scenario associated with a fault in the *Carbon* component.	f	1906	2
6827	A usability scenario.	f	1906	3
6828	Simplifies the evolution of the event schema.	f	1907	0
6829	Simplifies the query operations in the event store.	f	1907	1
6830	Provides a programming model developers are familiar with.	f	1907	3
6831	The decomposition was driven by a defer binding tactic.	f	1902	0
6832	The decomposition was driven by a quality that is supported by a restrict dependencies tactic.	f	1902	1
6833	The decomposition was driven by a quality that is supported by an encapsulate tactic.	f	1902	3
6834	Native applications provide better modifiability qualities.	f	1905	1
6835	HTML5 provides better usability qualities.	f	1905	2
6836	Native applications provide better support for working offline.	f	1905	3
6837	A modifiability scenario the *Graphite* system.	f	1335	0
6838	A usability scenario of the *Graphite* system.	f	1335	1
6839	A performance scenario of the *Graphite* system.	f	1335	2
6840	A request for a web page corresponds to a peer-to-peer interaction between all the web components containing the resources.	f	1832	0
6841	Web pages are explicitly cached on the browser to optimize accesses.	f	1832	1
6842	It is possible to customize the number of threads that run in the mobile device.	f	1832	3
6843	She should decide to use a microservices architecture to improve the scalability of the system.	f	1364	0
6844	She should decide to use a modular monolith architecture to reduce the cost of development, because developers will not need to define intermediate states for the transactional execution of the business logic.	f	1364	1
6845	She should give up because it is not possible to have the two approaches in a singe architecture.	f	1364	3
6849	The view illustrates the achievement of a security scenario.	f	1909	0
6850	The view illustrates the achievement of a performance scenario.	f	1909	1
6851	The view results from the implementation of a support user initiative tactic.	f	1909	2
6852	Performance was traded for easy of development to reduce the overall development costs.	f	1562	0
6853	Performance was traded for the availability quality.	f	1562	2
6854	An incremental development was followed, which allowed to have the system in production without being necessary to export all the information to the mainframe.	f	1562	3
6855	A deployment view.	f	1599	0
6856	A work assignment view.	f	1599	1
6857	A install view.	f	1599	3
6858	This generalization was driven by a split module tactic.	f	1449	0
6859	This view fulfills an availability scenario, which defines the expected behavior whenever an external source is not available.	f	1449	1
7848	Both, the mean time before failure and the mean time to repair.	t	1798	2
7849	All options are false.	f	1798	3
6860	This view fulfills a modifiability scenario, which states that it should be easy to support the system in new software platforms, e.g. *Windows* or *OS X* .	f	1449	3
6861	A modifiability scenario the *Graphite* system.	f	1367	0
6862	A usability scenario of the *Graphite* system.	f	1367	1
6863	A single performance scenario of the *Graphite* system.	f	1367	2
6864	Does not allow optimizations according to the type of query.	f	1916	0
6865	Does not support independent scalability according to the type of operation.	f	1916	1
6866	Querying the event sourcing becomes more complex.	f	1916	3
6868	Usability.	f	1728	1
6869	Security.	f	1728	2
6870	Availability.	f	1728	3
6871	A view of the Data Model style	f	1985	0
6872	A view of the Layers style	f	1985	1
6873	A view of the Uses style	f	1985	3
6874	By changing the commonalities that are in the children.	f	1488	0
6875	Because the *is-a* relation does not allow reuse of implementation.	f	1488	1
6876	By adding, removing, or changing children.	f	1488	2
6877	With a Deployment view, where the *load balancer* is part of the communication infra-structure used to execute the system	f	1417	1
6878	With a Uses view, representing the existing dependencies between the *load balancer* and the services that it uses	f	1417	2
6879	With a Layers view, where the *load balancer* creates an abstraction layer between who makes the request and who provides the service	f	1417	3
6880	It implements a maintain multiple copies of computation tactic.	f	1927	0
6881	It supports the access to persistent information.	f	1927	2
6882	It implements a maintain multiple copies of data tactic.	f	1927	3
6883	The solution where the cache is responsible for retrieving the missing piece of data from the underlying store has better availability	f	1404	1
6884	The solution where the application is responsible for retrieving the missing piece of data from the underlying store has better modifiability	f	1404	2
6885	The solution where the cache is responsible for retrieving the missing piece of data from the underlying store has better performance	f	1404	3
6886	A change to the uses view to represent that friends can use each other catalog.	f	1919	0
6887	A change of the layered view to support different presentations, one for each friend.	f	1919	1
6888	A change of the decomposition view to include the responsibilities associated with the access control.	f	1919	2
6889	The Shared Data style	f	1392	0
6890	The Pipes-and-filters style	f	1392	1
6891	The Client-Server style	f	1392	3
6892	Planning incremental releases of the system	f	1413	1
6893	but this reduces reliability because de webapp components do not access the most recent data	f	1379	0
6894	but it reduces performance, anyway, because the buffer components easily overflow	f	1379	1
6895	and it improves security because the buffer is protected agains attacks	f	1379	3
6896	A *web services* architecture	f	1428	0
6897	A Client-Server architecture, where the *mashup* is the client and the various sources are the servers	f	1428	1
6898	A Publish-Subscribe architecture, where the various sources publish events with the changes made and the *mashup* subscribes those events	f	1428	3
6899	Increase Resources	f	1780	1
6900	Introduce Concurrency	f	1780	2
6901	Maintain Multiple Copies of Computation	f	1780	3
6902	Support user initiative tactic.	f	1921	1
6903	Maintain multiple copies of data tactic.	f	1921	2
6904	Conflict detection tactic.	f	1921	3
6905	Performance was traded for easy of development.	f	1922	0
6906	Performance was traded for the modifiability quality.	f	1922	2
6907	An incremental development was followed, which allowed to have the system in production without being necessary to export all the information to the mainframe.	f	1922	3
6908	This decomposition is the only possible of the original domain model.	f	1830	0
6909	This decomposition implies that products will frequently change their unique identification.	f	1830	2
6910	All the above.	f	1830	3
6911	These tactics cannot be applied in conjunction with the self-test tactic.	f	1969	0
6912	These tactics are used to prevent the occurrence of a fault.	f	1969	1
6913	In ping/echo the components have the initiative to start the interaction.	f	1969	3
6914	Only views of the component-and-connector viewtype are needed	f	1457	0
6915	Only views of the component-and-connector viewtype and allocation viewtype are needed	f	1457	2
6916	Views of the module viewtype are not needed	f	1457	3
6917	When the environment is design time it means that the change should be done before the system enters into production	f	1975	0
6918	When the environment is build time it means that it is necessary to codify a new module that is added by rebuilding the system	f	1975	1
6919	When the environment is runtime the cost of doing the change is higher than in the other environments	f	1975	3
7010	Schedule resources tactic.	f	1932	3
6920	Can use the operations defined in the lower layer, but not the ones defined inthe upper layer	f	1925	1
6921	Can use the operations defined in the upper layer, but not the ones defined inthe lower layer	f	1925	2
6922	Should use some operation defined in the lower layer	f	1925	3
6923	It corresponds to a particular case of a specialization in a generalization view.	f	1926	0
6924	It represents a relation between a connector's role and a port of one of its internal components.	f	1926	1
6925	It represents a relation between a component's port and a connector's role.	f	1926	3
6926	To promote the use of a common communication protocol for all the remaining components of the system	f	1576	1
6927	To increase the performance of the interaction between the components of the system	f	1576	2
6928	To create a strong coupling between the various services provided by the organization	f	1576	3
6929	When a peer connects to the network it establishes connections with all other peers in the network	f	1915	0
6930	When a peer receives a connection it sends all its files to the peer connecting it	f	1915	2
6931	The behavior described in the sentence can be represented in a view where the tier architectural style is used	f	1915	3
6932	The development team is the main stakeholder interesting in these views.	f	1496	0
6933	It assigns modules to files.	f	1496	1
6934	It is completely independent of the deployment architectural style.	f	1496	2
6935	The style of the connector used to represent the interaction between the browser and the web server changed	f	1920	1
6936	The browser is now a component of a different type	f	1920	2
6937	That evolution did not have any consequences on the software architecture of a web application	f	1920	3
6938	Prevent a fault in hardware.	f	1666	0
6939	Prevent a fault in software.	f	1666	1
6940	Prevent a fault in a process.	f	1666	2
6941	Manage sampling rate	f	1384	0
6942	Prioritize events	f	1384	2
6943	Bound execution time	f	1384	3
6944	Split module	f	1898	0
6945	Encapsulate	f	1898	1
6946	Defer binding	f	1898	3
6947	Two users would not conflict if they attempt to edit different orders for the same customer.	f	1852	1
6948	The increase of the number of orders would not have impact on the load the aggregate.	f	1852	2
6949	All the above.	f	1852	3
6950	Does not allow optimizations according to the type of query.	f	1924	0
6951	Does not support independent scalability according to the type of operation.	f	1924	1
6952	Does not support joins.	f	1924	3
6953	A module contains the code that executes in a single component and a component executes the code of a single module	f	1923	0
6954	A module contains the code that can execute in several components and a component executes the code of a single module	f	1923	1
6955	A module contains the code that executes in a single component and a component can execute the code of several modules	f	1923	2
6956	Limit event response	f	1444	1
6957	Prioritize events	f	1444	2
6958	Bound execution times	f	1444	3
6959	The uses view to show the coupling between the different platforms.	f	1337	1
6960	The uses view to show the uses relationships between the different platforms.	f	1337	2
6961	The data model view to represent each one of the platforms.	f	1337	3
6962	The view does not address the scenario	f	1938	0
6963	Communicating Processes.	t	1908	2
6964	The view addresses the scenario because it separates the modules that represent the interfaces a new business partner has to implement	f	1938	2
6965	The view addresses the scenario because the `Consumer Website` module uses the `gwt` and `waf` modules	f	1938	3
6966	User Model	f	1783	3
6967	Usability e Modifiability	f	1765	0
6968	Availability e Usability	f	1765	2
6969	Availability e Performance	f	1765	3
6970	A module view of the decomposition style.	f	1929	0
6971	A component-and-connector view of the service-oriented architecture style.	f	1929	2
6972	A module view of the uses style.	f	1929	3
6973	Suffered from featuritis, because the architect decided to delay the difficult parts for latter in the development.	f	1930	0
6974	Did not suffer from featuritis.	f	1930	1
6975	Suffered from featuritis, but it had no impact on the final development.	f	1930	3
6976	Usability and Performance	f	1778	0
6977	Performance	f	1778	2
6978	Testability	f	1778	3
6979	Performance	f	1375	0
6980	Security	f	1375	1
6981	Security and modifiability	f	1375	3
6982	Performance	f	1369	0
6983	Availability	f	1369	1
6984	Usability	f	1369	2
6985	Limit event response	f	1947	1
6986	Introduce concurrency	f	1947	2
6987	Maintain multiple copies of data	f	1838	0
6988	Bound execution times	f	1838	2
6989	Reduce overhead	f	1838	3
6990	Performance.	f	1607	0
6991	Interoperability.	f	1607	1
6992	Security.	f	1607	3
6993	Limit event response	f	1397	0
6994	Maintain multiple copies of computation	f	1397	1
6995	Maintain multiple copies of data	f	1397	2
6996	Component	f	1446	0
6997	Module	f	1446	1
6998	None of the above	f	1446	3
6999	Manage sampling rate	f	1341	0
7000	Limit event response	f	1341	1
7001	Maintain multiple copies of computation	f	1341	3
7002	Schedule resources	f	1949	1
7003	Bound execution times	f	1949	2
7004	Increase resource efficiency	f	1949	3
7005	Increase resources.	f	1585	1
7006	Increase resource efficiency.	f	1585	2
7007	Maintain multiple copies of data.	f	1585	3
7008	Manage sampling rate tactic.	f	1932	0
7393	The Shared-Data style	t	1391	2
7011	When the modification should occur.	f	1974	0
7012	The features that will be implemented.	f	1974	1
7013	Defer binding.	f	1974	3
7014	Is driven by functional requirements.	f	1382	0
7015	Is done in a single step, after all the tactics were identified.	f	1382	1
7016	Is a top-down process where a initial decomposition is chosen and it is successively decomposed without changing the initial decisions.	f	1382	2
7017	Is applied only once at the beginning of the architectural design process.	f	1943	0
7018	Is mostly driven by the security attribute quality.	f	1943	2
7019	Follows a bottom-up decomposition process of the system.	f	1943	3
7020	Ping/Echo.	f	1328	0
7021	Retry.	f	1328	1
7022	Passive Redundancy.	f	1328	3
7023	This ASR requires a specific architectural design because it profoundly affects the architecture.	f	1933	1
7024	The cost of meeting the ASR after development starts is too high.	f	1933	2
7025	Any ASR that has a high business value cannot have a low architecture impact because it needs to be supported by the architecture.	f	1933	3
7026	The invoked function may not have any input parameter.	f	1931	1
7027	The invoked function may not have any output parameter.	f	1931	2
7028	The invoked function may not have both any input parameter nor any output parameter.	f	1931	3
7029	Maintain multiple copies of data tactic.	f	1957	0
7030	Introduce concurrence tactic.	f	1957	1
7031	Increase resource efficiency tactic.	f	1957	2
7032	Maintain task model	f	1934	0
7033	Maintain user model	f	1934	1
7034	Aggregate	f	1934	3
7035	Whenever complex connectors are used in architectural views it is necessary to also document their decomposition.	f	1529	0
7036	It is preferable to only design views that do not use complex connectors to increase understandability.	f	1529	1
7037	Whenever possible it should be avoided to use complex connectors because developers have difficult to know how to implement them.	f	1529	3
7038	Peer-to-Peer.	f	1517	0
7039	Client-Server.	f	1517	2
7040	If there is some technology available that implements the complex connectors, according to its expected qualities, it is not necessary to document their decomposition.	t	1529	2
7041	Pipe-and-Filter.	t	1517	1
7042	It cannot be applied when the system includes legacy systems.	f	1592	1
7043	Its enterprise service bus cannot support asynchronous communication between the components.	f	1592	2
7044	The typical communication pattern is point-to-point.	f	1592	3
7045	The modules inside a layer cannot use other modules in the same layer	f	1935	0
7046	A layer cannot call the layer above	f	1935	1
7047	It is possible to have a circular allowed-to-use relationship between several layers	f	1935	3
7048	It should always consider the physical detail level	f	1948	1
7049	The logical detail level should only be used when the target of implementation is a relational database	f	1948	2
7050	Only the conceptual level is required, the other two levels of detail are optional	f	1948	3
7051	Applies layers to tiers.	f	1537	0
7052	Is an extension of the Client-Server architectural style.	f	1537	2
7053	Defines tiers as components.	f	1537	3
7054	It imposes restrictions on which uses relationships may exist between the system's modules	f	1941	0
7055	It makes it easier to create generalization relationships between the system's modules	f	1941	1
7056	It allows the decomposition of each of the system's modules into finer grained modules	f	1941	3
7057	All the peers are equal.	f	1583	0
7058	Any peer can access any other peer.	f	1583	1
7059	Peers are only used to share files.	f	1583	2
7060	It is not necessary to have any view of the Data Model architectural style because Facebook information has a very simple structure.	f	1472	0
7061	It is enough to design a view of the Data Model architectural style at the conceptual level because Facebook information has a very simple structure.	f	1472	1
7062	It is enough to design a view of the Data Model architectural style at the logical level because the information will be stored in a relational database.	f	1472	2
7063	A component type is made of a single architectural style.	f	1513	1
7064	Only components can be associated with application-specific types.	f	1513	2
7065	A component-and-connector view can only use a single architectural style.	f	1513	3
7066	One view of the component-and-connector viewtype and another of the deployment style.	f	1411	0
7067	Two views of the communicating processes style.	f	1411	2
7068	A view of the aspects style.	f	1411	3
7069	The aggregates publishes the event in a message broker and subscribes to the published event.	f	1917	0
7070	Using the database of the aggregate as a temporary message queue.	f	1917	1
7071	Using event sourcing.	f	1917	2
7072	A deployment view.	f	1520	0
7073	A work assignment view.	f	1520	1
7074	A install view.	f	1520	3
7075	The layered view to support a new specific layer for the customization of the catalog.	f	1914	0
7076	The layered view to accommodate a new layer for each kind of catalog, which other layers may use.	f	1914	1
7077	The data model view in order to define entities for each kind of catalog.	f	1914	2
7078	Communicating Processes.	f	1939	1
7079	Repository.	f	1939	2
7080	Pipes-and-Filters.	f	1939	3
7081	Client-server.	f	1945	0
7082	Repository.	f	1945	2
7083	Pipes-and-Filters.	f	1945	3
7084	The communicating processes style.	f	1571	0
7085	The communicating processes style and the pipes-and-filters style.	f	1571	2
7086	The dynamic reconfiguration style.	f	1571	3
7087	The communicating processes.	f	1937	0
7088	Pipes-and-filters.	f	1937	1
7089	Dynamic reconfiguration.	f	1937	3
7090	Components are allocated to persons and teams.	f	1514	0
7091	Components and modules are allocated to persons and teams.	f	1514	2
7092	None of the above.	f	1514	3
7093	The uses view to represent how the mobile device uses the Catalog application.	f	1357	1
7094	The layered view to include a layer for each type of device.	f	1357	2
7095	The domain layer of the layered style to represent the types of devices.	f	1357	3
7096	Should be captured in scenarios, as the requirements for quality attributes, and be taken into account in the design of the software architecture	t	1522	1
7097	The *decomposition* and *uses* styles, which allow us to show how dependent a certain module is of other parts of the system	t	1545	0
7098	To replace the machine used to run the server component by a more powerful machine that meets the new performance requirements, keeping only a server component running	t	1631	3
7099	Even though each viewtype addresses different aspects of a system, there are relationships among all of them	t	1616	3
7100	A subset of the requirements that correspond to the most important business goals, regardless of whether they have conflicts among them or not	t	1715	2
7101	The *Peer-to-Peer* style	t	1437	0
7102	The *Shared data* style	t	1476	3
7103	This change manifests itself on the relationship between the system's modules and components	t	1408	3
7104	The *Communicating Processes* style	t	1501	0
7105	*Decomposition* and *Implementation* views	t	1362	2
7106	*Decomposition* and *Uses* views	t	1490	1
7107	The *Layers* style	t	1547	2
7108	the *Deployment* and *Layers* styles	t	1354	3
7109	Views of the Module viewtype	t	1400	1
7110	To control and to reduce the interface exposed by the domain logic layer, thereby increasing the modifiability of that layer	t	1646	0
7111	To keep a record of changes made to the data during a business transaction and to coordinate the writing of these changes to the database	t	1647	3
7112	To prevent data inconsistencies when there are multiple accesses within the same business operation to the same entity	t	1484	2
7113	Stakeholders do not mind if two simultaneous reads on the same file by two different applications may return different values	t	1814	2
7114	The availability quality is more important, thus performance is addressed afterwards and depends on the tactics used for availability	t	1448	1
7115	Passive replication and spare	t	1899	2
7116	State Resynchronization	t	1772	1
7117	Authenticate users and authorize users	t	1964	0
7118	That provides a set of complete and cohesive services	t	1752	3
7119	But it needs to be complemented, for each uses relationship, with the level of coupling	t	1650	2
7120	The call's results may not have impact on the correct execution of the caller module	t	1826	0
7121	When it is not possible to satisfy all of the requirements optimally, we should be aware of their relative importance so that we may find a solution that corresponds to a satisfactory trade-off	t	1737	3
7122	The Peer-to-Peer style	t	1745	2
7123	A Client-Server architecture, where the DataNode is the Client and the NameNode is the Server	t	1760	0
7124	The availability guarantee may be given by the usage of an adequate connector between the HDFS Client and the DataNodes	t	1739	3
7125	The Communicating Processes style	t	1727	1
7126	To control and to reduce the interface exposed by the domain logic layer, thereby increasing the modifiability of that layer	t	1736	0
7127	That is the recommended solution if there is a control flow that involves the choreography of both components	t	1481	2
7128	Essential to reduce costs whenever there is a fault in a hardware element	t	1645	1
7129	Allows the creation of checkpoints using the information that it gradually receives from the *NameNode*	t	1463	2
7130	Performance and availability qualities	t	1453	0
7131	Increases the system modifiability whenever it is necessary to change the placement policy	t	1796	0
7132	But it would imply an *overhead* in the *NameNode*	t	1959	1
7133	This script is a module that implements a modifiability tactic	t	1643	2
7134	Availability and security	t	1972	3
7135	Means that it may be difficult to design incremental testing	t	1602	0
7136	Is that the *Allowed to Use* relation defines a restriction for the possible *Uses* relations between modules belonging to different layers	t	1958	0
7137	Using a view of the component-and-connector viewtype	t	1352	2
7138	The application continues to have a three-tiered architecture, where one of the tiers is now the HDFS system	t	1651	2
7139	The Client Server style	t	1648	3
7140	May affect the data access layer because each pattern puts different requirements on the interface of that layer	t	1502	1
7141	All components may execute in all machines	t	1580	3
7142	We may need views of the component-and-connector viewtype and of the Deployment style	t	1597	2
7143	The researchers, because they wanted to use the system to validate their research	t	1624	3
7144	As modules of the system	t	1644	0
7145	Satisfied usability requirements of the system	t	1533	3
7146	The portability, because the RTS creates an abstraction layer that hides some of the details of the operating system	t	1653	2
7147	To facilitate changing the phases used in the compilation process, thereby making the compiler more modifiable	t	1654	2
7148	It allows the development of systems with Peer-to-Peer, Client-Server, or Publish-Subscribe architectures	t	1655	3
7149	The Deployment style	t	1656	2
7150	Facilitates the addition of new messaging patterns	t	1657	1
7151	To increase the throughput of the system when it is overloaded	t	1658	1
7152	The Communicating Processes style	t	1659	2
7153	To allow more simultaneous connections than Apache	t	1329	3
7154	They wanted to have a more efficient use of the computational resources	t	1667	1
7155	To make the system faster	t	1670	2
7156	The Communicating Processes style	t	1671	1
7157	Increases both the availability and the capacity	t	1664	3
7158	In the Component-and-Connector view, because components and connectors need to be changed	t	1548	2
7159	Increasing the fault tolerance of the system	t	1678	3
7160	The execution of the previously existing layers is split between the two new tiers, and new intermediate layers may be needed	t	1406	3
7161	The domain logic layer was implemented with the Transaction Script pattern	t	1324	1
7162	The Uses style	t	1578	2
7163	The Decomposition style	t	1348	3
7164	In the Deployment view	t	1625	0
7165	To allow testing and validating the software architecture in the early development stages	t	1679	2
7166	Typically gives rise to more modules than what we would have if not using this style	t	1581	2
7167	To launch a worker thread for each core, to maximize the core usage and to minimize the need for synchronization among threads	t	1680	2
7168	Module and component-and-connector views	t	1681	3
7169	It has less performance, because the *broker* introduces greater latency in the communication	t	1682	2
7170	Views of the Generalization style	t	1690	0
7171	Usability	t	1691	0
7172	The Communicating Processes style	t	1438	3
7173	The Shared data style	t	1693	2
7174	Each *worker* is responsible for various connections, processing all requests from those connections	t	1553	2
7175	By interleaving the various processing phases of each request in a sequential process	t	1554	0
7176	Be able to reduce the amount of memory needed for each connection	t	1440	3
7177	The Peer-to-Peer style	t	1710	2
7178	The performance decreases	t	1698	0
7179	To move from an anemic domain model to a rich domain model	t	1447	2
7180	Views of the Component-and-Connector and Allocation viewtypes	t	1443	3
7181	The Client-Server style	t	1566	1
7182	None of the other options solves the problem	t	1573	3
7183	The presentation logic layer and how it relates with the underlying layer changed	t	1569	0
7184	A grouping of components	t	1386	1
7185	Maintain Multiple Copies of Computation	t	1356	0
7186	Performance, availability, and usability	t	1733	0
7187	Client-Server e Repository	t	1461	0
7188	By using a Timestamp tactic	t	1462	0
7189	May stop accepting writes	t	1464	1
7190	Increase Resource Efficiency	t	1740	0
7191	Limit Access	t	1403	2
7192	Introduce Concurrency	t	1346	0
7193	Dynamic Creation and Destruction	t	1577	2
7194	It does not depend on a proprietary service	t	1776	3
7195	It can change during the execution of each instance of Chrome	t	1956	3
7196	Maintain User Model tactic	t	1365	0
7197	Limitations of the concurrent access to files	t	1741	3
7198	Increase the modifiability quality, because the new user interface was implemented using the REST interface	t	1732	0
7199	An object tree to simplify the processing of each filter	t	1742	3
7200	Does not guarantee the FIFO delivery of messages, some messages may be delivery by a different order	t	1743	2
7201	Dynamic Creation and Destruction	t	1465	2
7202	All the previous options	t	1712	3
7203	Differ on the emphasis on production and development phases of the software process	t	1606	1
7204	Modifiability and Performance	t	1726	3
7205	Defer Binding	t	1435	0
7206	Both, pessimistic and optimistic, concurrency control policies can be used	t	1751	2
7207	Detect and Recover from the attack	t	1731	0
7208	Only depend on the type of events	t	1538	1
7209	They are not implemented by a usability tactic	t	1669	3
7210	Reliability	t	1766	2
7211	Prioritize performance and availability over functionality	t	1684	2
7212	Publish-subscribe.	t	1663	0
7213	Passive Redundancy and Maintain Multiple Copies of Computation	t	1749	3
7214	By the load balancer	t	1723	1
7215	Degradation	t	1746	2
7216	Increase Resources Efficiency	t	1753	2
7217	Security, Performance, Usability and Mobility	t	1777	3
7218	The *browser* needs to make less requests to the server	t	1773	1
7219	Communicating-Processes	t	1762	1
7220	Uses the Introduce Concurrency tactic	t	1790	0
7221	External applications can administrate the GNU Mailman mailing lists	t	1755	1
7222	Pipes-and-Filters	t	1756	2
7223	Data Model	t	1757	0
7224	The quality of Performance	t	1758	0
7225	Security	t	1822	0
7226	Module and Component-and-Connector	t	1824	2
7227	Modifiability and Interoperability	t	1825	0
7228	Aspects	t	1334	1
7229	User Model	t	1326	1
7230	It is necessary that each object has a unique identifier	t	1833	3
7231	Exception Detection	t	1965	3
7232	In what concerns the notification, the Model module does not use the Observer module	t	1987	3
7233	There isn't any predefined order to design Uses and Layered views	t	1503	2
7234	May contain several architectural styles, but only if they are of the same viewtype	t	1701	2
7235	Once applied in a view may be necessary to change the Decomposition view	t	1831	2
7236	It is an advantage for programmers that the transactional behavior is transparently provided	t	1325	0
7237	The Domain Model pattern to reduce the interface of the Domain Logic layer to a controlled set.	t	1834	1
7238	The Domain Model pattern.	t	1835	2
7239	The Requirements function is part of the Design module.	t	1829	0
7240	A Condition Monitoring tactic for the Availability quality.	t	1802	0
7241	The stimulus for scenarios of the Security quality.	t	1764	2
7242	The environment is design time.	t	1836	2
7243	Increase resource efficiency.	t	1837	3
7244	Create a decomposition where there is a module corresponding to the Windows OS and another one for the Mac OS X, each one responsible for containing the OS-specific code.	t	1317	0
7245	We have to use a Repository component-and-connector style.	t	1340	0
7246	Client-server in the first case and Peer-to-peer in the second.	t	1445	1
7247	Is driven by a trade-off among the stakeholders needs.	t	1721	3
7248	May be responsible for the Performitis problems of architectures.	t	1761	1
7249	The location information is correctly included with a probability of 99.99% is the response measure.	t	1841	3
7250	A data model view and a component-and-connector view using a shared-data style.	t	1499	3
7251	Shared-data style.	t	1530	2
7252	The domain only needs CRUD (Create, Read, Update, and Delete) operations.	t	1789	1
7253	It is necessary to design two deployment views, one for each deployment option.	t	1806	0
7254	Increase resource efficiency tactic for performance, because it reduces the need of upfront calculus/computation on new clients.	t	1817	2
7255	The server propagates local commands and cursor movements to the clients, and keeps the snapshots for the initialization of new clients.	t	1818	1
7256	Testability and Modifiability.	t	1819	2
7257	All of the above	t	1827	3
7258	The Active Record pattern	t	1851	1
7259	Ping-and-echo requires the availability monitor to know the addresses of the components it is monitoring	t	1779	0
7336	In the view there are multiple instances of the `Queue` component.	t	1632	0
7260	It is necessary to use the encrypt data tactic to encrypt the information on the client web browser, before it is send to the web server	t	1842	3
7261	A scenario for usability associated with a support user initiative tactic	t	1843	3
7262	Multiple copies of computation	t	1844	0
7263	Decomposition view	t	1687	0
7264	To talk with the people that developed the system to know what they did and why they did it	t	1423	3
7265	Architecture is the design that gets harder to change as development progresses	t	1455	2
7266	None of the above	t	1845	3
7267	Represent different architectural qualities and they may not be all represented in a single view	t	1846	0
7268	When it is not possible to satisfy all of the requirements optimally, we should be aware of their relative importance so that we may find a solution that corresponds to a satisfactory trade-off	t	1791	3
7269	Both, a module and a component	t	1500	2
7270	Generalisation to represent an abstraction common to all interfaces and keep API-specific details in child modules	t	1452	2
7271	Development because it is not possible to do incremental development	t	1750	2
7272	Performance, because it describes what is the response to REST API calls	t	1847	0
7273	Increase resource efficiency tactic	t	1848	1
7274	Testability, because of the logic complexity	t	1850	2
7275	A communication processes style	t	1853	3
7276	It is necessary to change the connector between the web clients and the web servers, in the component-and-connector view, to show the semantics that is provided by the load-balancer	t	1454	1
7277	They have to process very large amounts of data in each request	t	1450	1
7278	A generalisation architectural style	t	1854	0
7279	Subscribes to cursor position events	t	1855	2
7280	All of the above	t	1821	3
7281	This shared understanding is what distinguishes architecture from design.	t	1525	0
7282	Frank Buschmann is referring to some possible consequences of the modifiability quality.	t	1634	0
7283	A solution for any quality in isolation may lead to a biased architecture.	t	1700	3
7284	Is focused on creating common generalizations of several systems.	t	1665	2
7285	To break such misunderstanding and mistrust the architecture has to make explicit the stakeholders needs.	t	1511	3
7286	Is a baseline architecture that allows to experiment with the most significant architectural requirements.	t	1512	2
7287	Are unable to distinguish architecture from design.	t	1563	2
7288	A solution to this problem is to prioritize the system qualities.	t	1509	1
7289	Professional and Technical Contexts.	t	1510	3
7290	Availability and Performance.	t	1604	1
7291	Tries to guarantee that the final system will have the qualities aimed by the architecture.	t	1342	1
7292	Availability.	t	1421	1
7293	Can be used as the source of a stimulus in a scenario.	t	1315	2
7294	Is a design tactic for a scenario where the source of stimulus are technical users.	t	1399	2
7295	Limit exposure.	t	1704	2
7296	The stimulus and the response should be always present.	t	1321	3
7297	The quality addressed is availability.	t	1322	0
7298	Verify message integrity.	t	1705	2
7299	Introduce concurrency.	t	1754	1
7300	Interoperability.	t	1372	2
7301	Exception prevention.	t	1769	3
7302	A Maintain Multiple Copies of Computation design tactic in Carbon.	t	1744	0
7303	Separate entities, to allow the use of more strict tactics on the sensitive data.	t	1706	2
7304	Maintain user model tactic.	t	1595	0
7305	May be associated to other tactics to deal with a single stimulus.	t	1360	1
7306	This situation corresponds to the use of the degradation availability tactic.	t	1707	0
7307	The quality addressed is modifiability.	t	1962	1
7308	Detect and Recover.	t	1708	2
7309	Usability.	t	1709	3
7310	Interoperability.	t	1456	0
7311	Business scenario.	t	1425	0
7312	Modifiability.	t	1436	1
7313	Support system initiative.	t	1714	3
7314	Allows to identify modules for which the development team does not have the required implementation competences.	t	1960	3
7315	Contains the business value and the architectural impact of architecturally significant requirements.	t	1339	3
7316	The architect have to decide on the cost/benefit of designing an architecture that supports this ASR.	t	1811	2
7317	Availability.	t	1716	2
7318	Availability.	t	1717	2
7319	Business scenario.	t	1730	0
7320	Applying the decomposition style to some of the modules in the loop chain.	t	1722	1
7321	Limit exposure.	t	1719	0
7322	The attachment between components and connectors only depends on their ports and roles types.	t	1579	3
7323	It is necessary to design a view of the Data Model architectural style at the physical level to deal with performance issues of the access to data.	t	1358	3
7324	Increase resource efficiency.	t	1720	2
7325	The required quality associated with the connector is supported by existing and well-know technology.	t	1515	3
7326	She can define a variant of this style with asynchronous communication by allowing the client to register callbacks that the server calls at specific times.	t	1543	0
7327	Allows incremental development because the possible increments of functionally can be inferred from use dependencies.	t	1713	2
7328	Relates a view of the Uses style with a view of the Data Model style.	t	1552	0
7329	Multiple copies of computation and Passive redundancy tactics.	t	1724	0
7330	A module interface cannot be replicated but component ports can.	t	1734	2
7331	It is possible to have redundant servers.	t	1524	1
7332	Condition monitoring.	t	1460	3
7333	It is possible to integrate a new data accessor without changing the other data accessors.	t	1527	0
7334	This means that the modules inside a layer are likely to be ported to a new application together.	t	1968	3
7335	Can use a Service Registry to improve transparency of location of service providers.	t	1539	2
7337	It encapsulates applications through well-defined interfaces, decouples the coordination of the interaction among applications from the applications themselves, and improves transparency of location of service providers.	t	1506	3
7338	There is a interface delegation relation between the `read` port of `Queue` and the `query` port of `Carbon`.	t	1729	1
7339	Implementation style.	t	1536	1
7340	Restrict the communication between components because, for instance, a group of components should be located in the same hardware.	t	1535	1
7341	Install style.	t	1528	2
7342	Memcached can be considered a sub-module of the Store Graphs module.	t	1333	0
7343	Buffering can be considered a sub-module of the Store Graphs module.	t	1735	0
7344	A deployment view.	t	1635	1
7345	An interface delegation is missing in the picture to represent the : TableEditor broadcasting the cursor position through the Pub port.	t	1565	1
7346	May not even exist, only record sets are used.	t	1622	3
7347	Amazon Silk predicts accesses based on the information gathered for all Silk users.	t	1587	0
7348	There is a ThousandParsec connector which can be decomposed into a set of components and Request/Reply connectors.	t	1590	2
7349	Was taken because HTML5 provides better portability qualities.	t	1627	0
7350	The server implements the : Repository component and the : Broadcast connector.	t	1636	0
7351	Table Data Gateway and Row Data Gateway.	t	1640	0
7352	Google Chrome predictor takes into consideration the amount of available cache.	t	1652	1
7353	As a module but not included in the RulesSet subtree.	t	1637	2
7354	Two distinct unidirectional connectors.	t	1638	0
7355	The Parser module is part of the code executed by the : Sheet component.	t	1639	1
7356	Active Record and Data Mapper.	t	1344	3
7357	The stimulus is to integrate reports from a variety of test tools	t	1614	0
7358	Performance and Reliability	t	1615	2
7359	In the mapping between layers of the system and the components where they execute	t	1349	3
7360	The system represented in the right part of the figure tends to have good modifiability	t	1318	3
7361	Tiers to express that different applications define their own contexts	t	1775	2
7362	When it is not possible to satisfy all of the requirements optimally, we should be aware of their relative importance so that we may find a solution that corresponds to a satisfactory trade-off	t	1359	3
7363	Retry	t	1415	0
7364	Decomposition and Generalization.	t	1662	2
7365	It is useful for system administrators.	t	1518	3
7366	Identify what should be the common and specific parts of the module responsible for the interaction with the external sources, before interacting again with the stakeholders	t	1623	2
7367	Increasing performance, scalability and availability	t	1668	3
7368	Nginx emphasizes the usability quality for system administrators by reducing the number or errors	t	1626	3
7369	You may need to use views of the three viewtypes	t	1564	2
7370	The Communicating Processes style	t	1521	3
7371	The Pipes-and-filters style	t	1466	1
7372	Launching a new process for processing each request is too expensive	t	1469	0
7373	A multiple copies of computation tactic	t	1482	2
7374	A communicating-processes view with non-blocking connectors for the interaction between threads and core data structures	t	1483	2
7375	The solution where the application is responsible for the eviction has better availability	t	1674	0
7376	Reliability	t	1953	2
7377	This law can be seen as an example of the architecture influence cycle	t	1767	1
7378	Becomes unavailable for banks if there is a fault in the hardware of service server (srv-opc)	t	1594	3
7379	We have to distinguish architectural performance from opportunistic performance	t	1683	2
7380	They are both components	t	1608	1
7381	By interleaving the various processing phases of each request in a sequential process	t	1630	0
7382	You need to change the decomposition view to represent modules with the responsibilities associated with the DSL	t	1688	0
7383	A decomposition view which contains the serialization/de-serilization modules	t	1768	3
7384	The main quality of the system in the right part of the figure is scalability	t	1971	0
7385	Decomposition to express the services interfaces	t	1361	1
7386	Performance because it allows the processing of more requests per unit of time	t	1677	1
7387	Is useful even if the implementation is concluded and the system has entered the maintenance phase	t	1774	3
7388	The most important requirements (both functional and qualities) that the system must achieve	t	1771	0
7389	Communicating processes, shared-data, service-oriented architecture, and peer-to-peer	t	1613	3
7390	A component may execute code from different modules	t	1429	1
7391	They guide us in the requirement elicitation process with the system's stakeholders	t	1473	3
7392	It is not possible to develop and to test the system incrementally	t	1955	2
7394	The Communicating Processes style	t	1420	3
7395	Voting	t	1343	2
7396	Put the requests into a queue and schedule their processing	t	1366	2
7397	A Decomposition view	t	1479	3
7398	Increasing performance and decreasing availability	t	1338	2
7399	Understand the architecturally significant requirements	t	1961	1
7400	Interoperability	t	1781	3
7401	Modifiability	t	1858	1
7402	Enumerates, for each kind of quality attribute, all the possible types of source of stimulus, stimulus, etc.	t	1480	1
7403	Is useful to support scenarios where the stimulus is an omission.	t	1815	1
7404	The level of abstraction of the system an architect works may vary.	t	1485	3
7405	Martin Fowler complains about this definition because the early decisions are not necessarily the right ones.	t	1561	1
7406	This view highlights the different performance levels for `upload` and `dowload` operations.	t	1478	2
7407	This is a case of an architectural influence cycle where the feedback cycle resulted in changes on the business and project.	t	1763	2
7408	This view highlights the scalability of `upload` and `dowload` operations, and of storage.	t	1401	3
7409	This shared understanding includes the architecturally significant requirements.	t	1782	1
7410	The exchange of information is the stimulus.	t	1353	0
7411	This view highlights the performance of the `download` operations.	t	1685	1
7412	Facilitate the communication among the stakeholders such that they can decide on what are the architecturally significant requirements.	t	1793	2
7413	To reason about a system is to verify whether the architecturally significant requirements are considered by the architecture.	t	1560	1
7414	Active redundancy can be used together with a voting tactic to detect and recover from faults.	t	1812	0
7415	It describes a usability scenario where the stimulus is reduce the number of errors when configuring the system.	t	1860	2
7416	In each iteration one or more architecturally significant requirements are used to decompose a software element of the system design.	t	1431	0
7417	A support user initiative tactic based on the definition of a language is used to achieve this scenario.	t	1966	3
7418	Cannot be used together with the Reduce Overhead performance tactic.	t	1871	3
7419	Schedule resources.	t	1862	2
7420	This is a modifiability scenario which has a defer binding tactic.	t	1378	1
7421	Bound execution times, bound queue sizes, and increase resources.	t	1988	0
7422	The utility tree covers all the significant qualities the system has to address.	t	1416	3
7423	Maintain multiple copies of computation.	t	1434	3
7424	The tactic used to solve the problem is based in the fact that data points are appended to the end of the metric file.	t	1976	2
7425	A modifiability scenario where defer binding occurs at compile time.	t	1863	2
7426	The server approach can scale independently of the number of applications.	t	1748	1
7427	A connector embodies a communication protocol.	t	1489	2
7428	Schedule resources.	t	1864	0
7429	Scalability.	t	1861	3
7430	The Merge component executes the modules merge and stdio.	t	1785	1
7431	Performance.	t	1759	0
7432	Scalability of read requests, because it is easy add more repositories to where reads are distributed, though there may be some level of inconsistency.	t	1471	1
7433	Interoperability.	t	1820	0
7434	A decomposition view, a view of the component-and-connector viewtype and a deployment view.	t	1617	3
7435	Client-server.	t	1660	0
7436	Client-server where the Buildbot is the client.	t	1661	2
7437	Tiers to represent scalability.	t	1542	1
7438	The sharing of data is done using a service-oriented architecture.	t	1474	1
7439	Client-server where the Dashboard is the server.	t	1787	3
7440	Communicating processes.	t	1865	1
7441	Implementation views	t	1458	3
7442	Communicating Processes	t	1351	0
7443	Typically have a software architecture that results from the common knowledge about the system that is shared among the team members	t	1970	0
7444	The functional requirements have a large impact on the definition of views of the module viewtype because they are used to define the high cohesion and low coupling of modules	t	1491	2
7445	The response is not correctly stated	t	1954	2
7446	The stimulus is an omission and the tactic is retry	t	1376	0
7447	The quality being addressed is performance and the tactics multiple copies of data and multiple copies of computation	t	1869	2
7448	Tiers style	t	1870	0
7449	The sharing of data is done using a service-oriented architecture	t	1575	1
7450	an ACID transaction occurs in the invoked application and ACID transactions in the other involved applications will eventually occur later	t	1371	3
7451	To support high scalability the request of `User 1` needs to be decomposed into a request to only one of the aggregate instances and the processing in the other aggregate occurs in the background	t	1410	3
7452	We must use various different views, both of the component-and-connector and the allocation viewtypes	t	1394	0
7453	Passive replication and spare	t	1942	2
7454	All of the above	t	1377	3
7455	The stimulus is to port the system to a new browser	t	1497	0
7456	Increase semantic coherence	t	1786	1
7457	Essential to reduce costs whenever there is a fault in a hardware element	t	1936	1
7458	Because this tactic simplifies the addition and removal of DataNodes	t	1866	0
7459	Is composed of things such as code units, runtime elements, hardware, and people, together with the relationships among them	t	1418	1
7460	Describing what are the qualities that the system should possess	t	1523	0
7461	The scenario is correct but it does not describe whether the request the servers fails to respond to succeeds or fails	t	1981	1
7462	Both in a component-and-connector and the Deployment views	t	1388	3
7463	Bound execution times	t	1534	3
7464	The web browser, o web server, and the data base	t	1493	2
7465	It decouples applications developed for different organizations	t	1877	3
7466	Install view	t	1880	1
7467	Write two scenarios on performance	t	1336	1
7468	Component-and-connector viewtype	t	1857	1
7469	A module view	t	1859	0
7470	The Decomposition and the Work Assignment styles	t	1385	0
7471	The Uses style	t	1430	2
7472	The Generalization style	t	1876	0
7473	Use a passive redundancy tactic in the OPC (Order Processing Center)	t	1875	0
7474	The decomposition view to include a module for the synchronization responsibilities	t	1882	0
7475	The price for high scalability and availability is the need to have several replicas of the files to be shared	t	1504	3
7476	Only a small set of functionalities are transactional	t	1792	1
7477	The distributed log guarantees that events will be delivered only once	t	1881	2
7478	It allows high scalability because it is possible the implement transactions associated to each one of the aggregates	t	1873	1
7479	Implementation	t	1459	0
7553	Results from the application of several ADD iterations.	t	1795	3
7480	When a read occurs, the client, if it is located in the cluster, receives a list of the DataNodes where the replicas are, ordered by its closeness to the client, to improve performance of reads	t	1973	3
7481	None of the above	t	1487	3
7482	Enumerates, for each kind of quality attribute, all the possible types of source of stimulus, stimulus, response, etc	t	1884	1
7483	The tweet unique ID is written in the home timeline of each one of the writer's followers	t	1885	2
7484	This solution optimizes the performance in terms of the throughput of processed requests	t	1893	3
7485	The periodic rebuild of the checkpoint improves the performance of the NameNode during its initialization	t	1813	3
7486	How to control the response to one or more stimulus	t	1894	1
7487	The ingestion process includes tokenizing of the tweet to include in an index	t	1886	1
7488	This is right but you cannot be completely sure whether the decisions are the right ones	t	1546	2
7489	This solution allows the dimensioning of the number of activities (threads or processes) that run in the server, taking into consideration the server's hardware capacity, in order to have a efficient usage of the server's CPU	t	1323	3
7490	Use an intermediary that contains all the code associated with the remote invocation separating it from the modules' business logic	t	1345	1
7491	The system would respond faster to requests about file locations	t	1584	2
7492	The Voting tactic	t	1486	2
7493	She should try to use a view of the Aspects style, assign this responsibility to a single module and define where it crosscuts the other modules	t	1803	2
7494	A new aspect view that includes a module with the responsibilities associated with the access control and that crosscuts some of the other modules	t	1895	3
7495	It is necessary to design a view of the Data Model architectural style at the physical level to deal with performance and consistency issues of the access to data	t	1977	3
7496	Service-oriented architecture, Shared-data, Peer-to-peer, and Client-server	t	1600	3
7497	May, on another view of the system, be represented by a set of components and connectors	t	1405	1
7498	Tiers, Shared-data, Service-oriented architecture, and Client-server	t	1507	3
7499	A component can subscribe to events	t	1363	0
7500	Client-Server and Shared-data	t	1890	3
7501	A Communicating processes view	t	1896	2
7502	If there is some technology available that implements the complex connectors it is not necessary to document their decomposition.	t	1591	2
7504	This solution optimizes the performance in terms of the throughput of processed requests.	t	1383	3
7505	Integrate the development of the software system with the organization's business goals.	t	1673	2
7506	A fault.	t	1816	2
7507	Throughput.	t	1809	3
7508	Restrict dependencies.	t	1451	2
7509	Uses style.	t	1983	1
7510	The Generalization and Decomposition styles.	t	1986	3
7511	An aggregate is usually loaded in its entirety from the database.	t	1794	1
7512	Testing is easier	t	1393	3
7513	Maintain system model.	t	1888	3
7514	This view shows that if is possible to scale differently the `web tier` from the `EJB tier`.	t	1889	0
7515	Maintain multiple copies of computation.	t	1891	2
7516	Retry tactic	t	1892	3
7517	The project manager uses this view to get advice on the incremental development of the system.	t	1738	1
7518	Analysing the performance of the system.	t	1526	0
5292	The *Client-Server* style	f	1547	3
7519	All layers are mapped to the browser component where the data access layer will contains, besides a module to access a local repository, modules to access external services.	t	1823	3
7520	All the above.	t	1887	3
7521	Increase competence set.	t	1316	0
7522	Reduce overhead.	t	1422	1
7523	It is necessary to use views of the three viewtypes.	t	1373	3
7524	Usability.	t	1770	1
7525	It is useful for the project managers.	t	1541	1
7526	Performance and Availability for crashes of the Image File Storage component.	t	1849	2
7527	Four.	t	1467	3
7528	That the impact of integrating a new source is controlled by the interface of *Import DVD Info* Module.	t	1867	2
7529	The Service Oriented Architecture style.	t	1414	2
7530	This view shows that the processing of tracking requests is done synchronously.	t	1593	1
7531	Pipe-and-filter and shared-data.	t	1878	3
7532	Do not loose the changes done on the client component if the server is not available.	t	1879	2
7533	Split module.	t	1910	0
7534	The connectors only use the *stdio* module for their implementation.	t	1872	2
7535	A common understanding to be achieve among all the system stakeholders	t	1897	2
7536	The measure of the response is throughput	t	1368	3
7537	Project	t	1596	2
7538	Scalability of the Image File Storage in terms of the storage capacity	t	1350	1
7611	Detect a fault.	t	1666	3
7612	Limit event response	t	1384	1
7539	Simple programming model, the clients only need to concern about the business logic of the application, the remote services are transparent	t	1409	3
7540	The asynchronous solution can support a larger number of simultaneous requests	t	1901	1
7541	None of the above	t	1900	3
7542	Modifiability	t	1390	2
7543	The separation of write and retrieval services allows them do scale independently	t	1407	3
7544	Limit event response	t	1797	1
7545	In the asynchronous solution it is possible to provide an implement where the tasks being executed are finished without requiring the client to resubmitted them	t	1439	2
7546	Provides the quality of availability	t	1672	0
7547	The need to have eventual consistency and compensating operations	t	1433	2
7548	Maintain multiple copies of computation	t	1828	2
7549	Time to market is the most important impact of cloud computing in an architecture	t	1903	0
7550	There should be at least one view of the system using this architectural style.	t	1327	3
7551	performance tactics.	t	1628	2
7552	May conflict with the Reduce Overhead performance tactic.	t	1442	3
7555	This is a modifiability scenario which has a defer binding tactic.	t	1381	1
7556	None of the above.	t	1555	3
7557	The quality addressed is availability and transactions tactic is required to solve the problem.	t	1387	0
7558	A low cost of change may imply a high cost of development.	t	1396	0
7559	Service-oriented architecture, Shared-data, Client-server and Peer-to-peer.	t	1508	3
7560	The Decomposition style.	t	1982	2
7561	Which improves modifiability, because filters are decoupled through pipes.	t	1913	1
7562	The view addresses the scenario because it separates the modules that represent the interfaces a new business partner has to implement.	t	1505	2
7563	If the OPC crashes the Consumer Website can continue to provide service in degraded mode.	t	1904	0
7564	A component can subscribe to events.	t	1498	0
7565	The Uses style.	t	1725	2
7566	The attachment between components and connectors only depends on their ports and roles types.	t	1911	3
7567	Publish-subscribe.	t	1540	1
7568	Layer 1.	t	1912	0
7569	The view does not address the scenario	t	1598	0
7570	The price for high scalability and availability is the need to have several replicas of the files to be shared.	t	1582	3
7571	Availability and Performance.	t	1532	1
7572	A performance scenario associated with the throughput of writing data points to disk.	t	1906	0
7573	Allows the querying of a past state.	t	1907	2
7574	The decomposition was driven by a split module tactic.	t	1902	2
7575	HTML5 provides better portability qualities.	t	1905	0
7576	An availability scenario of the *Graphite* system.	t	1335	3
7577	A request for a web page corresponds to requesting a service from the amazon cloud.	t	1832	2
7578	She should try to split the system in parts in order to isolate the complex business logic and use the two architectural approaches accordingly.	t	1364	2
7579	The view results from the implementation of a support system initiative tactic.	t	1909	3
7580	It helps on the configuration of systems.	t	1496	3
7581	An iterative development was followed, which allowed more time to develop a connector with good performance in the latter stages of the project.	t	1562	1
7582	A deployment and a work assignment view.	t	1599	2
7583	This view fulfills a modifiability scenario, which states about the cost of adding a new source of information to the system.	t	1449	2
7584	At least two performance scenarios of the *Graphite* system.	t	1367	3
7585	Reads may not be consistent with the most recent write.	t	1916	2
7586	Modifiability.	t	1728	0
7587	A view of the Decomposition style	t	1985	2
7588	By changing a parent, which will automatically change all the children that inherit from it.	t	1488	3
7589	With a component-and-connector view, where the *load balancer* is a component of the system	t	1417	0
7590	It supports the concurrent access of data accessors.	t	1927	1
7591	The solution where the application is responsible for retrieving the missing piece of data from the underlying store has better availability	t	1404	0
5293	The *Decompostion* and *Implementation* styles	f	1354	0
7592	A new aspect view to include the responsibilities associated with the access control.	t	1919	3
7593	The Publish-subscribe style	t	1392	2
7594	Analysing the performance of the system	t	1413	0
7595	Client-server, Repository and Publish-subscribe.	t	1918	3
7596	Security	t	1928	3
7597	and it does not penalize reliability because it also provides an interface that the webapp components can use to access the most recent data	t	1379	2
7598	A layered architecture, where the access to the various sources is the responsibility of the bottommost layer	t	1428	2
7599	Schedule Resources	t	1780	0
7600	Maintain system model tactic.	t	1921	0
7601	An iterative development was followed, which allowed more time to develop a connector with good performance in the latter stages of the project.	t	1922	1
7602	This decomposition implies that, in average, customers are going to have a large number of orders.	t	1830	1
7603	Heartbeat is more scalable than ping/echo because the monitor does not need to know in advance the addresses of the components.	t	1969	2
7604	All viewtypes may be necessary	t	1457	1
7605	When the environment is initiation time it means that it is necessary to restart the system for the change to take effect	t	1975	2
7606	Can use the operations defined in any of the system's modules	t	1925	0
7607	It represents a relation between a component's port and a port of one of its internal components.	t	1926	2
7608	To facilitate the interaction among heterogeneous components that use distinct communication protocols	t	1576	0
7609	The behavior described in the sentence can be represented in a view where the dynamic reconfiguration architectural style is used	t	1915	1
7610	The presentation logic layer and how it relates with the underlying layer changed	t	1920	0
7613	Restrict dependencies	t	1898	2
7614	It would reduce the scalability for updates of different orders for the same customer.	t	1852	0
7615	Reads may not be consistent with the most recent write.	t	1924	2
7616	A module contains the code that can execute in several components and a component can execute the code of several modules	t	1923	3
7617	Manage sampling rate	t	1444	0
7618	The layered view to deal with the aspects of portability.	t	1337	0
7619	The view addresses the scenario because it separates the `Consumer Website` module from the `OpcApp` module to allow the execution of the `Consumer Website` module in a component that can have multiple copies of computation	t	1938	1
7620	Performance	t	1783	2
7621	Performance e Usability	t	1765	1
7622	A module view of the data model style.	t	1929	1
7623	Suffered from some level of featuritis, but it allowed to have a pilot from which the team learned.	t	1930	2
7624	Usability	t	1778	1
7625	Performance and security	t	1375	2
7626	Monitorability	t	1369	3
7627	Manage sampling rate	t	1947	0
7628	Maintain multiple copies of computation	t	1838	1
7629	Availability.	t	1607	2
7630	Schedule resources	t	1397	3
7631	Component and Module	t	1446	2
7632	Maintain multiple copies of data	t	1341	2
7633	Limit event response	t	1949	0
7634	Limit access.	t	1585	0
7635	Increase resource efficiency tactic.	t	1932	1
7636	The new defects introduced.	t	1974	2
7637	Is an iterative process where architectural designs are proposed as hypothesis and tested.	t	1382	3
7638	Is applied at the begin of the architectural design process but may be necessary to redo it later.	t	1943	1
7639	Voting.	t	1328	2
7640	This ASR can easily be supported by the architecture because it has little effect in the architecture.	t	1933	0
7641	The correctness of the caller module may not depend on the correct implementation of the invoked function in the called module.	t	1931	0
7642	Schedule resources tactic.	t	1957	3
7643	Maintain system model	t	1934	2
7644	The main quality this style addresses is interoperability.	t	1592	0
7645	Each layer defines a virtual machine because it provides a set of cohesive functionalities to the upper layer	t	1935	2
7646	If there are performance requirements concerning the access to data, then the level of detail should be physical	t	1948	0
7647	Restricts the communication between components because, for instance, a group of components should be located in the same hardware.	t	1537	1
7648	It separates in new modules responsibilities that were spread over various of the system's modules	t	1941	2
7649	The interaction between peers is symmetric.	t	1583	3
7650	It is necessary to design a view of the Data Model architectural style at the physical level to deal with performance and consistency issues of the access to data.	t	1472	3
7651	A component is an instance and a view can have several instances of the same component type.	t	1513	0
7652	A single view of the communicating processes style.	t	1411	1
7653	All of the above.	t	1917	3
7654	A deployment and a work assignment view.	t	1520	2
7655	The data model view in order to define generic entities that can be customized for different kinds of catalogs.	t	1914	3
7656	Client-server.	t	1939	0
7657	Communicating Processes.	t	1945	1
7658	The communicating processes style and the dynamic reconfiguration style.	t	1571	1
7659	Publish-subscribe.	t	1937	2
7660	Modules are allocated to persons and teams.	t	1514	1
7661	The decomposition view to include a module for the synchronization responsibilities.	t	1357	0
7662	Enforces a focus on the specialization of the specific competences, like testing.	f	1557	0
5927	A module.	t	1519	1
7663	Allows teams to be focused on the product.	t	1557	2
7664	Does not have any relation with the Conway's Law.	f	1557	3
7665	May impact on the application's functionality.	t	1556	0
7666	Does not require the relaxing of the ACID properties of monolith transactions.	f	1556	1
7667	The latency of the application will decrease.	f	1556	2
7668	The throughput of the application will decrease. 	f	1556	3
7669	Vertical scalability.	f	1559	0
7670	Horizontal scalability.	t	1559	1
7671	Usability.	f	1559	2
7672	Testability.	f	1559	3
7673	It is possible to independently evolve only a small part of the functionality.	t	1558	0
7674	Continuous delivery is like continuous integration.	f	1558	1
7675	Facilitate automated testing.	f	1558	2
7676	The automation pipeline cannot be applied to the monolith.	f	1558	3
7677	It is still hard to change.	f	1427	0
7678	It is less hard to change because microservices communicate through smart endpoints and dumb pipes.	f	1427	1
7679	It is less hard to change because the functionalities do not share the same database.	f	1427	2
7680	It is less hard to change because each microservice can be implemented using a different programming language.	t	1427	3
7681	Does not reduce the amount of communication between the developers.	f	1557	1
7682	The concepts do not differ.	f	1549	0
7683	Software design is related with the implementation.	f	1549	1
7684	Software architecture is related with requirements.	t	1549	2
7685	Software design is a subconcept of software architecture.	f	1549	3
7686	A requirement that refers to a quality of the system that is relevant to one or more of its stakeholders.	t	1619	0
7687	A requirement that refers to a quality of the system that is relevant to the developers.	f	1619	2
7688	A requirement that refers to a quality of the system that is relevant to the business people.	f	1619	3
7689	End user stakeholder.	f	1618	0
7690	Maintenance organization stakeholder.	f	1618	1
7691	Marketing stakeholder.	t	1618	2
7692	All the stakeholders referred in the other options. 	f	1618	3
7693	Technical Context	f	1984	0
7694	Project Context	t	1984	1
7695	Business Context	f	1984	2
7696	Professional Context	f	1984	3
7697	Present the architecture according to different perspectives, because the description of the architecture is too large to be comprised by a single representation.	f	1332	2
7698	Present the architecture according to different perspectives, where each perspective addresses the fulfilment of one and only one quality.	f	1332	3
7699	A requirement that refers to a quality of the system that is relevant to the architect.	f	1619	1
7700	Technical Context.	f	1612	0
7701	Project Context.	t	1612	1
7702	Business Context.	f	1612	2
7703	Professional Context.	f	1612	3
7704	Performance, in terms of latency.	t	1620	0
7705	Performance, in terms of throughput.	f	1620	1
7706	Horizontal scalability.	f	1620	2
7707	Vertical scalability.	f	1620	3
7708	The drivers' app contains all the information about the current ride.	t	1380	2
7709	All the information is on memory.	f	1380	0
7711	The Uber system cannot recover from such type of failures.	f	1380	3
7712	To deal with the remotion of servers without requiring a rehashing.	f	1642	0
7713	To deal with the add of new servers without requiring a rehashing.	f	1642	1
7714	To deal with the dynamic change of demand.	f	1642	2
7715	All options are correct.	t	1642	3
7716	To do sharding by city.	f	1633	0
7717	To do sharding by cells using the Google S2 library.	t	1633	1
7718	To have several server replicas where writes occur.	f	1633	2
7719	Scalability.	f	1649	0
7720	Availability.	f	1649	1
7721	Performance.	f	1649	2
7722	Variability.	t	1649	3
7723	To sequence all the writes in a single server which is optimized in terms of CPU and no synchronization overheads.	f	1633	3
7724	A process.	f	1572	0
7725	A library.	t	1572	1
7726	A component.	f	1572	2
7727	A database.	f	1572	3
7728	Present the architecture according to different perspectives, where each perspective may focus on the area of interest of one or more of the system stackeholders.	t	1332	0
7729	Present the architecture according to different perspectives, because each of the perspectives describes a solution for each one of the conflicting architectural significant requirements.	f	1332	1
7730	Addresses the aspects of the assignment of people to the implementation of the units of code.	f	1402	0
7731	Addresses the aspects of the assignment of processes to the hardware where the system is going to execute.	f	1402	1
7732	Addresses the aspects of the runtime properties of the system.	t	1402	3
7733	The views where they are applied.	f	1395	1
7734	The three SEI viewtypes.	t	1395	2
7735	The qualities they provide.	f	1395	3
7736	The effort required to implement them.	f	1395	0
7737	Confidentiality of some data	f	1516	0
7738	Cost of extending a functionality	t	1516	1
7739	Cost in production	f	1516	2
7740	Time to execute a batch of requests	f	1516	3
7741	Addresses the aspects of the implementation of the system.	f	1402	2
7742	Because modules are part of the component-and-connector viewtype.	f	1676	0
7743	Because the software architect cannot implement all modules.	f	1676	1
7744	Because the identification of the main modules, and their responsibilities, allow the definition of interfaces for the modules that can be outsourced from third party venders.	t	1676	2
7745	Because the software architecture of the system has impact on the organizational structure of third party vendors.	f	1676	3
7746	Because the architectural views are designed using a small set of architectural styles.	t	1675	0
7747	Because the architectural views are designed using a small set of viewtypes.	f	1675	1
7748	Because the architecture is the shared understanding between all stakeholders.	f	1675	2
7749	Because it is easier to reason about the architecture qualities if it uses a restricted vocabulary.	f	1675	3
7750	They are used to describe what cannot be changed in the software architecture.	f	1494	1
7751	They are used to assign responsibilities to the modules of the system.	t	1494	2
7752	They have no impact because the software architecture only addresses the non-functional aspects of the system.	f	1494	3
7753	They may have impact on the identification of the requirements for a system to be designed and implemented.	t	1492	0
7754	They always have impact on the system architecture, because any problem requires a software architecture solution.	f	1492	1
7755	The business goals of a system only have impact on the system functionalities.	f	1492	2
7756	They do not have any impact.	f	1492	3
7757	To allow that new developers can learn about the system most relevant qualities without the details of the code. 	f	1495	0
7758	Because the architecture is the shared understanding about the system which is relevant to foster the communication with new developers.	f	1495	1
7759	Because the software architectures contains the most important decisions about the system that should be followed when implementing it.	f	1495	2
7760	All options are true.	t	1495	3
7761	They are used to define the structure of the system that provides performance.	f	1494	0
7762	The cost of changing the system.	t	1544	0
7763	The cost of developing the system.	f	1544	1
7764	The cost of implementing the system.	f	1544	2
7765	All the options are true.	f	1544	3
7766	How efficiently manages its battery.	f	1320	0
7767	The number of different user interfaces to support multiple platforms.	f	1320	1
7768	Its variability, due to the deployment on multiple platforms.	f	1320	2
7769	All options are true.	t	1320	3
7770	It is an interoperability scenario.	f	1940	0
7771	The scenario includes the environment.	f	1940	1
7772	The scenario does not include the stimulus.	f	1940	2
7773	The scenario includes the response measure.	t	1940	3
7774	Can be a failure.	f	1319	0
7775	Should be a fault.	t	1319	1
7776	Can be an error.	f	1319	2
7777	Should be a crash.	f	1319	3
7778	Define how the system is executed.	f	1551	0
7779	Define how the system can be tested.	t	1551	1
7780	Implement the system without bugs.	f	1551	2
7781	Show how the code is easy to read.	f	1551	3
7782	Availability.	f	1550	0
7783	Scalability.	f	1550	1
7784	Interoperability.	t	1550	2
7785	Performance.	f	1550	3
7786	Decreases throughput, because the management of queues add an overhead.	f	1611	0
7787	Increases availability in the case of the crash of a server, because the queues can be persistent.	t	1611	1
7788	Does not allow clients to work asynchronously with the server. 	f	1611	2
7789	Decouple clients from servers, such that it is possible to add and remove servers, transparently to the client.	f	1610	0
7790	Can reduce the amount of required processing, or disk accesses, associated with a set of requests.	t	1477	2
7791	May not require the synchronization of user sessions, if the requests of a particular client are always redirected to the same server.	f	1610	2
7792	All options are true.	t	1610	3
7793	Reduce the latency for all requests.	f	1477	0
7794	Cannot be combined with caches.	f	1477	1
7795	Does not allow the differentiation between request and reply.	f	1611	3
7796	All options are false.	f	1477	3
7798	Scalability.	t	1374	1
7799	Performance.	f	1374	2
7800	Application-specific eviction.	f	1374	3
7801	Improve performance because they can redirect the requests to the server that is less loaded.	f	1610	1
7802	The stimulus is stochastic.	f	1603	0
7803	The response measure is jitter.	f	1603	1
7804	The environment is specified.	t	1603	2
7805	The source of the stimulus is not specified.	f	1603	3
7806	Manage sampling rate.	t	1601	0
7807	Limit event response.	f	1601	1
7808	Introduce concurrency.	f	1601	2
7809	Reduce overhead.	f	1601	3
7810	Maintain multiple copies of computation.	t	1946	2
7811	Maintain multiple copies of data.	f	1946	3
7812	Increase resources.	f	1946	0
7813	Introduce concurrency.	f	1946	1
7814	The stimulus is not specified.	f	1641	0
7815	The environment is overload.	f	1641	1
7816	The response measure is miss rate.	f	1641	2
7817	All options are false.	t	1641	3
7818	Bound execution times.	f	1868	0
7819	Prioritize events.	f	1868	1
7820	Introduce concurrency.	t	1868	2
7821	Increase resource efficiency.	f	1868	3
7822	Increasing performance and availability	f	1398	0
7823	Increasing availability and decreasing performance	f	1398	1
7824	Increasing scalability and availability	f	1398	2
7825	Increasing performance and decreasing availability	t	1398	3
7826	Manage sampling rate.	f	1419	0
7827	Limit event response.	f	1419	1
5257	Influence the software development process and its management, but not the software architecture of the system under development	f	1522	0
5258	Are important to determine the feasibility of the system, but once we reach the conclusion that the system can be developed with these restrictions, software architecture no longer depends on these factors	f	1522	2
5259	Are not one of the influences of the software architecture in the Architecture Business Cycle	f	1522	3
5260	The *implementation* style, which allows us to know where are the artifacts that implement a certain module	f	1545	1
5261	The *layers* style, which allows us to show that the structure of our system is composed of various modules that may be easily reused in other systems	f	1545	2
5262	The *client-server* and *deployment* styles, which allow us to isolate the required functionality in a component that executes autonomously and, thus, reusable in other systems	f	1545	3
5263	To keep the current architecture of the system and optimize the code to achieve the currently required performance levels	f	1631	0
5264	To change the Deployment view, replicating the server component by more machines	f	1631	1
5265	To review the system's architecture so that part of the computation that is currently done at the server shifts to the clients	f	1631	2
5266	Those three viewtypes complement each other, but they are completely independent, showing different aspects that have no relation among them	f	1616	0
5267	The module and component-and-connector viewtypes are independent of one another, but the allocation viewtype depends on the first two	f	1616	1
5268	Each viewtype uses different software elements, such as modules or components, so it does not make sense to talk about relationships among these viewtypes	f	1616	2
5269	A subset of the requirements that do not have conflicts among them and that correspond to the most important business goals	f	1715	0
5270	A subset of the requirements that have many conflicts among them and for which you need to find tradeoffs early in the design process	f	1715	1
5271	A subset of the requirements that is chosen exclusively by the architect by taking into account their influence on the system's architecture	f	1715	3
5272	The *Shared data* style	f	1437	1
5273	The *Client-Server* style	f	1437	2
5274	The *Publish-subscribe* style	f	1437	3
5275	The *Communicating Processes* style	f	1476	0
5276	The *Pipes-and-filters* style	f	1476	1
5277	The *Peer-to-Peer* style	f	1476	2
5281	The *Uses* style	f	1501	1
5282	The *Layers* style	f	1501	2
5603	Client-Server	f	1465	0
6164	Performance	f	1953	0
6597	Tiers	f	1507	0
6847	Tiers.	f	1908	1
6867	Publish-Subscribe.	f	1517	3
7503	Decomposition.	t	1355	0
7710	There is replication of the databases.	f	1380	1
7797	Availability.	f	1374	0
7828	Prioritize events.	f	1419	2
7829	Maintain multiple copies of computation.	t	1419	3
7830	Influence the software development process and its management, but not the software architecture of the system under development	f	1699	0
7831	Are important to determine the feasibility of the system, but once we reach the conclusion that the system can be developed with these restrictions, software architecture no longer depends on these factors	f	1699	1
7832	Are not one of the influences of the software architecture in the Architecture Influence Cycle	f	1699	2
7833	Should be captured in scenarios, as the requirements for quality attributes, and be taken into account in the design of the software architecture	t	1699	3
7834	Mange sampling rate.	f	1963	0
7835	Limit event response.	t	1963	1
7836	Increase resources.	f	1963	2
7837	Maintain multiple copies of computation.	f	1963	3
7838	Omission.	f	1805	0
7839	Crash.	f	1805	1
7840	Timing.	f	1805	2
7841	Response.	t	1805	3
7850	The availability scenarios should describe what is the expected behavior of the system, from the end user point of view, without being necessary to identify the concrete faults that can occur in the components.	f	1370	1
7851	The definition of a solution for a particular fault does not bring the possibility of the occurrence of new faults.	f	1370	3
7852	A fault in the application code.	f	1389	0
7853	A fault in the application server hardware memory.	f	1389	1
7854	A fault in a library being used by the application	f	1389	2
7855	All options are true.	t	1389	3
7856	It is possible to write all scenarios without designing any part of the architecture.	f	1370	0
7857	The identification of which faults can occur is an iterative process that is applied at different phases of the architectural design.	t	1370	2
7858	Ping/echo.	f	1801	0
7859	Heartbeat.	t	1801	1
7860	Sanity checking.	f	1801	2
7861	Exception detection.	f	1801	3
7862	Replication.	f	1432	0
7863	Functional redundancy.	t	1432	1
7864	Analytic redundancy.	f	1432	2
7865	Whatever redundancy.	f	1432	3
7866	Active redundancy.	f	1840	0
7867	Passive redundancy.	t	1840	1
7868	Spare.	f	1840	2
7869	Retry.	f	1840	3
7870	Active redundancy.	t	1951	0
7871	Passive redundancy.	f	1951	1
7872	Spare.	f	1951	2
7873	Rollback.	f	1951	3
7874	Shadow.	f	1839	0
7875	State resynchronization.	f	1839	1
7876	Escalating restart.	t	1839	2
7877	Non-stop forwarding.	f	1839	3
7878	During design time.	f	1784	0
7879	During compile time.	f	1784	1
7880	During build time.	t	1784	2
7881	During initialization time.	f	1784	3
7882	The cost of change is higher than the cost of installing the mechanism.	f	1424	0
7883	The cost of installing the mechanism is higher than the cost of change.	t	1424	1
7884	Both costs are high, of change and of installing the mechanism.	f	1424	2
7885	Both costs are low, of change and of installing the mechanism.	f	1424	3
7886	A change to increase the availability of a set of application servers.	t	1856	1
7887	A change to support a new type of browser.	f	1856	2
7888	Split module.	f	1799	0
7889	Increase semantic coherence.	t	1799	1
7890	Encapsulate.	f	1799	2
7891	Use an intermediary.	f	1799	3
7892	Split module.	f	1788	0
7893	Increase semantic coherence.	f	1788	1
7894	Use an intermediary.	t	1788	2
7895	Refactor.	f	1788	3
7896	Change to the functionality of the system.	f	1856	0
7897	A change to improve the usability of an end user interface.	f	1856	3
7898	It is the Webapp component.	f	1944	0
7899	Are the Client applications.	f	1944	1
7900	It is the filesystem.	f	1944	2
7901	It is the implementation of a manage sampling rate tactic to fulfil a write performance scenario.	t	1944	3
7902	The datapoints that are being used to draw the graph can be located in a cache in the Webapp, removing the cost of retrieving them from the disk.	f	1952	0
7903	The datapoints that are being used to draw the graph can be located in a cache in the ComposerUI, removing the cost of getting them from the  Webapp, but it is necessary to define a module in the ComposerUI responsible for the generating graphs.	f	1952	1
7904	The ComposerUI is a completely independent component, closer to a simulator of the definition of URL graph expressions, but is it necessary that is contains metrics and datapoints values to be used in the simulations, though not corresponding to real data. 	f	1952	2
7905	All options present possible solutions but with different implementation costs and variations on the ComposerUI functionality.	t	1952	3
7906	Client-server to represent performance.	f	1605	0
7907	Service-oriented architecture to represent interoperability.	f	1605	1
7908	Shared-data to represent modifiability.	f	1605	2
7909	Tiers to represent scalability.	t	1605	3
7910	It is defined by the impact the goal can have in the environment.	f	1800	0
7911	It is its volatility.	f	1800	1
7912	It is how much the its owner is willing to spend to achieve it.	t	1800	2
7913	It is the level of confidence the person who stated the goal has in it.	f	1800	3
7914	Addresses several quality attributes.	f	1804	0
7915	All options are true.	t	1804	3
7916	Means that including this requirement will very likely results in a different architecture.	t	1807	2
7917	Is a value the stakeholders assign to the ASR.	f	1807	1
7918	Associates with each architecturally significant requirement its business value and architectural impact.	f	1804	1
7919	Is a technique to assess what are the most relevant requirements that architecture should address.	f	1804	2
7920	It is the cost associated with the implementation of the ASR.	f	1807	0
7921	Defines the number of software elements of the software architecture that are related with the ASR.	f	1807	3
7922	Prototyping of the user interface, for the usability quality.	f	1810	0
7923	Testing the behavior of the key software elements of the architecture under a heavy load, for the performance quality.	f	1810	1
7924	Simulating faults in a prototype implementation the architecture to analyse how it behaves, for the availability quality.	f	1810	2
7925	All option are correct.	t	1810	3
7926	A process model where the design is completely isolated from the identification of new requirements.	f	1588	0
7927	A process model which emphazise, first implementation of code and then its refactoring.	f	1588	1
7928	Is an iterative method that, at each iteration, helps the architect to choose a part of the system to design, marshal all significant architectural requirements for that part, and create and test a design for that part.	t	1588	3
7929	It excludes the possibility of undoing decompositions previously done.	f	1588	2
7930	Encapsulation.	t	1412	0
7931	Use an intermediary.	f	1412	1
7932	Refactor.	f	1412	2
7933	Restrict dependencies.	f	1412	3
7934	Increase resources.	f	1980	0
7935	Introduce concurrency.	f	1980	1
7936	Limit event resources.	f	1980	2
7937	Schedule resources.	t	1980	3
7938	Increase semantic coherence.	f	1967	0
7939	Use an intermediary.	f	1967	1
7940	Restrict dependencies.	f	1967	2
7941	Abstract common services.	t	1967	3
7942	Performance.	f	1978	0
7943	Usability.	f	1978	1
7944	Performance and usability.	t	1978	2
7945	Modifiability and performance.	f	1978	3
7946	Manage sampling rate.	f	1754	3
7947	Increase resources.	f	1692	1
7948	There is no relation between the cost of change and the cost of development.	f	1808	2
7949	This decision is not a consequence of the Fénix business case.	f	1475	3
7950	Increase resources and Maintain multiple copies of computation.	t	1692	3
7951	A low cost of change may imply a high cost of development.	t	1808	0
7952	This decision corresponds to a constraint requirement.	t	1475	1
\.


--
-- Data for Name: question_answers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.question_answers (id, time_taken, option_id, quiz_answer_id, quiz_question_id, sequence) FROM stdin;
61906	41640	7663	8593	40892	0
61907	33934	7666	8593	40893	1
61908	12327	7670	8593	40894	2
61909	24062	7673	8593	40895	3
61910	42289	7680	8593	40896	4
61911	36539	7699	8592	40897	0
61912	98271	7684	8592	40898	1
61913	90625	7691	8592	40899	2
61914	32504	7695	8592	40900	3
61915	83359	7701	8592	40901	4
61916	109618	7686	8064	40897	0
61917	641072	7683	8064	40898	1
61918	51966	7692	8064	40899	2
61919	549055	7695	8064	40900	3
61920	304994	7701	8064	40901	4
61921	19048	7699	8260	40897	0
61922	30635	7685	8260	40898	1
61923	17287	7691	8260	40899	2
61924	18687	7695	8260	40900	3
61925	10187	7703	8260	40901	4
61926	53543	7663	8063	40892	0
61927	93823	7668	8063	40893	1
61928	162496	7670	8063	40894	2
61929	64223	7673	8063	40895	3
61930	28520	7680	8063	40896	4
61931	70193	7686	8279	40897	0
61932	106569	7684	8279	40898	1
61933	92943	7691	8279	40899	2
61934	139665	7694	8279	40900	3
61935	118592	7701	8279	40901	4
61936	19139	7663	8278	40892	0
61937	18091	7668	8278	40893	1
61938	104108	7669	8278	40894	2
61939	23238	7673	8278	40895	3
61940	49703	7680	8278	40896	4
61941	33538	7699	8131	40897	0
61942	50658	7684	8131	40898	1
61943	9195	7691	8131	40899	2
61944	31551	7695	8131	40900	3
61945	13133	7701	8131	40901	4
61946	8231	7663	8130	40892	0
61947	46042	7667	8130	40893	1
61948	35977	7670	8130	40894	2
61949	8135	7673	8130	40895	3
61950	12462	7680	8130	40896	4
61951	36959	7662	8311	40892	0
61952	96142	7667	8311	40893	1
61953	12006	7670	8311	40894	2
61954	44010	7673	8311	40895	3
61955	34308	7680	8311	40896	4
61956	53608	7699	8120	40897	0
61957	19946	7683	8120	40898	1
61958	18778	7691	8120	40899	2
61959	15854	7695	8120	40900	3
61960	48234	7701	8120	40901	4
61961	40778	7663	8119	40892	0
61962	30045	7667	8119	40893	1
61963	18382	7670	8119	40894	2
61964	22461	7673	8119	40895	3
61965	37402	7680	8119	40896	4
61966	169277	7663	8543	40892	0
61967	105092	7668	8543	40893	1
61968	117767	7670	8543	40894	2
61969	34556	7673	8543	40895	3
61970	239201	7680	8543	40896	4
61971	194125	7687	8176	40897	0
61972	17690	7684	8176	40898	1
61973	18921	7692	8176	40899	2
61974	9901	7693	8176	40900	3
61975	12369	7700	8176	40901	4
61976	21832	7662	8175	40892	0
61977	30640	7666	8175	40893	1
61978	18516	7672	8175	40894	2
61979	10594	7674	8175	40895	3
61980	27451	7679	8175	40896	4
61981	947369	7686	8827	40897	0
61982	480220	7685	8827	40898	1
61983	31411	7691	8827	40899	2
61984	14280	7695	8827	40900	3
61985	25508	7701	8827	40901	4
61986	206522	7686	8200	40897	0
61987	325799	7684	8200	40898	1
61988	22590	7692	8200	40899	2
61989	151462	7695	8200	40900	3
61990	323527	7701	8200	40901	4
61991	85814	7663	8199	40892	0
61992	42049	7667	8199	40893	1
61993	48804	7670	8199	40894	2
61994	19526	7673	8199	40895	3
61995	18987	7680	8199	40896	4
61996	422363	7662	8414	40892	0
61997	123763	7667	8414	40893	1
61998	49176	7670	8414	40894	2
61999	44320	7673	8414	40895	3
62000	56884	7678	8414	40896	4
62001	155910	7686	8413	40897	0
62002	264482	7684	8413	40898	1
62003	186822	7691	8413	40899	2
62004	221200	7694	8413	40900	3
62005	58763	7703	8413	40901	4
62006	52805	7681	8431	40892	0
62007	24925	7668	8431	40893	1
62008	7972	7670	8431	40894	2
62009	14549	7673	8431	40895	3
62010	14316	7680	8431	40896	4
62011	5549	7686	8430	40897	0
62012	3695	7684	8430	40898	1
62013	29579	7692	8430	40899	2
62014	68113	7695	8430	40900	3
62015	9129	7700	8430	40901	4
62016	73691	7681	8507	40892	0
62017	37756	7667	8507	40893	1
62018	12297	7670	8507	40894	2
62019	17447	7673	8507	40895	3
62020	21620	7680	8507	40896	4
62021	384123	7699	8506	40897	0
62022	54139	7684	8506	40898	1
62023	16379	7691	8506	40899	2
62024	19400	7695	8506	40900	3
62025	26727	7701	8506	40901	4
62026	42963	7681	8612	40892	0
62027	3766	7665	8612	40893	1
62028	8734	7670	8612	40894	2
62029	10161	7673	8612	40895	3
62030	40867	7680	8612	40896	4
62031	550704	7686	8611	40897	0
62032	71359	7685	8611	40898	1
62033	157086	7691	8611	40899	2
62034	204994	7695	8611	40900	3
62035	221358	7701	8611	40901	4
62036	120292	7686	7837	40897	0
62037	79124	7685	7837	40898	1
62038	66591	7691	7837	40899	2
62039	147490	7695	7837	40900	3
62040	106089	7701	7837	40901	4
62041	67846	7687	8341	40897	0
62042	168072	7685	8341	40898	1
62043	144709	7662	8340	40892	0
62044	105000	7665	8340	40893	1
62045	20004	7670	8340	40894	2
62046	30053	7673	8340	40895	3
62047	50355	7677	8340	40896	4
62048	119776	7699	8163	40897	0
62049	93318	7684	8163	40898	1
62050	52029	7691	8163	40899	2
62051	39723	7695	8163	40900	3
62052	11411	7700	8163	40901	4
62053	585455	7699	8452	40897	0
62054	31072	7685	8452	40898	1
62055	11108	7691	8452	40899	2
62056	19236	7695	8452	40900	3
62057	285653	7701	8452	40901	4
62058	3523	7663	7861	40892	0
62059	5496	7665	7861	40893	1
62060	4260	7670	7861	40894	2
62061	3992	7674	7861	40895	3
62062	3324	7680	7861	40896	4
62063	26777	7686	7860	40897	0
62064	22428	7684	7860	40898	1
62065	11188	7691	7860	40899	2
62066	112296	7695	7860	40900	3
62067	41432	7703	7860	40901	4
62068	21438	7686	7933	40897	0
62069	16042	7684	7933	40898	1
62070	10861	7691	7933	40899	2
62071	19478	7695	7933	40900	3
62072	11316	7703	7933	40901	4
62073	23745	7663	8654	40892	0
62074	42486	7665	8654	40893	1
62075	28575	7669	8654	40894	2
62076	15024	7673	8654	40895	3
62077	30522	7678	8654	40896	4
62078	31650	7699	8653	40897	0
62079	28145	7684	8653	40898	1
62080	15072	7691	8653	40899	2
62081	17552	7695	8653	40900	3
62082	8097	7701	8653	40901	4
62083	45948	7663	7932	40892	0
62084	39399	7665	7932	40893	1
62085	29369	7670	7932	40894	2
62086	19722	7673	7932	40895	3
62087	27194	7680	7932	40896	4
62088	74287	7663	8689	40892	0
62089	43819	7667	8689	40893	1
62090	100717	7670	8689	40894	2
62091	18805	7673	8689	40895	3
62092	29464	7680	8689	40896	4
62093	49801	7686	8688	40897	0
62094	55639	7684	8688	40898	1
62095	52481	7692	8688	40899	2
62096	54811	7694	8688	40900	3
62097	5916	7701	8688	40901	4
62098	48651	7663	8736	40892	0
62099	169719	7665	8736	40893	1
62100	89458	7671	8736	40894	2
62101	17919	7673	8736	40895	3
62102	27857	7680	8736	40896	4
62103	414847	7663	8791	40892	0
62104	70292	7666	8791	40893	1
62105	19893	7670	8791	40894	2
62106	57029	7673	8791	40895	3
62107	19119	7680	8791	40896	4
62108	177088	7699	8542	40897	0
62109	260337	7685	8542	40898	1
62110	6394	7691	8542	40899	2
62111	180606	7694	8542	40900	3
62112	149421	7700	8542	40901	4
62113	80836	7686	8790	40897	0
62114	22765	7685	8790	40898	1
62115	30215	7692	8790	40899	2
62116	96212	7695	8790	40900	3
62117	19366	7701	8790	40901	4
62118	22652	7699	8035	40897	0
62119	206382	7685	8035	40898	1
62120	16114	7692	8035	40899	2
62121	33801	7695	8035	40900	3
62122	12324	7702	8035	40901	4
62123	104998	7681	8474	40892	0
62124	80412	7666	8474	40893	1
62125	26419	7670	8474	40894	2
62126	45151	7673	8474	40895	3
62127	219855	7680	8474	40896	4
62128	30812	7686	8310	40897	0
62129	28016	7684	8310	40898	1
62130	9357	7692	8310	40899	2
62131	160488	7695	8310	40900	3
62132	177797	7701	8310	40901	4
62133	13234	7663	8036	40892	0
62134	17199	7668	8036	40893	1
62135	7321	7670	8036	40894	2
62136	16369	7673	8036	40895	3
62137	31441	7680	8036	40896	4
62138	13214	7704	8034	40902	0
62139	18480	7708	8034	40903	1
62140	96210	7713	8034	40904	2
62141	8253	7717	8034	40905	3
62142	12553	7722	8034	40906	4
62143	23475	7728	8033	40907	0
62144	53516	7727	8033	40908	1
62145	32027	7732	8033	40909	2
62146	20646	7735	8033	40910	3
62147	8135	7738	8033	40911	4
62148	177059	7664	7892	40892	0
62149	26749	7665	7892	40893	1
62150	70327	7670	7892	40894	2
62151	96518	7675	7892	40895	3
62152	27578	7680	7892	40896	4
62153	24284	7686	7891	40897	0
62154	57530	7684	7891	40898	1
62155	23158	7692	7891	40899	2
62156	74748	7694	7891	40900	3
62157	48877	7700	7891	40901	4
62158	36115	7699	8725	40897	0
62159	25375	7683	8725	40898	1
62160	20603	7692	8725	40899	2
62161	90483	7695	8725	40900	3
62162	51646	7701	8725	40901	4
62163	142441	7681	8724	40892	0
62164	56729	7666	8724	40893	1
62165	44165	7672	8724	40894	2
62166	21552	7673	8724	40895	3
62167	30740	7680	8724	40896	4
62168	130837	7663	8290	40892	0
62169	94276	7665	8290	40893	1
62170	6358	7670	8290	40894	2
62171	17310	7673	8290	40895	3
62172	46094	7680	8290	40896	4
62173	11806	7686	8289	40897	0
62174	90388	7683	8289	40898	1
62175	297648	7692	8289	40899	2
62176	420199	7696	8289	40900	3
62177	11571	7703	8289	40901	4
62178	37357	7663	7822	40892	0
62179	64028	7665	7822	40893	1
62180	17539	7670	7822	40894	2
62181	20634	7673	7822	40895	3
62182	27054	7680	7822	40896	4
62183	54857	7686	7821	40897	0
62184	45982	7684	7821	40898	1
62185	38672	7691	7821	40899	2
62186	3948252	7694	7821	40900	3
62187	88483	7702	7821	40901	4
62188	260438	7705	7820	40902	0
62189	32142	7708	7820	40903	1
62190	39149	7715	7820	40904	2
62191	58695	7717	7820	40905	3
62192	25097	7722	7820	40906	4
62193	78282	7728	7819	40907	0
62194	13867	7725	7819	40908	1
62195	17689	7732	7819	40909	2
62196	362519	7734	7819	40910	3
62197	39492	7739	7819	40911	4
62198	33982	7704	8826	40902	0
62199	18792	7708	8826	40903	1
62200	23583	7715	8826	40904	2
62201	32024	7717	8826	40905	3
62202	24800	7722	8826	40906	4
62203	208015	7662	8762	40892	0
62204	328102	7666	8762	40893	1
62205	91784	7670	8762	40894	2
62206	88604	7673	8762	40895	3
62207	171163	7680	8762	40896	4
62208	149602	7663	8809	40892	0
62209	29304	7665	8809	40893	1
62210	350688	7670	8809	40894	2
62211	227832	7673	8809	40895	3
62212	74417	7680	8809	40896	4
62213	14857	7744	8032	40912	0
62214	30572	7749	8032	40913	1
62215	26028	7751	8032	40914	2
62216	30315	7753	8032	40915	3
62217	22568	7757	8032	40916	4
62218	242457	7705	8277	40902	0
62219	39470	7708	8277	40903	1
62220	14606	7715	8277	40904	2
62221	63392	7717	8277	40905	3
62222	12564	7722	8277	40906	4
62223	395725	7728	8276	40907	0
62224	486645	7725	8276	40908	1
62225	38392	7732	8276	40909	2
62226	221436	7735	8276	40910	3
62227	23688	7738	8276	40911	4
62228	66734	7744	8275	40912	0
62229	30621	7749	8275	40913	1
62230	106982	7751	8275	40914	2
62231	73090	7753	8275	40915	3
62232	30198	7760	8275	40916	4
62233	158812	7744	8825	40912	0
62234	22343	7748	8825	40913	1
62235	31613	7751	8825	40914	2
62236	29917	7753	8825	40915	3
62237	33438	7760	8825	40916	4
62238	44258	7681	8824	40892	0
62239	28440	7665	8824	40893	1
62240	24797	7670	8824	40894	2
62241	20266	7673	8824	40895	3
62242	24112	7678	8824	40896	4
62243	81937	7697	8823	40907	0
62244	18071	7725	8823	40908	1
62245	12265	7732	8823	40909	2
62246	25463	7734	8823	40910	3
62247	26511	7738	8823	40911	4
62248	17536	7663	8236	40892	0
62249	16093	7665	8236	40893	1
62250	26075	7670	8236	40894	2
62251	25461	7674	8236	40895	3
62252	26144	7678	8236	40896	4
62253	3437970	7663	8573	40892	0
62254	148126	7665	8573	40893	1
62255	17328	7669	8573	40894	2
62256	87858	7673	8573	40895	3
62257	67096	7680	8573	40896	4
62258	85133	7704	8118	40902	0
62259	25512	7708	8118	40903	1
62260	100193	7715	8118	40904	2
62261	15375	7717	8118	40905	3
62262	10944	7722	8118	40906	4
62263	25256	7704	8541	40902	0
62264	21929	7708	8541	40903	1
62265	258280	7715	8541	40904	2
62266	35627	7717	8541	40905	3
62267	19320	7722	8541	40906	4
62268	18153	7704	8062	40902	0
62269	31420	7708	8062	40903	1
62270	216611	7715	8062	40904	2
62271	56794	7717	8062	40905	3
62272	42416	7722	8062	40906	4
62273	132981	7728	8540	40907	0
62274	22600	7726	8540	40908	1
62275	120490	7732	8540	40909	2
62276	12013	7734	8540	40910	3
62277	59652	7738	8540	40911	4
62278	261263	7728	8117	40907	0
62279	49997	7726	8117	40908	1
62280	131555	7732	8117	40909	2
62281	228173	7733	8117	40910	3
62282	15411	7738	8117	40911	4
62283	278589	7728	8061	40907	0
62284	244090	7725	8061	40908	1
62285	31502	7732	8061	40909	2
62286	60163	7733	8061	40910	3
62287	31270	7738	8061	40911	4
62288	26522	7744	8116	40912	0
62289	78200	7749	8116	40913	1
62290	13447	7751	8116	40914	2
62291	104089	7753	8116	40915	3
62292	29131	7760	8116	40916	4
62293	709584	7744	8060	40912	0
62294	93676	7749	8060	40913	1
62295	53863	7751	8060	40914	2
62296	53549	7753	8060	40915	3
62297	72731	7760	8060	40916	4
62298	48647	7706	8652	40902	0
62299	13217	7708	8652	40903	1
62300	19039	7715	8652	40904	2
62301	154056	7717	8652	40905	3
62302	284721	7722	8652	40906	4
62303	27769	7728	8651	40907	0
62304	11144	7725	8651	40908	1
62305	39320	7731	8651	40909	2
62306	43753	7735	8651	40910	3
62307	14199	7738	8651	40911	4
62308	51759	7704	8309	40902	0
62309	24876	7708	8309	40903	1
62310	14944	7715	8309	40904	2
62311	25354	7717	8309	40905	3
62312	18643	7722	8309	40906	4
62313	79791	7728	8308	40907	0
62314	105752	7725	8308	40908	1
62315	97262	7732	8308	40909	2
62316	113762	7734	8308	40910	3
62317	23954	7738	8308	40911	4
62318	28424	7744	8307	40912	0
62319	36494	7748	8307	40913	1
62320	21440	7751	8307	40914	2
62321	29012	7753	8307	40915	3
62322	32857	7760	8307	40916	4
62323	195152	7704	7836	40902	0
62324	113570	7708	7836	40903	1
62325	279188	7713	7836	40904	2
62326	121193	7717	7836	40905	3
62327	376716	7722	7836	40906	4
62328	256368	7728	7835	40907	0
62329	206605	7725	7835	40908	1
62330	154787	7732	7835	40909	2
62331	89498	7734	7835	40910	3
62332	260833	7738	7835	40911	4
62333	422238	7744	7834	40912	0
62334	216697	7746	7834	40913	1
62335	165160	7751	7834	40914	2
62336	143562	7753	7834	40915	3
62337	135065	7758	7834	40916	4
62338	40355	7744	8539	40912	0
62339	359829	7749	8539	40913	1
62340	56439	7751	8539	40914	2
62341	121156	7753	8539	40915	3
62342	33181	7760	8539	40916	4
62343	15076	7704	8591	40902	0
62344	22434	7708	8591	40903	1
62345	22826	7715	8591	40904	2
62346	24261	7717	8591	40905	3
62347	19306	7722	8591	40906	4
62348	31770	7728	8590	40907	0
62349	28307	7726	8590	40908	1
62350	23214	7732	8590	40909	2
62351	34438	7734	8590	40910	3
62352	15008	7738	8590	40911	4
62353	40174	7744	8589	40912	0
62354	49055	7747	8589	40913	1
62355	28114	7751	8589	40914	2
62356	33245	7754	8589	40915	3
62357	19737	7760	8589	40916	4
62358	68526	7742	7968	40912	0
62359	45726	7746	7968	40913	1
62360	29965	7761	7968	40914	2
62361	24322	7753	7968	40915	3
62362	60452	7760	7968	40916	4
62363	35046	7704	7967	40902	0
62364	20588	7708	7967	40903	1
62365	24463	7715	7967	40904	2
62366	25588	7717	7967	40905	3
62367	29975	7720	7967	40906	4
62368	61636	7697	7966	40907	0
62369	35316	7725	7966	40908	1
62370	87086	7741	7966	40909	2
62371	41541	7734	7966	40910	3
62372	29886	7740	7966	40911	4
62373	38550	7686	7965	40897	0
62374	25469	7684	7965	40898	1
62375	14560	7691	7965	40899	2
62376	56768	7694	7965	40900	3
62377	16454	7700	7965	40901	4
62378	47772	7662	7964	40892	0
62379	37489	7668	7964	40893	1
62380	20563	7670	7964	40894	2
62381	29833	7673	7964	40895	3
62382	38103	7680	7964	40896	4
62383	36425	7704	7859	40902	0
62384	26231	7708	7859	40903	1
62385	25325	7715	7859	40904	2
62386	18962	7717	7859	40905	3
62387	21580	7722	7859	40906	4
62388	1018833	7663	8665	40892	0
62389	606853	7665	8665	40893	1
62390	233045	7670	8665	40894	2
62391	225675	7673	8665	40895	3
62392	608612	7680	8665	40896	4
62393	49035	7744	7818	40912	0
62394	130552	7746	7818	40913	1
62395	43279	7751	7818	40914	2
62396	20738	7753	7818	40915	3
62397	32291	7760	7818	40916	4
62398	31168	7663	8153	40892	0
62399	100154	7665	8153	40893	1
62400	14140	7670	8153	40894	2
62401	26549	7673	8153	40895	3
62402	32848	7680	8153	40896	4
62403	26139	7686	8152	40897	0
62404	38719	7684	8152	40898	1
62405	33802	7692	8152	40899	2
62406	31348	7695	8152	40900	3
62407	15017	7703	8152	40901	4
62408	37008	7704	8151	40902	0
62409	28929	7710	8151	40903	1
62410	14092	7715	8151	40904	2
62411	31219	7717	8151	40905	3
62412	31213	7722	8151	40906	4
62413	18245	7762	8305	40917	0
62414	22875	7766	8305	40918	1
62415	51707	7770	8305	40919	2
62416	41905	7774	8305	40920	3
62417	105431	7779	8305	40921	4
62418	23137	7784	8722	40922	0
62419	68263	7787	8722	40923	1
62420	31516	7801	8722	40924	2
62421	14621	7790	8722	40925	3
62422	223268	7799	8722	40926	4
62423	15989	7663	8259	40892	0
62424	23022	7668	8259	40893	1
62425	10451	7670	8259	40894	2
62426	25900	7673	8259	40895	3
62427	13430	7678	8259	40896	4
62428	30842	7705	8258	40902	0
62429	9819	7708	8258	40903	1
62430	19905	7715	8258	40904	2
62431	40248	7717	8258	40905	3
62432	16088	7722	8258	40906	4
62433	32698	7784	8306	40922	0
62434	55204	7787	8306	40923	1
62435	46402	7792	8306	40924	2
62436	13265	7796	8306	40925	3
62437	283811	7798	8306	40926	4
62438	7616	7762	7931	40917	0
62439	11320	7766	7931	40918	1
62440	27581	7770	7931	40919	2
62441	18964	7775	7931	40920	3
62442	21714	7779	7931	40921	4
62443	34540	7784	7930	40922	0
62444	203560	7787	7930	40923	1
62445	19676	7792	7930	40924	2
62446	9375	7790	7930	40925	3
62447	23519	7799	7930	40926	4
62448	495370	7686	8233	40897	0
62449	1102453	7683	8233	40898	1
62450	385957	7692	8233	40899	2
62451	233810	7694	8233	40900	3
62452	274807	7700	8233	40901	4
62453	76793	7706	8505	40902	0
62454	25337	7708	8505	40903	1
62455	37692	7715	8505	40904	2
62456	24968	7717	8505	40905	3
62457	15528	7722	8505	40906	4
62458	36051	7728	8504	40907	0
62459	42126	7726	8504	40908	1
62460	83754	7732	8504	40909	2
62461	163150	7734	8504	40910	3
62462	13189	7738	8504	40911	4
62463	49943	7744	8503	40912	0
62464	20464	7749	8503	40913	1
62465	22240	7751	8503	40914	2
62466	17676	7753	8503	40915	3
62467	39120	7760	8503	40916	4
62468	18303	7762	8502	40917	0
62469	13007	7769	8502	40918	1
62470	73426	7773	8502	40919	2
62471	20670	7775	8502	40920	3
62472	19489	7779	8502	40921	4
62473	69495	7785	8501	40922	0
62474	34077	7787	8501	40923	1
62475	17808	7792	8501	40924	2
62476	33943	7790	8501	40925	3
62477	23449	7798	8501	40926	4
62478	50240	7728	7858	40907	0
62479	16676	7726	7858	40908	1
62480	47712	7741	7858	40909	2
62481	16392	7735	7858	40910	3
62482	29400	7738	7858	40911	4
62483	128825	7744	7857	40912	0
62484	28711	7748	7857	40913	1
62485	45292	7751	7857	40914	2
62486	20941	7753	7857	40915	3
62487	28016	7760	7857	40916	4
62488	9445	7762	7856	40917	0
62489	24972	7766	7856	40918	1
62490	19140	7770	7856	40919	2
62491	21931	7775	7856	40920	3
62492	45221	7779	7856	40921	4
62493	20728	7784	7855	40922	0
62494	18979	7787	7855	40923	1
62495	18764	7792	7855	40924	2
62496	13635	7790	7855	40925	3
62497	64492	7797	7855	40926	4
62498	32924	7728	8721	40907	0
62499	22119	7725	8721	40908	1
62500	25782	7732	8721	40909	2
62501	17457	7734	8721	40910	3
62502	19346	7738	8721	40911	4
62503	28688	7686	8572	40897	0
62504	21671	7684	8572	40898	1
62505	29667	7691	8572	40899	2
62506	1740336	7694	8572	40900	3
62507	21238	7702	8572	40901	4
62508	12162	7762	8568	40917	0
62509	51732	7766	8568	40918	1
62510	127716	7773	8568	40919	2
62511	8546	7775	8568	40920	3
62512	48087	7779	8568	40921	4
62513	65354	7728	8571	40907	0
62514	1010421	7724	8571	40908	1
62515	32537	7732	8571	40909	2
62516	338990	7735	8571	40910	3
62517	78155	7738	8571	40911	4
62518	38928	7784	8059	40922	0
62519	28656	7787	8059	40923	1
62520	22241	7792	8059	40924	2
62521	102041	7790	8059	40925	3
62522	77798	7798	8059	40926	4
62523	11155	7762	8058	40917	0
62524	19473	7769	8058	40918	1
62525	22543	7773	8058	40919	2
62526	11017	7775	8058	40920	3
62527	15776	7779	8058	40921	4
62528	131819	7762	7833	40917	0
62529	112792	7766	7833	40918	1
62530	230967	7773	7833	40919	2
62531	55575	7775	7833	40920	3
62532	130378	7779	7833	40921	4
62533	110712	7784	8274	40922	0
62534	120226	7787	8274	40923	1
62535	186293	7792	8274	40924	2
62536	81256	7790	8274	40925	3
62537	239776	7798	8274	40926	4
62538	43042	7762	8273	40917	0
62539	55380	7769	8273	40918	1
62540	301192	7773	8273	40919	2
62541	17586	7775	8273	40920	3
62542	171064	7779	8273	40921	4
62543	259947	7802	8272	40927	0
62544	120544	7815	8272	40928	1
62545	39884	7806	8272	40929	2
62546	72896	7811	8272	40930	3
62547	175216	7820	8272	40931	4
62548	196542	7784	7832	40922	0
62549	213637	7787	7832	40923	1
62550	219329	7792	7832	40924	2
62551	81248	7790	7832	40925	3
62552	273563	7798	7832	40926	4
62553	92487	7804	8057	40927	0
62554	30543	7817	8057	40928	1
62555	8936	7806	8057	40929	2
62556	22769	7813	8057	40930	3
62557	79559	7820	8057	40931	4
62558	333497	7804	7831	40927	0
62559	172639	7817	7831	40928	1
62560	244398	7806	7831	40929	2
62561	137888	7810	7831	40930	3
62562	204456	7820	7831	40931	4
62563	35351	7744	8720	40912	0
62564	50410	7749	8720	40913	1
62565	43060	7751	8720	40914	2
62566	19311	7753	8720	40915	3
62567	75470	7760	8720	40916	4
62568	18744	7704	8761	40902	0
62569	30512	7708	8761	40903	1
62570	19684	7715	8761	40904	2
62571	17736	7717	8761	40905	3
62572	20124	7722	8761	40906	4
62573	80907	7728	8760	40907	0
62574	24807	7727	8760	40908	1
62575	24304	7732	8760	40909	2
62576	17415	7734	8760	40910	3
62577	22996	7738	8760	40911	4
62578	19862	7686	8759	40897	0
62579	59733	7684	8759	40898	1
62580	95319	7692	8759	40899	2
62581	15536	7694	8759	40900	3
62582	12676	7701	8759	40901	4
62583	38986	7744	8758	40912	0
62584	85707	7749	8758	40913	1
62585	34892	7751	8758	40914	2
62586	20663	7753	8758	40915	3
62587	29284	7760	8758	40916	4
62588	22649	7762	8757	40917	0
62589	356223	7769	8757	40918	1
62590	61231	7770	8757	40919	2
62591	7692	7775	8757	40920	3
62592	17488	7779	8757	40921	4
62593	46832	7783	8756	40922	0
62594	28820	7787	8756	40923	1
62595	24016	7792	8756	40924	2
62596	22150	7796	8756	40925	3
62597	19831	7798	8756	40926	4
62598	53036	7744	8257	40912	0
62599	18315	7747	8257	40913	1
62600	111023	7751	8257	40914	2
62601	14823	7753	8257	40915	3
62602	14542	7760	8257	40916	4
62603	37193	7804	8232	40927	0
62604	31397	7814	8232	40928	1
62605	30291	7806	8232	40929	2
62606	25920	7813	8232	40930	3
62607	24087	7819	8232	40931	4
62608	5531	7804	8755	40927	0
62609	7812	7817	8755	40928	1
62610	5812	7806	8755	40929	2
62611	3064	7813	8755	40930	3
62612	8926	7819	8755	40931	4
62613	11436	7762	8115	40917	0
62614	27586	7769	8115	40918	1
62615	37375	7773	8115	40919	2
62616	46036	7777	8115	40920	3
62617	24311	7779	8115	40921	4
62618	37806	7784	8114	40922	0
62619	46737	7787	8114	40923	1
62620	17406	7792	8114	40924	2
62621	18318	7790	8114	40925	3
62622	189113	7797	8114	40926	4
62623	62731	7804	8113	40927	0
62624	46702	7817	8113	40928	1
62625	11809	7806	8113	40929	2
62626	18351	7813	8113	40930	3
62627	19392	7820	8113	40931	4
62628	682678	7728	8610	40907	0
62629	604121	7725	8610	40908	1
62630	182597	7732	8610	40909	2
62631	201703	7738	8610	40911	4
62632	26997	7744	8650	40912	0
62633	25960	7749	8650	40913	1
62634	98084	7751	8650	40914	2
62635	27795	7753	8650	40915	3
62636	33605	7757	8650	40916	4
62637	17773	7784	8649	40922	0
62638	29563	7787	8649	40923	1
62639	24176	7792	8649	40924	2
62640	24342	7796	8649	40925	3
62641	12769	7798	8649	40926	4
62642	13241	7762	8648	40917	0
62643	27896	7769	8648	40918	1
62644	57725	7773	8648	40919	2
62645	33206	7774	8648	40920	3
62646	22627	7779	8648	40921	4
62647	79399	7804	8647	40927	0
62648	34414	7817	8647	40928	1
62649	18873	7806	8647	40929	2
62650	25253	7813	8647	40930	3
62651	18365	7821	8647	40931	4
62652	260215	7744	8609	40912	0
62653	643867	7749	8609	40913	1
62654	629763	7751	8609	40914	2
62655	254750	7753	8609	40915	3
62656	38193	7760	8609	40916	4
62657	59717	7802	8487	40927	0
62658	24069	7817	8487	40928	1
62659	34767	7806	8487	40929	2
62660	49865	7813	8487	40930	3
62661	27117	7819	8487	40931	4
62662	113849	7804	8304	40927	0
62663	41867	7815	8304	40928	1
62664	28092	7806	8304	40929	2
62665	39509	7810	8304	40930	3
62666	20748	7818	8304	40931	4
62667	46283	7705	8486	40902	0
62668	14159	7708	8486	40903	1
62669	101771	7712	8486	40904	2
62670	16109	7717	8486	40905	3
62671	15031	7722	8486	40906	4
62672	13610	7762	8485	40917	0
62673	39912	7766	8485	40918	1
62674	91737	7770	8485	40919	2
62675	12227	7775	8485	40920	3
62676	31429	7779	8485	40921	4
62677	21852	7784	8484	40922	0
62678	79940	7787	8484	40923	1
62679	37120	7789	8484	40924	2
62680	64768	7790	8484	40925	3
62681	33703	7798	8484	40926	4
62682	12169	7762	7816	40917	0
62683	37216	7769	7816	40918	1
62684	66610	7773	7816	40919	2
62685	19758	7775	7816	40920	3
62686	25822	7779	7816	40921	4
62687	59606	7784	7817	40922	0
62688	108944	7787	7817	40923	1
62689	36286	7792	7817	40924	2
62690	128967	7790	7817	40925	3
62691	368441	7797	7817	40926	4
62692	33843	7804	7815	40927	0
62693	54909	7817	7815	40928	1
62694	59150	7806	7815	40929	2
62695	86222	7811	7815	40930	3
62696	36074	7819	7815	40931	4
62697	29312	7744	8363	40912	0
62698	19885	7749	8363	40913	1
62699	70807	7751	8363	40914	2
62700	61846	7753	8363	40915	3
62701	34045	7760	8363	40916	4
62702	164561	7699	8358	40897	0
62703	286402	7684	8358	40898	1
62704	2318287	7691	8358	40899	2
62705	1006904	7695	8358	40900	3
62706	314101	7701	8358	40901	4
62707	135651	7728	8362	40907	0
62708	2905	7725	8362	40908	1
62709	15983	7732	8362	40909	2
62710	10491	7734	8362	40910	3
62711	47949	7738	8362	40911	4
62712	9416	7762	8719	40917	0
62713	15705	7766	8719	40918	1
62714	99610	7773	8719	40919	2
62715	37825	7775	8719	40920	3
62716	34681	7778	8719	40921	4
62717	118658	7804	8718	40927	0
62718	17839	7815	8718	40928	1
62719	22061	7806	8718	40929	2
62720	22838	7813	8718	40930	3
62721	63466	7819	8718	40931	4
62722	509737	7704	8608	40902	0
62723	22329	7710	8608	40903	1
62724	12944	7715	8608	40904	2
62725	27850	7717	8608	40905	3
62726	127557	7722	8608	40906	4
62727	207762	7785	8607	40922	0
62728	114568	7787	8607	40923	1
62729	52558	7792	8607	40924	2
62730	104052	7790	8607	40925	3
62731	30542	7800	8607	40926	4
62732	289654	7744	8412	40912	0
62733	213958	7749	8412	40913	1
62734	321198	7751	8412	40914	2
62735	471867	7753	8412	40915	3
62736	95100	7760	8412	40916	4
62737	67638	7804	8357	40927	0
62738	18737	7817	8357	40928	1
62739	39774	7806	8357	40929	2
62740	14830	7813	8357	40930	3
62741	20337	7821	8357	40931	4
62742	22471	7762	8361	40917	0
62743	42119	7769	8361	40918	1
62744	66705	7770	8361	40919	2
62745	15149	7774	8361	40920	3
62746	73000	7779	8361	40921	4
62747	55640	7704	8015	40902	0
62748	41135	7708	8015	40903	1
62749	29038	7714	8015	40904	2
62750	27929	7716	8015	40905	3
62751	31641	7719	8015	40906	4
62752	145531	7728	8014	40907	0
62753	61370	7727	8014	40908	1
62754	36302	7731	8014	40909	2
62755	81334	7734	8014	40910	3
62756	18865	7740	8014	40911	4
62757	122638	7804	7929	40927	0
62758	51790	7817	7929	40928	1
62759	12910	7806	7929	40929	2
62760	11390	7810	7929	40930	3
62761	110031	7820	7929	40931	4
62762	34620	7744	7928	40912	0
62763	49143	7746	7928	40913	1
62764	64451	7751	7928	40914	2
62765	30649	7753	7928	40915	3
62766	19729	7760	7928	40916	4
62767	45533	7728	7927	40907	0
62768	14198	7725	7927	40908	1
62769	11503	7732	7927	40909	2
62770	34294	7734	7927	40910	3
62771	30732	7738	7927	40911	4
62772	99225	7728	8687	40907	0
62773	11499	7724	8687	40908	1
62774	65095	7741	8687	40909	2
62775	50616	7734	8687	40910	3
62776	15849	7738	8687	40911	4
62777	175057	7742	8686	40912	0
62778	27263	7748	8686	40913	1
62779	123678	7751	8686	40914	2
62780	24647	7754	8686	40915	3
62781	38680	7760	8686	40916	4
62782	200147	7784	8538	40922	0
62783	57562	7787	8538	40923	1
62784	29573	7792	8538	40924	2
62785	13699	7790	8538	40925	3
62786	329143	7798	8538	40926	4
62787	18344	7762	8537	40917	0
62788	349051	7769	8537	40918	1
62789	174842	7773	8537	40919	2
62790	38902	7775	8537	40920	3
62791	103460	7779	8537	40921	4
62792	59286	7706	7926	40902	0
62793	23316	7708	7926	40903	1
62794	19208	7715	7926	40904	2
62795	41905	7718	7926	40905	3
62796	32656	7722	7926	40906	4
62797	84769	7686	8806	40897	0
62798	41712	7683	8806	40898	1
62799	103543	7692	8806	40899	2
62800	150855	7693	8806	40900	3
62801	13587	7701	8806	40901	4
62802	37722	7705	8808	40902	0
62803	28545	7710	8808	40903	1
62804	89448	7714	8808	40904	2
62805	39812	7717	8808	40905	3
62806	71101	7720	8808	40906	4
62807	63064	7704	7890	40902	0
62808	19129	7710	7890	40903	1
62809	17789	7715	7890	40904	2
62810	25972	7717	7890	40905	3
62811	17311	7722	7890	40906	4
62812	69473	7728	7888	40907	0
62813	11680	7727	7888	40908	1
62814	53371	7732	7888	40909	2
62815	15302	7733	7888	40910	3
62816	18147	7738	7888	40911	4
62817	21987	7744	7889	40912	0
62818	20940	7749	7889	40913	1
62819	94483	7751	7889	40914	2
62820	163669	7753	7889	40915	3
62821	26375	7760	7889	40916	4
62822	18571	7762	7887	40917	0
62823	34831	7769	7887	40918	1
62824	44390	7772	7887	40919	2
62825	20674	7774	7887	40920	3
62826	14472	7779	7887	40921	4
62827	31544	7782	7886	40922	0
62828	21371	7787	7886	40923	1
62829	18897	7801	7886	40924	2
62830	100915	7796	7886	40925	3
62831	16225	7798	7886	40926	4
62832	25984	7804	7885	40927	0
62833	41404	7816	7885	40928	1
62834	23103	7806	7885	40929	2
62835	14646	7813	7885	40930	3
62836	15253	7819	7885	40931	4
62837	2154785	7728	8807	40907	0
62838	47394	7727	8807	40908	1
62839	113410	7730	8807	40909	2
62840	165902	7735	8807	40910	3
62841	16063	7738	8807	40911	4
62842	89610	7744	8805	40912	0
62843	29397	7749	8805	40913	1
62844	81669	7751	8805	40914	2
62845	67857	7753	8805	40915	3
62846	130720	7760	8805	40916	4
62847	23745	7765	8804	40917	0
62848	109424	7768	8804	40918	1
62849	32573	7772	8804	40919	2
62850	62292	7774	8804	40920	3
62851	27092	7780	8804	40921	4
62852	15804	7804	8697	40927	0
62853	14322	7817	8697	40928	1
62854	14802	7806	8697	40929	2
62855	10284	7813	8697	40930	3
62856	15928	7819	8697	40931	4
62857	190663	7782	8803	40922	0
62858	47764	7787	8803	40923	1
62859	71455	7792	8803	40924	2
62860	28618	7790	8803	40925	3
62861	424094	7799	8803	40926	4
62862	18170	7728	8696	40907	0
62863	8348	7727	8696	40908	1
62864	22098	7741	8696	40909	2
62865	8832	7734	8696	40910	3
62866	8553	7738	8696	40911	4
62867	154977	7762	8606	40917	0
62868	98913	7769	8606	40918	1
62869	515759	7773	8606	40919	2
62870	21073	7775	8606	40920	3
62871	603506	7779	8606	40921	4
62872	433028	7804	8605	40927	0
62873	185259	7814	8605	40928	1
62874	13224	7806	8605	40929	2
62875	33472	7813	8605	40930	3
62876	351219	7821	8605	40931	4
62877	229684	7686	7980	40897	0
62878	16793	7685	7980	40898	1
62879	1263109	7692	7980	40899	2
62880	85173	7695	7980	40900	3
62881	113138	7703	7980	40901	4
62882	24423	7804	8031	40927	0
62883	16899	7817	8031	40928	1
62884	35064	7807	8031	40929	2
62885	53501	7812	8031	40930	3
62886	21554	7819	8031	40931	4
62887	40119	7785	8030	40922	0
62888	34568	7786	8030	40923	1
62889	28774	7792	8030	40924	2
62890	65306	7790	8030	40925	3
62891	49125	7798	8030	40926	4
62892	24683	7762	8029	40917	0
62893	14798	7769	8029	40918	1
62894	35665	7771	8029	40919	2
62895	14470	7775	8029	40920	3
62896	14488	7779	8029	40921	4
62897	803554	7804	8536	40927	0
62898	227983	7815	8536	40928	1
62899	107571	7806	8536	40929	2
62900	193127	7810	8536	40930	3
62901	284575	7820	8536	40931	4
62902	23651	7699	8473	40897	0
62903	31547	7685	8473	40898	1
62904	29437	7689	8473	40899	2
62905	45331	7695	8473	40900	3
62906	28549	7701	8473	40901	4
62907	127687	7706	8229	40902	0
62908	555979	7708	8229	40903	1
62909	122358	7713	8229	40904	2
62910	150378	7717	8229	40905	3
62911	151475	7719	8229	40906	4
62912	46807	7728	8235	40907	0
62913	54511	7725	8235	40908	1
62914	257956	7741	8235	40909	2
62915	23809	7734	8235	40910	3
62916	16427	7738	8235	40911	4
62917	47788	7744	8234	40912	0
62918	28839	7748	8234	40913	1
62919	154516	7751	8234	40914	2
62920	149255	7753	8234	40915	3
62921	40869	7760	8234	40916	4
62922	49405	7663	8356	40892	0
62923	41713	7665	8356	40893	1
62924	13617	7670	8356	40894	2
62925	111853	7675	8356	40895	3
62926	37416	7678	8356	40896	4
62927	23672	7762	8231	40917	0
62928	116921	7766	8231	40918	1
62929	161411	7773	8231	40919	2
62930	15176	7775	8231	40920	3
62931	49499	7778	8231	40921	4
62932	44485	7783	8230	40922	0
62933	23482	7786	8230	40923	1
62934	42265	7792	8230	40924	2
62935	27156	7796	8230	40925	3
62936	80034	7798	8230	40926	4
62937	116654	7784	8360	40922	0
62938	56826	7787	8360	40923	1
62939	39025	7792	8360	40924	2
62940	36099	7790	8360	40925	3
62941	46845	7798	8360	40926	4
62942	50403	7704	8198	40902	0
62943	21944	7708	8198	40903	1
62944	8738	7715	8198	40904	2
62945	6261	7717	8198	40905	3
62946	22811	7722	8198	40906	4
62947	32028	7728	8197	40907	0
62948	25828	7725	8197	40908	1
62949	94732	7732	8197	40909	2
62950	8430	7735	8197	40910	3
62951	12649	7738	8197	40911	4
62952	17149	7744	8196	40912	0
62953	23695	7748	8196	40913	1
62954	27850	7750	8196	40914	2
62955	28124	7753	8196	40915	3
62956	12671	7760	8196	40916	4
62957	34249	7784	8195	40922	0
62958	35222	7787	8195	40923	1
62959	12191	7792	8195	40924	2
62960	16583	7790	8195	40925	3
62961	17623	7798	8195	40926	4
62962	15036	7762	8194	40917	0
62963	33870	7769	8194	40918	1
62964	65538	7770	8194	40919	2
62965	56742	7775	8194	40920	3
62966	23613	7779	8194	40921	4
62967	80587	7804	8193	40927	0
62968	33505	7817	8193	40928	1
62969	64551	7809	8193	40929	2
62970	41818	7813	8193	40930	3
62971	15060	7819	8193	40931	4
62972	31663	7804	7854	40927	0
62973	25560	7817	7854	40928	1
62974	12363	7806	7854	40929	2
62975	23560	7813	7854	40930	3
62976	66777	7819	7854	40931	4
62977	23123240	7662	8395	40892	0
62978	50590	7667	8395	40893	1
62979	513382	7670	8395	40894	2
62980	57595	7673	8395	40895	3
62981	104378	7680	8395	40896	4
62982	456313	7686	8390	40897	0
62983	260689	7684	8390	40898	1
62984	271326	7691	8390	40899	2
62985	1157628	7693	8390	40900	3
62986	158775	7701	8390	40901	4
62987	127851	7706	8389	40902	0
62988	497725	7708	8389	40903	1
62989	96320	7715	8389	40904	2
62990	726988	7717	8389	40905	3
62991	375389	7719	8389	40906	4
62992	270478	7729	8394	40907	0
62993	17699	7726	8394	40908	1
62994	248530	7741	8394	40909	2
62995	261885	7734	8394	40910	3
62996	117495	7740	8394	40911	4
62997	108700	7744	8393	40912	0
62998	84245	7746	8393	40913	1
62999	337513	7761	8393	40914	2
63000	52152	7753	8393	40915	3
63001	55009	7760	8393	40916	4
63002	38019	7728	8429	40907	0
63003	17091	7725	8429	40908	1
63004	22216	7732	8429	40909	2
63005	23292	7735	8429	40910	3
63006	13527	7738	8429	40911	4
63007	12079	7762	8428	40917	0
63008	12229	7769	8428	40918	1
63009	44748	7773	8428	40919	2
63010	13238	7774	8428	40920	3
63011	28233	7779	8428	40921	4
63012	26271	7744	8427	40912	0
63013	16559	7748	8427	40913	1
63014	17181	7751	8427	40914	2
63015	22869	7753	8427	40915	3
63016	19518	7760	8427	40916	4
63017	39285	7804	8426	40927	0
63018	58869	7817	8426	40928	1
63019	25128	7809	8426	40929	2
63020	20551	7813	8426	40930	3
63021	15255	7819	8426	40931	4
63022	21352	7704	8425	40902	0
63023	7977	7708	8425	40903	1
63024	13424	7715	8425	40904	2
63025	25984	7717	8425	40905	3
63026	10757	7722	8425	40906	4
63027	79293	7704	8468	40902	0
63028	15746	7708	8468	40903	1
63029	5419	7715	8468	40904	2
63030	4759	7717	8468	40905	3
63031	4057	7722	8468	40906	4
63032	32036	7681	8629	40892	0
63033	73341	7665	8629	40893	1
63034	10957	7670	8629	40894	2
63035	45476	7673	8629	40895	3
63036	38118	7680	8629	40896	4
63037	22229	7728	8472	40907	0
63038	21716	7726	8472	40908	1
63039	208235	7732	8472	40909	2
63040	55826	7734	8472	40910	3
63041	30221	7738	8472	40911	4
63042	68122	7744	8471	40912	0
63043	28572	7749	8471	40913	1
63044	73596	7751	8471	40914	2
63045	15923	7753	8471	40915	3
63046	34692	7760	8471	40916	4
63047	27516	7686	8628	40897	0
63048	33565	7684	8628	40898	1
63049	18711	7691	8628	40899	2
63050	451754	7695	8628	40900	3
63051	53114	7701	8628	40901	4
63052	40173	7704	8627	40902	0
63053	15073	7708	8627	40903	1
63054	19577	7715	8627	40904	2
63055	14853	7717	8627	40905	3
63056	64169	7719	8627	40906	4
63057	343937	7699	8557	40897	0
63058	211802	7683	8557	40898	1
63059	502613	7691	8557	40899	2
63060	1389008	7694	8557	40900	3
63061	151169	7701	8557	40901	4
63062	38205	7728	8626	40907	0
63063	119503	7724	8626	40908	1
63064	41805	7731	8626	40909	2
63065	39511	7733	8626	40910	3
63066	33307	7738	8626	40911	4
63067	54461	7782	8625	40922	0
63068	29249	7787	8625	40923	1
63069	16524	7792	8625	40924	2
63070	29262	7790	8625	40925	3
63071	51567	7798	8625	40926	4
63072	55977	7744	8624	40912	0
63073	20129	7748	8624	40913	1
63074	93030	7751	8624	40914	2
63075	65727	7753	8624	40915	3
63076	110641	7760	8624	40916	4
63077	17045	7762	8467	40917	0
63078	100735	7768	8467	40918	1
63079	101717	7773	8467	40919	2
63080	14843	7775	8467	40920	3
63081	24779	7779	8467	40921	4
63082	24262	7803	8802	40927	0
63083	131069	7815	8802	40928	1
63084	66375	7806	8802	40929	2
63085	104411	7813	8802	40930	3
63086	103614	7818	8802	40931	4
63087	7348	7804	8255	40927	0
63088	19589	7817	8255	40928	1
63089	12133	7806	8255	40929	2
63090	10866	7811	8255	40930	3
63091	17058	7818	8255	40931	4
63092	24813	7762	8254	40917	0
63093	15332	7769	8254	40918	1
63094	61599	7770	8254	40919	2
63095	10042	7775	8254	40920	3
63096	22746	7779	8254	40921	4
63097	143902	7729	8556	40907	0
63098	54783	7727	8556	40908	1
63099	102890	7732	8556	40909	2
63100	253111	7733	8556	40910	3
63101	466796	7738	8556	40911	4
63102	138252	7785	8470	40922	0
63103	90379	7787	8470	40923	1
63104	22582	7792	8470	40924	2
63105	24066	7790	8470	40925	3
63106	142461	7798	8470	40926	4
63107	958381	7663	8553	40892	0
63108	322874	7666	8553	40893	1
63109	41787	7669	8553	40894	2
63110	12973	7673	8553	40895	3
63111	24535	7680	8553	40896	4
63112	159950	7662	8045	40892	0
63113	151819	7667	8045	40893	1
63114	17623	7670	8045	40894	2
63115	115628	7675	8045	40895	3
63116	404623	7680	8045	40896	4
63117	92578	7804	8469	40927	0
63118	560134	7815	8469	40928	1
63119	177013	7809	8469	40929	2
63120	115546	7813	8469	40930	3
63121	249060	7819	8469	40931	4
63122	19209	7704	8339	40902	0
63123	14004	7708	8339	40903	1
63124	3641	7715	8339	40904	2
63125	31504	7718	8339	40905	3
63126	206980	7722	8339	40906	4
63127	36692	7681	7875	40892	0
63128	25657	7665	7875	40893	1
63129	10228	7670	7875	40894	2
63130	14604	7673	7875	40895	3
63131	37307	7680	7875	40896	4
63132	69819	7705	7874	40902	0
63133	36640	7710	7874	40903	1
63134	20753	7715	7874	40904	2
63135	30178	7718	7874	40905	3
63136	32358	7720	7874	40906	4
63137	185105	7728	8338	40907	0
63138	311434	7725	8338	40908	1
63139	474554	7732	8338	40909	2
63140	161456	7733	8338	40910	3
63141	22089	7738	8338	40911	4
63142	104462	7785	8388	40922	0
63143	36770	7787	8388	40923	1
63144	62599	7801	8388	40924	2
63145	56039	7793	8388	40925	3
63146	7836	7800	8388	40926	4
63147	269184	7744	8335	40912	0
63148	664763	7748	8335	40913	1
63149	118194	7751	8335	40914	2
63150	43215	7753	8335	40915	3
63151	156245	7760	8335	40916	4
63152	79612	7762	8337	40917	0
63153	313281	7766	8337	40918	1
63154	321145	7772	8337	40919	2
63155	84946	7775	8337	40920	3
63156	165196	7681	8482	40892	0
63157	82576	7665	8482	40893	1
63158	36548	7669	8482	40894	2
63159	46646	7673	8482	40895	3
63160	30154	7680	8482	40896	4
63161	94808	7787	8334	40923	1
63162	85779	7792	8334	40924	2
63163	140429	7790	8334	40925	3
63164	20201	7798	8334	40926	4
63165	69815	7687	8481	40897	0
63166	50279	7684	8481	40898	1
63167	52712	7689	8481	40899	2
63168	47755	7695	8481	40900	3
63169	17545	7701	8481	40901	4
63170	52863	7697	7975	40907	0
63171	13926	7725	7975	40908	1
63172	72613	7732	7975	40909	2
63173	149952	7734	7975	40910	3
63174	26080	7738	7975	40911	4
63175	48647	7744	7979	40912	0
63176	25330	7749	7979	40913	1
63177	32132	7751	7979	40914	2
63178	21485	7754	7979	40915	3
63179	58005	7760	7979	40916	4
63180	176555	7804	8336	40927	0
63181	179840	7815	8336	40928	1
63182	472733	7808	8336	40929	2
63183	26696	7813	8336	40930	3
63184	132413	7819	8336	40931	4
63185	252931	7697	8480	40907	0
63186	23127	7725	8480	40908	1
63187	134083	7741	8480	40909	2
63188	22540	7735	8480	40910	3
63189	72698	7738	8480	40911	4
63190	529506	7728	8162	40907	0
63191	107099	7727	8162	40908	1
63192	125627	7732	8162	40909	2
63193	414957	7734	8162	40910	3
63194	124767	7738	8162	40911	4
63195	32390	7784	7873	40922	0
63196	47305	7787	7873	40923	1
63197	26875	7792	7873	40924	2
63198	30009	7790	7873	40925	3
63199	37365	7799	7873	40926	4
63200	23794	7744	7872	40912	0
63201	29794	7749	7872	40913	1
63202	28619	7761	7872	40914	2
63203	30144	7753	7872	40915	3
63204	22775	7760	7872	40916	4
63205	24209	7686	7871	40897	0
63206	18201	7685	7871	40898	1
63207	52187	7691	7871	40899	2
63208	71449	7694	7871	40900	3
63209	12624	7703	7871	40901	4
63210	14440	7762	7870	40917	0
63211	22887	7768	7870	40918	1
63212	27645	7770	7870	40919	2
63213	19982	7774	7870	40920	3
63214	33740	7778	7870	40921	4
63215	35854	7697	7869	40907	0
63216	24192	7724	7869	40908	1
63217	46910	7732	7869	40909	2
63218	16468	7733	7869	40910	3
63219	25308	7739	7869	40911	4
63220	66036	7663	8013	40892	0
63221	69634	7668	8013	40893	1
63222	27602	7672	8013	40894	2
63223	94085	7673	8013	40895	3
63224	97003	7680	8013	40896	4
63225	151058	7765	8392	40917	0
63226	58766	7768	8392	40918	1
63227	55191	7773	8392	40919	2
63228	4926033	7775	8392	40920	3
63229	33296	7779	8392	40921	4
63230	30573	7804	7868	40927	0
63231	27697	7817	7868	40928	1
63232	42178	7808	7868	40929	2
63233	31174	7811	7868	40930	3
63234	38506	7818	7868	40931	4
63235	472288	7744	8159	40912	0
63236	245547	7749	8159	40913	1
63237	254322	7751	8159	40914	2
63238	138533	7753	8159	40915	3
63239	137950	7760	8159	40916	4
63240	430846	7697	8411	40907	0
63241	254051	7725	8411	40908	1
63242	79753	7732	8411	40909	2
63243	201962	7734	8411	40910	3
63244	109091	7738	8411	40911	4
63245	98237	7704	8359	40902	0
63246	16966	7710	8359	40903	1
63247	17068	7715	8359	40904	2
63248	4929	7717	8359	40905	3
63249	20637	7719	8359	40906	4
63250	51422	7804	7977	40927	0
63251	113223	7817	7977	40928	1
63252	50177	7809	7977	40929	2
63253	91435	7811	7977	40930	3
63254	87882	7820	7977	40931	4
63255	8970	7762	7974	40917	0
63256	48840	7769	7974	40918	1
63257	110494	7770	7974	40919	2
63258	21603	7774	7974	40920	3
63259	22883	7779	7974	40921	4
63260	10222	7782	8588	40922	0
63261	10184	7787	8588	40923	1
63262	19371	7801	8588	40924	2
63263	20473	7790	8588	40925	3
63264	36590	7797	8588	40926	4
63265	9574	7762	8587	40917	0
63266	10135	7769	8587	40918	1
63267	55656	7770	8587	40919	2
63268	9456	7775	8587	40920	3
63269	35849	7778	8587	40921	4
63270	27533	7804	8586	40927	0
63271	28342	7817	8586	40928	1
63272	13044	7806	8586	40929	2
63273	44025	7811	8586	40930	3
63274	61748	7819	8586	40931	4
63275	1937720	7744	8010	40912	0
63276	35335	7748	8010	40913	1
63277	3043088	7751	8010	40914	2
63278	66795	7755	8010	40915	3
63279	30973	7760	8010	40916	4
63280	33513	7728	8735	40907	0
63281	13033	7725	8735	40908	1
63282	18232	7732	8735	40909	2
63283	31439	7735	8735	40910	3
63284	37747	7738	8735	40911	4
63285	34452	7744	8734	40912	0
63286	69913	7749	8734	40913	1
63287	70713	7750	8734	40914	2
63288	27154	7753	8734	40915	3
63289	62848	7758	8734	40916	4
63290	50461	7762	8410	40917	0
63291	607700	7769	8410	40918	1
63292	1505234	7770	8410	40919	2
63293	11568	7775	8410	40920	3
63294	40590	7779	8410	40921	4
63295	69606	7663	7978	40892	0
63296	90998	7666	7978	40893	1
63297	9385	7670	7978	40894	2
63298	37848	7673	7978	40895	3
63299	50977	7680	7978	40896	4
63300	40832	7704	8409	40902	0
63301	88133	7708	8409	40903	1
63302	203926	7715	8409	40904	2
63303	271117	7717	8409	40905	3
63304	78377	7719	8409	40906	4
63305	36007	7706	7973	40902	0
63306	121322	7708	7973	40903	1
63307	62078	7715	7973	40904	2
63308	13746	7717	7973	40905	3
63309	16991	7722	7973	40906	4
63310	164879	7765	8009	40917	0
63311	24564	7767	8009	40918	1
63312	141017	7770	8009	40919	2
63313	56806	7777	8009	40920	3
63314	93354	7779	8009	40921	4
63315	50776	7783	7976	40922	0
63316	72136	7787	7976	40923	1
63317	51536	7792	7976	40924	2
63318	15477	7796	7976	40925	3
63319	52078	7800	7976	40926	4
63320	131126	7785	8012	40922	0
63321	47278	7788	8012	40923	1
63322	36707	7792	8012	40924	2
63323	14622	7796	8012	40925	3
63324	58342	7799	8012	40926	4
63325	107607	7804	8011	40927	0
63326	113315	7815	8011	40928	1
63327	46826	7807	8011	40929	2
63328	33621	7810	8011	40930	3
63329	66981	7819	8011	40931	4
63330	52361	7762	8161	40917	0
63331	166121	7769	8161	40918	1
63332	198668	7773	8161	40919	2
63333	97555	7775	8161	40920	3
63334	52002	7779	8161	40921	4
63335	1755989	7803	8391	40927	0
63336	50324	7817	8391	40928	1
63337	66752	7807	8391	40929	2
63338	35338	7813	8391	40930	3
63339	70860	7821	8391	40931	4
63340	61321	7705	8683	40902	0
63341	17961	7708	8683	40903	1
63342	28936	7714	8683	40904	2
63343	46205	7717	8683	40905	3
63344	27783	7722	8683	40906	4
63345	563641	7681	8451	40892	0
63346	70988	7666	8451	40893	1
63347	63899	7669	8451	40894	2
63348	23592	7673	8451	40895	3
63349	61225	7680	8451	40896	4
63350	534516	7785	8685	40922	0
63351	77097	7787	8685	40923	1
63352	45626	7801	8685	40924	2
63353	139050	7790	8685	40925	3
63354	75352	7798	8685	40926	4
63355	96852	7705	8450	40902	0
63356	163929	7708	8450	40903	1
63357	323987	7715	8450	40904	2
63358	37412	7717	8450	40905	3
63359	20191	7722	8450	40906	4
63360	1692666	7784	8408	40922	0
63361	843146	7787	8408	40923	1
63362	1890722	7792	8408	40924	2
63363	63253	7790	8408	40925	3
63364	1979389	7797	8408	40926	4
63365	129666	7728	8449	40907	0
63366	8017	7726	8449	40908	1
63367	110553	7731	8449	40909	2
63368	132304	7733	8449	40910	3
63369	81399	7738	8449	40911	4
63370	184683	7765	8682	40917	0
63371	151943	7766	8682	40918	1
63372	86255	7770	8682	40919	2
63373	237640	7776	8682	40920	3
63374	103534	7779	8682	40921	4
63375	64989	7744	8448	40912	0
63376	87536	7749	8448	40913	1
63377	51879	7751	8448	40914	2
63378	37128	7753	8448	40915	3
63379	38369	7760	8448	40916	4
63380	10270	7762	8447	40917	0
63381	44712	7768	8447	40918	1
63382	47335	7773	8447	40919	2
63383	47544	7775	8447	40920	3
63384	22312	7779	8447	40921	4
63385	24118	7784	8446	40922	0
63386	56344	7787	8446	40923	1
63387	56431	7801	8446	40924	2
63388	20920	7796	8446	40925	3
63389	24344	7797	8446	40926	4
63390	55573	7804	8445	40927	0
63391	21960	7817	8445	40928	1
63392	19728	7806	8445	40929	2
63393	24344	7813	8445	40930	3
63394	62775	7818	8445	40931	4
63395	334494	7803	8684	40927	0
63396	275990	7817	8684	40928	1
63397	38651	7806	8684	40929	2
63398	118720	7810	8684	40930	3
63399	227156	7820	8684	40931	4
63400	730969	7804	8407	40927	0
63401	20617	7817	8407	40928	1
63402	733898	7806	8407	40929	2
63403	819770	7811	8407	40930	3
63404	273262	7820	8407	40931	4
63405	82930	7802	7995	40927	0
63406	37236	7815	7995	40928	1
63407	42695	7809	7995	40929	2
63408	14591	7813	7995	40930	3
63409	38030	7818	7995	40931	4
63410	82823	7704	8789	40902	0
63411	26458	7708	8789	40903	1
63412	16792	7715	8789	40904	2
63413	22776	7717	8789	40905	3
63414	16216	7722	8789	40906	4
63415	47686	7804	7963	40927	0
63416	27598	7817	7963	40928	1
63417	34813	7806	7963	40929	2
63418	73723	7811	7963	40930	3
63419	23819	7819	7963	40931	4
63420	34172	7784	7994	40922	0
63421	36560	7787	7994	40923	1
63422	29350	7792	7994	40924	2
63423	19596	7790	7994	40925	3
63424	26693	7800	7994	40926	4
63425	13290	7762	7993	40917	0
63426	28201	7768	7993	40918	1
63427	36723	7773	7993	40919	2
63428	11169	7775	7993	40920	3
63429	31765	7779	7993	40921	4
63430	1050690	7729	8785	40907	0
63431	13232	7727	8785	40908	1
63432	29392	7741	8785	40909	2
63433	23828	7735	8785	40910	3
63434	32769	7739	8785	40911	4
63435	168900	7744	7992	40912	0
63436	27725	7749	7992	40913	1
63437	56563	7761	7992	40914	2
63438	23546	7753	7992	40915	3
63439	81887	7760	7992	40916	4
63440	346989	7697	7998	40907	0
63441	9192	7726	7998	40908	1
63442	22576	7741	7998	40909	2
63443	40702	7733	7998	40910	3
63444	32097	7739	7998	40911	4
63445	376546	7706	7997	40902	0
63446	31852	7708	7997	40903	1
63447	37235	7715	7997	40904	2
63448	27849	7717	7997	40905	3
63449	10900	7722	7997	40906	4
63450	18722	7662	8323	40892	0
63451	38353	7665	8323	40893	1
63452	15192	7671	8323	40894	2
63453	24271	7674	8323	40895	3
63454	64681	7677	8323	40896	4
63455	86453	7785	7961	40922	0
63456	60085	7786	7961	40923	1
63457	32223	7792	7961	40924	2
63458	23956	7793	7961	40925	3
63459	69290	7797	7961	40926	4
63460	20860	7686	8322	40897	0
63461	22328	7682	8322	40898	1
63462	15328	7692	8322	40899	2
63463	22557	7695	8322	40900	3
63464	15376	7702	8322	40901	4
63465	22140	7686	7991	40897	0
63466	57909	7684	7991	40898	1
63467	44982	7691	7991	40899	2
63468	31735	7695	7991	40900	3
63469	13216	7700	7991	40901	4
63470	19630	7762	7962	40917	0
63471	26979	7768	7962	40918	1
63472	71748	7771	7962	40919	2
63473	21294	7775	7962	40920	3
63474	24734	7779	7962	40921	4
63475	25757	7704	8321	40902	0
63476	15478	7708	8321	40903	1
63477	21592	7714	8321	40904	2
63478	28977	7717	8321	40905	3
63479	15121	7721	8321	40906	4
63480	1241680	7744	8788	40912	0
63481	38063	7748	8788	40913	1
63482	29703	7761	8788	40914	2
63483	24647	7753	8788	40915	3
63484	66830	7760	8788	40916	4
63485	24308	7762	8784	40917	0
63486	40568	7769	8784	40918	1
63487	58389	7771	8784	40919	2
63488	12488	7775	8784	40920	3
63489	23360	7779	8784	40921	4
63490	33362	7728	8320	40907	0
63491	15584	7727	8320	40908	1
63492	29080	7741	8320	40909	2
63493	33287	7735	8320	40910	3
63494	12120	7739	8320	40911	4
63495	54403	7681	7996	40892	0
63496	23253	7667	7996	40893	1
63497	19505	7672	7996	40894	2
63498	14950	7673	7996	40895	3
63499	149208	7680	7996	40896	4
63500	26469	7785	8787	40922	0
63501	29695	7787	8787	40923	1
63502	31672	7792	8787	40924	2
63503	22327	7790	8787	40925	3
63504	26702	7798	8787	40926	4
63505	24800	7686	8695	40897	0
63506	20209	7683	8695	40898	1
63507	22514	7692	8695	40899	2
63508	22899	7694	8695	40900	3
63509	43678	7700	8695	40901	4
63510	24797	7744	8319	40912	0
63511	26875	7749	8319	40913	1
63512	21623	7761	8319	40914	2
63513	17441	7753	8319	40915	3
63514	48637	7760	8319	40916	4
63515	13572	7784	8318	40922	0
63516	23520	7787	8318	40923	1
63517	19880	7792	8318	40924	2
63518	32087	7796	8318	40925	3
63519	18016	7799	8318	40926	4
63520	11374	7765	8317	40917	0
63521	17167	7768	8317	40918	1
63522	23930	7773	8317	40919	2
63523	19577	7774	8317	40920	3
63524	25008	7780	8317	40921	4
63525	25173	7804	8316	40927	0
63526	14800	7817	8316	40928	1
63527	16648	7808	8316	40929	2
63528	18880	7813	8316	40930	3
63529	8792	7819	8316	40931	4
63530	859848	7804	8786	40927	0
63531	16279	7817	8786	40928	1
63532	58247	7806	8786	40929	2
63533	16351	7813	8786	40930	3
63534	14617	7819	8786	40931	4
63535	15769	7662	8701	40892	0
63536	18184	7668	8701	40893	1
63537	19976	7672	8701	40894	2
63538	20486	7673	8701	40895	3
63539	117399	7678	8701	40896	4
63540	53150	7704	8700	40902	0
63541	18907	7708	8700	40903	1
63542	16879	7715	8700	40904	2
63543	72072	7723	8700	40905	3
63544	11578	7722	8700	40906	4
63545	50933	7744	8694	40912	0
63546	18456	7748	8694	40913	1
63547	65163	7751	8694	40914	2
63548	11594	7753	8694	40915	3
63549	11635	7757	8694	40916	4
63550	17376	7785	8699	40922	0
63551	34645	7786	8699	40923	1
63552	18039	7792	8699	40924	2
63553	33640	7796	8699	40925	3
63554	15870	7800	8699	40926	4
63555	8542	7686	8379	40897	0
63556	3064	7684	8379	40898	1
63557	2956	7692	8379	40899	2
63558	3308	7694	8379	40900	3
63559	2588	7703	8379	40901	4
63560	43326	7762	8698	40917	0
63561	23729	7769	8698	40918	1
63562	12290	7770	8698	40919	2
63563	22414	7774	8698	40920	3
63564	22716	7778	8698	40921	4
63565	64693	7662	8375	40892	0
63566	63291	7666	8375	40893	1
63567	15048	7670	8375	40894	2
63568	37164	7673	8375	40895	3
63569	32960	7680	8375	40896	4
63570	19763	7704	8723	40902	0
63571	14875	7708	8723	40903	1
63572	20265	7715	8723	40904	2
63573	22049	7717	8723	40905	3
63574	19194	7722	8723	40906	4
63575	33055	7687	8044	40897	0
63576	99506	7685	8044	40898	1
63577	37792	7692	8044	40899	2
63578	27768	7695	8044	40900	3
63579	151987	7701	8044	40901	4
63580	49262	7704	8374	40902	0
63581	37164	7708	8374	40903	1
63582	17444	7715	8374	40904	2
63583	48496	7717	8374	40905	3
63584	20421	7722	8374	40906	4
63585	224670	7706	8043	40902	0
63586	35360	7710	8043	40903	1
63587	55953	7715	8043	40904	2
63588	40122	7717	8043	40905	3
63589	31744	7719	8043	40906	4
63590	73468	7728	8378	40907	0
63591	36044	7725	8378	40908	1
63592	26104	7732	8378	40909	2
63593	44124	7734	8378	40910	3
63594	31672	7738	8378	40911	4
63595	18091	7803	8158	40927	0
63596	21477	7817	8158	40928	1
63597	57782	7807	8158	40929	2
63598	52030	7810	8158	40930	3
63599	84246	7820	8158	40931	4
63600	50421	7744	8377	40912	0
63601	44079	7748	8377	40913	1
63602	44420	7751	8377	40914	2
63603	35047	7753	8377	40915	3
63604	38953	7760	8377	40916	4
63605	84833	7762	8373	40917	0
63606	22136	7769	8373	40918	1
63607	72216	7771	8373	40919	2
63608	20475	7775	8373	40920	3
63609	27849	7779	8373	40921	4
63610	81447	7804	8148	40927	0
63611	37879	7817	8148	40928	1
63612	36541	7809	8148	40929	2
63613	141963	7810	8148	40930	3
63614	66902	7820	8148	40931	4
63615	3075	7704	8555	40902	0
63616	10759	7708	8555	40903	1
63617	8843	7713	8555	40904	2
63618	6349	7717	8555	40905	3
63619	2650	7722	8555	40906	4
63620	17428	7762	8147	40917	0
63621	27172	7769	8147	40918	1
63622	55744	7771	8147	40919	2
63623	33383	7775	8147	40920	3
63624	53771	7778	8147	40921	4
63625	83441	7804	8376	40927	0
63626	39636	7817	8376	40928	1
63627	13512	7806	8376	40929	2
63628	19533	7813	8376	40930	3
63629	38131	7819	8376	40931	4
63630	441985	7697	8042	40907	0
63631	20928	7724	8042	40908	1
63632	101328	7732	8042	40909	2
63633	15848	7735	8042	40910	3
63634	48234	7740	8042	40911	4
63635	39589	7782	8150	40922	0
63636	78641	7787	8150	40923	1
63637	13928	7792	8150	40924	2
63638	41629	7790	8150	40925	3
63639	76378	7798	8150	40926	4
63640	51046	7744	8554	40912	0
63641	75516	7749	8554	40913	1
63642	61800	7751	8554	40914	2
63643	31790	7753	8554	40915	3
63644	72313	7760	8554	40916	4
63645	63767	7744	8146	40912	0
63646	85160	7749	8146	40913	1
63647	170272	7751	8146	40914	2
63648	30844	7753	8146	40915	3
63649	31059	7760	8146	40916	4
63650	73661	7663	8520	40892	0
63651	35354	7666	8520	40893	1
63652	23687	7670	8520	40894	2
63653	30048	7673	8520	40895	3
63654	35917	7680	8520	40896	4
63655	31852	7687	8519	40897	0
63656	50197	7683	8519	40898	1
63657	26305	7692	8519	40899	2
63658	14514	7695	8519	40900	3
63659	49103	7701	8519	40901	4
63660	11651	7762	8552	40917	0
63661	132645	7769	8552	40918	1
63662	327835	7773	8552	40919	2
63663	81791	7775	8552	40920	3
63664	18536	7779	8552	40921	4
63665	64230	7704	8518	40902	0
63666	12162	7708	8518	40903	1
63667	25250	7715	8518	40904	2
63668	39581	7717	8518	40905	3
63669	14214	7719	8518	40906	4
63670	71163	7728	8517	40907	0
63671	9590	7726	8517	40908	1
63672	30427	7741	8517	40909	2
63673	15848	7735	8517	40910	3
63674	19603	7738	8517	40911	4
63675	226063	7728	8149	40907	0
63676	11087	7726	8149	40908	1
63677	377900	7741	8149	40909	2
63678	95386	7734	8149	40910	3
63679	88771	7738	8149	40911	4
63680	21369	7784	8516	40922	0
63681	56763	7800	8516	40926	4
63682	17938	7704	8129	40902	0
63683	12076	7708	8129	40903	1
63684	22858	7713	8129	40904	2
63685	17159	7717	8129	40905	3
63686	12649	7722	8129	40906	4
63687	38035	7728	8128	40907	0
63688	15300	7725	8128	40908	1
63689	22991	7732	8128	40909	2
63690	97222	7734	8128	40910	3
63691	10093	7738	8128	40911	4
63692	25828	7744	8127	40912	0
63693	152320	7747	8127	40913	1
63694	17884	7751	8127	40914	2
63695	21972	7753	8127	40915	3
63696	37140	7760	8127	40916	4
63697	78711	7744	8515	40912	0
63698	12165	7784	8126	40922	0
63699	47252	7787	8126	40923	1
63700	36982	7792	8126	40924	2
63701	16734	7790	8126	40925	3
63702	67407	7798	8126	40926	4
63703	23045	7784	8372	40922	0
63704	64756	7787	8372	40923	1
63705	30304	7792	8372	40924	2
63706	51795	7790	8372	40925	3
63707	50676	7800	8372	40926	4
63708	11468	7762	8125	40917	0
63709	10427	7769	8125	40918	1
63710	27357	7773	8125	40919	2
63711	78772	7775	8125	40920	3
63712	21800	7779	8125	40921	4
63713	20887	7765	8514	40917	0
63714	53983	7768	8514	40918	1
63715	65397	7771	8514	40919	2
63716	59185	7775	8514	40920	3
63717	11649	7779	8514	40921	4
63718	27242	7804	8124	40927	0
63719	37466	7817	8124	40928	1
63720	10938	7806	8124	40929	2
63721	11827	7813	8124	40930	3
63722	18663	7818	8124	40931	4
63723	8705	7762	8623	40917	0
63724	20868	7769	8623	40918	1
63725	207910	7773	8623	40919	2
63726	12115	7775	8623	40920	3
63727	23723	7779	8623	40921	4
63728	77495	7804	8622	40927	0
63729	33228	7817	8622	40928	1
63730	25676	7806	8622	40929	2
63731	21077	7810	8622	40930	3
63732	36645	7818	8622	40931	4
63733	19115	7784	8424	40922	0
63734	17188	7795	8424	40923	1
63735	20428	7792	8424	40924	2
63736	34107	7790	8424	40925	3
63737	22334	7798	8424	40926	4
63738	34929	7686	8664	40897	0
63739	48522	7684	8664	40898	1
63740	18946	7689	8664	40899	2
63741	56520	7694	8664	40900	3
63742	26390	7702	8664	40901	4
63743	58087	7706	8661	40902	0
63744	27419	7708	8661	40903	1
63745	113544	7715	8661	40904	2
63746	24270	7717	8661	40905	3
63747	14942	7722	8661	40906	4
63748	36857	7728	8663	40907	0
63749	11707	7725	8663	40908	1
63750	23651	7732	8663	40909	2
63751	14439	7734	8663	40910	3
63752	17559	7738	8663	40911	4
63753	140813	7744	8662	40912	0
63754	31193	7748	8662	40913	1
63755	50219	7751	8662	40914	2
63756	37370	7753	8662	40915	3
63757	36403	7758	8662	40916	4
63758	21102	7784	8660	40922	0
63759	25926	7787	8660	40923	1
63760	20558	7792	8660	40924	2
63761	23138	7790	8660	40925	3
63762	35346	7800	8660	40926	4
63763	20798	7762	8659	40917	0
63764	14375	7769	8659	40918	1
63765	31730	7771	8659	40919	2
63766	21644	7775	8659	40920	3
63767	13320	7779	8659	40921	4
63768	41322	7804	8658	40927	0
63769	25477	7817	8658	40928	1
63770	26596	7806	8658	40929	2
63771	29270	7813	8658	40930	3
63772	18714	7818	8658	40931	4
63773	71237	7804	8500	40927	0
63774	16522	7817	8500	40928	1
63775	23654	7806	8500	40929	2
63776	11958	7813	8500	40930	3
63777	102443	7819	8500	40931	4
63778	94823	7662	8777	40892	0
63779	27423	7666	8777	40893	1
63780	26922	7670	8777	40894	2
63781	36882	7673	8777	40895	3
63782	50025	7680	8777	40896	4
63783	111965	7699	8778	40897	0
63784	12434	7684	8778	40898	1
63785	34444	7691	8778	40899	2
63786	23462	7695	8778	40900	3
63787	57452	7702	8778	40901	4
63788	172263	7705	8776	40902	0
63789	46376	7708	8776	40903	1
63790	53719	7714	8776	40904	2
63791	33280	7717	8776	40905	3
63792	41114	7720	8776	40906	4
63793	79387	7804	8095	40927	0
63794	37547	7817	8095	40928	1
63795	119368	7806	8095	40929	2
63796	23211	7813	8095	40930	3
63797	33042	7820	8095	40931	4
63798	16398	7706	8288	40902	0
63799	23292	7708	8288	40903	1
63800	19574	7715	8288	40904	2
63801	24193	7717	8288	40905	3
63802	23146	7721	8288	40906	4
63803	307848	7729	8775	40907	0
63804	50478	7725	8775	40908	1
63805	69399	7732	8775	40909	2
63806	30175	7733	8775	40910	3
63807	28689	7738	8775	40911	4
63808	80971	7728	8283	40907	0
63809	31125	7726	8283	40908	1
63810	90353	7732	8283	40909	2
63811	61666	7733	8283	40910	3
63812	52713	7738	8283	40911	4
63813	31532	7686	8094	40897	0
63814	116764	7684	8094	40898	1
63815	88055	7692	8094	40899	2
63816	55388	7694	8094	40900	3
63817	80069	7702	8094	40901	4
63818	41505	7744	8772	40912	0
63819	32331	7749	8772	40913	1
63820	101709	7751	8772	40914	2
63821	28747	7754	8772	40915	3
63822	37264	7760	8772	40916	4
63823	103258	7697	8093	40907	0
63824	122945	7725	8093	40908	1
63825	83459	7732	8093	40909	2
63826	52912	7735	8093	40910	3
63827	37130	7738	8093	40911	4
63828	13638	7762	8774	40917	0
63829	37528	7769	8774	40918	1
63830	27942	7772	8774	40919	2
63831	22085	7777	8774	40920	3
63832	61594	7779	8774	40921	4
63833	13758	7784	8253	40922	0
63834	13732	7787	8253	40923	1
63835	8409	7792	8253	40924	2
63836	13591	7790	8253	40925	3
63837	15898	7799	8253	40926	4
63838	27863	7728	8256	40907	0
63839	14680	7725	8256	40908	1
63840	10788	7732	8256	40909	2
63841	17347	7734	8256	40910	3
63842	9749	7738	8256	40911	4
63843	74638	7742	8092	40912	0
63844	167779	7748	8092	40913	1
63845	133420	7751	8092	40914	2
63846	122961	7753	8092	40915	3
63847	21784	7760	8092	40916	4
63848	36586	7705	8733	40902	0
63849	25236	7708	8733	40903	1
63850	24894	7715	8733	40904	2
63851	75791	7718	8733	40905	3
63852	27878	7722	8733	40906	4
63853	14412	7762	8091	40917	0
63854	16349	7769	8091	40918	1
63855	134667	7772	8091	40919	2
63856	129927	7775	8091	40920	3
63857	91006	7779	8091	40921	4
63858	11132	7762	8730	40917	0
63859	14590	7769	8730	40918	1
63860	30437	7773	8730	40919	2
63861	66410	7775	8730	40920	3
63862	31841	7779	8730	40921	4
63863	24060	7803	8732	40927	0
63864	26479	7817	8732	40928	1
63865	29662	7806	8732	40929	2
63866	31598	7812	8732	40930	3
63867	71351	7819	8732	40931	4
63868	88408	7785	8090	40922	0
63869	73341	7786	8090	40923	1
63870	42694	7801	8090	40924	2
63871	30425	7796	8090	40925	3
63872	98860	7800	8090	40926	4
63873	27748	7783	8731	40922	0
63874	35527	7786	8731	40923	1
63875	40489	7801	8731	40924	2
63876	16401	7796	8731	40925	3
63877	31042	7800	8731	40926	4
63878	16062	7784	8551	40922	0
63879	1614	7787	8551	40923	1
63880	1363	7789	8551	40924	2
63881	967	7794	8551	40925	3
63882	1918	7799	8551	40926	4
63883	121338	7704	8089	40902	0
63884	29227	7708	8089	40903	1
63885	28471	7715	8089	40904	2
63886	48425	7717	8089	40905	3
63887	23902	7719	8089	40906	4
63888	49483	7663	8088	40892	0
63889	84976	7666	8088	40893	1
63890	199487	7670	8088	40894	2
63891	46048	7673	8088	40895	3
63892	19487	7680	8088	40896	4
63893	80861	7662	7906	40892	0
63894	45672	7668	7906	40893	1
63895	17826	7670	7906	40894	2
63896	18033	7673	7906	40895	3
63897	25641	7680	7906	40896	4
63898	31157	7687	7902	40897	0
63899	30838	7685	7902	40898	1
63900	20921	7692	7902	40899	2
63901	30185	7694	7902	40900	3
63902	13811	7702	7902	40901	4
63903	100260	7804	8550	40927	0
63904	32287	7817	8550	40928	1
63905	73024	7807	8550	40929	2
63906	39498	7812	8550	40930	3
63907	46936	7819	8550	40931	4
63908	21890	7839	7853	40932	0
63909	25000	7842	7853	40933	1
63910	9224	7848	7853	40934	2
63911	32184	7857	7853	40935	3
63912	13381	7855	7853	40936	4
63913	149922	7841	8112	40932	0
63914	70597	7844	8112	40933	1
63915	26367	7848	8112	40934	2
63916	62984	7857	8112	40935	3
63917	18011	7855	8112	40936	4
63918	53615	7841	8271	40932	0
63919	145853	7844	8271	40933	1
63920	27779	7848	8271	40934	2
63921	58988	7857	8271	40935	3
63922	23040	7855	8271	40936	4
63923	19325	7859	8111	40937	0
63924	47341	7863	8111	40938	1
63925	33611	7866	8111	40939	2
63926	39388	7873	8111	40940	3
63927	11209	7876	8111	40941	4
63928	10013	7859	8270	40937	0
63929	49122	7863	8270	40938	1
63930	40457	7866	8270	40939	2
63931	46637	7870	8270	40940	3
63932	30728	7876	8270	40941	4
63933	15361	7841	8303	40932	0
63934	51225	7843	8303	40933	1
63935	23432	7848	8303	40934	2
63936	197846	7856	8303	40935	3
63937	26946	7855	8303	40936	4
63938	34026	7859	7852	40937	0
63939	46367	7863	7852	40938	1
63940	149736	7867	7852	40939	2
63941	230733	7873	7852	40940	3
63942	57144	7876	7852	40941	4
63943	262780	7859	8302	40937	0
63944	56104	7863	8302	40938	1
63945	49902	7866	8302	40939	2
63946	48045	7870	8302	40940	3
63947	101809	7876	8302	40941	4
63948	14227	7841	8646	40932	0
63949	27967	7844	8646	40933	1
63950	28209	7848	8646	40934	2
63951	2096144	7857	8646	40935	3
63952	37234	7855	8646	40936	4
63953	23524	7859	8645	40937	0
63954	304256	7863	8645	40938	1
63955	104192	7866	8645	40939	2
63956	13584	7871	8645	40940	3
63957	19529	7877	8645	40941	4
63958	15296	7841	8535	40932	0
63959	26754	7842	8535	40933	1
63960	110493	7848	8535	40934	2
63961	202507	7857	8535	40935	3
63962	30345	7855	8535	40936	4
63963	77692	7859	8534	40937	0
63964	42607	7863	8534	40938	1
63965	271035	7869	8534	40939	2
63966	423252	7870	8534	40940	3
63967	144660	7876	8534	40941	4
63968	10834	7841	8056	40932	0
63969	22678	7842	8056	40933	1
63970	12552	7848	8056	40934	2
63971	56041	7857	8056	40935	3
63972	14617	7855	8056	40936	4
63973	16816	7859	8055	40937	0
63974	107273	7863	8055	40938	1
63975	33552	7867	8055	40939	2
63976	108993	7870	8055	40940	3
63977	66317	7876	8055	40941	4
63978	6073	7841	8754	40932	0
63979	29432	7842	8754	40933	1
63980	84168	7848	8754	40934	2
63981	61151	7857	8754	40935	3
63982	96327	7853	8754	40936	4
63983	47483	7841	8604	40932	0
63984	214794	7842	8604	40933	1
63985	103567	7849	8604	40934	2
63986	54191	7857	8604	40935	3
63987	49751	7855	8604	40936	4
63988	71026	7841	8499	40932	0
63989	119897	7842	8499	40933	1
63990	54820	7848	8499	40934	2
63991	68564	7857	8499	40935	3
63992	31256	7855	8499	40936	4
63993	39682	7859	8498	40937	0
63994	54804	7864	8498	40938	1
63995	349944	7867	8498	40939	2
63996	45903	7870	8498	40940	3
63997	40470	7874	8498	40941	4
63998	52141	7841	8371	40932	0
63999	63800	7844	8371	40933	1
64000	24104	7848	8371	40934	2
64001	36687	7857	8371	40935	3
64002	23037	7855	8371	40936	4
64003	48873	7859	8370	40937	0
64004	37857	7863	8370	40938	1
64005	87179	7867	8370	40939	2
64006	124737	7870	8370	40940	3
64007	37964	7877	8370	40941	4
64008	35787	7835	7851	40942	0
64009	17411	7901	7851	40943	1
64010	71186	7424	7851	40944	2
64011	78323	7903	7851	40945	3
64012	75244	5932	7851	40946	4
64013	134352	7835	8301	40942	0
64014	55757	7901	8301	40943	1
64015	48084	6311	8301	40944	2
64016	45948	7905	8301	40945	3
64017	57986	5932	8301	40946	4
64018	42319	7835	8110	40942	0
64019	101482	7900	8110	40943	1
64020	94800	6311	8110	40944	2
64021	78443	7905	8110	40945	3
64022	61987	5931	8110	40946	4
64023	86009	7835	8269	40942	0
64024	74904	7901	8269	40943	1
64025	96835	6311	8269	40944	2
64026	59149	7905	8269	40945	3
64027	162747	7307	8269	40946	4
64028	78841	7880	8109	40947	0
64029	102816	7883	8109	40948	1
64030	60741	7886	8109	40949	2
64031	95302	7894	8109	40950	3
64032	39832	7889	8109	40951	4
64033	69908	7880	8268	40947	0
64034	80980	7883	8268	40948	1
64035	40313	7886	8268	40949	2
64036	104533	7894	8268	40950	3
64037	32541	7889	8268	40951	4
64038	12274	7841	7814	40932	0
64039	85752	7842	7814	40933	1
64040	37173	7847	7814	40934	2
64041	122390	7857	7814	40935	3
64042	25163	7855	7814	40936	4
64043	57868	7859	7813	40937	0
64044	127381	7863	7813	40938	1
64045	197203	7867	7813	40939	2
64046	15781	7870	7813	40940	3
64047	176949	7876	7813	40941	4
64048	10350	7841	8717	40932	0
64049	23750	7843	8717	40933	1
64050	15606	7848	8717	40934	2
64051	37231	7857	8717	40935	3
64052	83288	7855	8717	40936	4
64053	88948	7859	8716	40937	0
64054	43937	7863	8716	40938	1
64055	33831	7867	8716	40939	2
64056	38803	7870	8716	40940	3
64057	84134	7876	8716	40941	4
64058	463162	7835	8497	40942	0
64059	31404	7901	8497	40943	1
64060	69341	7424	8497	40944	2
64061	48468	7905	8497	40945	3
64062	34341	5932	8497	40946	4
64063	63566	7881	8496	40947	0
64064	58141	7882	8496	40948	1
64065	26915	7886	8496	40949	2
64066	28098	7892	8496	40950	3
64067	15183	7889	8496	40951	4
64068	263669	7835	7812	40942	0
64069	40318	7901	7812	40943	1
64070	216814	7424	7812	40944	2
64071	176455	7905	7812	40945	3
64072	98179	5931	7812	40946	4
64073	161606	7880	7811	40947	0
64074	38420	7883	7811	40948	1
64075	16625	7886	7811	40949	2
64076	53784	7894	7811	40950	3
64077	318254	7889	7811	40951	4
64078	39357	7835	8533	40942	0
64079	46822	7901	8533	40943	1
64080	397932	6313	8533	40944	2
64081	93899	7905	8533	40945	3
64082	456062	5933	8533	40946	4
64083	12174	7841	7867	40932	0
64084	25599	7842	7867	40933	1
64085	10603	7848	7867	40934	2
64086	46990	7857	7867	40935	3
64087	57186	7855	7867	40936	4
64088	32637	7859	7866	40937	0
64089	41999	7863	7866	40938	1
64090	54811	7869	7866	40939	2
64091	28405	7871	7866	40940	3
64092	41844	7877	7866	40941	4
64093	28293	7834	7865	40942	0
64094	61372	7898	7865	40943	1
64095	77660	6311	7865	40944	2
64096	42369	7905	7865	40945	3
64097	85799	5932	7865	40946	4
64098	62273	7880	8715	40947	0
64099	30692	7883	8715	40948	1
64100	25504	7886	8715	40949	2
64101	50677	7893	8715	40950	3
64102	54210	7889	8715	40951	4
64103	38012	7837	8714	40942	0
64104	84278	7898	8714	40943	1
64105	51337	7424	8714	40944	2
64106	72294	7902	8714	40945	3
64107	61586	5933	8714	40946	4
64108	35335	7835	8054	40942	0
64109	62910	7898	8054	40943	1
64110	81402	7424	8054	40944	2
64111	57632	7905	8054	40945	3
64112	55312	5932	8054	40946	4
64113	85101	7880	8053	40947	0
64114	34112	7883	8053	40948	1
64115	48575	7886	8053	40949	2
64116	35202	7893	8053	40950	3
64117	21127	7888	8053	40951	4
64118	65647	7880	8532	40947	0
64119	57219	7883	8532	40948	1
64120	43615	7896	8532	40949	2
64121	245681	7894	8532	40950	3
64122	34310	7889	8532	40951	4
64123	882681	7913	8108	40952	0
64124	13802	7915	8108	40953	1
64125	368475	7916	8108	40954	2
64126	18245	7925	8108	40955	3
64127	23639	7928	8108	40956	4
64128	28291	7841	7925	40932	0
64129	48003	7844	7925	40933	1
64130	15399	7848	7925	40934	2
64131	49615	7857	7925	40935	3
64132	25505	7855	7925	40936	4
64133	10037	7859	7923	40937	0
64134	29807	7863	7923	40938	1
64135	21232	7867	7923	40939	2
64136	16797	7871	7923	40940	3
64137	17394	7876	7923	40941	4
64138	60995	7299	8713	40957	0
64139	61818	7930	8713	40958	1
64140	13964	7937	8713	40959	2
64141	114298	7940	8713	40960	3
64142	23129	7944	8713	40961	4
64143	24264	7913	7850	40952	0
64144	14024	7915	7850	40953	1
64145	15300	7916	7850	40954	2
64146	19201	7925	7850	40955	3
64147	31236	7928	7850	40956	4
64148	41657	7299	7849	40957	0
64149	76060	7930	7849	40958	1
64150	13260	7937	7849	40959	2
64151	74956	7941	7849	40960	3
64152	29697	7944	7849	40961	4
64153	3035387	6452	7848	40962	0
64154	295284	6560	7848	40966	1
64155	29076	6437	7848	40963	2
64156	56224	7458	7848	40965	3
64157	68789	6575	7848	40964	4
64158	315802	7299	8107	40957	0
64159	340472	7930	8107	40958	1
64160	6375	7937	8107	40959	2
64161	9906	7940	8107	40960	3
64162	20958	7944	8107	40961	4
64163	28593	7880	7924	40947	0
64164	22651	7883	7924	40948	1
64165	23223	7886	7924	40949	2
64166	53443	7892	7924	40950	3
64167	17230	7889	7924	40951	4
64168	93602	7912	7922	40952	0
64169	13026	7915	7922	40953	1
64170	234845	7916	7922	40954	2
64171	22866	7925	7922	40955	3
64172	20792	7928	7922	40956	4
64173	126283	6452	8712	40962	0
64174	40095	6569	8712	40966	1
64175	51623	6437	8712	40963	2
64176	27653	7458	8712	40965	3
64177	67205	6575	8712	40964	4
64178	14752	7299	8267	40957	0
64179	66680	7930	8267	40958	1
64180	10259	7937	8267	40959	2
64181	170557	7941	8267	40960	3
64182	32175	7944	8267	40961	4
64183	54538	7912	8266	40952	0
64184	11968	7915	8266	40953	1
64185	93804	7916	8266	40954	2
64186	60585	7925	8266	40955	3
64187	44030	7928	8266	40956	4
64188	20527	7841	8681	40932	0
64189	36335	7843	8681	40933	1
64190	32262	7849	8681	40934	2
64191	50178	7857	8681	40935	3
64192	131722	7852	8681	40936	4
64193	302357	7859	8680	40937	0
64194	272922	7863	8680	40938	1
64195	226059	7866	8680	40939	2
64196	208049	7870	8680	40940	3
64197	163956	7877	8680	40941	4
64198	47147	7835	8679	40942	0
64199	147361	7901	8679	40943	1
64200	170643	6311	8679	40944	2
64201	88680	7905	8679	40945	3
64202	81255	5932	8679	40946	4
64203	90091	7457	7921	40962	0
64204	41098	6560	7921	40966	1
64205	72678	6437	7921	40963	2
64206	49210	7458	7921	40965	3
64207	53435	7491	7921	40964	4
64208	28549	7299	7920	40957	0
64209	26132	7930	7920	40958	1
64210	47158	7937	7920	40959	2
64211	51508	7941	7920	40960	3
64212	16769	7942	7920	40961	4
64213	34605	7835	7919	40942	0
64214	98727	7901	7919	40943	1
64215	79603	7424	7919	40944	2
64216	78624	7905	7919	40945	3
64217	36609	5932	7919	40946	4
64218	22181	7880	7847	40947	0
64219	24856	7883	7847	40948	1
64220	6535	7886	7847	40949	2
64221	14752	7894	7847	40950	3
64222	7788	7889	7847	40951	4
64223	52804	7783	8773	40922	0
64224	67748	7787	8773	40923	1
64225	40789	7801	8773	40924	2
64226	9650	7793	8773	40925	3
64227	57011	7799	8773	40926	4
64228	317366	7837	8644	40942	0
64229	24290	7898	8644	40943	1
64230	69937	6311	8644	40944	2
64231	62741	7902	8644	40945	3
64232	37007	5931	8644	40946	4
64233	286866	7804	8770	40927	0
64234	40782	7817	8770	40928	1
64235	10500	7806	8770	40929	2
64236	104454	7813	8770	40930	3
64237	248839	7821	8770	40931	4
64238	29510	7946	8643	40957	0
64239	32336	7930	8643	40958	1
64240	24049	7937	8643	40959	2
64241	40559	7941	8643	40960	3
64242	54764	7943	8643	40961	4
64243	25867	7880	8642	40947	0
64244	27298	7883	8642	40948	1
64245	51988	7897	8642	40949	2
64246	94222	7894	8642	40950	3
64247	16164	7889	8642	40951	4
64248	245662	7841	8769	40932	0
64249	71606	7843	8769	40933	1
64250	203547	7848	8769	40934	2
64251	164465	7857	8769	40935	3
64252	82538	7855	8769	40936	4
64253	9554	7841	8466	40932	0
64254	65689	7843	8466	40933	1
64255	19793	7848	8466	40934	2
64256	64600	7857	8466	40935	3
64257	24651	7855	8466	40936	4
64258	159303	7859	8465	40937	0
64259	37004	7863	8465	40938	1
64260	182560	7867	8465	40939	2
64261	45140	7870	8465	40940	3
64262	87437	7875	8465	40941	4
64263	440426	7880	8464	40947	0
64264	105005	7882	8464	40948	1
64265	23904	7886	8464	40949	2
64266	106892	7894	8464	40950	3
64267	95962	7889	8464	40951	4
64268	73422	7835	8463	40942	0
64269	47957	7901	8463	40943	1
64270	130448	7424	8463	40944	2
64271	101068	7902	8463	40945	3
64272	31573	5932	8463	40946	4
64273	25222	7299	8462	40957	0
64274	25437	7930	8462	40958	1
64275	18483	7937	8462	40959	2
64276	67727	7941	8462	40960	3
64277	28933	7944	8462	40961	4
64278	191550	7859	8768	40937	0
64279	116505	7863	8768	40938	1
64280	155277	7866	8768	40939	2
64281	151148	7871	8768	40940	3
64282	99449	7876	8768	40941	4
64283	474370	7835	8252	40942	0
64284	37269	7900	8252	40943	1
64285	42361	6313	8252	40944	2
64286	31454	7902	8252	40945	3
64287	53540	5931	8252	40946	4
64288	11277	7859	8753	40937	0
64289	25904	7864	8753	40938	1
64290	21705	7868	8753	40939	2
64291	10866	7873	8753	40940	3
64292	7190	7875	8753	40941	4
64293	136357	7880	8752	40947	0
64294	314328	7883	8752	40948	1
64295	126990	7886	8752	40949	2
64296	29764	7894	8752	40950	3
64297	23932	7889	8752	40951	4
64298	73002	7299	8751	40957	0
64299	69411	7930	8751	40958	1
64300	44472	7937	8751	40959	2
64301	41162	7941	8751	40960	3
64302	38738	7944	8751	40961	4
64303	105180	7834	8750	40942	0
64304	56371	7900	8750	40943	1
64305	110179	6311	8750	40944	2
64306	41205	7905	8750	40945	3
64307	49944	5932	8750	40946	4
64308	31588	7859	8355	40937	0
64309	1192157	7864	8355	40938	1
64310	2564280	7867	8355	40939	2
64311	118290	7871	8355	40940	3
64312	2024930	7876	8355	40941	4
64313	21675	7841	8353	40932	0
64314	49681	7843	8353	40933	1
64315	14830	7847	8353	40934	2
64316	41911	7857	8353	40935	3
64317	22376	7855	8353	40936	4
64318	44948	7841	7960	40932	0
64319	41536	7842	7960	40933	1
64320	19888	7848	7960	40934	2
64321	59401	7857	7960	40935	3
64322	37974	7855	7960	40936	4
64323	98246	7880	7959	40947	0
64324	50814	7883	7959	40948	1
64325	33175	7886	7959	40949	2
64326	63432	7892	7959	40950	3
64327	37019	7890	7959	40951	4
64328	30142	7859	7958	40937	0
64329	32837	7864	7958	40938	1
64330	32582	7867	7958	40939	2
64331	40486	7873	7958	40940	3
64332	30168	7874	7958	40941	4
64333	42677	7912	7957	40952	0
64334	46881	7915	7957	40953	1
64335	56839	7916	7957	40954	2
64336	54210	7925	7957	40955	3
64337	32530	7928	7957	40956	4
64338	46941	7913	8461	40952	0
64339	54710	7915	8461	40953	1
64340	46734	7916	8461	40954	2
64341	19564	7925	8461	40955	3
64342	28731	7928	8461	40956	4
64343	95385	7880	8300	40947	0
64344	71768	7882	8300	40948	1
64345	34797	7886	8300	40949	2
64346	12282	7894	8300	40950	3
64347	8338	7889	8300	40951	4
64348	89357	7841	8028	40932	0
64349	31469	7844	8028	40933	1
64350	40998	7848	8028	40934	2
64351	89851	7857	8028	40935	3
64352	77580	7855	8028	40936	4
64353	31276	7859	8027	40937	0
64354	186846	7863	8027	40938	1
64355	81873	7867	8027	40939	2
64356	27418	7873	8027	40940	3
64357	3584	7877	8027	40941	4
64358	40866	7835	8026	40942	0
64359	51554	7901	8026	40943	1
64360	262350	7424	8026	40944	2
64361	69152	7905	8026	40945	3
64362	113081	5933	8026	40946	4
64363	199842	7880	8025	40947	0
64364	220500	7883	8025	40948	1
64365	26097	7887	8025	40949	2
64366	91025	7894	8025	40950	3
64367	20364	7889	8025	40951	4
64368	624411	7912	8024	40952	0
64369	215485	7915	8024	40953	1
64370	33132	7916	8024	40954	2
64371	70068	7925	8024	40955	3
64372	79925	7926	8024	40956	4
64373	362427	7841	7830	40932	0
64374	50082	7843	7830	40933	1
64375	38185	7848	7830	40934	2
64376	35175	7857	7830	40935	3
64377	29257	7855	7830	40936	4
64378	21160	7299	8248	40957	0
64379	8434	7930	8248	40958	1
64380	7380	7937	8248	40959	2
64381	19817	7941	8248	40960	3
64382	20337	7944	8248	40961	4
64383	19252	7910	8247	40952	0
64384	13719	7915	8247	40953	1
64385	18718	7916	8247	40954	2
64386	16816	7925	8247	40955	3
64387	19045	7928	8247	40956	4
64388	16293	7841	7884	40932	0
64389	29671	7843	7884	40933	1
64390	14418	7848	7884	40934	2
64391	57340	7850	7884	40935	3
64392	15080	7855	7884	40936	4
64393	30371	7861	7883	40937	0
64394	17791	7864	7883	40938	1
64395	88586	7869	7883	40939	2
64396	49445	7873	7883	40940	3
64397	34814	7874	7883	40941	4
64398	394619	7457	8299	40962	0
64399	694429	6560	8299	40966	1
64400	27439	7453	8299	40963	2
64401	18524	7458	8299	40965	3
64402	107915	6576	8299	40964	4
64403	14060	7841	8442	40932	0
64404	28627	7842	8442	40933	1
64405	19090	7848	8442	40934	2
64406	31903	7857	8442	40935	3
64407	20769	7853	8442	40936	4
64408	36268	7835	7956	40942	0
64409	64795	7901	7956	40943	1
64410	125926	6313	7956	40944	2
64411	80899	7905	7956	40945	3
64412	59230	5933	7956	40946	4
64413	36700	7859	7829	40937	0
64414	51881	7863	7829	40938	1
64415	45326	7867	7829	40939	2
64416	55771	7873	7829	40940	3
64417	37430	7876	7829	40941	4
64418	118849	7299	7955	40957	0
64419	124352	7932	7955	40958	1
64420	25748	7935	7955	40959	2
64421	380397	7941	7955	40960	3
64422	19786	7944	7955	40961	4
64423	859891	7946	8298	40957	0
64424	177341	7933	8298	40958	1
64425	11440	7937	8298	40959	2
64426	44324	7941	8298	40960	3
64427	17169	7944	8298	40961	4
64428	79507	7457	8460	40962	0
64429	125163	6569	8460	40966	1
64430	271298	6439	8460	40963	2
64431	74434	7458	8460	40965	3
64432	207766	6577	8460	40964	4
64433	35436	7837	8352	40942	0
64434	155218	7901	8352	40943	1
64435	150158	6311	8352	40944	2
64436	798472	7905	8352	40945	3
64437	1059976	5931	8352	40946	4
64438	17052	7299	8052	40957	0
64439	87761	7930	8052	40958	1
64440	22840	7937	8052	40959	2
64441	38664	7941	8052	40960	3
64442	35855	7944	8052	40961	4
64443	23499	7880	7900	40947	0
64444	37975	7882	7900	40948	1
64445	34757	7897	7900	40949	2
64446	37187	7892	7900	40950	3
64447	18909	7890	7900	40951	4
64448	27960	7910	7899	40952	0
64449	29209	7915	7899	40953	1
64450	23885	7916	7899	40954	2
64451	35920	7925	7899	40955	3
64452	20207	7946	7898	40957	0
64453	65267	7933	7898	40958	1
64454	24441	7935	7898	40959	2
64455	72949	7939	7898	40960	3
64456	28810	7944	7898	40961	4
64457	109276	7457	7897	40962	0
64458	24488	6559	7897	40966	1
64459	14616	6438	7897	40963	2
64460	25222	7458	7897	40965	3
64461	42644	6577	7897	40964	4
64462	699302	7299	8351	40957	0
64463	77363	7933	8351	40958	1
64464	440354	7937	8351	40959	2
64465	424670	7941	8351	40960	3
64466	39804	7944	8351	40961	4
64467	196178	7834	7896	40942	0
64468	93642	7901	7896	40943	1
64469	69717	7424	7896	40944	2
64470	41887	7903	7896	40945	3
64471	30537	5933	7896	40946	4
64472	37092	6452	7972	40962	0
64473	16521	6569	7972	40966	1
64474	32986	6438	7972	40963	2
64475	22306	7458	7972	40965	3
64476	32622	6576	7972	40964	4
64477	162732	7881	8369	40947	0
64478	74093	7883	8369	40948	1
64479	58033	7886	8369	40949	2
64480	287342	7892	8369	40950	3
64481	31411	7889	8369	40951	4
64482	43914	7299	8549	40957	0
64483	368826	7930	8549	40958	1
64484	13031	7937	8549	40959	2
64485	478437	7940	8549	40960	3
64486	23992	7944	8549	40961	4
64487	57096	7299	8041	40957	0
64488	130119	7931	8041	40958	1
64489	10723	7937	8041	40959	2
64490	33685	7941	8041	40960	3
64491	30808	7944	8041	40961	4
64492	413561	6452	8040	40962	0
64493	6683	6560	8040	40966	1
64494	23318	6439	8040	40963	2
64495	38497	7458	8040	40965	3
64496	38734	6575	8040	40964	4
64497	70053	7912	8548	40952	0
64498	38476	7915	8548	40953	1
64499	519106	7916	8548	40954	2
64500	23908	7925	8548	40955	3
64501	255643	7928	8548	40956	4
64502	371372	7299	8368	40957	0
64503	454790	7931	8368	40958	1
64504	33698	7937	8368	40959	2
64505	90104	7941	8368	40960	3
64506	154600	7944	8368	40961	4
64507	106612	7859	8621	40937	0
64508	65792	7863	8621	40938	1
64509	72027	7867	8621	40939	2
64510	60775	7870	8621	40940	3
64511	50696	7876	8621	40941	4
64512	178646	7834	8783	40942	0
64513	39367	7901	8783	40943	1
64514	168996	6311	8783	40944	2
64515	66568	7902	8783	40945	3
64516	55812	7307	8783	40946	4
64517	71785	6452	8069	40962	0
64518	27288	6569	8069	40966	1
64519	47816	6438	8069	40963	2
64520	17552	7458	8069	40965	3
64521	43000	6576	8069	40964	4
64522	29850	7910	8068	40952	0
64523	12864	7915	8068	40953	1
64524	14544	7916	8068	40954	2
64525	25432	7925	8068	40955	3
64526	12343	7928	8068	40956	4
64527	50612	7457	8265	40962	0
64528	225648	6569	8265	40966	1
64529	280674	7453	8265	40963	2
64530	182740	7458	8265	40965	3
64531	117864	6576	8265	40964	4
64532	795323	7912	8367	40952	0
64533	33374	7915	8367	40953	1
64534	441940	7916	8367	40954	2
64535	339459	7925	8367	40955	3
64536	1319989	7928	8367	40956	4
64537	198277	7881	8620	40947	0
64538	187123	7883	8620	40948	1
64539	87507	7896	8620	40949	2
64540	397323	7894	8620	40950	3
64541	63955	7890	8620	40951	4
64542	29435	5910	8241	40957	0
64543	60396	7931	8241	40958	1
64544	9708	7937	8241	40959	2
64545	42470	7941	8241	40960	3
64546	7126	7943	8241	40961	4
64547	242829	7910	8641	40952	0
64548	16152	7915	8641	40953	1
64549	177728	7917	8641	40954	2
64550	26632	7925	8641	40955	3
64551	285170	7928	8641	40956	4
64552	78208	7457	8640	40962	0
64553	18976	6560	8640	40966	1
64554	25714	7453	8640	40963	2
64555	18504	7458	8640	40965	3
64556	23656	6575	8640	40964	4
64557	549032	7912	8619	40952	0
64558	18914	7915	8619	40953	1
64559	37716	7916	8619	40954	2
64560	86541	7925	8619	40955	3
64561	18624	7928	8619	40956	4
64562	139862	6452	7828	40962	0
64563	68966	6560	7828	40966	1
64564	1086289	6437	7828	40963	2
64565	108595	7458	7828	40965	3
64566	66041	7491	7828	40964	4
64567	40585	7838	8603	40932	0
64568	83466	7842	8603	40933	1
64569	14706	7849	8603	40934	2
64570	46836	7857	8603	40935	3
64571	127244	7855	8603	40936	4
64572	20143	7859	8598	40937	0
64573	25406	7865	8598	40938	1
64574	31804	7867	8598	40939	2
64575	20511	7870	8598	40940	3
64576	16530	7876	8598	40941	4
64577	57310	7835	8602	40942	0
64578	74916	7900	8602	40943	1
64579	78110	6311	8602	40944	2
64580	69055	7902	8602	40945	3
64581	41966	5932	8602	40946	4
64582	45941	7878	8601	40947	0
64583	44175	7883	8601	40948	1
64584	29216	7886	8601	40949	2
64585	32856	7894	8601	40950	3
64586	36877	7889	8601	40951	4
64587	85200	7912	8597	40952	0
64588	17935	7915	8597	40953	1
64589	27995	7916	8597	40954	2
64590	25056	7925	8597	40955	3
64591	22732	7928	8597	40956	4
64592	16590	7946	8600	40957	0
64593	34067	7930	8600	40958	1
64594	10112	7937	8600	40959	2
64595	55897	7939	8600	40960	3
64596	25680	7942	8600	40961	4
64597	29133	6452	8599	40962	0
64598	21093	6561	8599	40966	1
64599	14526	6438	8599	40963	2
64600	31709	7458	8599	40965	3
64601	29388	7491	8599	40964	4
64602	28062	7912	8729	40952	0
64603	15311	7915	8729	40953	1
64604	56985	7916	8729	40954	2
64605	25015	7925	8729	40955	3
64606	47784	7928	8729	40956	4
64607	32648	7912	7826	40952	0
64608	38194	7915	7826	40953	1
64609	145521	7916	7826	40954	2
64610	30455	7925	7826	40955	3
64611	46600	7928	7826	40956	4
64612	101331	7930	7827	40958	1
64613	23672	7937	7827	40959	2
64614	62264	7941	7827	40960	3
64615	33389	7944	7827	40961	4
64616	198114	7457	8354	40962	0
64617	268240	6559	8354	40966	1
64618	501261	6437	8354	40963	2
64619	45187	7458	8354	40965	3
64620	137239	7491	8354	40964	4
64621	17925	7859	8251	40937	0
64622	25906	7863	8251	40938	1
64623	40082	7867	8251	40939	2
64624	25608	7876	8251	40941	4
64625	7478	7841	8264	40932	0
64626	60051	7842	8264	40933	1
64627	19942	7849	8264	40934	2
64628	31097	7857	8264	40935	3
64629	14023	7855	8264	40936	4
64630	51173	7880	7846	40947	0
64631	87328	7883	7846	40948	1
64632	90992	7886	7846	40949	2
64633	78533	7894	7846	40950	3
64634	33757	7889	7846	40951	4
64635	109742	7841	8210	40932	0
64636	346298	7842	8210	40933	1
64637	8837	7848	8210	40934	2
64638	165670	7857	8210	40935	3
64639	17125	7855	8210	40936	4
64640	106478	7841	8639	40932	0
64641	85908	7844	8639	40933	1
64642	17509	7848	8639	40934	2
64643	86686	7857	8639	40935	3
64644	34267	7855	8639	40936	4
64645	14587	7841	8441	40932	0
64646	31540	7842	8441	40933	1
64647	21917	7847	8441	40934	2
64648	35892	7857	8441	40935	3
64649	17717	7855	8441	40936	4
64650	50293	7457	8493	40962	0
64651	28386	6569	8493	40966	1
64652	70312	6438	8493	40963	2
64653	43295	6457	8493	40965	3
64654	57948	7491	8493	40964	4
64655	1111490	7859	8638	40937	0
64656	56278	7863	8638	40938	1
64657	58262	7867	8638	40939	2
64658	166688	7870	8638	40940	3
64659	306059	7876	8638	40941	4
64660	59037	7835	8635	40942	0
64661	27084	7901	8635	40943	1
64662	84320	6311	8635	40944	2
64663	77585	7905	8635	40945	3
64664	113021	7307	8635	40946	4
64665	141970	7912	7845	40952	0
64666	23270	7915	7845	40953	1
64667	33953	7916	7845	40954	2
64668	33030	7925	7845	40955	3
64669	38054	7928	7845	40956	4
64670	48978	7841	8585	40932	0
64671	107687	7842	8585	40933	1
64672	39447	7848	8585	40934	2
64673	5884906	7857	8585	40935	3
64674	98073	7855	8585	40936	4
64675	238119	7880	8637	40947	0
64676	125262	7883	8637	40948	1
64677	25075	7886	8637	40949	2
64678	75809	7894	8637	40950	3
64679	26595	7889	8637	40951	4
64680	27290	7858	8436	40937	0
64681	15442	7863	8436	40938	1
64682	32877	7867	8436	40939	2
64683	10768	7870	8436	40940	3
64684	15339	7876	8436	40941	4
64685	39561	7880	8440	40947	0
64686	27744	7883	8440	40948	1
64687	20735	7886	8440	40949	2
64688	34733	7894	8440	40950	3
64689	14231	7889	8440	40951	4
64690	747040	7859	8209	40937	0
64691	167922	7863	8209	40938	1
64692	4381	7867	8209	40939	2
64693	65669	7871	8209	40940	3
64694	42764	7876	8209	40941	4
64695	177368	7299	8634	40957	0
64696	86094	7930	8634	40958	1
64697	14272	7937	8634	40959	2
64698	271594	7941	8634	40960	3
64699	24546	7944	8634	40961	4
64700	38604	7835	8208	40942	0
64701	124069	7901	8208	40943	1
64702	94521	7424	8208	40944	2
64703	49549	7902	8208	40945	3
64704	43033	7307	8208	40946	4
64705	58517	7835	7844	40942	0
64706	22544	7901	7844	40943	1
64707	58641	7424	7844	40944	2
64708	70456	7905	7844	40945	3
64709	34675	5932	7844	40946	4
64710	40270	7299	7842	40957	0
64711	32328	7930	7842	40958	1
64712	13247	7937	7842	40959	2
64713	46920	7941	7842	40960	3
64714	34691	7944	7842	40961	4
64715	17441	7910	8749	40952	0
64716	8031	7915	8749	40953	1
64717	19844	7920	8749	40954	2
64718	4204	7925	8749	40955	3
64719	10278	7928	8749	40956	4
64720	30690	7841	8423	40932	0
64721	456888	7844	8423	40933	1
64722	151398	7848	8423	40934	2
64723	141735	7850	8423	40935	3
64724	120167	7855	8423	40936	4
64725	120883	7881	8207	40947	0
64726	382144	7883	8207	40948	1
64727	1262283	7886	8207	40949	2
64728	40608	7894	8207	40950	3
64729	166573	7889	8207	40951	4
64730	20039	7910	8204	40952	0
64731	8568	7915	8204	40953	1
64732	21638	7916	8204	40954	2
64733	538750	7925	8204	40955	3
64734	17593	7928	8204	40956	4
64735	59705	7299	8206	40957	0
64736	77875	7930	8206	40958	1
64737	13248	7937	8206	40959	2
64738	52660	7941	8206	40960	3
64739	22609	7942	8206	40961	4
64740	18094	7835	8618	40942	0
64741	144115	7898	8618	40943	1
64742	27952	7424	8618	40944	2
64743	45986	7905	8618	40945	3
64744	93379	7307	8618	40946	4
64745	18805	7457	8205	40962	0
64746	85547	6560	8205	40966	1
64747	43750	6457	8205	40965	3
64748	67555	6575	8205	40964	4
64749	11338	7841	8350	40932	0
64750	978872	7842	8350	40933	1
64751	108917	7848	8350	40934	2
64752	270743	7850	8350	40935	3
64753	37786	7855	8350	40936	4
64754	66668	7913	8633	40952	0
64755	16583	7915	8633	40953	1
64756	79577	7917	8633	40954	2
64757	26101	7925	8633	40955	3
64758	293514	7299	8616	40957	0
64759	325652	7930	8616	40958	1
64760	53940	7937	8616	40959	2
64761	83543	7941	8616	40960	3
64762	33471	7944	8616	40961	4
64763	29959	6452	7843	40962	0
64764	46659	6560	7843	40966	1
64765	69412	6437	7843	40963	2
64766	66602	7458	7843	40965	3
64767	88289	7491	7843	40964	4
64768	291968	6452	8617	40962	0
64769	118349	6569	8617	40966	1
64770	136905	7453	8617	40963	2
64771	17809	7458	8617	40965	3
64772	118561	6576	8617	40964	4
64773	2427770	7859	8406	40937	0
64774	1022809	7863	8406	40938	1
64775	2139802	7867	8406	40939	2
64776	106594	7870	8406	40940	3
64777	921944	7876	8406	40941	4
64778	35318	7859	8346	40937	0
64779	595191	7863	8346	40938	1
64780	936620	7867	8346	40939	2
64781	80735	7870	8346	40940	3
64782	588677	7875	8346	40941	4
64783	576574	7834	8349	40942	0
64784	260936	7898	8349	40943	1
64785	346116	6313	8349	40944	2
64786	235647	7905	8349	40945	3
64787	224177	5933	8349	40946	4
64788	155945	7879	8348	40947	0
64789	71387	7883	8348	40948	1
64790	278026	7886	8348	40949	2
64791	274841	7892	8348	40950	3
64792	59017	7891	8348	40951	4
64793	243757	7912	8439	40952	0
64794	16445	7915	8439	40953	1
64795	23692	7916	8439	40954	2
64796	17941	7925	8439	40955	3
64797	19115	7928	8439	40956	4
64798	19229	7837	8438	40942	0
64799	17360	7901	8438	40943	1
64800	32843	7424	8438	40944	2
64801	33480	7902	8438	40945	3
64802	41602	5931	8438	40946	4
64803	394325	7910	8345	40952	0
64804	1019222	7915	8345	40953	1
64805	121196	7921	8345	40954	2
64806	345227	7925	8345	40955	3
64807	596847	7928	8345	40956	4
64808	32816	7457	8347	40962	0
64809	35508	6560	8347	40966	1
64810	468913	6438	8347	40963	2
64811	675912	7458	8347	40965	3
64812	94479	6577	8347	40964	4
64813	27354	7841	8531	40932	0
64814	62841	7844	8531	40933	1
64815	25996	7848	8531	40934	2
64816	41601	7857	8531	40935	3
64817	22655	7855	8531	40936	4
64818	56949	7859	8530	40937	0
64819	38891	7862	8530	40938	1
64820	33864	7867	8530	40939	2
64821	31219	7870	8530	40940	3
64822	33728	7876	8530	40941	4
64823	161109	7834	8529	40942	0
64824	67471	7901	8529	40943	1
64825	115703	6311	8529	40944	2
64826	53700	7905	8529	40945	3
64827	42938	5931	8529	40946	4
64828	90305	7880	8528	40947	0
64829	27019	7883	8528	40948	1
64830	41904	7887	8528	40949	2
64831	55556	7892	8528	40950	3
64832	18687	7889	8528	40951	4
64833	32571	7912	8527	40952	0
64834	20908	7915	8527	40953	1
64835	42837	7916	8527	40954	2
64836	15517	7925	8527	40955	3
64837	49310	7928	8527	40956	4
64838	60425	7930	8526	40958	1
64839	15427	7937	8526	40959	2
64840	44600	7941	8526	40960	3
64841	19578	7944	8526	40961	4
64842	15964	6452	8525	40962	0
64843	22228	6560	8525	40966	1
64844	23001	6437	8525	40963	2
64845	17479	7458	8525	40965	3
64846	79425	6576	8525	40964	4
64847	34807	7841	8171	40932	0
64848	53892	7843	8171	40933	1
64849	23363	7848	8171	40934	2
64850	147902	7857	8171	40935	3
64851	58338	7855	8171	40936	4
64852	6776	7841	8145	40932	0
64853	4737	7844	8145	40933	1
64854	3218	7848	8145	40934	2
64855	17879	7857	8145	40935	3
64856	60396	7855	8145	40936	4
64857	40250	7841	8479	40932	0
64858	59249	7842	8479	40933	1
64859	28828	7848	8479	40934	2
64860	79041	7857	8479	40935	3
64861	786335	7855	8479	40936	4
64862	43998	7860	8156	40937	0
64863	29954	7863	8156	40938	1
64864	44210	7867	8156	40939	2
64865	76992	7871	8156	40940	3
64866	15760	7875	8156	40941	4
64867	88724	7834	8144	40942	0
64868	96609	7901	8144	40943	1
64869	121374	7424	8144	40944	2
64870	74674	7905	8144	40945	3
64871	92576	7307	8144	40946	4
64872	189001	7881	8143	40947	0
64873	18871	7883	8143	40948	1
64874	46118	7886	8143	40949	2
64875	126096	7893	8143	40950	3
64876	75040	7891	8143	40951	4
64877	361152	7910	8155	40952	0
64878	34487	7915	8155	40953	1
64879	42068	7916	8155	40954	2
64880	26098	7925	8155	40955	3
64881	65586	7929	8155	40956	4
64882	123710	7859	8157	40937	0
64883	87303	7863	8157	40938	1
64884	36795	7866	8157	40939	2
64885	43010	7870	8157	40940	3
64886	51304	7876	8157	40941	4
64887	6222	7457	8748	40962	0
64888	6800	6569	8748	40966	1
64889	7596	7453	8748	40963	2
64890	6528	7458	8748	40965	3
64891	14718	7491	8748	40964	4
64892	17964	5910	8142	40957	0
64893	324706	7931	8142	40958	1
64894	18530	7937	8142	40959	2
64895	495257	7941	8142	40960	3
64896	27661	7942	8142	40961	4
64897	38139	7457	8154	40962	0
64898	28677	6561	8154	40966	1
64899	24653	6437	8154	40963	2
64900	39949	7458	8154	40965	3
64901	35162	6576	8154	40964	4
64902	319841	7858	8492	40937	0
64903	196225	7863	8492	40938	1
64904	599680	7867	8492	40939	2
64905	665365	7873	8492	40940	3
64906	856276	7876	8492	40941	4
64907	329876	7878	8170	40947	0
64908	208388	7885	8170	40948	1
64909	60870	7886	8170	40949	2
64910	56232	7894	8170	40950	3
64911	42952	7888	8170	40951	4
64912	13148	7839	8333	40932	0
64913	25120	7842	8333	40933	1
64914	10400	7848	8333	40934	2
64915	40229	7857	8333	40935	3
64916	20032	7855	8333	40936	4
64917	161023	7841	8105	40932	0
64918	125538	7844	8105	40933	1
64919	21391	7848	8105	40934	2
64920	95959	7850	8105	40935	3
64921	209997	7855	8105	40936	4
64922	1226780	7841	8801	40932	0
64923	14313	7842	8801	40933	1
64924	23960	7848	8801	40934	2
64925	43236	7857	8801	40935	3
64926	23483	7855	8801	40936	4
64927	66586	7834	8711	40942	0
64928	40488	7900	8711	40943	1
64929	92500	7424	8711	40944	2
64930	118524	7902	8711	40945	3
64931	140618	5932	8711	40946	4
64932	229193	7859	8104	40937	0
64933	769602	7863	8104	40938	1
64934	29612	7867	8104	40939	2
64935	30536	7870	8104	40940	3
64936	51451	7874	8104	40941	4
64937	31571	7859	8710	40937	0
64938	105218	7862	8710	40938	1
64939	12028	7866	8710	40939	2
64940	104188	7873	8710	40940	3
64941	26906	7876	8710	40941	4
64942	10529	7841	8708	40932	0
64943	12782	7844	8708	40933	1
64944	24479	7846	8708	40934	2
64945	59003	7857	8708	40935	3
64946	12158	7855	8708	40936	4
64947	1079727	7881	8103	40947	0
64948	32232	7883	8103	40948	1
64949	30096	7886	8103	40949	2
64950	104823	7893	8103	40950	3
64951	29743	7891	8103	40951	4
64952	13324	7839	8815	40932	0
64953	179195	7843	8815	40933	1
64954	76796	7846	8815	40934	2
64955	289228	7850	8815	40935	3
64956	121912	7855	8815	40936	4
64957	2717001	7861	8332	40937	0
64958	24562	7864	8332	40938	1
64959	29190	7869	8332	40939	2
64960	223896	7871	8332	40940	3
64961	34753	7877	8332	40941	4
64962	60840	7912	8174	40952	0
64963	97689	7915	8174	40953	1
64964	42911	7916	8174	40954	2
64965	28200	7925	8174	40955	3
64966	47935	7928	8174	40956	4
64967	36506	7860	8814	40937	0
64968	342810	7865	8814	40938	1
64969	164455	7866	8814	40939	2
64970	328210	7870	8814	40940	3
64971	77459	7876	8814	40941	4
64972	86601	7910	8101	40952	0
64973	201682	7915	8101	40953	1
64974	19320	7916	8101	40954	2
64975	180407	7924	8101	40955	3
64976	35522	7928	8101	40956	4
64977	2438893	7880	8495	40947	0
64978	45268	7883	8495	40948	1
64979	190490	7887	8495	40949	2
64980	2118676	7894	8495	40950	3
64981	49337	7890	8495	40951	4
64982	88039	7881	8584	40947	0
64983	69314	7883	8584	40948	1
64984	88656	7886	8584	40949	2
64985	6107035	7894	8584	40950	3
64986	34892	7889	8584	40951	4
64987	318863	7834	8813	40942	0
64988	291731	7901	8813	40943	1
64989	256860	6311	8813	40944	2
64990	134922	7902	8813	40945	3
64991	46436	5933	8813	40946	4
64992	50227	7881	8709	40947	0
64993	27448	7883	8709	40948	1
64994	14690	7886	8709	40949	2
64995	102589	7895	8709	40950	3
64996	8285	7889	8709	40951	4
64997	457096	7835	7918	40942	0
64998	180178	7899	7918	40943	1
64999	167938	6313	7918	40944	2
65000	245301	7905	7918	40945	3
65001	204431	5931	7918	40946	4
65002	19369	7912	8707	40952	0
65003	7077	7915	8707	40953	1
65004	9588	7917	8707	40954	2
65005	87205	7925	8707	40955	3
65006	24307	7928	8707	40956	4
65007	40314	7837	8331	40942	0
65008	57713	7901	8331	40943	1
65009	79456	6312	8331	40944	2
65010	28894	7905	8331	40945	3
65011	26808	5931	8331	40946	4
65012	27599	7804	8524	40927	0
65013	38188	7817	8524	40928	1
65014	28360	7806	8524	40929	2
65015	24647	7813	8524	40930	3
65016	14241	7820	8524	40931	4
65017	419884	7878	8330	40947	0
65018	36911	7884	8330	40948	1
65019	31537	7896	8330	40949	2
65020	20488	7892	8330	40950	3
65021	43681	7890	8330	40951	4
65022	39374	5910	8706	40957	0
65023	25550	7930	8706	40958	1
65024	11614	7937	8706	40959	2
65025	58587	7940	8706	40960	3
65026	17827	7944	8706	40961	4
65027	45035	7912	8329	40952	0
65028	19816	7915	8329	40953	1
65029	124423	7917	8329	40954	2
65030	16568	7925	8329	40955	3
65031	48089	7928	8329	40956	4
65032	8755	5910	8328	40957	0
65033	37809	7930	8328	40958	1
65034	23664	7936	8328	40959	2
65035	59296	7938	8328	40960	3
65036	85719	7944	8328	40961	4
65037	316663	7859	8800	40937	0
65038	48682	7865	8800	40938	1
65039	21836	7869	8800	40939	2
65040	152087	7871	8800	40940	3
65041	15467	7874	8800	40941	4
65042	66712	7835	8796	40942	0
65043	72078	7901	8796	40943	1
65044	101094	7424	8796	40944	2
65045	136313	7905	8796	40945	3
65046	86738	7307	8796	40946	4
65047	141237	7912	8579	40952	0
65048	134086	7915	8579	40953	1
65049	96697	7916	8579	40954	2
65050	72363	7925	8579	40955	3
65051	3952591	7928	8579	40956	4
65052	1571907	7881	8422	40947	0
65053	86487	7885	8422	40948	1
65054	110340	7886	8422	40949	2
65055	87921	7892	8422	40950	3
65056	49484	7890	8422	40951	4
65057	360483	7880	8799	40947	0
65058	62123	7883	8799	40948	1
65059	27824	7896	8799	40949	2
65060	198818	7894	8799	40950	3
65061	26104	7889	8799	40951	4
65062	40306	7841	8023	40932	0
65063	40273	7842	8023	40933	1
65064	22594	7848	8023	40934	2
65065	90297	7856	8023	40935	3
65066	98928	7855	8023	40936	4
65067	14005	7457	8705	40962	0
65068	67864	6561	8705	40966	1
65069	57810	6437	8705	40963	2
65070	18742	7458	8705	40965	3
65071	43211	6576	8705	40964	4
65072	250115	7912	8795	40952	0
65073	19301	7915	8795	40953	1
65074	208732	7920	8795	40954	2
65075	30097	7925	8795	40955	3
65076	127563	7928	8795	40956	4
65077	121838	7859	8022	40937	0
65078	62853	7864	8022	40938	1
65079	98350	7867	8022	40939	2
65080	162551	7870	8022	40940	3
65081	31142	7874	8022	40941	4
65082	10459	7946	8798	40957	0
65083	34917	7932	8798	40958	1
65084	13823	7935	8798	40959	2
65085	111257	7941	8798	40960	3
65086	27394	7942	8798	40961	4
65087	108048	6452	8797	40962	0
65088	18516	6561	8797	40966	1
65089	5888	7453	8797	40963	2
65090	7045	6457	8797	40965	3
65091	48083	6576	8797	40964	4
65092	196075	7837	8021	40942	0
65093	202158	7901	8021	40943	1
65094	133482	6311	8021	40944	2
65095	185300	7903	8021	40945	3
65096	93561	5933	8021	40946	4
65097	30336	7841	8141	40932	0
65098	30421	7844	8141	40933	1
65099	34858	7848	8141	40934	2
65100	39079	7857	8141	40935	3
65101	24347	7855	8141	40936	4
65102	60848	7859	8140	40937	0
65103	29376	7863	8140	40938	1
65104	58703	7866	8140	40939	2
65105	220365	7870	8140	40940	3
65106	79030	7876	8140	40941	4
65107	316734	7834	8173	40942	0
65108	76157	7901	8173	40943	1
65109	374468	6311	8173	40944	2
65110	329053	7903	8173	40945	3
65111	113016	5932	8173	40946	4
65112	72818	7910	8419	40952	0
65113	1421236	7915	8419	40953	1
65114	542134	7916	8419	40954	2
65115	376135	7922	8419	40955	3
65116	90042	7928	8419	40956	4
65117	100619	7835	8139	40942	0
65118	57991	7901	8139	40943	1
65119	47002	6313	8139	40944	2
65120	68826	7902	8139	40945	3
65121	56767	7307	8139	40946	4
65122	105119	7880	8767	40947	0
65123	59879	7884	8767	40948	1
65124	98266	7886	8767	40949	2
65125	305994	7894	8767	40950	3
65126	92305	7890	8767	40951	4
65127	129909	7880	8138	40947	0
65128	16009	7883	8138	40948	1
65129	23370	7886	8138	40949	2
65130	37071	7894	8138	40950	3
65131	28553	7888	8138	40951	4
65132	30937	7912	8137	40952	0
65133	14248	7915	8137	40953	1
65134	22239	7916	8137	40954	2
65135	17982	7925	8137	40955	3
65136	20200	7928	8137	40956	4
65137	992840	7878	8020	40947	0
65138	154370	7885	8020	40948	1
65139	220328	7895	8020	40950	3
65140	274824	7890	8020	40951	4
65141	115239	7299	8136	40957	0
65142	17109	7930	8136	40958	1
65143	18820	7937	8136	40959	2
65144	29560	7941	8136	40960	3
65145	16970	7944	8136	40961	4
65146	239873	7299	8583	40957	0
65147	97818	7930	8583	40958	1
65148	574138	7935	8583	40959	2
65149	447093	7939	8583	40960	3
65150	54582	7944	8583	40961	4
65151	25011	6452	8135	40962	0
65152	481759	6561	8135	40966	1
65153	180879	7453	8135	40963	2
65154	24331	7458	8135	40965	3
65155	35459	6575	8135	40964	4
65156	269312	7912	8019	40952	0
65157	88005	7915	8019	40953	1
65158	54453	7916	8019	40954	2
65159	34124	7925	8019	40955	3
65160	107189	7928	8019	40956	4
65161	1152003	6452	8567	40962	0
65162	271052	6569	8567	40966	1
65163	88557	6439	8567	40963	2
65164	5514434	7458	8567	40965	3
65165	117808	7491	8567	40964	4
65166	172376	7841	7917	40932	0
65167	125978	7842	7917	40933	1
65168	105230	7848	7917	40934	2
65169	30611	7850	7917	40935	3
65170	15698	7855	7917	40936	4
65171	56323	7299	8018	40957	0
65172	174666	7930	8018	40958	1
65173	43163	7937	8018	40959	2
65174	59059	7938	8018	40960	3
65175	39440	7943	8018	40961	4
65176	24534	7457	8017	40962	0
65177	27547	6560	8017	40966	1
65178	28382	7453	8017	40963	2
65179	64202	6457	8017	40965	3
65180	179984	6576	8017	40964	4
65181	19645	6452	8102	40962	0
65182	45939	6560	8102	40966	1
65183	50534	6439	8102	40963	2
65184	155177	6457	8102	40965	3
65185	54411	7491	8102	40964	4
65186	251784	7861	7916	40937	0
65187	265365	7863	7916	40938	1
65188	284044	7869	7916	40939	2
65189	1007852	7870	7916	40940	3
65190	175260	7877	7916	40941	4
65191	7112	7299	8435	40957	0
65192	3454	7930	8435	40958	1
65193	5319	7935	8435	40959	2
65194	5449	7941	8435	40960	3
65195	3745	7944	8435	40961	4
65196	16485	6452	8437	40962	0
65197	33692	6560	8437	40966	1
65198	24352	7453	8437	40963	2
65199	13266	6457	8437	40965	3
65200	34591	7491	8437	40964	4
65201	59000	7835	8478	40942	0
65202	41795	7901	8478	40943	1
65203	78848	6312	8478	40944	2
65204	135386	7905	8478	40945	3
65205	84537	5931	8478	40946	4
65206	7893558	7457	8327	40962	0
65207	17512	6560	8327	40966	1
65208	20209	6438	8327	40963	2
65209	34920	7458	8327	40965	3
65210	35907	6575	8327	40964	4
65211	24814	7835	8421	40942	0
65212	5452	7898	8421	40943	1
65213	24426	6313	8421	40944	2
65214	17771	7905	8421	40945	3
65215	3468	5931	8421	40946	4
65216	333152	7299	8100	40957	0
65217	48721	7930	8100	40958	1
65218	25059	7937	8100	40959	2
65219	173253	7941	8100	40960	3
65220	34544	7944	8100	40961	4
65221	43853	7913	8766	40952	0
65222	43470	7915	8766	40953	1
65223	27461	7920	8766	40954	2
65224	33661	7924	8766	40955	3
65225	50432	7928	8766	40956	4
65226	24670	6452	8008	40962	0
65227	67061	6569	8008	40966	1
65228	33409	7453	8008	40963	2
65229	17877	7458	8008	40965	3
65230	53533	7491	8008	40964	4
65231	56441	7858	8582	40937	0
65232	33866	7863	8582	40938	1
65233	48326	7867	8582	40939	2
65234	125933	7873	8582	40940	3
65235	2165772	7876	8582	40941	4
65236	11043	7299	8007	40957	0
65237	37879	7930	8007	40958	1
65238	24361	7937	8007	40959	2
65239	38164	7941	8007	40960	3
65240	22881	7944	8007	40961	4
65241	35268	7299	8491	40957	0
65242	143962	7932	8491	40958	1
65243	142616	7937	8491	40959	2
65244	148907	7941	8491	40960	3
65245	42847	7944	8491	40961	4
65246	121408	7912	8006	40952	0
65247	17839	7915	8006	40953	1
65248	54691	7921	8006	40954	2
65249	45515	7925	8006	40955	3
65250	46140	7928	8006	40956	4
65251	67855	7910	8494	40952	0
65252	35114	7915	8494	40953	1
65253	48397	7916	8494	40954	2
65254	85403	7924	8494	40955	3
65255	24110	7928	8494	40956	4
65256	45300	7880	8005	40947	0
65257	47530	7883	8005	40948	1
65258	30308	7896	8005	40949	2
65259	49108	7894	8005	40950	3
65260	27889	7891	8005	40951	4
65261	16705	6452	8578	40962	0
65262	24428	6569	8578	40966	1
65263	98299	6437	8578	40963	2
65264	36248	7458	8578	40965	3
65265	26719	6576	8578	40964	4
65266	33216	7834	8004	40942	0
65267	35308	7901	8004	40943	1
65268	91869	7424	8004	40944	2
65269	41562	7905	8004	40945	3
65270	19755	5932	8004	40946	4
65271	10416	7837	8581	40942	0
65272	25949	7901	8581	40943	1
65273	50759	7424	8581	40944	2
65274	73217	7905	8581	40945	3
65275	94387	7307	8581	40946	4
65276	10736	7859	8003	40937	0
65277	28462	7863	8003	40938	1
65278	27052	7869	8003	40939	2
65279	39684	7870	8003	40940	3
65280	38506	7875	8003	40941	4
65281	1190182	7834	8099	40942	0
65282	168386	7900	8099	40943	1
65283	184367	6312	8099	40944	2
65284	200744	7902	8099	40945	3
65285	70160	7307	8099	40946	4
65286	24516	7841	8002	40932	0
65287	22543	7844	8002	40933	1
65288	25317	7848	8002	40934	2
65289	73932	7850	8002	40935	3
65290	26507	7855	8002	40936	4
65291	183622	5910	8782	40957	0
65292	203222	7930	8782	40958	1
65293	78426	7937	8782	40959	2
65294	56778	7938	8782	40960	3
65295	37791	7942	8782	40961	4
65296	23131	7912	8513	40952	0
65297	19280	7915	8513	40953	1
65298	26005	7916	8513	40954	2
65299	17355	7925	8513	40955	3
65300	27360	7928	8513	40956	4
65301	2248026	7912	7915	40952	0
65302	21550	7915	7915	40953	1
65303	26705	7920	7915	40954	2
65304	28001	7925	7915	40955	3
65305	48111	7928	7915	40956	4
65306	41817	7299	8512	40957	0
65307	47250	7931	8512	40958	1
65308	20443	7937	8512	40959	2
65309	29089	7941	8512	40960	3
65310	23314	7944	8512	40961	4
65311	16223	6452	8511	40962	0
65312	18703	6569	8511	40966	1
65313	25417	6438	8511	40963	2
65314	33576	7458	8511	40965	3
65315	140541	7491	8511	40964	4
65316	997789	7879	7914	40947	0
65317	33078	7883	7914	40948	1
65318	21236	7886	7914	40949	2
65319	39293	7892	7914	40950	3
65320	15882	7891	7914	40951	4
65321	26759	7881	8250	40947	0
65322	34544	7885	8250	40948	1
65323	13602	7886	8250	40949	2
65324	131195	7894	8250	40950	3
65325	28470	7889	8250	40951	4
65326	13480	6452	8249	40962	0
65327	15286	6560	8249	40966	1
65328	34056	6437	8249	40963	2
65329	15013	7458	8249	40965	3
65330	42601	6577	8249	40964	4
65331	101870	6452	8172	40962	0
65332	181600	6569	8172	40966	1
65333	21562	6437	8172	40963	2
65334	40510	6457	8172	40965	3
65335	57345	6575	8172	40964	4
65336	46001	7835	8747	40942	0
65337	74392	7900	8747	40943	1
65338	148040	7424	8747	40944	2
65339	146637	7902	8747	40945	3
65340	113031	5932	8747	40946	4
65341	33647	7912	7882	40952	0
65342	15879	7915	7882	40953	1
65343	19337	7916	7882	40954	2
65344	29454	7925	7882	40955	3
65345	165605	7928	7882	40956	4
65346	90850	5909	8169	40957	0
65347	91645	7930	8169	40958	1
65348	26375	7937	8169	40959	2
65349	103522	7941	8169	40960	3
65350	55385	7943	8169	40961	4
65351	18188	7839	8566	40932	0
65352	121631	7843	8566	40933	1
65353	8835	7848	8566	40934	2
65354	64553	7850	8566	40935	3
65355	58263	7855	8566	40936	4
65356	61149	7880	7881	40947	0
65357	37272	7883	7881	40948	1
65358	96975	7886	7881	40949	2
65359	18785	7893	7881	40950	3
65360	22257	7891	7881	40951	4
65361	16672	7299	8746	40957	0
65362	59052	7933	8746	40958	1
65363	98024	7936	8746	40959	2
65364	196341	7938	8746	40960	3
65365	26351	7944	8746	40961	4
65366	122992	7859	8562	40937	0
65367	148508	7863	8562	40938	1
65368	32307	7866	8562	40939	2
65369	80472	7870	8562	40940	3
65370	10247	7876	8562	40941	4
65371	20525	5910	7880	40957	0
65372	33412	7930	7880	40958	1
65373	30970	7935	7880	40959	2
65374	36896	7941	7880	40960	3
65375	19472	7944	7880	40961	4
65376	187433	7881	8565	40947	0
65377	17542	7883	8565	40948	1
65378	114165	7886	8565	40949	2
65379	42261	7894	8565	40950	3
65380	21737	7889	8565	40951	4
65381	42441	7912	8564	40952	0
65382	92567	7915	8564	40953	1
65383	16441	7916	8564	40954	2
65384	17282	7925	8564	40955	3
65385	13271	7928	8564	40956	4
65386	18261	7457	8459	40962	0
65387	19690	6560	8459	40966	1
65388	7536	7453	8459	40963	2
65389	5776	7458	8459	40965	3
65390	24624	7491	8459	40964	4
65391	112040	5910	8420	40957	0
65392	120229	7930	8420	40958	1
65393	27402	7937	8420	40959	2
65394	129416	7938	8420	40960	3
65395	22954	7944	8420	40961	4
65396	74640	7299	8561	40957	0
65397	61149	7930	8561	40958	1
65398	18622	7937	8561	40959	2
65399	56457	7941	8561	40960	3
65400	24011	7944	8561	40961	4
65401	78461	7878	8678	40947	0
65402	71746	7883	8678	40948	1
65403	44027	7896	8678	40949	2
65404	84340	7892	8678	40950	3
65405	31087	7889	8678	40951	4
65406	14964	7457	8636	40962	0
65407	13673	6569	8636	40966	1
65408	22498	6437	8636	40963	2
65409	17062	7458	8636	40965	3
65410	37377	7491	8636	40964	4
65411	76647	7912	8677	40952	0
65412	26291	7918	8677	40953	1
65413	38752	7921	8677	40954	2
65414	34364	7925	8677	40955	3
65415	44999	7928	8677	40956	4
65416	60959	7299	8693	40957	0
65417	65289	7930	8693	40958	1
65418	39709	7937	8693	40959	2
65419	65376	7941	8693	40960	3
65420	25906	7942	8693	40961	4
65421	17648	7841	8742	40932	0
65422	38001	7844	8742	40933	1
65423	25850	7847	8742	40934	2
65424	50175	7857	8742	40935	3
65425	21990	7855	8742	40936	4
65426	82483	7859	8745	40937	0
65427	43654	7863	8745	40938	1
65428	34752	7866	8745	40939	2
65429	40203	7870	8745	40940	3
65430	32136	7876	8745	40941	4
65431	151270	7835	8563	40942	0
65432	72692	7901	8563	40943	1
65433	75423	7424	8563	40944	2
65434	73026	7902	8563	40945	3
65435	46583	7307	8563	40946	4
65436	20707	6452	7879	40962	0
65437	31054	6560	7879	40966	1
65438	35755	6437	7879	40963	2
65439	32729	7458	7879	40965	3
65440	189028	6577	7879	40964	4
65441	34375	7457	8676	40962	0
65442	24039	6560	8676	40966	1
65443	55945	7453	8676	40963	2
65444	42976	6457	8676	40965	3
65445	79771	6576	8676	40964	4
65446	17402	7457	8547	40962	0
65447	11316	6569	8547	40966	1
65448	5212	7453	8547	40963	2
65449	4395	7458	8547	40965	3
65450	6332	7491	8547	40964	4
65451	8236	7457	8240	40962	0
65452	15150	6560	8240	40966	1
65453	21498	7453	8240	40963	2
65454	17121	6457	8240	40965	3
65455	40333	7491	8240	40964	4
65456	2279	7457	8744	40962	0
65457	997	6569	8744	40966	1
65458	972	6439	8744	40963	2
65459	857	6457	8744	40965	3
65460	2716	7491	8744	40964	4
65461	5715	7299	7912	40957	0
65462	6219	7930	7912	40958	1
65463	12942	7937	7912	40959	2
65464	34179	7941	7912	40960	3
65465	17664	7944	7912	40961	4
65466	11435	7457	7913	40962	0
65467	16834	6569	7913	40966	1
65468	7085	7453	7913	40963	2
65469	12300	6457	7913	40965	3
65470	36671	6576	7913	40964	4
65471	130558	5952	7864	40967	0
65472	122631	5289	7864	40968	1
65473	85858	7588	7864	40969	2
65474	47706	6015	7864	40970	3
65475	78827	5472	7864	40971	4
65476	32837	7662	8215	40892	0
65477	37181	7667	8215	40893	1
65478	11125	7669	8215	40894	2
65479	52778	7673	8215	40895	3
65480	28559	7680	8215	40896	4
65481	727149	7686	8214	40897	0
65482	18571	7685	8214	40898	1
65483	20398	7692	8214	40899	2
65484	65040	7694	8214	40900	3
65485	11060	7700	8214	40901	4
65486	154095	5952	8282	40967	0
65487	107670	5289	8282	40968	1
65488	111545	6876	8282	40969	2
65489	92091	7334	8282	40970	3
65490	671868	5470	8282	40971	4
65491	58191	7314	8596	40967	0
65492	57735	5289	8596	40968	1
65493	31572	7588	8596	40969	2
65494	24228	7334	8596	40970	3
65495	25712	5470	8596	40971	4
65496	31703	7314	7936	40967	0
65497	37412	7106	7936	40968	1
65498	181340	7588	7936	40969	2
65499	273270	7334	7936	40970	3
65500	21820	5470	7936	40971	4
65501	14404	5952	8490	40967	0
65502	4194	5289	8490	40968	1
65503	14714	7588	8490	40969	2
65504	5777	7334	8490	40970	3
65505	5103	5471	8490	40971	4
65506	26894	5952	8657	40967	0
65507	72782	5289	8657	40968	1
65508	28593	7588	8657	40969	2
65509	94408	7334	8657	40970	3
65510	31377	5470	8657	40971	4
65511	59634	7314	7825	40967	0
65512	91325	5289	7825	40968	1
65513	50835	6876	7825	40969	2
65514	86575	7334	7825	40970	3
65515	65310	7166	7825	40971	4
65516	41983	7314	8263	40967	0
65517	33269	7106	8263	40968	1
65518	24381	6876	8263	40969	2
65519	20372	6015	8263	40970	3
65520	23603	7166	8263	40971	4
65521	76795	5952	8477	40967	0
65522	42004	5289	8477	40968	1
65523	23175	6876	8477	40969	2
65524	48932	7334	8477	40970	3
65525	88421	7166	8477	40971	4
65526	451619	5952	8546	40967	0
65527	147027	7106	8546	40968	1
65528	137548	7588	8546	40969	2
65529	524417	7334	8546	40970	3
65530	470313	5471	8546	40971	4
65531	54028	7912	8315	40952	0
65532	10213	7915	8315	40953	1
65533	11742	7916	8315	40954	2
65534	23074	7925	8315	40955	3
65535	10469	7928	8315	40956	4
65536	125031	5952	8314	40967	0
65537	87063	7106	8314	40968	1
65538	123533	6876	8314	40969	2
65539	138042	7334	8314	40970	3
65540	149399	5472	8314	40971	4
65541	52441	5954	7895	40967	0
65542	104405	5289	7895	40968	1
65543	118089	6876	7895	40969	2
65544	48346	6015	7895	40970	3
65545	27922	5471	7895	40971	4
65546	24714	7457	8106	40962	0
65547	11228	6569	8106	40966	1
65548	16261	7453	8106	40963	2
65549	5834	7458	8106	40965	3
65550	12085	6577	8106	40964	4
65551	75721	7314	7841	40967	0
65552	81437	7106	7841	40968	1
65553	25342	7588	7841	40969	2
65554	70450	6015	7841	40970	3
65555	88521	7166	7841	40971	4
65556	162321	5952	8366	40967	0
65557	955010	7588	8366	40969	2
65558	598234	7334	8366	40970	3
65559	1570640	5472	8366	40971	4
65560	72439	7314	8039	40967	0
65561	57122	7106	8039	40968	1
65562	24577	7588	8039	40969	2
65563	35350	7334	8039	40970	3
65564	27136	5471	8039	40971	4
65565	93545	5952	8067	40967	0
65566	76280	7106	8067	40968	1
65567	44311	7588	8067	40969	2
65568	77313	6015	8067	40970	3
65569	54288	7166	8067	40971	4
65570	104863	5952	8098	40967	0
65571	105664	7106	8098	40968	1
65572	36764	6876	8098	40969	2
65573	97046	6015	8098	40970	3
65574	64507	5471	8098	40971	4
65575	177040	5952	8615	40967	0
65576	47063	5289	8615	40968	1
65577	34839	7588	8615	40969	2
65578	92407	7334	8615	40970	3
65579	23296	5471	8615	40971	4
65580	5039038	5952	8794	40967	0
65581	50176	5289	8794	40968	1
65582	110079	7588	8794	40969	2
65583	48336	7334	8794	40970	3
65584	517840	5472	8794	40971	4
65585	57487	5952	8728	40967	0
65586	40202	7106	8728	40968	1
65587	38415	6876	8728	40969	2
65588	38293	6015	8728	40970	3
65589	76860	7166	8728	40971	4
65590	1517785	5952	8632	40967	0
65591	120090	7106	8632	40968	1
65592	601801	6876	8632	40969	2
65593	28851	7334	8632	40970	3
65594	781458	7166	8632	40971	4
65595	4582	7681	8168	40892	0
65596	2219	7665	8168	40893	1
65597	2812	7670	8168	40894	2
65598	1838	7674	8168	40895	3
65599	4391	7680	8168	40896	4
65600	6293	7705	8167	40902	0
65601	3357	7711	8167	40903	1
65602	2353	7712	8167	40904	2
65603	3109	7717	8167	40905	3
65604	2215	7721	8167	40906	4
65605	7002	7784	8160	40922	0
65606	3493	7787	8160	40923	1
65607	3202	7792	8160	40924	2
65608	1897	7793	8160	40925	3
65609	1937	7798	8160	40926	4
65610	199982	5952	8166	40967	0
65611	87729	7106	8166	40968	1
65612	36488	7588	8166	40969	2
65613	43944	7334	8166	40970	3
65614	99639	5472	8166	40971	4
65615	49131	5952	8123	40967	0
65616	28444	7106	8123	40968	1
65617	29940	6876	8123	40969	2
65618	16952	6017	8123	40970	3
65619	31113	7166	8123	40971	4
65620	240902	5952	7983	40967	0
65621	95440	5288	7983	40968	1
65622	71430	6874	7983	40969	2
65623	28553	7334	7983	40970	3
65624	31558	7166	7983	40971	4
65625	19212792	5952	7878	40967	0
65626	99077	7106	7878	40968	1
65627	60877	6876	7878	40969	2
65628	53419	7334	7878	40970	3
65629	244650	5471	7878	40971	4
65630	3655183	5952	7911	40967	0
65631	30230	5288	7911	40968	1
65632	32018	7588	7911	40969	2
65633	9756	6016	7911	40970	3
65634	5268	5471	7911	40971	4
65635	289307	7314	8765	40967	0
65636	96527	5289	8765	40968	1
65637	62790	6876	8765	40969	2
65638	360031	7334	8765	40970	3
65639	116592	5472	8765	40971	4
65640	172403	5952	8560	40967	0
65641	124659	5289	8560	40968	1
65642	101862	6876	8560	40969	2
65643	134335	6015	8560	40970	3
65644	209189	7166	8560	40971	4
65645	6498	5952	8134	40967	0
65646	3925	5289	8134	40968	1
65647	5742	7588	8134	40969	2
65648	7592	7334	8134	40970	3
65649	36852	7166	8134	40971	4
65650	188583	7744	8570	40912	0
65651	1127887	7749	8570	40913	1
65652	128790	7751	8570	40914	2
65653	99279	7753	8570	40915	3
65654	3952875	7760	8570	40916	4
65655	39522	5952	8704	40967	0
65656	25774	5289	8704	40968	1
65657	54664	6874	8704	40969	2
65658	18460	7334	8704	40970	3
65659	131020	5472	8704	40971	4
65660	52403	7913	8383	40952	0
65661	43448	7915	8383	40953	1
65662	31700	7920	8383	40954	2
65663	35922	7925	8383	40955	3
65664	32796	7928	8383	40956	4
65665	266314	5952	8203	40967	0
65666	98034	7106	8203	40968	1
65667	45596	7588	8203	40969	2
65668	102703	6015	8203	40970	3
65669	43252	5470	8203	40971	4
65670	96438	5954	8523	40967	0
65671	80677	5289	8523	40968	1
65672	34718	7588	8523	40969	2
65673	41011	7334	8523	40970	3
65674	600175	5472	8523	40971	4
65675	143653	7314	8382	40967	0
65676	98975	7106	8382	40968	1
65677	42323	7588	8382	40969	2
65678	35870	7334	8382	40970	3
65679	80819	5471	8382	40971	4
65680	56714	5952	8326	40967	0
65681	55002	5289	8326	40968	1
65682	40198	7588	8326	40969	2
65683	69912	6017	8326	40970	3
65684	29174	5472	8326	40971	4
65685	699889	7314	8398	40967	0
65686	570967	7106	8398	40968	1
65687	84507	6875	8398	40969	2
65688	25776	6015	8398	40970	3
65689	21411	5471	8398	40971	4
65690	20725	5952	8434	40967	0
65691	31903	7106	8434	40968	1
65692	16656	6876	8434	40969	2
65693	14511	7334	8434	40970	3
65694	60013	7166	8434	40971	4
65695	986384	7314	8577	40967	0
65696	193524	7106	8577	40968	1
65697	240255	6876	8577	40969	2
65698	214321	6015	8577	40970	3
65699	9010616	5472	8577	40971	4
65700	70307	5952	8510	40967	0
65701	106910	5288	8510	40968	1
65702	50447	6876	8510	40969	2
65703	32836	7334	8510	40970	3
65704	211340	5472	8510	40971	4
65705	163155	5952	8741	40967	0
65706	168049	7106	8741	40968	1
65707	23325	7588	8741	40969	2
65708	47253	7334	8741	40970	3
65709	99547	5472	8741	40971	4
65710	201492	5954	8418	40967	0
65711	75529	7106	8418	40968	1
65712	140129	6876	8418	40969	2
65713	91386	7334	8418	40970	3
65714	562976	5472	8418	40971	4
65715	1023487	5953	8692	40967	0
65716	1357466	7106	8692	40968	1
65717	75021	6876	8692	40969	2
65718	141908	7334	8692	40970	3
65719	1355330	7166	8692	40971	4
65720	68643	7427	8381	40972	0
65721	41276	7593	8381	40974	1
65722	60560	7498	8381	40976	2
65723	37548	7337	8381	40973	3
65724	82772	7475	8381	40975	4
65725	52252	7339	8380	40981	0
65726	10940	7660	8380	40978	1
65727	29845	7654	8380	40979	2
65728	47259	7518	8380	40980	3
65729	26752	7341	8380	40977	4
65730	31603	7427	7863	40972	0
65731	17160	7593	7863	40974	1
65732	25608	7498	7863	40976	2
65733	36109	6026	7863	40973	3
65734	12204	7475	7863	40975	4
65735	71776	7339	7862	40981	0
65736	15548	7660	7862	40978	1
65737	8108	7654	7862	40979	2
65738	58264	7518	7862	40980	3
65739	38197	7341	7862	40977	4
65740	20516	7427	7935	40972	0
65741	45573	7593	7935	40974	1
65742	71172	7498	7935	40976	2
65743	22959	7337	7935	40973	3
65744	48077	7475	7935	40975	4
65745	21725	7339	7934	40981	0
65746	45388	7660	7934	40978	1
65747	61322	7654	7934	40979	2
65748	23465	7518	7934	40980	3
65749	11504	7341	7934	40977	4
65750	56713	7427	8810	40972	0
65751	316596	7593	8810	40974	1
65752	173493	6599	8810	40976	2
65753	137437	7337	8810	40973	3
65754	51953	7475	8810	40975	4
65755	256527	7427	8313	40972	0
65756	60363	7593	8313	40974	1
65757	78156	7498	8313	40976	2
65758	10047	7337	8313	40973	3
65759	207087	7475	8313	40975	4
65760	138915	7427	8656	40972	0
65761	18499	7593	8656	40974	1
65762	28036	6598	8656	40976	2
65763	23264	7337	8656	40973	3
65764	33375	7475	8656	40975	4
65765	34676	7339	8655	40981	0
65766	10768	7660	8655	40978	1
65767	29361	7654	8655	40979	2
65768	408318	6659	8655	40980	3
65769	12413	7341	8655	40977	4
65770	1515550	7427	8343	40972	0
65771	34865	6889	8343	40974	1
65772	713590	7498	8343	40976	2
65773	130488	6024	8343	40973	3
65774	205138	7475	8343	40975	4
65775	80401	6332	8262	40972	0
65776	20242	7593	8262	40974	1
65777	53326	7498	8262	40976	2
65778	23808	7337	8262	40973	3
65779	32970	7475	8262	40975	4
65780	548815	7339	8261	40981	0
65781	6154	7660	8261	40978	1
65782	15541	7654	8261	40979	2
65783	10402	6657	8261	40980	3
65784	10350	7341	8261	40977	4
65785	468660	6031	8312	40981	0
65786	140411	7660	8312	40978	1
65787	163806	7654	8312	40979	2
65788	29726	7518	8312	40980	3
65789	186983	7341	8312	40977	4
65790	52977	6030	7894	40981	0
65791	24315	7091	7894	40978	1
65792	31020	7074	7894	40979	2
65793	20651	6659	7894	40980	3
65794	14826	6037	7894	40977	4
65795	12041	7427	7893	40972	0
65796	2479	6889	7893	40974	1
65797	2721	6599	7893	40976	2
65798	4048	7337	7893	40973	3
65799	9282	6509	7893	40975	4
65800	39519	7427	7840	40972	0
65801	32090	7593	7840	40974	1
65802	53186	7498	7840	40976	2
65803	35289	7337	7840	40973	3
65804	105043	6509	7840	40975	4
65805	19564	7339	7839	40981	0
65806	13977	7660	7839	40978	1
65807	19833	7654	7839	40979	2
65808	23053	7518	7839	40980	3
65809	19282	7341	7839	40977	4
65810	30617	7427	8281	40972	0
65811	37601	7593	8281	40974	1
65812	44779	7498	8281	40976	2
65813	28049	7337	8281	40973	3
65814	34135	7475	8281	40975	4
65815	27271	7339	8280	40981	0
65816	20088	7091	8280	40978	1
65817	93960	7654	8280	40979	2
65818	46731	7518	8280	40980	3
65819	48991	7341	8280	40977	4
65820	230472	7427	8545	40972	0
65821	173249	6889	8545	40974	1
65822	158435	7498	8545	40976	2
65823	148928	7337	8545	40973	3
65824	33622	7475	8545	40975	4
65825	310132	7339	8544	40981	0
65826	111898	7660	8544	40978	1
65827	20337	7654	8544	40979	2
65828	53937	7518	8544	40980	3
65829	58798	7341	8544	40977	4
65830	100751	7427	8489	40972	0
65831	100910	7593	8489	40974	1
65832	152837	7498	8489	40976	2
65833	51233	7337	8489	40973	3
65834	158515	7475	8489	40975	4
65835	26932	7427	8476	40972	0
65836	264657	7593	8476	40974	1
65837	15904	7498	8476	40976	2
65838	1334780	7337	8476	40973	3
65839	49663	7475	8476	40975	4
65840	102005	7339	8488	40981	0
65841	21123	7660	8488	40978	1
65842	28816	7654	8488	40979	2
65843	55150	6659	8488	40980	3
65844	23954	7341	8488	40977	4
65845	113678	7339	8038	40981	0
65846	86601	7660	8038	40978	1
65847	78558	7654	8038	40979	2
65848	26902	7518	8038	40980	3
65849	18344	6037	8038	40977	4
65850	21489	6333	8037	40972	0
65851	97564	7593	8037	40974	1
65852	54082	7498	8037	40976	2
65853	60243	7337	8037	40973	3
65854	32388	6511	8037	40975	4
65855	4992	7427	8066	40972	0
65856	3672	7593	8066	40974	1
65857	4792	7498	8066	40976	2
65858	3696	7337	8066	40973	3
65859	6784	7475	8066	40975	4
65860	9043	6333	8365	40972	0
65861	86791	7593	8365	40974	1
65862	58006	7498	8365	40976	2
65863	171208	7337	8365	40973	3
65864	48033	7475	8365	40975	4
65865	44723	6333	8631	40972	0
65866	534228	7593	8631	40974	1
65867	69518	7498	8631	40976	2
65868	54549	7337	8631	40973	3
65869	39976	7475	8631	40975	4
65870	53457	7427	8614	40972	0
65871	50678	6891	8614	40974	1
65872	209729	6599	8614	40976	2
65873	25173	7337	8614	40973	3
65874	115154	6511	8614	40975	4
65875	147304	7339	8613	40981	0
65876	39408	7660	8613	40978	1
65877	18004	7654	8613	40979	2
65878	65318	7518	8613	40980	3
65879	10995	7341	8613	40977	4
65880	19832	7339	8475	40981	0
65881	269468	7660	8475	40978	1
65882	4882	7654	8475	40979	2
65883	16082	7518	8475	40980	3
65884	15458	7341	8475	40977	4
65885	27266	6031	8364	40981	0
65886	53832	7660	8364	40978	1
65887	39069	7654	8364	40979	2
65888	92451	7518	8364	40980	3
65889	65527	6037	8364	40977	4
65890	13875	7339	8630	40981	0
65891	60450	7660	8630	40978	1
65892	27807	7654	8630	40979	2
65893	48877	7518	8630	40980	3
65894	11639	7341	8630	40977	4
65895	245661	7427	8164	40972	0
65896	111779	7593	8164	40974	1
65897	63386	7498	8164	40976	2
65898	153403	6025	8164	40973	3
65899	134232	7475	8164	40975	4
65900	119463	7339	8165	40981	0
65901	15305	7660	8165	40978	1
65902	28642	7654	8165	40979	2
65903	70392	7518	8165	40980	3
65904	24206	7341	8165	40977	4
65905	2423301	7427	8122	40972	0
65906	36456	7593	8122	40974	1
65907	129873	7498	8122	40976	2
65908	37304	7337	8122	40973	3
65909	59655	6509	8122	40975	4
65910	64634	6031	8121	40981	0
65911	6629	7660	8121	40978	1
65912	17119	7654	8121	40979	2
65913	23119	7518	8121	40980	3
65914	8796	7341	8121	40977	4
65915	64525	7427	7824	40972	0
65916	61152	7593	7824	40974	1
65917	99635	7498	7824	40976	2
65918	47386	7337	7824	40973	3
65919	82594	7475	7824	40975	4
65920	83573	6031	7823	40981	0
65921	32394	7660	7823	40978	1
65922	17001	7654	7823	40979	2
65923	28686	7518	7823	40980	3
65924	376238	7341	7823	40977	4
65925	335344	6332	8417	40972	0
65926	150795	6889	8417	40974	1
65927	57538	7498	8417	40976	2
65928	16608	7337	8417	40973	3
65929	31189	7475	8417	40975	4
65930	22328	6332	8433	40972	0
65931	22970	7593	8433	40974	1
65932	44404	7498	8433	40976	2
65933	27306	7337	8433	40973	3
65934	27158	7475	8433	40975	4
65935	23131	7339	8432	40981	0
65936	14180	7660	8432	40978	1
65937	20133	7654	8432	40979	2
65938	15893	6659	8432	40980	3
65939	13841	6037	8432	40977	4
65940	34084	7427	8764	40972	0
65941	1029669	7593	8764	40974	1
65942	75356	7498	8764	40976	2
65943	93126	7337	8764	40973	3
65944	149811	6511	8764	40975	4
65945	20501	6333	8703	40972	0
65946	23984	7593	8703	40974	1
65947	32757	7498	8703	40976	2
65948	126829	7337	8703	40973	3
65949	28551	6509	8703	40975	4
65950	21384	7339	8702	40981	0
65951	17851	7092	8702	40978	1
65952	14116	7072	8702	40979	2
65953	9114	6658	8702	40980	3
65954	10919	7341	8702	40977	4
65955	19929	6332	8325	40972	0
65956	36377	6889	8325	40974	1
65957	47113	6599	8325	40976	2
65958	25416	7337	8325	40973	3
65959	23340	6509	8325	40975	4
65960	30323	6030	8793	40981	0
65961	33923	7091	8793	40978	1
65962	18138	7654	8793	40979	2
65963	41936	6657	8793	40980	3
65964	49973	7341	8793	40977	4
65965	575058	6030	8324	40981	0
65966	62174	7092	8324	40978	1
65967	16901	7072	8324	40979	2
65968	13295	6658	8324	40980	3
65969	23668	6038	8324	40977	4
65970	33149	7427	8792	40972	0
65971	106556	7593	8792	40974	1
65972	19884	6599	8792	40976	2
65973	12749	7337	8792	40973	3
65974	58558	7475	8792	40975	4
65975	24363	7427	8740	40972	0
65976	42207	7593	8740	40974	1
65977	56866	7498	8740	40976	2
65978	55399	6024	8740	40973	3
65979	59709	6512	8740	40975	4
65980	508926	7339	8342	40981	0
65981	264258	7091	8342	40978	1
65982	14436	7074	8342	40979	2
65983	192974	6659	8342	40980	3
65984	33738	6037	8342	40977	4
65985	25947	7427	8595	40972	0
65986	58960	7593	8595	40974	1
65987	44621	6598	8595	40976	2
65988	27250	6024	8595	40973	3
65989	39374	6509	8595	40975	4
65990	7452	7427	8576	40972	0
65991	4962	7593	8576	40974	1
65992	4906	7498	8576	40976	2
65993	5053	7337	8576	40973	3
65994	21252	7475	8576	40975	4
65995	44191	5952	8666	40967	0
65996	36336	7106	8666	40968	1
65997	39280	6874	8666	40969	2
65998	39615	7334	8666	40970	3
65999	88529	5472	8666	40971	4
66000	97247	6333	8559	40972	0
66001	155536	7593	8559	40974	1
66002	226786	6599	8559	40976	2
66003	200417	7337	8559	40973	3
66004	216745	7475	8559	40975	4
66005	67020	7339	8575	40981	0
66006	61003	7660	8575	40978	1
66007	58002	7654	8575	40979	2
66008	123494	7518	8575	40980	3
66009	1688899	6037	8575	40977	4
66010	101866	6031	8558	40981	0
66011	111109	7660	8558	40978	1
66012	211130	7654	8558	40979	2
66013	160522	6659	8558	40980	3
66014	72633	7341	8558	40977	4
66015	8586	7339	8763	40981	0
66016	3877	7660	8763	40978	1
66017	5166	7654	8763	40979	2
66018	6062	7518	8763	40980	3
66019	6599	6036	8763	40977	4
66020	36052	7339	8416	40981	0
66021	28098	7660	8416	40978	1
66022	48671	7073	8416	40979	2
66023	36236	7518	8416	40980	3
66024	26085	6037	8416	40977	4
66025	267365	7339	8133	40981	0
66026	19867	7090	8133	40978	1
66027	18977	7654	8133	40979	2
66028	9136	7518	8133	40980	3
66029	10835	7341	8133	40977	4
66030	15988	7427	8132	40972	0
66031	24899	7593	8132	40974	1
66032	22056	7498	8132	40976	2
66033	15989	7337	8132	40973	3
66034	63867	7475	8132	40975	4
66035	509435	7427	8522	40972	0
66036	58565	7593	8522	40974	1
66037	60658	7498	8522	40976	2
66038	73387	7337	8522	40973	3
66039	34338	7475	8522	40975	4
66040	50009	7339	8521	40981	0
66041	6923	7660	8521	40978	1
66042	115081	7073	8521	40979	2
66043	21224	7518	8521	40980	3
66044	69007	7341	8521	40977	4
66045	18201	6031	8001	40981	0
66046	24534	7091	8001	40978	1
66047	24324	7072	8001	40979	2
66048	14151	6657	8001	40980	3
66049	74549	7341	8001	40977	4
66050	29013	7427	8000	40972	0
66051	29653	7593	8000	40974	1
66052	39504	6599	8000	40976	2
66053	17640	7337	8000	40973	3
66054	35031	7475	8000	40975	4
66055	57652	5952	7999	40967	0
66056	41790	5289	7999	40968	1
66057	85219	7588	7999	40969	2
66058	47144	6016	7999	40970	3
66059	11636	5471	7999	40971	4
66060	22697	6333	8691	40972	0
66061	70679	6891	8691	40974	1
66062	45583	6597	8691	40976	2
66063	41141	6024	8691	40973	3
66064	155459	6511	8691	40975	4
66065	24086	7339	8739	40981	0
66066	32909	7091	8739	40978	1
66067	17240	7074	8739	40979	2
66068	17968	6659	8739	40980	3
66069	52627	7341	8739	40977	4
66070	40669	7339	8690	40981	0
66071	110111	7092	8690	40978	1
66072	44697	7073	8690	40979	2
66073	18208	6659	8690	40980	3
66074	38751	6036	8690	40977	4
66075	8650	6333	7877	40972	0
66076	4583	7593	7877	40974	1
66077	5477	7498	7877	40976	2
66078	4773	7337	7877	40973	3
66079	7819	6512	7877	40975	4
66080	23685	7427	8097	40972	0
66081	106416	7593	8097	40974	1
66082	83434	6598	8097	40976	2
66083	127363	6024	8097	40973	3
66084	139335	6511	8097	40975	4
66085	85861	7339	7876	40981	0
66086	63013	7660	7876	40978	1
66087	23746	7654	7876	40979	2
66088	20555	6658	7876	40980	3
66089	23675	6037	7876	40977	4
66090	9861	7427	7910	40972	0
66091	11070	7593	7910	40974	1
66092	11862	6599	7910	40976	2
66093	8968	6024	7910	40973	3
66094	3817	7475	7910	40975	4
66095	9240	6031	7909	40981	0
66096	14148	7660	7909	40978	1
66097	14763	7072	7909	40979	2
66098	11057	6659	7909	40980	3
66099	14824	7341	7909	40977	4
66100	177982	7339	8096	40981	0
66101	20265	7660	8096	40978	1
66102	23480	7654	8096	40979	2
66103	129295	7518	8096	40980	3
66104	140591	7341	8096	40977	4
66105	152467	7697	8213	40907	0
66106	11966	7726	8213	40908	1
66107	72065	7741	8213	40909	2
66108	21553	7735	8213	40910	3
66109	42982	7738	8213	40911	4
66110	31136	7427	7971	40972	0
66111	106702	7593	7971	40974	1
66112	98884	7498	7971	40976	2
66113	65560	7337	7971	40973	3
66114	103869	6509	7971	40975	4
66115	111217	7339	7970	40981	0
66116	31418	7660	7970	40978	1
66117	26306	7654	7970	40979	2
66118	42289	6658	7970	40980	3
66119	36409	6037	7970	40977	4
66120	70224	7314	7969	40967	0
66121	112645	5289	7969	40968	1
66122	38386	7588	7969	40969	2
66123	123016	7334	7969	40970	3
66124	56076	5470	7969	40971	4
66125	20236	7339	8594	40981	0
66126	10848	7091	8594	40978	1
66127	11522	7654	8594	40979	2
66128	12837	6657	8594	40980	3
66129	90883	6037	8594	40977	4
66130	20672	7427	8727	40972	0
66131	42720	7593	8727	40974	1
66132	56283	7498	8727	40976	2
66133	21319	7337	8727	40973	3
66134	33252	7475	8727	40975	4
66135	39316	7339	8726	40981	0
66136	13205	7090	8726	40978	1
66137	9629	7654	8726	40979	2
66138	23174	7518	8726	40980	3
66139	13646	7341	8726	40977	4
66140	173406	7804	8580	40927	0
66141	69548	7817	8580	40928	1
66142	57224	7806	8580	40929	2
66143	103956	7810	8580	40930	3
66144	8121382	7819	8580	40931	4
66145	30440	6452	8415	40962	0
66146	20403	6560	8415	40966	1
66147	132349	6438	8415	40963	2
66148	18754	7458	8415	40965	3
66149	49471	6576	8415	40964	4
66150	38804	6332	7954	40972	0
66151	60633	7593	7954	40974	1
66152	59386	7498	7954	40976	2
66153	46002	7337	7954	40973	3
66154	58501	6511	7954	40975	4
66155	40009	6030	7953	40981	0
66156	24882	7660	7953	40978	1
66157	21619	7072	7953	40979	2
66158	51471	6657	7953	40980	3
66159	21968	7341	7953	40977	4
66160	70219	5952	7942	40967	0
66161	70219	5952	7942	40967	0
66162	64413	5289	7942	40968	1
66163	64413	5289	7942	40968	1
66164	39007	6874	7942	40969	2
66165	39007	6874	7942	40969	2
66166	70671	6015	7942	40970	3
66167	70671	6015	7942	40970	3
66168	40991	6452	7952	40962	0
66169	58079	6569	7952	40966	1
66170	57485	6438	7952	40963	2
66171	40258	6457	7952	40965	3
66172	39884	6576	7952	40964	4
66173	122974	5909	7951	40957	0
66174	80464	7932	7951	40958	1
66175	22508	7934	7951	40959	2
66176	90203	7941	7951	40960	3
66177	44908	7944	7951	40961	4
66178	58387	7910	7941	40952	0
66179	92871	7914	7941	40953	1
66180	155137	7921	7941	40954	2
66181	104949	7925	7941	40955	3
66182	41415	7928	7941	40956	4
66183	50239	7880	7950	40947	0
66184	34728	7883	7950	40948	1
66185	23786	7896	7950	40949	2
66186	43627	7894	7950	40950	3
66187	71293	7889	7950	40951	4
66188	33803	7858	7940	40937	0
66189	39909	7866	7940	40939	2
66190	36748	7871	7940	40940	3
66191	22966	7841	7948	40932	0
66192	45363	7843	7948	40933	1
66193	31418	7847	7948	40934	2
66194	75818	7856	7948	40935	3
66195	20689	7855	7948	40936	4
66196	40148	7804	7947	40927	0
66197	60767	7817	7947	40928	1
66198	45036	7809	7947	40929	2
66199	25983	7813	7947	40930	3
66200	22329	7819	7947	40931	4
66201	59136	7785	7939	40922	0
66202	99576	7795	7939	40923	1
66203	73055	7792	7939	40924	2
66204	24763	7796	7939	40925	3
66205	25154	7798	7939	40926	4
66206	19080	7762	7946	40917	0
66207	23340	7769	7946	40918	1
66208	80986	7771	7946	40919	2
66209	68359	7775	7946	40920	3
66210	31370	7778	7946	40921	4
66211	44116	7744	7945	40912	0
66212	47699	7749	7945	40913	1
66213	53578	7751	7945	40914	2
66214	30851	7753	7945	40915	3
66215	42265	7760	7945	40916	4
66216	20320	7725	7938	40908	1
66217	20605	7732	7938	40909	2
66218	30580	7735	7938	40910	3
66219	20166	7738	7938	40911	4
66220	64596	7706	7944	40902	0
66221	19842	7708	7944	40903	1
66222	32637	7714	7944	40904	2
66223	16869	7717	7944	40905	3
66224	15379	7722	7944	40906	4
66225	20357	7686	7943	40897	0
66226	16250	7685	7943	40898	1
66227	29872	7692	7943	40899	2
66228	27555	7695	7943	40900	3
66229	22866	7662	7937	40892	0
66230	25039	7666	7937	40893	1
66231	80622	7671	7937	40894	2
66232	25190	7673	7937	40895	3
66233	17481	7680	7937	40896	4
66234	17965	7427	8202	40972	0
66235	39973	7593	8202	40974	1
66236	64093	7498	8202	40976	2
66237	16329	7337	8202	40973	3
66238	11141	7475	8202	40975	4
66239	22167	7339	8201	40981	0
66240	16360	7660	8201	40978	1
66241	15128	7654	8201	40979	2
66242	44530	6659	8201	40980	3
66243	20896	6037	8201	40977	4
66244	42774	7686	8079	40897	0
66245	134424	7683	8079	40898	1
66246	46586	7691	8079	40899	2
66247	2593056	7693	8079	40900	3
66248	1094841	7702	8079	40901	4
66249	1963022	7744	8078	40912	0
66250	33035	7748	8078	40913	1
66251	86439	7751	8078	40914	2
66252	44725	7753	8078	40915	3
66253	121286	7760	8078	40916	4
66254	393458	7912	8087	40952	0
66255	2350185	7915	8087	40953	1
66256	45490	7921	8087	40954	2
66257	1006851	7925	8087	40955	3
66258	2465177	7928	8087	40956	4
66259	32381	7785	8086	40922	0
66260	110253	7787	8086	40923	1
66261	60826	7792	8086	40924	2
66262	102656	7790	8086	40925	3
66263	761106	7798	8086	40926	4
66264	1038348	7681	8077	40892	0
66265	114883	7667	8077	40893	1
66266	27827	7670	8077	40894	2
66267	42239	7673	8077	40895	3
66268	69422	7677	8077	40896	4
66269	38449	7762	8085	40917	0
66270	116209	7767	8085	40918	1
66271	1397961	7770	8085	40919	2
66272	23942	7775	8085	40920	3
66273	441917	7778	8085	40921	4
66274	344035	7805	8084	40927	0
66275	598722	7814	8084	40928	1
66276	85797	7806	8084	40929	2
66277	490362	7813	8084	40930	3
66278	91423	7818	8084	40931	4
66279	30478	6030	8189	40981	0
66280	11440	7091	8189	40978	1
66281	18096	7654	8189	40979	2
66282	22264	6659	8189	40980	3
66283	20017	7341	8189	40977	4
66284	15571	7427	8188	40972	0
66285	78304	6891	8188	40974	1
66286	70152	7498	8188	40976	2
66287	28608	7337	8188	40973	3
66288	164848	7475	8188	40975	4
66289	79107	7314	8187	40967	0
66290	78986	5287	8187	40968	1
66291	67886	6876	8187	40969	2
66292	30233	7334	8187	40970	3
66293	177200	5470	8187	40971	4
66294	26098	7457	8186	40962	0
66295	30623	6569	8186	40966	1
66296	8368	6438	8186	40963	2
66297	19736	6457	8186	40965	3
66298	120681	7491	8186	40964	4
66299	43780	7299	8185	40957	0
66300	92887	7930	8185	40958	1
66301	16328	7937	8185	40959	2
66302	608000	7941	8185	40960	3
66303	22841	7945	8185	40961	4
66304	86977	7912	8184	40952	0
66305	12288	7918	8184	40953	1
66306	22857	7921	8184	40954	2
66307	147064	7925	8184	40955	3
66308	23824	7928	8184	40956	4
66309	256930	5952	8344	40967	0
66310	163188	5288	8344	40968	1
66311	298943	7588	8344	40969	2
66312	203230	7334	8344	40970	3
66313	3992633	5472	8344	40971	4
66314	14558	7946	8074	40957	0
66315	18304	7931	8074	40958	1
66316	95752	7935	8074	40959	2
66317	20767	7941	8074	40960	3
66318	15505	7944	8074	40961	4
66319	26537	7841	8073	40932	0
66320	53023	7842	8073	40933	1
66321	63720	7847	8073	40934	2
66322	96520	7857	8073	40935	3
66323	41913	7855	8073	40936	4
66324	1172874	7878	8076	40947	0
66325	145592	7883	8076	40948	1
66326	217000	7896	8076	40949	2
66327	304432	7894	8076	40950	3
66328	46977	7890	8076	40951	4
66329	72705	7859	8083	40937	0
66330	78952	7862	8083	40938	1
66331	76416	7869	8083	40939	2
66332	149176	7870	8083	40940	3
66333	30928	7874	8083	40941	4
66334	387152	7803	7901	40927	0
66335	171503	7817	7901	40928	1
66336	83733	7809	7901	40929	2
66337	46507	7813	7901	40930	3
66338	103499	7821	7901	40931	4
66339	736182	7706	7908	40902	0
66340	463817	7708	7908	40903	1
66341	11801	7713	7908	40904	2
66342	10233	7717	7908	40905	3
66343	36539	7719	7908	40906	4
66344	120464	7728	7905	40907	0
66345	17798	7725	7905	40908	1
66346	23603	7732	7905	40909	2
66347	26077	7734	7905	40910	3
66348	20696	7739	7905	40911	4
66349	19090	7744	7904	40912	0
66350	21668	7749	7904	40913	1
66351	24812	7751	7904	40914	2
66352	42427	7753	7904	40915	3
66353	1086164	7760	7904	40916	4
66354	7571	7762	7907	40917	0
66355	17051	7769	7907	40918	1
66356	31649	7773	7907	40919	2
66357	10271	7775	7907	40920	3
66358	20619	7778	7907	40921	4
66359	348893	6452	8082	40962	0
66360	70532	6560	8082	40966	1
66361	45564	7453	8082	40963	2
66362	25072	7458	8082	40965	3
66363	579305	7491	8082	40964	4
66364	790285	7728	8072	40907	0
66365	21023	7725	8072	40908	1
66366	136511	7732	8072	40909	2
66367	60695	7735	8072	40910	3
66368	55586	7738	8072	40911	4
66369	104687	7704	8081	40902	0
66370	43385	7708	8081	40903	1
66371	1504444	7712	8081	40904	2
66372	435730	7717	8081	40905	3
66373	324526	7720	8081	40906	4
66374	7405	7859	8454	40937	0
66375	245495	7863	8454	40938	1
66376	14496	7869	8454	40939	2
66377	16479	7872	8454	40940	3
66378	8266	7876	8454	40941	4
66379	30668	7835	8444	40942	0
66380	34712	7900	8444	40943	1
66381	151344	6311	8444	40944	2
66382	8808	7905	8444	40945	3
66383	39312	5931	8444	40946	4
66384	91972	7880	8443	40947	0
66385	61000	7882	8443	40948	1
66386	15520	7896	8443	40949	2
66387	23760	7893	8443	40950	3
66388	23393	7888	8443	40951	4
66389	77151	7913	8453	40952	0
66390	8015	7915	8453	40953	1
66391	38704	7920	8453	40954	2
66392	62905	7925	8453	40955	3
66393	6448	7928	8453	40956	4
66394	16661	7299	8458	40957	0
66395	112952	7930	8458	40958	1
66396	31104	7935	8458	40959	2
66397	51271	7941	8458	40960	3
66398	9825	7944	8458	40961	4
66399	31197	7314	8457	40967	0
66400	40904	6874	8457	40969	2
66401	95855	7334	8457	40970	3
66402	19968	5472	8457	40971	4
66403	28744	6333	8456	40972	0
66404	57743	7593	8456	40974	1
66405	13128	7498	8456	40976	2
66406	10840	7337	8456	40973	3
66407	32505	7475	8456	40975	4
66408	115573	6031	8455	40981	0
66409	30767	7660	8455	40978	1
66410	54161	7654	8455	40979	2
66411	8672	7518	8455	40980	3
66412	19656	6037	8455	40977	4
66413	41498	6333	8071	40972	0
66414	108667	7593	8071	40974	1
66415	100722	7498	8071	40976	2
66416	895441	7337	8071	40973	3
66417	48283	7475	8071	40975	4
66418	141497	7835	8075	40942	0
66419	1450290	7901	8075	40943	1
66420	858755	6312	8075	40944	2
66421	352088	7905	8075	40945	3
66422	48621	5932	8075	40946	4
66423	124443	7881	8183	40947	0
66424	72096	7883	8183	40948	1
66425	29152	7886	8183	40949	2
66426	60568	7894	8183	40950	3
66427	94360	7889	8183	40951	4
66428	53237	7835	8178	40942	0
66429	63976	7901	8178	40943	1
66430	100992	6311	8178	40944	2
66431	279543	7905	8178	40945	3
66432	23296	5932	8178	40946	4
66433	21132	7860	8182	40937	0
66434	36887	7863	8182	40938	1
66435	55104	7869	8182	40939	2
66436	188897	7870	8182	40940	3
66437	13976	7874	8182	40941	4
66438	44959	7839	8181	40932	0
66439	89998	7844	8181	40933	1
66440	10144	7848	8181	40934	2
66441	26584	7850	8181	40935	3
66442	34944	7855	8181	40936	4
66443	255900	7802	8177	40927	0
66444	50144	7815	8177	40928	1
66445	19903	7809	8177	40929	2
66446	49881	7813	8177	40930	3
66447	27104	7818	8177	40931	4
66448	31347	7912	8738	40952	0
66449	18899	7918	8738	40953	1
66450	46346	7916	8738	40954	2
66451	52552	7925	8738	40955	3
66452	27365	7928	8738	40956	4
66453	76941	7765	8180	40917	0
66454	28752	7768	8180	40918	1
66455	90920	7770	8180	40919	2
66456	19056	7775	8180	40920	3
66457	117408	7778	8180	40921	4
66458	17573	7782	8179	40922	0
66459	21039	7786	8179	40923	1
66460	17592	7792	8179	40924	2
66461	90559	7796	8179	40925	3
66462	15385	7800	8179	40926	4
66463	80924	7879	8743	40947	0
66464	129162	7882	8743	40948	1
66465	103792	7886	8743	40949	2
66466	150975	7892	8743	40950	3
66467	59518	7889	8743	40951	4
66468	52548	6032	8070	40981	0
66469	24207	7091	8070	40978	1
66470	45856	7654	8070	40979	2
66471	61856	6658	8070	40980	3
66472	27448	6037	8070	40977	4
\.


--
-- Data for Name: questions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.questions (id, content, number_of_answers, number_of_correct, title, status, creation_date, course_id, key) FROM stdin;
1339	A utility tree	98	79	UtilityTree	AVAILABLE	2019-10-22 20:19:30.843479	2	1339
1382	Designing an architecture	91	71	IterativeDesign	AVAILABLE	\N	2	1382
1786	The main tactic associated with the aspects architectural style is:	62	30	AspectsTactics	AVAILABLE	\N	2	1786
1612	With which context of the Architecture Influence Cycle is the Conway's Law is related?	117	72	Conway's Law	AVAILABLE	2019-10-04 18:48:39.272942	2	1612
1809	A response measure of a performance scenario is	156	141	PerformanceOne	AVAILABLE	2019-10-02 23:21:49.816404	2	1809
1441	What was the main goal of the GHC's authors that led them to the design decision described in the previous question?	0	0	GHCBlockLayerQualitiesINGLES	DISABLED	\N	2	1441
1442	The modifiability tactic Use an Intermediary between two modules	134	98	ModifiabilityOne	AVAILABLE	2019-10-23 15:55:56.472234	2	1442
1532	The quality(ies) that is(are) more relevant to views of the component-and-connector viewtype is(are):	82	67	ComponentViewType	AVAILABLE	2019-11-21 15:19:46.178664	2	1532
1587	When comparing Amazon Silk with Google Chrome in the context of the prediction of pages the user is going to access	66	47	SilkPredictor	AVAILABLE	\N	2	1587
1484	The Identity Map pattern is typically used in enterprise applications	0	0	IdentityMap	REMOVED	\N	2	1484
1340	A requirement for a chess game is that it keeps a table with the best scores obtained in the game. Naturally, this information should be kept between two different executions of the system. Assuming that the game is a web-based application, then	167	79	Repository	AVAILABLE	\N	2	1340
1315	A heartbeat monitor	62	33	PingEcho	AVAILABLE	2019-10-22 20:25:00.632343	2	1315
1316	An availability tactic to prevent faults is	99	64	AvailabilityTwo	AVAILABLE	2019-10-23 22:44:10.444379	2	1316
1317	Assume that one of the requirements for a graphical chess game is that it should be able to run both in Microsoft's Windows and Apple's Mac OS X operating systems. A good solution for this system would:	172	85	Layered	AVAILABLE	\N	2	1317
1318	Consider the following figure depicting two different architectures for web applications   \n![image][image]  \n	117	99	MicroservicesArchitectureOne	AVAILABLE	\N	2	1318
1319	The stimulus of an availability scenario	234	211	Availability stimulus	AVAILABLE	2019-10-04 20:41:17.370802	2	1319
1320	The mobility quality of a system includes	149	110	Mobility quality	AVAILABLE	2019-10-04 16:28:01.336262	2	1320
1321	In a quality scenario	55	44	Scenario	AVAILABLE	\N	2	1321
1322	In the Graphite system description can be read:  \n>"We've got 600,000 metrics that update every minute and we're assuming our storage can only keep up with 60,000 write operations per minute. This means we will have approximately 10 minutes worth of data sitting in carbon's queues at any given time. To a user this means that the graphs they request from the Graphite webapp will be missing the most recent 10 minutes of data."  \n  \n	81	38	GraphiteReliability	AVAILABLE	2019-10-23 14:18:48.617529	2	1322
1323	Consider the following figure that presents a Queue where client applications write their requests to be served by a server.   \n![image][image]  \n	80	63	Queues	AVAILABLE	\N	2	1323
1324	The first architecture of the Fénix system, corresponding to its first years of development, could be described as a three-layered architecture, typical of an enterprise application. One of those layers was the *domain logic* layer. Which of the following sentences best describes the Fénix architecture in what concerns that layer?	57	14	DomainLogicFenixINGLES	AVAILABLE	2019-11-13 11:51:56.259118	2	1324
1325	In a enterprise-wide system, like Fénix system,	79	58	EnterpriseWideEN	AVAILABLE	\N	2	1325
1326	The internationalization of the user interface is supported by the tactic(s)	81	32	InternationalizationTacticsEN	AVAILABLE	\N	2	1326
1327	Consider the Decomposition architectural style of the Module viewtype	157	119	ModuleViewtypeOne	AVAILABLE	2019-11-01 22:07:10.494889	2	1327
1328	There are several tactics to satisfy availability requirements, which may be applied depending on the concrete requirement that we want to satisfy. Assuming that you want to detect faults of type *response* in your system, which tactic is more adequate?	143	123	AvailabilityVotingFirst	AVAILABLE	2019-10-23 22:54:53.389187	2	1328
1329	The main architectural driver for the nginx system was	0	0	nginxFirstADINGLES	DISABLED	\N	2	1329
1360	An architectural tactic	52	28	Tactics	AVAILABLE	\N	2	1360
1410	Consider the following figure   \n![image][image]  \n	128	71	DomainDesignOne	AVAILABLE	\N	2	1410
1431	The Attribute-Driven Design method is characterized by	110	103	BusinessToDesignOne	AVAILABLE	2019-10-23 16:25:40.550892	2	1431
1721	The software architecture of a system	203	155	ArchitectureInfluenceCycle	AVAILABLE	2019-10-04 16:30:22.194975	2	1721
1764	An attack is	179	155	Attack	AVAILABLE	2019-10-03 09:58:58.649725	2	1764
1413	In the software architecture of a system, the Deployment view is best suited for	199	106	AllocationOne	AVAILABLE	\N	2	1413
1330	The document describing the Glasgow Haskell Compiler presents two design decisions about the development of the *Runtime System*. The first of those decisions is described like this:  \n>"The garbage collector is built on top of a block layer that manages memory in units of blocks, where a block is a multiple of 4 KB in size. The block layer has a very simple API: [...]. This is the only API used by the garbage collector for allocating and deallocating memory. Blocks of memory are allocated with `allocGroup` and freed with `freeGroup`."  \n  \nWhich architectural style is more adequate to represent this design decision?	0	0	GHCBlockLayerINGLES	DISABLED	\N	2	1330
1331	Consider the following availability scenario:\n\n"If the application server fails to respond to a request every 5 minutes when the system is in its normal operation, this request fails, the application server is switched to degraded mode and the failure is logged."\n\nWhat is the type of stimulus?	110	73	Availability stimulus	AVAILABLE	2019-10-25 14:25:06.145259	2	1331
1332	The set of structures referred in the definition of software architecture fulfil a need to	140	118	Set of structures in software architecture definition	AVAILABLE	2019-10-02 21:54:18.334222	2	1332
1333	Consider the following decomposition view of the Graphite system where module Store Graphs is responsible for managing the storage of datapoints and graphs and module Present Graphs for graphs generation and presentation. Memcache is a library that maintains datapoints in memory to reduce the overhead of obtaining them from the file system.   \n![image][image]  \n	102	58	GraphiteDecompositionMemcached	AVAILABLE	\N	2	1333
1334	In the Fénix first architecture it was common programmers forget to lock objects in the context of transactions. A solution for this problem can be architecturally described using a view of the architectural style	158	88	FenixOneEN	AVAILABLE	2019-11-01 22:07:10.494889	2	1334
1335	In the context of the *Graphite* case study, consider the following view that represents the internal behavior of the *Carbon* component, where the components `r1,... , rn, w` are threads and `q1, ..., qn` are buffers. The port *read*, which provides an interface to read the data points stored in the queue, can be used, in an enrichment of the view, to illustrate   \n![image][image]  \n	73	37	GraphiteAvailabilityScenario	AVAILABLE	\N	2	1335
1336	Consider the following description of the behavior of Twitter  \n>"Solution is a write based fanout approach. Do a lot of processing when tweets arrive to figure out where tweets should go. This makes read time access fast and easy. Don't do any computation on reads. With all the work being performed on the write path ingest rates are slower than the read path, on the order of 4000 QPS."  \n  \nTo describe this behavior we need to	77	46	TwitterTwo	AVAILABLE	\N	2	1336
1337	Consider the module viewtype views of the DVDCatalog application. The architect knows about a new requirement  \n>"To support multi-platform (Mac, Windows, Linux)"  \n  \nThis requirement requires a change of	128	100	DVDCatalogTwo	AVAILABLE	\N	2	1337
1338	Consider the change in the architecture associated with the use of caches in web services shown in the figure   \n![image][image]  \n Taking into consideration that this change involves adding a server, which has a larger storage capacity than the request Nodes, that change has the impact of	88	45	PerformanceTwoOne	AVAILABLE	2019-10-23 19:19:54.166728	2	1338
1419	The two basic contributors for the response time are the processing time and the contention time. Which tactic for performance may reduce the contention time	108	80	PerformanceTwo	AVAILABLE	2019-10-04 17:11:21.887569	2	1419
1341	In the description of the *How Netflix works* can be read:  \n>"What CDNs basically do is, they take the original website and the media content it contains, and copy it across hundreds of servers spread all over the world. So when, say, you log in from Budapest, instead of connecting to the main Netflix server in the United States it will load a ditto copy of it from a CDN server that is the closest to Budapest."  \n  \nWhich corresponds to the application of the following tactic	89	68	NetflixTacticsTwo	AVAILABLE	\N	2	1341
1342	The *Ensuring that the implementation conforms to the architecture* step of how to create an architecture	59	31	CreateArchitectureTwo	AVAILABLE	\N	2	1342
1343	There are several tactics to satisfy availability requirements, which may be applied depending on the concrete requirement that we want to satisfy. Assuming that you want to detect faults of type *response* in your system, which tactic is more adequate?	99	77	AvailabilityTwo	AVAILABLE	2019-10-23 16:25:40.550892	2	1343
1344	When the domain logic is organized using a Domain Model pattern the most suitable data source patterns are	0	0	LogicAccessDomainModel	REMOVED	\N	2	1344
1345	Consider that a module, that contains a complex business logic, needs to invoke a remote entity using a particular communication protocol and it is needs to manage the invocation, like deal with the possible errors, delays and omissions in the invocation, transform the data before sending it, etc. Which tactic should be applied for a scenario where there will be changes in the communication protocol. Note that the business logic comprises a set of functionalities that is independent of the remote invocation technological aspects.	124	97	ModifiabilityTwo	AVAILABLE	2019-10-22 20:25:00.632343	2	1345
1346	In Chrome it is possible to associate a Renderer Process to each Tab, which results in the increase of performance due to a tactic of	109	80	CHPerformanceQualityINGLES	AVAILABLE	2019-10-23 15:35:36.879316	2	1346
1347	When comparing Amazon Silk with Google Chrome in the context of mobile devices	103	73	SilkMobileDevices	AVAILABLE	\N	2	1347
1348	The web page that describes the architecture of Chromium OS (an open source project to implement a new operating system) starts like this:  \n>"Chromium OS consists of three major components:  \n-  The Chromium-based browser and the window manager  \n-  System-level software and user-land services: the kernel, drivers, connection manager, and so on  \n-  Firmware  \n"  \n  \nConsidering this brief description of the software architecture of Chromium OS, which architectural style is more adequate to represent it?	45	31	ChromiumDecompositionINGLES	AVAILABLE	\N	2	1348
1349	With the evolution of the web application technologies, it is now possible to develop web applications with a user interface similar to the interface of desktop applications. Yet, for this to happen, part of the code that was executing in the web server is now executing in the web browser. How does this change manifests in the software architecture of the system?	123	53	WebTwoOne	AVAILABLE	\N	2	1349
1350	Consider the following figure that presents an architectural view of an *Image Hosting Application* which resulted from the enrichment of another architectural view by adding another *Image File Storage* pair, in the figure they are distinguished by 1 and 2.   \n![image][image]  \n Which quality results from this enrichment, that was not provided by the previous version of the architecture?	121	55	ScalablePartitioning	AVAILABLE	2019-10-04 15:08:14.085405	2	1350
1761	Frank Buschmann, *Introducing the Pragmatic Architect*, defines the *techno-geeks* architects. This kind of architect	115	25	TechoGeeks	DISABLED	\N	2	1761
1351	The Chromium is a web browser that introduced an innovative architecture. In the Chromium description we can read:  \n>"We use separate processes for browser tabs to protect the overall application from bugs and glitches in the rendering engine. We also restrict access from each rendering engine process to others and to the rest of the system. In some ways, this brings to web browsing the benefits that memory protection and access control brought to operating systems. We refer to the main process that runs the UI and manages tab and plugin processes as the "browser process" or "browser." Likewise, the tab-specific processes are called "render processes" or "renderers." The renderers use the WebKit open-source layout engine for interpreting and laying out HTML."  \n  \nWhich architectural style should we use to represent this aspect of Chromium?	97	70	ArqChrome	AVAILABLE	\N	2	1351
1352	The third paragraph of section 8.3.1 describes the buffering mechanism used by an HDFS client when it is writing to a file. How would you describe this mechanism using an architectural view?	0	0	HadoopFileWriteBufferedINGLES	REMOVED	\N	2	1352
1353	In a scenario for interoperability	52	37	InteroperabilityStimulus	AVAILABLE	\N	2	1353
1435	The elasticity of a system, defined as its capability to easily adapt to load changes, is often represented as a required property of the scalability quality. For this level of easiness contribute the architectural solutions associated with the following tactic(s)	93	27	ElasticityDeferBindingEN	AVAILABLE	\N	2	1435
1409	Consider the following figure that presents a Queue where client applications write their requests to be processed by a server.   \n![image][image]  \n This solution **does not** provide the following quality:	123	67	QueuesQualities	AVAILABLE	2019-10-02 23:21:49.816404	2	1409
1354	Suppose that you decided to use the Google App Engine (GAE) in the development of a web application. The GAE is described in the Wikipedia as follows:  \n>"Google App Engine is a platform for developing and hosting web applications in Google-managed data centers. Google App Engine is cloud computing technology. It virtualizes applications across multiple servers and data centers. [...] Google App Engine is free up to a certain level of used resources. Fees are charged for additional storage, bandwidth, or CPU cycles required by the application."  \n  \nOn the other hand, the GAE documentation reads the following:  \n>"With App Engine, you can build web applications using standard Java technologies and run them on Google's scalable infrastructure. The Java environment provides a Java 6 JVM, a Java Servlets interface, and support for standard interfaces to the App Engine scalable datastore and services, such as JDO, JPA, JavaMail, and JCache. Standards support makes developing your application easy and familiar, and also makes porting your application to and from your own servlet environment straightforward."  \n  \nTaking into account these two perspectives on the GAE, which architectural styles are more appropriate to represent the use of GAE in the software architecture of your web application?	50	14	GoogleAppEngine	AVAILABLE	\N	2	1354
1355	Consider the following fragment in the description of the Graphite system.  \n>"The Graphite webapp allows users to request custom graphs with a simple URL-based API. Graphing parameters are specified in the query-string of an HTTP GET request, and a PNG image is returned in response."  \n  \nTo describe this scenario it should be designed a view that applies the following architectural style	192	110	GraphiteOne	AVAILABLE	\N	2	1355
1356	In the Graphite system the component *carbon-relay* implements a tactic	136	59	GPCarbonRelayINGLES	AVAILABLE	2019-10-23 15:16:54.384826	2	1356
1357	Consider the module viewtype views of the Catalog of DVD application. The architect knows about a new requirement  \n>"To support iPhone, iPad, Android versions with sync, which allows offline use of the application in the mobile device and data synchronization to occur when a connection is available"  \n  \nThis requirement requires a change of	130	100	DVDCatalogMobile	AVAILABLE	\N	2	1357
1358	In Facebook it is not possible to have the information about more that one bilion users in a single disk. Therefore, a sharding technique is applied, where the persistent information is split between several database servers, and applications are routed to the right servers for queries and updates. To describe this architecture	132	117	DataModelFacebook	AVAILABLE	2019-11-06 19:47:12.894684	2	1358
1359	According to the attribute-driven design process, we should design the software architecture for a system based on a selected list of requirements, which are called the *architecture significant requirements*. These requirements should be sorted according to their importance for the system's stakeholders because	149	141	DesigningArchitectureOne	AVAILABLE	2019-10-23 15:45:52.72551	2	1359
1361	Consider the following excerpt about the Amazon system  \n>"Mainly, I think service orientation has helped us there. The stored data formats are decoupled from the format in which you communicate data items. If there is no need for sharing schemas of the actual storage layout, you can focus on making sure that the service interfaces can evolve in a way that allows you to handle variations of data formats. You could dictate a rigorous single format, but that would not be realistic if you are in Amazon's platform business. We have to make sure that the platform can be extended by our customers to meet their needs."  \n  \nThe architectural style that better represents these aspects of the Amazon architecture is	82	25	AmazonTwo	AVAILABLE	\N	2	1361
1362	Suppose that you join the development team of a very large software system, and that you are assigned some tasks to change some existing features. Which of the following architectural views would be, in principle, more useful to you to perform those tasks quickly?	44	20	AlterarFuncionalidadesExistentes	AVAILABLE	\N	2	1362
1363	In the Publish-Subscribe architectural style	105	86	PublishSubscribe	AVAILABLE	2019-11-21 14:04:15.479759	2	1363
1364	Suppose that an architect needs to decide whether to follow a modular monolith architecture or a microservices architecture for a new large system. The system to be developed has a complex logic and high volume of requests.	118	86	MicroservicesTwo	AVAILABLE	\N	2	1364
1365	In the description of the Chrome case study you can read:  \n*Typing in the Omnibox (URL) bar triggers high-likelihood suggestions, which may similarly kick off a DNS lookup, TCP pre-connect, and even prerender the page in a hidden tab.*  \nThis description refers to	147	77	CHOmniboxTacticsEN	AVAILABLE	2019-10-23 15:35:36.879316	2	1365
1366	Web servers typically receive requests from different users concurrently (that is, either different users make requests simultaneously or they make them fast enough that it is not possible for the web server to answer one request from one user before receiving another request from another user). To process all the requests, web servers may use different implementation strategies. Assuming that we want to develop a web server to serve only static pages with more or less the same size to a set of clients on the same LAN network as the server, which of the following strategies would be better?	134	103	NginxScenariosTacticsTwo	AVAILABLE	2019-10-02 23:33:55.015501	2	1366
1407	Consider the following figure that presents an architectural view of an *Image Hosting Application*.   \n![image][image]  \n	85	77	ReadsAndWrites	AVAILABLE	\N	2	1407
1367	In the context of the *Graphite* case study, consider the following application-specific types that are used in a view to represent the internal behavior of the *Webapp* component.   \n![image][image]  \n This view can show that the architecture fulfills	70	43	GraphiteWebapp	AVAILABLE	\N	2	1367
1368	Consider the following scenario for performance  \n>"During the enrollment period the FenixEDU system should be able to completely enroll 5.000 students in less than 30 minutes."  \n  \n	81	67	PerformanceSenario	AVAILABLE	\N	2	1368
1369	In the description of the Microservices Architecture by James Lewis and Martin Fowler can be read:  \n>"Microservice teams would expect to see (...) for each individual service such as dashboards showing up/down status and a variety of operational and business relevant metrics. Details on circuit breaker status, current throughput and latency are other examples we often encounter in the wild."  \n  \nWhich quality is being referred?	132	112	MicroservicesMonitorability	AVAILABLE	2019-10-04 17:03:09.261816	2	1369
1370	When writing the availability scenarios of a system	98	79	Design of availability scenarios	AVAILABLE	2019-10-25 16:51:37.703847	2	1370
1371	Consider the following representation of a system following a microservices architecture,   \n![image][image]  \n After an invocation through the REST API	161	108	BoundedContextOne	AVAILABLE	\N	2	1371
1588	The attribute-driven design method for the design of an architecture is	120	115	Attribute-driven design method	AVAILABLE	2019-10-24 10:46:41.370993	2	1588
1798	The availability quality depends on	104	82	Availability quality	AVAILABLE	2019-10-22 20:16:44.762513	2	1798
1372	Consider the following scenario  \n>"Our vehicle information system send our current location to the traffic monitoring system. The traffic monitoring system combines our location with other information, overlays this information on a Google Map, and broadcasts it. Our location information is correctly included with a probability of 99.99%."  \n  \nThe quality addressed by this scenario is	40	18	InteroperabilityScenario	AVAILABLE	2019-10-27 17:31:43.373956	2	1372
1373	Consider the following definition of Microservice architectural style by Martin Fowler  \n>"The microservice architectural style is an approach to developing a single application as a suite of small services, each running in its own process and communicating with lightweight mechanisms, often an HTTP resource API. These services are built around business capabilities and independently deployable by fully automated deployment machinery. There is a bare minimum of centralized management of these services, which may be written in different programming languages and use different data storage technologies."  \n  \nTo represent an architecture based on Microservices	96	60	MicroservicesExamTwo	AVAILABLE	\N	2	1373
1374	Which quality a global cache, where request nodes are responsible for retrieval, does not provide?	148	77	Scalable web architecture	AVAILABLE	2019-10-02 23:29:40.086745	2	1374
1375	Which quality(ies) of Chrome can be inferred from the sentence below?  \n>"By contrast, Chrome works on a multi-process model, which provides process and memory isolation, and a tight security sandbox for each tab. In an increasingly multi-core world, the ability to isolate the processes as well as shield each open tab from other misbehaving pages alone proves that Chrome has a significant performance edge over the competition. In fact, it is important to note that most other browsers have followed suit, or are in the process of migrating to similar architecture."  \n  \n	123	106	ChromeQualities	AVAILABLE	2019-10-23 23:09:52.689666	2	1375
1376	Consider the following requirement for availability of the Adventure Builder system  \n>"The Consumer Web site sent a purchase order request to the order processing center (OPC). The OPC processed that request but didn't reply to Consumer Web site within five seconds, so the Consumer Web site resends the request to the OPC."  \n  \nIf we represent this requirement as a scenario	92	82	AdventureBuilderFive	AVAILABLE	\N	2	1376
1377	The documentation of the software architecture for a system is often composed of several views, because	127	101	SecondEEEN	AVAILABLE	2019-10-03 09:58:58.649725	2	1377
1838	To which performance tactic can a load balancer be associated?	97	69	LoadBalancer	AVAILABLE	2019-10-03 14:03:31.829701	2	1838
1378	Consider the following scenario: *A system administrator simultaneously launches several instances of the system, each one using a different database, and is able to do it in less than 10 minutes.*	101	65	ModifiabilityTwo	AVAILABLE	2019-10-23 16:25:40.550892	2	1378
1379	In the Graphite system, in order to improve performance the component *carbon* do not write directly on disk, it uses a buffer instead:	144	116	GraphiteOne	AVAILABLE	2019-10-22 20:22:40.957034	2	1379
1380	What is an availability solution in the Uber system when total datacenter failure occurs.	137	121	Uber	AVAILABLE	2019-10-04 09:16:43.910122	2	1380
1381	Consider the following scenario: *A system administrator adds more copies of computation of the system, each one using a different database, and is able to do it in less than 10 minutes.*	121	90	ModifiabilityTwo	AVAILABLE	2019-10-23 22:44:10.444379	2	1381
1383	Consider the following figure that presents a Proxy Server that collapses requests from different users.   \n![image][image]  \n	156	118	ProxyServer	AVAILABLE	2019-10-03 21:53:20.140872	2	1383
1384	Consider a scenario for performance where the arrival of events is stochastic with a distribution where there are peeks of events but the arrival of events over a long period is uniform. The best tactic to apply is	154	90	PerformanceTwo	AVAILABLE	2019-10-02 21:47:19.441525	2	1384
1385	Suppose that you are developing a software architecture for a new large scale system and that you intend to resort extensively to third party subcontractors for the development of various parts of the system. Which architectural styles are most useful to plan the development of the system in this case?	70	38	SubcontractorsINGLES	AVAILABLE	\N	2	1385
1386	One of the terms often used to describe the software architecture of a system is the term *tier*, being common, for instance, to talk about *multi-tier* systems. Taking into account the various types of software elements that compose a software architecture, a *tier* is	88	78	TiersINGLES	AVAILABLE	2019-11-22 23:26:12.494277	2	1386
1387	In the Graphite system description can be read:  \n>"We've got 600,000 metrics that update every minute and we're assuming our storage can only keep up with 60,000 write operations per minute. This means we will have approximately 10 minutes worth of data sitting in carbon's queues at any given time. To a user this means that the graphs they request from the Graphite webapp will be missing the most recent 10 minutes of data."  \n  \n	97	55	GraphiteReliability	AVAILABLE	2019-10-23 22:44:10.444379	2	1387
1388	Suppose that, to satisfy an availability requirement related with the occurrence of faults at the network infrastructure used by your system, you want to use the tactic named *Ping/Echo*. How does the use of that tactic manifests in the architectural views of your system?	107	60	AvailabilityINGLES	AVAILABLE	\N	2	1388
1389	What could be the cause of the failures, when an application server shows some omissions to requests from the clients.	121	116	Availability causes of failure	AVAILABLE	2019-10-26 15:18:45.057874	2	1389
1451	The layered architectural style applies the modifiability architectural tactic of	114	96	ModifiabilityExamOne	AVAILABLE	\N	2	1451
1390	In the description of the Microservices Architecture by James Lewis and Martin Fowler can be read:  \n>"As well as the fact that services are independently deployable and scalable, each service also provides a firm module boundary, even allowing for different services to be written in different programming languages. They can also be managed by different teams."  \n  \nWhich is not necessarily an advantage of being independently deployable and scalable?	87	19	MicroservicesModularity	AVAILABLE	\N	2	1390
1411	In Chrome system, to show that it provides availability when the javascript code executing in a tab crashes, and security when the javascript code executing in a tab tries to access the information in another tab, it is necessary to design	118	66	ChromeCommunicatingProcesses	AVAILABLE	\N	2	1411
1412	Consider the following excerpt of the description of Chrome\n\n> Chrome has two different implementations of the internal cache: one backed by local disk, and second which stores everything in memory. The in-memory implementation is used for the Incognito browsing mode and is wiped clean whenever you close the window. Both implement the same internal interface (`disk_cache::Backend`, and `disk_cache::Entry`), which greatly simplifies the architecture and–if you are so inclined–allows you to easily experiment with your own experimental cache implementations.\n\nwhich refers to a modifiability quality. Which tactic is being applied?	109	76	Chrome modifiability	AVAILABLE	2019-10-22 20:16:28.437085	2	1412
1391	The email system is composed of various types of components playing different roles. For example, to send an email, a user uses a *mail user agent* (MUA), to compose his message and send it. To send the message, the MUA typically connects to a *mail transfer agent* (MTA) that receives the message, analyzes the message's headers to determine the recipients and, after querying the DNS system to determine the MTA responsible for each recipient, it connects to the MTAs responsible for the destination addresses to deliver the message. Each of these MTAs receives the message and stores it locally or forwards it to others MTAs until the message reaches its destination MTA. The recipient user of the message will then use his MUA to see the messages that were sent to him. To do it, the MUA connects to an IMAP or POP server to obtain the user's messages. Those IMAP and POP servers obtain the messages for a user by reading the messages stored by the MTA. Given this simplified description of the operation of the email system, which of the following architectural styles is more appropriate to represent the pattern of interaction between the MTA and the servers IMAP and POP?	107	42	RepositoryClientServerTwo	AVAILABLE	2019-11-21 13:31:02.822493	2	1391
1436	In the description of the SocialCalc case study can be read:  \n>"To make this work across browsers and operating systems, we use the Web::Hippie4 framework, a high-level abstraction of JSON-over-WebSocket with convenient jQuery bindings."  \n  \nFrom this fragment can be identified a scenario for	0	0	SocialCalcModifiability	DISABLED	\N	2	1436
1392	Typically, Instant Messaging clients have a window to list the contacts of the user, and show in that window the status of each contact (whether it is available, unavailable, busy, etc). Given that the status of a contact may be changed at any time, and that the contact's status is given by the Instant Messaging application of that contact, which architectural style represents best the interaction pattern between these components?	404	350	ComponentAndConnectorStyleThree	AVAILABLE	2019-11-21 15:19:46.178664	2	1392
1393	Consider the Microservice architectural style. Which of the following sentences **does not** describe an advantage of microservices?	233	161	MicroservicesExamOne	AVAILABLE	2019-10-02 21:54:18.334222	2	1393
1394	One of the key requirements for the HDFS system is that the data stored in the system remains available, even in the presence of various types of failures (non simultaneous) in the hardware in which the system executes. To show that the system satisfies this requirement	125	71	HadoopDisponibilidadeDadosINGLES	AVAILABLE	\N	2	1394
1395	The architectural styles are categorized, and grouped, in terms of	142	103	Architectural styles	AVAILABLE	2019-10-04 15:36:52.943277	2	1395
1396	Consider the modifiability quality and the cost of change.	116	97	ModifiabilityThree	AVAILABLE	2019-10-23 15:16:54.384826	2	1396
1397	Which performance tactic is referred in the following description of Chrome?  \n>"The ability of the browser to optimize the order, priority, and latency of each network resource is one of the most critical contributors to the overall user experience. You may not be aware of it, but Chrome's network stack is, quite literally, getting smarter every day, trying to hide or decrease the latency cost of each resource: it learns likely DNS lookups, it remembers the topology of the web, it pre-connects to likely destination targets, and more. From the outside, it presents itself as a simple resource fetching mechanism, but from the inside it is an elaborate and a fascinating case study for how to optimize web performance and deliver the best experience to the user."  \n  \n	81	72	ChromeTactics	AVAILABLE	2019-10-23 15:45:52.72551	2	1397
1485	According to Frank Buschmann in the article *Introducing the Pragmatic Architect*	20	17	PragmaticArchitect	DISABLED	\N	2	1485
1398	Consider the change in the architecture associated with the use of caches in web services shown in the figure   \n![image][image]  \nTaking into consideration that in this change the added global cache is located in a server, which has a larger storage capacity than the request Nodes, this change has the impact of	101	72	PerformanceTwoOne	AVAILABLE	2019-10-03 16:22:46.352926	2	1398
1399	Human-editable URL API for creating graphs is a usability design tactic used in the Graphite system. This tactic	93	67	GraphiteTechnicaAndNonTechnicalUsers	AVAILABLE	2019-10-23 14:18:48.617529	2	1399
1400	The Eclipse IDE is an open source application written in Java, and is extensible through the use of plug-ins. In the document that describes the existing plug-ins architecture in Eclipse, we may read the following:  \n>"A plug-in in Eclipse is a component that provides a certain type of service within the context of the Eclipse workbench. [...] The plug-in class provides configuration and management support for the plug-in. A plug-in class in Eclipse must extend `org.eclipse.core.runtime.Plugin`, which is an abstract class that provides generic facilities for managing plug-ins."  \n  \nConsidering the model and terminology used in the course to describe a software architecture, what kind of views are more appropriate to represent the plug-ins architecture of Eclipse described above?	46	18	PluginsEclipse	AVAILABLE	\N	2	1400
1401	Consider the following informal view of an Image Hosting System   \n![image][image]  \n	70	37	ImageHostingScalability	AVAILABLE	\N	2	1401
1402	The component-and-connector viewtype of the software architecture description	135	114	Component-and-connector viewtype	AVAILABLE	2019-10-04 21:03:23.915652	2	1402
1403	In Chrome, to accomplish the security quality, the Browser Process implements a tactic	143	102	CHSecurityQualityINGLES	AVAILABLE	2019-10-23 15:39:55.604858	2	1403
1404	Consider the following excerpt about the Scalable web architecture and distributed systems case study about two different possible implementations of a global cache  \n>"There are two common forms of global caches (...), when a cached response is not found in the cache, the cache itself becomes responsible for retrieving the missing piece of data from the underlying store. (...) it is the responsibility of request nodes to retrieve any data that is not found in the cache."  \n  \n	153	90	ScalableArchitectureOne	AVAILABLE	2019-10-04 20:27:41.035474	2	1404
1405	The connectors on component-and-connector view	112	59	ComponentConnectorTwo	AVAILABLE	2019-11-21 14:30:55.568524	2	1405
1406	The typical software architecture of an enterprise application is composed of three tiers and three layers. Yet, we may have variations of this architecture. For instance, by separating the middle tier in two tiers. In this case, which other changes exist on the architecture that are related with the layers?	65	35	ThreeVsFourTiersINGLES	AVAILABLE	\N	2	1406
1498	In the Publish-Subscribe architectural style	93	78	PublishSubscribe	AVAILABLE	2019-11-21 15:06:54.180074	2	1498
1408	The recent developments in web applications that made them provide a richer user interface led to a change in its architecture: part of the application's computation has to be done in the web browser used by users to access the application. How is this change in the architecture manifested in the different types of views that describe the software architecture of a web application?	57	27	AlteracaoWebDois	AVAILABLE	\N	2	1408
1832	In the Amazon Silk browser	96	86	Silk	AVAILABLE	\N	2	1832
1833	To implement the Identity Map pattern	45	19	IdentityMapEN	REMOVED	\N	2	1833
1414	Suppose that you are developing the software architecture of a new system for an organization composed of several organizational units, each one with its own information systems, which have been developed independently of each other over the course of several years and depending on the particular needs of each unit. Your system has the goal of integrating the various existing systems, providing in this way not only a unified view of how the organization works, but also allowing the creation of new processes within the organization that involve more than one unit. Which architectural style is better suited to design such a system?	103	64	SOA	AVAILABLE	\N	2	1414
1415	There are several tactics to satisfy availability requirements, which may be applied depending on the concrete requirement that we want to satisfy. Assuming that you want to deal with faults of type *omission* in your system, which tactic is more adequate?	120	83	AvailabilityOne	AVAILABLE	\N	2	1415
1416	Architecturally significant requirements (ASR) are captured in a utility tree where each one of the ASRs are classified in terms of its architectural impact and business value.	68	52	BusinessToDesignTwo	AVAILABLE	\N	2	1416
1417	According to the document that describes the architecture of web services:  \n>"Another critical piece of any distributed system is a load balancer. Load balancers are a principal part of any architecture, as their role is to distribute load across a set of nodes responsible for servicing requests. This allows multiple nodes to transparently service the same function in a system. Their main purpose is to handle a lot of simultaneous connections and route those connections to one of the request nodes, allowing the system to scale to service more requests by just adding nodes."  \n  \nBased on this description, what is the best way to represent the architecture of a system that is using a *load balancer*?	103	74	ComponentAndConnectorViewtypeOne	AVAILABLE	\N	2	1417
1418	The software architecture of a system	213	104	SoftwareArchitectureTwo	AVAILABLE	2019-10-02 23:29:40.086745	2	1418
1420	Consider the following excerpt from the tutorial on the Hadoop MapReduce:  \n>"Hadoop MapReduce is a software framework for easily writing applications which process vast amounts of data (multi-terabyte data-sets) in-parallel on large clusters (thousands of nodes) of commodity hardware in a reliable, fault-tolerant manner. A MapReduce job usually splits the input data-set into independent chunks which are processed by the map tasks in a completely parallel manner. The framework sorts the outputs of the maps, which are then input to the reduce tasks. Typically both the input and the output of the job are stored in a file-system. The framework takes care of scheduling tasks, monitoring them and re-executes the failed tasks."  \n  \nWhich architectural style of the component-and-connector viewtype is more adequate to describe how the MapReduce works, taking into account its main advantages in solving a problem?	133	62	TiersDynamicreconfigurationPeertopeerPublishsubscribeTwo	AVAILABLE	\N	2	1420
1421	Consider the following scenario  \n>"When writing to the database the system receives an exception about a write failure. The system should stop interacting with data base and write a log message."  \n  \nThe quality addressed by this scenario is	59	41	AvailabilityScenario	AVAILABLE	2019-10-27 17:31:43.373956	2	1421
1422	A performance tactic to control resource demand is	103	53	PerformanceTwo	AVAILABLE	2019-10-04 09:16:43.293901	2	1422
1423	Assuming that you were asked to document the software architecture of an existing (and already developed) system, the best thing for you to do would be	154	124	ArchitectureKnowledge	AVAILABLE	2019-10-04 17:18:14.245739	2	1423
1424	Consider a system where the end user can change the look and feel of the user interface she is using by setting a few configuration parameters. 	104	81	Modifiability cost of installing the mechanism and cost of change	AVAILABLE	2019-10-22 12:31:00.314429	2	1424
1425	In the context of the FenixEdu case study the following scenario was identified.  \n>"The school management pretends that all the members of the school, students, administrative staff, faculty and management should be able to use the system to perform their activities efficiently without requiring the installation of any client software or a long learning process."  \n  \nThis is a	54	11	BusinessScenarioOne	AVAILABLE	\N	2	1425
1426	Several of the cases studied in this course had scalability requirements. That means that those systems should be designed in such a way that they	163	116	ScalabilityINGLES	AVAILABLE	2019-10-02 23:21:49.816404	2	1426
1427	The decision on the programming language to implement the system is hard to change, which, makes it an architectural decision. In the context of microservices architecture	140	118	Microservices	AVAILABLE	2019-10-03 14:03:31.829701	2	1427
1558	The microservices architecture fosters rapid cycles of continuous delivery because	124	110	Microservices	AVAILABLE	2019-10-03 09:58:58.649725	2	1558
1559	The microservices architecture can be used when the system requires the quality of	119	101	Microservices	AVAILABLE	2019-10-03 13:23:59.360009	2	1559
1428	One of the evolutions in the development of web applications was the appearance of *mashups*, which are described in Wikipedia as follows:  \n>"In web development, a mashup is a web page or application that uses and combines data, presentation or functionality from two or more sources to create new services."  \n  \nKnowing that the sources used by *mashups* do not know about the existence of the *mashups* and that they change frequently, forcing the adaptation of the *mashups* to accommodate those changes, what is the best architecture to minimize the effects of those changes?	138	48	WebApplicationsOne	AVAILABLE	\N	2	1428
1429	Which of the following phrases best describe the relationship between modules and components?	189	130	ModuleComponentTwo	AVAILABLE	\N	2	1429
1430	To achieve a faster time-to-market, software companies are increasingly using a strategy of incremental releases of their software, where each new release has a set of new features. Which architectural style is better to analyse whether the system's software architecture is adequate for the planned incremental releases?	241	140	UsesStyle	AVAILABLE	\N	2	1430
1432	Considering the voting tactic for fault detection, which form of the tactic is more suitable to detect implementation faults?	122	96	Availability tactics	AVAILABLE	2019-10-22 12:31:00.314429	2	1432
1433	In the description of the Microservices Architecture by James Lewis and Martin Fowler can be read:  \n>"Decentralizing responsibility for data across microservices has implications for managing updates. The common approach to dealing with updates has been to use transactions to guarantee consistency when updating multiple resources. This approach is often used within monoliths."  \n  \nWhat is the impact of decentralizing responsibility for data across microservices?	116	94	MicroservicesConsistency	AVAILABLE	2019-10-04 15:40:26.772116	2	1433
1434	The two basic contributors for the response time are the processing time and the blocking time. Which tactic for performance may reduce the blocking time	68	30	PerformanceTwo	AVAILABLE	\N	2	1434
1834	The Service Layer pattern is typically used in conjunction with	115	48	ServiceLayer	REMOVED	\N	2	1834
1437	The email system is composed of various types of components playing different roles. For example, to send an email, a user can use a program such as Microsoft Outlook or Mozilla Thunderbird, generically designed a *mail user agent* (MUA), to compose his message and send it. To send the message, the MUA typically connects to a *mail transfer agent* (MTA) that receives the message, analyzes the message's headers to determine the recipients and, after querying the DNS system to determine the MTA responsible for each recipient, it connects to the MTAs responsible for the destination addresses to deliver the message. Each of these MTAs receives the message and stores it locally or forwards it to others MTAs (for example, when there are forwards or aliases configured, or when the MTA that receives the message is not the ultimately responsible for the email address of the recipient). Given this simplified description of the operation of the email system, which of the following architectural styles is more appropriate to represent the pattern of interaction between the MTAs?	87	67	ArqEmailMTA	AVAILABLE	2019-11-21 13:31:02.822493	2	1437
1438	According to the document that describes nginx:  \n>"nginx runs several processes in memory; there is a single master process and several worker processes. There are also a couple of special purpose processes, specifically a cache loader and cache manager. All processes are single-threaded in version 1.x of nginx. All processes primarily use shared-memory mechanisms for inter-process communication."  \n  \nAssuming that you want to highlight how the various nginx processes communicate among themselves, which architectural style is more adequate to represent the above information?	0	0	nginxProcessesINGLES	DISABLED	\N	2	1438
1439	Consider the following figure that presents a Queue where client applications write their requests to be processed by a server (asynchronous) and compare with another architectural design (synchronous) where a thread is associated with each request.   \n![image][image]  \n Consider a situation where the server that processes the tasks crashes	67	51	QueuesCrash	AVAILABLE	\N	2	1439
1440	The main *architectural driver* for the nginx system was to solve the *C10K problem*: being able to maintain 10.000 simultaneous connections with a single server running on conventional hardware. For this o happen, nginx must	0	0	nginxCTenKProblemINGLES	DISABLED	\N	2	1440
1443	Several of the cases studied in this course had performance requirements. Which architectural views are typically needed to show that those requirements are satisfied?	0	0	PerformanceINGLES	REMOVED	\N	2	1443
1444	Consider the use of a proxy to collapse requests. This corresponds to a tactic of	77	48	ScalableArchitectureTwo	AVAILABLE	\N	2	1444
1445	An email client such as Mozilla's Thunderbird or Microsoft's Outlook allows a user both to read the emails that were sent to him and to send new emails to other people. To do that, the email client connects to other components (one or more): some of these components keep the user's mailboxes with all the emails that were sent to him, whereas other components know how to forward the emails sent by the user to their final destinations (associated with a new set of destinations). In either case, it is always the email client that makes a request to the other components, but whereas in the first case the email client receives all the information about the user's emails, in the second case only a success or failure error code is returned. The architectural patterns that best describe the interactions between the components from the client to the final destinations	190	71	PeerToPeer	AVAILABLE	2019-11-21 15:06:54.180074	2	1445
1446	In the description of the Microservices Architecture by James Lewis and Martin Fowler can be read:  \n>"As well as the fact that services are independently deployable and scalable, each service also provides a firm module boundary, even allowing for different services to be written in different programming languages. They can also be managed by different teams."  \n  \nFor this description it is relevant to consider the software architecture concept(s) of	103	63	MicroservicesModuleAndComponent	AVAILABLE	2019-10-02 23:21:49.816404	2	1446
1447	One of the major changes introduced in the software architecture of the Fénix system, compared to its first architecture, was	0	0	DomainLogicFenixINGLES	DISABLED	\N	2	1447
1448	In the Hadoop system:	62	13	HadoopDisponibilidadeDesempenhoINGLES	REMOVED	2019-10-23 15:28:19.127857	2	1448
1449	Consider the following generalization view of the Catalog of DVD case study.   \n![image][image]  \n	95	67	DVDGeneralization	AVAILABLE	\N	2	1449
1450	One way to increase the performance of a 3-tier enterprise application (with the standard separation in the web client, web server, and database tiers) is to replicate the web server tier and to add a load-balancer between the web clients and the web servers. Unfortunately, for some enterprise applications that option is not enough (or does not work at all), because	112	22	ThreeTiers	AVAILABLE	\N	2	1450
1452	You have to develop an application that collects news from different web sources and process that information to present a digest to the application users. The different sources provide similar information through different interfaces (APIs). Additionally, the new sources may change the interfaces, for instance to enhance their service. Which architectural style can be used to represent this requirements?	107	76	GeneralizationInterfaces	AVAILABLE	\N	2	1452
1453	In the Hadoop system when the *CheckpointNode* and the *NameNode* are deployed in different nodes, the *CheckpointNode* provides:	55	40	HadoopCheckpointINGLES	AVAILABLE	2019-10-23 13:59:29.145958	2	1453
1454	To increase the availability of a web application it is possible to use a load-balancer between the clients and the servers that detects server failures and transparently redirects the requests to the servers that are functioning properly. To represent this architecture	105	59	LoadBalancer	AVAILABLE	\N	2	1454
1455	Ralph Johnson says that  \n>"Architecture is the decisions that you wish you could get right early in a project."  \n  \nThis sentence reflects the fact that	119	83	ArchitectureEvolution	AVAILABLE	2019-10-27 18:40:52.740401	2	1455
1456	In the description of the Thousand Parsec case study can be read:  \n>"Finding a public Thousand Parsec server to play on is much like locating a lone stealth scout in deep space - a daunting prospect if one doesn't know where to look. Fortunately, public servers can announce themselves to a metaserver, whose location, as a central hub, should ideally be well-known to players."  \n  \nFrom this fragment can be identified a scenario for	0	0	ThounsandParsecInteroperability	DISABLED	\N	2	1456
1457	To analyse the performance of a system	122	98	PerformanceOne	AVAILABLE	\N	2	1457
1529	In the component-and-connector viewtype connectors can be complex, which means that they provide a rich set of qualities to the interaction between the components that they connect. These complex connectors can be documented in another view using a set of components interacting through simpler connectors.	116	90	ComponentAndConnectorOne	AVAILABLE	2019-11-21 15:54:01.863876	2	1529
1835	The Active Record pattern is best used when we are also using	115	55	ActiveRecord	REMOVED	\N	2	1835
1458	Consider the following excerpt from the Wikipedia page on *white-box testing*:  \n>"White-box testing is a method of testing software that tests internal structures or workings of an application, as opposed to its functionality. In white-box testing an internal perspective of the system (including the module's code), as well as programming skills, are required and used to design test cases. The tester chooses inputs to exercise paths through the code and determine the appropriate outputs."  \n  \nAssuming that you belong to the team testing a complex system and that you are responsible for performing white box tests on the system, which of the following architectural views of the system would be most useful to you?	149	86	WhiteBoxTestingINGLES	AVAILABLE	\N	2	1458
1459	The Chromium is a web browser that introduced an innovative architecture. In the Chromium description we can read:  \n>"Chromium is a large and complex cross-platform product. We try to share as much code as possible between platforms, while implementing the UI and OS integration in the most appropriate way for each. While this gives a better user experience, it adds extra complexity to the code. This document describes the recommended practices for keeping such cross-platform code clean. We use a variety of different file naming suffixes to indicate when a file should be used:  \n-  Windows files use the `_win` suffix.  \n-  Cocoa (Mac UI) files use the `_cocoa` suffix, and lower-level Mac files use the `_mac` suffix.  \n-  Linux files use `_linux` for lower-level files, `_gtk` for GTK-specific files, and `_x` for X Windows (with no GTK) specific files.  \n-  Posix files shared between Mac and Linux use the `_posix` suffix.  \n-  Files for Chrome's ''Views'' UI (on Windows and experimental GTK) layout system use the `_views` suffix.  \nThe separate front-ends of the browser are contained in their own directories:  \n-  Windows Views (and the experimental GTK-views):`chrome/browser/ui/views`  \n-  Linux GTK: `chrome/browser/gtk`  \n-  Mac: `chrome/browser/cocoa`  \n"  \n  \nWhich architectural style should we use to represent this aspect of Chromium?	64	34	ChromeMultiPlatform	AVAILABLE	\N	2	1459
1460	In the description of the Git case study can be read how to deal with the corruption of pack files in the context of the availability quality:  \n>"If an object was only copied partially or another form of data corruption occurred, recalculating the SHA of the current object will identify such corruption."  \n  \nThe tactic addressed in this fragments is:	0	0	GitConditionMonitoring	DISABLED	\N	2	1460
1461	The architectural styles which are more suitable to describe the MediaWiki system from the end user viewpoint are	44	28	MWArchitecuralStyleINGLES	DISABLED	\N	2	1461
1462	The MediaWiki system tries to enforce a reliability criteria that all the changes done by a writer are consistently visualized in her subsequent reads	44	19	MWReliabilityTacticsINGLES	DISABLED	\N	2	1462
1463	In the Hadoop system the use of a *BackupNode* instead of a *CheckpointNode*:	41	25	HadoopCheckpointBackupNodeINGLES	AVAILABLE	\N	2	1463
1464	The MediaWiki system tries to guarantee a reliability criteria where all information is available to be read by any reader in less than 30 seconds after being written. To achieve this criteria the load balancer	44	33	MWReliabilityReadsImplementationINGLES	DISABLED	\N	2	1464
1465	The function of Master Runner component of GNU Mailman can be represented using an architecture style of	44	18	GMMasterRunnerEN	DISABLED	\N	2	1465
1466	Imagine that you want to develop a system that is to be used in email servers, whose goal is to allow changing the emails that are received by the server (for example, to remove potential viruses or URLs linking to phishing sites). The goal is that the server feeds each received email through this system before processing it (e.g., forward it to another server, or store it locally). The system is supposed to be easily modifiable, to support new types of email transformations. Which architectural style is the most adequate to satisfy these requirements?	128	114	SOAPipesFiltersOne	AVAILABLE	2019-11-21 13:17:44.021521	2	1466
1467	Consider a web application that supports several types of user interface, e.g., web, mobile, etc. If it has to process a high volume of requests, which depend on the type of user interface, and a multi-tier architecture is followed. How many tiers should be used?	102	38	WebAppsTwo	AVAILABLE	2019-11-21 15:54:01.863876	2	1467
1468	In which performance tactic it may occur that not all the inputs are processed	142	65	PerfomanceTacticOne	AVAILABLE	2019-10-04 17:24:39.525435	2	1468
1469	Web servers implemented in Java, such as the Tomcat web server, typically use a thread-based model for processing requests. That is, they process each request on a different thread within the same JVM process, rather than on a different process. One of the reasons for this is that	62	36	nginxOne	DISABLED	\N	2	1469
1470	When describing their system people refer to a part of it as containing a database server. Applying the component-and-connector styles learned in the course we can say that this system uses	82	46	CCStyleOne	AVAILABLE	2019-11-21 13:06:30.771148	2	1470
1471	Consider the shared-data style. Which of the following qualities does it support?	109	73	CCStyleThree	AVAILABLE	2019-11-21 13:31:02.822493	2	1471
1581	The Aspects style was introduced recently as a new style of the module viewtype. Using this style in the software architecture of a system	191	135	AspectsINGLES	AVAILABLE	2019-11-01 22:07:10.494889	2	1581
1472	In Facebook it is not possible to have the information about more that one bilion users in a single disk. Therefore, a sharding technique is applied, where the persistent information is split between several database servers, and requests are routed to the right servers for queries and updates. Additionally, due to performance requirements, the information needs to be replicated in several servers. To describe this architecture	187	164	DataModelTwo	AVAILABLE	2019-11-06 20:01:35.072903	2	1472
1473	General scenarios play an important role in the development of a software architecture because	123	59	ScenariosTacticsTwo	AVAILABLE	2019-10-03 14:03:31.829701	2	1473
1474	In the interview Werner Vogels from Amazon gives to Jim Gray, Werner Vogels says that  \n>"The stored data formats are decoupled from the format in which you communicate data items. If there is no need for sharing schemas of the actual storage layout, you can focus on making sure that the service interfaces can evolve in a way that allows you to handle variations of data formats."  \n  \nWhich means that in the software architecture of Amazon's systems	130	109	MicroAndAmazonThree	AVAILABLE	2019-11-21 12:13:41.17079	2	1474
1475	It was decided that the Fénix system should be based on open-source software.	29	11	BusinessToDesignThree	DISABLED	\N	2	1475
1528	An important stage of the development of any system is its build into the set of executable files. A suitable architectural style which helps on the definition of the build process is	102	71	InstallStyle	AVAILABLE	2019-11-22 23:26:12.494277	2	1528
1526	In the software architecture of a system, the Deployment architectural style of the allocation viewtype is best suited for	173	114	Deployment	AVAILABLE	2019-11-21 14:30:55.568524	2	1526
1476	Considering yet the example of the email system, MUAs are used not only to compose and to send messages, but also for users to read the email messages sent to them. For this, the MUAs have to get those messages from the component that stores them to show them to the user. Two different ways of doing this is by using the POP and IMAP protocols. In the first case, messages are moved from the POP server to the user's computer. In second case, the messages are always stored on the IMAP server, allowing the user to access email from different computers, as long as they are able to connect to the same IMAP server. Which of the following architectural styles is more appropriate to represent the pattern of interaction between the MUAs and a IMAP server?	67	47	ArqEmailIMAP	AVAILABLE	2019-11-21 13:17:44.021521	2	1476
1477	Proxy servers	132	101	Scalable web architecture	AVAILABLE	2019-10-02 23:33:55.015501	2	1477
1478	Consider the following informal view of an Image Hosting System   \n![image][image]  \n	98	51	ImageHostingPerformance	AVAILABLE	2019-10-04 17:24:39.525435	2	1478
1479	Suppose that in the development of an enterprise application (which needs to access a database) it was decided to use the Hibernate framework to simplify the development of the data access code. Which architectural view is the most adequate to represent this decision?	73	22	ModuleViewtypeTwoOne	AVAILABLE	\N	2	1479
1480	A general scenario for a quality attribute	92	39	GeneralScenario	AVAILABLE	2019-10-04 15:44:35.725472	2	1480
1481	Imagine that you are developing an architectural view where you are using the Shared Data style and that a member of your team proposes that two of Data Accessors communicate directly between them. In your opinion	150	51	SharedDataAccessorsDirectINGLES	AVAILABLE	2019-11-21 13:06:30.771148	2	1481
1482	Consider the following architectural view of the Pony-Build system as described in the Continous integration case study   \n![image][image]  \n According to this view the quality of performance is achieved through	62	23	ContinousIntegrationViewsOne	DISABLED	\N	2	1482
1483	In the Infinispan case study can be read  \n>"Infinispan's core data structures make use of software transactional memory techniques for concurrent access to shared data. This minimizes the need for explicit locks, mutexes and other forms of synchronization, preferring techniques like compare-and-set operations within a loop to achieve correctness when updating shared data structures. Such techniques have been proven to improve CPU utilization in multi-core and SMP systems, and despite the increased code complexity, has paid off in overall performance when under load."  \n  \nThese properties of Infinispan can be represented by	62	38	InfinispanViewsOne	DISABLED	\N	2	1483
1486	There are several tactics to satisfy availability requirements, which may be applied depending on the concrete requirement that we want to satisfy. Assuming that you want to detect faults of type *response* in your system, which tactic is more adequate?	104	71	Availability	AVAILABLE	2019-10-23 15:55:56.472234	2	1486
1487	The software architecture of a system	132	85	SoftwareArchitecture	AVAILABLE	2019-10-02 23:29:40.086745	2	1487
1488	The Generalization architectural style of the module viewtype can be use to support the evolution of a system	252	195	ModuleStyleThree	AVAILABLE	2019-11-06 20:19:32.458704	2	1488
1489	Consider the Component-and-Connector viewtype	123	98	ComponentAndConnectorOne	AVAILABLE	2019-11-21 12:13:41.17079	2	1489
1490	Consider the following excerpt from the Wikipedia page on *black-box testing*:  \n>"Black-box testing is a method of software testing that tests the functionality of an application as opposed to its internal structures or workings. Specific knowledge of the application's code/internal structure and programming knowledge in general is not required. Test cases are built around specifications and requirements, i.e., what the application is supposed to do."  \n  \nAssuming that you belong to the team testing a complex system and that you are responsible for performing black box tests on the system, which of the following architectural views of the system would be most useful to you?	181	132	BlackBoxTesting	AVAILABLE	2019-11-01 22:07:10.494889	2	1490
1491	The requirements impact on how an architecture is designed	150	72	RequirementsImpact	AVAILABLE	2019-10-03 15:45:28.202956	2	1491
1492	What is the impact of the business goals on the software architecture?	184	163	Impact of the business goals on the architecture	AVAILABLE	2019-10-04 09:05:33.123379	2	1492
1493	Currently, the most popular architecture for an enterprise application is composed of 3 tiers. The three tiers are	171	67	TresTiersINGLES	AVAILABLE	2019-11-21 14:04:15.479759	2	1493
1494	What is the main impact of the functionalities of a system on its software architecture?	128	114	The impact of functionalities on the architecture	AVAILABLE	2019-10-04 09:16:43.293901	2	1494
1495	Why it is important that the software architectures provides a basis for training?	168	156	Providing a basis for training	AVAILABLE	2019-10-03 16:22:46.352926	2	1495
1496	Consider the install architectural style of the allocation viewtype.	115	72	AllocationTwo	AVAILABLE	2019-11-21 15:19:46.178664	2	1496
1497	Consider the following modifiability scenario  \n>"The effort necessary to successfully port the system to execute in a new browser should not be higher than 5 person/month."  \n  \n	99	76	ModifiabilityScenario	AVAILABLE	2019-10-22 20:25:00.632343	2	1497
1499	Consider the architectural views for the SocialCalc system. In the case description can be read:  \n>"The save format is in standard MIME multipart/mixed format, consisting of four text/plain; charset=UTF-8 parts, each part containing  \n-delimited text with colon-delimited data fields. The parts are... This format is designed to be human-readable, as well as being relatively easy to generate programmatically. This makes it possible for Drupal's Sheetnode plugin to use PHP to convert between this format and other popular spreadsheet formats, such as Excel (.xls) and OpenDocument (.ods)."  \n  \nFrom the above excerpt can be inferred the need to have	115	40	SocialCalcView	DISABLED	\N	2	1499
1500	On the web page of Memcached can be read:  \n>"..., high-performance, distributed memory object caching system, generic in nature, but intended for use in speeding up dynamic web applications by alleviating database load."  \n  \nAccording to this information, Memcached is	148	75	ModueComponent	AVAILABLE	2019-10-02 23:21:49.816404	2	1500
1530	The architectural style that best represents the runtime execution of a system Git installed for a small group of developers is	193	131	GitViews	AVAILABLE	2019-11-21 13:17:44.021521	2	1530
1531	Consider the following architectural view of the Adventure Builder system, designed around the Order Processing Center   \n![image][image]  \n The views **does not** use the architectural style	137	76	AdventureBuilderThree	AVAILABLE	2019-11-21 13:06:30.771148	2	1531
1525	Martin Fowler, *Who Needs and Architect?*, cites Ralph Johnson sentence:  \n>"In most successful software projects, the expert developers working on that project have a shared understanding of the system design. This shared understanding is called architecture."  \n  \n	65	34	SharedUnderstanding	AVAILABLE	2019-10-02 21:47:19.441525	2	1525
1527	The repository architectural style provides modifiability because	90	67	RepositoryModifiability	AVAILABLE	2019-11-21 13:06:30.771148	2	1527
1501	Given the complexity of building a good automatic Chess player, programs that play chess usually make use of existing chess engines, as shown by the following excerpt from Wikipedia:  \n>"A chess engine is a computer program that can play the game of chess. Most chess engines do not have their own graphical user interface (GUI) but are rather console applications that communicate with a GUI such as XBoard (Linux) and WinBoard (Windows) via a standard protocol."  \n  \nIn the web page for XBoard, we may read the following:  \n>"XBoard is a graphical user interface for chess [...]. It displays a chessboard on the screen, accepts moves made with the mouse, and loads and saves games in Portable Game Notation (PGN). It serves as a front-end for many different chess services, including:  \n-  Chess engines that will run on your machine and play a game against you or help you analyze, such as GNU Chess, Crafty, or many others.  \n-  [...]  \n"  \n  \nGiven the above information on XBoard, chess engines, and how they interact at runtime, which of the following architectural styles best represents the of architecture of a software system based on XBoard and one of the engines?	42	18	XBoardChess	AVAILABLE	\N	2	1501
1502	*Domain Model* and *Transaction Script* are two of the existing patterns to implement the domain logic layer of an enterprise application. Choosing one or the other	0	0	DomainModelINGLES	REMOVED	\N	2	1502
1503	Consider the Uses and Layered architectural styles.	184	116	UsesLayersEN	AVAILABLE	2019-11-01 22:07:10.494889	2	1503
1504	The Peer-to-Peer architectural style provides high scalability and availability. In the context of a file sharing system	149	116	PeerToPeerSpace	AVAILABLE	2019-11-21 14:30:55.568524	2	1504
1505	Consider the following modifiability scenario for the Adventure Builder system  \n>"A new business partner (airline, lodging, or activity provider) that uses its own web services interface is added to the system in no more than 10 person-days of effort for the implementation. The business goal is easy integration with new business partners."  \n  \nand the following architectural view   \n![image][image]  \n	282	215	AdventureBuilderModuleOne	AVAILABLE	2019-11-06 19:25:14.813915	2	1505
1506	The Service-Oriented Architecture style improves modifiability because	92	80	SOAQualities	AVAILABLE	2019-11-22 23:26:12.494277	2	1506
1507	Consider the following view of the Adventure Builder system   \n![image][image]  \n In this view the following architectural styles are used	139	118	AdventureBuilderTwo	AVAILABLE	2019-11-21 14:04:15.479759	2	1507
1508	Consider the following view of the Adventure Builder system   \n![image][image]  \n In this view the following architectural styles are used	101	64	AdventureBuilderComponentAndConnectorOne	AVAILABLE	2019-11-21 15:13:23.894116	2	1508
1509	Frank Buschmann states that:  \n>"Architects use flexibility as a cover for uncertainty."  \n  \n	0	0	Prioritize	DISABLED	\N	2	1509
1572	An example of module in a software architecture is	143	111	Module in a software architecture	AVAILABLE	2019-10-04 09:16:53.991988	2	1572
1510	In his article *On Hammers and Nails, and Falling in Love with Technology and Design* what is the main type of influence on the architecture?	0	0	HammersNails	DISABLED	\N	2	1510
1511	Frank Buschmann states that:  \n>"There's only one escape from such situations: architects must actively break the cycle of mutual misunderstanding and mistrust!"  \n  \n	0	0	Explicit	DISABLED	\N	2	1511
1512	The *Walking Skeleton* referred in Frank Buschmann's article, *Featuritis, Performitis, and Other Deseases*:	0	0	WalkingSkeleton	DISABLED	\N	2	1512
1513	Consider the Component-and-Connector viewtype	157	138	ComponentAndConnectorTwo	AVAILABLE	2019-11-21 13:57:35.289593	2	1513
1514	The Work-assignment is an architectural style of the allocation viewtype, where	199	122	WorkAssignment	AVAILABLE	2019-11-21 15:54:01.863876	2	1514
1515	Consider an architect that is designing a component-and-connector view. In some point the architect decides that she does not need to decompose a connector with a demanding quality level. This may occur because	60	45	ConnectorDecomposition	AVAILABLE	2019-11-21 12:13:41.17079	2	1515
1516	The cohesion of a module of the architecture allows to reason about	125	109	Software architecture definition - Properties and reasoning	AVAILABLE	2019-10-03 15:45:28.202956	2	1516
1517	Consider that you intend to develop a system where it is necessary to change the emails received by the server (for instance, to remove potential virus or URLs for phishing sites). The goal is that each email is processed by this system before it is sent to other servers or it is stored locally. Additionally, the system should be easily modified to support new kinds of transformations. Which style is more suitable to satisfy these requirements?	256	227	PipesFilters	AVAILABLE	2019-11-21 15:50:44.86343	2	1517
1518	Consider the deployment architectural style of the allocation viewtype.	105	62	AllocationTwo	AVAILABLE	2019-11-21 13:57:35.289593	2	1518
1519	In the Java documentation you can find:  \n>"`public abstract class Component`\\*`extends Object`\\*`implements ImageObserver, MenuContainer, Serializable`"  \n  \nClass `Component` is:	42	23	ComponentvsModuleTwo	AVAILABLE	\N	2	1519
1520	Consider a stakeholder that is particularly concerned about the total cost of the project. When it comes to describing the system using allocation viewtypes she is interested in	114	97	AllocationStylesCost	AVAILABLE	2019-11-21 15:06:54.180074	2	1520
1560	The definition of software architecture, on the course book, is  \n>"*The software architecture of a system is the set of structures needed to reason about the system, which comprise software elements, relations among them, and properties of both.*"  \n  \nAccording to this definition	119	109	ASDefinition	AVAILABLE	2019-10-04 09:16:43.293901	2	1560
1521	Suppose that you are designing the software architecture for an enterprise application that has requirements about the maximum response time for a certain type of requests. Moreover, assume that those requests arrive at the system periodically, whereas the remaining requests have an unpredictable frequency. Finally, assume that your system will have a single server that will be executing on a predefined machine with a 12-core AMD processor. To show to the stakeholders that your system satisfies the performance requirements you have to use views of which architectural style?	232	90	RepositoryClientServerOne	AVAILABLE	2019-11-21 23:20:25.092777	2	1521
1522	There are other factors that affect the development of a software system, besides its functional requirements and quality attributes. For example, factors such as budget or available time. These factors	0	0	AtrQualNegocio	REMOVED	\N	2	1522
1523	As part of the process of creating an architecture, we talked about a framework for capturing some of the requirements for a system. In this context, **concrete scenarios** are used for	215	108	ConcreteScenarios	AVAILABLE	2019-10-04 09:05:33.123379	2	1523
1524	The client-server architectural style provides availability because	62	57	ClientServerAvailability	AVAILABLE	2019-11-21 12:56:26.88853	2	1524
1616	According to the SEI model, there are three different architectural viewtypes that are usually necessary to describe completely a software architecture.	35	33	TresTiposVista	AVAILABLE	\N	2	1616
1533	One of the most important decisions during the development of the Glasgow Haskell Compiler was to perform the type-checking before the desugaring of an Haskell program into a program in the Core language (*type-check-before-desugar*). This design decision	0	0	GHCDesugaringINGLES	DISABLED	\N	2	1533
1534	In which performance tactic it can occur that the inputs are not completely processed, even though they always start being processed	105	71	PerfomanceTacticTwo	AVAILABLE	2019-10-04 18:08:42.180229	2	1534
1535	The Tiers architectural style	66	43	Tiers	AVAILABLE	2019-11-21 12:56:26.88853	2	1535
1536	An architecture can also be represented by the set of files which contains its modules code. A suitable architectural style to represent this set of files is	93	70	ImplementationStyle	AVAILABLE	2019-11-22 23:26:12.494277	2	1536
1537	The Tiers architectural style	136	91	Tiers	AVAILABLE	2019-11-21 13:57:35.289593	2	1537
1538	In the Publish-Subscribe architectural style, the components, from the point of view of the modules they execute	121	51	PublishSubscribeEN	AVAILABLE	2019-11-21 12:56:26.88853	2	1538
1539	The Service-Oriented Architecture style	77	59	SOAClientServerPeertoPeer	AVAILABLE	2019-11-21 12:56:26.88853	2	1539
1540	Consider the following view of the Adventure Builder system   \n![image][image]  \n This view **does not** apply the architectural style	101	62	AdventureBuilderComponentAndConnectorThird	AVAILABLE	2019-11-21 15:13:23.894116	2	1540
1541	Consider the Work Assignment architectural style of the allocation viewtype.	115	87	WorkAssigment	AVAILABLE	2019-11-21 15:06:54.180074	2	1541
1542	Consider the following representation of Amazon's architecture (sorry for the figure's layout: **save trees**)   \n![image][image]  \n What is the most relevant architecture style that is used in this figure?	75	49	MicroAndAmazonTwo	AVAILABLE	2019-11-21 13:17:44.021521	2	1542
1543	In the client-server architectural style the request/reply connector is synchronous. Consider an architect that wants to describe an asynchronous interaction between clients and servers.	79	69	ClientServerSynchronous	AVAILABLE	2019-11-21 12:13:41.17079	2	1543
1544	The modifiability quality of a software system is associated with 	153	136	Modifiability quality	AVAILABLE	2019-10-02 21:47:19.441525	2	1544
1545	Suppose that you are developing a new software system and that you want some part of the system's functionality to be easily reusable in future systems. Which of the following architectural styles are more suitable to show that the system architecture meets this requirement.	36	16	Reutilizar	AVAILABLE	\N	2	1545
1546	In his article, *Who Needs and Architect?*, Martin Fowler cites Ralph Johnson definition:  \n>"Architecture is the set of decisions that must be made early in a project."  \n  \nIn his opinion:	117	105	EarlyDecisions	AVAILABLE	2019-10-03 13:23:59.360009	2	1546
1547	Consider an enterprise application that needs to keep its data persistently, but for which no one knows yet what is the volume of information that will be handled by the application. Therefore, the system's architect intends to develop the system such that it is possible to change easily the relational database (RDBMS) component used to store the application's data, replacing it with an RDBMS from another manufacturer. Given that this is a common requirement, the recommended software architecture for such applications fulfills this requirement by using a particular architectural style. Which style is it?	43	22	TrocarBDCamadas	AVAILABLE	\N	2	1547
1548	Considering again the case of the previous question, compare the architectures sketched in Figure~1.3 and Figure~1.4. The difference between the two shows	0	0	WebPartitioningDoisINGLES	REMOVED	\N	2	1548
1549	Consider the concepts of software architecture and software design	159	114	Architecture and Design	AVAILABLE	2019-10-03 13:54:43.41528	2	1549
1550	What quality we cannot reason about from this Image hosting application view?\n\n![image][image]	123	86	Reason about an Image hosting application view	AVAILABLE	2019-10-02 21:54:18.334222	2	1550
1551	To describe how easy is it to find faults with tests in a system it is necessary to	154	138	Testability	AVAILABLE	2019-10-04 17:29:59.099914	2	1551
1552	A CRUD matrix, which indicates whether each module creates, reads, updates, or deletes data (CRUD, for short) from each data entity. The CRUD matrix	145	105	UsesDataModel	AVAILABLE	2019-11-06 19:25:14.813915	2	1552
1553	As mentioned in the previous questions, the use of *workers* is one of the crucial elements in the software architecture of nginx. Which of the following sentences best describes how *workers* work in nginx?	0	0	nginxWorkersINGLES	DISABLED	\N	2	1553
1554	Given that a *worker* processes various requests during its life, how does it do it?	0	0	nginxWorkerParallelINGLES	DISABLED	\N	2	1554
1555	A software system is usually described using different architectural views	74	56	ArchitecturalViews	AVAILABLE	\N	2	1555
1556	When migrating a monolith to a microservice architecture	142	85	Microservices	AVAILABLE	2019-10-02 21:54:18.334222	2	1556
1557	The microservices impact on the team organization	140	99	Microservices	AVAILABLE	2019-10-03 13:54:43.41528	2	1557
1561	In his article *Who Needs an Architect?* Martin Fowler refers to the following architecture definition  \n>"*architecture is the set of design decisions that must be made early in a project*"  \n  \n	104	98	EarlydDecisions	AVAILABLE	2019-10-04 15:36:52.943277	2	1561
1562	Consider the architecture of the Morrison's OrderPad. In the description of the system can be read:  \n>"The pilot version included some architectural short-cuts that would not work with the full complement of stores. One of these was using a file-transfer to send data to the mainframe rather than MQ, which wouldn't perform well once many stores were active."  \n  \nThis approach means that	15	9	OrderPadIterative	DISABLED	\N	2	1562
1563	Frank Buschmann, *Introducing the Pragmatic Architect*, defines the *architecture dwarves*. These kind of architects	0	0	ArchitectDwarves	DISABLED	\N	2	1563
1564	Suppose that there are certain performance requirements for a system, and that you want to show to the stakeholders of the system that the software architecture that you designed meet those requirements. To do this	102	84	ComponentConnectorOne	AVAILABLE	\N	2	1564
1640	When the domain logic is organized using a Transaction Script pattern the most suitable data source patterns are	0	0	LogicAccessTransactionScript	REMOVED	\N	2	1640
1565	Consider the architectural views for the SocialCalc system. The following diagram depicts a proposal for a component-and-connector view of the client Spreadsheet. It can be read in the case description: *A simple improvement is for each client to broadcast its cursor position to other users, so everyone can see which cells are being worked on.*   \n![image][image]  \n	0	0	SocialCalcRemoteCursor	DISABLED	\N	2	1565
1665	Frank Buschmann, *Introducing the Pragmatic Architect*, defines the *architecture astronauts*. This kind of architect	0	0	ArchitectAstronauts	DISABLED	\N	2	1665
1566	The email system is composed of various types of components playing different roles. For example, to send an email, a user uses a *mail user agent* (MUA), to compose his message and send it. To send the message, the MUA typically connects to a *mail transfer agent* (MTA) that receives the message, analyzes the message's headers to determine the recipients and, after querying the DNS system to determine the MTA responsible for each recipient, it connects to the MTAs responsible for the destination addresses to deliver the message. Each of these MTAs receives the message and stores it locally or forwards it to others MTAs until the message reaches its destination MTA. The recipient user of the message will then use his MUA to see the messages that were sent to him. To do it, the MUA connects to an IMAP or POP server to obtain the user's messages. Those IMAP and POP servers obtain the messages for a user by reading the messages stored by the MTA. Given this simplified description of the operation of the email system, which of the following architectural styles is more appropriate to represent the pattern of interaction between the MUA and the MTA?	56	18	ArqEmailMUAMTAINGLES	AVAILABLE	2019-11-21 13:57:35.289593	2	1566
1567	Consider the architectural views for the ThousandParsec system. The following diagram depicts a proposal of application-specific types for the architectural components, where the names of the ports are missing. Between the GameServer and Repository component   \n![image][image]  \n	0	0	ThousandParsecReadWriteConnector	DISABLED	\N	2	1567
1568	Consider the architecture of the Morrison's OrderPad. The connector between the client component, executing in the Pad, and the server component, executing in the OrderPadDatabase	0	0	OrderPadReliability	DISABLED	\N	2	1568
1569	Web applications went through several evolutions over the last years. One of those evolutions was to make their user interfaces more sophisticated, by leveraging on new technologies available in the browsers, such as, for example, Javascript, to provide a more satisfying user experience. What were the most visible consequences of such an evolution on the typical software architecture of a web application?	46	35	WebEvolutionINGLES	AVAILABLE	\N	2	1569
1570	When comparing Amazon Silk with Google Chrome	85	76	SilkConnections	AVAILABLE	\N	2	1570
1571	In Chrome system, to show that it provides mobility qualities by managing the number of tab, it is necessary to use	110	74	ChromeDynamicReconfiguration	AVAILABLE	\N	2	1571
1573	Suppose that you are developing a web application that keeps in a database some information that is introduced by the users and that one of the requirements is that the information should be kept confidential, such that no one but the author of the information should be able to see it (but the author may access that information whenever he wants it). How would you satisfy this requirement?	0	0	SecurityINGLES	DISABLED	\N	2	1573
1574	Consider the architectural views for the SocialCalc system. The following diagram depicts a proposal for a component-and-connector view of the client Spreadsheet. A ConflictResolution module is used when local commands conflict with remote commands.   \n![image][image]  \n	0	0	SocialCalcConflictResolution	DISABLED	\N	2	1574
1575	In the interview Werner Vogels from Amazon gives to Jim Gray, Werner Vogels says that  \n>"The stored data formats are decoupled from the format in which you communicate data items. If there is no need for sharing schemas of the actual storage layout, you can focus on making sure that the service interfaces can evolve in a way that allows you to handle variations of data formats."  \n  \nWhich means that in the software architecture of Amazon's systems	128	100	MicroAndAmazonThree	AVAILABLE	2019-11-21 15:50:44.86343	2	1575
1576	In the Service Oriented Architecture style it is common to have a specialized component, named *Enterprise Service Bus* (ESB). The goal of using of an ESB in a system is	140	97	ComponentAndConnectorStyleTwo	AVAILABLE	2019-11-21 15:50:44.86343	2	1576
1577	In the description of the Chrome case you can read:  \n*On Android devices, Chrome leverages the same multi-process architecture as the desktop version - there is a browser process, and one or more renderer processes. The one difference is that due to memory constraints of the mobile device, Chrome may not be able to run a dedicated renderer for each open tab. Instead, Chrome determines the optimal number of renderer processes based on available memory, and other constraints of the device, and shares the renderer process between the multiple tabs.*  \nThis description can be represented by a view of viewtype Component-and-Connector using the architectural style	232	187	CHMobilityArchitecturalStyleINGLES	AVAILABLE	2019-11-21 13:31:02.822493	2	1577
1578	To achieve a faster time-to-market, software companies are increasingly using a strategy of incremental releases of their software, where each new release has a set of new features. Which architectural style is better to analyse whether the system's software architecture is adequate for the planned incremental releases?	110	98	IncrementalReleasesINGLES	AVAILABLE	2019-11-06 18:18:51.835992	2	1578
1579	A connector may be attached to components of different types because	56	46	ConnectorAttach	AVAILABLE	2019-11-22 23:26:12.494277	2	1579
1580	Which of the following sentences best captures the restrictions regarding which components may execute in which machines in the Deployment style?	84	55	RelacaoComponentesMaquinasINGLES	AVAILABLE	2019-11-21 22:08:51.773121	2	1580
1582	The Peer-to-Peer architectural style provides high scalability and availability. In the context of a file sharing system	88	78	PeerToPeerSpace	AVAILABLE	2019-11-21 15:13:23.894116	2	1582
1583	Consider the peer-to-peer architectural style	115	79	PeerToPeer	AVAILABLE	2019-11-21 15:50:44.86343	2	1583
1584	The architecture of the HDFS system only allows the existence of one NameNode. Given the responsibilities of this component and the current architecture of HDFS, what would be the consequences of adding the possibility of having replicas of the NameNode in the system?	212	109	HadoopNameNodeReplica	AVAILABLE	2019-10-23 15:28:19.127857	2	1584
1585	In the Chrome system the use of a process per tab results form the application of a tactic of	106	82	ChromeTabSecurity	AVAILABLE	2019-10-23 23:09:52.689666	2	1585
1586	Considering the availability architectural quality, the tactic of retry	93	85	OmissionRetry	AVAILABLE	2019-10-23 19:20:54.778075	2	1586
1666	A voting tactic can be used to	103	74	AvailabilityTwo	AVAILABLE	2019-10-22 12:31:00.314429	2	1666
1589	Consider the architectural views for the ThousandParsec system. The following diagram depicts a fragment of a proposal for the decomposition view of the system. The ThousandParsec protocol   \n![image][image]  \n	0	0	ThousandParsecModule	DISABLED	\N	2	1589
1590	Consider the architectural views for the ThousandParsec system. The following diagram depicts a proposal of application-specific types for the architectural components, where the names of the ports are missing. Between the GameClient and GameServer components   \n![image][image]  \n	0	0	ThousandParsecTPConnector	DISABLED	\N	2	1590
1855	Consider the (partial) component-and-connector view for the :SpreasdSheet component of the SocialCalc system   \n![image][image]  \n The sub1 port	62	27	SocialCalcBroadcastEvents	DISABLED	\N	2	1855
1591	In the component-and-connector viewtype connectors can be complex, which means that they provide a rich set of qualities to the interaction between the components that they connect. These complex connectors can be documented in another view using a set of components interacting through simpler connectors.	146	82	ComponentAndConnectorViewtypeOne	AVAILABLE	2019-11-21 14:30:55.568524	2	1591
1592	Consider the Service-Oriented Architecture architectural style	104	93	SOA	AVAILABLE	2019-11-21 15:54:01.863876	2	1592
1593	Consider the following view of the Adventure Builder case study   \n![image][image]  \n	265	178	AdventureBuilderTwo	AVAILABLE	2019-11-01 22:07:10.494889	2	1593
1594	Consider the following architectural view of the Adventure Builder system   \n![image][image]  \n According to this view the stakeholders can see that the Adventure Builder system	132	77	AdventureBuilderOne	AVAILABLE	2019-11-21 23:20:25.092777	2	1594
1595	In the description of the Chrome system can be read  \n>"As the user types, the Omnibox automatically proposes an action, which is either a URL based on your navigation history, or a search query."  \n  \nThe above sentence refers to	63	54	ChromeUsability	AVAILABLE	2019-10-23 15:39:55.604858	2	1595
1596	In the description of the Microservices Architecture by James Lewis and Martin Fowler can be read:  \n>"The microservice approach to division ..., splitting up into services organized around business capability. Such services take a broad-stack implementation of software for that business area, including user-interface, persistent storage, and any external collaborations. Consequently the teams are cross-functional, including the full range of skills required for the development: user-experience, database, and project management."  \n  \nConsidering the architecture influence cycle, which influence factor it is being considered?	113	74	MicroservicesProject	AVAILABLE	2019-10-02 23:29:40.086745	2	1596
1597	Suppose that you are designing the software architecture for an enterprise application that has security requirements about the confidentiality of some of its data. To show to the stakeholders that your system satisfies the security requirements you have to use views of which architectural style?	0	0	SegurancaINGLES	DISABLED	\N	2	1597
1692	Jeff Atwood wrote an article in its blog about performance of software systems that is entitled, *Hardware is Cheap, Programmers are Expensive*. Which performance tactic(s) is he suggesting	104	62	PerformanceThree	AVAILABLE	2019-10-02 23:33:55.015501	2	1692
1598	Consider the following availability scenario for the Adventure Builder system  \n>"The Consumer Web site is available to the user 24x7. If an instance of OPC application fails, the fault is detected and the system administrator is notified in 30 seconds; the system continues taking order requests; another OPC instance is created; and data remains in consistent state."  \n  \nand the following architectural view   \n![image][image]  \n	265	222	AdventureBuilderModuleTwo	AVAILABLE	2019-11-01 22:07:10.494889	2	1598
1599	Consider a stakeholder that is particularly concerned about the total cost of the project. When it comes to describing the system using allocation viewtypes is interested in	88	76	AllocationStylesCost	AVAILABLE	2019-11-21 15:19:46.178664	2	1599
1600	Consider the following view of the Adventure Builder system   \n![image][image]  \n In this view the following architectural styles are used	111	85	AdventureBuilderOne	AVAILABLE	2019-11-21 14:04:15.479759	2	1600
1601	Consider a situation where the stream of a movie is being done trough a slow network. Which performance tactic should be applied?	130	109	Performance tactic	AVAILABLE	2019-10-04 17:03:09.261816	2	1601
1602	A view of the *Uses* style that contains a loop in the uses relationships	141	112	UsaCircularINGLES	AVAILABLE	2019-11-06 20:01:35.072903	2	1602
1603	Consider the following performance scenario:\n\n"During the normal period, all periods except the enrolment period, a student accessing the Fénix system should finish a write transaction in less than 2 seconds."	138	118	Performance scenario 	AVAILABLE	2019-10-04 09:20:18.221354	2	1603
1604	The quality(ies) that is(are) more relevant to views of the component-and-connector viewtype is(are):	89	84	ComponentViewType	AVAILABLE	2019-11-21 12:13:41.17079	2	1604
1605	Consider the following representation of Amazon's architecture   \n![image][image]  \n What is the most relevant architecture style that is used in this figure?	80	72	MicroAndAmazonTwo	AVAILABLE	2019-10-23 19:19:54.166728	2	1605
1606	The Install and Implementation architectural styles	124	90	InstallImplementationStylesEN	AVAILABLE	2019-11-28 11:15:02.0394	2	1606
1607	In the Graphite system the component *carbon* provides to *webapp* components an access interface to the *buffers* in order to improve the quality of	142	84	GraphiteScenarioTacticsOne	AVAILABLE	2019-10-23 22:54:53.389187	2	1607
1608	Consider the Figure that describes the use of caches in web services.   \n![image][image]  \n In that Figure, there is a rectangle with the name *Cache* within another rectangle with the name *Request Node*. Taking into account the description made in the text and the goal of that Figure, those rectangles correspond to which type of software elements?	101	50	ModuleComponentOne	AVAILABLE	\N	2	1608
1609	Consider the Decomposition architectural style of the Module viewtype	144	126	ModuleViewtypeOne	AVAILABLE	2019-11-06 19:47:12.894684	2	1609
1610	Load balancers	136	119	Scalable web architecture	AVAILABLE	2019-10-02 21:54:18.334222	2	1610
1611	Using queues to manage client requests, as represented in the figure, has the following impact.\n\n![image][image]	137	119	Queues	AVAILABLE	2019-10-04 15:08:14.085405	2	1611
1613	Consider the following architectural view of the Adventure Builder system   \n![image][image]  \n In this component-and-connector view the interactions the interactions between components follow the architectural style(s)	88	49	AdventureBuilderTwo	AVAILABLE	2019-11-21 15:13:23.894116	2	1613
1614	In the Continous integration case study can be read about Jenkins  \n>"It takes advantage of the JUnit XML standard for unit test and code coverage reporting to integrate reports from a variety of test tools. Jenkins originated with Sun, but is very widely used and has a robust open-source community associated with it."  \n  \nConsider that a scenario is written from the above sentence	62	49	ContinousIntegrationScenariosTacticsOne	DISABLED	\N	2	1614
1639	Consider the architectural views for the SocialCalc system. The following diagram depicts a proposal for a component-and-connector view of the client Spreadsheet. A Parser module is used when loading a file   \n![image][image]  \n	0	0	SocialCalcParser	DISABLED	\N	2	1639
1615	In the Infinispan case study can be read  \n>"When persisting for durability, persistence can either be online, where the application thread is blocked until data is safely written to disk, or offline, where data is flushed to disk periodically and asynchronously. In the latter case, the application thread is not blocked on the process of persistence, in exchange for uncertainty as to whether the data was successfully persisted to disk at all."  \n  \nFrom the description we can infer a trade-off between the qualities of	62	51	InfinispanScenariosTacticsOne	DISABLED	\N	2	1615
1856	What type of change has as source of stimulus a system administrator?	117	88	Modifiability scenario	AVAILABLE	2019-10-22 20:16:28.437085	2	1856
1617	Consider the following distinction between Monoliths and Microservices made by Matin Fowler   \n![image][image]  \n If we try to map this figure into a set of views we will need.	57	42	MicroAndAmazonOne	AVAILABLE	\N	2	1617
1618	Which stakeholder may require the quality of time to market?	144	101	Time to market	AVAILABLE	2019-10-04 18:26:22.638079	2	1618
1619	What is an architectural significant requirement?	153	127	Architectural Significant Requirement	AVAILABLE	2019-10-03 15:41:54.892205	2	1619
1620	Consider the following representation of the interaction between two services in the Uber system.\n\n![image][image]\n\nWhich quality can we reason about this representation of the software elements, their relations and properties?	145	111	Uber	AVAILABLE	2019-10-02 21:47:19.441525	2	1620
1621	When the domain logic is organized using a Table Module pattern	0	0	LogicAccessTableModule	REMOVED	\N	2	1621
1622	When the domain logic is organized using a Transaction Script pattern the domain objects	0	0	LogicAccessTransactionScriptDomainObjects	REMOVED	\N	2	1622
1623	Consider that an architect needs to design a system which interacts with two external sources of information, and it has to import some of the information to store it in the system's internal database. The stakeholders inform him that it will be necessary to include new sources of information in the future, besides the two already identified, but they cannot precisely define which they are. This changes will occur after the first version of the system is in production. Additionally, the stakeholders define a short period of time to integrate a new source of information. Given this requirements the architect should	96	54	ModifiabilityOneOne	AVAILABLE	\N	2	1623
1624	Two of the *stakeholders* for the Glasgow Haskell Compiler were the UK government and the researchers that want to do research on functional programming languages. Which of these *stakeholders* had a more significant influence in the software architecture of the system?	0	0	GHCStakeholdersINGLES	DISABLED	\N	2	1624
1625	Suppose that, to satisfy a security requirement related with possible attacks coming from users that access your system through the Internet, you want to use the tactic named *Limit Exposure*. How does the use of that tactic manifests in the architectural views of your system?	0	0	SecurityINGLES	DISABLED	\N	2	1625
1626	Consider the following excerpt from Nginx case study  \n>"nginx configuration is kept in a number of plain text files which typically reside in /usr/local/etc/nginx or /etc/nginx. The main configuration file is usually called nginx.conf. To keep it uncluttered, parts of the configuration can be put in separate files which can be automatically included in the main one. However, it should be noted here that nginx does not currently support Apache-style distributed configurations (i.e., .htaccess files). All of the configuration relevant to nginx web server behavior should reside in a centralized set of configuration files."  \n  \nWhen comparing the configuration in Nginx with the configuration in Apache we can say that	62	22	NginxScenariosTacticsOne	DISABLED	\N	2	1626
1627	Consider the architecture of the Morrison's OrderPad. The decision between the use of a Native application or HTML5 on the implementation of the client in the Pad	0	0	OrderPadPortability	DISABLED	\N	2	1627
1628	The *Composer UI* component of Graphite system, described as - *Graphite's Composer UI provides a point-and-click method to create a graph from which you can simply copy and paste the URL* - to be effective needs to show to the user the changes she performs in the graph such that she has immediate feedback whenever she clicks on a option. To do so, the architecture needs to include	29	10	GraphiteComposerUIPerformance	REMOVED	\N	2	1628
1629	Frank Buschmann states that:  \n>"Featuritis is the tendency to trade functional coverage for quality - the more functions the earlier they're delivered, the better."  \n  \n	0	0	Featuritis	DISABLED	\N	2	1629
1630	In Nginx, given that a *worker* processes various requests during its life, how does it do it?	30	9	nginxTwo	DISABLED	\N	2	1630
1631	Suppose you have a system with a client-server architecture that was designed to support the simultaneous existence of at most 100 clients, without specific requirements for availability. The solution adopted and put into operation four years ago is a single server component to which all clients connect to. This solution satisfies the initial requirements but with the recent increase in the maximum number of clients to 200, the system no longer has acceptable performance. Not knowing anything else about the system's architecture, which solution do you propose to solve the system's performance problems?	38	13	AumentarDesempenhoClienteServidor	AVAILABLE	\N	2	1631
1632	Consider the following application-specific types that were defined for a component-and-connector view that depicts the components within `Carbon` component.   \n![image][image]  \n	67	30	GraphiteCarbon	AVAILABLE	\N	2	1632
1633	What is the solution in the Uber system to support one million of writes in the geospatial index per second? 	134	125	Uber	AVAILABLE	2019-10-04 18:48:29.575742	2	1633
1634	Frank Buschmann states that:  \n>"Overly flexible systems are hard to configure, and when they're finally configured, they lack qualities like performance or security."  \n  \n	0	0	Flexibilitis	DISABLED	\N	2	1634
1635	An architect needs to show that a security tactic of limit exposure will be effectively provided by the executing system. Therefore, she decides to design	0	0	DeploymentStyleLimitExposure	DISABLED	\N	2	1635
1636	Consider the architectural views for the SocialCalc system. The following diagram depicts a proposal for a component-and-connector view of the system. According to this representation   \n![image][image]  \n	0	0	SocialCalcServer	DISABLED	\N	2	1636
1637	Consider the architectural views for the ThousandParsec system. The following diagram depicts a fragment of a proposal for the decomposition view of the system. The AI players should be described   \n![image][image]  \n	0	0	ThousandParsecAI	DISABLED	\N	2	1637
1638	Consider the architecture of the Morrison's OrderPad. The final interaction between the OrderPadDatabase component and Mainframe component is supported by	0	0	OrderPadMainframeConnector	DISABLED	\N	2	1638
1641	Consider the following performance scenario.\n\n"During the enrolment period the Fénix system be able to completely enrol 5.000 students in less than 30 minutes."	140	112	Performance scenario	AVAILABLE	2019-10-03 15:41:54.892205	2	1641
1642	Why does the Uber system need to use consistent hashing in its software architecture?	142	126	Uber	AVAILABLE	2019-10-02 23:29:40.086745	2	1642
1643	Knowing the deployment structure in the Hadoop system is critical to the effective system operation. Therefore, for each deployment, the administrator can configure a script that returns a node's rack identification given a node's address (see section 8.3.2).	0	0	HadoopInstalacaoINGLES	REMOVED	\N	2	1643
1644	According to the document that describes the Glasgow Haskell Compiler:  \n>"At the highest level, GHC can be divided into three distinct chunks:  \n-  The compiler itself.  \n-  The Boot Libraries.  \n-  The Runtime System (RTS).  \n"  \n  \nWhat is the most architecturally correct way of classifying the three *chunks* that this text refers to?	0	0	GHCChunksINGLES	DISABLED	\N	2	1644
1645	From the stakeholders perspective the use of low cost servers to build the clusters is:	0	0	HadoopStakeholdersEurosINGLES	DISABLED	\N	2	1645
1646	When someone uses the Domain Model pattern to implement the domain logic layer of an enterprise application, it is common to use also the Service Layer pattern. The Service Layer pattern is used in these cases	0	0	ServiceLayer	REMOVED	\N	2	1646
1647	The Unit of Work pattern is often used in enterprise applications	0	0	UnitOfWork	REMOVED	\N	2	1647
1648	Considering the description of the *CheckpointNode* made in Section~8.2.5, which architectural style best represents the interaction between the *CheckpointNode* and the *NameNode* components?	0	0	HadoopCheckpointNodeINGLES	REMOVED	\N	2	1648
1649	Which quality of the Uber system is related with the ability to support different types of demand and supply relationships?	133	107	Uber	AVAILABLE	2019-10-03 15:41:54.892205	2	1649
1650	The uses architectural style allows to assess the impact of changes in modules	207	107	UtilizacaoImpactoAlteracoesINGLES	AVAILABLE	2019-11-01 22:07:10.494889	2	1650
1651	Suppose that you are implementing a web application and that you decided to use an HDFS system to store the data of your application---that is, your web application will be a client of the HDFS system. How does this decision affects the architecture of your web application?	60	25	HadoopComoDatabaseINGLES	AVAILABLE	\N	2	1651
1652	When comparing Amazon Silk with Google Chrome	99	71	SilkCaching	AVAILABLE	\N	2	1652
1805	What is the type of stimulus when a component responds with an incorrect value?	116	108	Availability stimulus	AVAILABLE	2019-10-24 10:27:01.310986	2	1805
1653	According to the document that describes the Glasgow Haskell Compiler:  \n>"The Runtime System is a library of mostly C code that is linked into every Haskell program. It provides the support infrastructure needed for running the compiled Haskell code, including the following main components:  \n-  Memory management, including a parallel, generational, garbage collector;  \n-  Thread management and scheduling;  \n-  The primitive operations provided by GHC;  \n-  A bytecode interpreter and dynamic linker for GHCi.  \n"  \n  \nWhich system qualities are improved by the design decision of creating the Runtime System, described above?	0	0	GHCRTSINGLES	DISABLED	\N	2	1653
1654	Like many other compilers, the compilation of an Haskell program with the Glasgow Haskell Compiler uses the Pipe-and-Filter style, creating a *pipeline* composed of several compilation phases. The goal of using this architectural style in GHC is	0	0	GHCPipeAndFilterINGLES	DISABLED	\N	2	1654
1655	Which of the following sentences better describes the ZeroMQ system?	0	0	ZeroMQAppsINGLES	DISABLED	\N	2	1655
1656	According to the document that describes ZeroMQ:  \n>"The idea was to launch one worker thread per CPU core---having two threads sharing the same core would only mean a lot of context switching for no particular advantage."  \n  \nWhich architectural style is more adequate to represent the information presented above?	0	0	ZeroMQWorkersPerCoreINGLES	DISABLED	\N	2	1656
1657	According to the document that describes ZeroMQ:  \n>"Messaging patterns form a layer (the so-called "scalability layer") on top of the transport layer (TCP and friends). Individual messaging patterns are implementations of this layer."  \n  \nWhat is the main advantage of this layered architecture adopted by ZeroMQ?	0	0	ZeroMQMessagingPatternsINGLES	DISABLED	\N	2	1657
1658	ZeroMQ uses dynamic batching to control the performance of the system. The goal of this approach is	0	0	ZeroMQBatchingINGLES	DISABLED	\N	2	1658
1659	According to the document that describes ZeroMQ:  \n>"ØMQ uses a lock-free queue in pipe objects to pass messages between the user's threads and ØMQ's worker threads. There are two interesting aspects to how ØMQ uses the lock-free queue. First, each queue has exactly one writer thread and exactly one reader thread. If there's a need for 1-to-N communication, multiple queues are created. Given that this way the queue doesn't have to take care of synchronising the writers (there's only one writer) or readers (there's only one reader) it can be implemented in an extra-efficient way."  \n  \nThe architectural style that better represents the interaction pattern described above is	0	0	ZeroMQLockFreeINGLES	DISABLED	\N	2	1659
1660	Consider the Infinispan system when it is configured as a remote data grid. The relation between the Applications and the Grid is	17	8	InfinispanOne	DISABLED	\N	2	1660
1661	Consider the following representation of the Buildbot system.   \n![image][image]  \n The architecture style between the Buildbot Master and the Clients is:	17	11	JenkinsOne	DISABLED	\N	2	1661
1697	Consider the change in the architecture introduced from Figure~1.9 to Figure~1.10 in the document that describes the use of caches in web services (see annex). That change has the goal and the consequence of, respectively	0	0	WebCacheGlobalINGLES	REMOVED	\N	2	1697
1662	In the description of Infinispan system can be read  \n>"Infinispan supports several pluggable cache stores-adapters that can be used to persist data to disk or any form of secondary storage. The current default implementation is a simplistic hash bucket and linked list implementation, where each hash bucket is represented by a file on the filesystem. While easy to use and configure, this isn't the best-performing implementation."  \n  \nThe architectural style(s) that should be used to illustrate the sentence is (are)	19	11	InfinispanTwo	DISABLED	\N	2	1662
1663	In the Continuous Integration case can be read  \n>"Build notification: The outcomes of builds generally need to be communicated to interested clients, either via pull (Web, RSS, RPC, etc.) or push notification (e-mail, Twitter, etc.) This can include notification of all builds, or only failed builds, or builds that haven't been executed within a certain period."  \n  \nThe architectural style used in push notifications is	19	18	JenkinsTwo	DISABLED	\N	2	1663
1664	According to the document that describes the architecture of web services (attached at the end of this document), one of the approaches introduced in Section~1.2 is *partitioning*, shown in Figure~1.4. The use of *partitioning*	0	0	WebPartioningINGLES	REMOVED	\N	2	1664
1767	Consider the following sentence by Melvin Conways, also known as Conway's Law  \n>"organizations which design systems ... are constrained to produce designs which are copies of the communication structures of these organizations"  \n  \n	144	69	ArchitectureInfluenceCycleOne	AVAILABLE	2019-10-03 16:23:04.967799	2	1767
1667	According to the document that describes nginx:  \n>"Traditional process- or thread-based models of handling concurrent connections involve handling each connection with a separate process or thread, and blocking on network or input/output operations. nginx followed a different model. It was actually inspired by the ongoing development of advanced event-based mechanisms in a variety of operating systems. What resulted is a modular, event-driven, asynchronous, single-threaded, non-blocking architecture which became the foundation of nginx code."  \n  \nThe decision of turning nginx into an *event-driven*, *asynchronous*, *single-threaded*, and *non-blocking* system was made because	0	0	nginxEventDrivenINGLES	DISABLED	\N	2	1667
1668	Consider the change in the architecture associated with the use of caches in web services shown in the figure   \n![image][image]  \n That change has the goal and the consequence of, respectively	140	63	PerformanceOneOne	AVAILABLE	2019-10-03 13:23:59.360009	2	1668
1669	Some usability qualities are not architectural because	76	34	UsabilityNonArchitecturalEN	AVAILABLE	\N	2	1669
1670	According to the document that describes nginx:  \n>"While handling a variety of actions associated with accepting, processing and managing network connections and content retrieval, nginx uses event notification mechanisms and a number of disk I/O performance enhancements in Linux, Solaris and BSD-based operating systems, like kqueue, epoll, and event ports. The goal is to provide as many hints to the operating system as possible, in regards to obtaining timely asynchronous feedback for inbound and outbound traffic, disk operations, reading from or writing to sockets, timeouts and so on."  \n  \nThe goal of this approach used in the development of nginx was	0	0	nginxOSOptimizationsINGLES	DISABLED	\N	2	1670
1671	According to the document that describes nginx:  \n>"Traditional process- or thread-based models of handling concurrent connections involve handling each connection with a separate process or thread, and blocking on network or input/output operations."  \n  \nThe architectural style that better describes the model presented above for processing requests is	0	0	nginxProcessThreadINGLES	DISABLED	\N	2	1671
1672	Consider the following figure that presents an architectural view of an *Image Hosting Application*.   \n![image][image]  \n The replication between the Image File Storage *n* and Image File Storage *nb*	107	101	DataStorageAvailability	AVAILABLE	2019-10-03 14:08:38.029894	2	1672
1673	In the context of the FenixEdu case study, the business case was to	68	39	FenixOne	DISABLED	\N	2	1673
1674	Consider the following excerpt about the Scalable web architecture and distributed systems case study about two different possible implementations of a global cache  \n>"The majority of applications leveraging global caches tend to use the first type, where the cache itself manages eviction and fetching data to prevent a flood of requests for the same data from the clients. However, there are some cases where the second implementation makes more sense. For example, if the cache is being used for very large files, a low cache hit percentage would cause the cache buffer to become overwhelmed with cache misses; in this situation it helps to have a large percentage of the total data set (or hot data set) in the cache."  \n  \n	142	58	ScalableArchitectureOne	AVAILABLE	2019-10-04 18:51:19.826632	2	1674
1675	How does the software architecture allows the restriction of the vocabulary?	151	42	Restrict vocabulary	AVAILABLE	2019-10-03 13:54:43.41528	2	1675
1676	How does the software architecture allow the incorporation of independently developed modules?	136	130	Allowing the incorporation of independently developed components	AVAILABLE	2019-10-03 09:58:58.649725	2	1676
1677	Consider the following excerpt about the Scalable web architecture and distributed systems case study  \n>"Employing such a strategy maximizes data locality for the requests, which can result in decreased request latency. For example, let's say a bunch of nodes request parts of B: partB1, partB2, etc. We can set up our proxy to recognize the spatial locality of the individual requests, collapsing them into a single request and returning only bigB, greatly minimizing the reads from the data origin."  \n  \nThe quality that is achieved with this tactic is	128	104	ScalableArchitectureTwo	AVAILABLE	2019-10-02 23:33:55.015501	2	1677
1678	Consider again the architecture shown in Figure~1.3, where redundancy was introduced into the system. In this particular case, introducing redundancy into the architecture has the goal of	0	0	WebRedundancyINGLES	REMOVED	\N	2	1678
1679	One of the best practices in the design of a software architecture is to create a skeleton system. What is its purpose?	44	13	SkeletonSystemINGLES	AVAILABLE	\N	2	1679
1680	According to the document that describes ZeroMQ:  \n>"One of the requirements for ØMQ was to take advantage of multi-core boxes; in other words, to scale the throughput linearly with the number of available CPU cores."  \n  \nTo satisfy this requirement, the solution adopted by ZeroMQ was	0	0	ZeroMQScaleMulticoreINGLES	DISABLED	\N	2	1680
1681	Knowing that in the document describing ZeroMQ there is the following statement:  \n>"ØMQ is a library, not a messaging server."  \n  \nWhich views are needed to describe the software architecture of ZeroMQ?	0	0	ZeroMQAsLibraryINGLES	DISABLED	\N	2	1681
1682	According to the document that describes ZeroMQ:  \n>"It took us several years working on AMQP protocol [...] to realise that there's something wrong with the classic client/server model of smart messaging server (broker) and dumb messaging clients."  \n  \nWhat is the main problem, according to the authors, of the *broker*-based model?	0	0	ZeroMQBrokerINGLES	DISABLED	\N	2	1682
1683	Frank Buschmann cites the characterization Marquardt does of Performitis:  \n>"Each part of the system is directly influenced by local performance tuning measures. There is no global performance strategy, or it ignores other qualities of the system as testability and maintainability."  \n  \nFrom this problem you can conclude that:	62	50	RequirementsOne	DISABLED	\N	2	1683
1684	The design of the MediaWiki architecture was constrained the requirement that the solution should have relatively low cost. Due to this restriction it was taken the architectural decision of	45	14	MWLowCostEN	DISABLED	\N	2	1684
1685	Consider the following informal view of an Image Hosting System   \n![image][image]  \n	118	71	ImageHostingReads	AVAILABLE	2019-10-04 16:30:22.194975	2	1685
1686	In the Graphite system the component *carbon* provides to *webapp* components an access interface to the *buffers* in order to improve the quality of	80	62	GraphiteScenarioTacticsOne	REMOVED	2019-10-23 15:35:36.879316	2	1686
1687	Views of the module viewtype can be used to support requirements traceability analysis, determine how the functional requirements of a system are supported. This is represented by	197	149	ModuleTraceability	AVAILABLE	2019-11-06 19:47:12.894684	2	1687
1766	In the Graphite system the component *carbon* provides to *webapp* components an access interface to the *buffers* in order to improve the quality(ies) of	108	35	GPCarbonBufferInterfaceEN	REMOVED	2019-10-22 20:22:40.957034	2	1766
1688	In the Continous integration case study can be read about future features for Pony-Build  \n>"Currently, each continuous integration system reinvents the wheel by providing its own build configuration language, which is manifestly ridiculous; there are fewer than a dozen commonly used build systems, and probably only a few dozen test runners. Nonetheless, each CI system has a new and different way of specifying the build and test commands to be run. In fact, this seems to be one of the reasons why so many basically identical CI systems exist: each language and community implements their own configuration system, tailored to their own build and test systems, and then layers on the same set of features above that system. Therefore, building a domain-specific language (DSL) capable of representing the options used by the few dozen commonly used build and test tool chains would go a long way toward simplifying the CI landscape."  \n  \nSuppose that you are the architect that has to change the architecture to accomodate this new feature. Therefore, as an architect	30	17	ContinousIntegrationViewsTwo	DISABLED	\N	2	1688
1689	In wikipedia you can find the following fragment of a definition:  \n>"An individual software component is a software package, or a module that encapsulates a set of related functions."  \n  \nAccording to the definitions taught in the course the above *individual software component* corresponds to:	36	21	ComponentvsModule	AVAILABLE	\N	2	1689
1690	According to the document that describes ZeroMQ:  \n>"The objects that handle data transfer are composed of two parts: the session object is responsible for interacting with the ØMQ socket, and the engine object is responsible for communication with the network. There's only one kind of the session object, but there's a different engine type for each underlying protocol ØMQ supports. Thus, we have TCP engines, IPC engines, PGM engines, etc. The set of engines is extensible---in the future we may choose to implement, say, a WebSocket engine or an SCTP engine."  \n  \nSupposing that the code implementing the *session object* does not need to be changed when a new type of *engine* is added to the system, which architectural views are better to show this extensibility aspect of the system?	0	0	ZeroMQExtensibleEnginesINGLES	DISABLED	\N	2	1690
1691	According to the document that describes ZeroMQ:  \n>"In early versions of ØMQ the API was based on AMQP's model of exchanges and queues. I spent the end of 2009 rewriting it almost from scratch to use the BSD Socket API instead."  \n  \nWhich requirements were targeted by this change in the system?	0	0	ZeroMQBSDSocketsINGLES	DISABLED	\N	2	1691
1693	In the continuation of the description presented in the previous question, later in the document there is this passage:  \n>"Caching in nginx is implemented in the form of hierarchical data storage on a filesystem. Cache keys are configurable, and different request-specific parameters can be used to control what gets into the cache. Cache keys and cache metadata are stored in the shared memory segments, which the cache loader, cache manager and workers can access."  \n  \nWhich architectural style is more adequate to represent the use of cache in nginx?	0	0	nginxCachingINGLES	DISABLED	\N	2	1693
1694	Some of the *architectural drivers* of the Glasgow Haskell Compiler are related with the system's extensibility, and one of the solutions adopted by its authors to provide that extensibility was the introduction of *user-defined rewrite rules*, described in the document as follows:  \n>"The core of GHC is a long sequence of optimisation passes, each of which performs some semantics-preserving transformation, `Core` into `Core`. But the author of a library defines functions that often have some non-trivial, domain-specific transformations of their own, ones that cannot possibly be predicted by GHC. So GHC allows library authors to define rewrite rules that are used to rewrite the program during optimisation. In this way, programmers can, in effect, extend GHC with domain-specific optimisations."  \n  \nHow does this solution manifests in the software architecture of the system?	0	0	GHCRewriteRulesINGLES	DISABLED	\N	2	1694
1695	According to the document that describes the Glasgow Haskell Compiler:  \n>"As the popularity of the Haskell language has grown, there has been an increasing need for tools and infrastructure that understand Haskell source code, and GHC of course contains a lot of the functionality necessary for building these tools: a Haskell parser, abstract syntax, type checker and so on. With this in mind, we made a simple change to GHC: rather than building GHC as a monolithic program, we build GHC as a library, that is then linked with a small Main module to make the GHC executable itself, but also shipped in library form so that users can call it from their own programs. At the same time we built an API to expose GHC's functionality to clients."  \n  \nWhich architectural diagram is more adequate to represent the information presented above?	0	0	GHCAsLibraryINGLES	DISABLED	\N	2	1695
1696	According to the document that describes the Glasgow Haskell Compiler:  \n>"Once the `Core` program has been optimised, the process of code generation begins. The code generator first converts the `Core` into a language called `STG`, which is essentially just `Core` annotated with more information required by the code generator. Then, `STG` is translated to `Cmm`, a low-level imperative language with an explicit stack. From here, the code takes one of three routes:  \n-  Native code generation: [...]  \n-  LLVM code generation: [...]  \n-  C code generation: [...]  \n"  \n  \nThat is, GHC may use one of three alternative code generators, which have different qualities (omitted in the excerpt presented above). Supposing that you want to present an architectural diagram to represent the description presented above, which one seems more adequate?	0	0	GHCCodeGenerationINGLES	DISABLED	\N	2	1696
1698	Consider the paragraph marked with the number 2 in the document that describes the use of caches in web services (see annex), where the failure of a node in the distributed cache is discussed. When that happens, what are the consequences for the system?	0	0	WebMissingCacheNodeINGLES	REMOVED	\N	2	1698
1699	There are other factors that affect the development of a software system, besides its functional requirements and quality attributes. For example, factors such as budget or available time. These factors	89	65	AtrQualNegocio	AVAILABLE	2019-10-02 23:33:55.015501	2	1699
1700	In his article, *Featuritis, Performitis, and Other Deseases*, Frank Buschmann claims that:	0	0	FeaturitisPerformitisFlexibilities	DISABLED	\N	2	1700
1701	An architectural view	45	0	SeveralStylesViewEN	REMOVED	\N	2	1701
1868	Which of the following performance tactics address the problem of resources contention?	124	62	Contention management	AVAILABLE	2019-10-04 17:42:05.269264	2	1868
1893	Consider the following figure that presents a Proxy Server, which collapses requests from different users.   \n![image][image]  \n	54	43	ProxyServer	AVAILABLE	\N	2	1893
1794	In a microservices architecture, aggregates are used as a unit of processing	124	41	AggregateOne	AVAILABLE	\N	2	1794
1795	When applying Attribute-Driven Design (ADD) to the FenixEdu system the creation of a view where there are redundant web servers, load balancers and database servers	157	126	FenixADD	AVAILABLE	2019-10-23 22:47:54.029158	2	1795
1702	On the course slides you can find the following definition of architecture:  \n>"The software architecture of a program or computing system is the structure or structures of the system, which comprise software elements, the externally visible properties of those elements, and the relationships among them."  \n  \nHowever, in the book you can find another definition:  \n>"The software architecture of a system is the set of structures needed to reason about the system, which comprise the software elements, relations among them, and the properties of both."  \n  \n	40	28	ArchitectureDefinition	AVAILABLE	\N	2	1702
1703	Consider the following fragment of the *MediaWiki* system description:  \n*To optimize the delivery of JavaScript and CSS assets, the ResourceLoader module was developed to optimize delivery of JS and CSS. Started in 2009, it was completed in 2011 and has been a core feature of MediaWiki since version 1.17. ResourceLoader works by loading JS and CSS assets on demand, thus reducing loading and parsing time when features are unused, for example by older browsers. It also minifies the code, groups resources to save requests, and can embed images as data URIs*  \nThe *ResourceLoader* supports a quality	45	41	MWResourceLoaderTacticEEEN	DISABLED	\N	2	1703
1704	Having a single point of access to an intranet is a security tactic of	0	0	Firewall	DISABLED	\N	2	1704
1705	In the Fenix system a checksum is associated to a set of grades. This is an application of the tactic	0	0	VerifyMessageIntegrity	DISABLED	\N	2	1705
1706	In a system where there are sensitive data an appropriate tactic to be used is	0	0	SeparateEntities	DISABLED	\N	2	1706
1707	Consider a enterprise web system, which provides services both on the company's intranet and to the company's clients on the internet, that when under a denial of service attack decides to stop providing internet services.	0	0	Degradation	DISABLED	\N	2	1707
1708	In a system where the source of attacks can be internal, from authorized users, the appropriate tactics to be used are	0	0	InternalAttack	DISABLED	\N	2	1708
1709	In the description of the SocialCalc case study can be read:  \n>"Even with race conditions resolved, it is still suboptimal to accidentally overwrite the cell another user is currently editing. A simple improvement is for each client to broadcast its cursor position to other users, so everyone can see which cells are being worked on."  \n  \nFrom this fragment can be identified a scenario for	0	0	SocialCalcUsability	DISABLED	\N	2	1709
1810	In the generate-and-test process of architecture design, which techniques may be applied to test the hypothesis?	123	115	Architecture design generate and test	AVAILABLE	2019-10-23 15:45:52.72551	2	1810
1710	Consider the paragraph marked with the number 1 in the document that describes the use of caches in web services (see annex), where the concept of *distributed cache* is introduced. Which architectural style better represents the interaction pattern that exists among the various request nodes?	0	0	WebDistributedCacheINGLES	REMOVED	\N	2	1710
1711	Consider the Figure~1.8 in the document that describes the use of caches in web services (see annex). In that Figure, there is a rectangle with the name *Cache* within another rectangle with the name *Request Node*. Taking into account the description made in the text and the goal of that Figure, those rectangles correspond to which type of software elements?	0	0	WebCacheModuleINGLES	REMOVED	\N	2	1711
1712	In Mailman 3 messages are still being persistently stored using pickle because	44	18	GMMessagesPersistenceEN	DISABLED	\N	2	1712
1713	The Uses architectural style of the Module viewtype	169	129	UsesFor	AVAILABLE	2019-11-01 22:07:10.494889	2	1713
1714	In the description of the Thousand Parsec case study can be read:  \n>"Next, the player is prompted to configure options for the ruleset and server, with sane defaults pulled from the metadata. Finally, if any compatible AI clients are installed, the player is prompted to configure one or more of them to play against."  \n  \nThe tactic referred in the fragments is	0	0	ThounsandParsecSystemInitiative	DISABLED	\N	2	1714
1715	The requirements for complex systems are usually very numerous and conflicting among them, making it impossible to satisfy all the requirements in a given implementation of the system. Therefore, the recommended process for making the design of a software architecture involves the identification of the *architectural drivers* that will shape the design of architecture. These *architectural drivers* should be chosen so that they are	0	0	ArchitecturalDrivers	REMOVED	\N	2	1715
1716	In the description of the SocialCalc case study can be read:  \n>"If users A and B simultaneously perform an operation affecting the same cells, then receive and execute commands broadcast from the other user, they will end up in different states."  \n  \nFrom this fragment can be identified a scenario for	0	0	SocialCalcAvailability	DISABLED	\N	2	1716
1717	In the description of the Thousand Parsec case study can be read:  \n>"Turns also have a time limit imposed by the server, so that slow or unresponsive players cannot hold up a game."  \n  \nFrom this fragment can be identified a scenario for	0	0	ThounsandParsecAvailability	DISABLED	\N	2	1717
1718	In the description of the Thousand Parsec case study can be read:  \n>"Besides often running far longer than the circadian rhythms of the players' species, during this extended period the server process might be prematurely terminated for any number of reasons. To allow players to pick up a game where they left off, Thousand Parsec servers provide persistence by storing the entire state of the universe (or even multiple universes) in a database."  \n  \nThe tactic referred in the fragments is	0	0	ThounsandParsecRollback	DISABLED	\N	2	1718
1760	According to Section 8.2.3, the NameNode component issues commands to the DataNodes so that they execute some operations on their blocks, whereas DataNodes have to send reports regularly to the NameNode. The architecture that best describes how these two types of components interact in the HDFS system is	60	26	HadoopInteraccaoNameNodeDataNodesINGLES	REMOVED	\N	2	1760
1719	In the description of the GitHub case study can be read:  \n>"Of course, allowing arbitrary execution of commands is unsafe, so SSH includes the ability to restrict what commands can be executed. In a very simple case, you can restrict execution to git-shell which is included with Git. All this script does is check the command that you're trying to execute and ensure that it's one of git upload-pack, git receive-pack, or git upload-archive."  \n  \nThe tactic addressed in this fragments is:	0	0	GitHubSecurity	DISABLED	\N	2	1719
1765	In the description of the Chrome case study you can read:  \n*Typing in the Omnibox (URL) bar triggers high-likelihood suggestions, which may similarly kick off a DNS lookup, TCP pre-connect, and even prerender the page in a hidden tab.*  \nThis description refers to the qualities of	146	125	ChromeTwo	AVAILABLE	2019-10-23 23:09:52.689666	2	1765
1796	The Hadoop system support of different block placement policies:	72	38	HadoopPoliticaLocalizacaoReplicasINGLES	AVAILABLE	2019-10-23 13:59:29.145958	2	1796
1720	In the description of the Git case study can be read how it efficiently compares content:  \n>"When a content (i.e., file or directory) node in the graph has the same reference identity (the SHA in Git) as that in a different commit, the two nodes are guaranteed to contain the same content, allowing Git to short-circuit content diffing efficiently."  \n  \nThe performance tactic addressed in this fragments is:	0	0	GitIncreaseResourceEfficiency	DISABLED	\N	2	1720
1722	Consider a view of the module viewtype where there is a uses loop, a cycle of uses dependences between several modules. It may be possible to break the dependence cycle by	138	104	UsesCycles	AVAILABLE	2019-11-01 22:07:10.494889	2	1722
1723	The MediaWiki system tries to enforce a reliability criteria that all the changes done by a writer are consistently visualized in her subsequent reads. This criteria is implemented	45	11	MWReliabilityImplementationEN	DISABLED	\N	2	1723
1724	In the description of the GitHub case study can be read:  \n>"Once the Smoke proxy has determined the user's route, it establishes a transparent proxy to the proper file server. We have four pairs of file servers. Their names are fs1a, fs1b, ..., fs4a, fs4b. These are 8 core, 16GB RAM bare metal servers, each with six 300GB 15K RPM SAS drives arranged in RAID 10. At any given time one server in each pair is active and the other is waiting to take over should there be a fatal failure in the master. All repository data is constantly replicated from the master to the slave via DRBD."  \n  \nThe four pairs of file servers implement:	0	0	GitHubComputationRedundancy	DISABLED	\N	2	1724
1725	Which architectural style is adequate for planning incremental releases?	148	123	UsesOne	AVAILABLE	2019-11-06 18:03:55.177485	2	1725
1726	The architecturally significant qualities of the second Fénix architecture are:	44	9	FenixTwoEN	DISABLED	\N	2	1726
1727	The last paragraph of Section 8.2.2 describes the solution used by the NameNode to obtain a certain level of performance while writing to disk. Which architectural style is more adequate to represent the solution used?	60	22	HadoopNameNodeThreadsINGLES	REMOVED	2019-10-27 17:31:43.373956	2	1727
1728	The quality that is more relevant to views of the module viewtype is:	225	207	ModuleViewTypeOne	AVAILABLE	2019-11-01 22:07:10.494889	2	1728
1729	Consider the following application-specific types. Note that `Queue` components are within the `Carbon` components. In a view that contains components of these three types   \n![image][image]  \n	65	35	GraphiteDataPointSocket	AVAILABLE	\N	2	1729
1730	In the context of the FenixEdu case study the following scenario was identified.  \n>"The management intends that the system should be available to all users, even after offices close and classes finish because students may need courses material to study 24X7 and faculty and administrative staff may want to work from home."  \n  \nThis is a	40	22	BusinessScenarioTwo	AVAILABLE	\N	2	1730
1731	When the source of an attack is internal to an organization the tactics which are more efective are	44	22	SecurityInternalSourceEN	DISABLED	\N	2	1731
1806	Consider the architectural views of EtherCalc system. In the case study description can be read  \n>"The Socialtext platform has both behind-the-firewall and on-the-cloud deployment options, imposing unique constraints on EtherCalc's resource and performance requirements."  \n  \n	115	56	EtherCalcAllocation	DISABLED	\N	2	1806
1732	Consider the following fragment of GNU Mailman case study:  \n*Mailman 3 has adopted the Representational State Transfer (REST) model for external administrative control. REST is based on HTTP, and Mailman's default object representation is JSON. These protocols are ubiquitous and well-supported in a large variety of programming languages and environments, making it fairly easy to integrate Mailman with third party systems. REST was the perfect fit for Mailman 3, and now much of its functionality is exposed through a REST API.*  \nThis solution allowed:	44	4	GMRestModularityEN	DISABLED	\N	2	1732
1733	Which are the most significant qualities of the MediaWiki system?	44	18	MWQualitiesINGLES	DISABLED	\N	2	1733
1734	Consider the concepts of module interface and component port.	34	17	ComponentPorts	AVAILABLE	\N	2	1734
1735	Consider the following decomposition view of the Graphite system where module Store Graphs is responsible for managing the storage of datapoints and graphs and module Present Graphs for graphs generation and presentation. Buffering is a library used to temporarily store incoming data point.   \n![image][image]  \n	84	66	GraphiteDecompositionBuffering	AVAILABLE	\N	2	1735
1736	When someone uses the Domain Model pattern to implement the domain logic layer of an enterprise application, it is common to use also the Service Layer pattern. The Service Layer pattern is used in these cases	60	26	ServiceLayerINGLES	REMOVED	\N	2	1736
1737	According to the attribute-driven design process, we should design the software architecture for a system based on a selected list of requirements, which are called the *architectural drivers*. These architectural drivers should be sorted according to their importance for the system's stakeholders because	60	38	UtilizacaoNotificaDoisINGLES	REMOVED	\N	2	1737
1738	Consider the Uses architectural style of the Module viewtype	223	166	Layered	AVAILABLE	2019-11-01 22:07:10.494889	2	1738
1739	Imagine that you intend to describe how a client reads a file from an HDFS system while supporting sporadic failures in the hardware of some DataNodes, but without affecting the availability of the system. To accomplish that, you want to use a component-and-connector view containing only two types of components: the HDFS Client, and the DataNode.	138	51	HadoopNameNodeComoConectorINGLES	AVAILABLE	\N	2	1739
1740	Consider the following fragment of the MediaWiki system description:  \n*To optimize the delivery of JavaScript and CSS assets, the ResourceLoader module was developed to optimize delivery of JS and CSS. Started in 2009, it was completed in 2011 and has been a core feature of MediaWiki since version 1.17. ResourceLoader works by loading JS and CSS assets on demand, thus reducing loading and parsing time when features are unused, for example by older browsers. It also minifies the code, groups resources to save requests, and can embed images as data URIs.*  \nThe *ResourceLoader* implements a tactic	44	40	MWResourceLoaderTacticINGLES	DISABLED	\N	2	1740
1741	Consider the following fragment of GNU Mailman  \n*In Mailman 2, the MailList object's state is stored in a file called config.pck, which is just the pickled representation of the MailList object's dictionary. Every Python object has an attribute dictionary called __dict__. So saving a mailing list object then is simply a matter of pickling its __dict__ to a file, and loading it just involves reading the pickle from the file and reconstituting its __dict__.*  \nAlthough simple, this solution resulted in several problems which had a negative impact on performance. This is due to:	44	8	GMPicklePerformanceEN	DISABLED	\N	2	1741
1797	Which of the following tactics is not related with the management of resources	104	74	PerformanceTacticsOne	AVAILABLE	2019-10-03 15:45:28.202956	2	1797
1742	Consider the following fragment of GNU Mailman case study:  \n*Once a message has made its way through the chains and rules and is accepted for posting, the message must be further processed before it can be delivered to the final recipients. For example, some headers may get added or deleted, and some messages may get some extra decorations that provide important disclaimers or information, such as how to leave the mailing list.*  \nThe Pipes-and-Filters architectural style is used in the handling of messages. In this context the data type which is sent among the filters is	44	23	GMPipesFiltersDataEN	DISABLED	\N	2	1742
1743	In the description of the GNU Mailman case study it is proposed a solution that, when there are several queue runners executing on the same queue, the delivery of messages is done according to arrival order (FIFO).  \n*There's another side effect of this algorithm that did cause problems during the early design of this system. Despite the unpredictability of email delivery in general, the best user experience is provided by processing the queue files in FIFO order, so that replies to a mailing list get sent out in roughly chronological order.*  \nThe proposed solution	44	13	GMReliabilityFIFOEN	DISABLED	\N	2	1743
1744	To reduce the backend load (writes) the Graphite system uses	94	53	GraphiteBackend	AVAILABLE	2019-10-23 14:18:48.617529	2	1744
1745	In the HDFS system, the main responsibility of the DataNode component is to store the data blocks corresponding to the client's files, and usually there are several instances of this component on each system. The architectural style that best describes the interaction pattern among the various instances of DataNode is	117	50	HadoopInteraccaoDataNodesINGLES	AVAILABLE	\N	2	1745
1746	The MediaWiki system tries to guarantee a reliability criteria where all information is available to be read by any reader in less than 30 seconds after being written. To achieve this criteria it is implemented a tactic of	45	23	MWReliabilityReadsTacticEN	DISABLED	\N	2	1746
1747	In the Continuous Integration case study can be read  \n>"The space of architectures for continuous integration systems seems to be dominated by two extremes: master/slave architectures, in which a central server directs and controls remote builds; and reporting architectures, in which a central server aggregates build reports contributed by clients. All of the continuous integration systems of which we are aware have chosen some combination of features from these two architectures."  \n  \nThe tactic that is referred in both architectures is	13	6	ContinuousIntegrationOne	DISABLED	\N	2	1747
1748	The Infinispan system can be used as a library, in which case it is embedded into a Java application, or as a server, in which case it is a remote data grid.	13	9	InfinispanOne	DISABLED	\N	2	1748
1749	Consider a Component-and-Connector architectural view of the MediaWiki system where all the clients are connected to a server through a request-reply connector. This connector implements the tactics	45	0	MWTacticsEN	REMOVED	\N	2	1749
1750	When designing the architecture for a system the architect realises that most of the modules have bidirectional uses relationships. This has impact on	93	46	UsesIncremental	AVAILABLE	\N	2	1750
1751	The Unit of Work pattern can be implemented in an application server, while it is still necessary to use transactions in the repository to access to the data. In this situation	44	6	UnitOfWorkEN	REMOVED	\N	2	1751
1752	A layer, in the layers architectural style, is a module:	204	127	ModulosCamadasINGLES	AVAILABLE	2019-11-01 22:07:10.494889	2	1752
1753	In the description of MediaWiki system we can read:  \n*The first revision of the blob is stored in full, and following revisions to the same page are stored as diffs relative to the previous revision; the blobs are then gzipped. Because the revisions are grouped per page, they tend to be similar, so the diffs are relatively small and gzip works well. The compression ratio achieved on Wikimedia sites nears 98%.*  \nThis description refers to a tactic of	45	35	MWVerBlobTacticEN	DISABLED	\N	2	1753
1754	In the Chrome system the following tactic is used the Render Processes to improve performance	111	88	ChromePerformance	AVAILABLE	2019-10-24 10:18:55.856785	2	1754
1755	Consider the following fragment of GNU Mailman case study:  \n*Mailman 3 has adopted the Representational State Transfer (REST) model for external administrative control. REST is based on HTTP, and Mailman's default object representation is JSON. These protocols are ubiquitous and well-supported in a large variety of programming languages and environments, making it fairly easy to integrate Mailman with third party systems. REST was the perfect fit for Mailman 3, and now much of its functionality is exposed through a REST API.*  \nThis solution allowed increased interoperability because	45	20	GMRestInteroperabilityEN	DISABLED	\N	2	1755
1756	Consider the following fragment of GNU Mailman case study:  \n*Once a message has made its way through the chains and rules and is accepted for posting, the message must be further processed before it can be delivered to the final recipients. For example, some headers may get added or deleted, and some messages may get some extra decorations that provide important disclaimers or information, such as how to leave the mailing list.*  \nThe architectural style that is more accurate to describe the flexible processing of messages is	45	41	GMPipesFiltersEN	DISABLED	\N	2	1756
1757	Consider the following fragment of GNU Mailman case study:  \n*Email messages can act as containers for other types of data, as defined in the various MIME standards. A container message part can encode an image, some audio, or just about any type of binary or text data, including other container parts.*  \nThe architectural style that is more accurate to describe this transcription is	45	22	GMDataModelEN	DISABLED	\N	2	1757
1758	Consider the following transcription of the GNU Mailman system:  \n*...Mailman supports running more than one runner process per queue directory...*  \nIt has the goal to support	45	32	GMPerformanceEN	DISABLED	\N	2	1758
1759	In the Infinispan case study can be read  \n>"Before putting data on the network, application objects need to be serialized into bytes so that they can be pushed across a network, into the grid, and then again between peers. The bytes then need to be de-serialized back into application objects, when read by the application. In most common configurations, about 20% of the time spent in processing a request is spent in serialization and de-serialization."  \n  \nThe above description can motivate a scenario for	14	13	InfinispanThree	DISABLED	\N	2	1759
1762	An architectural view of the Component-and-Connector viewtype that describes the interactions within the Renderer Process component of Chrome, uses the architectural style	113	87	CHRenderStyleEN	AVAILABLE	\N	2	1762
1763	Suppose that after designing a successful architecture for a particular client the software house management decides to create a cross-functional internal department to start providing products for this particular segment of the market.	92	56	ArchitecturalInfluenceCycle	AVAILABLE	2019-10-04 15:52:13.690027	2	1763
1768	In the Infinispan case study can be read  \n>"Infinispan uses its own serialization scheme, where full class definitions are not written to the stream. Instead, magic numbers are used for known types where each known type is represented by a single byte. This greatly improves not just serialization and de-serialization speed qualities, but also produces a much more compact byte stream for transmission across a network. An externalizer is registered for each known data type, registered against a magic number. This externalizer contains the logic to convert object to bytes and vice versa."  \n  \nThese properties of Infinispan can be represented by	30	5	InfinispanViewsTwo	DISABLED	\N	2	1768
1769	In wikipedia you can find the following definition:  \n>"The garbage collector, or just collector, attempts to reclaim garbage, or memory occupied by objects that are no longer in use by the program."  \n  \nThe garbage collector is a component that implements an availability tactic of	104	84	GarbageCollector	AVAILABLE	2019-10-23 15:16:54.384826	2	1769
1770	Which quality, or qualities, of the Graphite system are described by the sentence: *Graphite's Composer UI provides a point-and-click method to create a graph from which you can simply copy and paste the URL.*	95	82	GraphiteTwo	REMOVED	2019-10-22 20:22:40.957034	2	1770
1771	The architecturally significant requirements are important in the process of creating the software architecture for a system because they are	247	213	RequirementsTwo	AVAILABLE	2019-10-03 14:03:31.829701	2	1771
1772	In the Hadoop system the tactics used to reintroduce a DataNode after its failure are:	160	55	HadoopTacticasRecuperacaoFaltasDoisINGLES	AVAILABLE	2019-10-23 15:28:19.127857	2	1772
1773	An advantage of Amazon Silk when compared with Chrome is	149	107	CHAmazonSilkEN	AVAILABLE	\N	2	1773
1774	Designing the software architecture for a complex system	100	65	ArchitectureInfluenceCycleTwo	AVAILABLE	2019-10-03 15:38:37.402326	2	1774
1807	What is the architectural impact of an Architecturally Significant Requirement (ASR)?	126	100	Utility tree architectural impact	AVAILABLE	2019-10-24 10:18:55.856785	2	1807
1808	Consider the modifiability quality and the cost of change.	105	91	ModifiabilityThree	AVAILABLE	2019-10-23 15:55:56.472234	2	1808
1775	Consider the following excerpt about the Amazon system  \n>"Over time, this grew into hundreds of services and a number of application servers that aggregate the information from the services. The application that renders the Amazon.com Web pages is one such application server, but so are the applications that serve the Web-services interface, the customer service application, the seller interface, and the many third-party Web sites that run on our platform."  \n  \nThe architectural style that better represents these aspects of the Amazon architecture is	95	19	AmazonOne	AVAILABLE	\N	2	1775
1776	An advantage of Chrome when compared with Amazon Silk is	118	71	CHAmazonSilkTwoEN	AVAILABLE	\N	2	1776
1777	Chrome, as described in the case study, was designed to support the accomplish the following architectural qualities:	130	87	CHQualitiesEN	AVAILABLE	2019-10-23 15:35:36.879316	2	1777
1778	Which quality, or qualities, of the Graphite system are described by the sentence: *Graphite's Composer UI provides a point-and-click method to create a graph from which you can simply copy and paste the URL.*	225	198	GPComposerUIQuality	AVAILABLE	2019-10-23 22:54:53.389187	2	1778
1779	Ping-and-echo and Heartbeat are two availability tactics to detect faults.	151	96	AvailabilityPingEchoHeartbeat	AVAILABLE	2019-10-22 20:25:00.632343	2	1779
1780	In the description of Chrome case study we can read:  \n*Chrome maintains a single instance of the resource dispatcher, which is shared across all render processes, and runs within the browser kernel process.*  \nThe *Resource Dispatcher* contributes to the performance quality because it implements a tactic of	188	138	ChromeOne	AVAILABLE	2019-10-23 15:39:55.604858	2	1780
1781	In the Continous integration case study can be read about Jenkins  \n>"It takes advantage of the JUnit XML standard for unit test and code coverage reporting to integrate reports from a variety of test tools. Jenkins originated with Sun, but is very widely used and has a robust open-source community associated with it."  \n  \nThe quality of Jenkins that is emphasized in the above sentence is	30	23	ContinousIntegrationScenariosTacticsTwo	DISABLED	\N	2	1781
1782	In his article *Who Needs an Architect?* Martin Fowler refers to the following architecture definition  \n>"*the expert developers working on that project have a shared understanding of the system design*"  \n  \n	107	59	SharedUnderstanding	AVAILABLE	2019-10-03 16:23:04.967799	2	1782
1783	The *Composer UI* component of Graphite system, described as - *Graphite's Composer UI provides a point-and-click method to create a graph from which you can simply copy and paste the URL* - to be effective needs to show to the user the changes she performs in the graph such that she has immediate feedback about the result of the changes. To do so, the system needs to implement the tactics of	152	64	GraphiteTwo	AVAILABLE	2019-10-22 20:22:40.957034	2	1783
1784	Consider a situation where a product family is being developed, which means that different products can be built through different compositions of the product family modules, and that a request comes for the creation of a new product, which only requires a particular composition of some of the existing modules. When is it expected that this change can occur, considering that the executable image of the product only contains the required modules and that the modules in the product family are already compiled?	118	90	Modifiability mechanisms	AVAILABLE	2019-10-22 12:31:00.314429	2	1784
1785	Consider the two following views   \n![image][image]  \n	47	23	ComponentAndConnectorThree	AVAILABLE	\N	2	1785
1787	Consider the following representation of the CDash system   \n![image][image]  \n The architecture style between the Dashboard and the Clients is:	23	12	JenkinsThree	DISABLED	\N	2	1787
1788	Consider a situation where the domain logic of a business application should be kept persistently in a store. Which modifiability tactic should be applied to reduce the cost of changing the domain logic.	97	62	Modifiability tactics	AVAILABLE	2019-10-22 20:19:30.843479	2	1788
1789	In the OrderPad system they have decided to use a Row Data Gateway data access pattern because	115	79	OrderPad	REMOVED	\N	2	1789
1790	In some situations Chrome prerenders a page. To do it	124	66	CHPrerenderTacticsEN	AVAILABLE	2019-10-23 15:45:52.72551	2	1790
1791	According to the attribute-driven design process, we should design the software architecture for a system based on a selected list of requirements, which are called the *architecturally significant requirements*. These architecturally significant requirements should be sorted according to their importance for the system's stakeholders because	153	139	ArchitecturallySignificantRequirements	AVAILABLE	2019-10-26 15:21:39.482074	2	1791
1792	In world-wide systems like Facebook or Amazon,	108	60	WorldWideEN	AVAILABLE	\N	2	1792
1793	Very often, when a software architecture is being designed, conflicting requirements are identified, like between security and availability. The role of the software architect is to	114	61	Diplomat	AVAILABLE	2019-10-04 17:42:05.269264	2	1793
1799	In a system where the code implementing a particular functionality is scattered through several modules, which tactic can we apply to reduce the cost of changing this functionality?	111	84	Modifiability tactics	AVAILABLE	2019-10-22 20:19:30.843479	2	1799
1800	In the context of classifying the business goals during the process of designing a software architecture, what is the value of a business goal?	115	85	Business goals value	AVAILABLE	2019-10-22 20:19:40.524206	2	1800
1801	Which availability detection tactic is most adequate to deal with the dynamic scalability of a cluster of servers, like the crash and restart of servers?	93	79	Availability tactics	AVAILABLE	2019-10-24 10:46:41.370993	2	1801
1802	Checksum is a technic that it is often used in architectural design. It can be used as	115	43	Checksum	DISABLED	\N	2	1802
1803	An architect is decomposing a system into a set of responsibilities using a view of the Decomposition style. However, she had already to backtrack several times and try new decompositions because she always end up with some responsibility that cannot fit within a single module.	130	100	Aspects	AVAILABLE	2019-11-01 22:07:10.494889	2	1803
1804	A utility tree for the design of a system	113	100	Utility tree	AVAILABLE	2019-10-26 14:58:21.774261	2	1804
1811	Consider an architecturally significant requirement (ASR) that has a high impact on the architecture but a low business value	73	56	HighBusinessValue	AVAILABLE	2019-10-23 14:18:48.617529	2	1811
1812	Considering the availability architectural quality and the tactics of active redundancy, passive redundancy, and spare	109	85	RestartInRedundancy	AVAILABLE	2019-10-23 16:25:40.550892	2	1812
1813	In the description of Hadoop we can red.  \n>"The CheckpointNode periodically combines the existing checkpoint and journal to create a new checkpoint and an empty journal. The CheckpointNode usually runs on a different host from the NameNode since it has the same memory requirements as the NameNode."  \n  \n	115	59	HadoopCreateFile	AVAILABLE	2019-10-23 19:20:54.778075	2	1813
1814	The scalability quality is achieved in the Hadoop system only because	145	68	HadoopEscalabilidadePossivelINGLES	AVAILABLE	2019-10-23 15:16:54.384826	2	1814
1815	Considering the availability architectural quality, the tactic of retry	95	86	OmissionRetry	AVAILABLE	2019-10-23 15:45:52.72551	2	1815
1816	The stimulus of an availability scenario is	149	128	AvailabilityOne	AVAILABLE	2019-10-23 22:44:10.444379	2	1816
1817	In EtherCalc initial prototype clients send their local commands and snapshots to the server, which result on redundant information on the server about the state of the spreadsheet. This redundancy is an application of	115	41	EtherCalcRedundancy	DISABLED	\N	2	1817
1818	In EtherCalc initial prototype clients send their local commands, cursor movements and snapshots to the server.	115	76	EtherCalcSnapshotPerformance	DISABLED	\N	2	1818
1819	In the EtherCalc case description can be read  \n>"The in-browser SocialCalc engine is written in JavaScript. We considered translating that logic into Perl, but that would have carried the steep cost of maintaining two code bases."  \n  \nThe excerpt is referring to a quality of	115	54	EtherCalcModifiabilityTestability	DISABLED	\N	2	1819
1820	In the Continuous Integration case study can be read  \n>"It takes advantage of the JUnit XML standard for unit test and code coverage reporting to integrate reports from a variety of test tools."  \n  \nThe referred quality is	14	10	ContinuousIntegrationThree	DISABLED	\N	2	1820
1821	In the description of GitHub case study can be read  \n>"For requests to the main website, the load balancer ships your request off to one of the four frontend machines. Each of these is an 8 core, 16GB RAM bare metal server. Their names are fe1, ..., fe4. Nginx accepts the connection and sends it to a Unix domain socket upon which sixteen Unicorn worker processes are selecting. One of these workers grabs the request and runs the Rails code necessary to fulfill it."  \n  \nTo represent the above description it is necessary to use	62	36	GitHubViews	DISABLED	\N	2	1821
1822	Consider the following description of the GNU Mailman system:  \n*VERP stands for Variable Envelope Return Path, and it is a well-known technique that mailing lists use to unambiguously determine bouncing recipient addresses. When an address on a mailing list is no longer active, the recipient's mail server will send a notification back to the sender. In the case of a mailing list, you want this bounce to go back to the mailing list, not to the original author of the message; the author can't do anything about the bounce, and worse, sending the bounce back to the author can leak information about who is subscribed to the mailing list. When the mailing list gets the bounce, however, it can do something useful, such as disable the bouncing address or remove it from the list's membership.*  \nThis transcription describes the quality(ies) of	45	29	GMReliabilityBounceEN	DISABLED	\N	2	1822
1823	Consider a web application that was implemented using three layers: presentation, domain logic, and data access. How are these layers mapped into the components if it is a rich interface application.	122	41	WebAppsOne	AVAILABLE	\N	2	1823
1894	An architectural tactic for a system describes	64	19	ArchitecturalTactics	AVAILABLE	\N	2	1894
1824	Consider the following description of the GNU Mailman system:  \n*There is a core Mailman class called Switchboard which provides an interface for enqueuing (i.e., writing) and dequeuing (i.e., reading) the message object tree and metadata dictionary to files in a specific queue directory. Every queue directory has at least one switchboard instance, and every queue runner instance has exactly one switchboard.*  \nThis transcription contains relevant information for viewtypes of	45	24	GMSwitchboardEN	DISABLED	\N	2	1824
1825	The architecturally significant requirements of the third architecture of Fénix are	45	14	FenixThreeEN	DISABLED	\N	2	1825
1826	In the uses architectural style a call does not necessarily correspond to a uses relationship because:	182	115	UtilizacaoNotificaINGLES	AVAILABLE	2019-11-01 22:07:10.494889	2	1826
1827	Compared to the Transaction Script pattern, the Domain Logic pattern has a higher initial cost of adoption. That is, it is harder to start with the Domain Logic pattern than with the Transaction Script pattern. The reason for this is that the Domain Logic pattern	62	33	TransactionScript	REMOVED	\N	2	1827
1828	Which of the following tactics is not related with the control of resource demand	99	67	PerformanceTacticsTwo	AVAILABLE	2019-10-03 13:23:59.360009	2	1828
1829	Consider the architectural views for the ThousandParsec system. In the case description can be read:  \n>"The Requirements function verifies that each component added to the design conforms to the rules of other previously added components."  \n  \nThe following diagram depicts a fragment of a proposal for the decomposition view of the system.   \n![image][image]  \n	115	53	ThounsandParsecView	DISABLED	\N	2	1829
1830	Consider the following decomposition of a domain model into 3 aggregates.  \n![image][image]  \n	57	29	AggregatesOne	REMOVED	\N	2	1830
1831	The Uses architectural style	170	107	ApplyUsesEN	AVAILABLE	2019-11-01 22:07:10.494889	2	1831
1836	In the description of the Thousand Parsec case study can be read:  \n>"A ruleset designer thus has the ability to create new object types or store additional information in the existing object types as required by the ruleset, allowing for virtually unlimited extensibility in terms of the available physical objects in the game."  \n  \nThis excerpt can be represented as a modifiability scenario where	115	70	ThousandParsecTactics	DISABLED	\N	2	1836
1837	In the description of the Git case study can be read:  \n>"Git tackles the storage space problem by packing objects in a compressed format, using an index file which points to offsets to locate specific objects in the corresponding packed file."  \n  \nThe tactic addressed in this fragments is:	115	50	GitTactics	DISABLED	\N	2	1837
1839	Which reintroduction availability tactic allows the system to recover from faults while minimizing the level of service affected?	115	81	Availability tactics	AVAILABLE	2019-10-24 10:34:42.609103	2	1839
1840	Which of the following recover from fault tactics is more suitable to deal with a server crash, without requiring too much resources and providing a short time to repair, in the order of seconds?	93	55	Availability tactics	AVAILABLE	2019-10-24 22:16:07.711358	2	1840
1841	Consider the following scenario  \n>"Our vehicle information system send our current location to the traffic monitoring system. The traffic monitoring system combines our location with other information, overlays this information on a Google Map, and broadcasts it. Our location information is correctly included with a probability of 99.99%."  \n  \n	149	126	Scenario	AVAILABLE	\N	2	1841
1842	Consider that when designing the architecture of a web application, the architect intends to guarantee of the confidentiality of persistent data in face of an attack from a system administrator.	62	34	SecurityDatabase	DISABLED	\N	2	1842
1843	In the description of the ThousandParsec case study can be read:  \n>"The Thousand Parsec Component Language (TPCL) exists to allow clients to create designs locally without server interaction - allowing for instant feedback about the properties, makeup, and validity of the designs."  \n  \nFrom this sentence can be written	62	30	ThousandParsecScenario	DISABLED	\N	2	1843
1844	In the description of GitHub case study can be read  \n>"Once the Smoke proxy has determined the user's route, it establishes a transparent proxy to the proper file server. We have four pairs of fileservers. Their names are fs1a, fs1b, ..., fs4a, fs4b. These are 8 core, 16GB RAM bare metal servers, each with six 300GB 15K RPM SAS drives arranged in RAID 10. At any given time one server in each pair is active and the other is waiting to take over should there be a fatal failure in the master. All repository data is constantly replicated from the master to the slave via DRBD."  \n  \nIn this description we can find the application of tactics like	62	28	GitTactic	DISABLED	\N	2	1844
1845	Marquardt characterizes performitis as:  \n>"Each part of the system is directly influenced by local performance tuning measures. There is no global performance strategy, or it ignores other qualities of the system such as testability and maintainability."  \n  \nThis means that	62	52	Performitis	DISABLED	\N	2	1845
1846	The software architecture of a system is usually represented through several views because we need to	96	76	ArchitecturalViews	AVAILABLE	\N	2	1846
1847	In the description of EtherCalc case study can be read  \n>"Because all jsdom code runs in a single thread, subsequent REST API calls are blocked until the previous command's rendering completes. Under high concurrency, this queue eventually triggered a latent bug that ultimately resulted in server lock-up."  \n  \nThe above sentence is related to a quality for	62	39	EtherCalcPerformance	DISABLED	\N	2	1847
1848	In the description of EtherCalc case study can be read  \n>"So, we removed jsdom from the RenderSheet function, re-implemented a minimal DOM in 20 lines of LiveScript for HTML export, then ran the profiler again. Much better! We have improved throughput by a factor of 4, HTML exporting is 20 times faster, and the lock-up problem is gone."  \n  \nThe above sentence describes a	62	44	EtherCalcTactic	DISABLED	\N	2	1848
1866	In HDFS, during normal operation DataNodes use the heartbeat tactic	198	128	HadoopHeartbeatINGLES	AVAILABLE	2019-10-23 22:44:10.444379	2	1866
1849	Consider the following figure that presents a Image Hosting System.   \n![image][image]  \n By adding another Image File Storage component, which contains a redundant copy of the data and provides read access to the clients, but without guaranteeing a ACID transactional behavior between reads and writes, it improves the quality(ies) of	62	46	ScalableArchitectureOne	AVAILABLE	\N	2	1849
1850	In the description of EtherCalc case study can be read how the architect tried to increase the performance in a multi-core context  \n>"Is there a way to make use of all those spare CPUs in the multi-tenant server? For other Node.js services running on multi-core hosts, we utilized a pre-forking cluster server that creates a process for each CPU. However, while EtherCalc does support multi-server scaling with Redis, the interplay of Socket.io clustering with RedisStore in a single server would have massively complicated the logic, making debugging much more difficult."  \n  \nThis possible solution has impact on the	62	50	EtherCalcTestability	DISABLED	\N	2	1850
1851	Ruby on Rails is a popular full-stack framework for building web applications. One of the elements of this framework is the **model**, which is described in the Rails documentation in the following way:  \n>"A model represents the information (data) of the application and the rules to manipulate that data. In the case of Rails, models are primarily used for managing the rules of interaction with a corresponding database table. In most cases, one table in your database will correspond to one model in your application. The bulk of your application's business logic will be concentrated in the models."  \n  \nGiven this description, the Rails' model is best described as an instance of	62	18	ActiveRecordRuby	REMOVED	\N	2	1851
1852	Consider the following decomposition of a domain model into 3 aggregates. If, instead of this decomposition, `Customer` and `Order` were in the same aggregate  \n![image][image]  \n	20	11	AggregatesTwo	REMOVED	\N	2	1852
1853	In the description of EtherCalc case study can be read how the architect increased the performance in a multi-core context  \n>"Instead of pre-forking a fixed number of processes, we sought a way to create one background thread for each server-side spreadsheet, thereby distributing the work of command execution among all CPU cores."  \n  \nWhich is represented by the diagram  \n  \n![image][image]  \n The above diagram, describing a server spreadsheet, can be represented using	62	29	EtherCalcViews	DISABLED	\N	2	1853
1854	In the description of ThousandParsec case study can be read  \n>"The flagship server, tpserver-cpp, provides an abstract persistence interface and a modular plugin system to allow for various database back ends."  \n  \nThis above sentence can be diagrammatically represented using	62	31	ThousandParsecPersistence	DISABLED	\N	2	1854
1857	Consider the following description of the behavior of Twitter ingestion mechanisms  \n>"Write. when a tweet comes in there's an O(n) process to write to Redis clusters, where n is the number of people following you. Painful for Lady Gaga and Barack Obama where they are doing 10s of millions of inserts across the cluster. All the Redis clusters are backing disk, the Flock cluster stores the user timeline to disk, but usually timelines are found in RAM in the Redis cluster."  \n  \nThe view that represents this behavior should be of the	17	12	TwitterFour	DISABLED	\N	2	1857
1858	In the Infinispan case study can be read  \n>"Infinispan supports several pluggable cache stores-adapters that can be used to persist data to disk or any form of secondary storage. The current default implementation is a simplistic hash bucket and linked list implementation, where each hash bucket is represented by a file on the filesystem. While easy to use and configure, this isn't the best-performing implementation."  \n  \nThe main architectural quality addressed in the above excerpt is	30	10	InfinispanScenariosTacticsTwo	DISABLED	\N	2	1858
1859	When designing an architecture requirements can be split into functional, quality attributes, and constraints. Functional requirements have impact on:	51	29	FunctionalModule	AVAILABLE	\N	2	1859
1860	Consider the following fragment in the description of the nginx case study.  \n>"nginx's configuration system was inspired by Igor Sysoev's experiences with Apache. His main insight was that a scalable configuration system is essential for a web server. The main scaling problem was encountered when maintaining large complicated configurations with lots of virtual servers, directories, locations and datasets. In a relatively big web setup it can be a nightmare if not done properly both at the application level and by the system engineer himself."  \n  \n	18	4	NginxOne	DISABLED	\N	2	1860
1861	In the Infinispan case study can be read  \n>"This allows applications to theoretically address an unlimited amount of in-memory storage as nodes are added to the cluster, increasing overall capacity."  \n  \nThe quality that is referred is	21	18	InfinispanTwo	DISABLED	\N	2	1861
1862	In the description of the nginx case study we can read:  \n>"nginx is event-based, so it does not follow Apache's style of spawning new processes or threads for each web page request. The end result is that even as load increases, memory and CPU usage remain manageable. nginx can now deliver tens of thousands of concurrent connections on a server with typical hardware."  \n  \nThe tactic nginx follows to achieve tens of thousands of concurrent connections is	19	15	NginxTwo	DISABLED	\N	2	1862
1863	In the description of the nginx case study we can read:  \n>"nginx's modular architecture generally allows developers to extend the set of web server features without modifying the nginx core. nginx modules come in slightly different incarnations, namely core modules, event modules, phase handlers, protocols, variable handlers, filters, upstreams and load balancers. At this time, nginx doesn't support dynamically loaded modules; i.e., modules are compiled along with the core at build stage."  \n  \nThe above sentence corresponds to	29	5	NginxThree	DISABLED	\N	2	1863
1864	In the Continuous Integration case study can be read  \n>"External resource coordination: Integration tests may depend on non-local resources such as a staging database or a remote web service. The CI system may therefore need to coordinate builds between multiple machines to organize access to these resources."  \n  \nThe referred tactic is	21	14	ContinuousIntegrationTwo	DISABLED	\N	2	1864
1865	In the description of Infinispan system can be read  \n>"When dealing with thread pools to process such asynchronous tasks, there is always a context switching overhead. That threads are not cheap resources is also noteworthy. Allocating appropriately sized and configured thread pools is important to any installation making use of any of the asynchronous features of Infinispan."  \n  \nThe architectural style that should be used to illustrate the sentence is	23	14	InfinispanThree	DISABLED	\N	2	1865
1867	Consider the following generalization view of the Catalog of DVD case study to fulfill a modifiability scenario   \n![image][image]  \n From this view the stakeholders can infer	106	64	DVDTwo	AVAILABLE	\N	2	1867
1869	Consider the following description of the behavior of Twitter ingestion mechanisms  \n>"Write. when a tweet comes in there's an O(n) process to write to Redis clusters, where n is the number of people following you. Painful for Lady Gaga and Barack Obama where they are doing 10s of millions of inserts across the cluster. All the Redis clusters are backing disk, the Flock cluster stores the user timeline to disk, but usually timelines are found in RAM in the Redis cluster."  \n  \n	45	24	TwitterOne	DISABLED	\N	2	1869
1870	Consider the following description of the behavior of Twitter  \n>"Solution is a write based fanout approach. Do a lot of processing when tweets arrive to figure out where tweets should go. This makes read time access fast and easy. Don't do any computation on reads. With all the work being performed on the write path ingest rates are slower than the read path, on the order of 4000 QPS."  \n  \nTo describe the performance quality of this behavior, and considering that the number of reads is much higher than the number of writes, we need to have a view that includes	45	4	TwitterThree	DISABLED	\N	2	1870
1871	The modifiability tactic Use an Intermediary between two modules	99	78	ModifiabilityOne	AVAILABLE	2019-10-23 16:11:51.598289	2	1871
1872	Considered the following two views of a system that receive a stream of character and produce the same stream where the characters are alternately uppercase and lowercase.   \n![image][image]  \n	58	24	ComponentAndConnectorViewtypeTwo	AVAILABLE	\N	2	1872
1873	Consider the following data model   \n![image][image]  \n	58	19	DomainDesignTwo	AVAILABLE	\N	2	1873
1874	Consider the following description of the *Infinispan* system:  \n>"Before putting data on the network, application objects need to be serialized into bytes so that they can be pushed across a network, into the grid, and then again between peers. The bytes then need to be de-serialized back into application objects, when read by the application. In most common configurations, about 20% of the time spent in processing a request is spent in serialization and de-serialization."  \n  \nThe above description can motivate a scenario for	17	14	InfinispanThree	DISABLED	\N	2	1874
1875	Consider the following requirement for availability of the Adventure Builder system  \n>"The Consumer Web site is available to the user 24x7. If an instance of OPC application fails, the fault is detected and the system administrator is notified in 30 seconds; the system continues taking order requests; another OPC instance is automatically created; and data remains in consistent state."  \n  \nIn order to support this quality it is necessary to	53	36	AdventureBuilderSix	AVAILABLE	\N	2	1875
1956	One of the qualities of Chrome is the execution of the JavaScript code inside a process, which allows the isolation against possible interferences between the execution of JavaScript programs that are loaded from different sites. The isolation level	140	76	CHSecurityLevelEN	AVAILABLE	2019-10-23 15:39:55.604858	2	1956
1876	According to the document that describes nginx:  \n>"nginx's modular architecture generally allows developers to extend the set of web server features without modifying the nginx core. nginx modules come in slightly different incarnations, namely core modules, event modules, phase handlers, protocols, variable handlers, filters, upstreams and load balancers. [...] Event modules provide a particular OS-dependent event notification mechanism like kqueue or epoll. Protocol modules allow nginx to communicate through HTTPS, TLS/SSL, SMTP, POP3 and IMAP."  \n  \nWhich architectural style is more adequate to represent the information presented above?	17	7	nginxModuleTypesINGLES	DISABLED	\N	2	1876
1877	The Service-Oriented Architecture style improves interoperability because	62	35	SOAInteroperability	AVAILABLE	\N	2	1877
1878	Consider the following view of the Pulse case study   \n![image][image]  \n This view applies the following architectural styles	27	14	PulseTwo	DISABLED	\N	2	1878
1879	Consider the architecture of the Morrison's OrderPad. The connector between the client component, executing in the Pad, and the server component, executing in the OrderPadDatabase	27	16	OrderPadTwo	DISABLED	\N	2	1879
1880	Consider a system that will require a significative configuration effort during deployment, because it provides several variations of the same functionalities and it is necessary to choose which functionalities better fit in each case. The most helpful architectural view for this situation is	164	68	InstallView	AVAILABLE	\N	2	1880
1881	Consider the following representation of a system following a microservices architecture,   \n![image][image]  \n	17	3	BoundedContextTwo	REMOVED	\N	2	1881
1882	Consider the module viewtype views of the DVDCatalog application. The architect knows about a new requirement  \n>"To support iPhone/iPad/Android version with sync, which allows offline use of the application in the mobile device and data synchronization to occur when a connection is available"  \n  \nThis requirement requires a change of	98	83	DVDCatalogMobile	AVAILABLE	\N	2	1882
1883	Several of the cases studied in this course have scalability requirements. That means that those systems should be designed in such a way that they	52	34	Scalability	AVAILABLE	\N	2	1883
1884	A general scenario for a quality attribute	16	9	GeneralScenario	DISABLED	\N	2	1884
1885	In the description of the Twitter system we can read:  \n>"Twitter is optimized to be highly available on the read path on the home timeline. Read path is in the 10s of milliseconds."  \n  \nThis is achieved because:	16	10	TwitterScaleOne	DISABLED	\N	2	1885
1886	In the description of the Twitter system we can read:  \n>"On the search timeline: Write. when a tweet comes in and hits the Ingester only one Early Bird machine is hit. Write time path is O(1). A single tweet is ingested in under 5 seconds between the queuing and processing to find the one Early Bird to write it to."  \n  \n	32	20	TwitterScaleTwo	DISABLED	\N	2	1886
1887	Consider the following decomposition of a domain model into 3 aggregates. If, instead of this decomposition, `Customer` and `Order` were in the same aggregate   \n![image][image]  \n	83	65	AggregateTwo	AVAILABLE	\N	2	1887
1888	Consider the following usability scenario of the Catalog of DVDs case study  \n>"The user intends to have up-to-date info about the movies and the system informs the user that the existing sources have new information about one of his DVDs, which helps to maintain an up-to-date catalog."  \n  \nThe tactic used to fulfill this scenario is	139	60	DVDOne	AVAILABLE	\N	2	1888
1889	Consider the following view of the Adventure Builder case study that applies the tiers architectural style   \n![image][image]  \n	105	48	AdventureBuilderOne	AVAILABLE	\N	2	1889
1890	A high-level component-and-connect view of Graphite system can be designed using only the architectural style(s)	149	109	GraphiteViewsTwo	AVAILABLE	\N	2	1890
1891	Consider the following view of the Pulse case study   \n![image][image]  \n This view provides a solution that uses the following tactic	68	40	PulseOne	DISABLED	\N	2	1891
1892	In the description of architecture of the OrderPad case study it can be read that the updates the user does on the OrderPad when it is offline are not lost. This availability quality is achieved through a	130	102	OrderPadOne	DISABLED	\N	2	1892
1895	Consider the module viewtype views of the DVDCatalog application. The architect knows about a new requirement  \n>"To allow the share of catalogs with family and friends, including some access control."  \n  \nThis requirement requires	101	67	DVDCatalogAspects	AVAILABLE	\N	2	1895
1896	The Java web servers, like Tomcat, use threads to process requests. For each request they create (or reuse) a thread to process it. To draw a architectural view that describes this behaviour we should use	117	90	CommunicationProcesses	AVAILABLE	\N	2	1896
1897	In the Architect Elevator article by Gregor Hohpe can be read:  \n>"Finding the appropriate context requires the architect to visit many floors of the organization."  \n  \nThis sentence reflects the fact that an architecture is	30	28	ElevatorCommon	DISABLED	\N	2	1897
1898	The main tactic associated with the layered architectural style is:	89	56	ModifiabilityTwo	AVAILABLE	\N	2	1898
1899	In the Hadoop system the fault recovery tactics are:	116	54	HadoopTacticasRecuperacaoFaltasINGLES	AVAILABLE	2019-10-23 15:28:19.127857	2	1899
1900	In the Architect Elevator article by Gregor Hohpe can be read:  \n>"Once a developer approached our architecture team with an application that had "significant scalability demands". A quick look at the architecture diagram revealed numerous components communicating via XML messages. When I pointed out that this may be the very reason for the performance concerns, I was quickly informed that this was an architecture decision and couldn't be changed. Assuming the architects are smart and well-intentioned, they may have thought about interoperability when they made this decision but may be unaware of the negative impact on run-time performance and development velocity."  \n  \nFrom this sentence we can conclude that	29	25	ElevatorInteroperability	DISABLED	\N	2	1900
1901	Consider the following figure that presents a Queue where client applications write their requests to be processed by a server (asynchronous) and compare with another architectural design (synchronous) where a thread is associated with each request.   \n![image][image]  \n	62	37	QueuesSyncAndAsync	AVAILABLE	\N	2	1901
1902	Consider the following decomposition view of the Catalog of DVD case study.   \n![image][image]  \n	93	78	DVDTopDecomposition	AVAILABLE	\N	2	1902
1957	In the description of the Chrome system can be read  \n>"The goal of the predictor is to evaluate the likelihood of its success, and then to trigger the activity if resources are available."  \n  \nThe above sentence refer to	104	88	ChromePredictor	AVAILABLE	2019-10-23 23:09:52.689666	2	1957
1903	In the Architect Elevator article by Gregor Hohpe can be read:  \n>"A lot of large companies have discovered the benefits of cloud computing but see it mainly as an infrastructure topic. I feel that's misguided: being able to get compute resources more quickly and cheaply is useful, but the real business benefit lies in a fully automated tool chain that minimizes the time in which a normal code change can go into production. Not quite coincidentally, this is my favorite definition of DevOps."  \n  \nIn the author's opinion	15	10	ElevatorDevops	DISABLED	\N	2	1903
1904	Consider the following view of the Adventure Builder system   \n![image][image]  \n In this view it is possible to reason that	56	32	AdventureBuilderComponentAndConnectorSecond	AVAILABLE	\N	2	1904
1905	Consider the architecture of the Morrison's OrderPad. The decision whether use a Native application or HTML5 for the implementation of the client in the Pad was taken because	27	20	OrderPadPortability	DISABLED	\N	2	1905
1906	In the context of the *Graphite* case study, consider the following view that represents the internal behavior of the *Carbon* component, where the components `r1,... , rn, w` are threads and `q1, ..., qn` are buffers. This view shows the Graphite's architecture support of   \n![image][image]  \n	59	41	GraphitePerformanceScenario	AVAILABLE	\N	2	1906
1907	Consider the architectural solutions for microservices architectures that use the event sourcing technique. This technique has the following advantage	102	64	MicroservicesOne	AVAILABLE	\N	2	1907
1908	In the web page of the NGINX HTTP server can be read  \n>"NGINX is a free, open-source, high-performance HTTP server and reverse proxy, as well as an IMAP/POP3 proxy server. (...) Unlike traditional servers, NGINX doesn't rely on threads to handle requests. Instead it uses a much more scalable event-driven (asynchronous) architecture. This architecture uses small, but more importantly, predictable amounts of memory under load."  \n  \nAccording to the above description the most adequate architectural style to represent the performance qualities of NGINX is	24	11	CommunicatingProcesses	DISABLED	\N	2	1908
1909	Consider the following decomposition views of the Catalog of DVD case study were the *Autocomplete* module is implemented in javascript and executes in a browser.   \n![image][image]  \n	100	71	DVDAutocomplete	AVAILABLE	\N	2	1909
1910	One of the advantages of having views of the module viewtype is that they allow to do a traceability analysis of requirements, how the functional requirements of the system are supported by module responsibilities. The modifiability tactic that is involved in this mapping is	57	28	ModuleViewtypeExamTwo	AVAILABLE	\N	2	1910
1911	A connector may be attached to components of different types because	59	42	ConnectorAttach	AVAILABLE	\N	2	1911
1912	In a layered architecture composed by four layers, where the topmost layer is the layer number 1 and the bottommost layer is the layer number 4, which of the layers is more modifiable?	207	139	LayeredAspectsDataModelOne	AVAILABLE	2019-11-06 18:03:55.177485	2	1912
1913	The Pipe-and-Filter style allows composition of filters	105	83	PipeFilterComposition	AVAILABLE	\N	2	1913
1914	Consider the module viewtype views of the DVDCatalog application. The architect knows about a new requirement  \n>"The application should support other kinds of catalogs (CDs, games, books, ...)."  \n  \nThis requirement requires a change of	115	89	DVDCatalogMeta	AVAILABLE	\N	2	1914
1915	In the description of the Gnutella system can be read:  \n>"The topology of the system changes at runtime as peer components connect and disconnect to the network."  \n  \n	168	124	ComponentAndConnectorStyleFour	AVAILABLE	\N	2	1915
1916	Consider the architectural solutions for microservices architectures that use the Command Query Responsibility Segregation (CQRS) technique in the context of Event Sourcing. This technique has the following disadvantage	62	36	MicroservicesThree	AVAILABLE	\N	2	1916
1917	How can be guaranteed that the update of an aggregate and the publishing of an event about the update is an atomic action	98	79	AggregateAndEventSourcing	AVAILABLE	\N	2	1917
1918	Command Query Responsibility Segregation (CQRS) technique uses the following architectural styles	132	96	EventSourcingOne	AVAILABLE	\N	2	1918
1919	Consider the module viewtype views of the DVDCatalog application. The architect knows about a new requirement  \n>"To allow the share of catalogs with family and friends, including some access control."  \n  \nThis requirement requires	112	59	DVDCatalogOne	AVAILABLE	\N	2	1919
1920	Web applications went through several evolutions over the last years. One of those evolutions was to make their user interfaces more sophisticated, by leveraging on new technologies available in the browsers, such as, for example, Javascript, to provide a more satisfying user experience. What were the most visible consequences of such an evolution on the typical software architecture of a web application?	54	36	WebApplicationsTwo	AVAILABLE	\N	2	1920
1921	In the description of the SocialCalc case study can be read:  \n>"Therefore, on browsers with support for CSS3, we use the box-shadow property to represent multiple peer cursors in the same cell."  \n  \nThis corresponds to the application of	57	38	SocialCalcOne	DISABLED	\N	2	1921
1922	Consider the architecture of the Morrison's OrderPad. In the description of the system can be read:  \n>"One of these was using a file-transfer to send data to the mainframe rather than MQ, which wouldn't perform well once many stores were active."  \n  \nThis approach means that	57	23	OrderPadOne	DISABLED	\N	2	1922
1923	Consider the kind of relations between components and modules.	64	42	ModuleViewTypeTwo	AVAILABLE	\N	2	1923
1924	Consider the architectural solutions for microservices architectures that use the Command Query Responsibility Segregation (CQRS) technique in the context of Event Sourcing. This technique has the following disadvantage	68	49	EventSourcingTwo	AVAILABLE	\N	2	1924
1925	Suppose that you are implementing a module in a system that has a two layered architecture. Knowing that your module belongs to the upper layer (assuming the usual notation for the layer style), this means that you	214	149	ModuleStyleTwo	AVAILABLE	2019-11-06 18:03:55.177485	2	1925
1926	Consider the concept of interface delegation	221	124	ComponentAndConnectorViewtypeTwo	AVAILABLE	\N	2	1926
1927	The repository architectural style provides performance because	89	52	ComponentAndConnectorStyleOne	AVAILABLE	\N	2	1927
1928	Consider the following architectural view of the Adventure Builder system, designed around the Order Processing Center  \n![image][image]  \nThe views **does not** allow the reason about the quality of	111	78	AdventureBuilderOne	AVAILABLE	\N	2	1928
1958	The main difference between the *Uses* relation of the Uses style and the *Allowed to Use* relation of the Layers style	97	78	UsaPodeUsarINGLES	AVAILABLE	2019-11-06 18:18:51.835992	2	1958
1929	Consider the architectural views for the SocialCalc system. In the case description can be read:  \n>"The save format is in standard MIME multipart/mixed format, consisting of four text/plain; charset=UTF-8 parts, each part containing  \n-delimited text with colon-delimited data fields. The parts are..."  \n  \nFrom the above excerpt can be inferred the need to have	20	10	SocialCalcTwo	DISABLED	\N	2	1929
1930	Frank Buschmann defines Featuritis as  \n>"Featuritis is the tendency to trade functional coverage for quality - the more functions the earlier they're delivered, the better."  \n  \nIn the OrderPad system the architect regretted not getting performance tests going earlier. The OrderPad system	135	86	OrderPadTwo	DISABLED	\N	2	1930
1931	A function call is not necessarily a uses relation of the Uses architectural style of the Module viewtype because	191	153	UsesCalls	AVAILABLE	2019-11-06 18:11:48.447966	2	1931
1961	During the different steps on how to create an architecture, the precise specification of architecture quality attributes is initially relevant to	102	86	DesigningArchitectureTwo	AVAILABLE	2019-10-23 15:55:56.472234	2	1961
1932	In the description of the SocialCalc case study can be read:  \n>"As the user scrolls the spreadsheet through our custom-drawn scroll bars, we dynamically update the innerHTML of the pre-drawn &lttd&gt elements. This means we don't need to create or destroy any &lttr&gt or &lttd&gt elements in many common cases, which greatly speeds up response time."  \n  \nThis corresponds to the application of	147	105	SocialCalcTacticsOne	DISABLED	\N	2	1932
1933	Consider an architecturally significant requirement (ASR) that has a low impact on the architecture but a high business value	95	71	LowArchitecturalImpact	AVAILABLE	\N	2	1933
1934	In the description of the SocialCalc case study can be read:  \n>"A simple improvement is for each client to broadcast its cursor position to other users, so everyone can see which cells are being worked on."  \n  \nThis sentence describes a tactic for usability which is	91	37	SocialCalcTacticsTwo	DISABLED	\N	2	1934
1935	Consider the Layered architectural style of the Module viewtype	203	161	Layered	AVAILABLE	2019-11-06 18:11:48.447966	2	1935
1936	In the HDFS system, in the stakeholders perspective, the use of low cost servers to build the clusters is:	111	68	HadoopStakeholdersEurosINGLES	AVAILABLE	2019-10-22 20:25:00.632343	2	1936
1937	Event Sourcing is a technique that use the following architectural style	88	71	EventSourcing	AVAILABLE	\N	2	1937
1938	Consider the following performance/scalability scenario for the Adventure Builder system  \n>"Up to 500 users click to see the catalog of adventure packages following a random distribution over 1 minute; the system is under normal operating conditions; the maximal latency to serve the first page of content is under 5 seconds; average latency for same is less than 2 seconds. If required, the system should easily support an increase in the number of simultaneous requests while maintaining the same latency per request."  \n  \nand the following architectural view  \n![image][image]  \n	108	58	AdventureBuilderTwo	AVAILABLE	\N	2	1938
1939	In Graphite system, in order to generate up-to-date graphs, the *WebApp* component interacts with the *Carbon* component. The interaction between these two components follows the architectural style	79	39	GraphiteClientServer	AVAILABLE	\N	2	1939
1940	Consider the following scenario:\n\n"A request arrives to add support for a new browser to a Web-based system, and the change must be made within two weeks."	238	185	Scenario	AVAILABLE	2019-10-03 16:22:30.877018	2	1940
1941	Using the Aspects architectural style promotes the modifiability of a system because	176	133	Aspects	AVAILABLE	2019-11-06 18:11:48.447966	2	1941
1942	In the HDFS system the fault recovery tactics are:	176	89	HadoopTacticasRecuperacaoFaltasINGLES	AVAILABLE	2019-10-24 10:37:58.630909	2	1942
1943	The Decomposition architectural style of the Module viewtype	157	142	Decomposition	AVAILABLE	2019-11-06 18:11:48.447966	2	1943
1944	Consider an availability scenario for the Graphite case study where the fault is that the metrics stored in the database do not have the most recent values, which result in outdated graphs. Which is the source of the fault of this availability scenario?	104	74	Graphite Usability	AVAILABLE	2019-10-23 16:21:01.115484	2	1944
1945	In Graphite system the *receiver* and the *writer threads* support asynchronous writing of metrics to optimize disk accesses. The interaction between these two components follow the architectural style	203	124	GraphiteCommunicationProcesses	AVAILABLE	\N	2	1945
1946	Twitter process millions of tweets per second. Considering that the processing of each tweet can be done independently of the other tweets, which performance tactic should be applied?	125	62	Performance tactics	AVAILABLE	2019-10-02 21:47:19.441525	2	1946
1947	In the description of the *How Netflix works* can be read:  \n>"The Netflix app or website determines what particular device you are using to watch, and fetches the exact file for that show meant to specially play on your particular device, with a particular video quality based on how fast your internet is at that moment."  \n  \nWhich corresponds to the application of the following tactic	65	54	NetflixTacticsOne	AVAILABLE	\N	2	1947
1948	The detail that can be used in a view of the Data Model viewtype can be conceptual, logical or physical.	200	149	DataModelOne	AVAILABLE	2019-11-06 18:18:51.835992	2	1948
1949	To which performance tactic can the use of queues be associated?	63	18	Queues	AVAILABLE	\N	2	1949
1950	In the HDFS system when the *CheckpointNode* and the *NameNode* are deployed in different nodes, the *CheckpointNode* provides:	123	75	HadoopCheckpoint	AVAILABLE	2019-10-26 21:45:20.69076	2	1950
1951	Consider the state resynchronization reintroduction availability tactic. With which recover from fault availability tactic the state resynchronization occurs faster?	105	69	Availability tactics	AVAILABLE	2019-10-26 15:54:09.26158	2	1951
1952	In the Graphite case study, which architectural solutions can be implemented to guarantee that the ComposerUI has good performance, e.g. <1 second, when the end user experiments with different combinations of functions to generate a graph.	109	78	Graphite ComposerUI performance	AVAILABLE	2019-10-23 16:21:01.115484	2	1952
1953	In the Graphite system the component *carbon* provides to *webapp* components an access interface to the *buffers* in order to improve the quality of	105	58	GraphiteScenarioTacticsOne	REMOVED	2019-10-22 20:19:30.843479	2	1953
1954	Consider the following scenario  \n>"If one of the application servers fails to respond when the system is in its normal operation state, the load balancer should redirect requests to another application server."  \n  \n	115	49	AvailabilityScenarioOne	AVAILABLE	2019-10-23 13:59:29.145958	2	1954
1955	Suppose that in the process of designing a system's software architecture you come to the conclusion that there are uses relations in both directions in almost all of the system's modules. This means that	132	101	UsesGeneralizationTwo	AVAILABLE	2019-11-06 18:18:51.835992	2	1955
1959	In the Hadoop system, during normal operation, *NameNode* could use a ping tactic to know whether *DataNodes* are available	62	49	HadoopPingINGLES	AVAILABLE	2019-10-23 13:59:29.145958	2	1959
1960	A criteria for the the application of the Decomposition architectural style of the Module viewtype is Build-vs-Buy decisions. The application of the criteria	208	151	DecompositionBuilvsBuy	AVAILABLE	2019-11-01 22:07:10.494889	2	1960
1962	In the Graphite system description can be read:  \n>"Making multiple Graphite servers appear to be a single system from a user perspective isn't terribly difficult, at least for a naive implementation."  \n  \n	114	57	GraphiteModifiability	AVAILABLE	2019-10-24 10:18:55.856785	2	1962
1963	Consider the following Graphite scenario:\n\n"The Client applications request to Carbon the write of 600.000 datapoints per minute and all writes are successfully written to the File system in less than one minute."\n\nWhich performance tactic *cannot* be used to fulfil this scenario?	128	87	Graphite 600.000 writes	AVAILABLE	2019-10-23 16:21:01.115484	2	1963
1964	The security tactics used in The Hadoop system deployed at Yahoo! are:	135	54	HadoopTacticasSegurancaINGLES	AVAILABLE	2019-10-23 15:28:19.127857	2	1964
1965	In defensive programming the programmer checks that the conditions under which modules are invoked comply with their specification, and if they don't an exception is raised to avoid failure propagation. When defensive programming is followed, in the context of availability quality, we are using a tactic of	105	41	AvailabilityDefensiveEN	AVAILABLE	2019-10-23 16:11:51.598289	2	1965
1966	Consider the following fragment in the description of the Graphite system:  \n>"The Graphite webapp allows users to request custom graphs with a simple URL-based API. Graphing parameters are specified in the query-string of an HTTP GET request, and a PNG image is returned in response."  \n  \n	78	50	GraphiteOne	AVAILABLE	2019-10-23 16:11:51.598289	2	1966
1967	In the description of the Chrome we can read\n\n> What about network performance? First off, Chrome uses the same network stack on Android and iOS as it does on all other versions. This enables all of the same network optimizations across all platforms, which gives Chrome a significant performance advantage. However, what is different, and is often adjusted based on the capabilities of the device and the network in use, are variables such as priority of speculative optimization techniques, socket timeouts and management logic, cache sizes, and more.\n\nWhich modifiability tactic is being used?	121	100	Chrome	AVAILABLE	2019-10-24 12:41:56.969753	2	1967
1968	According to the definition of the Layered architectural style, each layer represents a grouping of modules that offers a cohesive set of services.	214	187	LayeredVirtualMachine	AVAILABLE	2019-11-01 22:07:10.494889	2	1968
1969	Considering the availability architectural quality and the tactics of ping/echo and heartbeat	139	103	AvailabilityOne	AVAILABLE	2019-10-23 22:47:54.029158	2	1969
1970	Consider that a software development team uses an agile methodology such as XP (Extreme Programming), where no documentation is produced. Then, the systems developed by that team	184	158	SoftwareArchitectureOne	AVAILABLE	2019-10-04 18:29:20.573576	2	1970
1971	Consider the following figure depicting two different architectures for web applications   \n![image][image]  \n	102	83	MicroservicesArchitectureTwo	AVAILABLE	2019-10-04 18:26:22.638079	2	1971
1972	The *Checkpoint/rollback* tactic is a tactic for	24	6	TacticaCheckpointRollbackINGLES	AVAILABLE	2019-10-23 19:19:54.166728	2	1972
1973	Consider the following figure that presents the Hadoop cluster topology.   \n![image][image]  \n	79	48	HadoopCluster	AVAILABLE	2019-10-23 19:20:54.778075	2	1973
1974	A response measure of a modifiability scenario is	131	76	ModifiabilityResponseMeasure	AVAILABLE	2019-10-23 22:54:53.389187	2	1974
1975	In a modifiability scenario the environment can be characterized as design time, compile time, build time, initiation time, and runtime.	149	86	ModifiabilityOne	AVAILABLE	2019-10-23 22:47:54.029158	2	1975
1976	Consider the following fragment in the description of the Graphite system:  \n>"Imagine that you have 60,000 metrics that you send to your Graphite server, and each of these metrics has one data point per minute. Remember that each metric has its own whisper file on the filesystem. This means carbon must do one write operation to 60,000 different files each minute. As long as carbon can write to one file each millisecond, it should be able to keep up. This isn't too far fetched, but let's say you have 600,000 metrics updating each minute, or your metrics are updating every second, or perhaps you simply cannot afford fast enough storage. Whatever the case, assume the rate of incoming data points exceeds the rate of write operations that your storage can keep up with. How should this situation be handled?"  \n  \n	111	61	GraphiteThree	AVAILABLE	2019-10-23 15:16:54.384826	2	1976
1977	In Facebook it is not possible to have the information about more that one bilion users in a single disk. Therefore, a sharding technique is applied, where the persistent information is split between several database servers, and applications are routed to the right servers for queries and updates. To describe this architecture	125	116	DataModelFacebook	AVAILABLE	2019-11-06 19:25:14.813915	2	1977
1978	Consider the following excerpt from the Chrome description\n\n> Typing in the Omnibox (URL) bar triggers high-likelihood suggestions, which may similarly kick off a DNS lookup, TCP pre-connect, and even prerender the page in a hidden tab.\n\nWhich quality(ies) is(are) being addressed?	70	53	Chrome qualities	AVAILABLE	\N	2	1978
1979	The availability quality can be supported by a voting tactic in order to identify faults of	190	140	AvailabilityVotingSecond	AVAILABLE	2019-10-23 22:47:54.029158	2	1979
1980	In Chrome prefetching is used as a technique to improve the latency. This technique belongs to which group of performance tactics?	89	70	Chrome prefetching	AVAILABLE	2019-10-22 20:22:40.957034	2	1980
1981	Consider the following availability scenario  \n>"If one of the application servers fails to respond to a request when the system is in its normal operation state, the system should notify the operator and continue to operate normally."  \n  \n	75	49	AvailabilityScenarioTwo	AVAILABLE	2019-10-23 19:20:54.778075	2	1981
1982	Suppose that in the development of an enterprise application (which needs to access a database) it was decided to use the FenixFramework library to simplify the development of the data access code. Which architectural style is the most adequate to represent this decision?	71	35	ModuleStylesOne	AVAILABLE	2019-11-06 18:03:55.177485	2	1982
1983	One of the advantages of having views of the module viewtype is that they allow to do an impact analysis to predict the effect of modifying the system. The architectural style of the module viewtype which provides richer information for this impact analysis is	181	139	ModuleViewtypeExamOne	AVAILABLE	2019-11-06 19:47:12.894684	2	1983
1984	What is the context of the Architecture Influence Cycle associate with the budget available for the development of a system?	125	71	Budget	AVAILABLE	2019-10-04 19:00:25.602126	2	1984
1985	Consider the following description of *Memcached*, which is adapted from its Wiki:  \n>"Memcached is an in-memory key-value store for small chunks of arbitrary data from results of database calls, API calls, or page rendering. It is made up of:  \n-  Client software, which is given a list of available memcached servers.  \n-  A client-based hashing algorithm, which chooses a server based on the "key" input.  \n-  Server software, which stores your values with their keys into an internal hash table.  \n-  Server algorithms, which determine when to throw out old data (if out of memory), or reuse memory.  \n"  \n  \nSuppose that you want to present an architectural view for *Memcached* that represents the above information. Which view is more adequate?	267	181	ModuleStyleOne	AVAILABLE	2019-11-06 19:25:14.813915	2	1985
1986	Consider that a chess game should provide an automatic and intelligent chess player, and that to implement that player we will use some of the many chess engines already available in the market. Moreover, the system should allow the user to choose which engine to use for each new game. Given these requirements, which of the architectural styles from the module viewtype are best suited to satisfy them?	277	166	DecompositionGeneralization	AVAILABLE	2019-11-01 22:07:10.494889	2	1986
1987	In the Observer design pattern, where the model invokes a notification method on all its observers whenever it is changed, can be said, in what concerns the Uses relation of the Uses architectural style, that	155	97	ObserverUsesEN	AVAILABLE	2019-11-01 22:07:10.494889	2	1987
1988	Consider the following fragment in the description of the Graphite system:  \n>"To avoid this kind of catastrophe, I added several features to carbon including configurable limits on how many data points can be queued and rate-limits on how quickly various whisper operations can be performed. These features can protect carbon from spiraling out of control and instead impose less harsh effects like dropping some data points or refusing to accept more data points. However, proper values for those settings are system-specific and require a fair amount of testing to tune. They are useful but they do not fundamentally solve the problem. For that, we'll need more hardware."  \n  \nThe performance tactics referred in the above description are:	87	38	GraphiteTwo	AVAILABLE	2019-10-23 16:11:51.598289	2	1988
\.


--
-- Data for Name: quiz_answers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.quiz_answers (id, answer_date, completed, quiz_id, user_id, used_in_statistics) FROM stdin;
7811	2019-10-18 21:17:28.460416	t	5373	678	f
7812	2019-10-18 21:04:51.381927	t	5372	678	f
7813	2019-10-17 22:41:47.899928	t	5369	678	f
7814	2019-10-17 22:29:15.779505	t	5371	678	f
7815	2019-10-02 23:19:41.872808	t	5365	678	f
7816	2019-10-02 22:55:15.977552	t	5360	678	f
7817	2019-10-02 23:11:25.189944	t	5363	678	f
7818	2019-09-30 18:54:10.67497	t	5359	678	f
7819	2019-09-25 07:48:11.581258	t	5366	678	f
7820	2019-09-25 07:04:51.753573	t	5364	678	f
7821	2019-09-25 06:54:54.613739	t	5362	678	f
7822	2019-09-25 05:36:05.548765	t	5361	678	f
7823	2019-12-01 23:38:12.926257	t	5376	678	f
7824	2019-12-01 23:27:30.06322	t	5375	678	f
7825	2019-11-13 16:39:09.500677	t	5374	678	f
7826	2019-10-27 11:14:57.419857	t	5370	678	f
7827	2019-10-27 11:27:10.989704	t	5367	678	f
7828	2019-10-27 09:19:29.378496	t	5368	678	f
7829	2019-10-26 13:21:05.024354	t	5369	679	f
7830	2019-10-26 10:06:00.415321	t	5371	679	f
7831	2019-10-02 13:48:45.854809	t	5365	679	f
7832	2019-10-02 13:29:26.459325	t	5363	679	f
7833	2019-10-02 13:00:41.341029	t	5360	679	f
7834	2019-09-27 17:23:26.507449	t	5359	679	f
7835	2019-09-27 16:57:30.108353	t	5366	679	f
7836	2019-09-27 16:37:58.698219	t	5364	679	f
7837	2019-09-22 10:02:08.556965	t	5362	679	f
7838	2019-09-22 09:41:42.498198	t	5361	679	f
7839	2019-11-30 16:46:08.698077	t	5376	679	f
7840	2019-11-30 16:44:10.349542	t	5375	679	f
7841	2019-11-16 18:08:15.978906	t	5374	679	f
7842	2019-10-27 15:57:32.832518	t	5367	679	f
7843	2019-10-27 17:49:40.764792	t	5368	679	f
7844	2019-10-27 15:48:21.823535	t	5372	679	f
7845	2019-10-27 15:13:23.241251	t	5370	679	f
7846	2019-10-27 12:13:00.888724	t	5373	679	f
7847	2019-10-24 13:54:45.234528	t	5373	680	f
7848	2019-10-22 20:15:48.760588	t	5368	680	f
7849	2019-10-22 19:17:35.040119	t	5367	680	f
7850	2019-10-22 19:13:27.706037	t	5370	680	f
7851	2019-10-15 19:23:52.436398	t	5372	680	f
7852	2019-10-10 15:58:57.545178	t	5369	680	f
7853	2019-10-09 10:22:36.557405	t	5371	680	f
7854	2019-10-05 20:10:53.699734	t	5365	680	f
7855	2019-10-01 20:20:17.056295	t	5363	680	f
7856	2019-10-01 20:17:44.094184	t	5360	680	f
7857	2019-10-01 20:15:22.423563	t	5359	680	f
7858	2019-10-01 20:10:20.090575	t	5366	680	f
7859	2019-09-30 11:43:20.436163	t	5364	680	f
7860	2019-09-22 21:42:12.600667	t	5362	680	f
7861	2019-09-22 21:38:13.260036	t	5361	680	f
7862	2019-11-26 21:24:53.28576	t	5376	680	f
7863	2019-11-26 21:21:13.168057	t	5375	680	f
7864	2019-11-01 01:30:52.13688	t	5374	680	f
7865	2019-10-19 10:57:20.426932	t	5372	681	f
7866	2019-10-19 10:48:26.451502	t	5369	681	f
7867	2019-10-19 10:44:55.827036	t	5371	681	f
7868	2019-10-06 19:55:44.179637	t	5365	681	f
7869	2019-10-06 19:52:14.455531	t	5366	681	f
7870	2019-10-06 19:49:07.430795	t	5360	681	f
7871	2019-10-06 19:44:18.005837	t	5362	681	f
7872	2019-10-06 19:40:35.615742	t	5359	681	f
7873	2019-10-06 19:37:43.911505	t	5363	681	f
7874	2019-10-06 17:47:22.139226	t	5364	681	f
7875	2019-10-06 17:43:35.518754	t	5361	681	f
7876	2019-12-02 17:33:49.281042	t	5376	681	f
7877	2019-12-02 17:29:19.307937	t	5375	681	f
7878	2019-11-18 00:59:40.422453	t	5374	681	f
7879	2019-10-28 16:45:37.472125	t	5368	681	f
7880	2019-10-28 16:26:30.692602	t	5367	681	f
7881	2019-10-28 16:18:38.90526	t	5373	681	f
7882	2019-10-28 16:14:21.84105	t	5370	681	f
7883	2019-10-26 11:26:35.887728	t	5369	682	f
7884	2019-10-26 11:22:21.690244	t	5371	682	f
7885	2019-10-04 13:56:45.481969	t	5365	682	f
7886	2019-10-04 13:53:31.264972	t	5363	682	f
7887	2019-10-04 13:49:51.758698	t	5360	682	f
7888	2019-10-04 13:36:30.553892	t	5366	682	f
7889	2019-10-04 13:45:39.147574	t	5359	682	f
7890	2019-10-04 13:33:21.914162	t	5364	682	f
7891	2019-09-24 15:12:20.549805	t	5362	682	f
7892	2019-09-24 15:06:32.528643	t	5361	682	f
7893	2019-11-30 16:28:13.138688	t	5375	682	f
7894	2019-11-30 16:21:33.46609	t	5376	682	f
7895	2019-11-16 16:44:39.358808	t	5374	682	f
7896	2019-10-26 16:06:27.118448	t	5372	682	f
7897	2019-10-26 15:58:27.469988	t	5368	682	f
7898	2019-10-26 15:53:53.976391	t	5367	682	f
7899	2019-10-26 15:49:46.401822	t	5370	682	f
7900	2019-10-26 15:46:51.010015	t	5373	682	f
7901	2020-01-24 11:34:41.185637	t	5365	683	f
7902	2019-10-07 16:40:04.456586	t	5362	683	f
7903	\N	f	5363	683	f
7904	2020-01-24 17:01:16.222715	t	5359	683	f
7905	2020-01-24 16:30:50.669894	t	5366	683	f
7906	2019-10-07 16:27:06.292908	t	5361	683	f
7907	2020-01-24 17:03:40.314196	t	5360	683	f
7908	2020-01-24 12:08:05.132084	t	5364	683	f
7909	2019-12-02 17:36:03.814479	t	5376	683	f
7910	2019-12-02 17:33:52.240618	t	5375	683	f
7911	2019-11-18 01:28:31.06774	t	5374	683	f
7912	2019-10-28 17:58:48.930894	t	5367	683	f
7913	2019-10-28 18:00:54.138801	t	5368	683	f
7914	2019-10-28 15:59:49.470488	t	5373	683	f
7915	2019-10-28 15:39:07.231327	t	5370	683	f
7916	2019-10-28 14:57:32.186588	t	5369	683	f
7917	2019-10-28 14:20:29.801321	t	5371	683	f
7918	2019-10-28 12:06:18.808985	t	5372	683	f
7919	2019-10-24 12:32:53.301598	t	5372	684	f
7920	2019-10-24 11:42:58.67561	t	5367	684	f
7921	2019-10-24 11:39:33.325952	t	5368	684	f
7922	2019-10-23 16:20:46.808964	t	5370	684	f
7923	2019-10-22 12:38:48.690024	t	5369	684	f
7924	2019-10-23 16:14:07.22888	t	5373	684	f
7925	2019-10-22 12:36:53.486334	t	5371	684	f
7926	2019-10-04 12:16:39.798826	t	5364	684	f
7927	2019-10-04 00:28:14.62283	t	5366	684	f
7928	2019-10-04 00:25:47.165132	t	5359	684	f
7929	2019-10-04 00:22:14.616214	t	5365	684	f
7930	2019-10-01 14:16:35.704077	t	5363	684	f
7931	2019-10-01 14:11:16.29497	t	5360	684	f
7932	2019-09-22 22:18:26.523363	t	5361	684	f
7933	2019-09-22 22:15:32.468158	t	5362	684	f
7934	2019-11-28 13:20:06.237452	t	5376	684	f
7935	2019-11-28 11:29:38.67887	t	5375	684	f
7936	2019-11-09 21:29:29.9448	t	5374	684	f
7937	2020-01-08 19:23:42.668764	t	5361	685	f
7938	2020-01-08 19:13:23.885043	t	5366	685	f
7939	2020-01-08 18:39:50.989864	t	5363	685	f
7940	2020-01-08 16:52:01.750468	t	5369	685	f
7941	2020-01-08 16:35:53.965516	t	5370	685	f
7942	2020-01-08 16:10:37.960059	t	5374	685	f
7943	2020-01-08 19:20:09.587559	t	5362	685	f
7944	2020-01-08 19:17:18.482303	t	5364	685	f
7945	2020-01-08 18:50:37.736452	t	5359	685	f
7946	2020-01-08 18:45:14.472678	t	5360	685	f
7947	2020-01-08 18:34:14.895874	t	5365	685	f
7948	2020-01-08 18:30:07.988934	t	5371	685	f
7949	2020-01-08 16:46:49.60725	t	5372	685	f
7950	2020-01-08 16:41:07.673721	t	5373	685	f
7951	2020-01-08 16:24:56.236972	t	5367	685	f
7952	2020-01-08 16:18:00.329166	t	5368	685	f
7953	2020-01-08 16:05:14.22082	t	5376	685	f
7954	2020-01-08 15:08:06.104651	t	5375	685	f
7955	2019-10-26 14:05:47.090265	t	5367	686	f
7956	2019-10-26 13:14:28.437638	t	5372	686	f
7957	2019-10-25 17:32:14.721041	t	5370	686	f
7958	2019-10-25 17:27:37.420469	t	5369	686	f
7959	2019-10-25 17:24:00.308131	t	5373	686	f
7960	2019-10-25 17:18:39.858381	t	5371	686	f
7961	2019-10-07 09:52:19.455954	t	5363	686	f
7962	2019-10-07 09:56:09.54546	t	5360	686	f
7963	2019-10-07 09:02:13.43865	t	5365	686	f
7964	2019-09-30 09:02:43.967491	t	5361	686	f
7965	2019-09-30 08:59:40.342673	t	5362	686	f
7966	2019-09-30 08:56:46.18615	t	5366	686	f
7967	2019-09-30 08:52:07.624204	t	5364	686	f
7968	2019-09-30 08:48:51.792091	t	5359	686	f
7969	2019-12-14 13:33:59.217261	t	5374	686	f
7970	2019-12-14 13:26:55.849108	t	5376	686	f
7971	2019-12-14 13:22:23.67455	t	5375	686	f
7972	2019-10-26 16:07:01.332634	t	5368	686	f
7973	2019-10-06 21:47:45.44834	t	5364	687	f
7974	2019-10-06 21:08:11.150363	t	5360	687	f
7975	2019-10-06 18:54:40.707651	t	5366	687	f
7976	2019-10-06 21:54:34.239671	t	5363	687	f
7977	2019-10-06 21:04:07.486455	t	5365	687	f
7978	2019-10-06 21:35:04.921869	t	5361	687	f
7979	2019-10-06 18:58:31.157731	t	5359	687	f
7980	2019-10-04 17:19:48.385566	t	5362	687	f
7981	\N	f	5376	687	f
7982	\N	f	5375	687	f
7983	2019-11-17 20:40:07.763454	t	5374	687	f
7984	\N	f	5373	687	f
7985	\N	f	5372	687	f
7986	\N	f	5371	687	f
7987	\N	f	5370	687	f
7988	\N	f	5369	687	f
7989	\N	f	5368	687	f
7990	\N	f	5367	687	f
7991	2019-10-07 09:56:04.374463	t	5362	688	f
7992	2019-10-07 09:26:38.186592	t	5359	688	f
7993	2019-10-07 09:17:04.885604	t	5360	688	f
7994	2019-10-07 09:09:09.064166	t	5363	688	f
7995	2019-10-07 08:51:13.326769	t	5365	688	f
7996	2019-10-07 10:03:26.112183	t	5361	688	f
7997	2019-10-07 09:49:35.004926	t	5364	688	f
7998	2019-10-07 09:37:59.3329	t	5366	688	f
7999	2019-12-02 16:17:51.325831	t	5374	688	f
8000	2019-12-02 16:13:23.772234	t	5375	688	f
8001	2019-12-02 16:10:27.641698	t	5376	688	f
8002	2019-10-28 15:26:44.06762	t	5371	688	f
8003	2019-10-28 15:23:20.367412	t	5369	688	f
8004	2019-10-28 15:20:29.355205	t	5372	688	f
8005	2019-10-28 15:16:26.705401	t	5373	688	f
8006	2019-10-28 15:12:48.770019	t	5370	688	f
8007	2019-10-28 15:07:57.420977	t	5367	688	f
8008	2019-10-28 15:05:24.709765	t	5368	688	f
8009	2019-10-06 21:54:03.004024	t	5360	689	f
8010	2019-10-06 21:18:24.405907	t	5359	689	f
8011	2019-10-06 22:10:44.902783	t	5365	689	f
8012	2019-10-06 22:01:48.00059	t	5363	689	f
8013	2019-10-06 19:52:32.46914	t	5361	689	f
8014	2019-10-03 22:06:11.609029	t	5366	689	f
8015	2019-10-03 21:57:04.373826	t	5364	689	f
8016	2019-10-03 21:53:16.86191	t	5362	689	f
8017	2019-10-28 14:27:08.349414	t	5368	689	f
8018	2019-10-28 14:21:18.954192	t	5367	689	f
8019	2019-10-28 14:14:34.026941	t	5370	689	f
8020	2019-10-28 14:00:36.064111	t	5373	689	f
8021	2019-10-28 13:29:22.256582	t	5372	689	f
8022	2019-10-28 13:14:59.565677	t	5369	689	f
8023	2019-10-28 13:04:30.549859	t	5371	689	f
8024	2019-10-26 09:04:32.577615	t	5370	690	f
8025	2019-10-26 08:47:11.84673	t	5373	690	f
8026	2019-10-26 08:37:27.790041	t	5372	690	f
8027	2019-10-26 08:25:50.425437	t	5369	690	f
8028	2019-10-26 08:19:57.470506	t	5371	690	f
8029	2019-10-04 20:23:06.269791	t	5360	690	f
8030	2019-10-04 20:21:02.386343	t	5363	690	f
8031	2019-10-04 20:16:56.990027	t	5365	690	f
8032	2019-09-25 15:18:05.189268	t	5359	690	f
8033	2019-09-24 13:34:46.604929	t	5366	690	f
8034	2019-09-24 12:09:38.145034	t	5364	690	f
8035	2019-09-23 16:14:24.454388	t	5362	690	f
8036	2019-09-23 19:59:51.525752	t	5361	690	f
8037	2019-12-01 16:09:13.481537	t	5375	690	f
8038	2019-12-01 16:04:13.167829	t	5376	690	f
8039	2019-11-16 18:49:39.475867	t	5374	690	f
8040	2019-10-26 16:25:10.230388	t	5368	690	f
8041	2019-10-26 16:13:49.789399	t	5367	690	f
8042	2019-10-07 12:02:02.38055	t	5366	691	f
8043	2019-10-07 11:17:00.408511	t	5364	691	f
8044	2019-10-07 10:59:13.764649	t	5362	691	f
8045	2019-10-06 17:28:09.170646	t	5361	691	f
8046	\N	f	5365	691	f
8047	\N	f	5363	691	f
8048	\N	f	5360	691	f
8049	\N	f	5359	691	f
8050	\N	f	5362	692	f
8051	\N	f	5361	692	f
8052	2019-10-26 15:23:31.723275	t	5367	693	f
8053	2019-10-19 15:32:06.698818	t	5373	693	f
8054	2019-10-19 15:02:59.614244	t	5372	693	f
8055	2019-10-13 10:34:01.557443	t	5369	693	f
8056	2019-10-13 10:08:58.546672	t	5371	693	f
8057	2019-10-02 13:45:29.560836	t	5365	693	f
8058	2019-10-02 12:54:41.822026	t	5360	693	f
8059	2019-10-02 12:30:19.581814	t	5363	693	f
8060	2019-09-26 15:00:14.609334	t	5359	693	f
8061	2019-09-26 14:39:00.737913	t	5366	693	f
8062	2019-09-26 14:27:58.427748	t	5364	693	f
8063	2019-09-21 12:34:34.32252	t	5361	693	f
8064	2019-09-21 12:23:10.703418	t	5362	693	f
8065	2019-12-01 16:13:00.017469	t	5376	693	f
8066	2019-12-01 16:12:50.848986	t	5375	693	f
8067	2019-11-16 18:56:36.591487	t	5374	693	f
8068	2019-10-26 20:35:14.343692	t	5370	693	f
8069	2019-10-26 20:32:33.245174	t	5368	693	f
8070	2020-01-28 00:07:06.309136	t	5376	694	f
8071	2020-01-26 00:31:38.916322	t	5375	694	f
8072	2020-01-25 01:57:04.558852	t	5366	694	f
8073	2020-01-23 23:37:47.370351	t	5371	694	f
8074	2020-01-23 23:07:35.268869	t	5367	694	f
8075	2020-01-26 01:52:52.134187	t	5372	694	f
8076	2020-01-24 00:21:43.900049	t	5373	694	f
8077	2020-01-19 19:59:01.77572	t	5361	694	f
8078	2020-01-19 16:00:18.986776	t	5359	694	f
8079	2020-01-19 15:15:59.911997	t	5362	694	f
8080	\N	f	5374	694	f
8081	2020-01-25 02:46:59.129888	t	5364	694	f
8082	2020-01-25 01:23:00.804007	t	5368	694	f
8083	2020-01-24 00:47:02.947323	t	5369	694	f
8084	2020-01-20 23:59:17.745227	t	5365	694	f
8085	2020-01-20 23:12:51.751121	t	5360	694	f
8086	2020-01-19 19:16:34.488972	t	5363	694	f
8087	2020-01-19 18:07:09.686088	t	5370	694	f
8088	2019-10-07 16:14:02.405765	t	5361	695	f
8089	2019-10-07 15:56:13.471732	t	5364	695	f
8090	2019-10-07 15:46:06.469196	t	5363	695	f
8091	2019-10-07 15:39:14.200374	t	5360	695	f
8092	2019-10-07 15:30:08.668289	t	5359	695	f
8093	2019-10-07 15:11:45.308321	t	5366	695	f
8094	2019-10-07 15:01:15.662821	t	5362	695	f
8095	2019-10-07 14:52:36.828708	t	5365	695	f
8096	2019-12-02 17:42:16.142652	t	5376	695	f
8097	2019-12-02 17:31:28.503791	t	5375	695	f
8098	2019-11-16 22:28:29.424983	t	5374	695	f
8099	2019-10-28 15:23:53.417035	t	5372	695	f
8100	2019-10-28 14:52:57.315228	t	5367	695	f
8101	2019-10-28 11:36:29.093818	t	5370	695	f
8102	2019-10-28 14:37:52.900875	t	5368	695	f
8103	2019-10-28 11:12:38.029575	t	5373	695	f
8104	2019-10-28 10:48:37.816354	t	5369	695	f
8105	2019-10-28 10:26:58.631201	t	5371	695	f
8106	2019-11-16 17:28:52.894479	t	5368	696	f
8107	2019-10-23 13:26:19.569952	t	5367	696	f
8108	2019-10-22 10:35:57.256967	t	5370	696	f
8109	2019-10-17 09:48:35.968248	t	5373	696	f
8110	2019-10-17 09:41:33.767271	t	5372	696	f
8111	2019-10-10 15:15:12.955337	t	5369	696	f
8112	2019-10-10 15:12:10.345257	t	5371	696	f
8113	2019-10-02 17:01:42.50174	t	5365	696	f
8114	2019-10-02 16:58:39.295628	t	5363	696	f
8115	2019-10-02 16:53:11.4711	t	5360	696	f
8116	2019-09-26 14:41:18.820717	t	5359	696	f
8117	2019-09-26 14:36:27.755004	t	5366	696	f
8118	2019-09-26 14:24:48.854806	t	5364	696	f
8119	2019-09-21 14:46:32.376602	t	5361	696	f
8120	2019-09-21 14:43:24.74266	t	5362	696	f
8121	2019-12-01 22:38:07.122493	t	5376	696	f
8122	2019-12-01 22:35:47.173313	t	5375	696	f
8123	2019-11-17 18:14:48.491445	t	5374	696	f
8124	2019-10-07 13:16:56.158138	t	5365	697	f
8125	2019-10-07 13:15:01.846525	t	5360	697	f
8126	2019-10-07 13:12:23.628189	t	5363	697	f
8127	2019-10-07 13:09:04.963378	t	5359	697	f
8128	2019-10-07 13:04:40.500652	t	5366	697	f
8129	2019-10-07 12:54:11.102945	t	5364	697	f
8130	2019-09-21 13:29:15.002771	t	5361	697	f
8131	2019-09-21 13:27:00.514456	t	5362	697	f
8132	2019-12-02 15:45:27.585416	t	5375	697	f
8133	2019-12-02 15:42:48.009059	t	5376	697	f
8134	2019-11-18 11:25:52.244011	t	5374	697	f
8135	2019-10-28 14:13:18.006801	t	5368	697	f
8136	2019-10-28 14:00:40.377892	t	5367	697	f
8137	2019-10-28 13:57:10.43092	t	5370	697	f
8138	2019-10-28 13:55:10.392293	t	5373	697	f
8139	2019-10-28 13:47:34.05487	t	5372	697	f
8140	2019-10-28 13:40:43.057886	t	5369	697	f
8141	2019-10-28 13:32:46.784976	t	5371	697	f
8142	2019-10-28 09:28:26.004763	t	5367	698	f
8143	2019-10-28 09:03:15.560508	t	5373	698	f
8144	2019-10-28 08:54:27.477949	t	5372	698	f
8145	2019-10-28 08:40:08.178153	t	5371	698	f
8146	2019-10-07 12:11:05.963123	t	5359	698	f
8147	2019-10-07 11:58:14.068711	t	5360	698	f
8148	2019-10-07 11:52:32.443715	t	5365	698	f
8149	2019-10-07 12:25:27.277746	t	5366	698	f
8150	2019-10-07 12:03:22.137115	t	5363	698	f
8151	2019-09-30 21:10:38.112244	t	5364	698	f
8152	2019-09-30 21:07:12.173581	t	5362	698	f
8153	2019-09-30 21:04:26.558983	t	5361	698	f
8154	2019-10-28 09:32:29.613011	t	5368	698	f
8155	2019-10-28 09:12:53.761935	t	5370	698	f
8156	2019-10-28 08:45:41.42373	t	5369	698	f
8157	2019-10-28 09:17:05.551376	t	5369	699	f
8158	2019-10-07 11:20:21.97522	t	5365	699	f
8159	2019-10-06 20:16:05.761669	t	5359	699	f
8160	2019-11-17 17:30:19.695986	t	5363	699	f
8161	2019-10-06 22:33:27.83594	t	5360	699	f
8162	2019-10-06 19:11:16.030377	t	5366	699	f
8163	2019-09-22 21:20:32.323201	t	5362	699	f
8164	2019-12-01 19:59:31.966669	t	5375	699	f
8165	2019-12-01 20:16:17.276536	t	5376	699	f
8166	2019-11-17 17:39:51.769105	t	5374	699	f
8167	2019-11-17 17:29:57.533613	t	5364	699	f
8168	2019-11-17 17:29:30.154325	t	5361	699	f
8169	2019-10-28 16:16:38.234844	t	5367	699	f
8170	2019-10-28 10:16:29.596479	t	5373	699	f
8171	2019-10-28 08:39:33.088158	t	5371	699	f
8172	2019-10-28 16:00:56.471792	t	5368	699	f
8173	2019-10-28 13:43:16.658209	t	5372	699	f
8174	2019-10-28 11:33:01.440988	t	5370	699	f
8175	2019-09-21 15:14:07.724541	t	5361	700	f
8176	2019-09-21 15:11:49.001741	t	5362	700	f
8177	2020-01-27 10:28:34.29993	t	5365	700	f
8178	2020-01-27 10:10:50.04979	t	5372	700	f
8179	2020-01-27 10:38:30.546065	t	5363	700	f
8180	2020-01-27 10:34:51.12013	t	5360	700	f
8181	2020-01-27 10:21:13.038397	t	5371	700	f
8182	2020-01-27 10:16:50.83043	t	5369	700	f
8183	2020-01-27 10:01:51.706222	t	5373	700	f
8184	2020-01-21 11:04:55.701986	t	5370	700	f
8185	2020-01-21 10:59:41.400549	t	5367	700	f
8186	2020-01-21 10:46:12.388975	t	5368	700	f
8187	2020-01-21 10:40:50.747147	t	5374	700	f
8188	2020-01-21 10:32:56.443076	t	5375	700	f
8189	2020-01-21 10:26:28.565016	t	5376	700	f
8190	\N	f	5366	700	f
8191	\N	f	5364	700	f
8192	\N	f	5359	700	f
8193	2019-10-05 20:03:05.043276	t	5365	701	f
8194	2019-10-05 19:58:59.138046	t	5360	701	f
8195	2019-10-05 19:55:28.370911	t	5363	701	f
8196	2019-10-05 19:53:14.884794	t	5359	701	f
8197	2019-10-05 19:50:52.877458	t	5366	701	f
8198	2019-10-05 19:47:49.450022	t	5364	701	f
8199	2019-09-21 15:47:58.319711	t	5361	701	f
8200	2019-09-21 15:43:56.855831	t	5362	701	f
8201	2020-01-08 23:18:35.806132	t	5376	701	f
8202	2020-01-08 23:16:12.577434	t	5375	701	f
8203	2019-11-18 12:54:43.948808	t	5374	701	f
8204	2019-10-27 16:28:59.40069	t	5370	701	f
8205	2019-10-27 16:38:47.004558	t	5368	701	f
8206	2019-10-27 16:33:08.959801	t	5367	701	f
8207	2019-10-27 16:18:30.551555	t	5373	701	f
8208	2019-10-27 15:45:23.574722	t	5372	701	f
8209	2019-10-27 15:39:09.720742	t	5369	701	f
8210	2019-10-27 13:47:08.683899	t	5371	701	f
8211	\N	f	5376	702	f
8212	\N	f	5375	702	f
8213	2019-12-11 20:15:20.858482	t	5366	702	f
8214	2019-11-02 16:43:08.161288	t	5362	702	f
8215	2019-11-02 16:27:49.732102	t	5361	702	f
8216	\N	f	5374	702	f
8217	\N	f	5373	702	f
8218	\N	f	5372	702	f
8219	\N	f	5371	702	f
8220	\N	f	5370	702	f
8221	\N	f	5369	702	f
8222	\N	f	5368	702	f
8223	\N	f	5367	702	f
8224	\N	f	5365	702	f
8225	\N	f	5364	702	f
8226	\N	f	5363	702	f
8227	\N	f	5360	702	f
8228	\N	f	5359	702	f
8229	2019-10-05 16:10:51.694528	t	5364	703	f
8230	2019-10-05 16:50:07.017299	t	5363	703	f
8231	2019-10-05 16:44:34.059202	t	5360	703	f
8232	2019-10-02 15:14:26.94757	t	5365	703	f
8233	2019-10-01 14:31:59.080105	t	5362	703	f
8234	2019-10-05 16:28:44.000596	t	5359	703	f
8235	2019-10-05 16:19:22.331343	t	5366	703	f
8236	2019-09-25 19:50:48.401823	t	5361	703	f
8237	\N	f	5376	703	f
8238	\N	f	5375	703	f
8239	\N	f	5374	703	f
8240	2019-10-28 17:09:51.795033	t	5368	703	f
8241	2019-10-26 21:44:57.867929	t	5367	703	f
8242	\N	f	5373	703	f
8243	\N	f	5372	703	f
8244	\N	f	5371	703	f
8245	\N	f	5370	703	f
8246	\N	f	5369	703	f
8247	2019-10-26 10:37:57.973817	t	5370	704	f
8248	2019-10-26 10:36:15.146862	t	5367	704	f
8249	2019-10-28 15:47:59.670581	t	5368	704	f
8250	2019-10-28 15:44:42.813058	t	5373	704	f
8251	2019-10-27 11:52:41.582674	t	5369	704	f
8252	2019-10-25 15:27:55.260278	t	5372	704	f
8253	2019-10-07 15:23:18.113952	t	5363	704	f
8254	2019-10-06 16:47:44.166122	t	5360	704	f
8255	2019-10-06 16:40:30.499466	t	5365	704	f
8256	2019-10-07 15:24:58.56854	t	5366	704	f
8257	2019-10-02 15:07:12.135867	t	5359	704	f
8258	2019-10-01 11:12:24.904771	t	5364	704	f
8259	2019-10-01 11:09:52.715185	t	5361	704	f
8260	2019-09-21 12:34:08.436103	t	5362	704	f
8261	2019-11-29 22:51:24.528561	t	5376	704	f
8262	2019-11-29 22:41:04.62323	t	5375	704	f
8263	2019-11-14 08:28:52.430816	t	5374	704	f
8264	2019-10-27 11:55:31.137201	t	5371	704	f
8265	2019-10-26 20:41:27.85449	t	5368	705	f
8266	2019-10-23 18:11:01.85707	t	5370	705	f
8267	2019-10-23 17:57:25.993078	t	5367	705	f
8268	2019-10-17 09:49:09.025066	t	5373	705	f
8269	2019-10-17 09:42:33.017487	t	5372	705	f
8270	2019-10-10 15:15:46.556248	t	5369	705	f
8271	2019-10-10 15:12:13.845051	t	5371	705	f
8272	2019-10-02 13:25:16.751466	t	5365	705	f
8273	2019-10-02 13:13:16.91427	t	5360	705	f
8274	2019-10-02 13:02:00.289902	t	5363	705	f
8275	2019-09-25 16:38:14.531496	t	5359	705	f
8276	2019-09-25 16:31:30.060239	t	5366	705	f
8277	2019-09-25 16:10:24.914355	t	5364	705	f
8278	2019-09-21 13:04:31.159628	t	5361	705	f
8279	2019-09-21 13:00:00.36252	t	5362	705	f
8280	2019-11-30 22:00:08.083961	t	5376	705	f
8281	2019-11-30 20:01:33.10381	t	5375	705	f
8282	2019-11-03 09:03:12.890002	t	5374	705	f
8283	2019-10-07 15:00:53.500578	t	5366	706	f
8284	\N	f	5365	706	f
8285	\N	f	5363	706	f
8286	\N	f	5360	706	f
8287	\N	f	5359	706	f
8288	2019-10-07 14:52:50.522508	t	5364	706	f
8289	2019-09-24 16:54:22.700086	t	5362	706	f
8290	2019-09-24 16:40:10.300515	t	5361	706	f
8291	\N	f	5373	706	f
8292	\N	f	5372	706	f
8293	\N	f	5371	706	f
8294	\N	f	5370	706	f
8295	\N	f	5369	706	f
8296	\N	f	5368	706	f
8297	\N	f	5367	706	f
8298	2019-10-26 14:20:03.175608	t	5367	707	f
8299	2019-10-26 11:51:39.710209	t	5368	707	f
8300	2019-10-25 21:36:57.332255	t	5373	707	f
8301	2019-10-16 16:50:11.894567	t	5372	707	f
8302	2019-10-10 20:07:09.338078	t	5369	707	f
8303	2019-10-10 15:20:53.238423	t	5371	707	f
8304	2019-10-02 20:50:45.677697	t	5365	707	f
8305	2019-09-30 22:49:54.382712	t	5360	707	f
8306	2019-10-01 11:59:12.414796	t	5363	707	f
8307	2019-09-26 23:22:03.193036	t	5359	707	f
8308	2019-09-26 23:19:26.709421	t	5366	707	f
8309	2019-09-26 23:12:01.932477	t	5364	707	f
8310	2019-09-23 19:54:13.338174	t	5362	707	f
8311	2019-09-21 13:44:57.059423	t	5361	707	f
8312	2019-11-30 11:32:49.446576	t	5376	707	f
8313	2019-11-29 11:34:09.552331	t	5375	707	f
8314	2019-11-16 16:39:00.000112	t	5374	707	f
8315	2019-11-16 16:28:26.589703	t	5370	707	f
8316	2019-10-07 10:14:21.758568	t	5365	708	f
8317	2019-10-07 10:12:13.301743	t	5360	708	f
8318	2019-10-07 10:09:45.467948	t	5363	708	f
8319	2019-10-07 10:06:56.190887	t	5359	708	f
8320	2019-10-07 10:03:22.793556	t	5366	708	f
8321	2019-10-07 09:56:49.784732	t	5364	708	f
8322	2019-10-07 09:53:56.758906	t	5362	708	f
8323	2019-10-07 09:49:44.528557	t	5361	708	f
8324	2019-12-02 12:47:19.181207	t	5376	708	f
8325	2019-12-02 12:34:51.539979	t	5375	708	f
8326	2019-11-18 13:07:07.965122	t	5374	708	f
8327	2019-10-28 14:58:10.55447	t	5368	708	f
8328	2019-10-28 12:42:34.044227	t	5367	708	f
8329	2019-10-28 12:36:23.62182	t	5370	708	f
8330	2019-10-28 12:30:26.470375	t	5373	708	f
8331	2019-10-28 12:11:48.282781	t	5372	708	f
8332	2019-10-28 11:23:22.139141	t	5369	708	f
8333	2019-10-28 10:22:40.436847	t	5371	708	f
8334	2019-10-06 18:44:53.446143	t	5363	709	f
8335	2019-10-06 18:20:10.633783	t	5359	709	f
8336	2019-10-06 19:04:15.887727	t	5365	709	f
8337	2019-10-06 18:35:58.011885	t	5360	709	f
8338	2019-10-06 17:57:49.230319	t	5366	709	f
8339	2019-10-06 17:37:14.561093	t	5364	709	f
8340	2019-09-22 17:39:07.508501	t	5361	709	f
8341	2019-09-22 17:32:52.330898	t	5362	709	f
8342	2019-12-02 14:04:20.77877	t	5376	709	f
8343	2019-11-29 19:31:37.898865	t	5375	709	f
8344	2020-01-23 19:45:58.160365	t	5374	709	f
8345	2019-10-27 22:30:33.158332	t	5370	709	f
8346	2019-10-27 18:26:47.301021	t	5369	709	f
8347	2019-10-27 23:04:08.821391	t	5368	709	f
8348	2019-10-27 19:41:11.358166	t	5373	709	f
8349	2019-10-27 19:11:26.075281	t	5372	709	f
8350	2019-10-27 16:56:41.988879	t	5371	709	f
8351	2019-10-26 15:59:59.81114	t	5367	709	f
8352	2019-10-26 15:07:01.706177	t	5372	710	f
8353	2019-10-25 17:15:39.931545	t	5371	710	f
8354	2019-10-27 11:42:06.195066	t	5368	710	f
8355	2019-10-25 17:03:38.731909	t	5369	710	f
8356	2019-10-05 16:34:48.0368	t	5361	710	f
8357	2019-10-03 15:37:34.284905	t	5365	710	f
8358	2019-10-03 11:24:08.319621	t	5362	710	f
8359	2019-10-06 20:45:56.404181	t	5364	710	f
8360	2019-10-05 18:09:52.360495	t	5363	710	f
8361	2019-10-03 17:14:46.555543	t	5360	710	f
8362	2019-10-03 11:33:40.924315	t	5366	710	f
8363	2019-10-03 09:57:45.972843	t	5359	710	f
8364	2019-12-01 17:42:19.008896	t	5376	710	f
8365	2019-12-01 16:44:42.787903	t	5375	710	f
8366	2019-11-16 18:19:10.66869	t	5374	710	f
8367	2019-10-26 21:18:52.571409	t	5370	710	f
8368	2019-10-26 16:36:49.471215	t	5367	710	f
8369	2019-10-26 16:09:20.771697	t	5373	710	f
8370	2019-10-14 09:08:48.222639	t	5369	711	f
8371	2019-10-14 08:35:54.504681	t	5371	711	f
8372	2019-10-07 13:13:07.947077	t	5363	711	f
8373	2019-10-07 11:36:28.720447	t	5360	711	f
8374	2019-10-07 11:14:09.563463	t	5364	711	f
8375	2019-10-07 10:43:22.539808	t	5361	711	f
8376	2019-10-07 12:00:30.371089	t	5365	711	f
8377	2019-10-07 11:31:58.486107	t	5359	711	f
8378	2019-10-07 11:18:21.843479	t	5366	711	f
8379	2019-10-07 10:39:04.809701	t	5362	711	f
8380	2019-11-25 15:05:54.722763	t	5376	711	f
8381	2019-11-25 15:02:57.451225	t	5375	711	f
8382	2019-11-18 13:01:08.130407	t	5374	711	f
8383	2019-11-18 12:52:14.018422	t	5370	711	f
8384	\N	f	5373	711	f
8385	\N	f	5372	711	f
8386	\N	f	5368	711	f
8387	\N	f	5367	711	f
8388	2019-10-06 18:15:44.598754	t	5363	712	f
8389	2019-10-05 22:31:16.718519	t	5364	712	f
8390	2019-10-05 21:51:01.744648	t	5362	712	f
8391	2019-10-06 22:47:26.467929	t	5365	712	f
8392	2019-10-06 19:55:31.335663	t	5360	712	f
8393	2019-10-05 23:27:50.983324	t	5359	712	f
8394	2019-10-05 22:57:53.840004	t	5366	712	f
8395	2019-10-05 20:50:50.994685	t	5361	712	f
8396	\N	f	5376	712	f
8397	\N	f	5375	712	f
8398	2019-11-18 13:35:57.710452	t	5374	712	f
8399	\N	f	5373	712	f
8400	\N	f	5372	712	f
8401	\N	f	5371	712	f
8402	\N	f	5370	712	f
8403	\N	f	5369	712	f
8404	\N	f	5368	712	f
8405	\N	f	5367	712	f
8406	2019-10-27 18:05:40.503916	t	5369	713	f
8407	2019-10-07 00:23:14.433949	t	5365	713	f
8408	2019-10-06 23:38:07.212246	t	5363	713	f
8409	2019-10-06 21:38:36.492013	t	5364	713	f
8410	2019-10-06 21:24:28.089223	t	5360	713	f
8411	2019-10-06 20:45:26.789332	t	5366	713	f
8412	2019-10-03 14:18:49.540296	t	5359	713	f
8413	2019-09-21 16:37:33.714151	t	5362	713	f
8414	2019-09-21 16:19:44.812996	t	5361	713	f
8415	2020-01-08 14:21:41.385637	t	5368	713	f
8416	2019-12-02 15:39:29.509309	t	5376	713	f
8417	2019-12-02 00:41:59.658058	t	5375	713	f
8418	2019-11-18 15:52:58.526698	t	5374	713	f
8419	2019-10-28 13:46:39.304898	t	5370	713	f
8420	2019-10-28 16:26:22.295188	t	5367	713	f
8421	2019-10-28 14:58:51.859437	t	5372	713	f
8422	2019-10-28 13:03:22.584386	t	5373	713	f
8423	2019-10-27 16:13:15.642404	t	5371	713	f
8424	2019-10-07 13:35:03.998354	t	5363	714	f
8425	2019-10-06 01:16:30.059485	t	5364	714	f
8426	2019-10-06 01:14:43.302165	t	5365	714	f
8427	2019-10-06 01:11:37.678989	t	5359	714	f
8428	2019-10-06 01:09:37.380293	t	5360	714	f
8429	2019-10-06 01:07:28.289222	t	5366	714	f
8430	2019-09-21 18:29:45.967618	t	5362	714	f
8431	2019-09-21 18:26:05.711105	t	5361	714	f
8432	2019-12-02 02:19:49.836873	t	5376	714	f
8433	2019-12-02 02:18:00.190218	t	5375	714	f
8434	2019-11-18 14:11:22.308746	t	5374	714	f
8435	2019-10-28 14:50:32.71992	t	5367	714	f
8436	2019-10-27 15:34:15.740601	t	5369	714	f
8437	2019-10-28 14:52:46.694528	t	5368	714	f
8438	2019-10-27 22:03:21.391451	t	5372	714	f
8439	2019-10-27 20:23:52.842838	t	5370	714	f
8440	2019-10-27 15:36:48.650251	t	5373	714	f
8441	2019-10-27 14:11:23.47231	t	5371	714	f
8442	2019-10-26 13:07:58.29128	t	5371	715	f
8443	2020-01-25 17:43:22.586312	t	5373	715	f
8444	2020-01-25 17:38:22.442857	t	5372	715	f
8445	2019-10-06 23:58:34.874814	t	5365	715	f
8446	2019-10-06 23:53:50.802006	t	5363	715	f
8447	2019-10-06 23:50:26.360876	t	5360	715	f
8448	2019-10-06 23:47:06.957804	t	5359	715	f
8449	2019-10-06 23:41:53.642846	t	5366	715	f
8450	2019-10-06 23:33:47.669734	t	5364	715	f
8451	2019-10-06 23:22:10.195233	t	5361	715	f
8452	2019-09-22 21:22:50.142414	t	5362	715	f
8453	2020-01-25 17:54:18.332636	t	5370	715	f
8454	2020-01-25 17:33:27.074113	t	5369	715	f
8455	2020-01-25 18:11:00.613618	t	5376	715	f
8456	2020-01-25 18:06:59.071129	t	5375	715	f
8457	2020-01-25 18:03:52.027329	t	5374	715	f
8458	2020-01-25 17:58:24.790311	t	5367	715	f
8459	2019-10-28 16:34:26.472265	t	5368	715	f
8460	2019-10-26 14:46:00.928609	t	5368	716	f
8461	2019-10-25 20:24:52.540207	t	5370	716	f
8462	2019-10-24 21:01:26.385303	t	5367	716	f
8463	2019-10-24 20:57:33.269386	t	5372	716	f
8464	2019-10-24 20:50:03.147498	t	5373	716	f
8465	2019-10-24 20:22:23.763775	t	5369	716	f
8466	2019-10-24 20:13:36.725999	t	5371	716	f
8467	2019-10-06 12:25:19.081385	t	5360	716	f
8468	2019-10-06 11:17:38.331112	t	5364	716	f
8469	2019-10-06 17:29:16.695295	t	5365	716	f
8470	2019-10-06 17:09:04.222657	t	5363	716	f
8471	2019-10-06 11:29:07.64547	t	5359	716	f
8472	2019-10-06 11:23:37.562029	t	5366	716	f
8473	2019-10-05 10:47:48.334527	t	5362	716	f
8474	2019-09-23 18:59:02.130478	t	5361	716	f
8475	2019-12-01 17:35:38.621376	t	5376	716	f
8476	2019-12-01 15:54:15.04462	t	5375	716	f
8477	2019-11-15 12:26:51.501967	t	5374	716	f
8478	2019-10-28 14:54:44.350739	t	5372	717	f
8479	2019-10-28 08:42:42.727788	t	5371	717	f
8480	2019-10-06 19:07:19.643757	t	5366	717	f
8481	2019-10-06 18:48:47.254818	t	5362	717	f
8482	2019-10-06 18:44:20.196843	t	5361	717	f
8483	2019-10-02 21:06:48.004831	t	5359	717	f
8484	2019-10-02 21:06:09.990838	t	5363	717	f
8485	2019-10-02 21:01:40.493193	t	5360	717	f
8486	2019-10-02 20:58:01.759553	t	5364	717	f
8487	2019-10-02 20:48:00.945951	t	5365	717	f
8488	2019-12-01 16:00:05.800681	t	5376	717	f
8489	2019-12-01 15:45:54.436644	t	5375	717	f
8490	2019-11-10 12:18:46.202768	t	5374	717	f
8491	2019-10-28 15:09:09.294411	t	5367	717	f
8492	2019-10-28 09:54:25.403487	t	5369	717	f
8493	2019-10-27 14:23:14.533143	t	5368	717	f
8494	2019-10-28 15:15:48.792642	t	5370	717	f
8495	2019-10-28 11:36:42.968609	t	5373	717	f
8496	2019-10-18 14:45:17.907145	t	5373	718	f
8497	2019-10-18 14:41:40.378484	t	5372	718	f
8498	2019-10-13 16:55:22.734886	t	5369	718	f
8499	2019-10-13 16:46:25.773193	t	5371	718	f
8500	2019-10-07 14:20:21.065668	t	5365	718	f
8501	2019-10-01 16:41:08.359198	t	5363	718	f
8502	2019-10-01 16:38:00.125328	t	5360	718	f
8503	2019-10-01 16:35:19.839337	t	5359	718	f
8504	2019-10-01 16:32:34.324394	t	5366	718	f
8505	2019-10-01 16:26:32.271751	t	5364	718	f
8506	2019-09-21 20:33:48.136253	t	5362	718	f
8507	2019-09-21 20:24:48.849942	t	5361	718	f
8508	2019-12-02 15:49:02.729004	t	5376	718	f
8509	2019-12-02 15:01:00.288562	t	5375	718	f
8510	2019-11-18 14:58:53.759348	t	5374	718	f
8511	2019-10-28 15:45:33.549247	t	5368	718	f
8512	2019-10-28 15:41:22.065146	t	5367	718	f
8513	2019-10-28 15:38:29.174658	t	5370	718	f
8514	2019-10-07 13:15:28.967074	t	5360	719	f
8515	2019-10-07 13:11:51.75975	t	5359	719	f
8516	2019-10-07 12:28:38.64909	t	5363	719	f
8517	2019-10-07 12:23:55.775029	t	5366	719	f
8518	2019-10-07 12:18:34.739986	t	5364	719	f
8519	2019-10-07 12:15:13.458648	t	5362	719	f
8520	2019-10-07 12:11:34.30911	t	5361	719	f
8521	2019-12-02 15:52:16.298513	t	5376	719	f
8522	2019-12-02 15:47:20.149512	t	5375	719	f
8523	2019-11-18 12:57:03.219277	t	5374	719	f
8524	2019-10-28 12:19:02.204281	t	5365	719	f
8525	2019-10-28 00:19:22.129235	t	5368	719	f
8526	2019-10-28 00:16:04.447347	t	5367	719	f
8527	2019-10-28 00:03:05.276921	t	5370	719	f
8528	2019-10-27 23:59:34.480046	t	5373	719	f
8529	2019-10-27 23:53:59.872655	t	5372	719	f
8530	2019-10-27 23:44:57.770175	t	5369	719	f
8531	2019-10-27 23:40:58.900122	t	5371	719	f
8532	2019-10-21 18:12:53.636828	t	5373	720	f
8533	2019-10-19 10:16:36.28188	t	5372	720	f
8534	2019-10-12 21:47:23.993019	t	5369	720	f
8535	2019-10-12 21:31:07.163122	t	5371	720	f
8536	2019-10-04 22:12:53.23349	t	5365	720	f
8537	2019-10-04 11:27:29.269556	t	5360	720	f
8538	2019-10-04 11:15:49.368229	t	5363	720	f
8539	2019-09-29 00:26:32.321353	t	5359	720	f
8540	2019-09-26 14:34:00.053069	t	5366	720	f
8541	2019-09-26 14:27:53.23343	t	5364	720	f
8542	2019-09-23 13:47:40.67445	t	5362	720	f
8543	2019-09-21 15:07:09.169954	t	5361	720	f
8544	2019-11-30 23:46:51.125918	t	5376	720	f
8545	2019-11-30 23:37:14.396	t	5375	720	f
8546	2019-11-16 12:37:27.453574	t	5374	720	f
8547	2019-10-28 17:09:08.17115	t	5368	720	f
8548	2019-10-26 16:25:55.44869	t	5370	720	f
8549	2019-10-26 16:10:16.113417	t	5367	720	f
8550	2019-10-07 16:48:06.714006	t	5365	721	f
8551	2019-10-07 15:53:50.746461	t	5363	721	f
8552	2019-10-07 12:16:41.961851	t	5360	721	f
8553	2019-10-06 17:27:10.233025	t	5361	721	f
8554	2019-10-07 12:06:14.124606	t	5359	721	f
8555	2019-10-07 11:55:02.431998	t	5364	721	f
8556	2019-10-06 16:50:14.157156	t	5366	721	f
8557	2019-10-06 11:41:36.042261	t	5362	721	f
8558	2019-12-02 15:02:03.039192	t	5376	721	f
8559	2019-12-02 14:46:34.076315	t	5375	721	f
8560	2019-11-18 11:06:03.460081	t	5374	721	f
8561	2019-10-28 16:38:12.015651	t	5367	721	f
8562	2019-10-28 16:24:21.841169	t	5369	721	f
8563	2019-10-28 16:45:14.691931	t	5372	721	f
8564	2019-10-28 16:34:09.839748	t	5370	721	f
8565	2019-10-28 16:30:59.020752	t	5373	721	f
8566	2019-10-28 16:16:51.843893	t	5371	721	f
8567	2019-10-28 14:15:03.315169	t	5368	721	f
8568	2019-10-02 11:04:22.714814	t	5360	722	f
8569	\N	f	5363	722	f
8570	2019-11-18 11:54:33.464491	t	5359	722	f
8571	2019-10-02 11:31:34.06057	t	5366	722	f
8572	2019-10-02 10:55:26.806218	t	5362	722	f
8573	2019-09-26 11:15:58.020376	t	5361	722	f
8574	\N	f	5364	722	f
8575	2019-12-02 15:01:11.061999	t	5376	722	f
8576	2019-12-02 14:27:40.529418	t	5375	722	f
8577	2019-11-18 14:52:58.292764	t	5374	722	f
8578	2019-10-28 15:17:35.593	t	5368	722	f
8579	2019-10-28 12:56:09.744374	t	5370	722	f
8580	2020-01-07 16:25:42.162465	t	5365	722	f
8581	2019-10-28 15:23:07.046737	t	5372	722	f
8582	2019-10-28 15:06:32.388896	t	5369	722	f
8583	2019-10-28 14:02:33.591558	t	5367	722	f
8584	2019-10-28 11:41:45.907325	t	5373	722	f
8585	2019-10-27 15:16:34.958692	t	5371	722	f
8586	2019-10-06 21:16:08.87061	t	5365	723	f
8587	2019-10-06 21:12:34.111067	t	5360	723	f
8588	2019-10-06 21:09:58.888276	t	5363	723	f
8589	2019-09-29 14:31:50.623926	t	5359	723	f
8590	2019-09-29 14:28:47.759632	t	5366	723	f
8591	2019-09-29 14:26:23.036082	t	5364	723	f
8592	2019-09-21 08:38:35.55465	t	5362	723	f
8593	2019-09-21 08:32:37.804198	t	5361	723	f
8594	2019-12-16 11:04:38.43939	t	5376	723	f
8595	2019-12-02 14:24:50.059475	t	5375	723	f
8596	2019-11-04 14:58:49.771447	t	5374	723	f
8597	2019-10-27 10:02:05.114038	t	5370	723	f
8598	2019-10-27 09:49:08.635483	t	5369	723	f
8599	2019-10-27 10:07:11.391576	t	5368	723	f
8600	2019-10-27 10:04:41.389271	t	5367	723	f
8601	2019-10-27 09:58:49.83511	t	5373	723	f
8602	2019-10-27 09:54:54.122956	t	5372	723	f
8603	2019-10-27 09:46:46.312372	t	5371	723	f
8604	2019-10-13 16:09:29.045853	t	5371	724	f
8605	2019-10-04 16:29:30.946043	t	5365	724	f
8606	2019-10-04 16:12:22.878996	t	5360	724	f
8607	2019-10-03 13:56:33.303557	t	5363	724	f
8608	2019-10-03 13:47:22.868512	t	5364	724	f
8609	2019-10-02 18:11:23.103531	t	5359	724	f
8610	2019-10-02 17:36:23.8977	t	5366	724	f
8611	2019-09-22 01:43:39.224506	t	5362	724	f
8612	2019-09-22 01:22:21.800712	t	5361	724	f
8613	2019-12-01 17:30:52.356104	t	5376	724	f
8614	2019-12-01 17:25:47.33857	t	5375	724	f
8615	2019-11-17 00:48:36.53928	t	5374	724	f
8616	2019-10-27 17:18:41.314629	t	5367	724	f
8617	2019-10-27 18:01:28.145554	t	5368	724	f
8618	2019-10-27 16:36:01.947031	t	5372	724	f
8619	2019-10-26 23:42:18.897789	t	5370	724	f
8620	2019-10-26 21:30:01.189273	t	5373	724	f
8621	2019-10-26 17:53:44.825202	t	5369	724	f
8622	2019-10-07 13:33:28.344654	t	5365	725	f
8623	2019-10-07 13:28:56.820335	t	5360	725	f
8624	2019-10-06 11:54:55.491285	t	5359	725	f
8625	2019-10-06 11:47:46.774955	t	5363	725	f
8626	2019-10-06 11:42:34.962102	t	5366	725	f
8627	2019-10-06 11:36:38.468824	t	5364	725	f
8628	2019-10-06 11:33:27.636558	t	5362	725	f
8629	2019-10-06 11:23:18.083789	t	5361	725	f
8630	2019-12-01 18:14:39.546681	t	5376	725	f
8631	2019-12-01 17:05:08.520763	t	5375	725	f
8632	2019-11-17 16:33:16.000712	t	5374	725	f
8633	2019-10-27 17:14:37.437241	t	5370	725	f
8634	2019-10-27 15:39:45.995979	t	5367	725	f
8635	2019-10-27 14:40:00.739492	t	5372	725	f
8636	2019-10-28 16:39:52.951791	t	5368	725	f
8637	2019-10-27 15:29:28.199289	t	5373	725	f
8638	2019-10-27 14:33:22.566889	t	5369	725	f
8639	2019-10-27 14:03:03.430749	t	5371	725	f
8640	2019-10-26 22:56:39.957723	t	5368	726	f
8641	2019-10-26 22:19:58.618162	t	5370	726	f
8642	2019-10-24 17:28:23.570724	t	5373	726	f
8643	2019-10-24 17:23:08.139753	t	5367	726	f
8644	2019-10-24 17:19:38.935789	t	5372	726	f
8645	2019-10-12 18:27:03.625454	t	5369	726	f
8646	2019-10-12 18:15:19.139854	t	5371	726	f
8647	2019-10-02 18:05:50.419075	t	5365	726	f
8648	2019-10-02 18:01:25.317045	t	5360	726	f
8649	2019-10-02 17:51:06.43948	t	5363	726	f
8650	2019-10-02 17:40:31.03347	t	5359	726	f
8651	2019-09-26 15:37:53.646824	t	5366	726	f
8652	2019-09-26 15:35:25.730839	t	5364	726	f
8653	2019-09-22 22:18:12.239521	t	5362	726	f
8654	2019-09-22 22:16:06.985111	t	5361	726	f
8655	2019-11-29 12:37:03.652647	t	5376	726	f
8656	2019-11-29 12:28:21.964107	t	5375	726	f
8657	2019-11-11 11:59:46.943508	t	5374	726	f
8658	2019-10-07 14:16:20.023319	t	5365	727	f
8659	2019-10-07 14:13:16.223847	t	5360	727	f
8660	2019-10-07 14:11:06.126023	t	5363	727	f
8661	2019-10-07 13:57:30.442324	t	5364	727	f
8662	2019-10-07 14:07:48.169346	t	5359	727	f
8663	2019-10-07 13:59:51.389989	t	5366	727	f
8664	2019-10-07 13:52:47.420558	t	5362	727	f
8665	2019-09-30 16:57:03.347032	t	5361	727	f
8666	2019-12-02 14:29:34.999322	t	5374	727	f
8667	\N	f	5376	727	f
8668	\N	f	5375	727	f
8669	\N	f	5373	727	f
8670	\N	f	5372	727	f
8671	\N	f	5371	727	f
8672	\N	f	5370	727	f
8673	\N	f	5369	727	f
8674	\N	f	5368	727	f
8675	\N	f	5367	727	f
8676	2019-10-28 16:52:35.647828	t	5368	728	f
8677	2019-10-28 16:43:13.342237	t	5370	728	f
8678	2019-10-28 16:39:05.974219	t	5373	728	f
8679	2019-10-23 21:17:33.813565	t	5372	728	f
8680	2019-10-23 21:02:27.845693	t	5369	728	f
8681	2019-10-23 20:42:18.09625	t	5371	728	f
8682	2019-10-06 23:44:07.327739	t	5360	728	f
8683	2019-10-06 23:14:50.798449	t	5364	728	f
8684	2019-10-07 00:01:59.519157	t	5365	728	f
8685	2019-10-06 23:30:07.839538	t	5363	728	f
8686	2019-10-04 07:41:27.088599	t	5359	728	f
8687	2019-10-04 07:29:15.884193	t	5366	728	f
8688	2019-09-23 07:32:56.835757	t	5362	728	f
8689	2019-09-23 07:28:54.677053	t	5361	728	f
8690	2019-12-02 16:29:22.38834	t	5376	728	f
8691	2019-12-02 16:24:06.518994	t	5375	728	f
8692	2019-11-18 16:10:18.88862	t	5374	728	f
8693	2019-10-28 16:48:04.464829	t	5367	728	f
8694	2019-10-07 10:31:47.14329	t	5359	729	f
8695	2019-10-07 10:04:52.505511	t	5362	729	f
8696	2019-10-04 15:23:06.513105	t	5366	729	f
8697	2019-10-04 14:29:22.730831	t	5365	729	f
8698	2019-10-07 10:39:30.538146	t	5360	729	f
8699	2019-10-07 10:36:44.474311	t	5363	729	f
8700	2019-10-07 10:27:45.865407	t	5364	729	f
8701	2019-10-07 10:22:09.907068	t	5361	729	f
8702	2019-12-02 10:54:54.195259	t	5376	729	f
8703	2019-12-02 10:52:28.220671	t	5375	729	f
8704	2019-11-18 11:57:53.168526	t	5374	729	f
8705	2019-10-28 13:08:04.716665	t	5368	729	f
8706	2019-10-28 12:32:25.00975	t	5367	729	f
8707	2019-10-28 12:07:28.920047	t	5370	729	f
8708	2019-10-28 11:05:52.231061	t	5371	729	f
8709	2019-10-28 12:03:12.46205	t	5373	729	f
8710	2019-10-28 11:00:14.147783	t	5369	729	f
8711	2019-10-28 10:39:34.130382	t	5372	729	f
8712	2019-10-23 17:04:04.266792	t	5368	730	f
8713	2019-10-22 15:25:02.176435	t	5367	730	f
8714	2019-10-19 13:28:06.940595	t	5372	730	f
8715	2019-10-19 11:34:00.150158	t	5373	730	f
8716	2019-10-18 12:46:21.535946	t	5369	730	f
8717	2019-10-18 10:29:33.372751	t	5371	730	f
8718	2019-10-03 12:55:03.251933	t	5365	730	f
8719	2019-10-03 12:30:00.722347	t	5360	730	f
8720	2019-10-02 14:16:47.062457	t	5359	730	f
8721	2019-10-01 20:41:05.09416	t	5366	730	f
8722	2019-10-01 10:01:32.883397	t	5363	730	f
8723	2019-10-07 10:48:35.195171	t	5364	730	f
8724	2019-09-24 16:28:44.770756	t	5361	730	f
8725	2019-09-24 16:21:54.585137	t	5362	730	f
8726	2019-12-25 10:45:51.039654	t	5376	730	f
8727	2019-12-25 10:43:56.836057	t	5375	730	f
8728	2019-11-17 14:24:26.067593	t	5374	730	f
8729	2019-10-27 10:12:21.579812	t	5370	730	f
8730	2019-10-07 15:41:10.803595	t	5360	731	f
8731	2019-10-07 15:48:24.864212	t	5363	731	f
8732	2019-10-07 15:44:29.458985	t	5365	731	f
8733	2019-10-07 15:37:37.970831	t	5364	731	f
8734	2019-10-06 21:23:32.147289	t	5359	731	f
8735	2019-10-06 21:18:34.915951	t	5366	731	f
8736	2019-09-23 10:23:02.783204	t	5361	731	f
8737	2019-09-21 11:25:00.6415	t	5362	731	f
8738	2020-01-27 10:32:40.819776	t	5370	731	f
8739	2019-12-02 16:24:29.012023	t	5376	731	f
8740	2019-12-02 13:42:43.25947	t	5375	731	f
8741	2019-11-18 15:32:37.941536	t	5374	731	f
8742	2019-10-28 16:52:12.312731	t	5371	731	f
8743	2020-01-27 10:42:26.568314	t	5373	731	f
8744	2019-10-28 17:30:33.667657	t	5368	731	f
8745	2019-10-28 16:56:55.413715	t	5369	731	f
8746	2019-10-28 16:19:06.383015	t	5367	731	f
8747	2019-10-28 16:11:00.982585	t	5372	731	f
8748	2019-10-28 09:19:50.679472	t	5368	732	f
8749	2019-10-27 16:04:23.107702	t	5370	732	f
8750	2019-10-25 15:57:03.086581	t	5372	732	f
8751	2019-10-25 15:50:34.718564	t	5367	732	f
8752	2019-10-25 15:45:38.968963	t	5373	732	f
8753	2019-10-25 15:33:28.999782	t	5369	732	f
8754	2019-10-13 13:26:13.71497	t	5371	732	f
8755	2019-10-02 15:22:13.916651	t	5365	732	f
8756	2019-10-02 15:01:04.558826	t	5363	732	f
8757	2019-10-02 14:55:38.263039	t	5360	732	f
8758	2019-10-02 14:35:18.483881	t	5359	732	f
8759	2019-10-02 14:27:43.037789	t	5362	732	f
8760	2019-10-02 14:21:52.951598	t	5366	732	f
8761	2019-10-02 14:17:30.509832	t	5364	732	f
8762	2019-09-25 10:31:08.533064	t	5361	732	f
8763	2019-12-02 15:37:35.740464	t	5376	732	f
8764	2019-12-02 09:57:13.430756	t	5375	732	f
8765	2019-11-18 10:24:57.002703	t	5374	732	f
8766	2019-10-28 15:00:50.213744	t	5370	733	f
8767	2019-10-28 13:49:53.31298	t	5373	733	f
8768	2019-10-24 21:23:10.874086	t	5369	733	f
8769	2019-10-24 17:45:40.737974	t	5371	733	f
8770	2019-10-24 17:19:54.514882	t	5365	733	f
8771	\N	f	5368	733	f
8772	2019-10-07 15:07:54.202677	t	5359	733	f
8773	2019-10-24 17:07:17.313952	t	5363	733	f
8774	2019-10-07 15:21:30.377722	t	5360	733	f
8775	2019-10-07 14:53:00.119859	t	5366	733	f
8776	2019-10-07 14:43:45.419752	t	5364	733	f
8777	2019-10-07 14:32:08.150038	t	5361	733	f
8778	2019-10-07 14:37:04.639997	t	5362	733	f
8779	\N	f	5376	733	f
8780	\N	f	5375	733	f
8781	\N	f	5374	733	f
8782	2019-10-28 15:37:48.675107	t	5367	733	f
8783	2019-10-26 20:20:46.180001	t	5372	733	f
8784	2019-10-07 10:00:53.880028	t	5360	734	f
8785	2019-10-07 09:18:09.394492	t	5366	734	f
8786	2019-10-07 10:19:52.765513	t	5365	734	f
8787	2019-10-07 10:03:31.423918	t	5363	734	f
8788	2019-10-07 09:57:22.78203	t	5359	734	f
8789	2019-10-07 08:58:27.255966	t	5364	734	f
8790	2019-09-23 13:49:07.474837	t	5362	734	f
8791	2019-09-23 13:44:36.879879	t	5361	734	f
8792	2019-12-02 12:48:45.273933	t	5375	734	f
8793	2019-12-02 12:40:55.71566	t	5376	734	f
8794	2019-11-17 12:02:02.024652	t	5374	734	f
8795	2019-10-28 13:14:55.67208	t	5370	734	f
8796	2019-10-28 12:51:48.29019	t	5372	734	f
8797	2019-10-28 13:24:22.339789	t	5368	734	f
8798	2019-10-28 13:20:26.813987	t	5367	734	f
8799	2019-10-28 13:03:44.671929	t	5373	734	f
8800	2019-10-28 12:42:53.273338	t	5369	734	f
8801	2019-10-28 10:33:17.309605	t	5371	734	f
8802	2019-10-06 15:05:25.444497	t	5365	735	f
8803	2019-10-04 14:30:35.521145	t	5363	735	f
8804	2019-10-04 14:16:16.023356	t	5360	735	f
8805	2019-10-04 14:11:20.002913	t	5359	735	f
8806	2019-10-04 13:12:49.847904	t	5362	735	f
8807	2019-10-04 14:02:40.816231	t	5366	735	f
8808	2019-10-04 13:19:10.553768	t	5364	735	f
8809	2019-09-25 10:45:54.873147	t	5361	735	f
8810	2019-11-28 17:04:09.775748	t	5375	735	f
8811	\N	f	5376	735	f
8812	\N	f	5374	735	f
8813	2019-10-28 11:51:51.806112	t	5372	735	f
8814	2019-10-28 11:33:08.983138	t	5369	735	f
8815	2019-10-28 11:15:50.400936	t	5371	735	f
8816	\N	f	5373	735	f
8817	\N	f	5370	735	f
8818	\N	f	5368	735	f
8819	\N	f	5367	735	f
8820	\N	f	5365	736	f
8821	\N	f	5363	736	f
8822	\N	f	5360	736	f
8823	2019-09-25 19:18:11.929462	t	5366	736	f
8824	2019-09-25 19:15:14.651661	t	5361	736	f
8825	2019-09-25 19:12:23.790734	t	5359	736	f
8826	2019-09-25 08:25:16.611933	t	5364	736	f
8827	2019-09-21 15:16:14.184467	t	5362	736	f
\.


--
-- Data for Name: quiz_questions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.quiz_questions (id, sequence, question_id, quiz_id) FROM stdin;
40892	0	1557	5361
40893	1	1556	5361
40894	2	1559	5361
40895	3	1558	5361
40896	4	1427	5361
40897	0	1619	5362
40898	1	1549	5362
40899	2	1618	5362
40900	3	1984	5362
40901	4	1612	5362
40902	0	1620	5364
40903	1	1380	5364
40904	2	1642	5364
40905	3	1633	5364
40906	4	1649	5364
40907	0	1332	5366
40908	1	1572	5366
40909	2	1402	5366
40910	3	1395	5366
40911	4	1516	5366
40912	0	1676	5359
40913	1	1675	5359
40914	2	1494	5359
40915	3	1492	5359
40916	4	1495	5359
40917	0	1544	5360
40918	1	1320	5360
40919	2	1940	5360
40920	3	1319	5360
40921	4	1551	5360
40922	0	1550	5363
40923	1	1611	5363
40924	2	1610	5363
40925	3	1477	5363
40926	4	1374	5363
40927	0	1603	5365
40928	1	1641	5365
40929	2	1601	5365
40930	3	1946	5365
40931	4	1868	5365
40932	0	1805	5371
40933	1	1331	5371
40934	2	1798	5371
40935	3	1370	5371
40936	4	1389	5371
40937	0	1801	5369
40938	1	1432	5369
40939	2	1840	5369
40940	3	1951	5369
40941	4	1839	5369
40942	0	1963	5372
40943	1	1944	5372
40944	2	1976	5372
40945	3	1952	5372
40946	4	1962	5372
40947	0	1784	5373
40948	1	1424	5373
40949	2	1856	5373
40950	3	1788	5373
40951	4	1799	5373
40952	0	1800	5370
40953	1	1804	5370
40954	2	1807	5370
40955	3	1810	5370
40956	4	1588	5370
40957	0	1754	5367
40958	1	1412	5367
40959	2	1980	5367
40960	3	1967	5367
40961	4	1978	5367
40962	0	1936	5368
40963	2	1942	5368
40964	4	1584	5368
40965	3	1866	5368
40966	1	1950	5368
40967	0	1960	5374
40968	1	1490	5374
40969	2	1488	5374
40970	3	1968	5374
40971	4	1581	5374
40972	0	1489	5375
40973	3	1506	5375
40974	1	1392	5375
40975	4	1504	5375
40976	2	1507	5375
40977	4	1528	5376
40978	1	1514	5376
40979	2	1520	5376
40980	3	1526	5376
40981	0	1536	5376
\.


--
-- Data for Name: quizzes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.quizzes (id, available_date, conclusion_date, creation_date, scramble, series, title, type, version, course_execution_id, key) FROM stdin;
5370	2019-10-22 10:11:00	\N	\N	f	\N	Designing an architecture	PROPOSED	\N	11	5370
5371	2019-10-09 10:00:00	2020-02-10 10:50:00	\N	f	\N	Availability scenarios	PROPOSED	\N	11	5371
5372	2019-10-15 16:40:00	\N	\N	f	\N	Graphite qualities, scenarios and tactics	PROPOSED	\N	11	5372
5373	2019-10-16 10:00:00	\N	\N	f	\N	Modifiability Scenarios and Tactics	PROPOSED	\N	11	5373
5374	2019-11-01 00:00:00	\N	\N	f	\N	Module Viewtype and its Styles	PROPOSED	\N	11	5374
5375	2019-11-22 00:00:00	\N	\N	f	\N	Component-and-connector viewtype	PROPOSED	\N	11	5375
5376	2019-11-22 00:00:00	\N	\N	f	\N	Allocation viewtype	PROPOSED	\N	11	5376
5359	2019-09-25 10:00:00	\N	\N	f	\N	The importance of software architecture and How to create an architecture	PROPOSED	\N	11	5359
5360	2019-09-30 19:00:00	\N	\N	f	\N	Quality attributes and scenarios	PROPOSED	\N	11	5360
5361	2019-09-20 21:40:00	\N	\N	f	\N	Microservices	PROPOSED	\N	11	5361
5362	2019-09-20 23:31:00	\N	\N	f	\N	What is software architecture? - Concepts	PROPOSED	\N	11	5362
5363	2019-09-30 19:00:00	\N	\N	f	\N	Scalable Web Architecture and Distributed Systems	PROPOSED	\N	11	5363
5364	2019-09-24 11:00:00	\N	\N	f	\N	Uber System - A Software Architecture Perspective	PROPOSED	\N	11	5364
5365	2019-10-02 12:00:00	\N	\N	f	\N	Performance Scenarios and Tactics	PROPOSED	\N	11	5365
5366	2019-09-24 13:23:00	\N	\N	f	\N	What is software architecture? - Definition and Viewtypes	PROPOSED	\N	11	5366
5367	2019-10-22 11:21:00	\N	\N	f	\N	Chrome qualities, scenarios and tactics	PROPOSED	\N	11	5367
5368	2019-10-22 11:42:00	\N	\N	f	\N	Hadoop qualities, scenarios, and tactics	PROPOSED	\N	11	5368
5369	2019-10-09 13:00:00	2020-02-10 14:00:00	\N	f	\N	Availability tactics	PROPOSED	\N	11	5369
\.


--
-- Data for Name: topic_conjunctions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.topic_conjunctions (id, assessment_id) FROM stdin;
938	10
939	10
940	6
941	10
942	10
943	10
944	10
945	10
946	10
947	10
948	10
949	10
950	10
951	6
952	6
953	6
954	6
955	6
956	6
957	6
958	6
959	10
960	6
961	6
962	6
963	6
964	6
965	6
966	6
967	6
968	7
969	7
970	8
971	8
972	8
973	8
974	9
975	9
976	8
977	8
978	9
979	9
980	9
981	9
982	9
983	9
\.


--
-- Data for Name: topics; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.topics (id, name, parent_topic_id, course_id) FROM stdin;
82	Software Architecture	\N	2
83	Architectural Style	\N	2
84	Case Studies	\N	2
85	Web Application	\N	2
86	Enterprise Application Architecture 	\N	2
87	Performance	\N	2
88	Availability	\N	2
89	Modifiability	\N	2
90	Usability	\N	2
91	Security	\N	2
92	Interoperability	\N	2
93	Graphite	\N	2
94	MediaWiki	\N	2
95	Chrome	\N	2
96	GNU Mailman	\N	2
97	Glasgow Haskell Compiler	\N	2
98	Uber	\N	2
99	Viewtypes	\N	2
100	Component-and-connector viewtype	\N	2
101	Module viewtype	\N	2
102	Scenarios	\N	2
103	Allocation viewtype	\N	2
104	Tactics	\N	2
105	Nginx	\N	2
106	Amazon Silk	\N	2
107	Qualities	\N	2
108	Adventure Builder	\N	2
109	Catalog of DVD	\N	2
110	Infinispan	\N	2
111	Twitter	\N	2
112	Continuous integration	\N	2
113	Testability	\N	2
114	Scalable web architecture	\N	2
115	ThousandParsec	\N	2
116	OrderPad	\N	2
117	Hadoop	\N	2
118	Architecture design	\N	2
119	ZeroMQ	\N	2
120	SocialCalc	\N	2
121	GitHub	\N	2
122	Microservices	\N	2
123	Microservices patterns	\N	2
124	Fénix	\N	2
\.


--
-- Data for Name: topics_questions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.topics_questions (topics_id, questions_id) FROM stdin;
82	1549
82	1619
82	1618
82	1984
82	1612
82	1332
82	1572
82	1402
82	1395
82	1516
82	1675
82	1676
82	1494
82	1492
82	1495
82	1560
82	1699
82	1418
82	1721
82	1423
82	1774
82	1771
82	1793
82	1970
82	1377
82	1487
82	1767
82	1763
82	1561
82	1782
82	1546
82	1525
82	1509
82	1510
82	1511
82	1512
82	1563
82	1455
82	1629
82	1634
82	1683
82	1702
82	1700
82	1665
82	1761
82	1485
82	1491
82	1845
82	1897
82	1900
82	1903
82	1930
85	1428
85	1349
85	1569
85	1408
85	1406
85	1325
85	1450
85	1823
85	1920
87	1422
87	1809
87	1384
87	1366
87	1946
87	1601
87	1603
87	1641
87	1868
87	1534
87	1468
87	1692
87	1434
87	1419
87	1838
87	1949
87	1828
87	1797
87	1421
87	1372
87	1947
87	1341
88	1315
88	1981
88	1815
88	1586
88	1319
88	1805
88	1331
88	1798
88	1370
88	1389
88	1801
88	1432
88	1840
88	1951
88	1839
88	1666
88	1972
88	1415
88	1421
88	1372
88	1769
88	1486
88	1965
88	1802
88	1779
88	1343
88	1812
88	1954
88	1816
88	1316
88	1979
88	1969
88	1328
88	1388
89	1974
89	1345
89	1940
89	1784
89	1424
89	1856
89	1799
89	1788
89	1623
89	1396
89	1808
89	1442
89	1871
89	1378
89	1497
89	1381
89	1975
89	1451
90	1669
90	1326
91	1972
91	1707
91	1802
91	1573
91	1597
91	1625
91	1635
91	1704
91	1706
91	1708
91	1731
91	1842
92	1372
92	1353
92	1841
93	1890
93	1367
93	1963
93	1944
93	1952
93	1962
93	1976
93	1399
93	1322
93	1744
93	1632
93	1729
93	1333
93	1735
93	1356
93	1966
93	1988
93	1355
93	1387
93	1906
93	1335
93	1379
93	1783
93	1778
93	1607
93	1939
93	1945
94	1703
94	1740
94	1753
94	1461
94	1684
94	1462
94	1723
94	1746
94	1464
94	1733
95	1347
95	1773
95	1776
95	1762
95	1777
95	1346
95	1411
95	1571
95	1403
95	1790
95	1754
95	1595
95	1412
95	1980
95	1967
95	1956
95	1365
95	1780
95	1765
95	1375
95	1397
95	1585
95	1957
95	1570
95	1832
95	1587
95	1652
95	1577
95	1978
96	1757
96	1824
96	1742
96	1756
96	1465
96	1732
96	1741
96	1743
96	1712
96	1755
96	1758
96	1822
97	1330
97	1653
97	1654
97	1696
97	1694
97	1695
97	1533
97	1644
97	1624
97	1441
98	1620
98	1380
98	1633
98	1642
98	1649
99	1373
99	1651
99	1745
99	1739
99	1420
99	1394
99	1571
99	1577
99	1762
99	1459
99	1411
99	1555
99	1846
100	1579
100	1911
100	1890
100	1340
100	1402
100	1519
100	1632
100	1729
100	1367
100	1906
100	1335
100	1939
100	1945
100	1318
100	1605
100	1982
100	1476
100	1466
100	1392
100	1437
100	1361
100	1391
100	1566
100	1539
100	1538
100	1531
100	1530
100	1363
100	1498
100	1540
100	1517
100	1616
100	1400
100	1500
100	1443
100	1457
100	1859
100	1896
100	1545
100	1547
100	1354
100	1521
100	1348
100	1543
100	1524
100	1535
100	1470
100	1542
100	1775
100	1537
100	1445
100	1452
100	1351
100	1600
100	1507
100	1508
100	1414
100	1501
100	1613
100	1504
100	1582
100	1583
100	1471
100	1575
100	1489
100	1474
100	1527
100	1481
100	1750
100	1454
100	1405
100	1417
100	1576
100	1592
100	1617
100	1564
100	1597
100	1604
100	1625
100	1515
100	1479
100	1325
100	1513
100	1388
100	1591
100	1532
100	1529
100	1467
100	1493
100	1608
100	1631
100	1580
100	1429
100	1386
100	1689
100	1734
100	1506
100	1785
100	1928
100	1938
100	1889
100	1904
100	1913
100	1877
100	1385
100	1872
100	1927
100	1926
100	1915
100	1923
101	1960
101	1552
101	1931
101	1340
101	1519
101	1333
101	1735
101	1355
101	1318
101	1623
101	1581
101	1488
101	1968
101	1490
101	1593
101	1505
101	1598
101	1722
101	1728
101	1713
101	1738
101	1609
101	1327
101	1687
101	1983
101	1986
101	1943
101	1935
101	1803
101	1334
101	1725
101	1912
101	1982
101	1941
101	1358
101	1977
101	1985
101	1948
101	1472
101	1578
101	1955
101	1503
101	1925
101	1602
101	1958
101	1752
101	1650
101	1826
101	1987
101	1831
101	1361
101	1616
101	1400
101	1500
101	1443
101	1457
101	1859
101	1896
101	1545
101	1547
101	1354
101	1348
101	1775
101	1452
101	1351
101	1414
101	1501
101	1750
101	1454
101	1417
101	1617
101	1564
101	1625
101	1479
101	1325
101	1388
101	1608
101	1631
101	1362
101	1429
101	1689
101	1734
101	1785
101	1786
101	1317
101	1910
101	1894
101	1451
101	1458
101	1385
101	1430
101	1872
101	1898
101	1923
102	1480
102	1884
102	1974
102	1809
102	1764
102	1523
102	1384
102	1981
102	1336
102	1815
102	1586
102	1473
102	1367
102	1940
102	1319
102	1603
102	1641
102	1805
102	1331
102	1798
102	1370
102	1389
102	1856
102	1944
102	1962
102	1976
102	1399
102	1906
102	1335
102	1396
102	1808
102	1378
102	1497
102	1381
102	1975
102	1421
102	1954
102	1816
102	1321
102	1376
102	1353
102	1841
102	1938
102	1425
102	1730
102	1368
103	1519
103	1616
103	1400
103	1518
103	1500
103	1443
103	1457
103	1859
103	1896
103	1526
103	1541
103	1520
103	1599
103	1413
103	1496
103	1514
103	1545
103	1354
103	1521
103	1750
103	1454
103	1417
103	1617
103	1564
103	1597
103	1625
103	1479
103	1388
103	1631
103	1362
103	1635
103	1580
103	1594
103	1536
103	1528
103	1606
103	1317
103	1880
103	1458
103	1385
103	1430
104	1315
104	1422
104	1346
104	1403
104	1790
104	1754
104	1384
104	1345
104	1815
104	1586
104	1366
104	1946
104	1601
104	1868
104	1534
104	1468
104	1692
104	1434
104	1419
104	1838
104	1949
104	1828
104	1797
104	1963
104	1801
104	1432
104	1840
104	1951
104	1784
104	1424
104	1799
104	1788
104	1952
104	1976
104	1744
104	1356
104	1966
104	1988
104	1387
104	1783
104	1623
104	1442
104	1871
104	1839
104	1666
104	1972
104	1415
104	1769
104	1707
104	1486
104	1965
104	1802
104	1779
104	1343
104	1812
104	1316
104	1979
104	1969
104	1328
104	1595
104	1412
104	1980
104	1967
104	1365
104	1780
104	1397
104	1585
104	1957
104	1942
104	1866
104	1959
104	1899
104	1772
104	1964
104	1704
104	1706
104	1360
104	1708
104	1376
104	1731
104	1435
104	1326
104	1875
104	1894
104	1444
104	1451
104	1898
104	1947
104	1341
105	1626
105	1329
105	1670
105	1876
105	1908
105	1438
105	1671
105	1693
105	1553
105	1554
105	1630
105	1469
105	1667
105	1440
105	1860
105	1862
105	1863
106	1773
106	1776
106	1570
106	1832
106	1347
106	1587
106	1652
107	1777
107	1544
107	1320
107	1551
107	1426
107	1778
107	1607
107	1550
107	1610
107	1611
107	1477
107	1374
107	1398
107	1668
107	1350
107	1409
107	1672
107	1677
107	1674
107	1404
107	1478
107	1685
107	1383
107	1893
107	1849
107	1379
107	1322
107	1338
107	1956
107	1765
107	1375
107	1950
107	1936
107	1584
107	1453
107	1796
107	1814
107	1973
107	1813
107	1669
107	1463
107	1978
107	1904
107	1883
108	1598
108	1593
108	1505
108	1600
108	1508
108	1613
108	1594
108	1376
108	1928
108	1938
108	1875
108	1889
108	1904
109	1909
109	1357
109	1882
109	1895
109	1888
109	1867
109	1902
109	1449
109	1919
109	1914
109	1337
110	1858
110	1615
110	1768
110	1483
110	1662
110	1865
110	1660
110	1748
110	1759
110	1861
110	1874
111	1857
111	1869
111	1870
111	1885
111	1886
112	1614
112	1781
112	1688
112	1663
112	1661
112	1787
112	1482
112	1747
112	1820
112	1864
113	1372
114	1550
114	1611
114	1610
114	1374
114	1477
114	1338
114	1398
114	1668
114	1677
114	1674
114	1404
114	1849
114	1383
114	1893
114	1409
114	1350
114	1672
114	1478
114	1685
114	1401
114	1407
114	1444
114	1323
114	1439
114	1901
115	1567
115	1589
115	1590
115	1637
115	1829
115	1836
115	1843
115	1854
115	1456
115	1714
115	1717
115	1718
116	1568
116	1627
116	1638
116	1562
116	1892
116	1879
116	1905
116	1922
117	1796
117	1453
117	1950
117	1942
117	1936
117	1866
117	1584
117	1959
117	1814
117	1899
117	1772
117	1964
117	1973
117	1813
117	1745
117	1420
117	1394
117	1651
117	1739
117	1645
117	1463
117	1352
118	1800
118	1807
118	1804
118	1339
118	1416
118	1795
118	1810
118	1588
118	1811
118	1359
118	1961
118	1431
118	1791
118	1342
118	1679
118	1382
118	1933
119	1658
119	1659
119	1682
119	1690
119	1655
119	1681
119	1656
119	1657
119	1680
119	1691
120	1574
120	1565
120	1636
120	1639
120	1709
120	1436
120	1716
120	1499
120	1855
120	1921
120	1929
120	1932
120	1934
120	1806
120	1817
120	1818
120	1819
120	1847
120	1848
120	1850
120	1853
121	1719
121	1724
121	1821
121	1844
121	1837
121	1460
121	1720
122	1557
122	1556
122	1559
122	1558
122	1427
122	1373
122	1369
122	1390
122	1971
122	1393
122	1596
122	1433
122	1446
122	1318
122	1617
122	1792
123	1794
123	1887
123	1917
123	1916
123	1918
123	1924
123	1907
123	1937
123	1410
123	1371
123	1364
123	1873
124	1447
124	1324
124	1825
124	1475
124	1726
\.


--
-- Data for Name: topics_topic_conjunctions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.topics_topic_conjunctions (topics_id, topic_conjunctions_id) FROM stdin;
82	938
85	974
87	945
87	949
87	950
88	955
88	956
88	960
89	940
89	957
89	958
93	951
93	952
93	953
93	954
93	981
93	982
95	962
95	963
95	964
95	975
95	979
98	939
99	979
99	980
100	970
100	972
100	973
100	982
101	968
101	969
101	977
101	981
102	940
102	943
102	947
102	949
102	952
102	954
102	955
102	957
102	960
102	963
102	966
103	971
103	972
103	976
104	940
104	944
104	946
104	950
104	953
104	954
104	956
104	958
104	960
104	964
104	967
106	975
107	942
107	948
107	951
107	962
107	965
108	969
108	973
108	976
108	977
109	978
114	941
114	946
114	947
114	948
117	965
117	966
117	967
117	980
118	961
122	959
123	983
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (id, creation_date, name, role, username, number_of_correct_teacher_answers, number_of_student_quizzes, number_of_teacher_answers, number_of_teacher_quizzes, enrolled_courses_acronyms, last_access, key) FROM stdin;
651	2019-09-23 19:14:32.817793	Student 651	STUDENT	\N	64	170	90	18	\N	\N	651
646	2019-09-23 13:23:58.974372	Student 646	STUDENT	\N	36	24	80	16	\N	\N	646
647	2019-09-23 13:31:50.148348	Student 647	STUDENT	\N	54	129	90	18	\N	\N	647
655	2019-09-25 10:27:06.196577	Student 655	STUDENT	\N	26	75	80	16	\N	\N	655
627	2019-09-21 14:50:53.688586	Student 627	STUDENT	\N	19	42	40	8	\N	\N	627
678	2019-09-25 05:32:58.216829	Student 678	STUDENT	\N	75	109	90	18	\N	\N	678
679	2019-09-22 09:41:16.599026	Student 679	STUDENT	\N	72	49	90	18	\N	\N	679
680	2019-09-21 22:11:48.82987	Student 680	STUDENT	\N	64	315	90	18	\N	\N	680
681	2019-10-06 17:41:17.553298	Student 681	STUDENT	\N	47	26	90	18	\N	\N	681
682	2019-09-23 14:39:24.825842	Student 682	STUDENT	\N	38	169	90	18	\N	\N	682
683	2019-09-23 16:59:36.94096	Student 683	STUDENT	\N	47	35	115	23	\N	\N	683
684	2019-09-22 22:14:04.075136	Student 684	STUDENT	\N	75	278	90	18	\N	\N	684
685	2020-01-04 12:43:04.332472	Student 685	STUDENT	\N	36	3	180	36	\N	\N	685
686	2019-09-30 08:44:39.097965	Student 686	STUDENT	\N	56	36	90	18	\N	\N	686
687	2019-09-29 20:15:41.602968	Student 687	STUDENT	\N	26	32	80	16	\N	\N	687
688	2019-09-30 14:35:40.358352	Student 688	STUDENT	\N	51	9	90	18	\N	\N	688
689	2019-10-03 21:48:19.067191	Student 689	STUDENT	\N	32	36	75	15	\N	\N	689
690	2019-09-22 16:21:59.174165	Student 690	STUDENT	\N	58	90	90	18	\N	\N	690
691	2019-10-06 17:04:08.109615	Student 691	STUDENT	\N	6	0	40	8	\N	\N	691
722	2019-09-26 10:12:51.715464	Student 722	STUDENT	\N	60	7	95	19	\N	\N	722
723	2019-09-21 08:29:40.345022	Student 723	STUDENT	\N	55	184	90	18	\N	\N	723
724	2019-09-22 01:17:44.361142	Student 724	STUDENT	\N	66	157	90	18	\N	\N	724
725	2019-10-06 11:19:43.830592	Student 725	STUDENT	\N	72	28	90	18	\N	\N	725
726	2019-09-22 22:13:28.857203	Student 726	STUDENT	\N	56	108	90	18	\N	\N	726
727	2019-09-30 16:09:20.676456	Student 727	STUDENT	\N	33	34	45	9	\N	\N	727
728	2019-09-23 07:23:54.866436	Student 728	STUDENT	\N	47	36	90	18	\N	\N	728
729	2019-10-04 14:28:04.163379	Student 729	STUDENT	\N	43	181	90	18	\N	\N	729
730	2019-09-24 16:17:21.976357	Student 730	STUDENT	\N	61	103	90	18	\N	\N	730
731	2019-09-21 11:11:51.01544	Student 731	STUDENT	\N	48	69	100	20	\N	\N	731
732	2019-09-23 19:14:32.817793	Student 732	STUDENT	\N	64	170	90	18	\N	\N	732
733	2019-09-23 13:23:58.974372	Student 733	STUDENT	\N	36	24	80	16	\N	\N	733
734	2019-09-23 13:31:50.148348	Student 734	STUDENT	\N	54	129	90	18	\N	\N	734
735	2019-09-25 10:27:06.196577	Student 735	STUDENT	\N	26	75	80	16	\N	\N	735
736	2019-09-21 14:50:53.688586	Student 736	STUDENT	\N	19	42	40	8	\N	\N	736
673	2019-11-12 15:30:10.760554	Student 673	TEACHER	\N	\N	\N	\N	\N	\N	\N	673
653	2019-09-25 05:32:58.216829	Student 653	STUDENT	\N	75	109	90	18	\N	\N	653
635	2019-09-22 09:41:16.599026	Student 635	STUDENT	\N	72	49	90	18	\N	\N	635
633	2019-09-21 22:11:48.82987	Student 633	STUDENT	\N	64	315	90	18	\N	\N	633
669	2019-10-06 17:41:17.553298	Student 669	STUDENT	\N	47	26	90	18	\N	\N	669
648	2019-09-23 14:39:24.825842	Student 648	STUDENT	\N	38	169	90	18	\N	\N	648
650	2019-09-23 16:59:36.94096	Student 650	STUDENT	\N	47	35	115	23	\N	\N	650
643	2019-09-22 22:14:04.075136	Student 643	STUDENT	\N	75	278	90	18	\N	\N	643
674	2020-01-04 12:43:04.332472	Student 674	STUDENT	\N	36	3	180	36	\N	\N	674
661	2019-09-30 08:44:39.097965	Student 661	STUDENT	\N	56	36	90	18	\N	\N	661
660	2019-09-29 20:15:41.602968	Student 660	STUDENT	\N	26	32	80	16	\N	\N	660
662	2019-09-30 14:35:40.358352	Student 662	STUDENT	\N	51	9	90	18	\N	\N	662
665	2019-10-03 21:48:19.067191	Student 665	STUDENT	\N	32	36	75	15	\N	\N	665
638	2019-09-22 16:21:59.174165	Student 638	STUDENT	\N	58	90	90	18	\N	\N	638
676	\N	Demo Student	STUDENT	Demo-Student	\N	\N	\N	\N	\N	\N	676
677	\N	Demo Teacher	TEACHER	Demo-Teacher	\N	\N	\N	\N	\N	\N	677
670	2019-10-07 09:46:47.415342	Student 670	STUDENT	\N	31	304	90	18	\N	\N	670
639	2019-09-22 17:28:41.14569	Student 639	STUDENT	\N	48	23	95	19	\N	\N	639
654	2019-09-25 07:20:39.933845	Student 654	STUDENT	\N	61	273	90	18	\N	\N	654
645	2019-09-23 08:00:43.037163	Student 645	STUDENT	\N	56	24	90	18	\N	\N	645
641	2019-09-22 20:56:41.078959	Student 641	STUDENT	\N	22	14	80	16	\N	\N	641
630	2019-09-21 16:07:42.935355	Student 630	STUDENT	\N	56	76	95	19	\N	\N	630
631	2019-09-21 18:24:01.766164	Student 631	STUDENT	\N	64	104	90	18	\N	\N	631
640	2019-09-22 20:46:30.39225	Student 640	STUDENT	\N	53	174	120	24	\N	\N	640
649	2019-09-23 16:12:39.68395	Student 649	STUDENT	\N	65	109	90	18	\N	\N	649
617	2019-09-21 08:19:24.654038	Student 617	STUDENT	\N	54	127	90	18	\N	\N	617
632	2019-09-21 20:21:51.288025	Student 632	STUDENT	\N	64	14	90	18	\N	\N	632
671	2019-10-07 12:07:38.944949	Student 671	STUDENT	\N	53	108	90	18	\N	\N	671
621	2019-09-21 10:20:43.60709	Student 621	STUDENT	\N	75	116	90	18	\N	\N	621
668	2019-10-06 17:04:08.109615	Student 668	STUDENT	\N	6	0	40	8	\N	\N	668
636	2019-09-22 12:25:23.916187	Student 636	STUDENT	\N	0	0	10	2	\N	\N	636
623	2019-09-21 11:53:59.699951	Student 623	STUDENT	\N	68	87	90	18	\N	\N	623
675	2020-01-15 21:24:17.293784	Student 675	STUDENT	\N	47	4	175	35	\N	\N	675
672	2019-10-07 14:47:26.759458	Student 672	STUDENT	\N	52	6	90	18	\N	\N	672
624	2019-09-21 13:10:10.167261	Student 624	STUDENT	\N	66	67	90	18	\N	\N	624
625	2019-09-21 13:24:31.619557	Student 625	STUDENT	\N	72	41	90	18	\N	\N	625
664	2019-09-30 16:34:12.493132	Student 664	STUDENT	\N	48	7	75	15	\N	\N	664
637	2019-09-22 12:59:07.436456	Student 637	STUDENT	\N	58	34	90	18	\N	\N	637
628	2019-09-21 15:07:10.148232	Student 628	STUDENT	\N	32	0	75	15	\N	\N	628
629	2019-09-21 15:26:21.272462	Student 629	STUDENT	\N	66	41	90	18	\N	\N	629
658	2019-09-26 21:28:23.051785	Student 658	STUDENT	\N	5	0	85	17	\N	\N	658
656	2019-09-25 19:48:46.645523	Student 656	STUDENT	\N	27	101	75	15	\N	\N	656
616	2019-09-21 01:44:49.570852	Student 616	STUDENT	\N	61	80	90	18	\N	\N	616
619	2019-09-21 08:38:38.898789	Student 619	STUDENT	\N	73	222	90	18	\N	\N	619
620	2019-09-21 09:14:35.176312	Student 620	STUDENT	\N	12	6	75	15	\N	\N	620
626	2019-09-21 13:40:59.743495	Student 626	STUDENT	\N	65	121	90	18	\N	\N	626
659	2019-09-28 22:07:37.570365	Student 659	STUDENT	\N	59	21	90	18	\N	\N	659
657	2019-09-26 10:12:51.715464	Student 657	STUDENT	\N	60	7	95	19	\N	\N	657
618	2019-09-21 08:29:40.345022	Student 618	STUDENT	\N	55	184	90	18	\N	\N	618
634	2019-09-22 01:17:44.361142	Student 634	STUDENT	\N	66	157	90	18	\N	\N	634
667	2019-10-06 11:19:43.830592	Student 667	STUDENT	\N	72	28	90	18	\N	\N	667
642	2019-09-22 22:13:28.857203	Student 642	STUDENT	\N	56	108	90	18	\N	\N	642
663	2019-09-30 16:09:20.676456	Student 663	STUDENT	\N	33	34	45	9	\N	\N	663
644	2019-09-23 07:23:54.866436	Student 644	STUDENT	\N	47	36	90	18	\N	\N	644
666	2019-10-04 14:28:04.163379	Student 666	STUDENT	\N	43	181	90	18	\N	\N	666
652	2019-09-24 16:17:21.976357	Student 652	STUDENT	\N	61	103	90	18	\N	\N	652
622	2019-09-21 11:11:51.01544	Student 622	STUDENT	\N	48	69	100	20	\N	\N	622
692	2019-09-22 12:25:23.916187	Student 692	STUDENT	\N	0	0	10	2	\N	\N	692
693	2019-09-21 11:53:59.699951	Student 693	STUDENT	\N	68	87	90	18	\N	\N	693
694	2020-01-15 21:24:17.293784	Student 694	STUDENT	\N	47	4	175	35	\N	\N	694
695	2019-10-07 14:47:26.759458	Student 695	STUDENT	\N	52	6	90	18	\N	\N	695
696	2019-09-21 13:10:10.167261	Student 696	STUDENT	\N	66	67	90	18	\N	\N	696
697	2019-09-21 13:24:31.619557	Student 697	STUDENT	\N	72	41	90	18	\N	\N	697
698	2019-09-30 16:34:12.493132	Student 698	STUDENT	\N	48	7	75	15	\N	\N	698
699	2019-09-22 12:59:07.436456	Student 699	STUDENT	\N	58	34	90	18	\N	\N	699
700	2019-09-21 15:07:10.148232	Student 700	STUDENT	\N	32	0	75	15	\N	\N	700
701	2019-09-21 15:26:21.272462	Student 701	STUDENT	\N	66	41	90	18	\N	\N	701
702	2019-09-26 21:28:23.051785	Student 702	STUDENT	\N	5	0	85	17	\N	\N	702
703	2019-09-25 19:48:46.645523	Student 703	STUDENT	\N	27	101	75	15	\N	\N	703
704	2019-09-21 01:44:49.570852	Student 704	STUDENT	\N	61	80	90	18	\N	\N	704
705	2019-09-21 08:38:38.898789	Student 705	STUDENT	\N	73	222	90	18	\N	\N	705
706	2019-09-21 09:14:35.176312	Student 706	STUDENT	\N	12	6	75	15	\N	\N	706
707	2019-09-21 13:40:59.743495	Student 707	STUDENT	\N	65	121	90	18	\N	\N	707
708	2019-10-07 09:46:47.415342	Student 708	STUDENT	\N	31	304	90	18	\N	\N	708
709	2019-09-22 17:28:41.14569	Student 709	STUDENT	\N	48	23	95	19	\N	\N	709
710	2019-09-25 07:20:39.933845	Student 710	STUDENT	\N	61	273	90	18	\N	\N	710
711	2019-09-23 08:00:43.037163	Student 711	STUDENT	\N	56	24	90	18	\N	\N	711
712	2019-09-22 20:56:41.078959	Student 712	STUDENT	\N	22	14	80	16	\N	\N	712
713	2019-09-21 16:07:42.935355	Student 713	STUDENT	\N	56	76	95	19	\N	\N	713
714	2019-09-21 18:24:01.766164	Student 714	STUDENT	\N	64	104	90	18	\N	\N	714
715	2019-09-22 20:46:30.39225	Student 715	STUDENT	\N	53	174	120	24	\N	\N	715
716	2019-09-23 16:12:39.68395	Student 716	STUDENT	\N	65	109	90	18	\N	\N	716
717	2019-09-21 08:19:24.654038	Student 717	STUDENT	\N	54	127	90	18	\N	\N	717
718	2019-09-21 20:21:51.288025	Student 718	STUDENT	\N	64	14	90	18	\N	\N	718
719	2019-10-07 12:07:38.944949	Student 719	STUDENT	\N	53	108	90	18	\N	\N	719
720	2019-09-21 10:20:43.60709	Student 720	STUDENT	\N	75	116	90	18	\N	\N	720
721	2019-09-28 22:07:37.570365	Student 721	STUDENT	\N	59	21	90	18	\N	\N	721
737	\N	Demo-Admin	DEMO_ADMIN	Demo-Admin	\N	\N	\N	\N	\N	\N	\N
\.


--
-- Data for Name: users_course_executions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users_course_executions (users_id, course_executions_id) FROM stdin;
676	11
677	11
678	11
679	11
680	11
681	11
682	11
683	11
684	11
685	11
686	11
687	11
688	11
689	11
690	11
691	11
692	11
693	11
694	11
695	11
696	11
697	11
698	11
699	11
700	11
701	11
702	11
703	11
704	11
705	11
706	11
707	11
708	11
709	11
710	11
711	11
712	11
713	11
714	11
715	11
716	11
717	11
718	11
719	11
720	11
721	11
722	11
723	11
724	11
725	11
726	11
727	11
728	11
729	11
730	11
731	11
732	11
733	11
734	11
735	11
736	11
\.


--
-- Name: assessments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.assessments_id_seq', 10, true);


--
-- Name: course_executions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.course_executions_id_seq', 12, true);


--
-- Name: courses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.courses_id_seq', 3, true);


--
-- Name: images_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.images_id_seq', 215, true);


--
-- Name: options_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.options_id_seq', 7952, true);


--
-- Name: question_answers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.question_answers_id_seq', 66472, true);


--
-- Name: questions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.questions_id_seq', 1988, true);


--
-- Name: quiz_answers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.quiz_answers_id_seq', 8827, true);


--
-- Name: quiz_questions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.quiz_questions_id_seq', 40981, true);


--
-- Name: quizzes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.quizzes_id_seq', 5376, true);


--
-- Name: topic_conjunctions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.topic_conjunctions_id_seq', 983, true);


--
-- Name: topics_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.topics_id_seq', 124, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.users_id_seq', 737, true);


--
-- Name: assessments assessments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.assessments
    ADD CONSTRAINT assessments_pkey PRIMARY KEY (id);


--
-- Name: course_executions course_executions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course_executions
    ADD CONSTRAINT course_executions_pkey PRIMARY KEY (id);


--
-- Name: courses courses_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.courses
    ADD CONSTRAINT courses_pkey PRIMARY KEY (id);


--
-- Name: images images_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_pkey PRIMARY KEY (id);


--
-- Name: options options_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.options
    ADD CONSTRAINT options_pkey PRIMARY KEY (id);


--
-- Name: question_answers question_answers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_answers
    ADD CONSTRAINT question_answers_pkey PRIMARY KEY (id);


--
-- Name: questions questions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.questions
    ADD CONSTRAINT questions_pkey PRIMARY KEY (id);


--
-- Name: quiz_answers quiz_answers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quiz_answers
    ADD CONSTRAINT quiz_answers_pkey PRIMARY KEY (id);


--
-- Name: quiz_questions quiz_questions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quiz_questions
    ADD CONSTRAINT quiz_questions_pkey PRIMARY KEY (id);


--
-- Name: quizzes quizzes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quizzes
    ADD CONSTRAINT quizzes_pkey PRIMARY KEY (id);


--
-- Name: topic_conjunctions topic_conjunctions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topic_conjunctions
    ADD CONSTRAINT topic_conjunctions_pkey PRIMARY KEY (id);


--
-- Name: topics topics_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topics
    ADD CONSTRAINT topics_pkey PRIMARY KEY (id);


--
-- Name: topics_questions topics_questions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topics_questions
    ADD CONSTRAINT topics_questions_pkey PRIMARY KEY (topics_id, questions_id);


--
-- Name: users_course_executions users_course_executions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users_course_executions
    ADD CONSTRAINT users_course_executions_pkey PRIMARY KEY (users_id, course_executions_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: topic_conjunctions fk1bei0hi20ym6b018f9yr59gya; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topic_conjunctions
    ADD CONSTRAINT fk1bei0hi20ym6b018f9yr59gya FOREIGN KEY (assessment_id) REFERENCES public.assessments(id);


--
-- Name: topics_topic_conjunctions fk2p0k3pivqm6btx62cmftv3ktq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topics_topic_conjunctions
    ADD CONSTRAINT fk2p0k3pivqm6btx62cmftv3ktq FOREIGN KEY (topics_id) REFERENCES public.topics(id);


--
-- Name: images fk37qs4lwbc8u9pmjhtuxp0d264; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT fk37qs4lwbc8u9pmjhtuxp0d264 FOREIGN KEY (question_id) REFERENCES public.questions(id);


--
-- Name: users_course_executions fk3nlrx7690x6hfu93fi69gskqg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users_course_executions
    ADD CONSTRAINT fk3nlrx7690x6hfu93fi69gskqg FOREIGN KEY (course_executions_id) REFERENCES public.course_executions(id);


--
-- Name: topics_questions fk4lsm9y4c8dmvp5b2g5no87om7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topics_questions
    ADD CONSTRAINT fk4lsm9y4c8dmvp5b2g5no87om7 FOREIGN KEY (questions_id) REFERENCES public.questions(id);


--
-- Name: options fk5bmv46so2y5igt9o9n9w4fh6y; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.options
    ADD CONSTRAINT fk5bmv46so2y5igt9o9n9w4fh6y FOREIGN KEY (question_id) REFERENCES public.questions(id);


--
-- Name: questions fk6y78pbrt5dxp12ljsa8r1ey82; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.questions
    ADD CONSTRAINT fk6y78pbrt5dxp12ljsa8r1ey82 FOREIGN KEY (course_id) REFERENCES public.courses(id);


--
-- Name: quiz_answers fk8m696ke2wtle7jrw9fp3ey2yt; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quiz_answers
    ADD CONSTRAINT fk8m696ke2wtle7jrw9fp3ey2yt FOREIGN KEY (quiz_id) REFERENCES public.quizzes(id);


--
-- Name: assessments fk9l06agsr3c75fewmve7t2tc43; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.assessments
    ADD CONSTRAINT fk9l06agsr3c75fewmve7t2tc43 FOREIGN KEY (course_execution_id) REFERENCES public.course_executions(id);


--
-- Name: question_answers fkamgy9t1s8asrsl00l5p8lold4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_answers
    ADD CONSTRAINT fkamgy9t1s8asrsl00l5p8lold4 FOREIGN KEY (quiz_answer_id) REFERENCES public.quiz_answers(id);


--
-- Name: quiz_questions fkanfmgf6ksbdnv7ojb0pfve54q; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quiz_questions
    ADD CONSTRAINT fkanfmgf6ksbdnv7ojb0pfve54q FOREIGN KEY (quiz_id) REFERENCES public.quizzes(id);


--
-- Name: users_course_executions fkbio7lec4k721ccqaoqecbr9ko; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users_course_executions
    ADD CONSTRAINT fkbio7lec4k721ccqaoqecbr9ko FOREIGN KEY (users_id) REFERENCES public.users(id);


--
-- Name: quiz_answers fkcqyj36ibcadfva2rc6psle7u1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quiz_answers
    ADD CONSTRAINT fkcqyj36ibcadfva2rc6psle7u1 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: question_answers fkdg6xjp7x8wn0pd8vctlqll5ok; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_answers
    ADD CONSTRAINT fkdg6xjp7x8wn0pd8vctlqll5ok FOREIGN KEY (option_id) REFERENCES public.options(id);


--
-- Name: quiz_questions fkev41c723fx659v28pjycox15o; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quiz_questions
    ADD CONSTRAINT fkev41c723fx659v28pjycox15o FOREIGN KEY (question_id) REFERENCES public.questions(id);


--
-- Name: topics_topic_conjunctions fkh080rtpwj6cuvoi3wq4avmmw0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topics_topic_conjunctions
    ADD CONSTRAINT fkh080rtpwj6cuvoi3wq4avmmw0 FOREIGN KEY (topic_conjunctions_id) REFERENCES public.topic_conjunctions(id);


--
-- Name: topics fkhn8u5k2hlwgftn6xkk7i2vh1o; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topics
    ADD CONSTRAINT fkhn8u5k2hlwgftn6xkk7i2vh1o FOREIGN KEY (course_id) REFERENCES public.courses(id);


--
-- Name: course_executions fkimcvi45c7kssp8vygn0diid7u; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course_executions
    ADD CONSTRAINT fkimcvi45c7kssp8vygn0diid7u FOREIGN KEY (course_id) REFERENCES public.courses(id);


--
-- Name: question_answers fkjy80p5uktr31ehg8d5v53nfq2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question_answers
    ADD CONSTRAINT fkjy80p5uktr31ehg8d5v53nfq2 FOREIGN KEY (quiz_question_id) REFERENCES public.quiz_questions(id);


--
-- Name: topics fkpd5mo30erv4abuyhk94iatmcx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topics
    ADD CONSTRAINT fkpd5mo30erv4abuyhk94iatmcx FOREIGN KEY (parent_topic_id) REFERENCES public.topics(id);


--
-- Name: topics_questions fkpv4eapx6jhsr3fnqygajogkyc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topics_questions
    ADD CONSTRAINT fkpv4eapx6jhsr3fnqygajogkyc FOREIGN KEY (topics_id) REFERENCES public.topics(id);


--
-- Name: quizzes fktjubpqjuw7b2yw4jp7kwstp3l; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.quizzes
    ADD CONSTRAINT fktjubpqjuw7b2yw4jp7kwstp3l FOREIGN KEY (course_execution_id) REFERENCES public.course_executions(id);


--
-- PostgreSQL database dump complete
--

