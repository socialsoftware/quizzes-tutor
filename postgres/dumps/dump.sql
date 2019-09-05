--
-- PostgreSQL database dump
--

-- Dumped from database version 10.10 (Ubuntu 10.10-0ubuntu0.18.04.1)
-- Dumped by pg_dump version 10.10 (Ubuntu 10.10-0ubuntu0.18.04.1)

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
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: images; Type: TABLE; Schema: public; Owner: pedro
--

CREATE TABLE public.images (
    id integer NOT NULL,
    url character varying(255),
    width integer,
    question_id integer
);


ALTER TABLE public.images OWNER TO pedro;

--
-- Name: images_id_seq; Type: SEQUENCE; Schema: public; Owner: pedro
--

CREATE SEQUENCE public.images_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.images_id_seq OWNER TO pedro;

--
-- Name: images_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pedro
--

ALTER SEQUENCE public.images_id_seq OWNED BY public.images.id;


--
-- Name: options; Type: TABLE; Schema: public; Owner: pedro
--

CREATE TABLE public.options (
    id integer NOT NULL,
    content text,
    correct boolean DEFAULT false,
    number integer,
    question_id integer
);


ALTER TABLE public.options OWNER TO pedro;

--
-- Name: options_id_seq; Type: SEQUENCE; Schema: public; Owner: pedro
--

CREATE SEQUENCE public.options_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.options_id_seq OWNER TO pedro;

--
-- Name: options_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pedro
--

ALTER SEQUENCE public.options_id_seq OWNED BY public.options.id;


--
-- Name: question_answers; Type: TABLE; Schema: public; Owner: pedro
--

CREATE TABLE public.question_answers (
    id integer NOT NULL,
    time_taken timestamp without time zone,
    option_id integer,
    quiz_answer_id integer,
    quiz_question_id integer
);


ALTER TABLE public.question_answers OWNER TO pedro;

--
-- Name: question_answers_id_seq; Type: SEQUENCE; Schema: public; Owner: pedro
--

CREATE SEQUENCE public.question_answers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.question_answers_id_seq OWNER TO pedro;

--
-- Name: question_answers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pedro
--

ALTER SEQUENCE public.question_answers_id_seq OWNED BY public.question_answers.id;


--
-- Name: questions; Type: TABLE; Schema: public; Owner: pedro
--

CREATE TABLE public.questions (
    id integer NOT NULL,
    active boolean DEFAULT true,
    content text,
    number integer,
    number_of_answers integer DEFAULT 0,
    number_of_correct integer DEFAULT 0,
    title character varying(255)
);


ALTER TABLE public.questions OWNER TO pedro;

--
-- Name: questions_id_seq; Type: SEQUENCE; Schema: public; Owner: pedro
--

CREATE SEQUENCE public.questions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.questions_id_seq OWNER TO pedro;

--
-- Name: questions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pedro
--

ALTER SEQUENCE public.questions_id_seq OWNED BY public.questions.id;


--
-- Name: quiz_answers; Type: TABLE; Schema: public; Owner: pedro
--

CREATE TABLE public.quiz_answers (
    id integer NOT NULL,
    answer_date timestamp without time zone,
    assigned_date timestamp without time zone,
    completed boolean,
    quiz_id integer,
    user_id integer
);


ALTER TABLE public.quiz_answers OWNER TO pedro;

--
-- Name: quiz_answers_id_seq; Type: SEQUENCE; Schema: public; Owner: pedro
--

CREATE SEQUENCE public.quiz_answers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.quiz_answers_id_seq OWNER TO pedro;

--
-- Name: quiz_answers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pedro
--

ALTER SEQUENCE public.quiz_answers_id_seq OWNED BY public.quiz_answers.id;


--
-- Name: quiz_questions; Type: TABLE; Schema: public; Owner: pedro
--

CREATE TABLE public.quiz_questions (
    id integer NOT NULL,
    sequence integer,
    question_id integer,
    quiz_id integer
);


ALTER TABLE public.quiz_questions OWNER TO pedro;

--
-- Name: quiz_questions_id_seq; Type: SEQUENCE; Schema: public; Owner: pedro
--

CREATE SEQUENCE public.quiz_questions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.quiz_questions_id_seq OWNER TO pedro;

--
-- Name: quiz_questions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pedro
--

ALTER SEQUENCE public.quiz_questions_id_seq OWNED BY public.quiz_questions.id;


--
-- Name: quizzes; Type: TABLE; Schema: public; Owner: pedro
--

CREATE TABLE public.quizzes (
    id integer NOT NULL,
    available_date timestamp without time zone,
    generation_date timestamp without time zone,
    number integer,
    series integer,
    title character varying(255),
    type character varying(255),
    version character varying(255),
    year integer
);


ALTER TABLE public.quizzes OWNER TO pedro;

--
-- Name: quizzes_id_seq; Type: SEQUENCE; Schema: public; Owner: pedro
--

CREATE SEQUENCE public.quizzes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.quizzes_id_seq OWNER TO pedro;

--
-- Name: quizzes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pedro
--

ALTER SEQUENCE public.quizzes_id_seq OWNED BY public.quizzes.id;


--
-- Name: topics; Type: TABLE; Schema: public; Owner: pedro
--

CREATE TABLE public.topics (
    id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.topics OWNER TO pedro;

--
-- Name: topics_id_seq; Type: SEQUENCE; Schema: public; Owner: pedro
--

CREATE SEQUENCE public.topics_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.topics_id_seq OWNER TO pedro;

--
-- Name: topics_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pedro
--

ALTER SEQUENCE public.topics_id_seq OWNED BY public.topics.id;


--
-- Name: topics_questions; Type: TABLE; Schema: public; Owner: pedro
--

CREATE TABLE public.topics_questions (
    topics_id integer NOT NULL,
    questions_id integer NOT NULL
);


ALTER TABLE public.topics_questions OWNER TO pedro;

--
-- Name: users; Type: TABLE; Schema: public; Owner: pedro
--

CREATE TABLE public.users (
    id integer NOT NULL,
    name character varying(255),
    number integer,
    role character varying(255),
    username character varying(255),
    year integer
);


ALTER TABLE public.users OWNER TO pedro;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: pedro
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO pedro;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pedro
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: images id; Type: DEFAULT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.images ALTER COLUMN id SET DEFAULT nextval('public.images_id_seq'::regclass);


--
-- Name: options id; Type: DEFAULT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.options ALTER COLUMN id SET DEFAULT nextval('public.options_id_seq'::regclass);


--
-- Name: question_answers id; Type: DEFAULT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.question_answers ALTER COLUMN id SET DEFAULT nextval('public.question_answers_id_seq'::regclass);


--
-- Name: questions id; Type: DEFAULT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.questions ALTER COLUMN id SET DEFAULT nextval('public.questions_id_seq'::regclass);


--
-- Name: quiz_answers id; Type: DEFAULT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.quiz_answers ALTER COLUMN id SET DEFAULT nextval('public.quiz_answers_id_seq'::regclass);


--
-- Name: quiz_questions id; Type: DEFAULT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.quiz_questions ALTER COLUMN id SET DEFAULT nextval('public.quiz_questions_id_seq'::regclass);


--
-- Name: quizzes id; Type: DEFAULT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.quizzes ALTER COLUMN id SET DEFAULT nextval('public.quizzes_id_seq'::regclass);


--
-- Name: topics id; Type: DEFAULT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.topics ALTER COLUMN id SET DEFAULT nextval('public.topics_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: images; Type: TABLE DATA; Schema: public; Owner: pedro
--

COPY public.images (id, url, width, question_id) FROM stdin;
2	214.png	100	214
5	293.png	120	293
6	299.png	70	299
7	383.png	100	383
8	388.png	120	388
9	395.png	100	395
10	398.png	100	398
11	402.png	80	402
12	404.png	80	404
13	407.png	100	407
14	409.png	80	409
15	412.png	80	412
16	414.png	80	414
17	417.png	100	417
18	419.png	80	419
24	454.png	100	454
25	462.png	150	462
26	470.png	100	470
27	476.png	80	476
28	480.png	80	480
32	517.png	140	517
33	525.png	120	525
34	537.png	100	537
36	550.png	100	550
38	558.png	100	558
40	566.png	100	566
42	626.png	100	626
45	636.png	100	636
46	639.png	80	639
50	650.png	80	650
53	657.png	80	657
58	705.png	120	705
59	712.png	140	712
60	713.png	70	713
65	778.png	140	778
66	779.png	80	779
69	786.png	100	786
70	790.png	120	790
72	800.png	130	800
78	846.png	120	846
80	856.png	140	856
84	884.png	120	884
85	894.png	100	894
86	895.png	120	895
93	921.png	120	921
94	929.png	120	929
95	931.png	60	931
96	934.png	120	934
97	936.png	120	936
98	940.png	120	940
101	949.png	100	949
102	950.png	100	950
105	956.png	100	956
106	959.png	100	959
109	966.png	100	966
110	967.png	100	967
113	1006.png	95	1006
114	1010.png	95	1010
117	1016.png	95	1016
121	1026.png	95	1026
122	1028.png	95	1028
125	1036.png	120	1036
126	1039.png	100	1039
129	1046.png	120	1046
130	1050.png	120	1050
133	1058.png	60	1058
134	1059.png	120	1059
\.


--
-- Data for Name: options; Type: TABLE DATA; Schema: public; Owner: pedro
--

COPY public.options (id, content, correct, number, question_id) FROM stdin;
5	Influence the software development process and its management, but not the software architecture of the system under development	f	0	2
7	Are important to determine the feasibility of the system, but once we reach the conclusion that the system can be developed with these restrictions, software architecture no longer depends on these factors	f	2	2
8	Are not one of the influences of the software architecture in the Architecture Business Cycle	f	3	2
10	The *implementation* style, which allows us to know where are the artifacts that implement a certain module	f	1	3
11	The *layers* style, which allows us to show that the structure of our system is composed of various modules that may be easily reused in other systems	f	2	3
12	The *client-server* and *deployment* styles, which allow us to isolate the required functionality in a component that executes autonomously and, thus, reusable in other systems	f	3	3
13	To keep the current architecture of the system and optimize the code to achieve the currently required performance levels	f	0	4
14	To change the Deployment view, replicating the server component by more machines	f	1	4
15	To review the system's architecture so that part of the computation that is currently done at the server shifts to the clients	f	2	4
17	Those three viewtypes complement each other, but they are completely independent, showing different aspects that have no relation among them	f	0	5
18	The module and component-and-connector viewtypes are independent of one another, but the allocation viewtype depends on the first two	f	1	5
19	Each viewtype uses different software elements, such as modules or components, so it does not make sense to talk about relationships among these viewtypes	f	2	5
29	A subset of the requirements that do not have conflicts among them and that correspond to the most important business goals	f	0	8
30	A subset of the requirements that have many conflicts among them and for which you need to find tradeoffs early in the design process	f	1	8
32	A subset of the requirements that is chosen exclusively by the architect by taking into account their influence on the system's architecture	f	3	8
34	The *Shared data* style	f	1	9
35	The *Client-Server* style	f	2	9
36	The *Publish-subscribe* style	f	3	9
37	The *Communicating Processes* style	f	0	10
38	The *Pipes-and-filters* style	f	1	10
39	The *Peer-to-Peer* style	f	2	10
41	Given that the change is on execution aspects, the change manifests itself only through the modification of components and connectors on the system	f	0	11
42	This change in the way of how web applications run does not correspond to any change in its architecture, because at the architectural level we still have the same components	f	1	11
43	The only architectural change is on the Deployment view, because the components and connectors remain the same, but execute in different places	f	2	11
46	The *Uses* style	f	1	12
47	The *Layers* style	f	2	12
48	The *Peer-to-Peer* style	f	3	12
49	*Decomposition* and *Layers* views	f	0	13
50	*Decomposition* and *Work assignment* views	f	1	13
52	*Decomposition* and *Generalization* views	f	3	13
53	*Decomposition* and *Implementation* views	f	0	14
55	*Component-and-connector* views	f	2	14
56	 None, given that to perform black-box testing you do not need to know the code or the internal structure of the application to be tested	f	3	14
57	The *Shared data* style	f	0	15
58	The *Repository* style	f	1	15
60	The *Client-Server* style	f	3	15
61	The *Decompostion* and *Implementation* styles	f	0	16
62	The *Deployment* and *Uses* styles	f	1	16
63	The *Client-Server* and *Generalization* styles	f	2	16
65	Views of the Component-and-Connector viewtype	f	0	17
67	Views of the Allocation viewtype	f	2	17
68	All of the above	f	3	17
70	To allow the existence of more than one layer of presentation logic for the same application (to provide for example, an interface to web services)	f	1	18
71	To expose different interfaces of the domain logic layer so that it allows the implementation of different layers for the presentation logic	f	2	18
72	To facilitate the use of the data access layer by the presentation logic layer	f	3	18
73	To implement each of the services that are executed whenever the client makes a request to the server	f	0	19
74	To improve the server performance by maintaining a cache of the objects most accessed during the processing of a client request	f	1	19
75	To split the computation required to process each request made by the client in smaller units of work that are parallelizable	f	2	19
77	To improve the performance of the application server because it maintains a cache of entities that reduces the number of operations made on the database	f	0	20
78	To avoid loading a lot of data from the database when an entity that has many relationships with other entities is loaded	f	1	20
80	To map each entity loaded by the server to the identity of that entity in the database, so that the server is able to update the database later, if needed	f	3	20
81	Stakeholders requirements do not emphasize performance as the most important issue	f	0	21
82	The Hadoop small development team is highly competent and skilled	f	1	21
84	The Hadoop system implementation uses complex distributed algorithms for scalability	f	3	21
85	There is a conflict between availability and performance qualities	f	0	22
87	There is no conflict between availability and performance qualities	f	2	22
88	Availability and performance qualities are ensured at deployment time only	f	3	22
89	Active replication and passive replication	f	0	23
90	Active replication, passive replication, and spare	f	1	23
92	Quorum, active replication, and passive replication	f	3	23
93	Shadow operation	f	0	24
95	Checkpoint/Rollback	f	2	24
96	All of the above	f	3	24
98	Authenticate users, authorize users, and limit exposure	f	1	25
99	Authenticate users, authorize users, and limit access	f	2	25
100	Authenticate users, authorize users, limit access, and maintain integrity	f	3	25
105	Because it has a well-defined interface	f	0	27
106	That aggregates modules according to the uses relationship	f	1	27
107	Because it has a well-defined interface and hides the internal behaviour	f	2	27
109	Because it is a natural extension of the use cases concept	f	0	28
110	But it requires additional information on the modules internal structure	f	1	28
112	And contains all the information required to assess effectively the impact	f	3	28
114	The call's results may not have impact on the correct execution of the callee module	f	1	29
115	The call may not transfer data between the modules	f	2	29
116	The uses relationship requires calls to return control to the caller module	f	3	29
117	We should always satisfy in the first place the requirements of more important stakeholders (such as the client)	f	0	30
118	If no order was established among them, we would not know from where should we start the design process	f	1	30
119	If one of the stakeholders complains that his requirement is not satisfied, we may explain to him that there were other more important requirements first	f	2	30
121	The Shared data style	f	0	31
122	The Pipes-and-filters style	f	1	31
124	The Client-Server style	f	3	31
126	A Client-Server architecture, where the NameNode is the Client and the DataNode is the Server	f	1	32
127	A Peer-to-Peer architecture	f	2	32
128	A Communicating Processes architecture	f	3	32
129	That view will always be incomplete without the NameNode, because the HDFS Client needs to interact with it	f	0	33
130	That view will always be incomplete without the NameNode, because the DataNode needs to interact with it	f	1	33
131	The view does not need to include the NameNode, but in that case it will not be possible to reason about the availability of the system	f	2	33
133	The Shared Data style	f	0	34
135	The Deployment style	f	2	34
136	The Peer-to-Peer style	f	3	34
142	To allow the existence of more than one interface to the domain logic layer (to provide, for example, an interface to web services)	f	1	36
143	To allow the existence of different presentation logic layers	f	2	36
144	To facilitate the use of the data access layer by the presentation logic layer	f	3	36
145	That should not be allowed because all interactions among components must be made through the Repository	f	0	37
146	That is an acceptable solution if we want to reduce the dependencies among the various components of the system	f	1	37
148	That interaction cannot be represented in this view, but it may in another view of the system's architecture	f	3	37
161	Essential to ensure the system scalability	f	0	41
163	Essential to ensure the system portability	f	2	41
164	Essential to facilitate the integration with legacy systems	f	3	41
165	Allows the creation of checkpoints but it is necessary to request all the information from the *NameNode* whenever a new checkpoint creation begins	f	0	42
166	Does not allow the creation of checkpoints	f	1	42
168	Allows the creation of checkpoints without requiring any kind of information from the *NameNode*	f	3	42
170	Performance qualities only	f	1	43
171	Availability qualities only	f	2	43
172	Performance and security qualities	f	3	43
174	Allows *DataNodes* to decide which replicas they have	f	1	44
175	Increases the system modifiability whenever it is necessary to change the the deployment structure	f	2	44
176	Allow several replicas to be located in different *DataNodes*	f	3	44
177	Because this tactic does not overload the *NameNode*	f	0	45
179	But an exceptions tactic could be used as well	f	2	45
180	To notify other *DataNodes* that they are available	f	3	45
181	This script is part of the system deployment module	f	0	46
182	This script is a module that implements a security tactic	f	1	46
184	This script cannot be considered a module because it is only a script	f	3	46
185	Availability, security, and performance	f	0	47
186	Availability only	f	1	47
187	Availability and performance	f	2	47
194	Means that it is possible to implement the system according to an incremental development process	f	1	49
195	Means that the modules that are part of the loop should be implemented first	f	2	49
196	Gives a hint to replace the uses relations by is-a relations	f	3	49
198	Is that the *Uses* relation can happen only among modules belonging to the same layer	f	1	50
199	Is that the *Allowed to Use* relation does not imply that the correctness of the upper layer depends on the correct implementation of its nearest lower layer	f	2	50
200	Is that the *Allowed to Use* relation is a *Uses* relation between layers	f	3	50
201	It does not make sense to use an architectural view for this, because this is an implementation detail that does affect the system's qualities	f	0	51
202	Using a view of the module viewtype that shows the interfaces available for the client to do the write	f	1	51
204	Using a view of the architectural style Deployment	f	3	51
213	The component-and-connector view must, necessarily, be changed to include the components *NameNode* and *DataNode*, with which the web application has to interact to access its data	f	0	54
214	The layered view of the web application will have to include a new layer corresponding to the Hadoop MapReduce framework	f	1	54
216	The Deployment view must be changed to include the racks needed to run the HDFS system	f	3	54
217	The Pipes-and-filters style	f	0	55
218	The Publish-Subscribe style	f	1	55
219	The Peer-to-Peer style	f	2	55
221	Affects only the domain logic layer	f	0	56
223	Does not affect the presentation logic layer because it cannot use it	f	2	56
224	Does not affect the data access layer because the data access layer does not use the domain logic layer	f	3	56
225	A machine may execute only one component, but a component may execute in more than one machine	f	0	57
226	A component may execute in only one machine, but a machine may execute more than one component	f	1	57
227	Each component executes in only one machine and each machine executes only one component	f	2	57
237	Only views of the component-and-connector viewtype are needed	f	0	60
238	Only views of the Deployment style are needed	f	1	60
240	We always need views of the component-and-connector viewtype and of the Deployment style	f	3	60
241	The UK government, because it funded the project	f	0	61
242	The researchers, because they invented the Haskell programming language	f	1	61
243	The UK government, because it intended that the system could be used to develop the British software industry	f	2	61
246	As components of the system	f	1	62
247	The compiler and the RTS as components and the boot libraries as a module	f	2	62
248	The compiler as a component and the other two as modules	f	3	62
249	Made the type-checking much simpler	f	0	63
250	Satisfied performance requirements of the system	f	1	63
251	Made the desugaring simpler	f	2	63
253	The performance of the compiler, because the RTS is written as very efficient C code	f	0	64
254	The performance of the compiled programs, because the RTS is written as very efficient C code	f	1	64
256	The modifiability of the compiled programs, because we may change their behavior by changing only the RTS	f	3	64
257	To allow the parallel execution of the several compilation phases, thereby improving the compiler performance	f	0	65
258	To allow the compilation of very large programs, because wach phase may execute incrementally without loading the entire program into memory at once	f	1	65
260	All other options	f	3	65
261	It is a system with a Peer-to-Peer architecture	f	0	66
262	It is a system with a Client-Server architecture	f	1	66
263	It allows the development of systems with a Peer-to-Peer architecture	f	2	66
265	The Tiers style	f	0	67
266	The Communicating Processes style	f	1	67
268	The Work Assignment style	f	3	67
269	Increases the portability of the system for other operating systems	f	0	68
271	Makes the system more scalable	f	2	68
272	Facilitates the reuse of the messaging patterns	f	3	68
273	To reduce the latency of sending a message when the system is overloaded	f	0	69
275	To reduce the amount of memory needed to send a large number of messages	f	2	69
276	To reduce the CPU usage when the system has just a few messages to send	f	3	69
277	The Pipe-and-Filter style	f	0	70
278	The Shared data style	f	1	70
280	The Client-Server style	f	3	70
281	To have better throughput than Apache	f	0	71
282	To have a lower latency in the processing of a request than Apache	f	1	71
283	To be more modifiable than Apache	f	2	71
285	The code is easier to develop because it is not concurrent	f	0	72
287	Event-driven programs are easier to change	f	2	72
288	In an event-driven system each component may function independently of the others	f	3	72
293	To make the system more modifiable	f	0	74
294	To make the system more portable to different operating systems	f	1	74
296	To make the system more fault tolerant	f	3	74
297	The Shared-data style	f	0	75
299	The Peer-to-Peer style	f	2	75
300	The Client-Server style	f	3	75
301	Increases the availability	f	0	76
302	Increases the capacity	f	1	76
303	Increases the capacity but decreases the availability	f	2	76
305	Only in the Deployment view, because only the number of machines storing images was changed	f	0	77
306	In the Uses view, because each *Image Write/Retrieval Service* is going to use a different *Image File Storage*	f	1	77
308	In the Decomposition view, because we need more modules to represent the split of images by different elements of the architecture	f	3	77
309	Increasing the performance of the *Upload* operation	f	0	78
310	Increasing the performance of the *Retrieval* operation	f	1	78
311	Increasing the scalability of the system	f	2	78
313	We now have four layers, where each layer is executed in the corresponding tier, as before	f	0	79
314	Each one of the two middle tiers executes the previously existing three layers, and no change is needed on the layers view	f	1	79
315	There is no relation between the tiers and the layers, so the layers architecture is not changed	f	2	79
321	The domain logic layer was implemented with the Domain Model pattern	f	0	81
323	The domain logic layer was implemented with the Service Layer pattern	f	2	81
324	The domain logic layer was implemented with a rich domain model, on top of which there was a thin service layer	f	3	81
325	The Decomposition style	f	0	82
326	The Generalization style	f	1	82
328	The SOA style	f	3	82
341	The Communicating Processes style	f	0	86
342	The Client-Server style	f	1	86
343	Any style of the component-and-connector viewtype	f	2	86
346	In the Decomposition view	f	1	87
347	In a view of the component-and-connector type	f	2	87
348	In the Aspects view	f	3	87
349	To create an abstraction layer between the architecture of the system and its functionalities, so that the architecture may be changed later without affecting the functionalities	f	0	88
350	To create an artifact that may be used to explain the system's software architecture to the various stakeholders	f	1	88
352	To facilitate the work assignment to the members of the development team that will implement the system's functionalities	f	3	88
357	Does not change the existing modules of the system, because they are determined by the system's Decomposition, which is not changed	f	0	90
358	Adds restrictions to the dependency relationships that exist between modules and that are represented using other styles, as with the layers style	f	1	90
360	Introduces only a new type of relation among the existing modules of the system, which resulted from other styles of the module viewtype	f	3	90
361	To process all of the requests to send messages with a single thread, to free the remaining cores for the user threads	f	0	91
362	To launch a worker thread for each user thread to guarantee that each user thread may send messages independently of what the others are doing	f	1	91
364	To launch a worker thread to process the sending of a new message, to guarantee maximal parallelism in message sending	f	3	91
365	Only module views	f	0	92
366	Only component-and-connector views	f	1	92
367	Only allocation views	f	2	92
369	It is less robust, because a fault in the *broker* causes a failure in the system	f	0	93
370	It is less modifiable, because all components depend on the *broker*	f	1	93
372	It is more expensive, because it forces the existence of additional hardware to execute the *broker*	f	3	93
374	Views of the Layers style	f	1	94
375	Views of the Decomposition style	f	2	94
376	Views of the Uses style	f	3	94
378	Modifiability	f	1	95
379	Performance	f	2	95
380	Security	f	3	95
381	The Publish-Subscribe style	f	0	96
382	The Client-Server style	f	1	96
383	The Peer-to-Peer style	f	2	96
385	The Layers style	f	0	97
386	The Uses style	f	1	97
388	The Communicating Processes style	f	3	97
389	A new *worker* is created whenever a new connection is established with the server, and that *worker* processes all of the requests for that connection, being destroyed at the end of the connection	f	0	98
390	There is a *pool* of *workers* that are reused between connections, but each *worker* processes only requests of a connection at a time	f	1	98
392	Each *worker* processes requests that it obtains from a *pool of requests* that is shared among all workers	f	3	98
394	By executing in parallel each of the phases of the pipeline corresponding to the processing of a request	f	1	99
395	By executing in parallel the processing of the various requests	f	2	99
396	By processing completely each request before moving to the next one, in a sequential process	f	3	99
397	Have a throughput higher than Apache	f	0	100
398	Be able to process each request faster than Apache	f	1	100
399	Be able to launch more simultaneous threads than Apache	f	2	100
401	The *Decomposition* style	f	0	101
402	The *Client-Server* style	f	1	101
404	The *Communicating Processes* style	f	3	101
405	To increase the performance of RTS	f	0	102
406	To allow changing the GC algorithm without affecting the rest of the system	f	1	102
407	To increase the performance of the programs compiled by the GHC	f	2	102
409	It does not manifest, as it corresponds only to an extension to the Haskell language that must be processed by the compiler	f	0	103
411	In a view of the Generalization style that includes a module defining an abstract interface that all *rewrite rules* must implement and of which all modules with the *rewrite rules* are specializations	f	2	103
412	In a view of the Pipes-and-Filters style, corresponding to the process of compiling an Haskell program, to which is added a new filter whenever a new *rewrite rule* is defined	f	3	103
413	A diagram of the component-and-connector viewtype	f	0	104
415	A diagram of the deployment style	f	2	104
416	A diagram of the implementation style	f	3	104
418	A diagram of the module viewtype, showing a decomposition of the compiler in the various modules that are responsible by each of the compilation process steps	f	1	105
419	A diagram of the module viewtype, showing which modules use other modules	f	2	105
420	A layered diagram, where there is a layer responsible for the code generation	f	3	105
421	They are both modules	f	0	106
423	The *Request Node* is a component and the *Cache* is a module	f	2	106
424	The *Request Node* is a module and the *Cache* is a component	f	3	106
425	Increasing performance and availability	f	0	107
426	Increasing availability and decreasing performance	f	1	107
403	The *Layers* style	t	2	101
408	To make the RTS more portable to other operating systems	t	3	102
410	In the existence of a compiler component that is responsible for interpreting and applying the rewrite rules during the compilation of a program	t	1	103
414	A diagram of the module viewtype	t	1	104
417	A diagram of the component-and-connector viewtype, showing the data flow between the various compiler components	t	0	105
422	They are both components	t	1	106
427	Increasing performance and decreasing availability	t	2	107
428	Increasing scalability and availability	f	3	107
429	The Shared-Data style	f	0	108
430	The Client-Server style	f	1	108
432	The Communicating Processes style	f	3	108
434	The availability of the system's data decreases	f	1	109
435	The availability of the system's services decreases	f	2	109
436	The system is not affected in any way	f	3	109
441	To start using the Transaction Script pattern in the domain logic layer	f	0	111
442	To start using the Service Layer pattern in a new layer	f	1	111
444	To eliminate the service layer	f	3	111
445	Views of the Module viewtype	f	0	112
446	Views of the Component-and-Connector viewtype	f	1	112
447	Views of the Allocation viewtype	f	2	112
461	The Peer-to-Peer style	f	0	116
463	The Shared-Data style	f	2	116
464	The Publish-subscribe style	f	3	116
469	By using HTTPS in the communication between the browser and the web server	f	0	118
470	By using robust authentication mechanisms to identify the users of the system with confidence	f	1	118
471	By encrypting the information in the database with a password that is known only by the web server	f	2	118
474	The connector used to represent the interaction between the browser and the web server changed	f	1	119
475	The browser is now a component of a different type	f	2	119
476	That evolution did not have any consequences on the software architecture of a web application	f	3	119
477	A component	f	0	120
479	A module	f	2	120
480	A layer	f	3	120
486	Maintain Multiple Copies of Data	f	1	122
487	Passive Redundancy	f	2	122
488	Active Redundancy	f	3	122
494	Performance, availability, and testability	f	1	124
495	Reliability, performance, and usability	f	2	124
496	Performance and usability	f	3	124
498	Repository e Service Oriented Architecture	f	1	125
499	Client-Server, Repository, Communicating-Processes e Service Oriented Architecture	f	2	125
500	Client-Server, Repository e Communicating-Processes	f	3	125
502	By using a Retry tactic	f	1	126
503	Storing the information in the client using cookies	f	2	126
504	By using a Transactions tactic	f	3	126
505	May stop accepting reads	f	0	127
507	May stop accepting reads and writes	f	2	127
508	May need to add more servers to the cluster	f	3	127
510	Increase Resources	f	1	128
511	Prioritize Events	f	2	128
512	Maintain Multiple Copies of Data	f	3	128
513	Data Encryption	f	0	129
514	Intrusion Detection	f	1	129
516	Authorize Actors	f	3	129
518	Maintain Multiple Copies of Computation	f	1	130
519	Limit Exposition	f	2	130
520	Active Redundancy	f	3	130
521	Publication-Subscription	f	0	131
522	Pipes-and-Filters	f	1	131
524	Client-server	f	3	131
525	It can take advantage of concurrency	f	0	132
526	The *browser* needs to make more requests to the server	f	1	132
527	It uses machine learning techniques	f	2	132
529	It is always the same for all instances of Chrome	f	0	133
530	It is defined compile-time of Chrome code	f	1	133
531	It is defined during the initialization of each instance of Chrome	f	2	133
534	Maintain Task Model tactic	f	1	134
535	Maintain System Model tactic	f	2	134
536	Support User Initiative tactics	f	3	134
537	It is necessary to decompress the complete file, even though if only a small part of the information is needed	f	0	135
538	Pickle algorithm is not efficient	f	1	135
539	It is a Python specific solution	f	2	135
542	Increase the modifiability quality, because external applications stopped using the administrative functionalities	f	1	136
543	Increase the interoperability quality, because external applications can read and send messages to GNU Mailman	f	2	136
544	None of the previous options	f	3	136
545	A sequence of bytes to allow independence between filters	f	0	137
546	An object tree to allow the simultaneous execution of several filters	f	1	137
547	A sequence of bytes to allow that the order of filters execution is not relevant	f	2	137
549	Effectively guarantees the FIFO delivery of messages and the queue runners do not need to synchronize	f	0	138
550	Guarantees the FIFO delivery of messages but the queue runners need to synchronize	f	1	138
552	Guarantees the FIFO delivery of messages because in each *queue* only are stored messages which arrived with a difference of more than one minute	f	3	138
553	Client-Server	f	0	139
554	Peer-to-Peer	f	1	139
556	Tiers	f	3	139
557	Each message does not need to be accessed concurrently by several processes	f	0	140
558	Pickle can efficiently write and read messages	f	1	140
559	Each message is stored as a file in a directory	f	2	140
561	Allocate modules to the file system	f	0	141
563	Are applied to completely distinct sets of files	f	2	141
564	Are applied to the same set of files	f	3	141
565	Interoperability e Performance	f	0	142
566	Performance and Availability	f	1	142
567	Easy Development and Performance	f	2	142
570	Passive Redundancy	f	1	143
571	Active Redundancy	f	2	143
572	Passive Redundancy and Active Redundancy	f	3	143
573	It is necessary to use a optimistic concurrency control policy because the transactions cannot be open for a long period	f	0	144
574	It is necessary to use a pessimistic concurrency control policy to avoid frequent conflicts in the transactions	f	1	144
576	Transactional management is the complete responsibility of the repository	f	3	144
582	Resist to the attack	f	1	146
583	React to the attack	f	2	146
584	Resist and React to the attack	f	3	146
589	Depend on the types of the publishers components	f	0	148
591	Are completely independent	f	2	148
592	It is necessary to support dynamic Defer Binding of components, publishers and subscribers, to the connector to be completely independent	f	3	148
593	They only concern the web designers	f	0	149
594	They are dependent on performance tactics	f	1	149
595	They are dependent on availability tactics	f	2	149
601	Performance	f	0	151
602	Reliability and Performance	f	1	151
604	Security	f	3	151
613	Only have a server for write requests	f	0	154
614	Store all the information statically	f	1	154
616	Use several levels of cache	f	3	154
617	Passive Redundancy and Increase Resources Efficiency	f	0	155
618	Active Redundancy and Increase Resources Efficiency	f	1	155
619	Active Redundancy and Maintain Multiple Copies of Computation	f	2	155
621	In the server	f	0	156
623	In the repository	f	2	156
624	In the client	f	3	156
625	Escalating Restart	f	0	157
626	Voting	f	1	157
628	Exception Handling	f	3	157
629	Prioritize Events	f	0	158
630	Increase Resources	f	1	158
632	Maintain Multiple Copies of Data	f	3	158
633	Security and Mobility	f	0	159
634	Performance, Security and Interoperability	f	1	159
635	Security, Performance, Usability, Interoperability and Mobility	f	2	159
641	Separates the Renderer process from the other processes	f	0	161
643	Applies machine learning techniques	f	2	161
644	Uses prefetching	f	3	161
645	Repository	f	0	162
647	Service-Oriented Architecture	f	2	162
648	Client-Server	f	3	162
650	The page is in cache	f	1	163
651	Uses the Maintain Multiple Copies of Computation tactic	f	2	163
652	Uses the Maintain Multiple Copies of Data tactic	f	3	163
657	The new user interface started using the REST interface	f	0	165
659	External applications can read and send messages to GNU Mailman	f	2	165
660	The GNU Mailman interface became public	f	3	165
661	Communicating-Processes	f	0	166
662	Client-Server	f	1	166
664	Publish-Subscribe	f	3	166
666	Decomposition	f	1	167
667	Aspects	f	2	167
668	Pipes-and-Filters	f	3	167
670	The quality of Availability	f	1	168
671	The quality of Reliability	f	2	168
672	The FIFO delivery of messages	f	3	168
674	Security and Testability	f	1	169
675	Reliability and Modifiability	f	2	169
676	Reliability and Testability	f	3	169
677	Module	f	0	170
678	Component-and-Connector	f	1	170
680	Module, but only for the Decomposition architectural style	f	3	170
682	Performance and Interoperability	f	1	171
683	Easiness of Development and Performance	f	2	171
684	Interoperability	f	3	171
685	Decomposition	f	0	172
687	Uses	f	2	172
688	Data Model	f	3	172
689	User Model and Undo	f	0	173
691	User Model and System Model	f	2	173
692	System Model	f	3	173
693	It is not possible to support SQL searches in the application server	f	0	174
694	It is always necessary to search in the database before accessing an object	f	1	174
695	All accesses to objects should occur through their inter-references	f	2	174
697	Exception Handling	f	0	175
698	Increase Competence Set	f	1	175
699	Exception Prevention	f	2	175
701	The Model module uses the Observer module	f	0	176
702	The Model module uses the Observer module if data is sent in the notification	f	1	176
703	The Model module uses the Observer module if complex data is sent in the notification	f	2	176
705	The Uses views are designed first	f	0	177
706	The Layered view are designed first	f	1	177
708	Whenever there is at least one Uses view then a Layered view needs to be designed as well	f	3	177
709	Can only contain a single architectural style	f	0	178
710	May contain several architectural styles, but only if the are of the Component-and-Connector and Allocation viewtypes	f	1	178
712	May contain several architectural styles, if that is the best way to convey the information to a group of stakeholders	f	3	178
713	Can only be applied after the Decomposition view is finished	f	0	179
714	Can be applied before a Decomposition view is designed	f	1	179
716	Should be applied in at least a view of the system	f	3	179
718	It is not necessary to have transactional behavior in the business logic	f	1	180
719	The Component-and-Connector architecture needs to have three Tiers	f	2	180
720	The Module architecture needs to have three Layers	f	3	180
841	The Transaction Script pattern to help demarcate the business transactions.	f	0	211
843	The Data Access layer to be able to access the data that it needs in each service.	f	2	211
844	The Table Module pattern to hide the details of the table structure for the Presentation layer.	f	3	211
845	The Transaction Script pattern.	f	0	212
846	The Table Module pattern.	f	1	212
848	The Service Layer pattern.	f	3	212
854	The Requirements function is not part of the RulesSet module.	f	1	214
855	The Requirements function is part of the Objects module.	f	2	214
856	The Requirements function is part of the Dynamic Design module.	f	3	214
858	An Encrypt Data tactic for the Security quality.	f	1	215
859	A Verify Message Integrity tactic to React to Attacks for the Security quality.	f	2	215
860	An Exception Prevention tactic to Prevent Faults for the Availability quality.	f	3	215
861	The source of stimulus for scenarios of the Availability quality.	f	0	216
862	The stimulus for scenarios of the Availability quality.	f	1	216
864	The source of stimulus for scenarios of the Security quality.	f	3	216
869	The source of stimulus is the ruleset.	f	0	218
870	The ruleset designer is the stimulus.	f	1	218
872	The response is defer binding.	f	3	218
873	Schedule resources.	f	0	219
874	Condition monitoring.	f	1	219
875	Reduce overhead.	f	2	219
878	Use a classic 3-layer architecture with the following layers, from top to bottom: Presentation, Domain Logic, and Data Access.	f	1	220
879	Use an aspect-oriented architecture, where an aspect is used to generate a graphical interface.	f	2	220
880	Use two deployment views, each one allocating different components to different machines with different operating systems.	f	3	220
886	It is not necessary to use a ''Data Access'' layer because the information is simple.	f	1	222
887	We must identify a module for writing the scores in a Decomposition style.	f	2	222
888	We may assign the responsibility of writing the scores to another module that already has other responsibilities.	f	3	222
889	Client-server in both cases.	f	0	223
891	Peer-to-peer in both cases.	f	2	223
892	Peer-to-peer in the first case and Client-Server in the second.	f	3	223
893	Depends mostly on the system's functional requirements.	f	0	224
894	Depends more on the architect's experience than on anything else.	f	1	224
895	Should not depend on the skills of the developing team.	f	2	224
897	May be responsible for the Featuritis problems of architectures.	f	0	225
899	Is focused on creating common generalizations of several systems.	f	2	225
900	Is focused on the details of the architecture.	f	3	225
905	The current location is the source of the stimulus.	f	0	227
906	The traffic monitoring system is the environment.	f	1	227
907	The Google Map is the artefact.	f	2	227
913	A component-and-connector view using a shared-data style.	f	0	229
914	A data model view.	f	1	229
915	A service-oriented architecture view.	f	2	229
917	Peer-to-peer style.	f	0	230
918	Pipe-and-Filter style.	f	1	230
920	Publish-subscribe style.	f	3	230
921	The team did not know the FenixFramework.	f	0	231
923	A domain layer is absent from the architecture.	f	2	231
924	Most of the information is stored in the client.	f	3	231
926	It is necessary to design a single deployment view that contains all the variation, because only the hardware capabilities change.	f	1	232
927	Two different component-and-connector views are necessary to represent the same runtime behavior of the system.	f	2	232
928	The deployment options have a large impact on the work assignment view.	f	3	232
929	Passive redundancy for availability, because it is possible to recover from the commands log.	f	0	233
930	Undo tactic for usability, because the server can undo the snapshot.	f	1	233
932	Multiple copies of data tactic for performance, clients do not have to execute the commands.	f	3	233
933	The server propagates them to all the clients.	f	0	234
935	The server only propagates local commands to the clients and keeps cursor movements in a log and the snapshots in a repository.	f	2	234
936	The server propagates the snapshots and the cursor movements to the clients and store the local commands for the initialization of new clients.	f	3	234
937	Testability.	f	0	235
938	Modifiability.	f	1	235
940	Performance.	f	3	235
1085	Requires a more skilled team, because the object-oriented paradigm is more complex than the procedural paradigm	f	0	272
1086	Is typically used with more complex data access code	f	1	272
1087	Requires that we write more code when we have only a couple of simple use cases	f	2	272
1089	The Service Layer pattern	f	0	273
1091	The Transaction Script pattern	f	2	273
1092	The Data Mapper pattern	f	3	273
1098	Heartbeat requires the availability monitor to confirm the reception of the signal	f	1	275
1099	In Ping-and-echo the availability monitor should always send the same request	f	2	275
1100	In Heartbeat, the monitored components can change the message rate	f	3	275
1101	It is not possible to achieve this requirement. A non-architectural solution is to be careful when hiring system administrators	f	0	276
1102	It is necessary to use the authenticate authors tactic to authenticate system administrators before they access to the database	f	1	276
1103	It is necessary to use the encrypt data tactic to encrypt the information with a password that is in a configuration file	f	2	276
1109	A scenario for performance associated with a multiple copies of computation tactic	f	0	278
1110	A scenario for usability associated with a support system initiative tactic	f	1	278
1111	A scenario for performance associated with a limit event response tactic	f	2	278
1114	Active redundancy	f	1	279
1115	Increase resource efficiency	f	2	279
1116	All of the above	f	3	279
1118	Data model view	f	1	280
1119	Generalization view	f	2	280
1120	Layered view	f	3	280
1121	To analyse the source code of the system to see how it is built	f	0	281
1122	To analyse the system's functional requirements to see what is the system supposed to do	f	1	281
1123	To analyse the implemented set of features to see what is it that the system actually does	f	2	281
1125	The architecture of a system cannot change	f	0	282
1126	The main goal of an architect is to identify the quality attributes of system	f	1	282
1128	The main goal of an architect is to design a detailed structure of the system that supports most of the requirements	f	3	282
1129	It is not a good idea to consider performance when designing the architecture of the system	f	0	283
1130	The performance of a system only depends on the global performance strategies	f	1	283
1131	Testability and maintainability always conflict with performance	f	2	283
1134	Have a view for each stakeholder	f	1	284
1135	Have at least a view for each viewtype	f	2	284
1136	Have a view for each group of interconnected components, and very often a system has several groups of interconnected components	f	3	284
1137	We should always satisfy in the first place the requirements of the more important stakeholders (such as the client)	f	0	285
1138	If no order was established among them, we would not know from where should we start the design process	f	1	285
1139	If one of the stakeholders complains that her requirement is not satisfied, we may explain to her that there were other more important requirements first	f	2	285
1141	A module	f	0	286
1142	A component	f	1	286
1144	An allocation element	f	3	286
1145	Peer-to-Peer to represent the communication between the components	f	0	287
1146	Client-Server to represent the request the application makes to the different new sources	f	1	287
1148	Layers to create a virtual machine that hides the internals of the application from its users interface code to allow the support of different user interfaces	f	3	287
1149	Performance because there is an overhead of communication between the modules.	f	0	288
1150	Install because most of the modules need to be assigned to the same executable file	f	1	288
1152	Availability because if a module fails the failure easily propagates to all the other modules	f	3	288
1154	Modifiability, because the jsdom code can not be reused by several threads	f	1	289
1155	Security, because it describes a "queue overflow" attack	f	2	289
1156	Interoperability, because the REST API allow the exchange of information with external applications	f	3	289
1157	Reduce overhead tactic	f	0	290
1159	Increase resources tactic	f	2	290
1160	Testability tactic	f	3	290
1161	Overall costs, because of deployment	f	0	291
1162	Availability, because of the interprocess communication	f	1	291
1164	Performance, because there is not a significative improvement by using more CPUs	f	3	291
1169	A publish-subscribe style	f	0	293
1170	A peer-to-peer style	f	1	293
1171	A client-server style	f	2	293
1173	It is enough to show the load-balancer between the web clients machines and the servers machines using a deployment view	f	0	294
1175	It is necessary to create a uses view to show how clients require the correct functioning of servers	f	2	294
1176	It is necessary to change the component-and-connector view to show the communicating processes	f	3	294
1343	Is an architectural pattern.	f	2	336
1177	They have many different use cases, corresponding to many distinct user interfaces	f	0	295
1179	They need to be able to process concurrent requests from the users	f	2	295
1180	They have a very complex domain logic that requires much processing power for answering each request	f	3	295
1190	An aspects architectural style	f	1	298
1191	A data model architectural style	f	2	298
1192	A shared-data architectural style	f	3	298
1193	Subscribes to the same kind of events that the sub2 port	f	0	299
1194	Subscribes to the same kind of events that the inputSub port	f	1	299
1196	It is unnecessary in the diagram because the :TableEditor can use port sub2 through the :Sheet component	f	3	299
1197	The communicating processes architectural style	f	0	300
1198	The client-server architectural style	f	1	300
1199	The deployment architectural style	f	2	300
1201	Featuritis may result from a requirement of the technical context.	f	0	301
1202	Featuritis requires the performance quality because the end user needs to execute the features.	f	1	301
1204	Featuritis requires the modifiability quality to allow a the system to be easily modified to support new features.	f	3	301
1209	The book definition does not consider relevant the externally visible properties.	f	0	303
1211	The book definition also considers that the properties are externally visible because by definition an architectural property is externally visible.	f	2	303
1212	The book definition is not correct, as pointed out in the errata.	f	3	303
1214	This shared understanding is necessary to define precise requirements.	f	1	304
1215	This shared understanding does not allow to define the architecture trade-offs because some of the stakeholders have their own goals.	f	2	304
1216	This shared understanding does not allow to have a global perspective of the system, because stakeholders have different interests.	f	3	304
1222	Frank Buschmann are considering performance and security as the most important qualities.	f	1	306
1223	Frank Buschmann is referring that the consequences of a flexible system is poor performance and bad security.	f	2	306
1224	Frank Buschmann is not considering modifiability as an important quality	f	3	306
1225	Performance should be the last quality to be addressed because it is a local property of an architecture.	f	0	307
1226	Modifiability, flexibility, should be the first quality to be addressed because it allows the delay of architectural decisions.	f	1	307
1227	The lack of functionality results in a system without business value, therefore a rich set of features should be implemented first.	f	2	307
1229	A component.	f	0	308
1231	Both, a component and a module, depending on the perspective.	f	2	308
1232	An external element.	f	3	308
1237	Is unable to define a domain model of the system.	f	0	310
1238	Is focused on the technology context of the architecture.	f	1	310
1240	Is focused on the details of the architecture.	f	3	310
1241	Such misunderstanding and mistrust occurs because the stakeholders have their own agendas	f	0	311
1242	The cycle Frank Buschmann refers to is the Architectural Influence Cycle.	f	1	311
1243	The cycle Frank Buschmann refers to allows the clarification of requirements.	f	2	311
1245	Is a functional prototype, which tests the functionalities required by the business stakeholders.	f	0	312
1246	Is an architecture that demonstrates that the system will support the qualities raised by the stakeholders.	f	1	312
1344	Is a system decomposition.	f	3	336
1203	Featuritis may be a result of a requirement of the business context.	t	2	301
1210	The book definition also considers that the properties are externally visible because they are used for reasoning by the stakeholders.	t	1	303
1230	A module.	t	1	308
1248	Is an object-oriented framework, which integrates functional and non-functional requirements of the system.	f	3	312
1249	A component.	f	0	313
1251	Both, a component and a module, depending on the perspective.	f	2	313
1252	An external element.	f	3	313
1257	Are unable to understand the technology capabilities.	f	0	315
1258	Are focused on the project context of the architecture.	f	1	315
1260	Are focused on the business context of the architecture.	f	3	315
1261	A flexible architecture occurs when it is not possible to identify all the requirements.	f	0	316
1263	Performance uncertainty about the system should be dealt with more flexibility.	f	2	316
1264	A solution to this problem is to reduce the level of flexibility of a system.	f	3	316
1265	Project and Technical Contexts.	f	0	317
1266	Project and Professional Contexts.	f	1	317
1267	Business and Project Contexts.	f	2	317
1273	Modifiability.	f	0	319
1275	Testability.	f	2	319
1276	Availability.	f	3	319
1277	Tries to guarantee that the final system will have the qualities required by stakeholders.	f	0	320
1279	Does not allow developers to define some of the design of the system	f	2	320
1280	It requires automatic generation of code from the architecture.	f	3	320
1281	Performance.	f	0	321
1283	Reliability.	f	2	321
1284	Fault-tolerance	f	3	321
1285	Implements a tactic to recover from faults.	f	0	322
1286	Implements a tactic to prevent faults.	f	1	322
1288	Can be used in a non-concurrent system.	f	3	322
1289	Is an aggregate design tactic.	f	0	323
1290	Is a maintain user model design tactic.	f	1	323
1292	Is a design tactic for a scenario where the source of stimulus is the graph owner user.	f	3	323
1293	Detect intrusion.	f	0	324
1294	Limit access.	f	1	324
1296	Separate entities.	f	3	324
1301	The stimulus is a system input.	f	0	326
1302	The response can be omitted.	f	1	326
1303	The artefact can be outside the system.	f	2	326
1310	The quality addressed is performance.	f	1	328
1311	The quality addressed is availability and a voting design tactic is required to solve the problem.	f	2	328
1312	The quality addressed is performance and a maintain multiple copies of data design tactic is required to solve the problem.	f	3	328
1313	Detect intrusion.	f	0	329
1314	Detect service denial.	f	1	329
1316	Detect message delay.	f	3	329
1317	Increase resources.	f	0	330
1319	Reduce overhead.	f	2	330
1320	Manage sample rate.	f	3	330
1321	Performance.	f	0	331
1322	Availability.	f	1	331
1324	Testability.	f	3	331
1325	Ignore faulty behavior.	f	0	332
1326	Transactions.	f	1	332
1327	Rollback.	f	2	332
1330	A Maintain Multiple Copies of Computation design tactic in the WebApp such that reads do not compete with writes.	f	1	333
1331	A Maintain Multiple Copies of Data design tactic in Carbon.	f	2	333
1332	A Maintain Multiple Copies of Data design tactic in the WebApp such that reads do not compete with writes.	f	3	333
1333	Limit access, to restrict the access to the database system.	f	0	334
1334	Limit exposure, locate the database system in the intranet.	f	1	334
1336	Change default settings, because default passwords are sensitive.	f	3	334
1338	Introduce concurrence tactic.	f	1	335
1339	Increase resource efficiency tactic.	f	2	335
1340	Maintain task model tactic.	f	3	335
1341	Is a mediator, an application of the mediator pattern, between the input stimulus and the output response.	f	0	336
1250	A module.	t	1	313
1346	This situation corresponds to the use of the removal from service availability tactic.	f	1	337
1347	This situation corresponds to the use of the limit access security tactic.	f	2	337
1348	This situation corresponds to the use of the limit exposure security tactic.	f	3	337
1349	The quality addressed is availability.	f	0	338
1351	The quality addressed is availability and an active redundancy design tactic is required to solve the problem.	f	2	338
1352	The quality addressed is modifiability and an increase cohesion design tactic is required to solve the problem.	f	3	338
1353	Detect and Resist.	f	0	339
1354	Detect and React.	f	1	339
1356	Resist and React.	f	3	339
1369	Testability.	f	0	343
1370	Reliability.	f	1	343
1371	Availability.	f	2	343
1374	Performance.	f	1	344
1375	Availability.	f	2	344
1376	Usability.	f	3	344
1382	Availability scenario.	f	1	346
1383	Modifiability scenario.	f	2	346
1384	Usability scenario.	f	3	346
1389	Performance.	f	0	348
1391	Availability.	f	2	348
1392	Usability.	f	3	348
1393	Change default settings.	f	0	349
1394	Limit access.	f	1	349
1395	Support user initiative.	f	2	349
1397	Results in a similar decomposition as if the criteria was not applied but some modules are identified to be outsourced.	f	0	350
1398	Results in a decomposition where each module may be implemented by a single developer.	f	1	350
1399	Allows to increase the overall calendar development time of the project because there is a communication overhead with external teams.	f	2	350
1401	Only contains business qualities.	f	0	351
1402	Cannot be defined for the security quality.	f	1	351
1403	Contains the architectural tactics associated with architecturally significant requirements.	f	2	351
1405	This ASR can easily be supported by the architecture.	f	0	352
1406	This ASR should be supported by the architecture because of its high impact.	f	1	352
1408	The architect should support this ASR after designing an architecture that supports all the ASRs with high business value.	f	3	352
1409	Performance.	f	0	353
1410	Reliability.	f	1	353
1412	Usability.	f	3	353
1413	Performance.	f	0	354
1414	Interoperability.	f	1	354
1416	Usability.	f	3	354
1422	Availability scenario.	f	1	356
1423	Modifiability scenario.	f	2	356
1424	Usability scenario.	f	3	356
1434	Persistence.	f	1	359
1435	Retry.	f	2	359
1436	Passive redundancy.	f	3	359
1441	Applying the generalization style to identify child modules of a module in the loop chain.	f	0	361
1443	Identifying which of the *uses* dependencies are actually generalization dependencies.	f	2	361
1444	Decomposing a *uses* relation into different interfaces.	f	3	361
1450	Limit access.	f	1	363
1451	Authorize actors.	f	2	363
1452	Separate entities.	f	3	363
1453	The type of a connector does not depend on the type of its roles.	f	0	364
1454	The type of a component does not depend on the type of its ports.	f	1	364
1455	The attachment is a runtime relation which dynamically manages type compliance.	f	2	364
1465	It is necessary design a CRUD matrix to show the dependencies between the persistent information.	f	0	367
1466	It is enough to design a view of the Data Model architectural style at the conceptual level because Facebook information has a very simple structure.	f	1	367
1467	It is not necessary to have any view of the Data Model architectural style because Facebook information has a very simple structure.	f	2	367
1469	Schedule resources.	f	0	368
1470	Maintain multiple copies of data.	f	1	368
1472	Reduce overhead.	f	3	368
1473	She encapsulates the connector qualities inside a higher level component.	f	0	369
1474	She delays the complete specification of the connector for development time to have human resources to prototype and measure different implementations.	f	1	369
1475	She does not want to clutter the view with details and trusts the development team to implement the connector according to the required quality level.	f	2	369
1478	She has to use another architectural style to describe asynchronous communication.	f	1	370
1479	She can use the request/reply connector but the server should not return results to the client.	f	2	370
1480	She can define a variant of this style with asynchronous communication by allowing the server to have the initiative to initiate the interaction.	f	3	370
1481	Allows the analysis of the impact of changes because if a module uses another it will necessarily have to change whenever the used module changes.	f	0	371
1482	Improves testability because if a module uses another then it is only possible to test them together.	f	1	371
1484	Improves testability because it informs the tester about which modules involved in circular use dependencies.	f	3	371
1486	Is an extension of a view of the Data Model style.	f	1	372
1487	Allows to avoid redundancy and inconsistency.	f	2	372
1488	Describes the structure of the data used by the system.	f	3	372
1490	Multiple copies of computation tactic.	f	1	373
1491	Passive redundancy tactic.	f	2	373
1492	Multiple copies of computation and Active redundancy tactics.	f	3	373
1493	A module interface has to be attached to a single component port.	f	0	374
1494	A module interface can be replicated but component ports cannot.	f	1	374
1496	A module interface may be attached to several component ports.	f	3	374
1497	It allows an undefined number of clients.	f	0	375
1499	Servers can also be clients.	f	2	375
1433	Rollback.	t	0	359
1500	Servers can send a heartbeat to clients.	f	3	375
1505	Sanity checking.	f	0	377
1506	Exception detection.	f	1	377
1507	Detect intrusion.	f	2	377
1510	It is possible to change the repository schema without changing the data accessors.	f	1	378
1511	The integration of a new data accessor only implies changes in the data accessors that access the same type of data.	f	2	378
1512	The communication between data accessors does not occur through the repository.	f	3	378
1517	This means that the modules inside a layer cannot be loosely coupled.	f	0	380
1518	This means that this architectural style emphasizes the quality of performance.	f	1	380
1519	This means that each module cannot use other modules inside the same layer.	f	2	380
1525	Is a Client-Server style because consumers are clients and providers are servers.	f	0	382
1526	Is a Peer-to-Peer style because consumers and providers are peers.	f	1	382
1528	Is a Publish-subscriber style because consumers use an enterprise service bus.	f	3	382
1530	In the view there are multiple instances of the `Writer` component.	f	1	383
1531	In the view `Receiver` component's `client` port is not associated with an external port.	f	2	383
1532	In the view the `produce` port of a `Receiver` component is attached to the `consume` port of a `Writer` component.	f	3	383
1545	It encapsulates applications through well-defined interfaces.	f	0	387
1546	It decouples the coordination of the interaction among applications from the applications themselves.	f	1	387
1547	It improves transparency of location of service providers.	f	2	387
1549	There is a message passing connector between the `read` port of `Queue` and the `data points access` port of `WebApp`.	f	0	388
1551	There is a connector between the `producer` port of a `Queue` component and the `client` port of its `Carbon` component.	f	2	388
1552	The `client` ports of `Carbon` and `WebApp` are connected to a `Client` component through the same connector instance.	f	3	388
1553	Deployment style.	f	0	389
1555	Install style.	f	2	389
1556	Work assignment style.	f	3	389
1561	It applies layers to tiers.	f	0	391
1563	Is an extension of the Client-Server architectural style.	f	2	391
1564	Defines tiers as components.	f	3	391
1569	Deployment style.	f	0	393
1570	Implementation style.	f	1	393
1572	Work assignment style.	f	3	393
1578	Memcached can be considered a sub-module of the Present Graphs module.	f	1	395
1579	Memcached can be considered a direct sub-module of the top Graphite module.	f	2	395
1580	Memcached is not a module.	f	3	395
1590	Buffering can be considered a sub-module of the Present Graphs module.	f	1	398
1591	Buffering can be considered a direct sub-module of the top Graphite module.	f	2	398
1592	Buffering is not a module.	f	3	398
1593	A work assignment view.	f	0	399
1595	An install view.	f	2	399
1596	An implementation view.	f	3	399
1602	Google Chrome is more convenient for mobile devices because it has an optimized network stack that runs in any kind device.	f	1	401
1603	Amazon Silk is more convenient for mobile devices because it customizes the number of threads that run in the device.	f	2	401
1604	Google Chrome is more convenient for mobile devices because content delivery is optimized.	f	3	401
1605	There is a ThousandParsec connector.	f	0	402
1606	There is a Read/Write connector which encapsulates a redundant Repository.	f	1	402
1608	There is a Read/Write connector which guarantees that only the turns of the last two players may be lost.	f	3	402
1609	Supports asynchronous communication to deal with disconnected mode.	f	0	403
1610	Implements an event bus that allows the server to inform the client about new order recommendations.	f	1	403
1611	May loose some of the changes done on the client component.	f	2	403
1613	The ConflictResolution module is part of the code executed by the : TableEditor component.	f	0	404
1615	The code of the ConflictResolution module is executed by a broadcast connector that implements an eventbus between the SpreadSheet components.	f	2	404
1616	The code of the ConflictResolution module is executed in a server component because it needs to be centralized.	f	3	404
1617	An object oriented style is followed.	f	0	405
1619	Row Data Gateway is the most suitable data source pattern.	f	2	405
1620	A Service Layer should be used to provide an interface for the presentation layer.	f	3	405
1621	In Amazon Silk a request for a web page corresponds to a peer-to-peer interaction between all the web components containing the resources.	f	0	406
1622	In Google Chrome a request for a web page is accomplished by a single access to the internet.	f	1	406
1624	In Google Chrome a request for a web page aggregates the page on the background before it is sent to the client.	f	3	406
1625	Should be described as a submodule of the RulesSet module.	f	0	407
1627	Should be described as a submodule of the Design module.	f	2	407
1628	Should not be described as a module because it is a component.	f	3	407
1633	The : TableEditor broadcasts the cursor position through the : Sheet.	f	0	409
1601	Amazon Silk is more convenient for mobile devices because it does most of the computation in the cloud.	t	0	401
1607	There is a Read/Write connector which guarantees that players turns are not lost.	t	2	402
1612	Has reduced reliability qualities.	t	3	403
1614	The ConflictResolution module is part of the code executed by the : Sheet component.	t	1	404
1618	The business logic is organized around record sets.	t	1	405
1623	In Amazon Silk a request for a web page corresponds to requesting a service from the amazon cloud.	t	2	406
1626	Should be described as a submodule of but not included in the RulesSet subtree.	t	1	407
1635	The : Sheet broadcasts the cursor position through the Pub port.	f	2	409
1636	The : TableEditor broadcasts the cursor position through its : StatusCallback port.	f	3	409
1637	Are responsible for loading the objects they refer to.	f	0	410
1638	Are responsible for the management of transactions, begin and end of transactions.	f	1	410
1639	Contain the business logic.	f	2	410
1642	Google Chrome uses a usability maintain system model tactic.	f	1	411
1643	Amazon Silk predictions are constrained by the amount of information it can store about each user access.	f	2	411
1644	Google Chrome predictions do not require storage in the client-side.	f	3	411
1645	There is a ThousandParsec connector.	f	0	412
1646	There is a Request/Reply connector.	f	1	412
1648	There is an EventBus connector.	f	3	412
1650	Was taken because Native applications provide better modifiability qualities.	f	1	413
1651	Was taken because HTML5 provides better usability qualities.	f	2	413
1652	Was taken because Native application provide better support for working offline.	f	3	413
1654	The server implements the : Repository component.	f	1	414
1655	The server implements the : Broadcast connector.	f	2	414
1656	The server implements the SpreadSheet components	f	3	414
1658	Row Data Gateway and Active Record.	f	1	415
1659	Row Data Gateway and Data Mapper.	f	2	415
1660	Active Record and Data Mapper.	f	3	415
1661	Amazon Silk explicitly caches pages on the browser to optimize accesses.	f	0	416
1663	Amazon Silk cache is not shared between different users of the service to support confidentiality.	f	2	416
1664	Google Chrome cache is shared among the different users of a desktop machine.	f	3	416
1665	As a specialization of the RulesSet module.	f	0	417
1666	As a submodule of the RulesSet module.	f	1	417
1668	As a specialization of the Design module.	f	3	417
1670	A single bidirectional connector.	f	1	418
1671	Three distinct unidirectional connectors.	f	2	418
1672	A single unidirectional connector.	f	3	418
1673	The Parser module is part of the code executed by the : TableEditor component.	f	0	419
1675	The code of the Parser module is executed by a repository component, which is not represented in the view.	f	2	419
1676	The code of the Parser module is executed by both, the : Sheet and the repository components (the latter is not visible in the view).	f	3	419
1677	Table Data Gateway and Row Data Gateway.	f	0	420
1678	Row Data Gateway and Active Record.	f	1	420
1679	Row Data Gateway and Data Mapper.	f	2	420
1802	The response is JUnit XML standard	f	1	451
1803	The source of the stimulus is Sun	f	2	451
1804	The measure of the response is a robust open-source community associated with it	f	3	451
1805	Modifiability and Performance	f	0	452
1806	Availability and Modifiability	f	1	452
1808	Reliability and Security	f	3	452
1809	In the Deployment view, because the presentation component is now executing in a different place	f	0	453
1810	In the component-and-connector view, because the connector between the web client and the web server has to change	f	1	453
1811	In the Layer view, because the order of the layers will have to change	f	2	453
1813	The left part of the figure represents a three-layered architecture	f	0	454
1814	The most relevant architectural style in the right part of the figure is shared-data	f	1	454
1815	The system represented in the left part of the figure tends to be non-transactional	f	2	454
1817	Service-oriented architecture to express how clients can access the services	f	0	455
1818	Client-server to express how multiple clients can access the applications	f	1	455
1820	Decomposition to express the different responsibilities assigned to each application	f	3	455
1821	We should always satisfy in the first place the requirements of more important stakeholders (such as the client)	f	0	456
1822	If no order was established among them, we would not know from where should we start the design process	f	1	456
1823	If one of the stakeholders complains that his requirement is not satisfied, we may explain to him that there were other more important requirements first	f	2	456
1838	Active redundancy	f	1	460
1839	Ignore faulty behaviour	f	2	460
1840	Ping/Echo	f	3	460
1841	Consider the requirements not realistic	f	0	461
1842	Apply tactics of defer binding to allow the addition of the new sources of information in initialization time	f	1	461
1844	Consider this requirement as a non architecturally significant requirement	f	3	461
1845	Increasing performance and availability	f	0	462
1846	Increasing availability and decreasing performance	f	1	462
1847	Increasing performance and decreasing availability	f	2	462
1849	Due to its configuration strategy Apache has better performance	f	0	463
1850	Performance was the main concern of the design of the configuration strategy in Nginx	f	1	463
1851	Apache emphasizes the usability quality for system administrators by allowing to split the configuration by different files	f	2	463
1853	It makes no sense to use views of the module viewtype, as they give only a static view of the system	f	0	464
1854	You should use only views of the component-and-connector viewtype, which describe the dynamic aspects of the system	f	1	464
1856	The only views that are relevant to performance requirements are views of the Deployment style	f	3	464
1857	The Work Assignment style	f	0	465
1858	The Client-Server style	f	1	465
1859	The Deployment style	f	2	465
1865	The Peer-to-Peer style	f	0	467
1867	The Client-Server style	f	2	467
1868	The Publish-subscribe style	f	3	467
1874	Using threads ensures that the processing of each request is isolated from the remaining requests	f	1	469
1875	With this approach they may use all of the available cores in multiprocessor machines	f	2	469
1876	They are used for implementing enterprise applications that typically have complex domain logic and, by using threads, it is easier to reuse code from one request to another	f	3	469
1877	An increase resource efficiency tactic	f	0	470
1878	A schedule resources tactic	f	1	470
1880	A manage sampling rate tactic	f	3	470
1881	A decomposition view which represent the module for compare-and-set	f	0	471
1882	A client-server view with non-blocking connectors for the interaction between threads and core data structures	f	1	471
1884	A deployment view which allocate threads to the multi-cores	f	3	471
1886	The solution where the cache is responsible for the eviction has better availability	f	1	472
1887	The solution where the application is responsible for the eviction has better modifiability	f	2	472
1888	The solution where the cache is responsible for the eviction has better performance	f	3	472
1889	Performance	f	0	473
1890	Interoperability	f	1	473
1892	Security	f	3	473
1897	This law highlights the impact of the business on the architecture	f	0	475
1899	This law states that architectures impact on the structure of the organization	f	2	475
1900	This law does not apply to the design of architectures	f	3	475
1901	Becomes unavailable for clients if there is a fault in the hardware of web server (srv-web)	f	0	476
1902	Becomes unavailable for clients if there is a fault in the hardware of database server (srv-db)	f	1	476
1903	Becomes unavailable for clients if there is a fault in the hardware of service server (srv-opc)	f	2	476
1905	Performance is a quality that you have to address at the end of the development process	f	0	477
1906	There is no system which can have good performance and be easily maintainable	f	1	477
1908	The system performance quality has impact on the performance of the execution of tests	f	3	477
1917	They are both modules	f	0	480
1919	The *Request Node* is a component and the *Cache* is a module	f	2	480
1920	The *Request Node* is a module and the *Cache* is a component	f	3	480
2050	By executing in parallel each of the phases of the pipeline corresponding to the processing of a request	f	1	513
2051	By executing in parallel the processing of the various requests	f	2	513
2052	By processing completely each request before moving to the next one, in a sequential process	f	3	513
2054	You need to design a client-server view representing the interaction between the DSL and the servers that execute its commands	f	1	514
2055	You need to design an implementation view to allow system administrators configure the builds	f	2	514
2056	You do not need to change the views because the DSL does not have any architectural impact	f	3	514
2057	A uses view which represent modules for the externalizers	f	0	515
2058	A client-server view which represent the byte stream for transmission across a network	f	1	515
2059	A connector that has the serialization and de-serialization speed qualities	f	2	515
2066	The main quality of the system in the left part of the figure is scalability	f	1	517
2067	The main quality of the system in the right part of the figure is ease of development	f	2	517
2068	The main quality of the system in the left part of the figure is to promote cross-functional teams	f	3	517
2069	Data model to express the stored data formats	f	0	518
2071	Aspects to express the evolution of service interfaces	f	2	518
2072	Publish-subscribe to express how data is shared between services	f	3	518
2073	Performance because all requests will be processed faster	f	0	519
2075	Availability because even if PartB1 is not available partB2 can be provided	f	2	519
2076	Reliability because a single correct read is used to responde to several requests	f	3	519
2085	Is useful only if done (even if only partially) before the system's implementation is concluded, given that the architecture is used for restricting the implementation	f	0	522
2086	Is useful only if done (even if only partially) before the system's implementation is concluded, because if the system is already implemented, its implementation uniquely determines the architecture	f	1	522
2087	Is useful only if done (even if only partially) before the system passes all of the acceptance tests by the client, given that no more requirements changes will take place after that time	f	2	522
2090	The components that manage the communication between the remaining elements in the system	f	1	523
2091	The stakeholders that drive the development of the system	f	2	523
2239	The design of a reusable interface is the stimulus.	f	2	560
2092	The tactics that satisfy the most important requirements for the system	f	3	523
2097	Communicating processes	f	0	525
2098	Communicating processes and shared-data	f	1	525
2099	Communicating processes, shared-data and service-oriented architecture	f	2	525
2101	A module may contain code from different components	f	0	526
2103	A module may execute code from different components	f	2	526
2104	A component may contain code from different modules	f	3	526
2105	They describe general requirements that all systems should try to satisfy	f	0	527
2106	They allow us to build a more robust architecture that satisfies less specific requirements, which address a wider range of situations that may happen in the system	f	1	527
2107	They identify the most important requirements that the system should satisfy	f	2	527
2109	There is a high level of communication between the several modules, and this will cause the system to have a low performance	f	0	528
2110	It is not possible to install the system in more than one machine	f	1	528
2112	It is very hard to explain what the system does, because we need to understand all the execution fluxes	f	3	528
2121	The Peer-to-Peer style	f	0	531
2122	The Client-Server style	f	1	531
2124	The Publish-subscribe style	f	3	531
2125	The Shared data style	f	0	532
2126	The Pipes-and-filters style	f	1	532
2127	The Peer-to-Peer style	f	2	532
2129	Ping/Echo	f	0	533
2130	Heartbeat	f	1	533
2132	Removal from Service	f	3	533
2137	Launch a new process for processing each request	f	0	535
2138	Spawn a new thread for processing each request	f	1	535
2140	Buy a server with high processing power	f	3	535
2141	A Deployment view	f	0	536
2142	A Component-and-Connector view	f	1	536
2143	A Uses view	f	2	536
2145	Increasing performance and availability	f	0	537
2146	Increasing availability and decreasing performance	f	1	537
2148	Increasing scalability and availability	f	3	537
2149	Make a business case for the system	f	0	538
2151	The system design	f	2	538
2152	Documenting and communicating the architecture	f	3	538
2153	Modifiability	f	0	539
2154	Availability	f	1	539
2155	Testability	f	2	539
2157	Performance	f	0	540
2159	Usability	f	2	540
2160	Security	f	3	540
2240	The data input to the system is the stimulus.	f	3	560
2181	Should be avoided because scenarios should describe very concrete situations.	f	0	546
2183	Can omit some of the elements like, for instance, the environment, if they are not relevant for the general scenario.	f	2	546
2184	Is a very reusable scenario that can be used in many different concrete situations.	f	3	546
2185	Can be applied to any kind of availability scenario.	f	0	547
2187	Guarantees that the system will not become unavailable.	f	2	547
2188	Reduces the availability scenario response time because the request occurs twice.	f	3	547
2189	The business aspects of the system are for business architects, not the software architects.	f	0	548
2190	Dealing with the technological aspects of the system should be delayed to the implementation stage of development.	f	1	548
2191	The modeling of a system is not part of the software architect duties.	f	2	548
2193	Martin Fowler disagrees with this definition because we should delay the design decisions and focus on features first.	f	0	549
2195	Martin Fowler complains about this definition because architecture should stress flexibility which can only be necessary later.	f	2	549
2196	Martin Fowler disagrees with this definition because to design an architecture it is not necessary to make any decision.	f	3	549
2197	This view highlights the availability of the system.	f	0	550
2198	This view highlights the performance of the `Image File Storage`.	f	1	550
2200	This view highlights the scalability of `upload` and `dowload` operations.	f	3	550
2225	This is a case of an architectural influence cycle where the feedback cycle resulted in changes on the business.	f	0	557
2226	This is a case of an architectural influence cycle where the feedback cycle resulted in changes on the project.	f	1	557
2228	This is a case of an architectural influence cycle without feedback.	f	3	557
2229	This view highlights the security of the system.	f	0	558
2230	This view highlights the scalability of `upload` and `dowload` operations.	f	1	558
2231	This view highlights the scalability of storage.	f	2	558
2233	This shared understanding can be represented by a set of architectural views.	f	0	559
2235	The system algorithms should be part of the shared understanding.	f	2	559
2236	The shared understanding describes the system from a high-level perspective.	f	3	559
2238	The request to adapt an interface is the stimulus.	f	1	560
2261	This view highlights the availability of the `Image File Storage`.	f	0	566
2263	This view highlights the performance of `upload` operations.	f	2	566
2264	This view highlights the scalability of `upload` and `dowload` operations.	f	3	566
2269	Design an architectural solution together with the stakeholders to be sure that everybody agrees on the resolution of conflits.	f	0	568
2270	Solve the conflicts between requirements by deciding on the best trad-offs the system should support.	f	1	568
2272	Design an architecture that supports all the conflicting requirements and present it to the stakeholders.	f	3	568
2273	The set of structures is needed to support different levels of performance for the system.	f	0	569
2275	The hardware is an example of a software element.	f	2	569
2276	There isn't any relation between the properties of the software elements and the ability to reason about the system.	f	3	569
2278	These tactics are used to prevent the occurence of a fault.	f	1	570
2279	Spare guarantees immediate recover.	f	2	570
2280	Passive redundancy does not work with non-deterministic behavior of request's execution.	f	3	570
2301	It describes an availability scenario because the configuration allows to define redundant virtual servers.	f	0	576
2302	It describes a scalability scenario because it is possible to increment the size of the configuration at a linear cost.	f	1	576
2304	It describes a modifiability scenario because of the cost associated with maintaining the configuration.	f	3	576
2306	The architect cannot backtrack the decomposition decisions she made.	f	1	577
2307	During the design process the number of architecturally significant requirements cannot change.	f	2	577
2308	Contraints cannot be used as requirements for the decomposition process.	f	3	577
2313	It describes a performance scenario where the stimulus is the request of a custom graph.	f	0	579
2314	The scenario is supported by a manage sampling rate tactic because several requests to the same graph return the same result.	f	1	579
2315	It describes a usability scenario where the source of stimulus is a non-technical user.	f	2	579
2317	Has as main goal the reduction of the modules' size.	f	0	580
2318	Results in the creation of a third module that does not have to change when any of the original modules are changed.	f	1	580
2319	Increases the cohesion between the two modules.	f	2	580
2341	Introduce concurrency.	f	0	586
2342	Increase resources.	f	1	586
2344	Maintain multiple copies of computation.	f	3	586
2345	This is a performance scenario because the stimulus is an input, *launches several instances of the system*.	f	0	587
2347	This is not a modifiability scenario because the source of the stimulus cannot be a system administrator.	f	2	587
2348	This is a modifiability scenario and its environment design time.	f	3	587
2350	Bound execution times, and increase resources.	f	1	588
2351	Manage sampling rate, bound queue sizes, and increase resources.	f	2	588
2352	Bound queue sizes, and increase resources.	f	3	588
2353	Only the scenarios that have high architectural impact and high business value should appear in the tree.	f	0	589
2354	A scenario for a power outage should have a low business value because the fault is temporary.	f	1	589
2355	A scenario for a 24 hours x 7 days availability of the system should appear as a leaf of the utility tree.	f	2	589
2357	Manage sampling rate.	f	0	590
2358	Limit event response.	f	1	590
2359	Prioritize events.	f	2	590
2381	This decision does not have any impact on the architecture.	f	0	596
2382	This decision corresponds to a constraint requirement.	f	1	596
2383	This decision needs to be made concrete by an interoperability scenario.	f	2	596
2385	It describes a reliability scenario because the data points for each metric will be split between a buffer and disk.	f	0	597
2386	It describes a performance scenario for the execution of reads.	f	1	597
2388	The tactic used to solve the problem is not manage sampling rate because there isn't any loss of data points.	f	3	597
2389	Increase resource efficiency.	f	0	598
2391	Increase resource efficiency and Increase resources.	f	2	598
2392	Increase resources and Maintain multiple copies of computation.	f	3	598
2393	A security scenario because it allows the introduction of filters to encrypt the messages.	f	0	599
2394	A availability scenario because it allows the introduction of load balancers.	f	1	599
2396	A usability scenario because developers can extend the system without having to modify the nginx core.	f	3	599
2397	A low cost of change may imply a high cost of development.	f	0	600
2398	A low cost of change implies a low cost of development, because changing the code is part of development.	f	1	600
2400	A high cost of change occurs if it is necessary to defer the binding of what needs to be changed.	f	3	600
2421	A client-server style.	f	0	606
2422	A shared-data style.	f	1	606
2424	A blackboard style.	f	3	606
2425	Passive redundancy.	f	0	607
2426	Active redundancy.	f	1	607
2427	Voting.	f	2	607
2429	Its main goal is to establish the reusability qualities of the architecture.	f	0	608
2430	Project managers are not interested in views that use this style because it lacks the necessary level of detail.	f	1	608
2431	Incremental development is a criteria that drives the design of views of this type.	f	2	608
2433	The library approach allows non-java applications.	f	0	609
2435	The server approach implements a local cache.	f	2	609
2436	The library approach does not build a cluster.	f	3	609
2437	A component cannot be decomposed into a set of components and connectors.	f	0	610
2438	A connector cannot be decomposed into a set of components and connectors.	f	1	610
2440	A component can only have a single type of port.	f	3	610
2423	Both, client-server and shared-data styles.	t	2	606
2428	Maintain multiples copies of computation.	t	3	607
2432	There should be at least one view of the system using this architectural style.	t	3	608
2474	Introduce concurrency.	f	1	619
2475	Tailor interface.	f	2	619
2476	Increase resources.	f	3	619
2477	Modifiability.	f	0	620
2478	Availability.	f	1	620
2479	Performance.	f	2	620
2501	The Merge component executes the module merge.	f	0	626
2503	The module main is executed in the Merge component.	f	2	626
2504	The Pipe connectors do not execute any module.	f	3	626
2506	Availability.	f	1	627
2507	Modifiability.	f	2	627
2508	Reliability.	f	3	627
2509	Modifiability, because the Data Accessors do not depend on the data model.	f	0	628
2511	Scalability of write requests, because a transactional system will synchronize the writes among the several repositories.	f	2	628
2512	Confidentially of data, because it can be replicated in several repositories.	f	3	628
2514	Usability.	f	1	629
2515	Performance.	f	2	629
2516	Modifiability.	f	3	629
2541	A decomposition view.	f	0	636
2542	A view of the component-and-connector viewtype.	f	1	636
2543	A view of the component-and-connector viewtype and a deployment view.	f	2	636
2550	Peer-to-peer.	f	1	638
2551	Master-slave.	f	2	638
2552	Pipe-and-filter.	f	3	638
2553	Peer-to-peer.	f	0	639
2554	Shared-data where the Buildbot is the data accessor.	f	1	639
2556	Client-server where the Buildbot is the server.	f	3	639
2581	Decomposition.	f	0	646
2582	Generalization.	f	1	646
2584	Peer-to-peer.	f	3	646
2589	It assigns modules to the hardware.	f	0	648
2590	It cannot assign software elements to virtual servers because they are not hardware.	f	1	648
2591	For each set of software elements there is a single possible assignment to hardwre.	f	2	648
2594	Client-server.	f	1	649
2595	Shared-date.	f	2	649
2596	Generalization.	f	3	649
2597	Client-server to represent performance.	f	0	650
2599	Service-oriented architecture to represent interoperability.	f	2	650
2600	Shared-data to represent modifiability.	f	3	650
2621	The data-shared architectural style is not applied because data is encapsulated inside services.	f	0	656
2623	Modifiability is not a concern of their architecture.	f	2	656
2624	The decouple of data formats does not support scalability because of the transactional properties.	f	3	656
2625	Peer-to-peer.	f	0	657
2626	Shared-data where the Dashboard is the repository.	f	1	657
2627	Client-server where the Dashboard is the client.	f	2	657
2637	Client-server.	f	0	660
2639	Peer-to-peer.	f	2	660
2640	Shared-data.	f	3	660
2777	Work Assignment views	f	0	695
2778	Generalization views	f	1	695
2779	Deployment views	f	2	695
2782	Client-Server	f	1	696
2783	Peer-to-Peer	f	2	696
2784	Uses	f	3	696
2861	Different stakeholders are interested in different views of the system	f	0	716
2786	Do not have a software architecture, because in agile methodologies there is no architectural design phase	f	1	697
2787	Do not have a software architecture, because the practice of refactoring allows changing every part of the system easily	f	2	697
2788	May have a software architecture, but that architecture is not known because it was neither designed nor documented	f	3	697
2789	However, functional requirements do not have any impact on the architecture because the systemic qualities of an architecture are non-functional	f	0	698
2790	The functional requirements have a large impact on the definition of views of the component-and-connector viewtype because each component executes a functionality	f	1	698
2792	The functional requirements can be considered as constraints on the software architecture design	f	3	698
2793	The stimulus is incorrect response	f	0	699
2794	The artefact is the load balancer	f	1	699
2796	The quality it addresses is interoperability	f	3	699
2802	Usability	f	1	701
2803	Availability	f	2	701
2804	Modifiability	f	3	701
2806	Limit event response	f	1	702
2807	Reduce overhead	f	2	702
2808	Bound execution times	f	3	702
2809	Have high throughput	f	0	703
2810	Have low latency	f	1	703
2811	Allow many simultaneous users	f	2	703
2817	Per-to-peer	f	0	705
2818	Shared-data	f	1	705
2819	Communicating processes	f	2	705
2822	The stimulus is a crash and the tactic is retry	f	1	706
2823	The stimulus is an incorrect timing and the tactic is ignore faulty behaviour	f	2	706
2824	The stimulus is incorrect response and the tactic is voting	f	3	706
2829	The quality being addressed is performance and the tactic multiple copies of data	f	0	708
2830	The quality being addressed is performance and the tactic multiple copies of computation	f	1	708
2832	The quality being addressed is availability and the tactic passive redundancy	f	3	708
2834	Client-server style	f	1	709
2835	Shared-data style	f	2	709
2836	Pipe-and-filter style	f	3	709
2841	The shared-data architectural style is not applied because data is encapsulated inside services	f	0	711
2843	Modifiability is not a concern of their architecture	f	2	711
2844	The decouple of data formats does not support scalability because of the transactional properties	f	3	711
2845	an ACID transaction occurs in all the involved applications	f	0	712
2846	a two-phase commit protocol takes place between the involved applications	f	1	712
2847	a ACID transaction occurs in each of the involved applications, but we can not infer which transaction occurs first	f	2	712
2849	The access to two different aggregate instances in the context of the same request does not hinder scalability	f	0	713
2850	This is the solution followed by Twitter client applications	f	1	713
2851	It describes the typical behavior of a microservices system	f	2	713
2854	We just have to show, through component-and-connector views, that the system maintains replicas of the data in different components	f	1	714
2855	We just have to show, through Deployment views, that the *DataNode* component executes in more than one machine of the cluster	f	2	714
2856	We just have to show, through Decomposition views, that there are modules responsible for the replication of file blocks	f	3	714
2857	Active replication and passive replication	f	0	715
2858	Active replication, passive replication, and spare	f	1	715
2860	Quorum, active replication, and passive replication	f	3	715
2801	Performance	t	0	701
2805	Manage sampling rate	t	0	702
2812	May be easily changed to increase their performance	t	3	703
2820	Publish-subscribe	t	3	705
2862	A single view would be too simplistic	f	1	716
2863	The views describe different aspects of the system	f	2	716
3002	The environment is build time	f	1	751
3003	The response is 5 person/month	f	2	751
3004	To implement the above scenario it is necessary to apply a runtime defer binding tactic	f	3	751
3005	Split module	f	0	752
3007	Restrict dependencies	f	2	752
3008	Defer binding	f	3	752
3009	Essential to ensure the system scalability	f	0	753
3011	Essential to ensure the system portability	f	2	753
3012	Essential to facilitate the integration with legacy systems	f	3	753
3014	But they could have used the ping tactic instead without adding any overhead to the NameNode	f	1	754
3015	But the exceptions tactic could have been used as well	f	2	754
3016	To inform other DataNodes about their availability	f	3	754
3017	Is a high-level view of the system with the purpose of understanding what are the system's goals and features	f	0	755
3019	Is a set of guidelines that the developing team should follow in the development of the system	f	2	755
3020	Is a set of diagrams that show the runtime elements of the system and their relationships	f	3	755
3022	Describing a set of steps that a user of the system must perform to accomplish some task	f	1	756
3023	Describing a use case for the system that makes clear what should be the system's responses to each of the user's inputs	f	2	756
3024	Describing the system's features by way of different usage scenarios for it, in which users play the role of actors	f	3	756
3025	The scenario is not correct	f	0	757
3027	The scenario is correct but it is not clear what is the artefact	f	2	757
3028	The scenario is not completely correct because it contains two responses	f	3	757
3029	Only in the Deployment view	f	0	758
3030	Only in the Decomposition view	f	1	758
3031	Only in a component-and-connector view	f	2	758
3034	Availability	f	1	759
3035	Modifiability	f	2	759
3036	Reliability	f	3	759
3037	Manage sampling rate	f	0	760
3038	Limit event response	f	1	760
3039	Reduce overhead	f	2	760
3041	The presentation logic layer, domain logic layer, and data access layer	f	0	761
3042	The traditional web applications, the mashups, and the rich internet applications (RIAs)	f	1	761
3044	The web services layer, the domain logic layer, and the data access layer	f	3	761
3045	It enforces the use of a single implementation language among all applications	f	0	762
3046	The orchestration is in charge of improving the transparent location of service providers	f	1	762
3047	The enterprise service bus coordinates the execution of several services	f	2	762
3049	Work assignment view	f	0	763
3051	Implementation view	f	2	763
3052	Deployment view	f	3	763
3053	Write a single scenario on performance	f	0	764
3055	Write a scenario on performance and a scenario on interoperability	f	2	764
3056	Write a single scenario on interoperability	f	3	764
3057	Module viewtype	f	0	765
3059	Install architectural style of the allocation viewtype	f	2	765
3060	It is not necessary to represent this behavior because it does not describe any qualities	f	3	765
3066	A component-and-connector view	f	1	767
3067	An allocation view	f	2	767
3068	They are not represented by a view	f	3	767
3070	The Decomposition and the Layers styles	f	1	768
3071	The Decomposition and the Uses styles	f	2	768
3072	The Decomposition and the SOA styles	f	3	768
3073	The Decomposition style	f	0	769
3074	The Deployment style	f	1	769
3076	The Work-assignment style	f	3	769
3078	The Uses style	f	1	770
3079	The Layers style	f	2	770
3080	The Aspects style	f	3	770
3086	Use a passive redundancy tactic in the Consumer Web site	f	1	772
3087	Use an active redundancy tactic in the OPC (Order Processing Center) 	f	2	772
3088	Use an active redundancy tactic in the Consumer Web site	f	3	772
3090	The uses view to represent how the mobile device uses the Catalog application	f	1	773
3091	The layered view to include a layer for each type of device	f	2	773
3092	The domain layer of the layered style to represent the types of devices	f	3	773
3097	The file transfers follows the same path of nodes used to identify where the file was located	f	0	775
3098	The peer initiating the request for a file needs to know where the file is located	f	1	775
3099	If a peer providing a file crashes it is necessary to restart downloading the file from the begin	f	2	775
3105	All functionalities can be transactional	f	0	777
3107	It is not necessary to have transactional properties because all data is in memory	f	2	777
3108	Only the isolation property of transactions is supported	f	3	777
3109	When an event is published to the distributed log, the order of delivery to the different subscribing applications is predefined	f	0	778
3033	Performance	t	0	759
3110	When two events are published to the distributed log they are delivered to the different subscribing applications in the same order	f	1	778
3112	The distributed log may not deliver some of the events that are published to their subscribers	f	3	778
3113	It allows high scalability because the data model has only four entities	f	0	779
3115	It allows high scalability because the only synchronized access is to the `ProductId`, so it requires a single contention point	f	2	779
3116	It does not allow high scalability	f	3	779
3118	Work assignment	f	1	780
3119	Decomposition	f	2	780
3120	None, because this description does not describe any architectural aspect of the system	f	3	780
3141	When a new block is created, the first replica is written in the node where the writer is located, to improve availability	f	0	786
3142	When a new block is created, the second replica is not stored in the same rack than the first replica to increase the availability when a Data Node fails	f	1	786
3143	When a new block is created, the third replica is stored in the same rack than the second replica to improve the performance of reads	f	2	786
3145	Depends mostly on the system's functional requirements	f	0	787
3146	Depends more on the architect's experience than on anything else	f	1	787
3147	Should not depend on the skills of the developing team	f	2	787
3149	Describes a concrete quality that a particular system has to implement	f	0	788
3151	Can omit some of the elements like, for instance, the environment, if they are not relevant for the general scenario	f	2	788
3152	Is a very reusable scenario that can be effectively used in many different concrete situations	f	3	788
3153	The writing of a tweet is a synchronous process where different users have a consistent view of the sequence of tweets	f	0	789
3154	A tweet is written in each one of the Twitter's servers	f	1	789
3156	The tweet content is written in the home timeline of each one of the writer's followers	f	3	789
3157	This solution optimizes the performance in terms of the latency of each request	f	0	790
3158	This solution allows an "infinite"increase of the number clients by allowing the inclusion of more Request Nodes	f	1	790
3159	This solution continues to provide service even if a crash occurs in the Data server	f	2	790
3181	The periodic rebuild of the checkpoint is done to increase the availability of the NameNode	f	0	796
3182	The advantage of running the CheckpointNode in a different host is to not degrade the availability of the NameNode during checkpoint construction	f	1	796
3183	The periodic rebuild of the checkpoint improves the performance of the NameNode during normal operation	f	2	796
3185	A non-functional requirement a system has to achieve	f	0	797
3187	What should be the system response in the occurrence of a stimulus	f	2	797
3188	A decomposition of the system that fulfills an architectural quality	f	3	797
3189	The search timeline is the most important business use case for Twitter	f	0	798
3191	The Early Bird server contains the tweet content	f	2	798
3192	The write in the Early Bird server is synchronous, only when it finishes does the user receives the feedback of a successful post	f	3	798
3193	This is right because if you don't the project fails	f	0	799
3194	This is wrong because you can easily change these decisions during the project lifetime	f	1	799
3196	This is wrong because it is against the agile way of thinking the software development process	f	3	799
3197	This solution assures a consistency view to the clients of the data that is written	f	0	800
3198	In this solution the clients invocations have to be synchronous	f	1	800
3199	In this solution the tasks in the queue need to be sequentially processed, only when a task is finished can another start to be processed	f	2	800
3225	Performance	f	0	807
3226	Interoperability	f	1	807
3228	Security	f	3	807
3230	Performance qualities only	f	1	808
3231	Availability qualities only	f	2	808
3232	Performance and security qualities	f	3	808
3233	Can be applied to any kind stimulus in availability scenarios	f	0	809
3235	Can guarantee that the system will not become unavailable	f	2	809
3236	When applied it increases the latency of the availability scenario's response time	f	3	809
3237	Have high throughput	f	0	810
3238	Have low latency	f	1	810
3239	Allow many simultaneous users	f	2	810
3227	Availability (Reliability)	t	2	807
3229	Performance and availability qualities	t	0	808
3234	Is useful to support scenarios where the stimulus is an omission	t	1	809
3240	May be easily changed to increase their storage capacity	t	3	810
3261	Encapsulate the module such that the clients of the module should not be aware of the remote invocations	f	0	816
3263	Refactor the common parts between the business logic and the remote invocation	f	2	816
3264	Increase the semantic coherence between the business logic code and the remote invocation code	f	3	816
3269	The system would respond faster to all the clients' requests	f	0	818
3270	The performance of the system would not change	f	1	818
3272	The system would respond faster to requests made by DataNodes to update the metadata	f	3	818
3277	The Ping/Echo tactic	f	0	820
3278	The Heartbeat tactic	f	1	820
3280	The Removal from Service tactic	f	3	820
3313	This means that in this software system it is not possible to modularize each responsibility in a cohesive module	f	0	829
3314	She should define finer-grained modules where she splits the unassigned responsibility	f	1	829
3316	She should try to use a view of the Layered style and assign this responsibility to a module in the bottom layer that can be used by all the other modules	f	3	829
3345	A change to the uses view to represent that friends can use each other catalog	f	0	837
3346	A change of the layered view to support different presentations, one for each friend	f	1	837
3347	A change of the decomposition view to include a set of new modules with the responsibilities associated with the access control	f	2	837
3357	It is not necessary to have any view of the Data Model architectural style because Facebook information has a very simple structure	f	0	840
3358	It is enough to design a view of the Data Model architectural style at the conceptual level because Facebook information has a very simple structure	f	1	840
3359	It is enough to design a view of the Data Model architectural style at the logical level because the information will be stored in a relational database	f	2	840
3381	Service-oriented architecture, and Client-server	f	0	846
3382	Service-oriented architecture, and Shared-data	f	1	846
3383	Service-oriented architecture, Shared-data, and Peer-to-peer	f	2	846
3389	Represent the hardware infrastructure that allows components to communicate with each other	f	0	848
3391	Represent the dependency relations that exist among the various components	f	2	848
3392	Represent the control flow during an execution of the system	f	3	848
3421	Tiers	f	0	856
3422	Tiers, and Shared-data	f	1	856
3423	Tiers, Shared-data, and Service-oriented architecture	f	2	856
3426	It is always guaranteed that all the published events are received by their subscribing components	f	1	857
3427	The events should be delivered by the same order they are sent	f	2	857
3428	The set of events types are predefined at initialization time	f	3	857
3429	Shared-data and Communicating-Processes	f	0	858
3430	Communicating-Processes	f	1	858
3431	Tiers	f	2	858
3437	A Module viewtype view	f	0	860
3438	A Allocation viewtype view	f	1	860
3440	A Install view	f	3	860
3521	Whenever complex connectors are used in architectural views it is necessary to also document their decomposition.	f	0	881
3522	It is preferable to only design views that do not use complex connectors to increase understandability.	f	1	881
3524	Whenever possible it should be avoided to use complex connectors because developers have difficult to know how to implement them.	f	3	881
3530	Aspects.	f	1	883
3531	Layered.	f	2	883
3532	Data model.	f	3	883
3533	This solution optimizes the performance in terms of the latency of each request.	f	0	884
3534	This solution allows an "infinite"increase of the number clients by allowing the inclusion of more Request Nodes.	f	1	884
3535	This solution continues to provide service even if a crash occurs in the Data server.	f	2	884
3537	Incorporate in the organization's core business the goals of a software house.	f	0	885
3538	Do in-house development.	f	1	885
3540	Reimplement all the information systems of the organization	f	3	885
3541	A failure.	f	0	886
3542	An error.	f	1	886
3544	An input.	f	3	886
3545	Stochastic event.	f	0	887
3546	Overload.	f	1	887
3547	Change level of service.	f	2	887
3549	Split module.	f	0	888
3550	Use an intermediary.	f	1	888
3552	Refactor.	f	3	888
3553	Decomposition style.	f	0	889
3555	Generalization style.	f	2	889
3556	Layered style.	f	3	889
3557	The Decomposition style.	f	0	890
3558	The Decomposition and Uses styles.	f	1	890
3559	The Layered style.	f	2	890
3561	An aggregate can contain a large number of instances.	f	0	891
3563	An aggregate has runtime references to other aggregates.	f	2	891
3564	An aggregate is cluster of domain classes.	f	3	891
3565	Each service can be developed and deployed independently	f	0	892
3566	Easier to scale development	f	1	892
3567	Eliminates any long-term commitment to a technology stack	f	2	892
3569	Aggregate.	f	0	893
3570	Maintain user model.	f	1	893
3571	Maintain task model.	f	2	893
3574	This view shows that the `Adventure Builder Catalog DB` and the `OPC` components should be deployed in the same hardware.	f	1	894
3575	This view **does not** show that the `Adventure Builder Catalog DB` and the `OPC` components can execute behind a firewall.	f	2	894
3576	This view **does not** show that the access to the `web tier` has some security qualities.	f	3	894
3577	Pipe-and-filter.	f	0	895
3578	Maintain multiple copies of data.	f	1	895
3580	Introduce concurrency.	f	3	895
3581	Ignore faulty behaviour tactic	f	0	896
3582	Ping-and-echo tactic	f	1	896
3583	Active redundancy tactic	f	2	896
3585	Cycles in the uses relation between modules are a good sign, because it indicates that several modules should be tested together.	f	0	897
3587	The uses relation should be applied to the coarse-grained modules, because it allows to identify circular dependences.	f	2	897
3588	There isn't any relation with the layered architectural style because the allowed-to-use relation is more generic.	f	3	897
3594	Planning incremental releases of the system.	f	1	899
3595	Estimating the effort needed to implement the system.	f	2	899
3596	Analysing the system's portability and reusability.	f	3	899
3597	All layers are mapped to the application server component.	f	0	900
3598	The presentation and domain logic layers are mapped to the application server component and the data access layer to the repository component.	f	1	900
3599	The presentation layer is mapped to the browser component and the other two layers are mapped to the application server component.	f	2	900
3681	It would reduce the scalability for updates of different orders for the same customer.	f	0	921
3682	Two users would conflict if they attempt to edit different orders for the same customer.	f	1	921
3683	As the number of orders grows it will be increasingly expensive to load the aggregate.	f	2	921
3686	Shadow.	f	1	922
3687	Voting.	f	2	922
3688	Ignore faulty behavior.	f	3	922
3689	Increase resources.	f	0	923
3691	Bound queue sizes.	f	2	923
3692	Introduce concurrency.	f	3	923
3693	We do not need a view of the module viewtype because it is about the runtime properties of the system.	f	0	924
3694	We do not need a view of the allocation viewtype because deployment is automated.	f	1	924
3695	The component-and-connector view should emphasize the performance qualities of systems following the microservices architecture.	f	2	924
3705	Usability and Performance.	f	0	927
3707	Performance.	f	2	927
3708	Modifiability.	f	3	927
3709	It assigns components and connectors to people and teams.	f	0	928
3711	It does not consider the software that is outsourced.	f	2	928
3712	It allows to estimate the cost of hardware.	f	3	928
3713	Performance.	f	0	929
3714	Availability for incorrect responses from the Image File Storage component.	f	1	929
3716	Performance and Availability for incorrect responses from the Image File Storage component.	f	3	929
3717	One.	f	0	930
3718	Two.	f	1	930
3719	Three.	f	2	930
3721	The cost of the modification.	f	0	931
3722	That the integration of a new source will not have any impact on the other modules of the Catalog of DVDs.	f	1	931
3724	That the modification can occur at runtime.	f	3	931
3729	The Decomposition style.	f	0	933
3730	The Client-Server style.	f	1	933
3732	The Communicating Processes style.	f	3	933
3733	This view shows that the processing of orders is done synchronously.	f	0	934
3735	This view shows that bank debits are done asynchronously.	f	2	934
3736	This view shows that the responses from the providers are processed synchronously.	f	3	934
3741	Pipe-and-filter and tiers.	f	0	936
3742	Shared-data and publish-subscribe.	f	1	936
3743	Pipe-and-filter and publish-subscribe.	f	2	936
3745	Guarantees that the redundant data in the client and the server is always synchronized.	f	0	937
3746	Implements an event bus that allows the server to inform the client about new order recommendations.	f	1	937
3748	It completely hides the server faults from the Pad user.	f	3	937
3750	Abstract common services.	f	1	938
3751	Restrict dependencies.	f	2	938
3752	Encapsulation.	f	3	938
3757	The *config* module is not used in the implementation of any component.	f	0	940
3758	The *main* module is used in the implementation of all components.	f	1	940
3760	The *Split* component uses the *to_lower* module for its implementation	f	3	940
3781	The result of decisions that are made at the "upper floors" of the organization	f	0	946
3782	The sole decision of an architect	f	1	946
3784	A set of software elements and their relations	f	3	946
3785	The source of stimulus is the FenixEDU system	f	0	947
3786	The stimulus is periodic	f	1	947
3787	The environment is overloaded	f	2	947
3789	Commercial	f	0	948
3790	Technical	f	1	948
3792	Professional	f	3	948
3793	Availability of the Image Write Service, whenever one of the Image Write Service components crashes	f	0	949
3795	Availability of the Image File Storage, whenever the Image File Storage component crashes	f	2	949
3796	Performance of the Image Write Service	f	3	949
3797	Availability whenever the server running the tasks crashes, the tasks are restarted and eventually finished	f	0	950
3798	Performance of the tasks execution, scheduling of tasks can be optimized for the particular context of the system	f	1	950
3799	Performance of the services being executed by the clients, they can execute other actions while waiting for the response	f	2	950
3821	The synchronous solution requires less memory than asynchronous solution	f	0	956
3823	In the synchronous solution a task can be associated, during its execution, with different execution entities, e.g. thread	f	2	956
3824	In the asynchronous solution a task is always associated, during its execution, with the same execution entity, e.g. thread	f	3	956
3825	Interoperability is a quality that as lower priority than performance	f	0	957
3826	Scalability should be the quality to be achieved first by any architecture	f	1	957
3827	That the use of XML technology for interoperability is not a correct decision	f	2	957
3829	Performance	f	0	958
3830	Availability	f	1	958
3832	Time to market	f	3	958
3833	The number of Image Write Service components should be the same of the number Image Retrieval Service components	f	0	959
3834	The hardware where of Image Write Service components execute should have the same capabilities of the hardware where Image Retrieval Service components run	f	1	959
3835	Both components, the Image Write Service and the Image Retrieval Service, should be designed using an synchronous model of interactions, where a thread is associated with each request	f	2	959
3837	Introduce concurrency	f	0	960
3839	Maintain multiple copies of data	f	2	960
3840	Schedule resources	f	3	960
3861	In the synchronous solution only some of the tasks that are being executed are lost and they have to be resubmitted by the client	f	0	966
3862	In the asynchronous solution the tasks that are being executed are lost and they have to be resubmitted by the client	f	1	966
3864	In the synchronous solution the tasks being executed are finished without requiring the client to resubmitted them	f	3	966
3866	Provides the quality of performance	f	1	967
3867	Provides the quality of modifiability	f	2	967
3868	Does not provide any additional quality	f	3	967
3869	The need to use a two-phase commit protocol	f	0	968
3870	The need to have a tight integration of the development teams	f	1	968
3872	The need to deploy all the microservices simultaneously	f	3	968
3873	Manage sampling rate	f	0	969
3874	Bound execution times	f	1	969
3876	Increase resource efficiency	f	3	969
3878	Reduction of cost is the most important impact of cloud computing in an architecture	f	1	970
3879	Cloud computing has impact on the business but it is not an architectural aspect	f	2	970
3880	Using cloud computing we cannot delay some architectural decisions	f	3	970
3901	Its main goal is to establish the reusability qualities of the architecture.	f	0	976
3902	Project managers are not interested in views that use this style because it lacks the necessary level of detail.	f	1	976
3903	Views of this type are mostly useful to guide the testing of the system.	f	2	976
3905	Task Model tactics.	f	0	977
3906	System Model tactics.	f	1	977
3908	User Model tactics.	f	3	977
3913	Has as main goal the reduction of the modules' size.	f	0	979
3914	Results in the creation of a third module that makes the original modules independent.	f	1	979
3915	Increases the cohesion between the two modules.	f	2	979
3917	Results from a utility tree for performance.	f	0	980
3918	Results from a single availability scenario.	f	1	980
3919	Results from the application of a single ADD iteration.	f	2	980
3945	Programming, if the components execute modules developed by different teams.	f	0	987
3946	Hardware, if there is hardware redundancy.	f	1	987
3947	Operating Systems, if redundant components execute on top of different operating systems..	f	2	987
3953	This is a performance scenario and the measure of the response is 10 minutes latency.	f	0	989
3955	This is not a modifiability scenario because the source of the stimulus cannot be a system administrator.	f	2	989
3956	This is a modifiability scenario and its environment design time.	f	3	989
3985	Each view contains a single architectural style.	f	0	997
3986	Views need to contain more than one architectural style.	f	1	997
3987	A view may not contain any architectural style.	f	2	997
3990	The quality addressed is performance and a limit event response is required to solve the problem.	f	1	998
3991	The quality addressed is availability and a voting design tactic is required to solve the problem.	f	2	998
3992	The quality addressed is performance and a maintain multiple copies of data design tactic is required to solve the problem.	f	3	998
3998	A low cost of change implies a low cost of development, because changing the code is part of development.	f	1	1000
3999	There is no relation between the cost of change and the cost of development.	f	2	1000
4000	The cost of change is higher if it occurs at runtime.	f	3	1000
4636	Prioritize events	f	3	1159
4021	Service-oriented architecture, and Client-server.	f	0	1006
4022	Service-oriented architecture, and Shared-data.	f	1	1006
4023	Service-oriented architecture, Shared-data, and Client-server.	f	2	1006
4025	The Aspects style.	f	0	1007
4026	The Generalisation style.	f	1	1007
4028	The Shared-data style.	f	3	1007
4033	But when the filters are executed sequentially the composition power is reduced.	f	0	1009
4035	But the size of buffers may reduce the composition power.	f	2	1009
4036	And filters do not have to agree on the data formats.	f	3	1009
4037	The view does not address the scenario	f	0	1010
4038	The view addresses the scenario because it separates the `Consumer Website` module from the `OpcApp` module.	f	1	1010
4040	The view addresses the scenario because the `Consumer Website` module does not use the interfaces a new business partner has to implement.	f	3	1010
4062	If the OPC crashes the Consumer Website can continue to provide service in normal mode.	f	1	1016
4063	If the Adventure Catalog BD crashes the Consumer Website can continue to present the Adventure Builder offers.	f	2	1016
4064	If a Bank component is not available the OPC cannot continue to provide service.	f	3	1016
4066	All the published events are received by their subscribing components.	f	1	1017
4067	The events should be received by the same order they are sent.	f	2	1017
4068	The set of events types are predefined at initialization time.	f	3	1017
4069	The Decomposition style.	f	0	1018
4070	The Generalisation style.	f	1	1018
4072	The Aspects style.	f	3	1018
4077	The type of a connector does not depend on the type of its roles.	f	0	1020
4078	Components of different types may have ports of the same type.	f	1	1020
4079	The attachment is a runtime relation which dynamically manages type compliance.	f	2	1020
4101	Client-server.	f	0	1026
4103	Shared-data.	f	2	1026
4104	Peer-to-peer.	f	3	1026
4106	Layer 4.	f	1	1027
4107	In a layered architecture all layers are equally modifiable.	f	2	1027
4108	Modifiability is not made easier by a layered architecture.	f	3	1027
4110	The view addresses the scenario because the uses relation between the `Consumer Website` module and the `OpcApp` module has the require properties.	f	1	1028
4111	The view addresses the scenario because it separates the modules that represent the interfaces a new business partner has to implement.	f	2	1028
4112	The view addresses the scenario because the `Consumer Website` module uses the `gwt` and `waf` modules.	f	3	1028
4113	The file transfer has to follow the same path of nodes used to identify where the file was located.	f	0	1029
4114	The peer initiating the request for a file needs to know where the file is located.	f	1	1029
4115	If a peer providing a file crashes the file will not be downloaded.	f	2	1029
4117	Modifiability.	f	0	1030
4119	Testability and Modifiability.	f	2	1030
4120	Maintainability and Availability.	f	3	1030
4142	A performance scenario associated with the latency of writing data points to disk.	f	1	1036
4143	An availability scenario associated with a fault in the *Carbon* component.	f	2	1036
4144	A usability scenario.	f	3	1036
4145	Simplifies the evolution of the event schema.	f	0	1037
4146	Simplifies the query operations in the event store.	f	1	1037
4148	Provides a programming model developers are familiar with.	f	3	1037
4153	The decomposition was driven by a defer binding tactic.	f	0	1039
4154	The decomposition was driven by a quality that is supported by a restrict dependencies tactic.	f	1	1039
4156	The decomposition was driven by a quality that is supported by an encapsulate tactic.	f	3	1039
4158	Native applications provide better modifiability qualities.	f	1	1040
4159	HTML5 provides better usability qualities.	f	2	1040
4160	Native applications provide better support for working offline.	f	3	1040
4181	A modifiability scenario the *Graphite* system.	f	0	1046
4182	A usability scenario of the *Graphite* system.	f	1	1046
4183	A performance scenario of the *Graphite* system.	f	2	1046
4185	A request for a web page corresponds to a peer-to-peer interaction between all the web components containing the resources.	f	0	1047
4186	Web pages are explicitly cached on the browser to optimize accesses.	f	1	1047
4188	It is possible to customize the number of threads that run in the mobile device.	f	3	1047
4189	She should decide to use a microservices architecture to improve the scalability of the system.	f	0	1048
4190	She should decide to use a modular monolith architecture to reduce the cost of development, because developers will not need to define intermediate states for the transactional execution of the business logic.	f	1	1048
4192	She should give up because it is not possible to have the two approaches in a singe architecture.	f	3	1048
4193	Dynamic Reconfiguration.	f	0	1049
4194	Tiers.	f	1	1049
4196	Install.	f	3	1049
4197	The view illustrates the achievement of a security scenario.	f	0	1050
4198	The view illustrates the achievement of a performance scenario.	f	1	1050
4199	The view results from the implementation of a support user initiative tactic.	f	2	1050
4221	Performance was traded for easy of development to reduce the overall development costs.	f	0	1056
4223	Performance was traded for the availability quality.	f	2	1056
4224	An incremental development was followed, which allowed to have the system in production without being necessary to export all the information to the mainframe.	f	3	1056
4225	A deployment view.	f	0	1057
4226	A work assignment view.	f	1	1057
4228	A install view.	f	3	1057
4229	This generalization was driven by a split module tactic.	f	0	1058
4230	This view fulfills an availability scenario, which defines the expected behavior whenever an external source is not available.	f	1	1058
4232	This view fulfills a modifiability scenario, which states that it should be easy to support the system in new software platforms, e.g. *Windows* or *OS X* .	f	3	1058
4233	A modifiability scenario the *Graphite* system.	f	0	1059
4234	A usability scenario of the *Graphite* system.	f	1	1059
4235	A single performance scenario of the *Graphite* system.	f	2	1059
4237	Does not allow optimizations according to the type of query.	f	0	1060
4238	Does not support independent scalability according to the type of operation.	f	1	1060
4240	Querying the event sourcing becomes more complex.	f	3	1060
4828	Publish-Subscribe.	f	3	1207
4322	Usability.	f	1	1081
4323	Security.	f	2	1081
4324	Availability.	f	3	1081
4325	A view of the Data Model style	f	0	1082
4326	A view of the Layers style	f	1	1082
4328	A view of the Uses style	f	3	1082
4329	By changing the commonalities that are in the children.	f	0	1083
4330	Because the *is-a* relation does not allow reuse of implementation.	f	1	1083
4331	By adding, removing, or changing children.	f	2	1083
4334	With a Deployment view, where the *load balancer* is part of the communication infra-structure used to execute the system	f	1	1084
4335	With a Uses view, representing the existing dependencies between the *load balancer* and the services that it uses	f	2	1084
4336	With a Layers view, where the *load balancer* creates an abstraction layer between who makes the request and who provides the service	f	3	1084
4337	It implements a maintain multiple copies of computation tactic.	f	0	1085
4339	It supports the access to persistent information.	f	2	1085
4340	It implements a maintain multiple copies of data tactic.	f	3	1085
4342	The solution where the cache is responsible for retrieving the missing piece of data from the underlying store has better availability	f	1	1086
4343	The solution where the application is responsible for retrieving the missing piece of data from the underlying store has better modifiability	f	2	1086
4344	The solution where the cache is responsible for retrieving the missing piece of data from the underlying store has better performance	f	3	1086
4345	A change to the uses view to represent that friends can use each other catalog.	f	0	1087
4346	A change of the layered view to support different presentations, one for each friend.	f	1	1087
4347	A change of the decomposition view to include the responsibilities associated with the access control.	f	2	1087
4349	The Shared Data style	f	0	1088
4350	The Pipes-and-filters style	f	1	1088
4352	The Client-Server style	f	3	1088
4354	Planning incremental releases of the system	f	1	1089
4355	Estimating the effort needed to implement the system	f	2	1089
4356	Analysing the system's portability and reusability	f	3	1089
4357	Client-server and Repository.	f	0	1090
4358	Repository and Publish-subscribe.	f	1	1090
4359	Publish-subscribe and Repository.	f	2	1090
4361	Interoperability	f	0	1091
4362	Modifiability	f	1	1091
4363	Performance	f	2	1091
4365	but this reduces reliability because de webapp components do not access the most recent data	f	0	1092
4366	but it reduces performance, anyway, because the buffer components easily overflow	f	1	1092
4368	and it improves security because the buffer is protected agains attacks	f	3	1092
4369	A *web services* architecture	f	0	1093
4370	A Client-Server architecture, where the *mashup* is the client and the various sources are the servers	f	1	1093
4372	A Publish-Subscribe architecture, where the various sources publish events with the changes made and the *mashup* subscribes those events	f	3	1093
4374	Increase Resources	f	1	1094
4375	Introduce Concurrency	f	2	1094
4376	Maintain Multiple Copies of Computation	f	3	1094
4378	Support user initiative tactic.	f	1	1095
4379	Maintain multiple copies of data tactic.	f	2	1095
4380	Conflict detection tactic.	f	3	1095
4381	Performance was traded for easy of development.	f	0	1096
4383	Performance was traded for the modifiability quality.	f	2	1096
4384	An incremental development was followed, which allowed to have the system in production without being necessary to export all the information to the mainframe.	f	3	1096
4385	This decomposition is the only possible of the original domain model.	f	0	1097
4387	This decomposition implies that products will frequently change their unique identification.	f	2	1097
4388	All the above.	f	3	1097
4389	These tactics cannot be applied in conjunction with the self-test tactic.	f	0	1098
4390	These tactics are used to prevent the occurrence of a fault.	f	1	1098
4392	In ping/echo the components have the initiative to start the interaction.	f	3	1098
4393	Only views of the component-and-connector viewtype are needed	f	0	1099
4395	Only views of the component-and-connector viewtype and allocation viewtype are needed	f	2	1099
4396	Views of the module viewtype are not needed	f	3	1099
4397	When the environment is design time it means that the change should be done before the system enters into production	f	0	1100
4398	When the environment is build time it means that it is necessary to codify a new module that is added by rebuilding the system	f	1	1100
4400	When the environment is runtime the cost of doing the change is higher than in the other environments	f	3	1100
4482	Can use the operations defined in the lower layer, but not the ones defined inthe upper layer	f	1	1121
4483	Can use the operations defined in the upper layer, but not the ones defined inthe lower layer	f	2	1121
4484	Should use some operation defined in the lower layer	f	3	1121
4489	It corresponds to a particular case of a specialization in a generalization view.	f	0	1123
4490	It represents a relation between a connector's role and a port of one of its internal components.	f	1	1123
4492	It represents a relation between a component's port and a connector's role.	f	3	1123
4494	To promote the use of a common communication protocol for all the remaining components of the system	f	1	1124
4495	To increase the performance of the interaction between the components of the system	f	2	1124
4496	To create a strong coupling between the various services provided by the organization	f	3	1124
4497	When a peer connects to the network it establishes connections with all other peers in the network	f	0	1125
4499	When a peer receives a connection it sends all its files to the peer connecting it	f	2	1125
4500	The behavior described in the sentence can be represented in a view where the tier architectural style is used	f	3	1125
4501	The development team is the main stakeholder interesting in these views.	f	0	1126
4502	It assigns modules to files.	f	1	1126
4503	It is completely independent of the deployment architectural style.	f	2	1126
4506	The style of the connector used to represent the interaction between the browser and the web server changed	f	1	1127
4507	The browser is now a component of a different type	f	2	1127
4508	That evolution did not have any consequences on the software architecture of a web application	f	3	1127
4509	Prevent a fault in hardware.	f	0	1128
4510	Prevent a fault in software.	f	1	1128
4511	Prevent a fault in a process.	f	2	1128
4513	Manage sampling rate	f	0	1129
4515	Prioritize events	f	2	1129
4516	Bound execution time	f	3	1129
4517	Split module	f	0	1130
4518	Encapsulate	f	1	1130
4520	Defer binding	f	3	1130
4522	Two users would not conflict if they attempt to edit different orders for the same customer.	f	1	1131
4523	The increase of the number of orders would not have impact on the load the aggregate.	f	2	1131
4524	All the above.	f	3	1131
4525	Does not allow optimizations according to the type of query.	f	0	1132
4526	Does not support independent scalability according to the type of operation.	f	1	1132
4528	Does not support joins.	f	3	1132
4529	A module contains the code that executes in a single component and a component executes the code of a single module	f	0	1133
4530	A module contains the code that can execute in several components and a component executes the code of a single module	f	1	1133
4531	A module contains the code that executes in a single component and a component can execute the code of several modules	f	2	1133
4534	Limit event response	f	1	1134
4535	Prioritize events	f	2	1134
4536	Bound execution times	f	3	1134
4538	The uses view to show the coupling between the different platforms.	f	1	1135
4539	The uses view to show the uses relationships between the different platforms.	f	2	1135
4540	The data model view to represent each one of the platforms.	f	3	1135
4541	The view does not address the scenario	f	0	1136
4543	The view addresses the scenario because it separates the modules that represent the interfaces a new business partner has to implement	f	2	1136
4544	The view addresses the scenario because the `Consumer Website` module uses the `gwt` and `waf` modules	f	3	1136
4545	Task Model	f	0	1137
4546	System Model	f	1	1137
4548	User Model	f	3	1137
4549	Usability e Modifiability	f	0	1138
4551	Availability e Usability	f	2	1138
4552	Availability e Performance	f	3	1138
4553	A module view of the decomposition style.	f	0	1139
4555	A component-and-connector view of the service-oriented architecture style.	f	2	1139
4556	A module view of the uses style.	f	3	1139
4557	Suffered from featuritis, because the architect decided to delay the difficult parts for latter in the development.	f	0	1140
4558	Did not suffer from featuritis.	f	1	1140
4560	Suffered from featuritis, but it had no impact on the final development.	f	3	1140
4621	Usability and Performance	f	0	1156
4623	Performance	f	2	1156
4624	Testability	f	3	1156
4625	Performance	f	0	1157
4626	Security	f	1	1157
4628	Security and modifiability	f	3	1157
4629	Performance	f	0	1158
4630	Availability	f	1	1158
4631	Usability	f	2	1158
4634	Limit event response	f	1	1159
4635	Introduce concurrency	f	2	1159
4637	Maintain multiple copies of data	f	0	1160
4639	Bound execution times	f	2	1160
4640	Reduce overhead	f	3	1160
4701	Performance.	f	0	1176
4702	Interoperability.	f	1	1176
4704	Security.	f	3	1176
4705	Limit event response	f	0	1177
4706	Maintain multiple copies of computation	f	1	1177
4707	Maintain multiple copies of data	f	2	1177
4709	Component	f	0	1178
4710	Module	f	1	1178
4712	None of the above	f	3	1178
4713	Manage sampling rate	f	0	1179
4714	Limit event response	f	1	1179
4716	Maintain multiple copies of computation	f	3	1179
4718	Schedule resources	f	1	1180
4719	Bound execution times	f	2	1180
4720	Increase resource efficiency	f	3	1180
4742	Increase resources.	f	1	1186
4743	Increase resource efficiency.	f	2	1186
4744	Maintain multiple copies of data.	f	3	1186
4745	Manage sampling rate tactic.	f	0	1187
4747	Introduce concurrency tactic.	f	2	1187
4748	Schedule resources tactic.	f	3	1187
4749	When the modification should occur.	f	0	1188
4750	The features that will be implemented.	f	1	1188
4752	Defer binding.	f	3	1188
4753	Is driven by functional requirements.	f	0	1189
4754	Is done in a single step, after all the tactics were identified.	f	1	1189
4755	Is a top-down process where a initial decomposition is chosen and it is successively decomposed without changing the initial decisions.	f	2	1189
4757	Is applied only once at the beginning of the architectural design process.	f	0	1190
4759	Is mostly driven by the security attribute quality.	f	2	1190
4760	Follows a bottom-up decomposition process of the system.	f	3	1190
4781	Ping/Echo.	f	0	1196
4782	Retry.	f	1	1196
4784	Passive Redundancy.	f	3	1196
4786	This ASR requires a specific architectural design because it profoundly affects the architecture.	f	1	1197
4787	The cost of meeting the ASR after development starts is too high.	f	2	1197
4788	Any ASR that has a high business value cannot have a low architecture impact because it needs to be supported by the architecture.	f	3	1197
4790	The invoked function may not have any input parameter.	f	1	1198
4791	The invoked function may not have any output parameter.	f	2	1198
4792	The invoked function may not have both any input parameter nor any output parameter.	f	3	1198
4793	Maintain multiple copies of data tactic.	f	0	1199
4794	Introduce concurrence tactic.	f	1	1199
4795	Increase resource efficiency tactic.	f	2	1199
4797	Maintain task model	f	0	1200
4798	Maintain user model	f	1	1200
4800	Aggregate	f	3	1200
4821	Whenever complex connectors are used in architectural views it is necessary to also document their decomposition.	f	0	1206
4822	It is preferable to only design views that do not use complex connectors to increase understandability.	f	1	1206
4824	Whenever possible it should be avoided to use complex connectors because developers have difficult to know how to implement them.	f	3	1206
4825	Peer-to-Peer.	f	0	1207
4827	Client-Server.	f	2	1207
4823	If there is some technology available that implements the complex connectors, according to its expected qualities, it is not necessary to document their decomposition.	t	2	1206
4826	Pipe-and-Filter.	t	1	1207
4830	It cannot be applied when the system includes legacy systems.	f	1	1208
4831	Its enterprise service bus cannot support asynchronous communication between the components.	f	2	1208
4832	The typical communication pattern is point-to-point.	f	3	1208
4833	The modules inside a layer cannot use other modules in the same layer	f	0	1209
4834	A layer cannot call the layer above	f	1	1209
4836	It is possible to have a circular allowed-to-use relationship between several layers	f	3	1209
4838	It should always consider the physical detail level	f	1	1210
4839	The logical detail level should only be used when the target of implementation is a relational database	f	2	1210
4840	Only the conceptual level is required, the other two levels of detail are optional	f	3	1210
4861	Applies layers to tiers.	f	0	1216
4863	Is an extension of the Client-Server architectural style.	f	2	1216
4864	Defines tiers as components.	f	3	1216
4865	It imposes restrictions on which uses relationships may exist between the system's modules	f	0	1217
4866	It makes it easier to create generalization relationships between the system's modules	f	1	1217
4868	It allows the decomposition of each of the system's modules into finer grained modules	f	3	1217
4869	All the peers are equal.	f	0	1218
4870	Any peer can access any other peer.	f	1	1218
4871	Peers are only used to share files.	f	2	1218
4873	It is not necessary to have any view of the Data Model architectural style because Facebook information has a very simple structure.	f	0	1219
4874	It is enough to design a view of the Data Model architectural style at the conceptual level because Facebook information has a very simple structure.	f	1	1219
4875	It is enough to design a view of the Data Model architectural style at the logical level because the information will be stored in a relational database.	f	2	1219
4878	A component type is made of a single architectural style.	f	1	1220
4879	Only components can be associated with application-specific types.	f	2	1220
4880	A component-and-connector view can only use a single architectural style.	f	3	1220
4901	One view of the component-and-connector viewtype and another of the deployment style.	f	0	1226
4903	Two views of the communicating processes style.	f	2	1226
4904	A view of the aspects style.	f	3	1226
4905	The aggregates publishes the event in a message broker and subscribes to the published event.	f	0	1227
4906	Using the database of the aggregate as a temporary message queue.	f	1	1227
4907	Using event sourcing.	f	2	1227
4909	A deployment view.	f	0	1228
4910	A work assignment view.	f	1	1228
4912	A install view.	f	3	1228
4913	The layered view to support a new specific layer for the customization of the catalog.	f	0	1229
4914	The layered view to accommodate a new layer for each kind of catalog, which other layers may use.	f	1	1229
4915	The data model view in order to define entities for each kind of catalog.	f	2	1229
4918	Communicating Processes.	f	1	1230
4919	Repository.	f	2	1230
4920	Pipes-and-Filters.	f	3	1230
4941	Client-server.	f	0	1236
4943	Repository.	f	2	1236
4944	Pipes-and-Filters.	f	3	1236
4945	The communicating processes style.	f	0	1237
4947	The communicating processes style and the pipes-and-filters style.	f	2	1237
4948	The dynamic reconfiguration style.	f	3	1237
4949	The communicating processes.	f	0	1238
4950	Pipes-and-filters.	f	1	1238
4952	Dynamic reconfiguration.	f	3	1238
4953	Components are allocated to persons and teams.	f	0	1239
4955	Components and modules are allocated to persons and teams.	f	2	1239
4956	None of the above.	f	3	1239
4958	The uses view to represent how the mobile device uses the Catalog application.	f	1	1240
4959	The layered view to include a layer for each type of device.	f	2	1240
4960	The domain layer of the layered style to represent the types of devices.	f	3	1240
6	Should be captured in scenarios, as the requirements for quality attributes, and be taken into account in the design of the software architecture	t	1	2
9	The *decomposition* and *uses* styles, which allow us to show how dependent a certain module is of other parts of the system	t	0	3
16	To replace the machine used to run the server component by a more powerful machine that meets the new performance requirements, keeping only a server component running	t	3	4
20	Even though each viewtype addresses different aspects of a system, there are relationships among all of them	t	3	5
31	A subset of the requirements that correspond to the most important business goals, regardless of whether they have conflicts among them or not	t	2	8
33	The *Peer-to-Peer* style	t	0	9
40	The *Shared data* style	t	3	10
44	This change manifests itself on the relationship between the system's modules and components	t	3	11
45	The *Communicating Processes* style	t	0	12
51	*Decomposition* and *Implementation* views	t	2	13
54	*Decomposition* and *Uses* views	t	1	14
59	The *Layers* style	t	2	15
64	the *Deployment* and *Layers* styles	t	3	16
66	Views of the Module viewtype	t	1	17
69	To control and to reduce the interface exposed by the domain logic layer, thereby increasing the modifiability of that layer	t	0	18
76	To keep a record of changes made to the data during a business transaction and to coordinate the writing of these changes to the database	t	3	19
79	To prevent data inconsistencies when there are multiple accesses within the same business operation to the same entity	t	2	20
83	Stakeholders do not mind if two simultaneous reads on the same file by two different applications may return different values	t	2	21
86	The availability quality is more important, thus performance is addressed afterwards and depends on the tactics used for availability	t	1	22
91	Passive replication and spare	t	2	23
94	State Resynchronization	t	1	24
97	Authenticate users and authorize users	t	0	25
108	That provides a set of complete and cohesive services	t	3	27
111	But it needs to be complemented, for each uses relationship, with the level of coupling	t	2	28
113	The call's results may not have impact on the correct execution of the caller module	t	0	29
120	When it is not possible to satisfy all of the requirements optimally, we should be aware of their relative importance so that we may find a solution that corresponds to a satisfactory trade-off	t	3	30
123	The Peer-to-Peer style	t	2	31
125	A Client-Server architecture, where the DataNode is the Client and the NameNode is the Server	t	0	32
132	The availability guarantee may be given by the usage of an adequate connector between the HDFS Client and the DataNodes	t	3	33
134	The Communicating Processes style	t	1	34
141	To control and to reduce the interface exposed by the domain logic layer, thereby increasing the modifiability of that layer	t	0	36
147	That is the recommended solution if there is a control flow that involves the choreography of both components	t	2	37
162	Essential to reduce costs whenever there is a fault in a hardware element	t	1	41
167	Allows the creation of checkpoints using the information that it gradually receives from the *NameNode*	t	2	42
169	Performance and availability qualities	t	0	43
173	Increases the system modifiability whenever it is necessary to change the placement policy	t	0	44
178	But it would imply an *overhead* in the *NameNode*	t	1	45
183	This script is a module that implements a modifiability tactic	t	2	46
188	Availability and security	t	3	47
193	Means that it may be difficult to design incremental testing	t	0	49
197	Is that the *Allowed to Use* relation defines a restriction for the possible *Uses* relations between modules belonging to different layers	t	0	50
203	Using a view of the component-and-connector viewtype	t	2	51
215	The application continues to have a three-tiered architecture, where one of the tiers is now the HDFS system	t	2	54
220	The Client Server style	t	3	55
222	May affect the data access layer because each pattern puts different requirements on the interface of that layer	t	1	56
228	All components may execute in all machines	t	3	57
239	We may need views of the component-and-connector viewtype and of the Deployment style	t	2	60
244	The researchers, because they wanted to use the system to validate their research	t	3	61
245	As modules of the system	t	0	62
252	Satisfied usability requirements of the system	t	3	63
255	The portability, because the RTS creates an abstraction layer that hides some of the details of the operating system	t	2	64
259	To facilitate changing the phases used in the compilation process, thereby making the compiler more modifiable	t	2	65
264	It allows the development of systems with Peer-to-Peer, Client-Server, or Publish-Subscribe architectures	t	3	66
267	The Deployment style	t	2	67
270	Facilitates the addition of new messaging patterns	t	1	68
274	To increase the throughput of the system when it is overloaded	t	1	69
279	The Communicating Processes style	t	2	70
284	To allow more simultaneous connections than Apache	t	3	71
286	They wanted to have a more efficient use of the computational resources	t	1	72
295	To make the system faster	t	2	74
298	The Communicating Processes style	t	1	75
304	Increases both the availability and the capacity	t	3	76
307	In the Component-and-Connector view, because components and connectors need to be changed	t	2	77
312	Increasing the fault tolerance of the system	t	3	78
316	The execution of the previously existing layers is split between the two new tiers, and new intermediate layers may be needed	t	3	79
322	The domain logic layer was implemented with the Transaction Script pattern	t	1	81
327	The Uses style	t	2	82
344	The Decomposition style	t	3	86
345	In the Deployment view	t	0	87
351	To allow testing and validating the software architecture in the early development stages	t	2	88
359	Typically gives rise to more modules than what we would have if not using this style	t	2	90
363	To launch a worker thread for each core, to maximize the core usage and to minimize the need for synchronization among threads	t	2	91
368	Module and component-and-connector views	t	3	92
371	It has less performance, because the *broker* introduces greater latency in the communication	t	2	93
373	Views of the Generalization style	t	0	94
377	Usability	t	0	95
384	The Communicating Processes style	t	3	96
387	The Shared data style	t	2	97
391	Each *worker* is responsible for various connections, processing all requests from those connections	t	2	98
393	By interleaving the various processing phases of each request in a sequential process	t	0	99
400	Be able to reduce the amount of memory needed for each connection	t	3	100
431	The Peer-to-Peer style	t	2	108
433	The performance decreases	t	0	109
443	To move from an anemic domain model to a rich domain model	t	2	111
448	Views of the Component-and-Connector and Allocation viewtypes	t	3	112
462	The Client-Server style	t	1	116
472	None of the other options solves the problem	t	3	118
473	The presentation logic layer and how it relates with the underlying layer changed	t	0	119
478	A grouping of components	t	1	120
485	Maintain Multiple Copies of Computation	t	0	122
493	Performance, availability, and usability	t	0	124
497	Client-Server e Repository	t	0	125
501	By using a Timestamp tactic	t	0	126
506	May stop accepting writes	t	1	127
509	Increase Resource Efficiency	t	0	128
515	Limit Access	t	2	129
517	Introduce Concurrency	t	0	130
523	Dynamic Creation and Destruction	t	2	131
528	It does not depend on a proprietary service	t	3	132
532	It can change during the execution of each instance of Chrome	t	3	133
533	Maintain User Model tactic	t	0	134
540	Limitations of the concurrent access to files	t	3	135
541	Increase the modifiability quality, because the new user interface was implemented using the REST interface	t	0	136
548	An object tree to simplify the processing of each filter	t	3	137
551	Does not guarantee the FIFO delivery of messages, some messages may be delivery by a different order	t	2	138
555	Dynamic Creation and Destruction	t	2	139
560	All the previous options	t	3	140
562	Differ on the emphasis on production and development phases of the software process	t	1	141
568	Modifiability and Performance	t	3	142
569	Defer Binding	t	0	143
575	Both, pessimistic and optimistic, concurrency control policies can be used	t	2	144
581	Detect and Recover from the attack	t	0	146
590	Only depend on the type of events	t	1	148
596	They are not implemented by a usability tactic	t	3	149
603	Reliability	t	2	151
615	Prioritize performance and availability over functionality	t	2	154
620	Passive Redundancy and Maintain Multiple Copies of Computation	t	3	155
622	By the load balancer	t	1	156
627	Degradation	t	2	157
631	Increase Resources Efficiency	t	2	158
636	Security, Performance, Usability and Mobility	t	3	159
642	The *browser* needs to make less requests to the server	t	1	161
646	Communicating-Processes	t	1	162
649	Uses the Introduce Concurrency tactic	t	0	163
658	External applications can administrate the GNU Mailman mailing lists	t	1	165
663	Pipes-and-Filters	t	2	166
665	Data Model	t	0	167
669	The quality of Performance	t	0	168
673	Security	t	0	169
679	Module and Component-and-Connector	t	2	170
681	Modifiability and Interoperability	t	0	171
686	Aspects	t	1	172
690	User Model	t	1	173
696	It is necessary that each object has a unique identifier	t	3	174
700	Exception Detection	t	3	175
704	In what concerns the notification, the Model module does not use the Observer module	t	3	176
707	There isn't any predefined order to design Uses and Layered views	t	2	177
711	May contain several architectural styles, but only if they are of the same viewtype	t	2	178
715	Once applied in a view may be necessary to change the Decomposition view	t	2	179
717	It is an advantage for programmers that the transactional behavior is transparently provided	t	0	180
842	The Domain Model pattern to reduce the interface of the Domain Logic layer to a controlled set.	t	1	211
847	The Domain Model pattern.	t	2	212
853	The Requirements function is part of the Design module.	t	0	214
857	A Condition Monitoring tactic for the Availability quality.	t	0	215
863	The stimulus for scenarios of the Security quality.	t	2	216
871	The environment is design time.	t	2	218
876	Increase resource efficiency.	t	3	219
877	Create a decomposition where there is a module corresponding to the Windows OS and another one for the Mac OS X, each one responsible for containing the OS-specific code.	t	0	220
885	We have to use a Repository component-and-connector style.	t	0	222
890	Client-server in the first case and Peer-to-peer in the second.	t	1	223
896	Is driven by a trade-off among the stakeholders needs.	t	3	224
898	May be responsible for the Performitis problems of architectures.	t	1	225
908	The location information is correctly included with a probability of 99.99% is the response measure.	t	3	227
916	A data model view and a component-and-connector view using a shared-data style.	t	3	229
919	Shared-data style.	t	2	230
922	The domain only needs CRUD (Create, Read, Update, and Delete) operations.	t	1	231
925	It is necessary to design two deployment views, one for each deployment option.	t	0	232
931	Increase resource efficiency tactic for performance, because it reduces the need of upfront calculus/computation on new clients.	t	2	233
934	The server propagates local commands and cursor movements to the clients, and keeps the snapshots for the initialization of new clients.	t	1	234
939	Testability and Modifiability.	t	2	235
1088	All of the above	t	3	272
1090	The Active Record pattern	t	1	273
1097	Ping-and-echo requires the availability monitor to know the addresses of the components it is monitoring	t	0	275
1104	It is necessary to use the encrypt data tactic to encrypt the information on the client web browser, before it is send to the web server	t	3	276
1112	A scenario for usability associated with a support user initiative tactic	t	3	278
1113	Multiple copies of computation	t	0	279
1117	Decomposition view	t	0	280
1124	To talk with the people that developed the system to know what they did and why they did it	t	3	281
1127	Architecture is the design that gets harder to change as development progresses	t	2	282
1132	None of the above	t	3	283
1133	Represent different architectural qualities and they may not be all represented in a single view	t	0	284
1140	When it is not possible to satisfy all of the requirements optimally, we should be aware of their relative importance so that we may find a solution that corresponds to a satisfactory trade-off	t	3	285
1143	Both, a module and a component	t	2	286
1147	Generalisation to represent an abstraction common to all interfaces and keep API-specific details in child modules	t	2	287
1151	Development because it is not possible to do incremental development	t	2	288
1153	Performance, because it describes what is the response to REST API calls	t	0	289
1158	Increase resource efficiency tactic	t	1	290
1163	Testability, because of the logic complexity	t	2	291
1172	A communication processes style	t	3	293
1174	It is necessary to change the connector between the web clients and the web servers, in the component-and-connector view, to show the semantics that is provided by the load-balancer	t	1	294
1178	They have to process very large amounts of data in each request	t	1	295
1189	A generalisation architectural style	t	0	298
1195	Subscribes to cursor position events	t	2	299
1200	All of the above	t	3	300
1213	This shared understanding is what distinguishes architecture from design.	t	0	304
1221	Frank Buschmann is referring to some possible consequences of the modifiability quality.	t	0	306
1228	A solution for any quality in isolation may lead to a biased architecture.	t	3	307
1239	Is focused on creating common generalizations of several systems.	t	2	310
1244	To break such misunderstanding and mistrust the architecture has to make explicit the stakeholders needs.	t	3	311
1247	Is a baseline architecture that allows to experiment with the most significant architectural requirements.	t	2	312
1259	Are unable to distinguish architecture from design.	t	2	315
1262	A solution to this problem is to prioritize the system qualities.	t	1	316
1268	Professional and Technical Contexts.	t	3	317
1274	Availability and Performance.	t	1	319
1278	Tries to guarantee that the final system will have the qualities aimed by the architecture.	t	1	320
1282	Availability.	t	1	321
1287	Can be used as the source of a stimulus in a scenario.	t	2	322
1291	Is a design tactic for a scenario where the source of stimulus are technical users.	t	2	323
1295	Limit exposure.	t	2	324
1304	The stimulus and the response should be always present.	t	3	326
1309	The quality addressed is availability.	t	0	328
1315	Verify message integrity.	t	2	329
1318	Introduce concurrency.	t	1	330
1323	Interoperability.	t	2	331
1328	Exception prevention.	t	3	332
1329	A Maintain Multiple Copies of Computation design tactic in Carbon.	t	0	333
1335	Separate entities, to allow the use of more strict tactics on the sensitive data.	t	2	334
1337	Maintain user model tactic.	t	0	335
1342	May be associated to other tactics to deal with a single stimulus.	t	1	336
1345	This situation corresponds to the use of the degradation availability tactic.	t	0	337
1350	The quality addressed is modifiability.	t	1	338
1355	Detect and Recover.	t	2	339
1372	Usability.	t	3	343
1373	Interoperability.	t	0	344
1381	Business scenario.	t	0	346
1390	Modifiability.	t	1	348
1396	Support system initiative.	t	3	349
1400	Allows to identify modules for which the development team does not have the required implementation competences.	t	3	350
1404	Contains the business value and the architectural impact of architecturally significant requirements.	t	3	351
1407	The architect have to decide on the cost/benefit of designing an architecture that supports this ASR.	t	2	352
1411	Availability.	t	2	353
1415	Availability.	t	2	354
1421	Business scenario.	t	0	356
1442	Applying the decomposition style to some of the modules in the loop chain.	t	1	361
1449	Limit exposure.	t	0	363
1456	The attachment between components and connectors only depends on their ports and roles types.	t	3	364
1468	It is necessary to design a view of the Data Model architectural style at the physical level to deal with performance issues of the access to data.	t	3	367
1471	Increase resource efficiency.	t	2	368
1476	The required quality associated with the connector is supported by existing and well-know technology.	t	3	369
1477	She can define a variant of this style with asynchronous communication by allowing the client to register callbacks that the server calls at specific times.	t	0	370
1483	Allows incremental development because the possible increments of functionally can be inferred from use dependencies.	t	2	371
1485	Relates a view of the Uses style with a view of the Data Model style.	t	0	372
1489	Multiple copies of computation and Passive redundancy tactics.	t	0	373
1495	A module interface cannot be replicated but component ports can.	t	2	374
1498	It is possible to have redundant servers.	t	1	375
1508	Condition monitoring.	t	3	377
1509	It is possible to integrate a new data accessor without changing the other data accessors.	t	0	378
1520	This means that the modules inside a layer are likely to be ported to a new application together.	t	3	380
1527	Can use a Service Registry to improve transparency of location of service providers.	t	2	382
1529	In the view there are multiple instances of the `Queue` component.	t	0	383
1548	It encapsulates applications through well-defined interfaces, decouples the coordination of the interaction among applications from the applications themselves, and improves transparency of location of service providers.	t	3	387
1550	There is a interface delegation relation between the `read` port of `Queue` and the `query` port of `Carbon`.	t	1	388
1554	Implementation style.	t	1	389
1562	Restrict the communication between components because, for instance, a group of components should be located in the same hardware.	t	1	391
1571	Install style.	t	2	393
1577	Memcached can be considered a sub-module of the Store Graphs module.	t	0	395
1589	Buffering can be considered a sub-module of the Store Graphs module.	t	0	398
1594	A deployment view.	t	1	399
1634	An interface delegation is missing in the picture to represent the : TableEditor broadcasting the cursor position through the Pub port.	t	1	409
1640	May not even exist, only record sets are used.	t	3	410
1641	Amazon Silk predicts accesses based on the information gathered for all Silk users.	t	0	411
1647	There is a ThousandParsec connector which can be decomposed into a set of components and Request/Reply connectors.	t	2	412
1649	Was taken because HTML5 provides better portability qualities.	t	0	413
1653	The server implements the : Repository component and the : Broadcast connector.	t	0	414
1657	Table Data Gateway and Row Data Gateway.	t	0	415
1662	Google Chrome predictor takes into consideration the amount of available cache.	t	1	416
1667	As a module but not included in the RulesSet subtree.	t	2	417
1669	Two distinct unidirectional connectors.	t	0	418
1674	The Parser module is part of the code executed by the : Sheet component.	t	1	419
1680	Active Record and Data Mapper.	t	3	420
1801	The stimulus is to integrate reports from a variety of test tools	t	0	451
1807	Performance and Reliability	t	2	452
1812	In the mapping between layers of the system and the components where they execute	t	3	453
1816	The system represented in the right part of the figure tends to have good modifiability	t	3	454
1819	Tiers to express that different applications define their own contexts	t	2	455
1824	When it is not possible to satisfy all of the requirements optimally, we should be aware of their relative importance so that we may find a solution that corresponds to a satisfactory trade-off	t	3	456
1837	Retry	t	0	460
1843	Identify what should be the common and specific parts of the module responsible for the interaction with the external sources, before interacting again with the stakeholders	t	2	461
1848	Increasing performance, scalability and availability	t	3	462
1852	Nginx emphasizes the usability quality for system administrators by reducing the number or errors	t	3	463
1855	You may need to use views of the three viewtypes	t	2	464
1860	The Communicating Processes style	t	3	465
1866	The Pipes-and-filters style	t	1	467
1873	Launching a new process for processing each request is too expensive	t	0	469
1879	A multiple copies of computation tactic	t	2	470
1883	A communicating-processes view with non-blocking connectors for the interaction between threads and core data structures	t	2	471
1885	The solution where the application is responsible for the eviction has better availability	t	0	472
1891	Reliability	t	2	473
1898	This law can be seen as an example of the architecture influence cycle	t	1	475
1904	Becomes unavailable for banks if there is a fault in the hardware of service server (srv-opc)	t	3	476
1907	We have to distinguish architectural performance from opportunistic performance	t	2	477
1918	They are both components	t	1	480
2049	By interleaving the various processing phases of each request in a sequential process	t	0	513
2053	You need to change the decomposition view to represent modules with the responsibilities associated with the DSL	t	0	514
2060	A decomposition view which contains the serialization/de-serilization modules	t	3	515
2065	The main quality of the system in the right part of the figure is scalability	t	0	517
2070	Decomposition to express the services interfaces	t	1	518
2074	Performance because it allows the processing of more requests per unit of time	t	1	519
2088	Is useful even if the implementation is concluded and the system has entered the maintenance phase	t	3	522
2089	The most important requirements (both functional and qualities) that the system must achieve	t	0	523
2100	Communicating processes, shared-data, service-oriented architecture, and peer-to-peer	t	3	525
2102	A component may execute code from different modules	t	1	526
2108	They guide us in the requirement elicitation process with the system's stakeholders	t	3	527
2111	It is not possible to develop and to test the system incrementally	t	2	528
2123	The Shared-Data style	t	2	531
2128	The Communicating Processes style	t	3	532
2131	Voting	t	2	533
2139	Put the requests into a queue and schedule their processing	t	2	535
2144	A Decomposition view	t	3	536
2147	Increasing performance and decreasing availability	t	2	537
2150	Understand the architecturally significant requirements	t	1	538
2156	Interoperability	t	3	539
2158	Modifiability	t	1	540
2182	Enumerates, for each kind of quality attribute, all the possible types of source of stimulus, stimulus, etc.	t	1	546
2186	Is useful to support scenarios where the stimulus is an omission.	t	1	547
2192	The level of abstraction of the system an architect works may vary.	t	3	548
2194	Martin Fowler complains about this definition because the early decisions are not necessarily the right ones.	t	1	549
2199	This view highlights the different performance levels for `upload` and `dowload` operations.	t	2	550
2227	This is a case of an architectural influence cycle where the feedback cycle resulted in changes on the business and project.	t	2	557
2232	This view highlights the scalability of `upload` and `dowload` operations, and of storage.	t	3	558
2234	This shared understanding includes the architecturally significant requirements.	t	1	559
2237	The exchange of information is the stimulus.	t	0	560
2262	This view highlights the performance of the `download` operations.	t	1	566
2271	Facilitate the communication among the stakeholders such that they can decide on what are the architecturally significant requirements.	t	2	568
2274	To reason about a system is to verify whether the architecturally significant requirements are considered by the architecture.	t	1	569
2277	Active redundancy can be used together with a voting tactic to detect and recover from faults.	t	0	570
2303	It describes a usability scenario where the stimulus is reduce the number of errors when configuring the system.	t	2	576
2305	In each iteration one or more architecturally significant requirements are used to decompose a software element of the system design.	t	0	577
2316	A support user initiative tactic based on the definition of a language is used to achieve this scenario.	t	3	579
2320	Cannot be used together with the Reduce Overhead performance tactic.	t	3	580
2343	Schedule resources.	t	2	586
2346	This is a modifiability scenario which has a defer binding tactic.	t	1	587
2349	Bound execution times, bound queue sizes, and increase resources.	t	0	588
2356	The utility tree covers all the significant qualities the system has to address.	t	3	589
2360	Maintain multiple copies of computation.	t	3	590
2384	This decision is not a consequence of the Fénix business case.	t	3	596
2387	The tactic used to solve the problem is based in the fact that data points are appended to the end of the metric file.	t	2	597
2390	Increase resources.	t	1	598
2395	A modifiability scenario where defer binding occurs at compile time.	t	2	599
2399	There is no relation between the cost of change and the cost of development.	t	2	600
2434	The server approach can scale independently of the number of applications.	t	1	609
2439	A connector embodies a communication protocol.	t	2	610
2473	Schedule resources.	t	0	619
2480	Scalability.	t	3	620
2502	The Merge component executes the modules merge and stdio.	t	1	626
2505	Performance.	t	0	627
2510	Scalability of read requests, because it is easy add more repositories to where reads are distributed, though there may be some level of inconsistency.	t	1	628
2513	Interoperability.	t	0	629
2544	A decomposition view, a view of the component-and-connector viewtype and a deployment view.	t	3	636
2549	Client-server.	t	0	638
2555	Client-server where the Buildbot is the client.	t	2	639
2583	Decomposition and Generalization.	t	2	646
2592	It is useful for system administrators.	t	3	648
2593	Publish-subscribe.	t	0	649
2598	Tiers to represent scalability.	t	1	650
2622	The sharing of data is done using a service-oriented architecture.	t	1	656
2628	Client-server where the Dashboard is the server.	t	3	657
2638	Communicating processes.	t	1	660
2780	Implementation views	t	3	695
2781	Communicating Processes	t	0	696
2785	Typically have a software architecture that results from the common knowledge about the system that is shared among the team members	t	0	697
2791	The functional requirements have a large impact on the definition of views of the module viewtype because they are used to define the high cohesion and low coupling of modules	t	2	698
2795	The response is not correctly stated	t	2	699
2821	The stimulus is an omission and the tactic is retry	t	0	706
2831	The quality being addressed is performance and the tactics multiple copies of data and multiple copies of computation	t	2	708
2833	Tiers style	t	0	709
2842	The sharing of data is done using a service-oriented architecture	t	1	711
2848	an ACID transaction occurs in the invoked application and ACID transactions in the other involved applications will eventually occur later	t	3	712
2852	To support high scalability the request of `User 1` needs to be decomposed into a request to only one of the aggregate instances and the processing in the other aggregate occurs in the background	t	3	713
2853	We must use various different views, both of the component-and-connector and the allocation viewtypes	t	0	714
2859	Passive replication and spare	t	2	715
2864	All of the above	t	3	716
3001	The stimulus is to port the system to a new browser	t	0	751
3006	Increase semantic coherence	t	1	752
3010	Essential to reduce costs whenever there is a fault in a hardware element	t	1	753
3013	Because this tactic simplifies the addition and removal of DataNodes	t	0	754
3018	Is composed of things such as code units, runtime elements, hardware, and people, together with the relationships among them	t	1	755
3021	Describing what are the qualities that the system should possess	t	0	756
3026	The scenario is correct but it does not describe whether the request the servers fails to respond to succeeds or fails	t	1	757
3032	Both in a component-and-connector and the Deployment views	t	3	758
3040	Bound execution times	t	3	760
3043	The web browser, o web server, and the data base	t	2	761
3048	It decouples applications developed for different organizations	t	3	762
3050	Install view	t	1	763
3054	Write two scenarios on performance	t	1	764
3058	Component-and-connector viewtype	t	1	765
3065	A module view	t	0	767
3069	The Decomposition and the Work Assignment styles	t	0	768
3075	The Uses style	t	2	769
3077	The Generalization style	t	0	770
3085	Use a passive redundancy tactic in the OPC (Order Processing Center)	t	0	772
3089	The decomposition view to include a module for the synchronization responsibilities	t	0	773
3100	The price for high scalability and availability is the need to have several replicas of the files to be shared	t	3	775
3106	Only a small set of functionalities are transactional	t	1	777
3111	The distributed log guarantees that events will be delivered only once	t	2	778
3114	It allows high scalability because it is possible the implement transactions associated to each one of the aggregates	t	1	779
3117	Implementation	t	0	780
3144	When a read occurs, the client, if it is located in the cluster, receives a list of the DataNodes where the replicas are, ordered by its closeness to the client, to improve performance of reads	t	3	786
3148	None of the above	t	3	787
3150	Enumerates, for each kind of quality attribute, all the possible types of source of stimulus, stimulus, response, etc	t	1	788
3155	The tweet unique ID is written in the home timeline of each one of the writer's followers	t	2	789
3160	This solution optimizes the performance in terms of the throughput of processed requests	t	3	790
3184	The periodic rebuild of the checkpoint improves the performance of the NameNode during its initialization	t	3	796
3186	How to control the response to one or more stimulus	t	1	797
3190	The ingestion process includes tokenizing of the tweet to include in an index	t	1	798
3195	This is right but you cannot be completely sure whether the decisions are the right ones	t	2	799
3200	This solution allows the dimensioning of the number of activities (threads or processes) that run in the server, taking into consideration the server's hardware capacity, in order to have a efficient usage of the server's CPU	t	3	800
3262	Use an intermediary that contains all the code associated with the remote invocation separating it from the modules' business logic	t	1	816
3271	The system would respond faster to requests about file locations	t	2	818
3279	The Voting tactic	t	2	820
3315	She should try to use a view of the Aspects style, assign this responsibility to a single module and define where it crosscuts the other modules	t	2	829
3348	A new aspect view that includes a module with the responsibilities associated with the access control and that crosscuts some of the other modules	t	3	837
3360	It is necessary to design a view of the Data Model architectural style at the physical level to deal with performance and consistency issues of the access to data	t	3	840
3384	Service-oriented architecture, Shared-data, Peer-to-peer, and Client-server	t	3	846
3390	May, on another view of the system, be represented by a set of components and connectors	t	1	848
3424	Tiers, Shared-data, Service-oriented architecture, and Client-server	t	3	856
3425	A component can subscribe to events	t	0	857
3432	Client-Server and Shared-data	t	3	858
3439	A Communicating processes view	t	2	860
3523	If there is some technology available that implements the complex connectors it is not necessary to document their decomposition.	t	2	881
3529	Decomposition.	t	0	883
3536	This solution optimizes the performance in terms of the throughput of processed requests.	t	3	884
3539	Integrate the development of the software system with the organization's business goals.	t	2	885
3543	A fault.	t	2	886
3548	Throughput.	t	3	887
3551	Restrict dependencies.	t	2	888
3554	Uses style.	t	1	889
3560	The Generalization and Decomposition styles.	t	3	890
3562	An aggregate is usually loaded in its entirety from the database.	t	1	891
3568	Testing is easier	t	3	892
3572	Maintain system model.	t	3	893
3573	This view shows that if is possible to scale differently the `web tier` from the `EJB tier`.	t	0	894
3579	Maintain multiple copies of computation.	t	2	895
3584	Retry tactic	t	3	896
3586	The project manager uses this view to get advice on the incremental development of the system.	t	1	897
3593	Analysing the performance of the system.	t	0	899
3600	All layers are mapped to the browser component where the data access layer will contains, besides a module to access a local repository, modules to access external services.	t	3	900
3684	All the above.	t	3	921
3685	Increase competence set.	t	0	922
3690	Reduce overhead.	t	1	923
3696	It is necessary to use views of the three viewtypes.	t	3	924
3706	Usability.	t	1	927
3710	It is useful for the project managers.	t	1	928
3715	Performance and Availability for crashes of the Image File Storage component.	t	2	929
3720	Four.	t	3	930
3723	That the impact of integrating a new source is controlled by the interface of *Import DVD Info* Module.	t	2	931
3731	The Service Oriented Architecture style.	t	2	933
3734	This view shows that the processing of tracking requests is done synchronously.	t	1	934
3744	Pipe-and-filter and shared-data.	t	3	936
3747	Do not loose the changes done on the client component if the server is not available.	t	2	937
3749	Split module.	t	0	938
3759	The connectors only use the *stdio* module for their implementation.	t	2	940
3783	A common understanding to be achieve among all the system stakeholders	t	2	946
3788	The measure of the response is throughput	t	3	947
3791	Project	t	2	948
3794	Scalability of the Image File Storage in terms of the storage capacity	t	1	949
3800	Simple programming model, the clients only need to concern about the business logic of the application, the remote services are transparent	t	3	950
3822	The asynchronous solution can support a larger number of simultaneous requests	t	1	956
3828	None of the above	t	3	957
3831	Modifiability	t	2	958
3836	The separation of write and retrieval services allows them do scale independently	t	3	959
3838	Limit event response	t	1	960
3863	In the asynchronous solution it is possible to provide an implement where the tasks being executed are finished without requiring the client to resubmitted them	t	2	966
3865	Provides the quality of availability	t	0	967
3871	The need to have eventual consistency and compensating operations	t	2	968
3875	Maintain multiple copies of computation	t	2	969
3877	Time to market is the most important impact of cloud computing in an architecture	t	0	970
3904	There should be at least one view of the system using this architectural style.	t	3	976
3907	performance tactics.	t	2	977
3916	May conflict with the Reduce Overhead performance tactic.	t	3	979
3920	Results from the application of several ADD iterations.	t	3	980
3948	All the previous options.	t	3	987
3954	This is a modifiability scenario which has a defer binding tactic.	t	1	989
3988	None of the above.	t	3	997
3989	The quality addressed is availability and transactions tactic is required to solve the problem.	t	0	998
3997	A low cost of change may imply a high cost of development.	t	0	1000
4024	Service-oriented architecture, Shared-data, Client-server and Peer-to-peer.	t	3	1006
4027	The Decomposition style.	t	2	1007
4034	Which improves modifiability, because filters are decoupled through pipes.	t	1	1009
4039	The view addresses the scenario because it separates the modules that represent the interfaces a new business partner has to implement.	t	2	1010
4061	If the OPC crashes the Consumer Website can continue to provide service in degraded mode.	t	0	1016
4065	A component can subscribe to events.	t	0	1017
4071	The Uses style.	t	2	1018
4080	The attachment between components and connectors only depends on their ports and roles types.	t	3	1020
4102	Publish-subscribe.	t	1	1026
4105	Layer 1.	t	0	1027
4109	The view does not address the scenario	t	0	1028
4116	The price for high scalability and availability is the need to have several replicas of the files to be shared.	t	3	1029
4118	Availability and Performance.	t	1	1030
4141	A performance scenario associated with the throughput of writing data points to disk.	t	0	1036
4147	Allows the querying of a past state.	t	2	1037
4155	The decomposition was driven by a split module tactic.	t	2	1039
4157	HTML5 provides better portability qualities.	t	0	1040
4184	An availability scenario of the *Graphite* system.	t	3	1046
4187	A request for a web page corresponds to requesting a service from the amazon cloud.	t	2	1047
4191	She should try to split the system in parts in order to isolate the complex business logic and use the two architectural approaches accordingly.	t	2	1048
4195	Communicating Processes.	t	2	1049
4200	The view results from the implementation of a support system initiative tactic.	t	3	1050
4504	It helps on the configuration of systems.	t	3	1126
4222	An iterative development was followed, which allowed more time to develop a connector with good performance in the latter stages of the project.	t	1	1056
4227	A deployment and a work assignment view.	t	2	1057
4231	This view fulfills a modifiability scenario, which states about the cost of adding a new source of information to the system.	t	2	1058
4236	At least two performance scenarios of the *Graphite* system.	t	3	1059
4239	Reads may not be consistent with the most recent write.	t	2	1060
4321	Modifiability.	t	0	1081
4327	A view of the Decomposition style	t	2	1082
4332	By changing a parent, which will automatically change all the children that inherit from it.	t	3	1083
4333	With a component-and-connector view, where the *load balancer* is a component of the system	t	0	1084
4338	It supports the concurrent access of data accessors.	t	1	1085
4341	The solution where the application is responsible for retrieving the missing piece of data from the underlying store has better availability	t	0	1086
4348	A new aspect view to include the responsibilities associated with the access control.	t	3	1087
4351	The Publish-subscribe style	t	2	1088
4353	Analysing the performance of the system	t	0	1089
4360	Client-server, Repository and Publish-subscribe.	t	3	1090
4364	Security	t	3	1091
4367	and it does not penalize reliability because it also provides an interface that the webapp components can use to access the most recent data	t	2	1092
4371	A layered architecture, where the access to the various sources is the responsibility of the bottommost layer	t	2	1093
4373	Schedule Resources	t	0	1094
4377	Maintain system model tactic.	t	0	1095
4382	An iterative development was followed, which allowed more time to develop a connector with good performance in the latter stages of the project.	t	1	1096
4386	This decomposition implies that, in average, customers are going to have a large number of orders.	t	1	1097
4391	Heartbeat is more scalable than ping/echo because the monitor does not need to know in advance the addresses of the components.	t	2	1098
4394	All viewtypes may be necessary	t	1	1099
4399	When the environment is initiation time it means that it is necessary to restart the system for the change to take effect	t	2	1100
4481	Can use the operations defined in any of the system's modules	t	0	1121
4491	It represents a relation between a component's port and a port of one of its internal components.	t	2	1123
4493	To facilitate the interaction among heterogeneous components that use distinct communication protocols	t	0	1124
4498	The behavior described in the sentence can be represented in a view where the dynamic reconfiguration architectural style is used	t	1	1125
4505	The presentation logic layer and how it relates with the underlying layer changed	t	0	1127
4512	Detect a fault.	t	3	1128
4514	Limit event response	t	1	1129
4519	Restrict dependencies	t	2	1130
4521	It would reduce the scalability for updates of different orders for the same customer.	t	0	1131
4527	Reads may not be consistent with the most recent write.	t	2	1132
4532	A module contains the code that can execute in several components and a component can execute the code of several modules	t	3	1133
4533	Manage sampling rate	t	0	1134
4537	The layered view to deal with the aspects of portability.	t	0	1135
4542	The view addresses the scenario because it separates the `Consumer Website` module from the `OpcApp` module to allow the execution of the `Consumer Website` module in a component that can have multiple copies of computation	t	1	1136
4547	Performance	t	2	1137
4550	Performance e Usability	t	1	1138
4554	A module view of the data model style.	t	1	1139
4559	Suffered from some level of featuritis, but it allowed to have a pilot from which the team learned.	t	2	1140
4622	Usability	t	1	1156
4627	Performance and security	t	2	1157
4632	Monitorability	t	3	1158
4633	Manage sampling rate	t	0	1159
4638	Maintain multiple copies of computation	t	1	1160
4703	Availability.	t	2	1176
4708	Schedule resources	t	3	1177
4711	Component and Module	t	2	1178
4715	Maintain multiple copies of data	t	2	1179
4717	Limit event response	t	0	1180
4741	Limit access.	t	0	1186
4746	Increase resource efficiency tactic.	t	1	1187
4751	The new defects introduced.	t	2	1188
4756	Is an iterative process where architectural designs are proposed as hypothesis and tested.	t	3	1189
4758	Is applied at the begin of the architectural design process but may be necessary to redo it later.	t	1	1190
4783	Voting.	t	2	1196
4785	This ASR can easily be supported by the architecture because it has little effect in the architecture.	t	0	1197
4789	The correctness of the caller module may not depend on the correct implementation of the invoked function in the called module.	t	0	1198
4796	Schedule resources tactic.	t	3	1199
4799	Maintain system model	t	2	1200
4829	The main quality this style addresses is interoperability.	t	0	1208
4835	Each layer defines a virtual machine because it provides a set of cohesive functionalities to the upper layer	t	2	1209
4837	If there are performance requirements concerning the access to data, then the level of detail should be physical	t	0	1210
4862	Restricts the communication between components because, for instance, a group of components should be located in the same hardware.	t	1	1216
4867	It separates in new modules responsibilities that were spread over various of the system's modules	t	2	1217
4872	The interaction between peers is symmetric.	t	3	1218
4876	It is necessary to design a view of the Data Model architectural style at the physical level to deal with performance and consistency issues of the access to data.	t	3	1219
4877	A component is an instance and a view can have several instances of the same component type.	t	0	1220
4902	A single view of the communicating processes style.	t	1	1226
4908	All of the above.	t	3	1227
4911	A deployment and a work assignment view.	t	2	1228
4916	The data model view in order to define generic entities that can be customized for different kinds of catalogs.	t	3	1229
4917	Client-server.	t	0	1230
4942	Communicating Processes.	t	1	1236
4946	The communicating processes style and the dynamic reconfiguration style.	t	1	1237
4951	Publish-subscribe.	t	2	1238
4954	Modules are allocated to persons and teams.	t	1	1239
4957	The decomposition view to include a module for the synchronization responsibilities.	t	0	1240
\.


--
-- Data for Name: question_answers; Type: TABLE DATA; Schema: public; Owner: pedro
--

COPY public.question_answers (id, time_taken, option_id, quiz_answer_id, quiz_question_id) FROM stdin;
1	\N	83	475	21
2	\N	\N	1	21
3	\N	84	482	21
4	\N	81	26	21
5	\N	81	35	21
6	\N	84	483	21
7	\N	\N	2	21
8	\N	83	476	21
9	\N	81	29	21
10	\N	84	16	21
11	\N	82	473	21
12	\N	83	36	21
13	\N	81	472	21
14	\N	82	30	21
15	\N	\N	3	21
16	\N	84	17	21
17	\N	84	37	21
18	\N	\N	4	21
19	\N	83	477	21
20	\N	84	18	21
21	\N	84	41	21
22	\N	84	24	21
23	\N	84	484	21
24	\N	84	12	21
25	\N	\N	5	21
26	\N	81	25	21
27	\N	83	478	21
28	\N	\N	6	21
29	\N	83	479	21
30	\N	84	45	21
31	\N	84	485	21
32	\N	\N	7	21
33	\N	81	13	21
34	\N	83	31	21
35	\N	82	474	21
36	\N	83	480	21
37	\N	81	42	21
38	\N	84	19	21
39	\N	82	40	21
40	\N	82	14	21
41	\N	81	20	21
42	\N	83	27	21
43	\N	81	38	21
44	\N	82	39	21
45	\N	81	21	21
46	\N	84	486	21
47	\N	83	15	21
48	\N	82	43	21
49	\N	81	22	21
50	\N	82	32	21
51	\N	\N	8	21
52	\N	83	481	21
53	\N	81	28	21
54	\N	\N	9	21
55	\N	81	23	21
56	\N	\N	10	21
57	\N	83	33	21
58	\N	81	44	21
59	\N	\N	11	21
60	\N	83	34	21
61	\N	87	475	22
62	\N	86	1	22
63	\N	86	482	22
64	\N	86	26	22
65	\N	86	35	22
66	\N	86	483	22
67	\N	\N	2	22
68	\N	86	476	22
69	\N	88	29	22
70	\N	86	16	22
71	\N	87	473	22
72	\N	86	36	22
73	\N	88	472	22
74	\N	88	30	22
75	\N	88	3	22
76	\N	85	17	22
77	\N	88	37	22
78	\N	86	4	22
79	\N	86	477	22
80	\N	85	18	22
81	\N	87	41	22
82	\N	87	24	22
83	\N	85	484	22
84	\N	\N	12	22
85	\N	88	5	22
86	\N	87	25	22
87	\N	88	478	22
88	\N	\N	6	22
89	\N	87	479	22
90	\N	86	45	22
91	\N	87	485	22
92	\N	87	7	22
93	\N	\N	13	22
94	\N	86	31	22
95	\N	88	474	22
96	\N	87	480	22
97	\N	85	42	22
98	\N	88	19	22
99	\N	88	40	22
100	\N	\N	14	22
101	\N	87	20	22
102	\N	88	27	22
103	\N	88	38	22
104	\N	88	39	22
105	\N	85	21	22
106	\N	87	486	22
107	\N	\N	15	22
108	\N	88	43	22
109	\N	85	22	22
110	\N	88	32	22
111	\N	87	8	22
112	\N	85	481	22
113	\N	85	28	22
114	\N	88	9	22
115	\N	85	23	22
116	\N	85	10	22
117	\N	87	33	22
118	\N	87	44	22
119	\N	85	11	22
120	\N	87	34	22
121	\N	90	475	23
122	\N	\N	1	23
123	\N	89	482	23
124	\N	91	26	23
125	\N	89	35	23
126	\N	90	483	23
127	\N	89	2	23
128	\N	90	476	23
129	\N	92	29	23
130	\N	\N	16	23
131	\N	89	473	23
132	\N	89	36	23
133	\N	89	472	23
134	\N	89	30	23
135	\N	91	3	23
136	\N	\N	17	23
137	\N	91	37	23
138	\N	\N	4	23
139	\N	91	477	23
140	\N	\N	18	23
141	\N	90	41	23
142	\N	89	24	23
143	\N	89	484	23
144	\N	89	12	23
145	\N	91	5	23
146	\N	91	25	23
147	\N	89	478	23
148	\N	\N	6	23
149	\N	89	479	23
150	\N	91	45	23
151	\N	91	485	23
152	\N	90	7	23
153	\N	\N	13	23
154	\N	90	31	23
155	\N	90	474	23
156	\N	91	480	23
157	\N	91	42	23
158	\N	\N	19	23
159	\N	89	40	23
160	\N	\N	14	23
161	\N	\N	20	23
162	\N	91	27	23
163	\N	91	38	23
164	\N	91	39	23
165	\N	\N	21	23
166	\N	91	486	23
167	\N	91	15	23
168	\N	89	43	23
169	\N	\N	22	23
170	\N	91	32	23
171	\N	89	8	23
172	\N	89	481	23
173	\N	90	28	23
174	\N	\N	9	23
175	\N	\N	23	23
176	\N	89	10	23
177	\N	89	33	23
178	\N	89	44	23
179	\N	90	11	23
180	\N	91	34	23
181	\N	93	475	24
182	\N	\N	1	24
183	\N	94	482	24
184	\N	96	26	24
185	\N	93	35	24
186	\N	94	483	24
187	\N	94	2	24
188	\N	95	476	24
189	\N	95	29	24
190	\N	94	16	24
191	\N	96	473	24
192	\N	96	36	24
193	\N	94	472	24
194	\N	95	30	24
195	\N	93	3	24
196	\N	\N	17	24
197	\N	94	37	24
198	\N	95	4	24
199	\N	95	477	24
200	\N	93	18	24
201	\N	93	41	24
202	\N	\N	24	24
203	\N	95	484	24
204	\N	94	12	24
205	\N	95	5	24
206	\N	\N	25	24
207	\N	95	478	24
208	\N	96	6	24
209	\N	95	479	24
210	\N	93	45	24
211	\N	93	485	24
212	\N	96	7	24
213	\N	96	13	24
214	\N	93	31	24
215	\N	96	474	24
216	\N	95	480	24
217	\N	93	42	24
218	\N	96	19	24
219	\N	96	40	24
220	\N	\N	14	24
221	\N	95	20	24
222	\N	96	27	24
223	\N	96	38	24
224	\N	96	39	24
225	\N	93	21	24
226	\N	95	486	24
227	\N	\N	15	24
228	\N	96	43	24
229	\N	93	22	24
230	\N	96	32	24
231	\N	95	8	24
232	\N	93	481	24
233	\N	93	28	24
234	\N	96	9	24
235	\N	93	23	24
236	\N	93	10	24
237	\N	95	33	24
238	\N	95	44	24
239	\N	93	11	24
240	\N	95	34	24
241	\N	100	475	25
242	\N	100	1	25
243	\N	99	482	25
244	\N	100	26	25
245	\N	100	35	25
246	\N	100	483	25
247	\N	100	2	25
248	\N	100	476	25
249	\N	100	29	25
250	\N	97	16	25
251	\N	100	473	25
252	\N	99	36	25
253	\N	99	472	25
254	\N	99	30	25
255	\N	97	3	25
256	\N	\N	17	25
257	\N	97	37	25
258	\N	\N	4	25
259	\N	100	477	25
260	\N	97	18	25
261	\N	100	41	25
262	\N	97	24	25
263	\N	100	484	25
264	\N	99	12	25
265	\N	\N	5	25
266	\N	100	25	25
267	\N	100	478	25
268	\N	99	6	25
269	\N	97	479	25
270	\N	99	45	25
271	\N	100	485	25
272	\N	100	7	25
273	\N	99	13	25
274	\N	99	31	25
275	\N	97	474	25
276	\N	97	480	25
277	\N	97	42	25
278	\N	100	19	25
279	\N	98	40	25
280	\N	98	14	25
281	\N	100	20	25
282	\N	98	27	25
283	\N	97	38	25
284	\N	97	39	25
285	\N	98	21	25
286	\N	99	486	25
287	\N	97	15	25
288	\N	100	43	25
289	\N	98	22	25
290	\N	100	32	25
291	\N	98	8	25
292	\N	100	481	25
293	\N	98	28	25
294	\N	97	9	25
295	\N	97	23	25
296	\N	100	10	25
297	\N	98	33	25
298	\N	98	44	25
299	\N	98	11	25
300	\N	98	34	25
7188	\N	2091	592	238
7189	\N	2091	593	238
4500	\N	2091	535	192
7147	\N	2092	583	238
7154	\N	2092	585	238
7155	\N	2092	564	238
7164	\N	2092	568	238
7170	\N	2092	589	238
7179	\N	2092	575	238
7183	\N	2092	591	238
7196	\N	2092	596	238
4475	\N	2092	493	192
4486	\N	2092	524	192
4490	\N	2092	527	192
4502	\N	2092	508	192
4503	\N	2092	509	192
4505	\N	2092	130	192
318	\N	\N	4	26
4506	\N	2092	510	192
4507	\N	2092	538	192
301	\N	3013	475	26
302	\N	3013	1	26
304	\N	3013	26	26
305	\N	3013	35	26
307	\N	3013	2	26
308	\N	3013	476	26
309	\N	3013	29	26
310	\N	3013	16	26
349	\N	\N	22	26
311	\N	3013	473	26
312	\N	3013	36	26
314	\N	3013	30	26
315	\N	3013	3	26
316	\N	3013	17	26
317	\N	3013	37	26
320	\N	3013	18	26
321	\N	3013	41	26
322	\N	3013	24	26
326	\N	3013	25	26
327	\N	3013	478	26
361	\N	108	475	27
362	\N	107	1	27
363	\N	106	482	27
364	\N	\N	26	27
365	\N	108	35	27
366	\N	108	483	27
367	\N	107	2	27
368	\N	107	476	27
369	\N	107	29	27
370	\N	107	16	27
371	\N	107	473	27
372	\N	106	36	27
373	\N	107	472	27
374	\N	108	30	27
375	\N	108	3	27
376	\N	\N	17	27
377	\N	108	37	27
378	\N	107	4	27
379	\N	107	477	27
380	\N	107	18	27
381	\N	106	41	27
382	\N	106	24	27
383	\N	106	484	27
384	\N	107	12	27
385	\N	107	5	27
386	\N	107	25	27
387	\N	107	478	27
388	\N	106	6	27
389	\N	107	479	27
390	\N	106	45	27
391	\N	108	485	27
392	\N	\N	7	27
393	\N	108	13	27
394	\N	108	31	27
395	\N	105	474	27
396	\N	105	480	27
397	\N	105	42	27
398	\N	105	19	27
399	\N	107	40	27
400	\N	105	14	27
401	\N	107	20	27
402	\N	\N	27	27
403	\N	105	38	27
404	\N	105	39	27
405	\N	\N	21	27
406	\N	108	486	27
407	\N	105	15	27
408	\N	105	43	27
409	\N	105	22	27
410	\N	105	32	27
411	\N	105	8	27
412	\N	105	481	27
413	\N	\N	28	27
414	\N	106	9	27
415	\N	105	23	27
416	\N	106	10	27
417	\N	105	33	27
418	\N	105	44	27
419	\N	106	11	27
420	\N	107	34	27
421	\N	110	475	28
422	\N	\N	1	28
423	\N	110	482	28
424	\N	111	26	28
425	\N	112	35	28
426	\N	109	483	28
427	\N	109	2	28
428	\N	110	476	28
429	\N	\N	29	28
430	\N	109	16	28
431	\N	111	473	28
432	\N	111	36	28
433	\N	111	472	28
434	\N	\N	30	28
435	\N	111	3	28
436	\N	112	17	28
437	\N	111	37	28
438	\N	\N	4	28
439	\N	110	477	28
440	\N	109	18	28
441	\N	110	41	28
442	\N	111	24	28
443	\N	109	484	28
444	\N	\N	12	28
445	\N	\N	5	28
446	\N	\N	25	28
447	\N	111	478	28
448	\N	111	6	28
449	\N	111	479	28
450	\N	111	45	28
451	\N	111	485	28
452	\N	\N	7	28
453	\N	\N	13	28
454	\N	\N	31	28
455	\N	109	474	28
456	\N	109	480	28
457	\N	109	42	28
458	\N	\N	19	28
459	\N	109	40	28
460	\N	\N	14	28
461	\N	110	20	28
462	\N	\N	27	28
463	\N	109	38	28
464	\N	109	39	28
465	\N	\N	21	28
466	\N	109	486	28
467	\N	109	15	28
468	\N	109	43	28
469	\N	110	22	28
470	\N	\N	32	28
471	\N	\N	8	28
472	\N	112	481	28
473	\N	\N	28	28
474	\N	109	9	28
475	\N	\N	23	28
476	\N	109	10	28
477	\N	\N	33	28
478	\N	111	44	28
479	\N	109	11	28
480	\N	\N	34	28
481	\N	115	475	29
482	\N	113	1	29
483	\N	113	482	29
484	\N	\N	26	29
485	\N	\N	35	29
486	\N	113	483	29
487	\N	116	2	29
488	\N	115	476	29
489	\N	\N	29	29
490	\N	113	16	29
491	\N	113	473	29
492	\N	\N	36	29
493	\N	116	472	29
494	\N	\N	30	29
495	\N	113	3	29
496	\N	\N	17	29
497	\N	\N	37	29
498	\N	\N	4	29
499	\N	114	477	29
500	\N	115	18	29
501	\N	113	41	29
502	\N	113	24	29
503	\N	116	484	29
504	\N	\N	12	29
505	\N	\N	5	29
506	\N	\N	25	29
507	\N	113	478	29
508	\N	113	6	29
509	\N	114	479	29
510	\N	113	45	29
511	\N	113	485	29
512	\N	113	7	29
513	\N	113	13	29
514	\N	\N	31	29
515	\N	116	474	29
516	\N	114	480	29
517	\N	116	42	29
518	\N	113	19	29
519	\N	116	40	29
520	\N	\N	14	29
521	\N	113	20	29
522	\N	116	27	29
523	\N	\N	38	29
524	\N	\N	39	29
525	\N	116	21	29
526	\N	115	486	29
527	\N	116	15	29
528	\N	116	43	29
529	\N	116	22	29
530	\N	115	32	29
531	\N	113	8	29
532	\N	116	481	29
533	\N	115	28	29
534	\N	116	9	29
535	\N	116	23	29
536	\N	\N	10	29
537	\N	\N	33	29
538	\N	116	44	29
539	\N	116	11	29
540	\N	\N	34	29
541	\N	120	475	30
542	\N	120	1	30
543	\N	120	482	30
544	\N	120	26	30
545	\N	120	35	30
546	\N	120	483	30
547	\N	120	2	30
548	\N	117	476	30
549	\N	120	29	30
550	\N	117	16	30
551	\N	120	473	30
552	\N	120	36	30
553	\N	120	472	30
554	\N	120	30	30
555	\N	120	3	30
556	\N	120	17	30
557	\N	120	37	30
558	\N	120	4	30
559	\N	120	477	30
560	\N	120	18	30
561	\N	120	41	30
562	\N	120	24	30
563	\N	120	484	30
564	\N	120	12	30
565	\N	120	5	30
566	\N	120	25	30
567	\N	120	478	30
568	\N	120	6	30
569	\N	120	479	30
570	\N	120	45	30
571	\N	120	485	30
572	\N	120	7	30
573	\N	120	13	30
574	\N	120	31	30
575	\N	120	474	30
576	\N	120	480	30
577	\N	119	42	30
578	\N	119	19	30
579	\N	119	40	30
580	\N	\N	14	30
581	\N	119	20	30
582	\N	119	27	30
583	\N	119	38	30
584	\N	119	39	30
585	\N	120	21	30
586	\N	119	486	30
587	\N	119	15	30
588	\N	120	43	30
589	\N	119	22	30
590	\N	120	32	30
591	\N	119	8	30
592	\N	119	481	30
593	\N	117	28	30
594	\N	119	9	30
595	\N	117	23	30
596	\N	\N	10	30
597	\N	119	33	30
598	\N	119	44	30
599	\N	120	11	30
600	\N	\N	34	30
601	\N	122	475	31
602	\N	123	1	31
603	\N	122	482	31
604	\N	121	26	31
605	\N	121	35	31
606	\N	123	483	31
607	\N	123	2	31
608	\N	124	476	31
609	\N	122	29	31
610	\N	124	16	31
611	\N	123	473	31
612	\N	123	36	31
613	\N	122	472	31
614	\N	123	30	31
615	\N	123	3	31
616	\N	\N	17	31
617	\N	122	37	31
618	\N	123	4	31
619	\N	123	477	31
620	\N	122	18	31
621	\N	122	41	31
622	\N	123	24	31
623	\N	123	484	31
624	\N	124	12	31
625	\N	121	5	31
626	\N	\N	25	31
627	\N	123	478	31
628	\N	123	6	31
629	\N	123	479	31
630	\N	121	45	31
631	\N	123	485	31
632	\N	123	7	31
633	\N	\N	13	31
634	\N	122	31	31
635	\N	122	474	31
636	\N	122	480	31
637	\N	122	42	31
638	\N	124	19	31
639	\N	\N	40	31
640	\N	124	14	31
641	\N	121	20	31
642	\N	124	27	31
643	\N	122	38	31
644	\N	123	39	31
645	\N	124	21	31
646	\N	123	486	31
647	\N	124	15	31
648	\N	122	43	31
649	\N	121	22	31
650	\N	121	32	31
651	\N	124	8	31
652	\N	122	481	31
653	\N	122	28	31
654	\N	124	9	31
655	\N	124	23	31
656	\N	124	10	31
657	\N	124	33	31
658	\N	123	44	31
659	\N	121	11	31
660	\N	124	34	31
661	\N	125	475	32
662	\N	125	1	32
663	\N	125	482	32
664	\N	126	26	32
665	\N	128	35	32
666	\N	125	483	32
667	\N	126	2	32
668	\N	128	476	32
669	\N	125	29	32
670	\N	127	16	32
671	\N	125	473	32
672	\N	125	36	32
673	\N	125	472	32
674	\N	128	30	32
675	\N	125	3	32
676	\N	125	17	32
677	\N	125	37	32
678	\N	\N	4	32
679	\N	125	477	32
680	\N	125	18	32
681	\N	\N	41	32
682	\N	125	24	32
683	\N	126	484	32
684	\N	125	12	32
685	\N	128	5	32
686	\N	127	25	32
687	\N	125	478	32
688	\N	125	6	32
689	\N	125	479	32
690	\N	128	45	32
691	\N	128	485	32
692	\N	125	7	32
693	\N	125	13	32
694	\N	125	31	32
695	\N	128	474	32
696	\N	128	480	32
697	\N	126	42	32
698	\N	126	19	32
699	\N	\N	40	32
700	\N	125	14	32
701	\N	126	20	32
702	\N	126	27	32
703	\N	126	38	32
704	\N	125	39	32
705	\N	126	21	32
706	\N	128	486	32
707	\N	125	15	32
708	\N	126	43	32
709	\N	125	22	32
710	\N	128	32	32
711	\N	128	8	32
712	\N	128	481	32
713	\N	126	28	32
714	\N	\N	9	32
715	\N	125	23	32
716	\N	\N	10	32
717	\N	128	33	32
718	\N	126	44	32
719	\N	126	11	32
720	\N	126	34	32
721	\N	131	475	33
722	\N	132	1	33
723	\N	132	482	33
724	\N	131	26	33
725	\N	132	35	33
726	\N	131	483	33
727	\N	131	2	33
728	\N	129	476	33
729	\N	131	29	33
730	\N	129	16	33
731	\N	129	473	33
732	\N	131	36	33
733	\N	132	472	33
734	\N	131	30	33
735	\N	132	3	33
736	\N	\N	17	33
737	\N	129	37	33
738	\N	\N	4	33
739	\N	131	477	33
740	\N	129	18	33
741	\N	129	41	33
742	\N	131	24	33
743	\N	130	484	33
744	\N	129	12	33
745	\N	\N	5	33
746	\N	132	25	33
747	\N	132	478	33
748	\N	\N	6	33
749	\N	132	479	33
750	\N	131	45	33
751	\N	132	485	33
752	\N	132	7	33
753	\N	132	13	33
754	\N	\N	31	33
755	\N	130	474	33
756	\N	130	480	33
757	\N	130	42	33
758	\N	130	19	33
759	\N	130	40	33
760	\N	130	14	33
761	\N	130	20	33
762	\N	130	27	33
763	\N	130	38	33
764	\N	130	39	33
765	\N	130	21	33
766	\N	130	486	33
767	\N	130	15	33
768	\N	130	43	33
769	\N	130	22	33
770	\N	130	32	33
771	\N	130	8	33
772	\N	130	481	33
773	\N	130	28	33
774	\N	130	9	33
775	\N	130	23	33
776	\N	130	10	33
777	\N	130	33	33
778	\N	130	44	33
779	\N	130	11	33
780	\N	130	34	33
781	\N	133	475	34
782	\N	134	1	34
783	\N	134	482	34
784	\N	134	26	34
785	\N	135	35	34
786	\N	135	483	34
787	\N	133	2	34
788	\N	133	476	34
789	\N	134	29	34
790	\N	134	16	34
791	\N	133	473	34
792	\N	134	36	34
793	\N	134	472	34
794	\N	133	30	34
795	\N	134	3	34
796	\N	134	17	34
797	\N	133	37	34
798	\N	134	4	34
799	\N	133	477	34
800	\N	134	18	34
801	\N	134	41	34
802	\N	133	24	34
803	\N	133	484	34
804	\N	134	12	34
805	\N	\N	5	34
806	\N	134	25	34
807	\N	134	478	34
808	\N	134	6	34
809	\N	134	479	34
810	\N	134	45	34
811	\N	134	485	34
812	\N	134	7	34
813	\N	134	13	34
814	\N	134	31	34
815	\N	133	474	34
816	\N	133	480	34
817	\N	133	42	34
818	\N	133	19	34
819	\N	133	40	34
820	\N	133	14	34
821	\N	133	20	34
822	\N	133	27	34
823	\N	135	38	34
824	\N	135	39	34
825	\N	133	21	34
826	\N	136	486	34
827	\N	133	15	34
828	\N	133	43	34
829	\N	133	22	34
830	\N	133	32	34
831	\N	133	8	34
832	\N	135	481	34
833	\N	136	28	34
834	\N	133	9	34
835	\N	133	23	34
836	\N	133	10	34
837	\N	136	33	34
838	\N	133	44	34
839	\N	133	11	34
840	\N	\N	34	34
328	\N	3013	6	26
329	\N	3013	479	26
331	\N	3013	485	26
333	\N	3013	13	26
334	\N	3013	31	26
350	\N	3013	32	26
336	\N	3015	480	26
337	\N	3015	42	26
339	\N	3015	40	26
345	\N	3015	21	26
346	\N	3015	486	26
351	\N	3015	8	26
353	\N	3015	28	26
355	\N	3015	23	26
356	\N	3015	10	26
856	\N	\N	17	35
303	\N	3014	482	26
306	\N	3014	483	26
313	\N	3014	472	26
319	\N	3014	477	26
324	\N	3014	12	26
325	\N	3014	5	26
330	\N	3014	45	26
332	\N	3014	7	26
865	\N	\N	5	35
335	\N	3014	474	26
323	\N	3016	484	26
868	\N	\N	6	35
338	\N	3016	19	26
340	\N	3016	14	26
341	\N	3016	20	26
342	\N	3016	27	26
873	\N	\N	13	35
343	\N	3016	38	26
344	\N	3016	39	26
347	\N	3016	15	26
877	\N	\N	42	35
348	\N	3016	43	26
352	\N	3016	481	26
354	\N	3016	9	26
881	\N	\N	20	35
882	\N	\N	27	35
357	\N	3016	33	26
358	\N	3016	44	26
359	\N	3016	11	26
360	\N	3016	34	26
2576	\N	3432	105	152
888	\N	\N	43	35
2577	\N	3432	106	152
890	\N	\N	32	35
891	\N	\N	8	35
2578	\N	3432	107	152
2579	\N	3432	108	152
2580	\N	3432	109	152
895	\N	\N	23	35
2581	\N	3432	110	152
2582	\N	3432	111	152
2583	\N	3432	95	152
899	\N	\N	11	35
2584	\N	3432	86	152
901	\N	141	475	36
902	\N	142	1	36
903	\N	141	482	36
904	\N	144	26	36
905	\N	141	35	36
906	\N	141	483	36
907	\N	141	2	36
908	\N	141	476	36
909	\N	142	29	36
910	\N	141	16	36
911	\N	141	473	36
912	\N	142	36	36
913	\N	141	472	36
914	\N	141	30	36
915	\N	141	3	36
916	\N	142	17	36
917	\N	141	37	36
918	\N	141	4	36
919	\N	142	477	36
920	\N	141	18	36
921	\N	141	41	36
922	\N	141	24	36
923	\N	142	484	36
924	\N	142	12	36
925	\N	\N	5	36
926	\N	141	25	36
927	\N	141	478	36
928	\N	141	6	36
929	\N	141	479	36
930	\N	141	45	36
931	\N	141	485	36
932	\N	141	7	36
933	\N	\N	13	36
934	\N	141	31	36
935	\N	144	474	36
936	\N	143	480	36
937	\N	141	42	36
938	\N	143	19	36
939	\N	144	40	36
940	\N	144	14	36
941	\N	144	20	36
942	\N	143	27	36
943	\N	144	38	36
944	\N	143	39	36
945	\N	\N	21	36
946	\N	144	486	36
947	\N	141	15	36
948	\N	144	43	36
949	\N	144	22	36
950	\N	144	32	36
951	\N	144	8	36
952	\N	143	481	36
953	\N	144	28	36
954	\N	144	9	36
955	\N	\N	23	36
956	\N	\N	10	36
957	\N	143	33	36
958	\N	\N	44	36
959	\N	144	11	36
960	\N	144	34	36
961	\N	145	475	37
962	\N	148	1	37
963	\N	145	482	37
964	\N	\N	26	37
965	\N	\N	35	37
966	\N	148	483	37
967	\N	148	2	37
968	\N	148	476	37
969	\N	148	29	37
970	\N	148	16	37
971	\N	145	473	37
972	\N	145	36	37
973	\N	146	472	37
974	\N	\N	30	37
975	\N	147	3	37
976	\N	145	17	37
977	\N	\N	37	37
978	\N	145	4	37
979	\N	148	477	37
980	\N	148	18	37
981	\N	145	41	37
982	\N	148	24	37
983	\N	148	484	37
984	\N	148	12	37
985	\N	\N	5	37
986	\N	\N	25	37
987	\N	147	478	37
988	\N	148	6	37
989	\N	145	479	37
990	\N	148	45	37
991	\N	148	485	37
992	\N	145	7	37
993	\N	148	13	37
994	\N	145	31	37
995	\N	148	474	37
996	\N	148	480	37
997	\N	146	42	37
998	\N	148	19	37
999	\N	145	40	37
1000	\N	146	14	37
1001	\N	148	20	37
1002	\N	\N	27	37
1003	\N	148	38	37
1004	\N	148	39	37
1005	\N	148	21	37
1006	\N	146	486	37
1007	\N	146	15	37
1008	\N	146	43	37
1009	\N	146	22	37
1010	\N	\N	32	37
1011	\N	145	8	37
1012	\N	146	481	37
1013	\N	\N	28	37
1014	\N	\N	9	37
1015	\N	146	23	37
1016	\N	146	10	37
1017	\N	147	33	37
1018	\N	146	44	37
1019	\N	145	11	37
1020	\N	148	34	37
2589	\N	3432	114	152
2590	\N	3432	92	152
2568	\N	3432	100	152
2569	\N	3432	101	152
2571	\N	3432	85	152
2572	\N	3432	102	152
2573	\N	3432	103	152
2574	\N	3432	104	152
2591	\N	3432	87	152
2592	\N	3432	115	152
2595	\N	3432	118	152
2596	\N	3432	97	152
2599	\N	3432	121	152
1034	\N	\N	30	38
2600	\N	3432	122	152
2601	\N	3432	123	152
1037	\N	\N	37	38
1038	\N	\N	4	38
2602	\N	3432	124	152
2603	\N	3432	98	152
2604	\N	3432	125	152
2605	\N	3432	93	152
2606	\N	3432	126	152
2607	\N	3432	127	152
1045	\N	\N	5	38
2609	\N	3432	94	152
2610	\N	3432	129	152
2566	\N	3429	99	152
2570	\N	3429	91	152
1050	\N	\N	45	38
2585	\N	3429	112	152
2586	\N	3429	113	152
2588	\N	3429	96	152
2598	\N	3429	120	152
2608	\N	3429	128	152
2594	\N	3429	117	152
2597	\N	3429	119	152
2567	\N	3431	90	152
2593	\N	3431	116	152
1060	\N	\N	14	38
7016	\N	4558	554	236
7027	\N	4558	597	236
7029	\N	4557	562	236
7017	\N	4559	582	236
7018	\N	4559	555	236
7019	\N	4559	556	236
7020	\N	4559	557	236
7021	\N	4559	545	236
7022	\N	4559	558	236
1070	\N	\N	32	38
7023	\N	4559	559	236
7024	\N	4559	546	236
7025	\N	4559	583	236
7026	\N	4559	560	236
7028	\N	4559	561	236
7030	\N	4559	584	236
1096	\N	\N	17	39
1105	\N	\N	5	39
1113	\N	\N	13	39
1136	\N	\N	10	39
1190	\N	\N	32	40
1194	\N	\N	9	40
1196	\N	\N	10	40
1207	\N	\N	46	121
1224	\N	\N	47	121
1235	\N	\N	48	121
1237	\N	\N	49	121
1245	\N	\N	50	122
1246	\N	\N	51	122
1247	\N	\N	52	122
1248	\N	486	73	122
1249	\N	\N	53	122
1250	\N	\N	54	122
1251	\N	\N	46	122
1252	\N	\N	55	122
1253	\N	\N	56	122
1254	\N	\N	57	122
1255	\N	\N	58	122
1256	\N	\N	59	122
1257	\N	486	487	122
1258	\N	486	80	122
1259	\N	485	75	122
1260	\N	486	491	122
1261	\N	486	82	122
1262	\N	\N	60	122
1263	\N	485	84	122
1264	\N	488	83	122
1265	\N	\N	61	122
1266	\N	487	76	122
1267	\N	485	79	122
1268	\N	\N	47	122
1269	\N	486	78	122
1270	\N	\N	62	122
1271	\N	486	72	122
1272	\N	\N	63	122
1273	\N	486	81	122
1274	\N	\N	64	122
1275	\N	485	488	122
1276	\N	487	489	122
1277	\N	487	70	122
1278	\N	\N	65	122
1279	\N	\N	48	122
1280	\N	488	71	122
1281	\N	486	49	122
1282	\N	485	490	122
1283	\N	\N	66	122
1284	\N	\N	67	122
1285	\N	487	74	122
1286	\N	\N	68	122
1287	\N	\N	69	122
1288	\N	487	77	122
1300	\N	\N	59	123
1333	\N	493	50	124
1334	\N	493	51	124
1335	\N	493	52	124
1336	\N	495	73	124
1337	\N	493	53	124
1338	\N	495	54	124
1339	\N	495	46	124
1340	\N	493	55	124
1341	\N	495	56	124
1342	\N	495	57	124
1343	\N	493	58	124
1344	\N	493	59	124
1345	\N	495	487	124
1346	\N	493	80	124
1347	\N	493	75	124
1348	\N	495	491	124
1349	\N	493	82	124
1350	\N	495	60	124
1351	\N	493	84	124
1352	\N	495	83	124
1353	\N	\N	61	124
1354	\N	493	76	124
1355	\N	495	79	124
1356	\N	496	47	124
1357	\N	495	78	124
1358	\N	495	62	124
1359	\N	493	72	124
1360	\N	493	63	124
1361	\N	495	81	124
1362	\N	495	64	124
1363	\N	495	488	124
1364	\N	493	489	124
1365	\N	\N	70	124
1366	\N	495	65	124
1367	\N	493	48	124
1368	\N	495	71	124
1369	\N	493	49	124
1370	\N	495	490	124
1371	\N	495	66	124
1372	\N	495	67	124
1373	\N	495	74	124
1374	\N	495	68	124
1375	\N	493	69	124
1376	\N	496	77	124
1377	\N	497	50	125
1378	\N	497	51	125
1379	\N	497	52	125
1380	\N	497	73	125
1381	\N	497	53	125
1382	\N	497	54	125
1383	\N	497	46	125
1384	\N	500	55	125
1385	\N	497	56	125
1386	\N	\N	57	125
1387	\N	497	58	125
1388	\N	500	59	125
1389	\N	498	487	125
1390	\N	498	80	125
1391	\N	497	75	125
1392	\N	497	491	125
1393	\N	497	82	125
1394	\N	497	60	125
1395	\N	500	84	125
1396	\N	500	83	125
1397	\N	\N	61	125
1398	\N	497	76	125
1399	\N	497	79	125
1400	\N	500	47	125
1401	\N	497	78	125
1402	\N	497	62	125
1403	\N	497	72	125
1404	\N	\N	63	125
1405	\N	497	81	125
1406	\N	497	64	125
1407	\N	497	488	125
1408	\N	497	489	125
1409	\N	497	70	125
1410	\N	497	65	125
1411	\N	\N	48	125
1412	\N	\N	71	125
1413	\N	\N	49	125
1414	\N	497	490	125
1415	\N	500	66	125
1416	\N	497	67	125
1417	\N	499	74	125
1418	\N	497	68	125
1419	\N	497	69	125
1420	\N	500	77	125
1421	\N	501	50	126
1422	\N	503	51	126
1423	\N	\N	52	126
1424	\N	501	73	126
1425	\N	504	53	126
1426	\N	504	54	126
1427	\N	\N	46	126
1428	\N	503	55	126
1429	\N	501	56	126
1430	\N	\N	57	126
1431	\N	504	58	126
1432	\N	501	59	126
1433	\N	501	487	126
1434	\N	503	80	126
1435	\N	503	75	126
1436	\N	504	491	126
1437	\N	501	82	126
1438	\N	\N	60	126
1439	\N	501	84	126
1440	\N	501	83	126
1441	\N	\N	61	126
1442	\N	501	76	126
1443	\N	504	79	126
1444	\N	501	47	126
1445	\N	501	78	126
1446	\N	503	62	126
1447	\N	\N	72	126
1448	\N	501	63	126
1449	\N	501	81	126
1450	\N	504	64	126
1451	\N	503	488	126
1452	\N	503	489	126
1453	\N	\N	70	126
1454	\N	501	65	126
1455	\N	501	48	126
1456	\N	504	71	126
1457	\N	501	49	126
1458	\N	501	490	126
1459	\N	504	66	126
1460	\N	501	67	126
1461	\N	503	74	126
1462	\N	503	68	126
1463	\N	503	69	126
1464	\N	501	77	126
1465	\N	506	50	127
1466	\N	506	51	127
1467	\N	506	52	127
1468	\N	506	73	127
1469	\N	506	53	127
1470	\N	506	54	127
1471	\N	506	46	127
1472	\N	506	55	127
1473	\N	506	56	127
1474	\N	506	57	127
1475	\N	506	58	127
1476	\N	506	59	127
1477	\N	506	487	127
1478	\N	506	80	127
1479	\N	507	75	127
1480	\N	506	491	127
1481	\N	506	82	127
1482	\N	505	60	127
1483	\N	506	84	127
1484	\N	508	83	127
1485	\N	\N	61	127
1486	\N	506	76	127
1487	\N	506	79	127
1488	\N	506	47	127
1489	\N	506	78	127
1490	\N	507	62	127
1491	\N	506	72	127
1492	\N	507	63	127
1493	\N	507	81	127
1494	\N	506	64	127
1495	\N	505	488	127
1496	\N	506	489	127
1497	\N	507	70	127
1498	\N	508	65	127
1499	\N	506	48	127
1500	\N	506	71	127
1501	\N	506	49	127
1502	\N	505	490	127
1503	\N	506	66	127
1504	\N	506	67	127
1505	\N	506	74	127
1506	\N	506	68	127
1507	\N	506	69	127
1508	\N	506	77	127
1509	\N	509	50	128
1510	\N	509	51	128
1511	\N	\N	52	128
1512	\N	509	73	128
1513	\N	509	53	128
1514	\N	509	54	128
1515	\N	509	46	128
1516	\N	509	55	128
1517	\N	509	56	128
1518	\N	509	57	128
1519	\N	509	58	128
1520	\N	509	59	128
1521	\N	509	487	128
1522	\N	509	80	128
1523	\N	509	75	128
1524	\N	509	491	128
1525	\N	509	82	128
1526	\N	509	60	128
1527	\N	509	84	128
1528	\N	509	83	128
1529	\N	\N	61	128
1530	\N	509	76	128
1531	\N	509	79	128
1532	\N	509	47	128
1533	\N	509	78	128
1534	\N	509	62	128
1535	\N	509	72	128
1536	\N	509	63	128
1537	\N	509	81	128
1538	\N	509	64	128
1539	\N	509	488	128
1540	\N	509	489	128
1541	\N	509	70	128
1542	\N	509	65	128
1543	\N	509	48	128
1544	\N	511	71	128
1545	\N	509	49	128
1546	\N	509	490	128
1547	\N	509	66	128
1548	\N	509	67	128
1549	\N	512	74	128
1550	\N	509	68	128
1551	\N	509	69	128
1552	\N	509	77	128
1553	\N	515	50	129
1554	\N	515	51	129
1555	\N	\N	52	129
1556	\N	\N	73	129
1557	\N	515	53	129
1558	\N	515	54	129
1559	\N	515	46	129
1560	\N	\N	55	129
1561	\N	513	56	129
1562	\N	513	57	129
1563	\N	\N	58	129
1564	\N	\N	59	129
1565	\N	513	487	129
1566	\N	514	80	129
1567	\N	515	75	129
1568	\N	515	491	129
1569	\N	515	82	129
1570	\N	515	60	129
1571	\N	515	84	129
1572	\N	514	83	129
1573	\N	514	61	129
1574	\N	513	76	129
1575	\N	513	79	129
1576	\N	513	47	129
1577	\N	515	78	129
1578	\N	\N	62	129
1579	\N	\N	72	129
1580	\N	515	63	129
1581	\N	515	81	129
1582	\N	\N	64	129
1583	\N	513	488	129
1584	\N	515	489	129
1585	\N	516	70	129
1586	\N	516	65	129
1587	\N	513	48	129
1588	\N	\N	71	129
1589	\N	\N	49	129
1590	\N	515	490	129
1591	\N	513	66	129
1592	\N	\N	67	129
1593	\N	\N	74	129
1594	\N	515	68	129
1595	\N	515	69	129
1596	\N	515	77	129
1597	\N	519	50	130
1598	\N	517	51	130
1599	\N	517	52	130
1600	\N	517	73	130
1601	\N	517	53	130
1602	\N	517	54	130
1603	\N	517	46	130
1604	\N	517	55	130
1605	\N	517	56	130
1606	\N	517	57	130
1607	\N	517	58	130
1608	\N	517	59	130
1609	\N	518	487	130
1610	\N	517	80	130
1611	\N	517	75	130
1612	\N	520	491	130
1613	\N	517	82	130
1614	\N	518	60	130
1615	\N	518	84	130
1616	\N	517	83	130
1617	\N	518	61	130
1618	\N	517	76	130
1619	\N	520	79	130
1620	\N	\N	47	130
1621	\N	517	78	130
1622	\N	517	62	130
1623	\N	517	72	130
1624	\N	517	63	130
1625	\N	518	81	130
1626	\N	517	64	130
1627	\N	517	488	130
1628	\N	517	489	130
1629	\N	517	70	130
1630	\N	520	65	130
1631	\N	\N	48	130
1632	\N	517	71	130
1633	\N	517	49	130
1634	\N	517	490	130
1635	\N	518	66	130
1636	\N	517	67	130
1637	\N	520	74	130
1638	\N	517	68	130
1639	\N	517	69	130
1640	\N	517	77	130
1641	\N	\N	50	131
1642	\N	523	51	131
1643	\N	523	52	131
1644	\N	523	73	131
1645	\N	521	53	131
1646	\N	524	54	131
1647	\N	521	46	131
1648	\N	524	55	131
1649	\N	521	56	131
1650	\N	524	57	131
1651	\N	523	58	131
1652	\N	523	59	131
1653	\N	522	487	131
1654	\N	521	80	131
1655	\N	523	75	131
1656	\N	523	491	131
1657	\N	521	82	131
1658	\N	523	60	131
1659	\N	523	84	131
1660	\N	522	83	131
1661	\N	\N	61	131
1662	\N	523	76	131
1663	\N	522	79	131
1664	\N	523	47	131
1665	\N	524	78	131
1666	\N	521	62	131
1667	\N	523	72	131
1668	\N	523	63	131
1669	\N	523	81	131
1670	\N	523	64	131
1671	\N	523	488	131
1672	\N	523	489	131
1673	\N	523	70	131
1674	\N	521	65	131
1675	\N	523	48	131
1676	\N	523	71	131
1677	\N	521	49	131
1678	\N	524	490	131
1679	\N	523	66	131
1680	\N	522	67	131
1681	\N	521	74	131
1682	\N	522	68	131
1683	\N	523	69	131
1684	\N	522	77	131
1685	\N	527	50	132
1686	\N	528	51	132
1687	\N	\N	52	132
1688	\N	527	73	132
1689	\N	\N	53	132
1690	\N	528	54	132
1691	\N	528	46	132
1692	\N	\N	55	132
1693	\N	527	56	132
1694	\N	\N	57	132
1695	\N	528	58	132
1696	\N	528	59	132
1697	\N	528	487	132
1698	\N	527	80	132
1699	\N	\N	75	132
1700	\N	528	491	132
1701	\N	527	82	132
1702	\N	527	60	132
1703	\N	527	84	132
1704	\N	528	83	132
1705	\N	527	61	132
1706	\N	\N	76	132
1707	\N	527	79	132
1708	\N	527	47	132
1709	\N	528	78	132
1710	\N	527	62	132
1711	\N	\N	72	132
1712	\N	\N	63	132
1713	\N	527	81	132
1714	\N	527	64	132
1715	\N	527	488	132
1716	\N	528	489	132
1717	\N	528	70	132
1718	\N	528	65	132
1719	\N	\N	48	132
1720	\N	527	71	132
1721	\N	\N	49	132
1722	\N	527	490	132
1723	\N	528	66	132
1724	\N	\N	67	132
1725	\N	527	74	132
1726	\N	527	68	132
1727	\N	\N	69	132
1728	\N	\N	77	132
1729	\N	531	50	133
1730	\N	532	51	133
1731	\N	\N	52	133
1732	\N	532	73	133
1733	\N	\N	53	133
1734	\N	532	54	133
1735	\N	529	46	133
1736	\N	532	55	133
1737	\N	532	56	133
1738	\N	\N	57	133
1739	\N	\N	58	133
1740	\N	\N	59	133
1741	\N	532	487	133
1742	\N	531	80	133
1743	\N	530	75	133
1744	\N	529	491	133
1745	\N	529	82	133
1746	\N	529	60	133
1747	\N	531	84	133
1748	\N	530	83	133
1749	\N	531	61	133
1750	\N	531	76	133
1751	\N	531	79	133
1752	\N	532	47	133
1753	\N	529	78	133
1754	\N	\N	62	133
1755	\N	532	72	133
1756	\N	\N	63	133
1757	\N	532	81	133
1758	\N	\N	64	133
1759	\N	532	488	133
1760	\N	529	489	133
1761	\N	531	70	133
1762	\N	530	65	133
1763	\N	\N	48	133
1764	\N	530	71	133
1765	\N	532	49	133
1766	\N	532	490	133
1767	\N	529	66	133
1768	\N	531	67	133
1769	\N	532	74	133
1770	\N	531	68	133
1771	\N	\N	69	133
1772	\N	529	77	133
1773	\N	534	50	134
1774	\N	534	51	134
1775	\N	536	52	134
1776	\N	536	73	134
1777	\N	533	53	134
1778	\N	\N	54	134
1779	\N	\N	46	134
1780	\N	534	55	134
1781	\N	536	56	134
1782	\N	536	57	134
1783	\N	534	58	134
1784	\N	533	59	134
1785	\N	535	487	134
1786	\N	536	80	134
1787	\N	533	75	134
1788	\N	533	491	134
1789	\N	533	82	134
1790	\N	536	60	134
1791	\N	536	84	134
1792	\N	536	83	134
1793	\N	\N	61	134
1794	\N	533	76	134
1795	\N	536	79	134
1796	\N	535	47	134
1797	\N	\N	78	134
1798	\N	536	62	134
1799	\N	536	72	134
1800	\N	533	63	134
1801	\N	536	81	134
1802	\N	534	64	134
1803	\N	536	488	134
1804	\N	536	489	134
1805	\N	533	70	134
1806	\N	536	65	134
1807	\N	536	48	134
1808	\N	533	71	134
1809	\N	\N	49	134
1810	\N	536	490	134
1811	\N	536	66	134
1812	\N	536	67	134
1813	\N	533	74	134
1814	\N	533	68	134
1815	\N	533	69	134
1816	\N	533	77	134
1817	\N	537	50	135
1818	\N	537	51	135
1819	\N	540	52	135
1820	\N	537	73	135
1821	\N	537	53	135
1822	\N	540	54	135
1823	\N	538	46	135
1824	\N	540	55	135
1825	\N	540	56	135
1826	\N	539	57	135
1827	\N	\N	58	135
1828	\N	537	59	135
1829	\N	540	487	135
1830	\N	537	80	135
1831	\N	\N	75	135
1832	\N	540	491	135
1833	\N	539	82	135
1834	\N	540	60	135
1835	\N	537	84	135
1836	\N	539	83	135
1837	\N	\N	61	135
1838	\N	539	76	135
1839	\N	\N	79	135
1840	\N	537	47	135
1841	\N	\N	78	135
1842	\N	\N	62	135
1843	\N	537	72	135
1844	\N	\N	63	135
1845	\N	537	81	135
1846	\N	537	64	135
1847	\N	537	488	135
1848	\N	537	489	135
1849	\N	540	70	135
1850	\N	537	65	135
1851	\N	537	48	135
1852	\N	\N	71	135
1853	\N	537	49	135
1854	\N	539	490	135
1855	\N	537	66	135
1856	\N	537	67	135
1857	\N	539	74	135
1858	\N	537	68	135
1859	\N	537	69	135
1860	\N	537	77	135
1861	\N	543	50	136
1862	\N	543	51	136
1863	\N	543	52	136
1864	\N	543	73	136
1865	\N	543	53	136
1866	\N	543	54	136
1867	\N	544	46	136
1868	\N	543	55	136
1869	\N	541	56	136
1870	\N	544	57	136
1871	\N	544	58	136
1872	\N	543	59	136
1873	\N	544	487	136
1874	\N	\N	80	136
1875	\N	543	75	136
1876	\N	543	491	136
1877	\N	543	82	136
1878	\N	543	60	136
1879	\N	543	84	136
1880	\N	541	83	136
1881	\N	\N	61	136
1882	\N	543	76	136
1883	\N	\N	79	136
1884	\N	543	47	136
1885	\N	543	78	136
1886	\N	543	62	136
1887	\N	543	72	136
1888	\N	543	63	136
1889	\N	544	81	136
1890	\N	543	64	136
1891	\N	544	488	136
1892	\N	543	489	136
1893	\N	\N	70	136
1894	\N	543	65	136
1895	\N	543	48	136
1896	\N	543	71	136
1897	\N	543	49	136
1898	\N	541	490	136
1899	\N	543	66	136
1900	\N	\N	67	136
1901	\N	543	74	136
1902	\N	541	68	136
1903	\N	\N	69	136
1904	\N	543	77	136
1905	\N	547	50	137
1906	\N	548	51	137
1907	\N	546	52	137
1908	\N	548	73	137
1909	\N	\N	53	137
1910	\N	547	54	137
1911	\N	548	46	137
1912	\N	\N	55	137
1913	\N	548	56	137
1914	\N	548	57	137
1915	\N	548	58	137
1916	\N	545	59	137
1917	\N	546	487	137
1918	\N	\N	80	137
1919	\N	\N	75	137
1920	\N	548	491	137
1921	\N	546	82	137
1922	\N	548	60	137
1923	\N	548	84	137
1924	\N	545	83	137
1925	\N	\N	61	137
1926	\N	546	76	137
1927	\N	\N	79	137
1928	\N	548	47	137
1929	\N	547	78	137
1930	\N	\N	62	137
1931	\N	546	72	137
1932	\N	548	63	137
1933	\N	548	81	137
1934	\N	548	64	137
1935	\N	548	488	137
1936	\N	545	489	137
1937	\N	548	70	137
1938	\N	547	65	137
1939	\N	548	48	137
1940	\N	548	71	137
1941	\N	548	49	137
1942	\N	548	490	137
1943	\N	548	66	137
1944	\N	546	67	137
1945	\N	547	74	137
1946	\N	548	68	137
1947	\N	548	69	137
1948	\N	548	77	137
1949	\N	550	50	138
1950	\N	549	51	138
1951	\N	\N	52	138
1952	\N	550	73	138
1953	\N	\N	53	138
1954	\N	549	54	138
1955	\N	\N	46	138
1956	\N	552	55	138
1957	\N	551	56	138
1958	\N	549	57	138
1959	\N	\N	58	138
1960	\N	550	59	138
1961	\N	552	487	138
1962	\N	551	80	138
1963	\N	\N	75	138
1964	\N	551	491	138
1965	\N	549	82	138
1966	\N	550	60	138
1967	\N	549	84	138
1968	\N	552	83	138
1969	\N	\N	61	138
1970	\N	\N	76	138
1971	\N	\N	79	138
1972	\N	550	47	138
1973	\N	551	78	138
1974	\N	551	62	138
1975	\N	550	72	138
1976	\N	\N	63	138
1977	\N	\N	81	138
1978	\N	550	64	138
1979	\N	551	488	138
1980	\N	552	489	138
1981	\N	551	70	138
1982	\N	551	65	138
1983	\N	550	48	138
1984	\N	550	71	138
1985	\N	551	49	138
1986	\N	550	490	138
1987	\N	551	66	138
1988	\N	550	67	138
1989	\N	550	74	138
1990	\N	551	68	138
1991	\N	551	69	138
1992	\N	551	77	138
1993	\N	555	50	139
1994	\N	\N	51	139
1995	\N	\N	52	139
1996	\N	555	73	139
1997	\N	\N	53	139
1998	\N	553	54	139
1999	\N	\N	46	139
2000	\N	555	55	139
2001	\N	555	56	139
2002	\N	553	57	139
2003	\N	555	58	139
2004	\N	556	59	139
2005	\N	556	487	139
2006	\N	556	80	139
2007	\N	\N	75	139
2008	\N	553	491	139
2009	\N	553	82	139
2010	\N	\N	60	139
2011	\N	555	84	139
2012	\N	556	83	139
2013	\N	\N	61	139
2014	\N	553	76	139
2015	\N	\N	79	139
2016	\N	\N	47	139
2017	\N	553	78	139
2018	\N	555	62	139
2019	\N	555	72	139
2020	\N	\N	63	139
2021	\N	555	81	139
2022	\N	553	64	139
2023	\N	555	488	139
2024	\N	554	489	139
2025	\N	555	70	139
2026	\N	553	65	139
2027	\N	555	48	139
2028	\N	553	71	139
2029	\N	555	49	139
2030	\N	555	490	139
2031	\N	555	66	139
2032	\N	555	67	139
2033	\N	553	74	139
2034	\N	553	68	139
2035	\N	555	69	139
2036	\N	555	77	139
2037	\N	557	50	140
2038	\N	557	51	140
2039	\N	560	52	140
2040	\N	559	73	140
2041	\N	\N	53	140
2042	\N	560	54	140
2043	\N	\N	46	140
2044	\N	560	55	140
2045	\N	560	56	140
2046	\N	\N	57	140
2047	\N	\N	58	140
2048	\N	558	59	140
2049	\N	558	487	140
2050	\N	560	80	140
2051	\N	560	75	140
2052	\N	560	491	140
2053	\N	560	82	140
2054	\N	560	60	140
2055	\N	560	84	140
2056	\N	559	83	140
2057	\N	\N	61	140
2058	\N	\N	76	140
2059	\N	\N	79	140
2060	\N	560	47	140
2061	\N	559	78	140
2062	\N	\N	62	140
2063	\N	559	72	140
2064	\N	\N	63	140
2065	\N	\N	81	140
2066	\N	560	64	140
2067	\N	558	488	140
2068	\N	558	489	140
2069	\N	560	70	140
2070	\N	560	65	140
2071	\N	559	48	140
2072	\N	\N	71	140
2073	\N	558	49	140
2074	\N	560	490	140
2075	\N	557	66	140
2076	\N	\N	67	140
2077	\N	\N	74	140
2078	\N	560	68	140
2079	\N	560	69	140
2080	\N	560	77	140
2081	\N	562	50	141
2082	\N	562	51	141
2083	\N	562	52	141
2084	\N	562	73	141
2085	\N	562	53	141
2086	\N	562	54	141
2087	\N	562	46	141
2088	\N	562	55	141
2089	\N	562	56	141
2090	\N	\N	57	141
2091	\N	562	58	141
2092	\N	\N	59	141
2093	\N	562	487	141
2094	\N	562	80	141
2095	\N	\N	75	141
2096	\N	564	491	141
2097	\N	564	82	141
2098	\N	561	60	141
2099	\N	564	84	141
2100	\N	562	83	141
2101	\N	\N	61	141
2102	\N	\N	76	141
2103	\N	561	79	141
2104	\N	562	47	141
2105	\N	564	78	141
2106	\N	564	62	141
2107	\N	564	72	141
2108	\N	562	63	141
2109	\N	562	81	141
2110	\N	562	64	141
2111	\N	564	488	141
2112	\N	562	489	141
2113	\N	\N	70	141
2114	\N	562	65	141
2115	\N	\N	48	141
2116	\N	\N	71	141
2117	\N	564	49	141
2118	\N	564	490	141
2119	\N	\N	66	141
2120	\N	564	67	141
2121	\N	561	74	141
2122	\N	563	68	141
2123	\N	562	69	141
2124	\N	\N	77	141
2125	\N	566	50	142
2126	\N	567	51	142
2127	\N	\N	52	142
2128	\N	565	73	142
2129	\N	567	53	142
2130	\N	\N	54	142
2131	\N	568	46	142
2132	\N	567	55	142
2133	\N	565	56	142
2134	\N	567	57	142
2135	\N	565	58	142
2136	\N	566	59	142
2137	\N	568	487	142
2138	\N	565	80	142
2139	\N	568	75	142
2140	\N	568	491	142
2141	\N	567	82	142
2142	\N	\N	60	142
2143	\N	568	84	142
2144	\N	566	83	142
2145	\N	\N	61	142
2146	\N	565	76	142
2147	\N	567	79	142
2148	\N	568	47	142
2149	\N	566	78	142
2150	\N	566	62	142
2151	\N	\N	72	142
2152	\N	567	63	142
2153	\N	567	81	142
2154	\N	\N	64	142
2155	\N	565	488	142
2156	\N	567	489	142
2157	\N	\N	70	142
2158	\N	566	65	142
2159	\N	\N	48	142
2160	\N	567	71	142
2161	\N	568	49	142
2162	\N	567	490	142
2163	\N	566	66	142
2164	\N	568	67	142
2165	\N	568	74	142
2166	\N	567	68	142
2167	\N	567	69	142
2168	\N	567	77	142
2169	\N	572	50	143
2170	\N	572	51	143
2171	\N	\N	52	143
2172	\N	572	73	143
2173	\N	\N	53	143
2174	\N	572	54	143
2175	\N	572	46	143
2176	\N	\N	55	143
2177	\N	569	56	143
2178	\N	571	57	143
2179	\N	572	58	143
2180	\N	\N	59	143
2181	\N	569	487	143
2182	\N	572	80	143
2183	\N	572	75	143
2184	\N	569	491	143
2185	\N	\N	82	143
2186	\N	572	60	143
2187	\N	572	84	143
2188	\N	572	83	143
2189	\N	\N	61	143
2190	\N	570	76	143
2191	\N	569	79	143
2192	\N	\N	47	143
2193	\N	569	78	143
2194	\N	572	62	143
2195	\N	\N	72	143
2196	\N	569	63	143
2197	\N	569	81	143
2198	\N	\N	64	143
2199	\N	569	488	143
2200	\N	572	489	143
2201	\N	569	70	143
2202	\N	572	65	143
2203	\N	\N	48	143
2204	\N	\N	71	143
2205	\N	\N	49	143
2206	\N	572	490	143
2207	\N	\N	66	143
2208	\N	569	67	143
2209	\N	572	74	143
2210	\N	570	68	143
2211	\N	\N	69	143
2212	\N	\N	77	143
2213	\N	574	50	144
2214	\N	\N	51	144
2215	\N	\N	52	144
2216	\N	575	73	144
2217	\N	575	53	144
2218	\N	576	54	144
2219	\N	\N	46	144
2220	\N	574	55	144
2221	\N	576	56	144
2222	\N	\N	57	144
2223	\N	\N	58	144
2224	\N	\N	59	144
2225	\N	576	487	144
2226	\N	576	80	144
2227	\N	\N	75	144
2228	\N	574	491	144
2229	\N	576	82	144
2230	\N	576	60	144
2231	\N	576	84	144
2232	\N	\N	83	144
2233	\N	\N	61	144
2234	\N	\N	76	144
2235	\N	\N	79	144
2236	\N	\N	47	144
2237	\N	576	78	144
2238	\N	\N	62	144
2239	\N	574	72	144
2240	\N	\N	63	144
2241	\N	575	81	144
2242	\N	\N	64	144
2243	\N	574	488	144
2244	\N	575	489	144
2245	\N	576	70	144
2246	\N	574	65	144
2247	\N	\N	48	144
2248	\N	576	71	144
2249	\N	575	49	144
2250	\N	573	490	144
2251	\N	\N	66	144
2252	\N	\N	67	144
2253	\N	\N	74	144
2254	\N	576	68	144
2255	\N	575	69	144
2256	\N	\N	77	144
2261	\N	\N	53	145
2271	\N	\N	75	145
2277	\N	\N	61	145
2278	\N	\N	76	145
2286	\N	\N	64	145
2291	\N	\N	48	145
2292	\N	\N	71	145
2296	\N	\N	67	145
2297	\N	\N	74	145
2300	\N	\N	77	145
2301	\N	581	50	146
2302	\N	584	51	146
2303	\N	584	52	146
2304	\N	584	73	146
2305	\N	581	53	146
2306	\N	581	54	146
2307	\N	\N	46	146
2308	\N	584	55	146
2309	\N	581	56	146
2310	\N	581	57	146
2311	\N	584	58	146
2312	\N	584	59	146
2313	\N	584	487	146
2314	\N	581	80	146
2315	\N	\N	75	146
2316	\N	581	491	146
2317	\N	581	82	146
2318	\N	581	60	146
2319	\N	584	84	146
2320	\N	581	83	146
2321	\N	\N	61	146
2322	\N	584	76	146
2323	\N	581	79	146
2324	\N	584	47	146
2325	\N	584	78	146
2326	\N	\N	62	146
2327	\N	581	72	146
2328	\N	\N	63	146
2329	\N	584	81	146
2330	\N	581	64	146
2331	\N	581	488	146
2332	\N	581	489	146
2333	\N	581	70	146
2334	\N	581	65	146
2335	\N	\N	48	146
2336	\N	581	71	146
2337	\N	581	49	146
2338	\N	581	490	146
2339	\N	583	66	146
2340	\N	\N	67	146
2341	\N	584	74	146
2342	\N	584	68	146
2343	\N	581	69	146
2344	\N	581	77	146
2348	\N	\N	73	147
2351	\N	\N	46	147
2352	\N	\N	55	147
2354	\N	\N	57	147
2359	\N	\N	75	147
2361	\N	\N	82	147
2365	\N	\N	61	147
2366	\N	\N	76	147
2370	\N	\N	62	147
2379	\N	\N	48	147
2383	\N	\N	66	147
2388	\N	\N	77	147
2389	\N	591	50	148
2390	\N	592	51	148
2391	\N	590	52	148
2392	\N	589	73	148
2393	\N	591	53	148
2394	\N	592	54	148
2395	\N	\N	46	148
2396	\N	592	55	148
2397	\N	592	56	148
2398	\N	\N	57	148
2399	\N	590	58	148
2400	\N	592	59	148
2401	\N	591	487	148
2402	\N	592	80	148
2403	\N	589	75	148
2404	\N	591	491	148
2405	\N	589	82	148
2406	\N	\N	60	148
2407	\N	592	84	148
2408	\N	591	83	148
2409	\N	\N	61	148
2410	\N	592	76	148
2411	\N	589	79	148
2412	\N	590	47	148
2413	\N	590	78	148
2414	\N	\N	62	148
2415	\N	\N	72	148
2416	\N	\N	63	148
2417	\N	590	81	148
2418	\N	591	64	148
2419	\N	591	488	148
2420	\N	592	489	148
2421	\N	590	70	148
2422	\N	591	65	148
2423	\N	\N	48	148
2424	\N	\N	71	148
2425	\N	\N	49	148
2426	\N	592	490	148
2427	\N	592	66	148
2428	\N	590	67	148
2429	\N	\N	74	148
2430	\N	589	68	148
2431	\N	592	69	148
2432	\N	591	77	148
2433	\N	594	50	149
2434	\N	\N	51	149
2435	\N	596	52	149
2436	\N	596	73	149
2437	\N	594	53	149
2438	\N	596	54	149
2439	\N	596	46	149
2440	\N	594	55	149
2441	\N	596	56	149
2442	\N	596	57	149
2443	\N	593	58	149
2444	\N	594	59	149
2445	\N	596	487	149
2446	\N	593	80	149
2447	\N	\N	75	149
2448	\N	593	491	149
2449	\N	593	82	149
2450	\N	596	60	149
2451	\N	596	84	149
2452	\N	596	83	149
2453	\N	\N	61	149
2454	\N	596	76	149
2455	\N	594	79	149
2456	\N	596	47	149
2457	\N	596	78	149
2458	\N	593	62	149
2459	\N	593	72	149
2460	\N	\N	63	149
2461	\N	594	81	149
2462	\N	594	64	149
2463	\N	593	488	149
2464	\N	593	489	149
2465	\N	593	70	149
2466	\N	593	65	149
2467	\N	\N	48	149
2468	\N	594	71	149
2469	\N	594	49	149
2470	\N	596	490	149
2471	\N	593	66	149
2472	\N	596	67	149
2473	\N	593	74	149
2474	\N	596	68	149
2475	\N	593	69	149
2476	\N	\N	77	149
2479	\N	\N	52	150
2481	\N	\N	53	150
2483	\N	\N	46	150
2484	\N	\N	55	150
2491	\N	\N	75	150
2494	\N	\N	60	150
2495	\N	\N	84	150
2496	\N	\N	83	150
2497	\N	\N	61	150
2503	\N	\N	72	150
2504	\N	\N	63	150
2506	\N	\N	64	150
2515	\N	\N	66	150
2521	\N	603	99	151
2522	\N	602	90	151
2523	\N	601	100	151
2524	\N	603	101	151
2525	\N	602	91	151
2526	\N	\N	85	151
2527	\N	601	102	151
2528	\N	602	103	151
2529	\N	602	104	151
2530	\N	602	88	151
2531	\N	602	105	151
2532	\N	601	106	151
2533	\N	601	107	151
2534	\N	601	108	151
2535	\N	602	109	151
2536	\N	602	110	151
2537	\N	602	111	151
2538	\N	601	95	151
2539	\N	\N	86	151
2540	\N	603	112	151
2541	\N	603	113	151
2542	\N	602	89	151
2543	\N	601	96	151
2544	\N	601	114	151
2545	\N	603	92	151
2546	\N	\N	87	151
2547	\N	602	115	151
2548	\N	601	116	151
2549	\N	602	117	151
2550	\N	601	118	151
2551	\N	603	97	151
2552	\N	602	119	151
2553	\N	601	120	151
2554	\N	601	121	151
2555	\N	602	122	151
2556	\N	601	123	151
2557	\N	601	124	151
2558	\N	602	98	151
2559	\N	601	125	151
2560	\N	601	93	151
2561	\N	602	126	151
2562	\N	603	127	151
2563	\N	601	128	151
2564	\N	601	94	151
2565	\N	601	129	151
2575	\N	\N	88	152
2587	\N	\N	89	152
2612	\N	\N	90	153
2615	\N	\N	91	153
2629	\N	\N	86	153
2635	\N	\N	92	153
2650	\N	\N	93	153
2654	\N	\N	94	153
2656	\N	615	99	154
2657	\N	615	90	154
2658	\N	615	100	154
2659	\N	615	101	154
2660	\N	\N	91	154
2661	\N	\N	85	154
2662	\N	613	102	154
2663	\N	615	103	154
2664	\N	615	104	154
2665	\N	616	88	154
2666	\N	613	105	154
2667	\N	616	106	154
2668	\N	615	107	154
2669	\N	613	108	154
2670	\N	613	109	154
2671	\N	616	110	154
2672	\N	615	111	154
2673	\N	\N	95	154
2674	\N	615	86	154
2675	\N	616	112	154
2676	\N	613	113	154
2677	\N	615	89	154
2678	\N	\N	96	154
2679	\N	613	114	154
2680	\N	614	92	154
2681	\N	\N	87	154
2682	\N	616	115	154
2683	\N	613	116	154
2684	\N	613	117	154
2685	\N	613	118	154
2686	\N	\N	97	154
2687	\N	613	119	154
2688	\N	615	120	154
2689	\N	614	121	154
2690	\N	615	122	154
2691	\N	613	123	154
2692	\N	616	124	154
2693	\N	\N	98	154
2694	\N	614	125	154
2695	\N	613	93	154
2696	\N	616	126	154
2697	\N	615	127	154
2698	\N	616	128	154
2699	\N	615	94	154
2700	\N	613	129	154
2701	\N	\N	99	155
2702	\N	\N	90	155
2703	\N	\N	100	155
2704	\N	\N	101	155
2705	\N	\N	91	155
2706	\N	\N	85	155
2707	\N	\N	102	155
2708	\N	\N	103	155
2709	\N	\N	104	155
2710	\N	\N	88	155
2711	\N	\N	105	155
2712	\N	\N	106	155
2713	\N	\N	107	155
2714	\N	\N	108	155
2715	\N	\N	109	155
2716	\N	\N	110	155
2717	\N	\N	111	155
2718	\N	\N	95	155
2719	\N	\N	86	155
2720	\N	\N	112	155
2721	\N	\N	113	155
2722	\N	\N	89	155
2723	\N	\N	96	155
2724	\N	\N	114	155
2725	\N	\N	92	155
2726	\N	\N	87	155
2727	\N	\N	115	155
2728	\N	\N	116	155
2729	\N	\N	117	155
2730	\N	\N	118	155
2731	\N	\N	97	155
2732	\N	\N	119	155
2733	\N	\N	120	155
2734	\N	\N	121	155
2735	\N	\N	122	155
2736	\N	\N	123	155
2737	\N	\N	124	155
2738	\N	\N	98	155
2739	\N	\N	125	155
2740	\N	\N	93	155
2741	\N	\N	126	155
2742	\N	\N	127	155
2743	\N	\N	128	155
2744	\N	\N	94	155
2745	\N	\N	129	155
2746	\N	624	99	156
2747	\N	624	90	156
2748	\N	624	100	156
2749	\N	622	101	156
2750	\N	622	91	156
2751	\N	622	85	156
2752	\N	624	102	156
2753	\N	624	103	156
2754	\N	621	104	156
2755	\N	623	88	156
2756	\N	624	105	156
2757	\N	\N	106	156
2758	\N	\N	107	156
2759	\N	623	108	156
2760	\N	621	109	156
2761	\N	624	110	156
2762	\N	622	111	156
2763	\N	622	95	156
2764	\N	\N	86	156
2765	\N	623	112	156
2766	\N	\N	113	156
2767	\N	622	89	156
2768	\N	\N	96	156
2769	\N	621	114	156
2770	\N	622	92	156
2771	\N	\N	87	156
2772	\N	624	115	156
2773	\N	621	116	156
2774	\N	621	117	156
2775	\N	623	118	156
2776	\N	624	97	156
2777	\N	623	119	156
2778	\N	\N	120	156
2779	\N	624	121	156
2780	\N	624	122	156
2781	\N	621	123	156
2782	\N	623	124	156
2783	\N	\N	98	156
2784	\N	622	125	156
2785	\N	623	93	156
2786	\N	621	126	156
2787	\N	624	127	156
2788	\N	622	128	156
2789	\N	622	94	156
2790	\N	622	129	156
2791	\N	627	99	157
2792	\N	627	90	157
2793	\N	626	100	157
2794	\N	627	101	157
2795	\N	\N	91	157
2796	\N	627	85	157
2797	\N	626	102	157
2798	\N	627	103	157
2799	\N	627	104	157
2800	\N	\N	88	157
2801	\N	627	105	157
2802	\N	627	106	157
2803	\N	625	107	157
2804	\N	625	108	157
2805	\N	627	109	157
2806	\N	625	110	157
2807	\N	627	111	157
2808	\N	626	95	157
2809	\N	627	86	157
2810	\N	626	112	157
2811	\N	627	113	157
2812	\N	627	89	157
2813	\N	627	96	157
2814	\N	627	114	157
2815	\N	627	92	157
2816	\N	627	87	157
2817	\N	625	115	157
2818	\N	626	116	157
2819	\N	625	117	157
2820	\N	625	118	157
2821	\N	627	97	157
2822	\N	625	119	157
2823	\N	626	120	157
2824	\N	627	121	157
2825	\N	626	122	157
2826	\N	627	123	157
2827	\N	626	124	157
2828	\N	625	98	157
2829	\N	627	125	157
2830	\N	627	93	157
2831	\N	625	126	157
2832	\N	625	127	157
2833	\N	627	128	157
2834	\N	\N	94	157
2835	\N	\N	129	157
2836	\N	631	99	158
2837	\N	631	90	158
2838	\N	631	100	158
2839	\N	631	101	158
2840	\N	631	91	158
2841	\N	631	85	158
2842	\N	631	102	158
2843	\N	631	103	158
2844	\N	631	104	158
2845	\N	631	88	158
2846	\N	632	105	158
2847	\N	631	106	158
2848	\N	631	107	158
2849	\N	632	108	158
2850	\N	631	109	158
2851	\N	630	110	158
2852	\N	631	111	158
2853	\N	631	95	158
2854	\N	631	86	158
2855	\N	631	112	158
2856	\N	631	113	158
2857	\N	631	89	158
2858	\N	631	96	158
2859	\N	631	114	158
2860	\N	631	92	158
2861	\N	631	87	158
2862	\N	629	115	158
2863	\N	\N	116	158
2864	\N	631	117	158
2865	\N	632	118	158
2866	\N	631	97	158
2867	\N	631	119	158
2868	\N	629	120	158
2869	\N	631	121	158
2870	\N	631	122	158
2871	\N	631	123	158
2872	\N	630	124	158
2873	\N	632	98	158
2874	\N	631	125	158
2875	\N	631	93	158
2876	\N	631	126	158
2877	\N	631	127	158
2878	\N	632	128	158
2879	\N	631	94	158
2880	\N	631	129	158
2881	\N	636	99	159
2882	\N	636	90	159
2883	\N	635	100	159
2884	\N	636	101	159
2885	\N	636	91	159
2886	\N	636	85	159
2887	\N	636	102	159
2888	\N	636	103	159
2889	\N	635	104	159
2890	\N	636	88	159
2891	\N	636	105	159
2892	\N	636	106	159
2893	\N	635	107	159
2894	\N	635	108	159
2895	\N	636	109	159
2896	\N	634	110	159
2897	\N	636	111	159
2898	\N	636	95	159
2899	\N	634	86	159
2900	\N	636	112	159
2901	\N	\N	113	159
2902	\N	636	89	159
2903	\N	636	96	159
2904	\N	636	114	159
2905	\N	636	92	159
2906	\N	635	87	159
2907	\N	636	115	159
2908	\N	635	116	159
2909	\N	636	117	159
2910	\N	636	118	159
2911	\N	635	97	159
2912	\N	634	119	159
2913	\N	636	120	159
2914	\N	636	121	159
2915	\N	634	122	159
2916	\N	635	123	159
2917	\N	634	124	159
2918	\N	636	98	159
2919	\N	635	125	159
2920	\N	635	93	159
2921	\N	635	126	159
2922	\N	636	127	159
2923	\N	634	128	159
2924	\N	635	94	159
2925	\N	634	129	159
2927	\N	\N	90	160
2934	\N	\N	104	160
2953	\N	\N	116	160
2971	\N	642	99	161
2972	\N	\N	90	161
2973	\N	642	100	161
2974	\N	642	101	161
2975	\N	\N	91	161
2976	\N	\N	85	161
2977	\N	642	102	161
2978	\N	\N	103	161
2979	\N	\N	104	161
2980	\N	644	88	161
2981	\N	642	105	161
2982	\N	642	106	161
2983	\N	642	107	161
2984	\N	642	108	161
2985	\N	\N	109	161
2986	\N	641	110	161
2987	\N	642	111	161
2988	\N	\N	95	161
2989	\N	642	86	161
2990	\N	642	112	161
2991	\N	642	113	161
2992	\N	642	89	161
2993	\N	642	96	161
2994	\N	641	114	161
2995	\N	641	92	161
2996	\N	642	87	161
2997	\N	641	115	161
2998	\N	\N	116	161
2999	\N	641	117	161
3000	\N	\N	118	161
3001	\N	\N	97	161
3002	\N	643	119	161
3003	\N	641	120	161
3004	\N	644	121	161
3005	\N	642	122	161
3006	\N	641	123	161
3007	\N	642	124	161
3008	\N	642	98	161
3009	\N	642	125	161
3010	\N	642	93	161
3011	\N	642	126	161
3012	\N	\N	127	161
3013	\N	642	128	161
3014	\N	642	94	161
3015	\N	\N	129	161
3016	\N	646	99	162
3017	\N	646	90	162
3018	\N	\N	100	162
3019	\N	646	101	162
3020	\N	646	91	162
3021	\N	\N	85	162
3022	\N	\N	102	162
3023	\N	646	103	162
3024	\N	\N	104	162
3025	\N	\N	88	162
3026	\N	646	105	162
3027	\N	\N	106	162
3028	\N	646	107	162
3029	\N	645	108	162
3030	\N	646	109	162
3031	\N	648	110	162
3032	\N	646	111	162
3033	\N	646	95	162
3034	\N	646	86	162
3035	\N	646	112	162
3036	\N	646	113	162
3037	\N	\N	89	162
3038	\N	646	96	162
3039	\N	646	114	162
3040	\N	647	92	162
3041	\N	646	87	162
3042	\N	646	115	162
3043	\N	648	116	162
3044	\N	646	117	162
3045	\N	648	118	162
3046	\N	646	97	162
3047	\N	648	119	162
3048	\N	646	120	162
3049	\N	647	121	162
3050	\N	648	122	162
3051	\N	646	123	162
3052	\N	\N	124	162
3053	\N	646	98	162
3054	\N	646	125	162
3055	\N	646	93	162
3056	\N	647	126	162
3057	\N	\N	127	162
3058	\N	646	128	162
3059	\N	646	94	162
3060	\N	646	129	162
3061	\N	650	99	163
3062	\N	649	90	163
3063	\N	650	100	163
3064	\N	\N	101	163
3065	\N	651	91	163
3066	\N	649	85	163
3067	\N	649	102	163
3068	\N	651	103	163
3069	\N	649	104	163
3070	\N	650	88	163
3071	\N	649	105	163
3072	\N	651	106	163
3073	\N	650	107	163
3074	\N	649	108	163
3075	\N	649	109	163
3076	\N	650	110	163
3077	\N	649	111	163
3078	\N	650	95	163
3079	\N	\N	86	163
3080	\N	649	112	163
3081	\N	651	113	163
3082	\N	649	89	163
3083	\N	649	96	163
3084	\N	649	114	163
3085	\N	649	92	163
3086	\N	\N	87	163
3087	\N	651	115	163
3088	\N	650	116	163
3089	\N	650	117	163
3090	\N	650	118	163
3091	\N	649	97	163
3092	\N	650	119	163
3093	\N	649	120	163
3094	\N	649	121	163
3095	\N	649	122	163
3096	\N	650	123	163
3097	\N	651	124	163
3098	\N	649	98	163
3099	\N	651	125	163
3100	\N	652	93	163
3101	\N	650	126	163
3102	\N	649	127	163
3103	\N	649	128	163
3104	\N	650	94	163
3105	\N	649	129	163
3151	\N	657	99	165
3152	\N	657	90	165
3153	\N	\N	100	165
3154	\N	\N	101	165
3155	\N	657	91	165
3156	\N	657	85	165
3157	\N	658	102	165
3158	\N	658	103	165
3159	\N	\N	104	165
3160	\N	658	88	165
3161	\N	660	105	165
3162	\N	659	106	165
3163	\N	657	107	165
3164	\N	658	108	165
3165	\N	657	109	165
3166	\N	658	110	165
3167	\N	660	111	165
3168	\N	658	95	165
3169	\N	657	86	165
3170	\N	658	112	165
3171	\N	658	113	165
3172	\N	658	89	165
3173	\N	657	96	165
3174	\N	658	114	165
3175	\N	657	92	165
3176	\N	657	87	165
3177	\N	659	115	165
3178	\N	660	116	165
3179	\N	658	117	165
3180	\N	658	118	165
3181	\N	658	97	165
3182	\N	\N	119	165
3183	\N	657	120	165
3184	\N	658	121	165
3185	\N	660	122	165
3186	\N	658	123	165
3187	\N	658	124	165
3188	\N	658	98	165
3189	\N	660	125	165
3190	\N	658	93	165
3191	\N	657	126	165
3192	\N	659	127	165
3193	\N	658	128	165
3194	\N	658	94	165
3195	\N	657	129	165
3196	\N	663	99	166
3197	\N	663	90	166
3198	\N	663	100	166
3199	\N	663	101	166
3200	\N	663	91	166
3201	\N	663	85	166
3202	\N	663	102	166
3203	\N	663	103	166
3204	\N	663	104	166
3205	\N	663	88	166
3206	\N	663	105	166
3207	\N	663	106	166
3208	\N	663	107	166
3209	\N	663	108	166
3210	\N	663	109	166
3211	\N	663	110	166
3212	\N	663	111	166
3213	\N	663	95	166
3214	\N	663	86	166
3215	\N	663	112	166
3216	\N	663	113	166
3217	\N	663	89	166
3218	\N	663	96	166
3219	\N	663	114	166
3220	\N	663	92	166
3221	\N	663	87	166
3222	\N	663	115	166
3223	\N	661	116	166
3224	\N	663	117	166
3225	\N	663	118	166
3226	\N	663	97	166
3227	\N	664	119	166
3228	\N	664	120	166
3229	\N	663	121	166
3230	\N	662	122	166
3231	\N	663	123	166
3232	\N	663	124	166
3233	\N	663	98	166
3234	\N	663	125	166
3235	\N	663	93	166
3236	\N	663	126	166
3237	\N	663	127	166
3238	\N	663	128	166
3239	\N	663	94	166
3240	\N	663	129	166
3241	\N	666	99	167
3242	\N	665	90	167
3243	\N	\N	100	167
3244	\N	665	101	167
3245	\N	666	91	167
3246	\N	\N	85	167
3247	\N	666	102	167
3248	\N	665	103	167
3249	\N	666	104	167
3250	\N	\N	88	167
3251	\N	665	105	167
3252	\N	666	106	167
3253	\N	665	107	167
3254	\N	666	108	167
3255	\N	665	109	167
3256	\N	665	110	167
3257	\N	\N	111	167
3258	\N	665	95	167
3259	\N	\N	86	167
3260	\N	665	112	167
3261	\N	666	113	167
3262	\N	666	89	167
3263	\N	665	96	167
3264	\N	665	114	167
3265	\N	665	92	167
3266	\N	666	87	167
3267	\N	666	115	167
3268	\N	665	116	167
3269	\N	665	117	167
3270	\N	666	118	167
3271	\N	665	97	167
3272	\N	668	119	167
3273	\N	668	120	167
3274	\N	665	121	167
3275	\N	665	122	167
3276	\N	666	123	167
3277	\N	666	124	167
3278	\N	665	98	167
3279	\N	665	125	167
3280	\N	666	93	167
3281	\N	665	126	167
3282	\N	665	127	167
3283	\N	665	128	167
3284	\N	666	94	167
3285	\N	666	129	167
3286	\N	669	99	168
3287	\N	669	90	168
3288	\N	\N	100	168
3289	\N	669	101	168
3290	\N	669	91	168
3291	\N	\N	85	168
3292	\N	669	102	168
3293	\N	669	103	168
3294	\N	669	104	168
3295	\N	669	88	168
3296	\N	670	105	168
3297	\N	\N	106	168
3298	\N	669	107	168
3299	\N	669	108	168
3300	\N	669	109	168
3301	\N	669	110	168
3302	\N	669	111	168
3303	\N	669	95	168
3304	\N	669	86	168
3305	\N	669	112	168
3306	\N	669	113	168
3307	\N	669	89	168
3308	\N	669	96	168
3309	\N	669	114	168
3310	\N	669	92	168
3311	\N	\N	87	168
3312	\N	669	115	168
3313	\N	672	116	168
3314	\N	672	117	168
3315	\N	672	118	168
3316	\N	669	97	168
3317	\N	672	119	168
3318	\N	672	120	168
3319	\N	669	121	168
3320	\N	671	122	168
3321	\N	669	123	168
3322	\N	669	124	168
3323	\N	669	98	168
3324	\N	669	125	168
3325	\N	669	93	168
3326	\N	670	126	168
3327	\N	671	127	168
3328	\N	669	128	168
3329	\N	669	94	168
3330	\N	669	129	168
3331	\N	674	99	169
3332	\N	673	90	169
3333	\N	673	100	169
3334	\N	673	101	169
3335	\N	675	91	169
3336	\N	673	85	169
3337	\N	673	102	169
3338	\N	675	103	169
3339	\N	673	104	169
3340	\N	673	88	169
3341	\N	673	105	169
3342	\N	673	106	169
3343	\N	673	107	169
3344	\N	676	108	169
3345	\N	673	109	169
3346	\N	673	110	169
3347	\N	673	111	169
3348	\N	673	95	169
3349	\N	673	86	169
3350	\N	673	112	169
3351	\N	673	113	169
3352	\N	673	89	169
3353	\N	675	96	169
3354	\N	673	114	169
3355	\N	676	92	169
3356	\N	\N	87	169
3357	\N	676	115	169
3358	\N	675	116	169
3359	\N	675	117	169
3360	\N	673	118	169
3361	\N	673	97	169
3362	\N	\N	119	169
3363	\N	673	120	169
3364	\N	675	121	169
3365	\N	673	122	169
3366	\N	675	123	169
3367	\N	674	124	169
3368	\N	673	98	169
3369	\N	\N	125	169
3370	\N	675	93	169
3371	\N	673	126	169
3372	\N	673	127	169
3373	\N	673	128	169
3374	\N	673	94	169
3375	\N	673	129	169
3376	\N	680	99	170
3377	\N	679	90	170
3378	\N	677	100	170
3379	\N	679	101	170
3380	\N	678	91	170
3381	\N	679	85	170
3382	\N	679	102	170
3383	\N	679	103	170
3384	\N	679	104	170
3385	\N	680	88	170
3386	\N	678	105	170
3387	\N	677	106	170
3388	\N	679	107	170
3389	\N	679	108	170
3390	\N	677	109	170
3391	\N	678	110	170
3392	\N	679	111	170
3393	\N	679	95	170
3394	\N	679	86	170
3395	\N	678	112	170
3396	\N	679	113	170
3397	\N	679	89	170
3398	\N	679	96	170
3399	\N	679	114	170
3400	\N	679	92	170
3401	\N	\N	87	170
3402	\N	679	115	170
3403	\N	\N	116	170
3404	\N	677	117	170
3405	\N	679	118	170
3406	\N	677	97	170
3407	\N	\N	119	170
3408	\N	680	120	170
3409	\N	678	121	170
3410	\N	679	122	170
3411	\N	679	123	170
3412	\N	\N	124	170
3413	\N	679	98	170
3414	\N	677	125	170
3415	\N	680	93	170
3416	\N	679	126	170
3417	\N	678	127	170
3418	\N	679	128	170
3419	\N	\N	94	170
3420	\N	679	129	170
3421	\N	681	99	171
3422	\N	\N	90	171
3423	\N	682	100	171
3424	\N	\N	101	171
3425	\N	\N	91	171
3426	\N	\N	85	171
3427	\N	681	102	171
3428	\N	\N	103	171
3429	\N	\N	104	171
3430	\N	\N	88	171
3431	\N	683	105	171
3432	\N	681	106	171
3433	\N	682	107	171
3434	\N	683	108	171
3435	\N	681	109	171
3436	\N	\N	110	171
3437	\N	681	111	171
3438	\N	\N	95	171
3439	\N	683	86	171
3440	\N	682	112	171
3441	\N	683	113	171
3442	\N	681	89	171
3443	\N	683	96	171
3444	\N	681	114	171
3445	\N	\N	92	171
3446	\N	\N	87	171
3447	\N	681	115	171
3448	\N	682	116	171
3449	\N	683	117	171
3450	\N	682	118	171
3451	\N	683	97	171
3452	\N	681	119	171
3453	\N	681	120	171
3454	\N	683	121	171
3455	\N	681	122	171
3456	\N	\N	123	171
3457	\N	683	124	171
3458	\N	\N	98	171
3459	\N	681	125	171
3460	\N	683	93	171
3461	\N	683	126	171
3462	\N	683	127	171
3463	\N	681	128	171
3464	\N	683	94	171
3465	\N	681	129	171
3466	\N	\N	99	172
3467	\N	687	90	172
3468	\N	687	100	172
3469	\N	\N	101	172
3470	\N	686	91	172
3471	\N	686	85	172
3472	\N	688	102	172
3473	\N	\N	103	172
3474	\N	\N	104	172
3475	\N	\N	88	172
3476	\N	686	105	172
3477	\N	\N	106	172
3478	\N	\N	107	172
3479	\N	686	108	172
3480	\N	686	109	172
3481	\N	687	110	172
3482	\N	685	111	172
3483	\N	686	95	172
3484	\N	688	86	172
3485	\N	686	112	172
3486	\N	686	113	172
3487	\N	\N	89	172
3488	\N	687	96	172
3489	\N	687	114	172
3490	\N	\N	92	172
3491	\N	\N	87	172
3492	\N	687	115	172
3493	\N	\N	116	172
3494	\N	685	117	172
3495	\N	687	118	172
3496	\N	\N	97	172
3497	\N	\N	119	172
3498	\N	688	120	172
3499	\N	686	121	172
3500	\N	688	122	172
3501	\N	686	123	172
3502	\N	\N	124	172
3503	\N	688	98	172
3504	\N	\N	125	172
3505	\N	687	93	172
3506	\N	688	126	172
3507	\N	\N	127	172
3508	\N	688	128	172
3509	\N	687	94	172
3510	\N	\N	129	172
3511	\N	691	99	173
3512	\N	\N	90	173
3513	\N	690	100	173
3514	\N	690	101	173
3515	\N	\N	91	173
3516	\N	\N	85	173
3517	\N	690	102	173
3518	\N	\N	103	173
3519	\N	690	104	173
3520	\N	\N	88	173
3521	\N	690	105	173
3522	\N	690	106	173
3523	\N	691	107	173
3524	\N	690	108	173
3525	\N	690	109	173
3526	\N	692	110	173
3527	\N	690	111	173
3528	\N	\N	95	173
3529	\N	690	86	173
3530	\N	690	112	173
3531	\N	689	113	173
3532	\N	690	89	173
3533	\N	690	96	173
3534	\N	690	114	173
3535	\N	\N	92	173
3536	\N	\N	87	173
3537	\N	690	115	173
3538	\N	\N	116	173
3539	\N	692	117	173
3540	\N	691	118	173
3541	\N	\N	97	173
3542	\N	691	119	173
3543	\N	690	120	173
3544	\N	690	121	173
3545	\N	692	122	173
3546	\N	\N	123	173
3547	\N	\N	124	173
3548	\N	691	98	173
3549	\N	\N	125	173
3550	\N	\N	93	173
3551	\N	690	126	173
3552	\N	691	127	173
3553	\N	692	128	173
3554	\N	\N	94	173
3555	\N	\N	129	173
3556	\N	\N	99	174
3557	\N	\N	90	174
3558	\N	\N	100	174
3559	\N	696	101	174
3560	\N	696	91	174
3561	\N	\N	85	174
3562	\N	696	102	174
3563	\N	\N	103	174
3564	\N	\N	104	174
3565	\N	\N	88	174
3566	\N	696	105	174
3567	\N	\N	106	174
3568	\N	695	107	174
3569	\N	695	108	174
3570	\N	\N	109	174
3571	\N	696	110	174
3572	\N	696	111	174
3573	\N	696	95	174
3574	\N	\N	86	174
3575	\N	696	112	174
3576	\N	\N	113	174
3577	\N	696	89	174
3578	\N	696	96	174
3579	\N	\N	114	174
3580	\N	\N	92	174
3581	\N	696	87	174
3582	\N	696	115	174
3583	\N	\N	116	174
3584	\N	696	117	174
3585	\N	696	118	174
3586	\N	695	97	174
3587	\N	694	119	174
3588	\N	\N	120	174
3589	\N	696	121	174
3590	\N	695	122	174
3591	\N	694	123	174
3592	\N	\N	124	174
3593	\N	696	98	174
3594	\N	696	125	174
3595	\N	\N	93	174
3596	\N	696	126	174
3597	\N	\N	127	174
3598	\N	696	128	174
3599	\N	\N	94	174
3600	\N	\N	129	174
3601	\N	699	99	175
3602	\N	700	90	175
3603	\N	699	100	175
3604	\N	\N	101	175
3605	\N	698	91	175
3606	\N	\N	85	175
3607	\N	\N	102	175
3608	\N	700	103	175
3609	\N	698	104	175
3610	\N	699	88	175
3611	\N	699	105	175
3612	\N	700	106	175
3613	\N	700	107	175
3614	\N	697	108	175
3615	\N	700	109	175
3616	\N	699	110	175
3617	\N	699	111	175
3618	\N	700	95	175
3619	\N	697	86	175
3620	\N	699	112	175
3621	\N	697	113	175
3622	\N	700	89	175
3623	\N	700	96	175
3624	\N	700	114	175
3625	\N	699	92	175
3626	\N	700	87	175
3627	\N	699	115	175
3628	\N	699	116	175
3629	\N	697	117	175
3630	\N	698	118	175
3631	\N	700	97	175
3632	\N	697	119	175
3633	\N	699	120	175
3634	\N	700	121	175
3635	\N	697	122	175
3636	\N	700	123	175
3637	\N	700	124	175
3638	\N	\N	98	175
3639	\N	697	125	175
3640	\N	698	93	175
3641	\N	700	126	175
3642	\N	697	127	175
3643	\N	\N	128	175
3644	\N	697	94	175
3645	\N	\N	129	175
3646	\N	\N	99	176
3647	\N	701	90	176
3648	\N	\N	100	176
3649	\N	\N	101	176
3650	\N	\N	91	176
3651	\N	701	85	176
3652	\N	701	102	176
3653	\N	\N	103	176
3654	\N	\N	104	176
3655	\N	\N	88	176
3656	\N	701	105	176
3657	\N	701	106	176
3658	\N	704	107	176
3659	\N	701	108	176
3660	\N	701	109	176
3661	\N	702	110	176
3662	\N	\N	111	176
3663	\N	\N	95	176
3664	\N	\N	86	176
3665	\N	704	112	176
3666	\N	704	113	176
3667	\N	704	89	176
3668	\N	\N	96	176
3669	\N	704	114	176
3670	\N	701	92	176
3671	\N	\N	87	176
3672	\N	\N	115	176
3673	\N	\N	116	176
3674	\N	704	117	176
3675	\N	704	118	176
3676	\N	\N	97	176
3677	\N	\N	119	176
3678	\N	704	120	176
3679	\N	704	121	176
3680	\N	\N	122	176
3681	\N	\N	123	176
3682	\N	701	124	176
3683	\N	701	98	176
3684	\N	701	125	176
3685	\N	702	93	176
3686	\N	701	126	176
3687	\N	\N	127	176
3688	\N	\N	128	176
3689	\N	704	94	176
3690	\N	\N	129	176
3691	\N	\N	99	177
3692	\N	707	90	177
3693	\N	705	100	177
3694	\N	\N	101	177
3695	\N	706	91	177
3696	\N	\N	85	177
3697	\N	\N	102	177
3698	\N	706	103	177
3699	\N	705	104	177
3700	\N	705	88	177
3701	\N	706	105	177
3702	\N	707	106	177
3703	\N	707	107	177
3704	\N	707	108	177
3705	\N	\N	109	177
3706	\N	705	110	177
3707	\N	\N	111	177
3708	\N	705	95	177
3709	\N	706	86	177
3710	\N	705	112	177
3711	\N	705	113	177
3712	\N	706	89	177
3713	\N	\N	96	177
3714	\N	707	114	177
3715	\N	706	92	177
3716	\N	\N	87	177
3717	\N	707	115	177
3718	\N	\N	116	177
3719	\N	707	117	177
3720	\N	706	118	177
3721	\N	705	97	177
3722	\N	708	119	177
3723	\N	708	120	177
3724	\N	707	121	177
3725	\N	708	122	177
3726	\N	705	123	177
3727	\N	707	124	177
3728	\N	707	98	177
3729	\N	705	125	177
3730	\N	706	93	177
3731	\N	706	126	177
3732	\N	706	127	177
3733	\N	706	128	177
3734	\N	707	94	177
3735	\N	\N	129	177
3736	\N	\N	99	178
3737	\N	\N	90	178
3738	\N	\N	100	178
3739	\N	\N	101	178
3740	\N	\N	91	178
3741	\N	\N	85	178
3742	\N	\N	102	178
3743	\N	\N	103	178
3744	\N	\N	104	178
3745	\N	\N	88	178
3746	\N	\N	105	178
3747	\N	\N	106	178
3748	\N	\N	107	178
3749	\N	\N	108	178
3750	\N	\N	109	178
3751	\N	\N	110	178
3752	\N	\N	111	178
3753	\N	\N	95	178
3754	\N	\N	86	178
3755	\N	\N	112	178
3756	\N	\N	113	178
3757	\N	\N	89	178
3758	\N	\N	96	178
3759	\N	\N	114	178
3760	\N	\N	92	178
3761	\N	\N	87	178
3762	\N	\N	115	178
3763	\N	\N	116	178
3764	\N	\N	117	178
3765	\N	\N	118	178
3766	\N	\N	97	178
3767	\N	\N	119	178
3768	\N	\N	120	178
3769	\N	\N	121	178
3770	\N	\N	122	178
3771	\N	\N	123	178
3772	\N	\N	124	178
3773	\N	\N	98	178
3774	\N	\N	125	178
3775	\N	\N	93	178
3776	\N	\N	126	178
3777	\N	\N	127	178
3778	\N	\N	128	178
3779	\N	\N	94	178
3780	\N	\N	129	178
3781	\N	\N	99	179
3782	\N	713	90	179
3783	\N	715	100	179
3784	\N	715	101	179
3785	\N	715	91	179
3786	\N	\N	85	179
3787	\N	715	102	179
3788	\N	\N	103	179
3789	\N	\N	104	179
3790	\N	715	88	179
3791	\N	713	105	179
3792	\N	715	106	179
3793	\N	715	107	179
3794	\N	713	108	179
3795	\N	715	109	179
3796	\N	716	110	179
3797	\N	714	111	179
3798	\N	\N	95	179
3799	\N	713	86	179
3800	\N	713	112	179
3801	\N	715	113	179
3802	\N	715	89	179
3803	\N	\N	96	179
3804	\N	715	114	179
3805	\N	715	92	179
3806	\N	\N	87	179
3807	\N	713	115	179
3808	\N	\N	116	179
3809	\N	713	117	179
3810	\N	713	118	179
3811	\N	\N	97	179
3812	\N	\N	119	179
3813	\N	713	120	179
3814	\N	713	121	179
3815	\N	716	122	179
3816	\N	\N	123	179
3817	\N	715	124	179
3818	\N	714	98	179
3819	\N	\N	125	179
3820	\N	715	93	179
3821	\N	715	126	179
3822	\N	715	127	179
3823	\N	716	128	179
3824	\N	715	94	179
3825	\N	\N	129	179
3826	\N	717	99	180
3827	\N	717	90	180
3828	\N	\N	100	180
3829	\N	\N	101	180
3830	\N	\N	91	180
3831	\N	\N	85	180
3832	\N	717	102	180
3833	\N	718	103	180
3834	\N	717	104	180
3835	\N	\N	88	180
3836	\N	717	105	180
3837	\N	717	106	180
3838	\N	717	107	180
3839	\N	717	108	180
3840	\N	717	109	180
3841	\N	718	110	180
3842	\N	717	111	180
3843	\N	717	95	180
3844	\N	\N	86	180
3845	\N	717	112	180
3846	\N	717	113	180
3847	\N	717	89	180
3848	\N	717	96	180
3849	\N	717	114	180
3850	\N	717	92	180
3851	\N	\N	87	180
3852	\N	717	115	180
3853	\N	\N	116	180
3854	\N	717	117	180
3855	\N	717	118	180
3856	\N	717	97	180
3857	\N	717	119	180
3858	\N	717	120	180
3859	\N	718	121	180
3860	\N	718	122	180
3861	\N	\N	123	180
3862	\N	718	124	180
3863	\N	717	98	180
3864	\N	717	125	180
3865	\N	717	93	180
3866	\N	717	126	180
3867	\N	\N	127	180
3868	\N	719	128	180
3869	\N	\N	94	180
3870	\N	717	129	180
3965	\N	\N	130	182
4019	\N	\N	130	183
4721	\N	\N	130	196
4775	\N	\N	130	197
4746	\N	886	519	197
4751	\N	886	495	197
4768	\N	886	498	197
4741	\N	885	492	197
4742	\N	885	518	197
4744	\N	885	502	197
4745	\N	885	493	197
5099	\N	\N	130	203
5153	\N	\N	130	204
5369	\N	\N	130	208
5491	\N	842	554	211
5492	\N	843	582	211
5493	\N	842	555	211
5494	\N	842	556	211
5495	\N	842	557	211
5496	\N	841	545	211
5497	\N	842	558	211
5498	\N	842	559	211
5499	\N	841	546	211
5500	\N	843	583	211
5501	\N	842	560	211
5502	\N	844	597	211
5503	\N	842	561	211
5504	\N	842	562	211
5505	\N	843	584	211
5506	\N	842	563	211
5507	\N	843	585	211
5508	\N	842	564	211
5509	\N	841	547	211
5510	\N	843	586	211
5511	\N	842	565	211
5512	\N	843	587	211
5513	\N	842	566	211
5514	\N	841	548	211
5515	\N	841	549	211
5516	\N	842	567	211
5517	\N	842	568	211
5518	\N	842	569	211
5519	\N	842	570	211
5520	\N	841	550	211
5521	\N	843	588	211
5522	\N	842	132	211
5523	\N	843	589	211
5524	\N	842	571	211
5525	\N	\N	131	211
5526	\N	842	572	211
5527	\N	842	573	211
5528	\N	842	574	211
5529	\N	843	134	211
5530	\N	843	590	211
5531	\N	841	551	211
5532	\N	842	575	211
5533	\N	844	598	211
5534	\N	842	576	211
5535	\N	843	133	211
5536	\N	843	591	211
5537	\N	842	577	211
5538	\N	844	599	211
5539	\N	842	578	211
5540	\N	841	552	211
5541	\N	843	592	211
5542	\N	843	593	211
5543	\N	843	594	211
5544	\N	842	579	211
5545	\N	844	600	211
5546	\N	843	595	211
5547	\N	844	601	211
5548	\N	841	553	211
5549	\N	843	596	211
5550	\N	842	580	211
5551	\N	842	581	211
5552	\N	847	554	212
5553	\N	845	582	212
5554	\N	845	555	212
5555	\N	847	556	212
5556	\N	847	557	212
5557	\N	847	545	212
5558	\N	847	558	212
5559	\N	848	559	212
5560	\N	847	546	212
5561	\N	845	583	212
5562	\N	847	560	212
5563	\N	846	597	212
5564	\N	847	561	212
5565	\N	845	562	212
5566	\N	846	584	212
5567	\N	847	563	212
5568	\N	846	585	212
5569	\N	845	564	212
5570	\N	845	547	212
5571	\N	846	586	212
5572	\N	846	565	212
5573	\N	846	587	212
5574	\N	845	566	212
5575	\N	846	548	212
5576	\N	848	549	212
5577	\N	847	567	212
5578	\N	847	568	212
5579	\N	847	569	212
5580	\N	847	570	212
5581	\N	847	550	212
5582	\N	847	588	212
5583	\N	847	132	212
5584	\N	845	589	212
5585	\N	847	571	212
5586	\N	845	131	212
5587	\N	847	572	212
5588	\N	845	573	212
5589	\N	845	574	212
5590	\N	847	134	212
5591	\N	847	590	212
5592	\N	846	551	212
5593	\N	848	575	212
5594	\N	847	598	212
5595	\N	847	576	212
5596	\N	847	133	212
5597	\N	845	591	212
5598	\N	845	577	212
5599	\N	845	599	212
5600	\N	847	578	212
5601	\N	847	552	212
5602	\N	846	592	212
5603	\N	847	593	212
5604	\N	845	594	212
5605	\N	845	579	212
5606	\N	847	600	212
5607	\N	847	595	212
5608	\N	847	601	212
5609	\N	847	553	212
5610	\N	846	596	212
5611	\N	847	580	212
5612	\N	847	581	212
5674	\N	853	554	214
5675	\N	855	582	214
5676	\N	856	555	214
5677	\N	856	556	214
5678	\N	853	557	214
5679	\N	853	545	214
5680	\N	856	558	214
5681	\N	856	559	214
5682	\N	856	546	214
5683	\N	855	583	214
5684	\N	853	560	214
5685	\N	854	597	214
5686	\N	853	561	214
5687	\N	853	562	214
5688	\N	853	584	214
5689	\N	853	563	214
5690	\N	856	585	214
5691	\N	854	564	214
5692	\N	856	547	214
5693	\N	853	586	214
5694	\N	856	565	214
5695	\N	853	587	214
5696	\N	856	566	214
5697	\N	856	548	214
5698	\N	853	549	214
5699	\N	853	567	214
5700	\N	853	568	214
5701	\N	856	569	214
5702	\N	856	570	214
5703	\N	854	550	214
5704	\N	856	588	214
5705	\N	856	132	214
5706	\N	856	589	214
5707	\N	856	571	214
5708	\N	853	131	214
5709	\N	856	572	214
5710	\N	853	573	214
5711	\N	856	574	214
5712	\N	853	134	214
5713	\N	854	590	214
5714	\N	853	551	214
5715	\N	853	575	214
5716	\N	854	598	214
5717	\N	853	576	214
5718	\N	856	133	214
5719	\N	853	591	214
5720	\N	853	577	214
5721	\N	853	599	214
5722	\N	853	578	214
5723	\N	853	552	214
5724	\N	853	592	214
5725	\N	853	593	214
5726	\N	853	594	214
5727	\N	856	579	214
5728	\N	855	600	214
5729	\N	853	595	214
5730	\N	854	601	214
5731	\N	854	553	214
5732	\N	856	596	214
5733	\N	856	580	214
5734	\N	856	581	214
5735	\N	857	554	215
5736	\N	857	582	215
5737	\N	859	555	215
5738	\N	857	556	215
5739	\N	859	557	215
5740	\N	857	545	215
5741	\N	857	558	215
5742	\N	857	559	215
5743	\N	859	546	215
5744	\N	859	583	215
5745	\N	859	560	215
5746	\N	860	597	215
5747	\N	860	561	215
5748	\N	859	562	215
5749	\N	859	584	215
5750	\N	857	563	215
5751	\N	859	585	215
5752	\N	859	564	215
5753	\N	860	547	215
5754	\N	858	586	215
5755	\N	857	565	215
5756	\N	859	587	215
5757	\N	859	566	215
5758	\N	857	548	215
5759	\N	857	549	215
5760	\N	857	567	215
5761	\N	858	568	215
5762	\N	859	569	215
5763	\N	859	570	215
5764	\N	859	550	215
5765	\N	857	588	215
5766	\N	857	132	215
5767	\N	860	589	215
5768	\N	860	571	215
5769	\N	857	131	215
5770	\N	857	572	215
5771	\N	857	573	215
5772	\N	857	574	215
5773	\N	860	134	215
5774	\N	857	590	215
5775	\N	859	551	215
5776	\N	857	575	215
5777	\N	857	598	215
5778	\N	860	576	215
5779	\N	860	133	215
5780	\N	860	591	215
5781	\N	857	577	215
5782	\N	859	599	215
5783	\N	858	578	215
5784	\N	858	552	215
5785	\N	859	592	215
5786	\N	859	593	215
5787	\N	859	594	215
5788	\N	857	579	215
5789	\N	859	600	215
5790	\N	858	595	215
5791	\N	859	601	215
5792	\N	858	553	215
5793	\N	860	596	215
5794	\N	857	580	215
5795	\N	859	581	215
5796	\N	863	554	216
5797	\N	864	582	216
5798	\N	863	555	216
5799	\N	863	556	216
5800	\N	863	557	216
5801	\N	863	545	216
5802	\N	863	558	216
5803	\N	863	559	216
5804	\N	861	546	216
5805	\N	864	583	216
5806	\N	863	560	216
5807	\N	863	597	216
5808	\N	863	561	216
5809	\N	863	562	216
5810	\N	863	584	216
5811	\N	863	563	216
5812	\N	863	585	216
5813	\N	864	564	216
5814	\N	863	547	216
5815	\N	863	586	216
5816	\N	863	565	216
5817	\N	863	587	216
5818	\N	863	566	216
5819	\N	863	548	216
5820	\N	864	549	216
5821	\N	863	567	216
5822	\N	863	568	216
5823	\N	863	569	216
5824	\N	864	570	216
5825	\N	864	550	216
5826	\N	863	588	216
5827	\N	863	132	216
5828	\N	863	589	216
5829	\N	863	571	216
5830	\N	864	131	216
5831	\N	863	572	216
5832	\N	863	573	216
5833	\N	863	574	216
5834	\N	863	134	216
5835	\N	863	590	216
5836	\N	863	551	216
5837	\N	863	575	216
5838	\N	863	598	216
5839	\N	862	576	216
5840	\N	863	133	216
5841	\N	863	591	216
5842	\N	863	577	216
5843	\N	863	599	216
5844	\N	863	578	216
5845	\N	863	552	216
5846	\N	863	592	216
5847	\N	863	593	216
5848	\N	861	594	216
5849	\N	863	579	216
5850	\N	863	600	216
5851	\N	863	595	216
5852	\N	863	601	216
5853	\N	863	553	216
5854	\N	863	596	216
5855	\N	863	580	216
5856	\N	863	581	216
5918	\N	871	554	218
5919	\N	872	582	218
5920	\N	871	555	218
5921	\N	871	556	218
5922	\N	872	557	218
5923	\N	871	545	218
5924	\N	871	558	218
5925	\N	871	559	218
5926	\N	872	546	218
5927	\N	869	583	218
5928	\N	872	560	218
5929	\N	869	597	218
5930	\N	872	561	218
5931	\N	871	562	218
5932	\N	871	584	218
5933	\N	871	563	218
5934	\N	871	585	218
5935	\N	869	564	218
5936	\N	871	547	218
5937	\N	871	586	218
5938	\N	872	565	218
5939	\N	871	587	218
5940	\N	871	566	218
5941	\N	872	548	218
5942	\N	872	549	218
5943	\N	871	567	218
5944	\N	871	568	218
5945	\N	871	569	218
5946	\N	871	570	218
5947	\N	872	550	218
5948	\N	871	588	218
5949	\N	\N	132	218
5950	\N	872	589	218
5951	\N	871	571	218
5952	\N	\N	131	218
5953	\N	871	572	218
5954	\N	870	573	218
5955	\N	869	574	218
5956	\N	869	134	218
5957	\N	871	590	218
5958	\N	871	551	218
5959	\N	871	575	218
5960	\N	869	598	218
5961	\N	871	576	218
5962	\N	871	133	218
5963	\N	871	591	218
5964	\N	871	577	218
5965	\N	871	599	218
5966	\N	871	578	218
5967	\N	871	552	218
5968	\N	869	592	218
5969	\N	871	593	218
5970	\N	869	594	218
5971	\N	871	579	218
5972	\N	871	600	218
5973	\N	871	595	218
5974	\N	871	601	218
5975	\N	872	553	218
5976	\N	871	596	218
5977	\N	871	580	218
5978	\N	871	581	218
5979	\N	875	554	219
5980	\N	873	582	219
5981	\N	876	555	219
5982	\N	876	556	219
5983	\N	875	557	219
5984	\N	873	545	219
5985	\N	876	558	219
5986	\N	876	559	219
5987	\N	875	546	219
5988	\N	876	583	219
5989	\N	875	560	219
5990	\N	875	597	219
5991	\N	875	561	219
5992	\N	875	562	219
5993	\N	875	584	219
5994	\N	875	563	219
5995	\N	876	585	219
5996	\N	876	564	219
5997	\N	876	547	219
5998	\N	876	586	219
5999	\N	873	565	219
6000	\N	875	587	219
6001	\N	875	566	219
6002	\N	876	548	219
6003	\N	876	549	219
6004	\N	873	567	219
6005	\N	876	568	219
6006	\N	875	569	219
6007	\N	876	570	219
6008	\N	875	550	219
6009	\N	876	588	219
6010	\N	875	132	219
6011	\N	875	589	219
6012	\N	876	571	219
6013	\N	873	131	219
6014	\N	876	572	219
6015	\N	875	573	219
6016	\N	876	574	219
6017	\N	873	134	219
6018	\N	874	590	219
6019	\N	874	551	219
6020	\N	876	575	219
6021	\N	874	598	219
6022	\N	875	576	219
6023	\N	876	133	219
6024	\N	876	591	219
6025	\N	876	577	219
6026	\N	875	599	219
6027	\N	876	578	219
6028	\N	875	552	219
6029	\N	876	592	219
6030	\N	876	593	219
6031	\N	876	594	219
6032	\N	876	579	219
6033	\N	874	600	219
6034	\N	875	595	219
6035	\N	875	601	219
6036	\N	875	553	219
6037	\N	876	596	219
6038	\N	876	580	219
6039	\N	876	581	219
6040	\N	877	554	220
6041	\N	880	582	220
6042	\N	878	555	220
6043	\N	880	556	220
6044	\N	880	557	220
6045	\N	877	545	220
6046	\N	878	558	220
6047	\N	877	559	220
6048	\N	877	546	220
6049	\N	877	583	220
6050	\N	878	560	220
6051	\N	880	597	220
6052	\N	877	561	220
6053	\N	877	562	220
6054	\N	878	584	220
6055	\N	877	563	220
6056	\N	879	585	220
6057	\N	877	564	220
6058	\N	878	547	220
6059	\N	878	586	220
6060	\N	880	565	220
6061	\N	879	587	220
6062	\N	880	566	220
6063	\N	878	548	220
6064	\N	877	549	220
6065	\N	880	567	220
6066	\N	880	568	220
6067	\N	877	569	220
6068	\N	877	570	220
6069	\N	877	550	220
6070	\N	877	588	220
6071	\N	878	132	220
6072	\N	877	589	220
6073	\N	877	571	220
6074	\N	\N	131	220
6075	\N	878	572	220
6076	\N	880	573	220
6077	\N	878	574	220
6078	\N	878	134	220
6079	\N	877	590	220
6080	\N	878	551	220
6081	\N	880	575	220
6082	\N	877	598	220
6083	\N	880	576	220
6084	\N	879	133	220
6085	\N	877	591	220
6086	\N	878	577	220
6087	\N	877	599	220
6088	\N	880	578	220
6089	\N	878	552	220
6090	\N	880	592	220
6091	\N	877	593	220
6092	\N	878	594	220
6093	\N	877	579	220
6094	\N	877	600	220
6095	\N	877	595	220
6096	\N	877	601	220
6097	\N	879	553	220
6098	\N	877	596	220
6099	\N	877	580	220
6100	\N	879	581	220
6132	\N	\N	132	221
6135	\N	\N	131	221
6162	\N	885	554	222
6163	\N	885	582	222
6164	\N	888	555	222
6165	\N	888	556	222
6166	\N	888	557	222
6167	\N	885	545	222
6168	\N	885	558	222
6169	\N	888	559	222
6170	\N	887	546	222
6171	\N	887	583	222
6172	\N	887	560	222
6173	\N	885	597	222
6174	\N	887	561	222
6175	\N	888	562	222
6176	\N	885	584	222
6177	\N	886	563	222
6178	\N	885	585	222
6179	\N	887	564	222
6180	\N	888	547	222
6181	\N	885	586	222
6182	\N	885	565	222
6183	\N	885	587	222
6184	\N	885	566	222
6185	\N	887	548	222
6186	\N	887	549	222
6187	\N	885	567	222
6188	\N	885	568	222
6189	\N	888	569	222
6190	\N	887	570	222
6191	\N	887	550	222
6192	\N	885	588	222
6193	\N	886	132	222
6194	\N	887	589	222
6195	\N	887	571	222
6196	\N	885	131	222
6197	\N	885	572	222
6198	\N	885	573	222
6199	\N	885	574	222
6200	\N	886	134	222
6201	\N	885	590	222
6202	\N	885	551	222
6203	\N	885	575	222
6204	\N	887	598	222
6205	\N	885	576	222
6206	\N	885	133	222
6207	\N	887	591	222
6208	\N	887	577	222
6209	\N	885	599	222
6210	\N	887	578	222
6211	\N	885	552	222
6212	\N	885	592	222
6213	\N	885	593	222
6214	\N	885	594	222
6215	\N	888	579	222
6216	\N	885	600	222
6217	\N	887	595	222
6218	\N	885	601	222
6219	\N	888	553	222
6220	\N	885	596	222
6221	\N	885	580	222
6222	\N	885	581	222
6223	\N	892	554	223
6224	\N	889	582	223
6225	\N	890	555	223
6226	\N	890	556	223
6227	\N	890	557	223
6228	\N	889	545	223
6229	\N	889	558	223
6230	\N	890	559	223
6231	\N	891	546	223
6232	\N	889	583	223
6233	\N	889	560	223
6234	\N	889	597	223
6235	\N	892	561	223
6236	\N	890	562	223
6237	\N	889	584	223
6238	\N	889	563	223
6239	\N	889	585	223
6240	\N	892	564	223
6241	\N	889	547	223
6242	\N	890	586	223
6243	\N	889	565	223
6244	\N	891	587	223
6245	\N	892	566	223
6246	\N	889	548	223
6247	\N	890	549	223
6248	\N	889	567	223
6249	\N	889	568	223
6250	\N	889	569	223
6251	\N	889	570	223
6252	\N	889	550	223
6253	\N	890	588	223
6254	\N	889	132	223
6255	\N	889	589	223
6256	\N	891	571	223
6257	\N	\N	131	223
6258	\N	892	572	223
6259	\N	889	573	223
6260	\N	890	574	223
6261	\N	892	134	223
6262	\N	889	590	223
6263	\N	891	551	223
6264	\N	889	575	223
6265	\N	889	598	223
6266	\N	892	576	223
6267	\N	890	133	223
6268	\N	889	591	223
6269	\N	889	577	223
6270	\N	890	599	223
6271	\N	889	578	223
6272	\N	889	552	223
6273	\N	890	592	223
6274	\N	890	593	223
6275	\N	890	594	223
6276	\N	889	579	223
6277	\N	889	600	223
6278	\N	889	595	223
6279	\N	889	601	223
6280	\N	889	553	223
6281	\N	889	596	223
6282	\N	889	580	223
6283	\N	889	581	223
6284	\N	893	554	224
6285	\N	894	582	224
6286	\N	896	555	224
6287	\N	893	556	224
6288	\N	893	557	224
6289	\N	896	545	224
6290	\N	896	558	224
6291	\N	896	559	224
6292	\N	893	546	224
6293	\N	894	583	224
6294	\N	896	560	224
6295	\N	893	597	224
6296	\N	896	561	224
6297	\N	896	562	224
6298	\N	896	584	224
6299	\N	896	563	224
6300	\N	896	585	224
6301	\N	896	564	224
6302	\N	896	547	224
6303	\N	896	586	224
6304	\N	896	565	224
6305	\N	896	587	224
6306	\N	896	566	224
6307	\N	896	548	224
6308	\N	896	549	224
6309	\N	896	567	224
6310	\N	896	568	224
6311	\N	896	569	224
6312	\N	896	570	224
6313	\N	896	550	224
6314	\N	896	588	224
6315	\N	893	132	224
6316	\N	896	589	224
6317	\N	896	571	224
6318	\N	894	131	224
6319	\N	896	572	224
6320	\N	893	573	224
6321	\N	895	574	224
6322	\N	893	134	224
6323	\N	896	590	224
6324	\N	896	551	224
6325	\N	894	575	224
6326	\N	896	598	224
6327	\N	896	576	224
6328	\N	893	133	224
6329	\N	893	591	224
6330	\N	896	577	224
6331	\N	896	599	224
6332	\N	896	578	224
6333	\N	896	552	224
6334	\N	895	592	224
6335	\N	896	593	224
6336	\N	896	594	224
6337	\N	896	579	224
6338	\N	896	600	224
6339	\N	896	595	224
6340	\N	896	601	224
6341	\N	896	553	224
6342	\N	896	596	224
6343	\N	896	580	224
6344	\N	896	581	224
6345	\N	897	554	225
6346	\N	898	582	225
6347	\N	898	555	225
6348	\N	897	556	225
6349	\N	897	557	225
6350	\N	897	545	225
6351	\N	900	558	225
6352	\N	900	559	225
6353	\N	897	546	225
6354	\N	897	583	225
6355	\N	900	560	225
6356	\N	898	597	225
6357	\N	899	561	225
6358	\N	897	562	225
6359	\N	897	584	225
6360	\N	897	563	225
6361	\N	898	585	225
6362	\N	897	564	225
6363	\N	899	547	225
6364	\N	899	586	225
6365	\N	900	565	225
6366	\N	898	587	225
6367	\N	900	566	225
6368	\N	900	548	225
6369	\N	900	549	225
6370	\N	900	567	225
6371	\N	899	568	225
6372	\N	900	569	225
6373	\N	900	570	225
6374	\N	898	550	225
6375	\N	897	588	225
6376	\N	898	132	225
6377	\N	898	589	225
6378	\N	897	571	225
6379	\N	897	131	225
6380	\N	900	572	225
6381	\N	900	573	225
6382	\N	900	574	225
6383	\N	897	134	225
6384	\N	900	590	225
6385	\N	898	551	225
6386	\N	900	575	225
6387	\N	897	598	225
6388	\N	899	576	225
6389	\N	\N	133	225
6390	\N	900	591	225
6391	\N	897	577	225
6392	\N	898	599	225
6393	\N	900	578	225
6394	\N	900	552	225
6395	\N	899	592	225
6396	\N	897	593	225
6397	\N	897	594	225
6398	\N	898	579	225
6399	\N	897	600	225
6400	\N	897	595	225
6401	\N	898	601	225
6402	\N	897	553	225
6403	\N	898	596	225
6404	\N	898	580	225
6405	\N	897	581	225
6467	\N	908	554	227
6468	\N	907	582	227
6469	\N	908	555	227
6470	\N	907	556	227
6471	\N	908	557	227
6472	\N	908	545	227
6473	\N	908	558	227
6474	\N	907	559	227
6475	\N	906	546	227
6476	\N	906	583	227
6477	\N	908	560	227
6478	\N	908	597	227
6479	\N	908	561	227
6480	\N	908	562	227
6481	\N	908	584	227
6482	\N	908	563	227
6483	\N	908	585	227
6484	\N	908	564	227
6485	\N	908	547	227
6486	\N	908	586	227
6487	\N	908	565	227
6488	\N	908	587	227
6489	\N	908	566	227
6490	\N	907	548	227
6491	\N	908	549	227
6492	\N	908	567	227
6493	\N	908	568	227
6494	\N	908	569	227
6495	\N	908	570	227
6496	\N	908	550	227
6497	\N	908	588	227
6498	\N	908	132	227
6499	\N	908	589	227
6500	\N	908	571	227
6501	\N	908	131	227
6502	\N	908	572	227
6503	\N	905	573	227
6504	\N	907	574	227
6505	\N	908	134	227
6506	\N	908	590	227
6507	\N	908	551	227
6508	\N	908	575	227
6509	\N	908	598	227
6510	\N	907	576	227
6511	\N	908	133	227
6512	\N	908	591	227
6513	\N	908	577	227
6514	\N	908	599	227
6515	\N	905	578	227
6516	\N	908	552	227
6517	\N	908	592	227
6518	\N	908	593	227
6519	\N	908	594	227
6520	\N	908	579	227
6521	\N	908	600	227
6522	\N	905	595	227
6523	\N	908	601	227
6524	\N	908	553	227
6525	\N	908	596	227
6526	\N	908	580	227
6527	\N	908	581	227
6589	\N	915	554	229
6590	\N	914	582	229
6591	\N	914	555	229
6592	\N	915	556	229
6593	\N	914	557	229
6594	\N	914	545	229
6595	\N	915	558	229
6596	\N	914	559	229
6597	\N	913	546	229
6598	\N	915	583	229
6599	\N	914	560	229
6600	\N	915	597	229
6601	\N	916	561	229
6602	\N	916	562	229
6603	\N	916	584	229
6604	\N	916	563	229
6605	\N	915	585	229
6606	\N	916	564	229
6607	\N	916	547	229
6608	\N	916	586	229
6609	\N	916	565	229
6610	\N	914	587	229
6611	\N	915	566	229
6612	\N	914	548	229
6613	\N	913	549	229
6614	\N	914	567	229
6615	\N	916	568	229
6616	\N	915	569	229
6617	\N	916	570	229
6618	\N	916	550	229
6619	\N	915	588	229
6620	\N	913	132	229
6621	\N	916	589	229
6622	\N	914	571	229
6623	\N	\N	131	229
6624	\N	915	572	229
6625	\N	914	573	229
6626	\N	915	574	229
6627	\N	915	134	229
6628	\N	916	590	229
6629	\N	913	551	229
6630	\N	914	575	229
6631	\N	913	598	229
6632	\N	913	576	229
6633	\N	916	133	229
6634	\N	914	591	229
6635	\N	916	577	229
6636	\N	914	599	229
6637	\N	916	578	229
6638	\N	916	552	229
6639	\N	914	592	229
6640	\N	914	593	229
6641	\N	914	594	229
6642	\N	914	579	229
6643	\N	915	600	229
6644	\N	916	595	229
6645	\N	915	601	229
6646	\N	915	553	229
6647	\N	916	596	229
6648	\N	914	580	229
6649	\N	915	581	229
6650	\N	917	554	230
6651	\N	919	582	230
6652	\N	919	555	230
6653	\N	919	556	230
6654	\N	919	557	230
6655	\N	919	545	230
6656	\N	917	558	230
6657	\N	919	559	230
6658	\N	919	546	230
6659	\N	919	583	230
6660	\N	920	560	230
6661	\N	919	597	230
6662	\N	920	561	230
6663	\N	919	562	230
6664	\N	920	584	230
6665	\N	920	563	230
6666	\N	918	585	230
6667	\N	920	564	230
6668	\N	919	547	230
6669	\N	919	586	230
6670	\N	920	565	230
6671	\N	919	587	230
6672	\N	919	566	230
6673	\N	919	548	230
6674	\N	919	549	230
6675	\N	917	567	230
6676	\N	917	568	230
6677	\N	919	569	230
6678	\N	919	570	230
6679	\N	917	550	230
6680	\N	920	588	230
6681	\N	920	132	230
6682	\N	919	589	230
6683	\N	919	571	230
6684	\N	919	131	230
6685	\N	917	572	230
6686	\N	920	573	230
6687	\N	917	574	230
6688	\N	920	134	230
6689	\N	919	590	230
6690	\N	917	551	230
6691	\N	920	575	230
6692	\N	919	598	230
6693	\N	918	576	230
6694	\N	920	133	230
6695	\N	920	591	230
6696	\N	919	577	230
6697	\N	920	599	230
6698	\N	919	578	230
6699	\N	919	552	230
6700	\N	919	592	230
6701	\N	917	593	230
6702	\N	919	594	230
6703	\N	919	579	230
6704	\N	919	600	230
6705	\N	919	595	230
6706	\N	919	601	230
6707	\N	919	553	230
6708	\N	920	596	230
6709	\N	919	580	230
6710	\N	919	581	230
6711	\N	922	554	231
6712	\N	922	582	231
6713	\N	923	555	231
6714	\N	923	556	231
6715	\N	922	557	231
6716	\N	924	545	231
6717	\N	922	558	231
6718	\N	922	559	231
6719	\N	922	546	231
6720	\N	922	583	231
6721	\N	923	560	231
6722	\N	922	597	231
6723	\N	922	561	231
6724	\N	922	562	231
6725	\N	922	584	231
6726	\N	922	563	231
6727	\N	922	585	231
6728	\N	922	564	231
6729	\N	922	547	231
6730	\N	922	586	231
6731	\N	922	565	231
6732	\N	924	587	231
6733	\N	922	566	231
6734	\N	923	548	231
6735	\N	922	549	231
6736	\N	922	567	231
6737	\N	922	568	231
6738	\N	922	569	231
6739	\N	924	570	231
6740	\N	922	550	231
6741	\N	921	588	231
6742	\N	922	132	231
6743	\N	922	589	231
6744	\N	922	571	231
6745	\N	\N	131	231
6746	\N	923	572	231
6747	\N	922	573	231
6748	\N	923	574	231
6749	\N	923	134	231
6750	\N	922	590	231
6751	\N	924	551	231
6752	\N	922	575	231
6753	\N	922	598	231
6754	\N	923	576	231
6755	\N	\N	133	231
6756	\N	922	591	231
6757	\N	922	577	231
6758	\N	922	599	231
6759	\N	922	578	231
6760	\N	923	552	231
6761	\N	922	592	231
6762	\N	922	593	231
6763	\N	923	594	231
6764	\N	922	579	231
6765	\N	924	600	231
6766	\N	924	595	231
6767	\N	922	601	231
6768	\N	922	553	231
6769	\N	922	596	231
6770	\N	922	580	231
6771	\N	922	581	231
6772	\N	927	554	232
6773	\N	926	582	232
6774	\N	925	555	232
6775	\N	925	556	232
6776	\N	925	557	232
6777	\N	926	545	232
6778	\N	925	558	232
6779	\N	928	559	232
6780	\N	925	546	232
6781	\N	925	583	232
6782	\N	925	560	232
6783	\N	926	597	232
6784	\N	925	561	232
6785	\N	927	562	232
6786	\N	925	584	232
6787	\N	925	563	232
6788	\N	925	585	232
6789	\N	927	564	232
6790	\N	925	547	232
6791	\N	925	586	232
6792	\N	925	565	232
6793	\N	926	587	232
6794	\N	925	566	232
6795	\N	927	548	232
6796	\N	925	549	232
6797	\N	927	567	232
6798	\N	926	568	232
6799	\N	925	569	232
6800	\N	925	570	232
6801	\N	926	550	232
6802	\N	926	588	232
6803	\N	\N	132	232
6804	\N	926	589	232
6805	\N	925	571	232
6806	\N	927	131	232
6807	\N	927	572	232
6808	\N	926	573	232
6809	\N	926	574	232
6810	\N	926	134	232
6811	\N	926	590	232
6812	\N	926	551	232
6813	\N	926	575	232
6814	\N	925	598	232
6815	\N	928	576	232
6816	\N	927	133	232
6817	\N	925	591	232
6818	\N	925	577	232
6819	\N	925	599	232
6820	\N	925	578	232
6821	\N	926	552	232
6822	\N	927	592	232
6823	\N	928	593	232
6824	\N	925	594	232
6825	\N	926	579	232
6826	\N	927	600	232
6827	\N	925	595	232
6828	\N	925	601	232
6829	\N	928	553	232
6830	\N	925	596	232
6831	\N	925	580	232
6832	\N	926	581	232
6833	\N	931	554	233
6834	\N	931	582	233
6835	\N	931	555	233
6836	\N	929	556	233
6837	\N	931	557	233
6838	\N	931	545	233
6839	\N	929	558	233
6840	\N	929	559	233
6841	\N	929	546	233
6842	\N	932	583	233
6843	\N	931	560	233
6844	\N	929	597	233
6845	\N	931	561	233
6846	\N	929	562	233
6847	\N	931	584	233
6848	\N	931	563	233
6849	\N	931	585	233
6850	\N	929	564	233
6851	\N	931	547	233
6852	\N	930	586	233
6853	\N	929	565	233
6854	\N	929	587	233
6855	\N	929	566	233
6856	\N	930	548	233
6857	\N	932	549	233
6858	\N	931	567	233
6859	\N	929	568	233
6860	\N	931	569	233
6861	\N	931	570	233
6862	\N	929	550	233
6863	\N	931	588	233
6864	\N	\N	132	233
6865	\N	932	589	233
6866	\N	931	571	233
6867	\N	\N	131	233
6868	\N	931	572	233
6869	\N	929	573	233
6870	\N	931	574	233
6871	\N	932	134	233
6872	\N	929	590	233
6873	\N	930	551	233
6874	\N	932	575	233
6875	\N	932	598	233
6876	\N	931	576	233
6877	\N	929	133	233
6878	\N	929	591	233
6879	\N	930	577	233
6880	\N	929	599	233
6881	\N	931	578	233
6882	\N	929	552	233
6883	\N	929	592	233
6884	\N	931	593	233
6885	\N	930	594	233
6886	\N	931	579	233
6887	\N	932	600	233
6888	\N	929	595	233
6889	\N	932	601	233
6890	\N	931	553	233
6891	\N	932	596	233
6892	\N	931	580	233
6893	\N	931	581	233
6894	\N	934	554	234
6895	\N	936	582	234
6896	\N	934	555	234
6897	\N	935	556	234
6898	\N	934	557	234
6899	\N	934	545	234
6900	\N	936	558	234
6901	\N	934	559	234
6902	\N	934	546	234
6903	\N	936	583	234
6904	\N	934	560	234
6905	\N	935	597	234
6906	\N	934	561	234
6907	\N	934	562	234
6908	\N	935	584	234
6909	\N	934	563	234
6910	\N	936	585	234
6911	\N	934	564	234
6912	\N	934	547	234
6913	\N	933	586	234
6914	\N	934	565	234
6915	\N	934	587	234
6916	\N	934	566	234
6917	\N	935	548	234
6918	\N	935	549	234
6919	\N	934	567	234
6920	\N	935	568	234
6921	\N	934	569	234
6922	\N	934	570	234
6923	\N	934	550	234
6924	\N	934	588	234
6925	\N	\N	132	234
6926	\N	935	589	234
6927	\N	934	571	234
6928	\N	\N	131	234
6929	\N	934	572	234
6930	\N	935	573	234
6931	\N	934	574	234
6932	\N	934	134	234
6933	\N	933	590	234
6934	\N	933	551	234
6935	\N	934	575	234
6936	\N	935	598	234
6937	\N	936	576	234
6938	\N	936	133	234
6939	\N	934	591	234
6940	\N	934	577	234
6941	\N	934	599	234
6942	\N	934	578	234
6943	\N	934	552	234
6944	\N	934	592	234
6945	\N	934	593	234
6946	\N	935	594	234
6947	\N	933	579	234
6948	\N	934	600	234
6949	\N	934	595	234
6950	\N	934	601	234
6951	\N	934	553	234
6952	\N	934	596	234
6953	\N	934	580	234
6954	\N	934	581	234
6955	\N	939	554	235
6956	\N	939	582	235
6957	\N	939	555	235
6958	\N	939	556	235
6959	\N	939	557	235
6960	\N	938	545	235
6961	\N	938	558	235
6962	\N	939	559	235
6963	\N	939	546	235
6964	\N	938	583	235
6965	\N	939	560	235
6966	\N	939	597	235
6967	\N	939	561	235
6968	\N	938	562	235
6969	\N	937	584	235
6970	\N	939	563	235
6971	\N	940	585	235
6972	\N	939	564	235
6973	\N	938	547	235
6974	\N	939	586	235
6975	\N	939	565	235
6976	\N	938	587	235
6977	\N	937	566	235
6978	\N	938	548	235
6979	\N	939	549	235
6980	\N	938	567	235
6981	\N	938	568	235
6982	\N	939	569	235
6983	\N	938	570	235
6984	\N	939	550	235
6985	\N	938	588	235
6986	\N	939	132	235
6987	\N	937	589	235
6988	\N	938	571	235
6989	\N	938	131	235
6990	\N	939	572	235
6991	\N	939	573	235
6992	\N	937	574	235
6993	\N	938	134	235
6994	\N	938	590	235
6995	\N	938	551	235
6996	\N	939	575	235
6997	\N	939	598	235
6998	\N	940	576	235
6999	\N	939	133	235
7000	\N	939	591	235
7001	\N	938	577	235
7002	\N	939	599	235
7003	\N	939	578	235
7004	\N	939	552	235
7005	\N	938	592	235
7006	\N	939	593	235
7007	\N	938	594	235
7008	\N	939	579	235
7009	\N	938	600	235
7010	\N	940	595	235
7011	\N	939	601	235
7012	\N	937	553	235
7013	\N	939	596	235
7014	\N	938	580	235
7015	\N	939	581	235
7050	\N	\N	131	236
7054	\N	\N	134	236
7060	\N	\N	133	236
7194	\N	2090	601	238
7138	\N	2089	554	238
7139	\N	2089	582	238
7140	\N	2089	555	238
7141	\N	2089	556	238
7142	\N	2089	557	238
7143	\N	2089	545	238
7144	\N	2089	558	238
7145	\N	2089	559	238
7146	\N	2089	546	238
7148	\N	2089	560	238
7149	\N	2089	597	238
7150	\N	2089	561	238
7151	\N	2089	562	238
7152	\N	2089	584	238
7153	\N	2089	563	238
7156	\N	2089	547	238
7157	\N	2089	586	238
7158	\N	2089	565	238
7159	\N	2089	587	238
7160	\N	2089	566	238
7162	\N	2089	549	238
7163	\N	2089	567	238
7165	\N	2089	569	238
7166	\N	2089	570	238
7167	\N	2089	550	238
7168	\N	2089	588	238
7169	\N	2089	132	238
7031	\N	4558	563	236
7032	\N	4558	585	236
7035	\N	4558	586	236
7039	\N	4558	548	236
7048	\N	4558	589	236
7051	\N	4558	572	236
7056	\N	4558	551	236
7057	\N	4558	575	236
7071	\N	4558	595	236
7052	\N	4557	573	236
7068	\N	4557	594	236
7038	\N	4560	566	236
7055	\N	4560	590	236
7058	\N	4560	598	236
7059	\N	4560	576	236
7066	\N	4560	592	236
7069	\N	4560	579	236
7072	\N	4560	601	236
7073	\N	4560	553	236
7033	\N	4559	564	236
7034	\N	4559	547	236
7036	\N	4559	565	236
7037	\N	4559	587	236
7040	\N	4559	549	236
7041	\N	4559	567	236
7042	\N	4559	568	236
7043	\N	4559	569	236
7044	\N	4559	570	236
7045	\N	4559	550	236
7046	\N	4559	588	236
7047	\N	4559	132	236
7049	\N	4559	571	236
7053	\N	4559	574	236
7061	\N	4559	591	236
7062	\N	4559	577	236
7063	\N	4559	599	236
7064	\N	4559	578	236
7065	\N	4559	552	236
7067	\N	4559	593	236
7070	\N	4559	600	236
7074	\N	4559	596	236
7075	\N	4559	580	236
7076	\N	4559	581	236
7233	\N	\N	131	239
7414	\N	1135	609	244
7419	\N	1135	607	244
7439	\N	1135	605	244
7441	\N	1135	626	244
7429	\N	1136	624	244
7431	\N	1136	628	244
7434	\N	1136	616	244
7437	\N	1136	623	244
7443	\N	1136	629	244
7428	\N	1134	606	244
7438	\N	1134	604	244
7442	\N	1134	608	244
7606	\N	\N	135	250
8282	\N	1088	645	272
8283	\N	1088	646	272
8284	\N	1087	639	272
8285	\N	1086	640	272
8286	\N	1085	648	272
8287	\N	1087	651	272
8288	\N	1086	654	272
8289	\N	1088	657	272
8290	\N	1086	641	272
8291	\N	1087	636	272
8292	\N	1087	647	272
8293	\N	1087	633	272
8294	\N	1087	653	272
8295	\N	1088	637	272
8296	\N	1088	642	272
8297	\N	1088	644	272
8298	\N	1088	659	272
8299	\N	1088	632	272
8300	\N	1085	649	272
8301	\N	1088	634	272
8302	\N	1088	650	272
8303	\N	1087	652	272
8304	\N	1088	655	272
8305	\N	1086	656	272
8306	\N	1087	658	272
8307	\N	1088	635	272
8308	\N	1085	136	272
8309	\N	1088	643	272
8310	\N	1088	638	272
8311	\N	1086	660	272
8312	\N	1088	661	272
8313	\N	1092	645	273
8314	\N	1090	646	273
8315	\N	1092	639	273
8316	\N	1089	640	273
8317	\N	1092	648	273
8318	\N	1092	651	273
8319	\N	1091	654	273
8320	\N	1089	657	273
8321	\N	1092	641	273
8322	\N	1092	636	273
8323	\N	1092	647	273
8324	\N	1092	633	273
8325	\N	1090	653	273
8221	\N	1089	610	270
8225	\N	1089	607	270
8229	\N	1091	613	270
8233	\N	1091	603	270
8242	\N	1091	622	270
8245	\N	1091	605	270
8250	\N	1091	630	270
8253	\N	3583	639	271
8254	\N	3583	640	271
8259	\N	3583	641	271
8265	\N	3583	642	271
8266	\N	3583	644	271
8278	\N	3583	643	271
8260	\N	3581	636	271
8262	\N	3581	633	271
8268	\N	3581	632	271
8270	\N	3581	634	271
8276	\N	3581	635	271
8264	\N	3582	637	271
8279	\N	3582	638	271
8251	\N	3584	645	271
8252	\N	3584	646	271
8255	\N	3584	648	271
8256	\N	3584	651	271
8257	\N	3584	654	271
8258	\N	3584	657	271
8261	\N	3584	647	271
8263	\N	3584	653	271
8267	\N	3584	659	271
8269	\N	3584	649	271
8271	\N	3584	650	271
8272	\N	3584	652	271
8273	\N	3584	655	271
8274	\N	3584	656	271
8275	\N	3584	658	271
8277	\N	3584	136	271
8280	\N	3584	660	271
8281	\N	3584	661	271
8326	\N	1092	637	273
8327	\N	1092	642	273
8328	\N	1089	644	273
8329	\N	1092	659	273
8330	\N	1092	632	273
8331	\N	1090	649	273
8332	\N	1092	634	273
8333	\N	1091	650	273
8334	\N	1092	652	273
8335	\N	1090	655	273
8336	\N	1090	656	273
8337	\N	1090	658	273
8338	\N	1090	635	273
8339	\N	1092	136	273
8340	\N	1091	643	273
8341	\N	1091	638	273
8342	\N	1089	660	273
8343	\N	1090	661	273
8375	\N	1097	645	275
8376	\N	1097	646	275
8377	\N	1097	639	275
8378	\N	1099	640	275
8379	\N	1098	648	275
8380	\N	1098	651	275
8381	\N	1098	654	275
8382	\N	1100	657	275
8383	\N	1097	641	275
8384	\N	1097	636	275
8385	\N	1097	647	275
8386	\N	1097	633	275
8387	\N	1098	653	275
8388	\N	1098	637	275
8389	\N	1098	642	275
8390	\N	1098	644	275
8391	\N	1098	659	275
8392	\N	1097	632	275
8393	\N	1098	649	275
8394	\N	1097	634	275
8395	\N	1097	650	275
8396	\N	1099	652	275
8397	\N	1097	655	275
8398	\N	1097	656	275
8399	\N	1100	658	275
8400	\N	1100	635	275
8401	\N	1097	136	275
8402	\N	1098	643	275
8403	\N	1100	638	275
8404	\N	1099	660	275
8405	\N	1097	661	275
8406	\N	1104	645	276
8407	\N	1102	646	276
8408	\N	1104	639	276
8409	\N	1102	640	276
8410	\N	1104	648	276
8411	\N	1103	651	276
8412	\N	1102	654	276
8413	\N	1104	657	276
8414	\N	1104	641	276
8415	\N	1104	636	276
8416	\N	1101	647	276
8417	\N	1104	633	276
8418	\N	1104	653	276
8419	\N	1104	637	276
8420	\N	1102	642	276
8421	\N	1104	644	276
8422	\N	1104	659	276
8423	\N	1104	632	276
8424	\N	1101	649	276
8425	\N	1104	634	276
8426	\N	1102	650	276
8427	\N	1101	652	276
8428	\N	1102	655	276
8429	\N	1102	656	276
8430	\N	1104	658	276
8431	\N	1104	635	276
8432	\N	1103	136	276
8433	\N	1102	643	276
8434	\N	1104	638	276
8435	\N	1104	660	276
8436	\N	1104	661	276
8468	\N	1111	645	278
8469	\N	1109	646	278
8470	\N	1109	639	278
8471	\N	1112	640	278
8472	\N	1111	648	278
8473	\N	1111	651	278
8474	\N	1112	654	278
8475	\N	1110	657	278
8476	\N	1112	641	278
8477	\N	1112	636	278
8478	\N	1112	647	278
8479	\N	1110	633	278
8480	\N	1110	653	278
8481	\N	1112	637	278
8482	\N	1110	642	278
8483	\N	1112	644	278
8484	\N	1112	659	278
8485	\N	1112	632	278
8486	\N	1112	649	278
8487	\N	1112	634	278
8488	\N	1112	650	278
8489	\N	1110	652	278
8490	\N	1112	655	278
8491	\N	1110	656	278
8492	\N	1109	658	278
8493	\N	1110	635	278
8494	\N	1110	136	278
8495	\N	1110	643	278
8496	\N	1111	638	278
8497	\N	1110	660	278
8498	\N	1112	661	278
8499	\N	1113	645	279
8500	\N	1113	646	279
8501	\N	1114	639	279
8502	\N	1114	640	279
8503	\N	1114	648	279
8504	\N	1113	651	279
8505	\N	1113	654	279
8506	\N	1116	657	279
8507	\N	1113	641	279
8508	\N	1116	636	279
8509	\N	1113	647	279
8510	\N	1116	633	279
8511	\N	1114	653	279
8512	\N	1116	637	279
8513	\N	1116	642	279
8514	\N	1116	644	279
8515	\N	1116	659	279
8516	\N	1113	632	279
8517	\N	1113	649	279
8518	\N	1113	634	279
8519	\N	1114	650	279
8520	\N	1113	652	279
8521	\N	1116	655	279
8522	\N	1115	656	279
8523	\N	1113	658	279
8524	\N	1116	635	279
8525	\N	1114	136	279
8526	\N	1113	643	279
8527	\N	1114	638	279
8528	\N	1113	660	279
8529	\N	1113	661	279
8530	\N	1117	645	280
8531	\N	1117	646	280
8532	\N	1117	639	280
8533	\N	1120	640	280
8534	\N	1117	648	280
8535	\N	1117	651	280
8536	\N	1117	654	280
8537	\N	1117	657	280
8538	\N	1118	641	280
8539	\N	1118	636	280
8540	\N	1120	647	280
8541	\N	1117	633	280
8542	\N	1117	653	280
8543	\N	1118	637	280
8544	\N	1117	642	280
8545	\N	1120	644	280
8546	\N	1120	659	280
8547	\N	1117	632	280
8548	\N	1117	649	280
8549	\N	1117	634	280
8550	\N	1117	650	280
8551	\N	1117	652	280
8552	\N	1117	655	280
8553	\N	1117	656	280
8554	\N	1117	658	280
8555	\N	1117	635	280
8556	\N	\N	136	280
8557	\N	1117	643	280
8558	\N	1120	638	280
8559	\N	1118	660	280
8560	\N	1117	661	280
8561	\N	1123	645	281
8562	\N	1124	646	281
8563	\N	1124	639	281
8564	\N	1124	640	281
8565	\N	1124	648	281
8566	\N	1122	651	281
8567	\N	1124	654	281
8568	\N	1124	657	281
8569	\N	1123	641	281
8570	\N	1124	636	281
8571	\N	1124	647	281
8572	\N	1124	633	281
8573	\N	1124	653	281
8574	\N	1124	637	281
8575	\N	1122	642	281
8576	\N	1124	644	281
8577	\N	1124	659	281
8578	\N	1124	632	281
8579	\N	1123	649	281
8580	\N	1124	634	281
8581	\N	1123	650	281
8582	\N	1124	652	281
8583	\N	1124	655	281
8584	\N	1124	656	281
8585	\N	1124	658	281
8586	\N	1122	635	281
8587	\N	1123	136	281
8588	\N	1124	643	281
8589	\N	1122	638	281
8590	\N	1122	660	281
8591	\N	1124	661	281
8592	\N	1127	645	282
8593	\N	1127	646	282
8594	\N	1127	639	282
8595	\N	1127	640	282
8596	\N	1127	648	282
8597	\N	1127	651	282
8598	\N	1128	654	282
8599	\N	1127	657	282
8600	\N	1127	641	282
8601	\N	1127	636	282
8602	\N	1127	647	282
8603	\N	1127	633	282
8604	\N	1127	653	282
8605	\N	1128	637	282
8606	\N	1127	642	282
8607	\N	1126	644	282
8608	\N	1127	659	282
8609	\N	1127	632	282
8610	\N	1128	649	282
8611	\N	1127	634	282
8612	\N	1128	650	282
8613	\N	1126	652	282
8614	\N	1127	655	282
8615	\N	1127	656	282
8616	\N	1127	658	282
8617	\N	1127	635	282
8618	\N	1127	136	282
8619	\N	1128	643	282
8620	\N	1125	638	282
8621	\N	1127	660	282
8622	\N	1127	661	282
8623	\N	1132	645	283
8624	\N	1132	646	283
8625	\N	1132	639	283
8626	\N	1131	640	283
8627	\N	1129	648	283
8628	\N	1132	651	283
8629	\N	1131	654	283
8630	\N	1132	657	283
8631	\N	1132	641	283
8632	\N	1132	636	283
8633	\N	1132	647	283
8634	\N	1132	633	283
8635	\N	1132	653	283
8636	\N	1132	637	283
8637	\N	1132	642	283
8638	\N	1132	644	283
8639	\N	1132	659	283
8640	\N	1132	632	283
8641	\N	1132	649	283
8642	\N	1132	634	283
8643	\N	1131	650	283
8644	\N	1132	652	283
8645	\N	1132	655	283
8646	\N	1131	656	283
8647	\N	1132	658	283
8648	\N	1132	635	283
8649	\N	1132	136	283
8650	\N	1132	643	283
8651	\N	1129	638	283
8652	\N	1132	660	283
8653	\N	1132	661	283
8654	\N	1133	645	284
8655	\N	1133	646	284
8656	\N	1133	639	284
8657	\N	1133	640	284
8658	\N	1136	648	284
8659	\N	1133	651	284
8660	\N	1133	654	284
8661	\N	1133	657	284
8662	\N	1133	641	284
8663	\N	1133	636	284
8664	\N	1133	647	284
8665	\N	1133	633	284
8666	\N	1133	653	284
8667	\N	1135	637	284
8668	\N	1133	642	284
8669	\N	1133	644	284
8670	\N	1133	659	284
8671	\N	1133	632	284
8672	\N	1133	649	284
8673	\N	1133	634	284
8674	\N	1136	650	284
8675	\N	1133	652	284
8676	\N	1133	655	284
8677	\N	1136	656	284
8678	\N	1133	658	284
8679	\N	1133	635	284
8680	\N	1136	136	284
8681	\N	1133	643	284
8682	\N	1136	638	284
8683	\N	1133	660	284
8684	\N	1133	661	284
8685	\N	1140	645	285
8686	\N	1140	646	285
8687	\N	1140	639	285
8688	\N	1140	640	285
8689	\N	1138	648	285
8690	\N	1140	651	285
8691	\N	1140	654	285
8692	\N	1140	657	285
8693	\N	1140	641	285
8694	\N	1140	636	285
8695	\N	1140	647	285
8696	\N	1140	633	285
8697	\N	1140	653	285
8698	\N	1140	637	285
8699	\N	1140	642	285
8700	\N	1140	644	285
8701	\N	1140	659	285
8702	\N	1140	632	285
8703	\N	1140	649	285
8704	\N	1140	634	285
8705	\N	1140	650	285
8706	\N	1140	652	285
8707	\N	1140	655	285
8708	\N	1140	656	285
8709	\N	1138	658	285
8710	\N	1140	635	285
8711	\N	1140	136	285
8712	\N	1140	643	285
8713	\N	1139	638	285
8714	\N	1140	660	285
8715	\N	1140	661	285
8716	\N	1142	645	286
8717	\N	1142	646	286
8718	\N	1142	639	286
8719	\N	1142	640	286
8720	\N	1142	648	286
8721	\N	1141	651	286
8722	\N	1143	654	286
8723	\N	1142	657	286
8724	\N	1144	641	286
8725	\N	1143	636	286
8726	\N	1142	647	286
8727	\N	1141	633	286
8728	\N	1141	653	286
8729	\N	1141	637	286
8730	\N	1144	642	286
8731	\N	1141	644	286
8732	\N	1142	659	286
8733	\N	1142	632	286
8734	\N	1142	649	286
8735	\N	1142	634	286
8736	\N	1144	650	286
8737	\N	1144	652	286
8738	\N	1143	655	286
8739	\N	1144	656	286
8740	\N	1142	658	286
8741	\N	1144	635	286
8742	\N	1142	136	286
8743	\N	1144	643	286
8744	\N	1142	638	286
8745	\N	1143	660	286
8746	\N	1143	661	286
8747	\N	1147	645	287
8748	\N	1147	646	287
8749	\N	1148	639	287
8750	\N	1147	640	287
8751	\N	1147	648	287
8752	\N	1147	651	287
8753	\N	1148	654	287
8754	\N	1147	657	287
8755	\N	1146	641	287
8756	\N	1147	636	287
8757	\N	1147	647	287
8758	\N	1147	633	287
8759	\N	1147	653	287
8760	\N	1147	637	287
8761	\N	1145	642	287
8762	\N	1147	644	287
8763	\N	1147	659	287
8764	\N	1148	632	287
8765	\N	1147	649	287
8766	\N	1148	634	287
8767	\N	1146	650	287
8768	\N	1146	652	287
8769	\N	1147	655	287
8770	\N	1147	656	287
8771	\N	1147	658	287
8772	\N	1147	635	287
8773	\N	1147	136	287
8774	\N	1148	643	287
8775	\N	1145	638	287
8776	\N	1147	660	287
8777	\N	1147	661	287
8778	\N	1151	645	288
8779	\N	1151	646	288
8780	\N	1152	639	288
8781	\N	1149	640	288
8782	\N	1152	648	288
8783	\N	1151	651	288
8784	\N	1152	654	288
8785	\N	1152	657	288
8786	\N	1152	641	288
8787	\N	1149	636	288
8788	\N	1151	647	288
8789	\N	1152	633	288
8790	\N	1152	653	288
8791	\N	1151	637	288
8792	\N	1151	642	288
8793	\N	1152	644	288
8794	\N	1149	659	288
8795	\N	1150	632	288
8796	\N	1152	649	288
8797	\N	1150	634	288
8798	\N	1149	650	288
8799	\N	1151	652	288
8800	\N	1151	655	288
8801	\N	1152	656	288
8802	\N	1151	658	288
8803	\N	1151	635	288
8804	\N	1152	136	288
8805	\N	1149	643	288
8806	\N	1152	638	288
8807	\N	1152	660	288
8808	\N	1151	661	288
8809	\N	1155	645	289
8810	\N	1153	646	289
8811	\N	1153	639	289
8812	\N	1153	640	289
8813	\N	1153	648	289
8814	\N	1155	651	289
8815	\N	1155	654	289
8816	\N	1153	657	289
8817	\N	1155	641	289
8818	\N	1153	636	289
8819	\N	1153	647	289
8820	\N	1155	633	289
8821	\N	1153	653	289
8822	\N	1156	637	289
8823	\N	1153	642	289
8824	\N	1155	644	289
8825	\N	1154	659	289
8826	\N	1153	632	289
8827	\N	1153	649	289
8828	\N	1153	634	289
8829	\N	1153	650	289
8830	\N	1156	652	289
8831	\N	1156	655	289
8832	\N	1153	656	289
8833	\N	1153	658	289
8834	\N	1155	635	289
8835	\N	1155	136	289
8836	\N	1153	643	289
8837	\N	1156	638	289
8838	\N	1153	660	289
8839	\N	1153	661	289
8840	\N	1158	645	290
8841	\N	1157	646	290
8842	\N	1158	639	290
8843	\N	1158	640	290
8844	\N	1158	648	290
8845	\N	1157	651	290
8846	\N	1158	654	290
8847	\N	1158	657	290
8848	\N	1158	641	290
8849	\N	1158	636	290
8850	\N	1158	647	290
8851	\N	1158	633	290
8852	\N	1158	653	290
8853	\N	1158	637	290
8854	\N	1158	642	290
8855	\N	1158	644	290
8856	\N	1158	659	290
8857	\N	1158	632	290
8858	\N	1158	649	290
8859	\N	1158	634	290
8860	\N	1158	650	290
8861	\N	1157	652	290
8862	\N	1157	655	290
8863	\N	1158	656	290
8864	\N	1157	658	290
8865	\N	1157	635	290
8866	\N	1158	136	290
8867	\N	1158	643	290
8868	\N	1157	638	290
8869	\N	1158	660	290
8870	\N	1158	661	290
8871	\N	1163	645	291
8872	\N	1163	646	291
8873	\N	1163	639	291
8874	\N	1163	640	291
8875	\N	1163	648	291
8876	\N	1163	651	291
8877	\N	1163	654	291
8878	\N	1163	657	291
8879	\N	1163	641	291
8880	\N	1163	636	291
8881	\N	1163	647	291
8882	\N	1163	633	291
8883	\N	1163	653	291
8884	\N	1163	637	291
8885	\N	1162	642	291
8886	\N	1163	644	291
8887	\N	1161	659	291
8888	\N	1163	632	291
8889	\N	1163	649	291
8890	\N	1163	634	291
8891	\N	1163	650	291
8892	\N	1163	652	291
8893	\N	1163	655	291
8894	\N	1163	656	291
8895	\N	1163	658	291
8896	\N	1163	635	291
8897	\N	1163	136	291
8898	\N	1164	643	291
8899	\N	1163	638	291
8900	\N	1163	660	291
8901	\N	1163	661	291
8933	\N	1169	645	293
8934	\N	1171	646	293
8935	\N	1169	639	293
8936	\N	1169	640	293
8937	\N	1171	648	293
8938	\N	1169	651	293
8939	\N	1172	654	293
8940	\N	1169	657	293
8941	\N	1172	641	293
8942	\N	1172	636	293
8943	\N	1169	647	293
8944	\N	1170	633	293
8945	\N	1171	653	293
8946	\N	1170	637	293
8947	\N	1172	642	293
8948	\N	1172	644	293
8949	\N	1172	659	293
8950	\N	1169	632	293
8951	\N	1172	649	293
8952	\N	1169	634	293
8953	\N	1169	650	293
8954	\N	1172	652	293
8955	\N	1169	655	293
8956	\N	1171	656	293
8957	\N	1171	658	293
8958	\N	1170	635	293
8959	\N	1172	136	293
8960	\N	1169	643	293
8961	\N	1170	638	293
8962	\N	1172	660	293
8963	\N	1172	661	293
8964	\N	1174	645	294
8965	\N	1174	646	294
8966	\N	1174	639	294
8967	\N	1175	640	294
8968	\N	1174	648	294
8969	\N	1174	651	294
8970	\N	1174	654	294
8971	\N	1174	657	294
8972	\N	1174	641	294
8973	\N	1176	636	294
8974	\N	1176	647	294
8975	\N	1175	633	294
8976	\N	1174	653	294
8977	\N	1174	637	294
8978	\N	1176	642	294
8979	\N	1175	644	294
8980	\N	1173	659	294
8981	\N	1174	632	294
8982	\N	1176	649	294
8983	\N	1174	634	294
8984	\N	1176	650	294
8985	\N	1174	652	294
8986	\N	1174	655	294
8987	\N	1176	656	294
8988	\N	1174	658	294
8989	\N	1174	635	294
8990	\N	1174	136	294
8991	\N	1173	643	294
8992	\N	1176	638	294
8993	\N	1174	660	294
8994	\N	1174	661	294
8995	\N	1178	645	295
8996	\N	1179	646	295
8997	\N	1179	639	295
8998	\N	1178	640	295
8999	\N	1179	648	295
9000	\N	1177	651	295
9001	\N	1177	654	295
9002	\N	1180	657	295
9003	\N	1179	641	295
9004	\N	1177	636	295
9005	\N	1180	647	295
9006	\N	1179	633	295
9007	\N	1179	653	295
9008	\N	1179	637	295
9009	\N	1179	642	295
9010	\N	1179	644	295
9011	\N	1177	659	295
9012	\N	1177	632	295
9013	\N	1179	649	295
9014	\N	1177	634	295
9015	\N	1179	650	295
9016	\N	1180	652	295
9017	\N	1178	655	295
9018	\N	1180	656	295
9019	\N	1177	658	295
9020	\N	1179	635	295
9021	\N	1179	136	295
9022	\N	1177	643	295
9023	\N	1177	638	295
9024	\N	1179	660	295
9025	\N	1178	661	295
9088	\N	1189	645	298
9089	\N	1189	646	298
9090	\N	1189	639	298
9091	\N	1191	640	298
9092	\N	1189	648	298
9093	\N	1189	651	298
9094	\N	1189	654	298
9095	\N	1189	657	298
9096	\N	1192	641	298
9097	\N	1189	636	298
9098	\N	1189	647	298
9099	\N	1192	633	298
9100	\N	1189	653	298
9101	\N	1192	637	298
9102	\N	1190	642	298
9103	\N	1192	644	298
9104	\N	1192	659	298
9105	\N	1189	632	298
9106	\N	1190	649	298
9107	\N	1189	634	298
9108	\N	1191	650	298
9109	\N	1189	652	298
9110	\N	1192	655	298
9111	\N	1189	656	298
9112	\N	1192	658	298
9113	\N	1190	635	298
9114	\N	1190	136	298
9115	\N	1192	643	298
9116	\N	1191	638	298
9117	\N	1191	660	298
9118	\N	1189	661	298
9119	\N	1193	645	299
9120	\N	1195	646	299
9121	\N	1193	639	299
9122	\N	1195	640	299
9123	\N	1193	648	299
9124	\N	1193	651	299
9125	\N	1195	654	299
9126	\N	1195	657	299
9127	\N	1194	641	299
9128	\N	1194	636	299
9129	\N	1193	647	299
9130	\N	1195	633	299
9131	\N	1196	653	299
9132	\N	1196	637	299
9133	\N	1193	642	299
9134	\N	1194	644	299
9135	\N	1196	659	299
9136	\N	1194	632	299
9137	\N	1193	649	299
9138	\N	1194	634	299
9139	\N	1193	650	299
9140	\N	1195	652	299
9141	\N	1195	655	299
9142	\N	1195	656	299
9143	\N	1195	658	299
9144	\N	1194	635	299
9145	\N	1193	136	299
9146	\N	1195	643	299
9147	\N	1194	638	299
9148	\N	1196	660	299
9149	\N	1195	661	299
9150	\N	1197	645	300
9151	\N	1200	646	300
9152	\N	1198	639	300
9153	\N	1200	640	300
9154	\N	1198	648	300
9155	\N	1198	651	300
9156	\N	1199	654	300
9157	\N	1200	657	300
9158	\N	1200	641	300
9159	\N	1200	636	300
9160	\N	1200	647	300
9161	\N	1200	633	300
9162	\N	1198	653	300
9163	\N	1197	637	300
9164	\N	1198	642	300
9165	\N	1200	644	300
9166	\N	1200	659	300
9167	\N	1200	632	300
9168	\N	1200	649	300
9169	\N	1200	634	300
9170	\N	1200	650	300
9171	\N	1198	652	300
9172	\N	1199	655	300
9173	\N	1198	656	300
9174	\N	1199	658	300
9175	\N	1200	635	300
9176	\N	1200	136	300
9177	\N	1197	643	300
9178	\N	1197	638	300
9179	\N	1200	660	300
9180	\N	1200	661	300
9196	\N	\N	137	421
9218	\N	\N	138	422
9226	\N	\N	139	422
9294	\N	\N	139	424
9298	\N	\N	137	424
9317	\N	\N	140	425
9342	\N	\N	141	425
9388	\N	\N	138	427
9405	\N	\N	142	427
9419	\N	\N	140	428
9432	\N	\N	143	428
9468	\N	\N	137	429
9480	\N	\N	144	429
9500	\N	\N	143	430
9613	\N	\N	145	433
9638	\N	\N	137	434
9650	\N	\N	144	434
9657	\N	\N	140	435
9713	\N	\N	146	436
9738	\N	\N	143	437
9740	\N	\N	137	437
9770	\N	\N	139	438
9861	\N	\N	140	441
9872	\N	\N	139	441
9874	\N	\N	143	441
9892	\N	\N	147	441
9902	\N	\N	148	442
9908	\N	\N	143	442
9917	\N	\N	146	442
9929	\N	\N	140	443
9931	\N	\N	149	443
9942	\N	\N	143	443
9944	\N	\N	137	443
9951	\N	\N	146	443
9963	\N	\N	140	444
9978	\N	\N	137	444
9895	\N	1873	140	442
9898	\N	1873	138	442
9900	\N	1873	663	442
9901	\N	1873	671	442
9903	\N	1873	680	442
9904	\N	1873	664	442
9909	\N	1873	673	442
9910	\N	1873	137	442
9914	\N	1873	666	442
9915	\N	1873	142	442
9916	\N	1873	676	442
9919	\N	1873	145	442
9920	\N	1873	141	442
9922	\N	1873	144	442
9923	\N	1873	677	442
9924	\N	1873	669	442
9927	\N	1873	681	442
9928	\N	1873	678	442
9896	\N	1876	662	442
9899	\N	1876	679	442
9905	\N	1876	672	442
9906	\N	1876	139	442
9907	\N	1876	150	442
9911	\N	1876	674	442
9913	\N	1876	675	442
9918	\N	1876	667	442
9897	\N	1874	149	442
9912	\N	1874	665	442
9921	\N	1874	668	442
9925	\N	1874	670	442
9926	\N	1874	147	442
9932	\N	1879	138	443
9937	\N	1879	680	443
9938	\N	1879	664	443
9939	\N	1879	672	443
9940	\N	1879	139	443
9946	\N	1879	665	443
9947	\N	1879	675	443
9948	\N	1879	666	443
9952	\N	1879	667	443
9954	\N	1879	141	443
9955	\N	1879	668	443
9956	\N	1879	144	443
9957	\N	1879	677	443
9958	\N	1879	669	443
9962	\N	1879	678	443
9933	\N	1877	679	443
9941	\N	1877	150	443
9959	\N	1877	670	443
9960	\N	1877	147	443
9930	\N	1878	662	443
9934	\N	1878	663	443
9935	\N	1878	671	443
9936	\N	1878	148	443
9943	\N	1878	673	443
9945	\N	1878	674	443
9949	\N	1878	142	443
9950	\N	1878	676	443
9953	\N	1878	145	443
9961	\N	1878	681	443
9965	\N	1882	149	444
9970	\N	1882	148	444
9974	\N	1882	139	444
9980	\N	1882	665	444
9989	\N	1882	668	444
9964	\N	1883	662	444
9966	\N	1883	138	444
9967	\N	1883	679	444
9968	\N	1883	663	444
9969	\N	1883	671	444
9971	\N	1883	680	444
9973	\N	1883	672	444
9975	\N	1883	150	444
9977	\N	1883	673	444
10012	\N	\N	137	445
10058	\N	\N	144	446
10078	\N	\N	143	447
10090	\N	\N	141	447
10106	\N	\N	148	448
10111	\N	\N	150	448
10119	\N	\N	142	448
10124	\N	\N	141	448
10126	\N	\N	144	448
10145	\N	\N	150	449
10148	\N	\N	137	449
10169	\N	\N	149	450
10178	\N	\N	139	450
10201	\N	1801	163	451
10202	\N	1801	682	451
10203	\N	1803	160	451
10204	\N	1801	683	451
10205	\N	1801	684	451
10206	\N	1801	156	451
10207	\N	1801	165	451
10208	\N	1801	152	451
10209	\N	1801	685	451
10210	\N	1801	158	451
10211	\N	1801	686	451
10212	\N	\N	151	451
10213	\N	1801	159	451
10214	\N	1802	690	451
10215	\N	1802	155	451
10216	\N	1801	167	451
10217	\N	1801	162	451
10218	\N	1804	691	451
10219	\N	1801	687	451
10220	\N	1801	166	451
10221	\N	1801	153	451
10222	\N	1801	164	451
10223	\N	1801	157	451
10224	\N	1801	154	451
10225	\N	1801	688	451
10226	\N	1801	689	451
10227	\N	1801	168	451
10228	\N	1801	161	451
10229	\N	1807	163	452
10230	\N	1807	682	452
10231	\N	1805	160	452
10232	\N	1807	683	452
10233	\N	1807	684	452
10234	\N	1807	156	452
10235	\N	1807	165	452
10236	\N	\N	152	452
10237	\N	1807	685	452
10238	\N	1807	158	452
10239	\N	1807	686	452
10240	\N	1807	151	452
10241	\N	1807	159	452
10242	\N	1807	690	452
10243	\N	1807	155	452
10244	\N	1807	167	452
10245	\N	1807	162	452
10246	\N	1807	691	452
10247	\N	1807	687	452
10248	\N	1806	166	452
10249	\N	1807	153	452
10250	\N	1807	164	452
10251	\N	1806	157	452
10252	\N	1806	154	452
10253	\N	1806	688	452
10254	\N	1807	689	452
10255	\N	1807	168	452
10256	\N	1807	161	452
10257	\N	1812	163	453
10258	\N	1812	682	453
10259	\N	1810	160	453
10260	\N	1812	683	453
10261	\N	1812	684	453
10262	\N	1812	156	453
10263	\N	1809	165	453
10264	\N	1812	152	453
10265	\N	1812	685	453
10266	\N	1809	158	453
10267	\N	1809	686	453
10268	\N	1812	151	453
10269	\N	1812	159	453
10270	\N	1812	690	453
10271	\N	1812	155	453
10272	\N	1812	167	453
10273	\N	1809	162	453
10274	\N	1809	691	453
10275	\N	1809	687	453
10276	\N	1809	166	453
10277	\N	\N	153	453
10278	\N	1812	164	453
10279	\N	1809	157	453
10280	\N	\N	154	453
10281	\N	1809	688	453
10282	\N	1809	689	453
10283	\N	1809	168	453
10284	\N	1810	161	453
10285	\N	1816	163	454
10286	\N	1816	682	454
10287	\N	1816	160	454
10288	\N	1816	683	454
10289	\N	1816	684	454
10290	\N	1816	156	454
10291	\N	1816	165	454
10292	\N	1813	152	454
10293	\N	1816	685	454
10294	\N	1816	158	454
10295	\N	1816	686	454
10296	\N	1816	151	454
10297	\N	1816	159	454
10298	\N	1816	690	454
10299	\N	1816	155	454
10300	\N	1816	167	454
10301	\N	1816	162	454
10302	\N	1816	691	454
10303	\N	1816	687	454
10304	\N	1816	166	454
10305	\N	\N	153	454
10306	\N	1816	164	454
10307	\N	1816	157	454
10308	\N	\N	154	454
10309	\N	1813	688	454
10310	\N	1816	689	454
10311	\N	1813	168	454
10312	\N	1815	161	454
10313	\N	1817	163	455
10314	\N	1817	682	455
10315	\N	1818	160	455
10316	\N	1819	683	455
10317	\N	1817	684	455
10318	\N	1820	156	455
10319	\N	1817	165	455
10320	\N	1817	152	455
10321	\N	1817	685	455
10322	\N	1817	158	455
10323	\N	1817	686	455
10324	\N	1817	151	455
10325	\N	1817	159	455
10326	\N	1819	690	455
10327	\N	1817	155	455
10328	\N	1817	167	455
10329	\N	1819	162	455
10330	\N	1817	691	455
10331	\N	1817	687	455
10332	\N	1817	166	455
10333	\N	1817	153	455
10334	\N	1817	164	455
10335	\N	1819	157	455
10336	\N	1817	154	455
10337	\N	1817	688	455
10338	\N	1817	689	455
10339	\N	1819	168	455
10340	\N	1820	161	455
10341	\N	1824	163	456
10342	\N	1824	682	456
10343	\N	1824	160	456
10344	\N	1824	683	456
10345	\N	1824	684	456
10346	\N	1824	156	456
10347	\N	1824	165	456
10348	\N	1824	152	456
10349	\N	1824	685	456
10350	\N	1824	158	456
10351	\N	1824	686	456
10352	\N	1824	151	456
10353	\N	1824	159	456
10354	\N	1824	690	456
10355	\N	1824	155	456
10356	\N	1824	167	456
10357	\N	1824	162	456
10358	\N	1824	691	456
10359	\N	1822	687	456
10360	\N	1824	166	456
10361	\N	1824	153	456
10362	\N	1824	164	456
10363	\N	1824	157	456
10364	\N	1824	154	456
10365	\N	1824	688	456
10366	\N	1824	689	456
10367	\N	1824	168	456
10368	\N	1824	161	456
10392	\N	\N	154	457
10411	\N	\N	155	458
10430	\N	\N	156	459
10447	\N	\N	157	459
10453	\N	1840	163	460
10454	\N	1837	682	460
10455	\N	1838	160	460
10456	\N	1837	683	460
10457	\N	1839	684	460
10458	\N	1837	156	460
10459	\N	1838	165	460
10460	\N	1838	152	460
10461	\N	1837	685	460
10462	\N	\N	158	460
10463	\N	1837	686	460
10464	\N	1838	151	460
10465	\N	1840	159	460
10466	\N	1837	690	460
10467	\N	1837	155	460
10468	\N	1837	167	460
10469	\N	1837	162	460
10470	\N	1837	691	460
10471	\N	1837	687	460
10472	\N	1840	166	460
10473	\N	1837	153	460
10474	\N	1837	164	460
10475	\N	1837	157	460
10476	\N	1837	154	460
10477	\N	1839	688	460
10478	\N	1837	689	460
10479	\N	1837	168	460
10480	\N	1838	161	460
10481	\N	1843	163	461
10482	\N	1842	682	461
10483	\N	1842	160	461
10484	\N	1843	683	461
10485	\N	1842	684	461
10486	\N	1842	156	461
10487	\N	1842	165	461
10488	\N	1842	152	461
10489	\N	1843	685	461
10490	\N	1842	158	461
10491	\N	1843	686	461
10492	\N	\N	151	461
10493	\N	1843	159	461
10494	\N	1843	690	461
10495	\N	1842	155	461
10496	\N	1843	167	461
10497	\N	1842	162	461
10498	\N	1842	691	461
10499	\N	1842	687	461
10500	\N	1843	166	461
10501	\N	1843	153	461
10502	\N	1843	164	461
10503	\N	1843	157	461
10504	\N	\N	154	461
10505	\N	1842	688	461
10506	\N	1842	689	461
10507	\N	1842	168	461
10508	\N	1843	161	461
10509	\N	1848	163	462
10510	\N	1845	682	462
10511	\N	1848	160	462
10512	\N	1845	683	462
10513	\N	1845	684	462
10514	\N	1848	156	462
10515	\N	1848	165	462
10516	\N	1848	152	462
10517	\N	1848	685	462
10518	\N	1847	158	462
10519	\N	1846	686	462
10520	\N	1845	151	462
10521	\N	\N	159	462
10522	\N	1847	690	462
10523	\N	\N	155	462
10524	\N	1848	167	462
10525	\N	1845	162	462
10526	\N	1848	691	462
10527	\N	1847	687	462
10528	\N	1845	166	462
10529	\N	\N	153	462
10530	\N	1848	164	462
10531	\N	1847	157	462
10532	\N	1848	154	462
10533	\N	1848	688	462
10534	\N	1847	689	462
10535	\N	1845	168	462
10536	\N	1847	161	462
10537	\N	1852	163	463
10538	\N	1850	682	463
10539	\N	\N	160	463
10540	\N	1852	683	463
10541	\N	1850	684	463
10542	\N	1850	156	463
10543	\N	1850	165	463
10544	\N	1852	152	463
10545	\N	1852	685	463
10546	\N	1852	158	463
10547	\N	1852	686	463
10548	\N	1851	151	463
10549	\N	1850	159	463
10550	\N	1852	690	463
10551	\N	\N	155	463
10552	\N	1852	167	463
10553	\N	1851	162	463
10554	\N	1852	691	463
10555	\N	1852	687	463
10556	\N	1851	166	463
10557	\N	1850	153	463
10558	\N	1850	164	463
10559	\N	1852	157	463
10560	\N	1850	154	463
10561	\N	1850	688	463
10562	\N	1849	689	463
10563	\N	1850	168	463
10564	\N	\N	161	463
10565	\N	1855	163	464
10566	\N	1855	682	464
10567	\N	1855	160	464
10568	\N	1855	683	464
10569	\N	1855	684	464
10570	\N	1855	156	464
10571	\N	1855	165	464
10572	\N	1855	152	464
10573	\N	1855	685	464
10574	\N	1855	158	464
10575	\N	1855	686	464
10576	\N	1855	151	464
10577	\N	1855	159	464
10578	\N	1855	690	464
10579	\N	\N	155	464
10580	\N	1853	167	464
10581	\N	1855	162	464
10582	\N	1855	691	464
10583	\N	1855	687	464
10584	\N	1855	166	464
10585	\N	\N	153	464
10586	\N	1855	164	464
10587	\N	1854	157	464
10588	\N	1855	154	464
10589	\N	1854	688	464
10590	\N	1854	689	464
10591	\N	1855	168	464
10592	\N	1855	161	464
10593	\N	1859	163	465
10594	\N	1860	682	465
10595	\N	1858	160	465
10596	\N	1859	683	465
10597	\N	1858	684	465
10598	\N	\N	156	465
10599	\N	1860	165	465
10600	\N	1860	152	465
10601	\N	1860	685	465
10602	\N	1860	158	465
10603	\N	1859	686	465
10604	\N	1860	151	465
10605	\N	1858	159	465
10606	\N	1859	690	465
10607	\N	1859	155	465
10608	\N	1859	167	465
10609	\N	1858	162	465
10610	\N	1859	691	465
10611	\N	1860	687	465
10612	\N	1859	166	465
10613	\N	1860	153	465
10614	\N	1860	164	465
10615	\N	1859	157	465
10616	\N	1859	154	465
10617	\N	1859	688	465
10618	\N	1860	689	465
10619	\N	1860	168	465
10620	\N	1859	161	465
10635	\N	\N	155	466
10649	\N	1866	163	467
10650	\N	1866	682	467
10651	\N	1866	160	467
10652	\N	1866	683	467
10653	\N	1868	684	467
10654	\N	1866	156	467
10655	\N	1866	165	467
10656	\N	1866	152	467
10657	\N	1866	685	467
10658	\N	1866	158	467
10659	\N	1866	686	467
10660	\N	1866	151	467
10661	\N	1866	159	467
10662	\N	1866	690	467
10663	\N	1866	155	467
10664	\N	1866	167	467
10665	\N	1866	162	467
10666	\N	1866	691	467
10667	\N	1866	687	467
10668	\N	1866	166	467
10669	\N	1866	153	467
10670	\N	1866	164	467
10671	\N	1866	157	467
10672	\N	\N	154	467
10673	\N	1866	688	467
10674	\N	1866	689	467
10675	\N	1866	168	467
10676	\N	1866	161	467
10682	\N	\N	156	468
10691	\N	\N	155	468
10693	\N	\N	162	468
10705	\N	\N	163	469
10706	\N	1873	682	469
10707	\N	1876	160	469
10708	\N	1873	683	469
10709	\N	1875	684	469
10710	\N	1873	156	469
10711	\N	1873	165	469
10712	\N	1876	152	469
10713	\N	1873	685	469
10714	\N	\N	158	469
10715	\N	1876	686	469
10716	\N	1873	151	469
10717	\N	1873	159	469
10718	\N	1873	690	469
10719	\N	1876	155	469
10720	\N	1873	167	469
10721	\N	1873	162	469
10722	\N	1873	691	469
10723	\N	1873	687	469
10724	\N	1876	166	469
10725	\N	1873	153	469
10726	\N	1873	164	469
10727	\N	1873	157	469
10728	\N	1876	154	469
10729	\N	1873	688	469
10730	\N	1876	689	469
10731	\N	1873	168	469
10732	\N	1873	161	469
10733	\N	1878	163	470
10734	\N	1878	682	470
10735	\N	\N	160	470
10736	\N	1878	683	470
10737	\N	1877	684	470
10738	\N	\N	156	470
10739	\N	1878	165	470
10740	\N	1878	152	470
10741	\N	1879	685	470
10742	\N	1878	158	470
10743	\N	1879	686	470
10744	\N	1877	151	470
10745	\N	\N	159	470
10746	\N	1879	690	470
10747	\N	1879	155	470
10748	\N	1879	167	470
10749	\N	1880	162	470
10750	\N	1879	691	470
10751	\N	1878	687	470
10752	\N	1878	166	470
10753	\N	\N	153	470
10754	\N	\N	164	470
10755	\N	1878	157	470
10756	\N	1877	154	470
10757	\N	1877	688	470
10758	\N	1879	689	470
10759	\N	1879	168	470
10760	\N	1880	161	470
10761	\N	1883	163	471
10762	\N	1883	682	471
10763	\N	\N	160	471
10764	\N	1883	683	471
10765	\N	1883	684	471
10766	\N	1883	156	471
10767	\N	\N	165	471
10768	\N	1883	152	471
10769	\N	1883	685	471
10770	\N	1881	158	471
10771	\N	1883	686	471
10772	\N	1883	151	471
10773	\N	1883	159	471
10774	\N	1883	690	471
10775	\N	1881	155	471
10776	\N	1883	167	471
10777	\N	1883	162	471
10778	\N	1883	691	471
10779	\N	1883	687	471
10780	\N	\N	166	471
10781	\N	1883	153	471
10782	\N	1881	164	471
10783	\N	1883	157	471
10784	\N	\N	154	471
10785	\N	1884	688	471
10786	\N	1883	689	471
10787	\N	1881	168	471
10788	\N	1881	161	471
10789	\N	1888	163	472
10790	\N	1888	682	472
10791	\N	1885	160	472
10792	\N	1885	683	472
10793	\N	1885	684	472
10794	\N	1888	156	472
10795	\N	1886	165	472
10796	\N	1887	152	472
10797	\N	1885	685	472
10798	\N	1888	158	472
10799	\N	1887	686	472
10800	\N	1888	151	472
10801	\N	\N	159	472
10802	\N	1888	690	472
10803	\N	1886	155	472
10804	\N	\N	167	472
10805	\N	1886	162	472
10806	\N	1887	691	472
10807	\N	1885	687	472
10808	\N	1888	166	472
10809	\N	1888	153	472
10810	\N	1885	164	472
10811	\N	\N	157	472
10812	\N	1886	154	472
10813	\N	1888	688	472
10814	\N	1888	689	472
10815	\N	1888	168	472
10816	\N	1888	161	472
10817	\N	1891	163	473
10818	\N	1891	682	473
10819	\N	1890	160	473
10820	\N	1891	683	473
10821	\N	1891	684	473
10822	\N	1890	156	473
10823	\N	1891	165	473
10824	\N	1890	152	473
10825	\N	1891	685	473
10826	\N	1891	158	473
10827	\N	1891	686	473
10828	\N	1889	151	473
10829	\N	1890	159	473
10830	\N	1891	690	473
10831	\N	\N	155	473
10832	\N	1891	167	473
10833	\N	1889	162	473
10834	\N	1891	691	473
10835	\N	1891	687	473
10836	\N	1891	166	473
10837	\N	\N	153	473
10838	\N	1891	164	473
10839	\N	1889	157	473
10840	\N	1889	154	473
10841	\N	1889	688	473
10842	\N	1890	689	473
10843	\N	1891	168	473
10844	\N	1889	161	473
10859	\N	\N	155	474
10865	\N	\N	153	474
10873	\N	1898	163	475
10874	\N	1897	682	475
10875	\N	1899	160	475
10876	\N	1899	683	475
10877	\N	1897	684	475
10878	\N	1897	156	475
10879	\N	1897	165	475
10880	\N	1898	152	475
10881	\N	1898	685	475
10882	\N	1897	158	475
10883	\N	1898	686	475
10884	\N	\N	151	475
10885	\N	1898	159	475
10886	\N	1897	690	475
10887	\N	1898	155	475
10888	\N	1897	167	475
10889	\N	\N	162	475
10890	\N	1897	691	475
10891	\N	1897	687	475
10892	\N	1898	166	475
10893	\N	1898	153	475
10894	\N	1897	164	475
10895	\N	\N	157	475
10896	\N	1897	154	475
10897	\N	1898	688	475
10898	\N	1897	689	475
10899	\N	1897	168	475
10900	\N	1898	161	475
10901	\N	1904	163	476
10902	\N	1903	682	476
10903	\N	1903	160	476
10904	\N	1903	683	476
10905	\N	1904	684	476
10906	\N	1903	156	476
10907	\N	1903	165	476
10908	\N	1903	152	476
10909	\N	1903	685	476
10910	\N	1903	158	476
10911	\N	1904	686	476
10912	\N	1904	151	476
10913	\N	1904	159	476
10914	\N	1903	690	476
10915	\N	1904	155	476
10916	\N	1903	167	476
10917	\N	1903	162	476
10918	\N	1903	691	476
10919	\N	1903	687	476
10920	\N	1904	166	476
10921	\N	1904	153	476
10922	\N	1904	164	476
10923	\N	1903	157	476
10924	\N	1903	154	476
10925	\N	1903	688	476
10926	\N	1904	689	476
10927	\N	1903	168	476
10928	\N	1904	161	476
10929	\N	1907	163	477
10930	\N	1907	682	477
10931	\N	1907	160	477
10932	\N	1907	683	477
10933	\N	1907	684	477
10934	\N	1907	156	477
10935	\N	1907	165	477
10936	\N	1907	152	477
10937	\N	1907	685	477
10938	\N	1907	158	477
10939	\N	1907	686	477
10940	\N	1908	151	477
10941	\N	1907	159	477
10942	\N	1907	690	477
10943	\N	1906	155	477
10944	\N	1907	167	477
10945	\N	1908	162	477
10946	\N	1907	691	477
10947	\N	1907	687	477
10948	\N	1908	166	477
10949	\N	1907	153	477
10950	\N	1907	164	477
10951	\N	1907	157	477
10952	\N	1907	154	477
10953	\N	1905	688	477
10954	\N	1907	689	477
10955	\N	1907	168	477
10956	\N	1907	161	477
10968	\N	\N	151	478
10971	\N	\N	155	478
10983	\N	\N	168	478
11013	\N	\N	163	480
11014	\N	1917	682	480
11015	\N	1918	160	480
11016	\N	1918	683	480
11017	\N	1918	684	480
11018	\N	1919	156	480
11019	\N	1918	165	480
11020	\N	1917	152	480
11021	\N	1918	685	480
11022	\N	1918	158	480
11023	\N	1918	686	480
11024	\N	1918	151	480
11025	\N	1919	159	480
11026	\N	1918	690	480
11027	\N	\N	155	480
11028	\N	1918	167	480
11029	\N	1918	162	480
11030	\N	1918	691	480
11031	\N	1918	687	480
11032	\N	1919	166	480
11033	\N	1919	153	480
11034	\N	1918	164	480
11035	\N	1919	157	480
11036	\N	1917	154	480
11037	\N	1918	688	480
11038	\N	1920	689	480
11039	\N	1918	168	480
11040	\N	1917	161	480
11056	\N	\N	169	481
11058	\N	\N	170	481
11107	\N	\N	171	484
11119	\N	\N	172	485
11131	\N	\N	173	486
11134	\N	\N	174	486
11146	\N	\N	169	486
11163	\N	\N	175	487
11197	\N	\N	171	489
11214	\N	\N	176	490
11217	\N	\N	175	490
11239	\N	2156	173	492
11240	\N	2156	694	492
11241	\N	2156	692	492
11243	\N	2156	695	492
11244	\N	2156	693	492
11245	\N	2156	172	492
11246	\N	2156	180	492
11247	\N	2156	177	492
11248	\N	2156	178	492
11249	\N	2156	179	492
11250	\N	2156	176	492
11251	\N	2156	171	492
11252	\N	2156	696	492
11253	\N	2156	175	492
11254	\N	2156	169	492
11256	\N	2156	170	492
11242	\N	2155	174	492
11255	\N	2155	697	492
11257	\N	2158	173	493
11261	\N	2158	695	493
11262	\N	2158	693	493
11265	\N	2158	177	493
11266	\N	2158	178	493
11268	\N	2158	176	493
11269	\N	2158	171	493
11272	\N	2158	169	493
11259	\N	2157	692	493
11263	\N	2157	172	493
11264	\N	2157	180	493
11271	\N	2157	175	493
11273	\N	2157	697	493
11258	\N	2159	694	493
11260	\N	2159	174	493
11267	\N	2159	179	493
11270	\N	2159	696	493
11274	\N	2159	170	493
11280	\N	2142	693	494
11281	\N	2142	172	494
11282	\N	2142	180	494
11275	\N	2144	173	494
11276	\N	2144	694	494
11284	\N	2144	178	494
11277	\N	2143	692	494
11278	\N	2143	174	494
11279	\N	2143	695	494
11283	\N	2143	177	494
11285	\N	2143	179	494
11337	\N	\N	177	497
11343	\N	\N	175	497
11359	\N	\N	171	498
11383	\N	\N	173	500
11401	\N	\N	173	501
11422	\N	\N	174	502
11426	\N	\N	180	502
11427	\N	\N	177	502
11428	\N	\N	178	502
11429	\N	\N	179	502
11430	\N	\N	176	502
11431	\N	\N	171	502
11433	\N	\N	175	502
11436	\N	\N	170	502
11443	\N	\N	172	503
11463	\N	\N	177	504
11467	\N	\N	171	504
11470	\N	\N	169	504
11481	\N	\N	177	505
11488	\N	\N	169	505
11490	\N	\N	170	505
11505	\N	\N	175	506
11524	\N	\N	169	507
11536	\N	\N	178	508
11542	\N	\N	169	508
11562	\N	\N	170	509
11605	\N	2049	183	513
11606	\N	2049	698	513
11607	\N	\N	181	513
11608	\N	2051	184	513
11609	\N	2050	699	513
11610	\N	2049	700	513
11611	\N	2050	182	513
11612	\N	2050	701	513
11613	\N	2049	702	513
11614	\N	2050	185	513
11615	\N	2049	186	513
11616	\N	2049	703	513
11617	\N	2053	183	514
11618	\N	2055	698	514
11619	\N	2053	181	514
11620	\N	2054	184	514
11621	\N	2055	699	514
11622	\N	2053	700	514
11623	\N	\N	182	514
11624	\N	2055	701	514
11625	\N	2054	702	514
11626	\N	2053	185	514
11627	\N	2053	186	514
11628	\N	2053	703	514
11629	\N	\N	183	515
11630	\N	2058	698	515
11631	\N	2058	181	515
11632	\N	\N	184	515
11633	\N	2058	699	515
11634	\N	2057	700	515
11635	\N	\N	182	515
11636	\N	2057	701	515
11637	\N	2059	702	515
11638	\N	2059	185	515
11639	\N	2060	186	515
11640	\N	2057	703	515
11653	\N	2065	183	517
11654	\N	2065	698	517
11655	\N	2067	181	517
11656	\N	2065	184	517
11657	\N	2068	699	517
11658	\N	2068	700	517
11659	\N	2065	182	517
11660	\N	2067	701	517
11661	\N	2065	702	517
11662	\N	2065	185	517
11663	\N	2067	186	517
11664	\N	2065	703	517
11665	\N	2072	183	518
11666	\N	2070	698	518
11667	\N	2070	181	518
11668	\N	2070	184	518
11669	\N	2071	699	518
11670	\N	2071	700	518
11671	\N	2069	182	518
11672	\N	2069	701	518
11673	\N	2071	702	518
11674	\N	\N	185	518
11675	\N	2070	186	518
11676	\N	2070	703	518
11677	\N	2073	183	519
11678	\N	2074	698	519
11679	\N	2074	181	519
11680	\N	2074	184	519
11681	\N	2074	699	519
11682	\N	2073	700	519
11683	\N	2074	182	519
11684	\N	2076	701	519
11685	\N	2074	702	519
11686	\N	2074	185	519
11687	\N	2074	186	519
11688	\N	2074	703	519
11713	\N	2088	183	522
11714	\N	2088	698	522
11715	\N	2088	181	522
11716	\N	2088	184	522
11717	\N	2087	699	522
11718	\N	2088	700	522
11719	\N	2087	182	522
11720	\N	2087	701	522
11721	\N	2088	702	522
11722	\N	2087	185	522
11723	\N	\N	186	522
11724	\N	2088	703	522
11725	\N	2089	183	523
11726	\N	2089	698	523
11727	\N	2089	181	523
11728	\N	\N	184	523
11729	\N	2089	699	523
11730	\N	2089	700	523
11731	\N	2089	182	523
11732	\N	2092	701	523
11733	\N	2089	702	523
11734	\N	2089	185	523
11735	\N	2089	186	523
11736	\N	2089	703	523
11737	\N	\N	183	524
11740	\N	\N	184	524
11749	\N	2099	183	525
11750	\N	2099	698	525
11751	\N	\N	181	525
11752	\N	2099	184	525
11753	\N	2099	699	525
11754	\N	2099	700	525
11755	\N	2099	182	525
11756	\N	2100	701	525
11757	\N	2099	702	525
11758	\N	2099	185	525
11759	\N	2099	186	525
11760	\N	2099	703	525
11761	\N	2102	183	526
11762	\N	2102	698	526
11763	\N	2101	181	526
11764	\N	2103	184	526
11765	\N	2102	699	526
11766	\N	2102	700	526
11767	\N	2102	182	526
11768	\N	2102	701	526
11769	\N	2102	702	526
11770	\N	2102	185	526
11771	\N	2103	186	526
11772	\N	2102	703	526
11773	\N	\N	183	527
11774	\N	2108	698	527
11775	\N	2106	181	527
11776	\N	\N	184	527
11777	\N	2106	699	527
11778	\N	2108	700	527
11779	\N	2107	182	527
11780	\N	2108	701	527
11781	\N	2108	702	527
11782	\N	2107	185	527
11783	\N	2107	186	527
11784	\N	2106	703	527
11785	\N	2111	183	528
11786	\N	2112	698	528
11787	\N	2112	181	528
11788	\N	2111	184	528
11789	\N	2111	699	528
11790	\N	2111	700	528
11791	\N	2111	182	528
11792	\N	2109	701	528
11793	\N	2111	702	528
11794	\N	2112	185	528
11795	\N	2111	186	528
11796	\N	2111	703	528
11821	\N	2124	183	531
11822	\N	2121	698	531
11823	\N	\N	181	531
11824	\N	2124	184	531
11825	\N	2124	699	531
11826	\N	2123	700	531
11827	\N	2121	182	531
11828	\N	2121	701	531
11829	\N	2122	702	531
11830	\N	\N	185	531
11831	\N	2122	186	531
11832	\N	2121	703	531
11833	\N	2126	183	532
11834	\N	2125	698	532
11835	\N	2126	181	532
11836	\N	2125	184	532
11837	\N	2126	699	532
11838	\N	2127	700	532
11839	\N	2128	182	532
11840	\N	2126	701	532
11841	\N	2126	702	532
11842	\N	2126	185	532
11843	\N	2128	186	532
11844	\N	2128	703	532
11845	\N	2131	183	533
11846	\N	2129	698	533
11847	\N	2129	181	533
11848	\N	2129	184	533
11849	\N	2131	699	533
11850	\N	2131	700	533
11851	\N	2131	182	533
11852	\N	2131	701	533
11853	\N	2130	702	533
11854	\N	2129	185	533
11855	\N	2131	186	533
11856	\N	2131	703	533
11869	\N	2139	183	535
11870	\N	2139	698	535
11871	\N	2138	181	535
11872	\N	2139	184	535
11873	\N	2139	699	535
11874	\N	2139	700	535
11875	\N	\N	182	535
11876	\N	2139	701	535
11877	\N	2138	702	535
11878	\N	2139	185	535
11879	\N	2138	186	535
11880	\N	2139	703	535
11881	\N	\N	183	536
11882	\N	2144	698	536
11883	\N	2143	181	536
11884	\N	2143	184	536
11885	\N	2142	699	536
11886	\N	2142	700	536
11887	\N	2143	182	536
11888	\N	2141	701	536
11889	\N	2141	702	536
11890	\N	2143	185	536
11891	\N	\N	186	536
11892	\N	2143	703	536
11893	\N	2146	183	537
11894	\N	2145	698	537
11895	\N	2145	181	537
11896	\N	2146	184	537
11897	\N	2148	699	537
11898	\N	2145	700	537
11899	\N	2148	182	537
11900	\N	2147	701	537
11901	\N	2147	702	537
11902	\N	2145	185	537
11903	\N	2145	186	537
11904	\N	2145	703	537
11905	\N	2152	183	538
11906	\N	2150	698	538
11907	\N	2150	181	538
11908	\N	2150	184	538
11909	\N	2150	699	538
11910	\N	2150	700	538
11911	\N	2150	182	538
11912	\N	2149	701	538
11913	\N	2150	702	538
11914	\N	2150	185	538
11915	\N	2150	186	538
11916	\N	2150	703	538
11917	\N	2156	183	539
11918	\N	2156	698	539
11919	\N	2155	181	539
11920	\N	2156	184	539
11921	\N	2155	699	539
11922	\N	2156	700	539
11923	\N	2156	182	539
11924	\N	2153	701	539
11925	\N	2156	702	539
11926	\N	2156	185	539
11927	\N	2155	186	539
11928	\N	2153	703	539
11929	\N	2159	183	540
11930	\N	2160	698	540
11931	\N	2159	181	540
11932	\N	2159	184	540
11933	\N	2158	699	540
11934	\N	2159	700	540
11935	\N	2159	182	540
11936	\N	2159	701	540
11937	\N	2157	702	540
11938	\N	2158	185	540
11939	\N	2159	186	540
11940	\N	2159	703	540
11949	\N	\N	187	541
11967	\N	\N	187	543
11986	\N	2184	719	546
11987	\N	2184	718	546
11988	\N	2181	712	546
11989	\N	2183	717	546
11990	\N	\N	188	546
11991	\N	2182	715	546
11992	\N	2182	189	546
11993	\N	2181	713	546
11994	\N	2181	714	546
11995	\N	2184	720	546
11996	\N	2182	716	546
11997	\N	2186	719	547
11998	\N	2186	718	547
11999	\N	2186	712	547
12000	\N	2186	717	547
12001	\N	2187	188	547
12002	\N	2186	715	547
12003	\N	\N	189	547
12004	\N	2186	713	547
12005	\N	2186	714	547
12006	\N	2186	720	547
12007	\N	2185	716	547
12008	\N	2192	719	548
12009	\N	2192	718	548
12010	\N	2192	712	548
12011	\N	2192	717	548
12012	\N	\N	188	548
12013	\N	2192	715	548
12014	\N	2192	189	548
12015	\N	2192	713	548
12016	\N	2192	714	548
12017	\N	2192	720	548
12018	\N	2192	716	548
12019	\N	2194	719	549
12020	\N	2194	718	549
12021	\N	2194	712	549
12022	\N	2194	717	549
12023	\N	2196	188	549
12024	\N	2194	715	549
12025	\N	2194	189	549
11972	\N	2182	706	544
11974	\N	2182	708	544
11968	\N	2184	709	544
11975	\N	2184	704	544
11969	\N	2181	710	544
11970	\N	2181	711	544
11971	\N	2181	705	544
11976	\N	2181	187	544
11985	\N	2187	187	545
11977	\N	2186	709	545
11978	\N	2186	710	545
11979	\N	2186	711	545
11980	\N	2186	705	545
11981	\N	2186	706	545
11982	\N	2186	707	545
11983	\N	2186	708	545
11984	\N	2186	704	545
12026	\N	2194	713	549
12027	\N	2194	714	549
12028	\N	2194	720	549
12029	\N	2194	716	549
12030	\N	2200	719	550
12031	\N	2197	718	550
12032	\N	2199	712	550
12033	\N	2197	717	550
12034	\N	2200	188	550
12035	\N	2197	715	550
12036	\N	2200	189	550
12037	\N	2200	713	550
12038	\N	2199	714	550
12039	\N	2199	720	550
12040	\N	2200	716	550
12049	\N	\N	190	551
12057	\N	\N	191	552
12087	\N	\N	191	555
12098	\N	\N	192	556
12100	\N	2225	730	557
12101	\N	2225	729	557
12102	\N	2227	731	557
12103	\N	2227	732	557
12104	\N	2225	733	557
12105	\N	2225	194	557
12106	\N	2227	193	557
12107	\N	2225	192	557
12108	\N	2225	734	557
12109	\N	2232	730	558
12110	\N	2229	729	558
12111	\N	2230	731	558
12112	\N	2232	732	558
12113	\N	2232	733	558
12114	\N	2230	194	558
12115	\N	2232	193	558
12116	\N	2230	192	558
12117	\N	2232	734	558
12118	\N	2236	730	559
12119	\N	2233	729	559
12120	\N	2234	731	559
12121	\N	2234	732	559
12122	\N	2234	733	559
12123	\N	2234	194	559
12124	\N	\N	193	559
12125	\N	2233	192	559
12126	\N	2233	734	559
12127	\N	2240	730	560
12128	\N	2237	729	560
12129	\N	2237	731	560
12130	\N	2240	732	560
12131	\N	2237	733	560
12132	\N	\N	194	560
12133	\N	2237	193	560
12134	\N	2237	192	560
12135	\N	2239	734	560
12171	\N	\N	195	563
12186	\N	\N	196	564
12189	\N	\N	197	565
12190	\N	\N	198	565
12201	\N	2263	752	566
12202	\N	2262	744	566
12203	\N	2262	745	566
12204	\N	2264	756	566
12205	\N	2262	746	566
12206	\N	2262	747	566
12207	\N	2262	749	566
12208	\N	2264	754	566
12209	\N	2263	753	566
12210	\N	2264	755	566
12211	\N	2262	748	566
12212	\N	2264	757	566
12213	\N	2262	750	566
12214	\N	2262	751	566
12229	\N	2269	752	568
12230	\N	2269	744	568
12231	\N	2269	745	568
12232	\N	2271	756	568
12233	\N	2271	746	568
12234	\N	2270	747	568
12235	\N	2269	749	568
12236	\N	2271	754	568
12237	\N	2269	753	568
12238	\N	2271	755	568
12239	\N	2269	748	568
12240	\N	2270	757	568
12241	\N	2271	750	568
12242	\N	2271	751	568
12243	\N	2274	752	569
12244	\N	2274	744	569
12245	\N	2274	745	569
12246	\N	2274	756	569
12247	\N	2274	746	569
12248	\N	2274	747	569
12249	\N	2274	749	569
12250	\N	2274	754	569
12251	\N	2274	753	569
12252	\N	2274	755	569
12253	\N	2274	748	569
12254	\N	2274	757	569
12255	\N	2274	750	569
12256	\N	2274	751	569
12257	\N	2277	752	570
12258	\N	2277	744	570
12259	\N	2277	745	570
12260	\N	2277	756	570
12261	\N	2277	746	570
12262	\N	2277	747	570
12263	\N	2277	749	570
12264	\N	2280	754	570
12265	\N	2277	753	570
12266	\N	2280	755	570
12267	\N	2277	748	570
12268	\N	2277	757	570
12269	\N	2277	750	570
12270	\N	2278	751	570
12280	\N	\N	199	571
12300	\N	\N	199	573
12321	\N	2304	200	576
12322	\N	2304	771	576
12323	\N	2303	201	576
12324	\N	2302	768	576
12325	\N	2303	769	576
12326	\N	2302	767	576
12327	\N	2304	770	576
12328	\N	2301	202	576
12329	\N	2305	200	577
12330	\N	2305	771	577
12331	\N	2305	201	577
12332	\N	2305	768	577
12333	\N	2305	769	577
12334	\N	2305	767	577
12335	\N	2305	770	577
12336	\N	2305	202	577
12337	\N	\N	200	578
12339	\N	\N	201	578
12344	\N	\N	202	578
12345	\N	2316	200	579
12346	\N	2316	771	579
12347	\N	2315	201	579
12348	\N	2316	768	579
12349	\N	2316	769	579
12350	\N	2316	767	579
12351	\N	2316	770	579
12352	\N	2315	202	579
12353	\N	\N	200	580
12354	\N	2318	771	580
12355	\N	2318	201	580
12356	\N	2319	768	580
12357	\N	2318	769	580
12358	\N	2318	767	580
12359	\N	2320	770	580
12360	\N	2317	202	580
12380	\N	\N	203	583
12386	\N	\N	204	583
12405	\N	\N	205	585
12406	\N	2343	779	586
12407	\N	2343	207	586
12408	\N	2343	206	586
12409	\N	2344	784	586
12410	\N	2343	778	586
12411	\N	2343	780	586
12412	\N	2343	781	586
12413	\N	2341	208	586
12414	\N	2343	782	586
12415	\N	2343	783	586
12416	\N	2346	779	587
12417	\N	2348	207	587
12418	\N	\N	206	587
12419	\N	2346	784	587
12420	\N	2345	778	587
12421	\N	2346	780	587
12422	\N	2345	781	587
12423	\N	2345	208	587
12424	\N	2345	782	587
12425	\N	2348	783	587
12426	\N	2351	779	588
12427	\N	2351	207	588
12428	\N	2351	206	588
12429	\N	2351	784	588
12430	\N	2351	778	588
12431	\N	2349	780	588
12432	\N	2351	781	588
12433	\N	2351	208	588
12434	\N	2351	782	588
12435	\N	2349	783	588
12436	\N	2356	779	589
12437	\N	\N	207	589
12438	\N	2356	206	589
12439	\N	2356	784	589
12440	\N	2356	778	589
12441	\N	2356	780	589
12442	\N	2356	781	589
12443	\N	2356	208	589
12444	\N	2356	782	589
12445	\N	2356	783	589
12446	\N	2357	779	590
12447	\N	2357	207	590
12448	\N	2360	206	590
12449	\N	2360	784	590
12450	\N	2360	778	590
12451	\N	2358	780	590
12452	\N	2357	781	590
12453	\N	\N	208	590
12454	\N	2359	782	590
12455	\N	2359	783	590
12474	\N	\N	209	592
12484	\N	\N	210	593
12492	\N	\N	211	593
12526	\N	2383	800	596
12527	\N	2382	213	596
12528	\N	2382	797	596
12529	\N	2382	798	596
12530	\N	\N	212	596
12531	\N	2384	802	596
12532	\N	2383	801	596
12533	\N	2384	805	596
12534	\N	2381	796	596
12535	\N	2384	806	596
12536	\N	2382	799	596
12537	\N	2384	803	596
12538	\N	2384	804	596
12539	\N	2383	214	596
12540	\N	2384	807	596
12541	\N	2388	800	597
12542	\N	2388	213	597
12543	\N	2385	797	597
12544	\N	2388	798	597
12545	\N	2385	212	597
12546	\N	2385	802	597
12547	\N	2385	801	597
12548	\N	2385	805	597
12549	\N	2388	796	597
12550	\N	2386	806	597
12551	\N	2385	799	597
12552	\N	2385	803	597
12553	\N	2385	804	597
12554	\N	2385	214	597
12555	\N	2385	807	597
12556	\N	2389	800	598
12557	\N	\N	213	598
12558	\N	2390	797	598
12559	\N	2390	798	598
12560	\N	2390	212	598
12561	\N	2392	802	598
12562	\N	2391	801	598
12563	\N	2389	805	598
12564	\N	2389	796	598
12565	\N	2390	806	598
12566	\N	2390	799	598
12567	\N	2390	803	598
12568	\N	2390	804	598
12569	\N	2391	214	598
12570	\N	2390	807	598
12571	\N	2395	800	599
12572	\N	2395	213	599
12573	\N	2395	797	599
12574	\N	2393	798	599
12575	\N	2394	212	599
12576	\N	2395	802	599
12577	\N	2393	801	599
12578	\N	2394	805	599
12579	\N	2394	796	599
12580	\N	2394	806	599
12581	\N	2394	799	599
12582	\N	2395	803	599
12583	\N	2393	804	599
12584	\N	\N	214	599
12585	\N	2393	807	599
12586	\N	2399	800	600
12587	\N	2399	213	600
12588	\N	2399	797	600
12589	\N	2399	798	600
12590	\N	2399	212	600
12591	\N	2399	802	600
12592	\N	2400	801	600
12593	\N	2399	805	600
12594	\N	2399	796	600
12595	\N	2399	806	600
12596	\N	2399	799	600
12597	\N	2399	803	600
12598	\N	2399	804	600
12599	\N	2399	214	600
12600	\N	2399	807	600
12612	\N	\N	215	602
12631	\N	2421	813	606
12632	\N	2423	817	606
12633	\N	2421	814	606
12634	\N	2422	815	606
12635	\N	2421	216	606
12636	\N	2423	816	606
12637	\N	2423	217	606
12638	\N	2426	813	607
12639	\N	2428	817	607
12640	\N	2428	814	607
12641	\N	2428	815	607
12642	\N	2427	216	607
12643	\N	2428	816	607
12644	\N	2428	217	607
12645	\N	2431	813	608
12646	\N	2432	817	608
12647	\N	2431	814	608
12648	\N	2431	815	608
12649	\N	2431	216	608
12650	\N	2432	816	608
12651	\N	2432	217	608
12652	\N	2434	813	609
12653	\N	2436	817	609
12654	\N	2434	814	609
12655	\N	2434	815	609
12656	\N	\N	216	609
12657	\N	2434	816	609
12658	\N	\N	217	609
12659	\N	2439	813	610
12660	\N	2439	817	610
12661	\N	2439	814	610
12662	\N	2439	815	610
12663	\N	2438	216	610
12664	\N	2439	816	610
12665	\N	2439	217	610
12668	\N	\N	218	611
12749	\N	2474	827	619
12750	\N	2473	829	619
12751	\N	2473	836	619
12752	\N	2473	830	619
12753	\N	2475	837	619
12754	\N	2476	831	619
12755	\N	2473	835	619
12756	\N	2473	833	619
12757	\N	2475	834	619
12758	\N	2473	828	619
12759	\N	2474	832	619
12760	\N	2480	827	620
12761	\N	2480	829	620
12762	\N	2480	836	620
12763	\N	2480	830	620
12764	\N	2479	837	620
12765	\N	2478	831	620
12679	\N	2474	819	612
12680	\N	2474	825	612
12676	\N	2473	824	612
12677	\N	2473	818	612
12678	\N	2473	218	612
12681	\N	2473	820	612
12682	\N	2473	821	612
12683	\N	2473	822	612
12684	\N	2473	826	612
12685	\N	2473	823	612
12686	\N	2480	824	613
12687	\N	2480	818	613
12688	\N	2480	218	613
12689	\N	2480	819	613
12690	\N	2480	825	613
12691	\N	2480	820	613
12692	\N	2480	821	613
12693	\N	2480	822	613
12694	\N	2480	826	613
12695	\N	2480	823	613
12766	\N	2479	835	620
12767	\N	2480	833	620
12768	\N	2480	834	620
12769	\N	2480	828	620
12770	\N	2480	832	620
12813	\N	\N	219	624
12826	\N	\N	220	624
12828	\N	\N	221	625
12867	\N	\N	222	633
12886	\N	2544	861	636
12887	\N	2541	857	636
12888	\N	2544	862	636
12889	\N	2544	863	636
12890	\N	2544	859	636
12891	\N	2543	858	636
12892	\N	2544	860	636
12893	\N	2543	223	636
12902	\N	2549	861	638
12903	\N	2549	857	638
12904	\N	2550	862	638
12905	\N	2549	863	638
12906	\N	2549	859	638
12907	\N	2549	858	638
12908	\N	2550	860	638
12909	\N	\N	223	638
12910	\N	2555	861	639
12911	\N	2554	857	639
12912	\N	2555	862	639
12913	\N	2555	863	639
12914	\N	2555	859	639
12915	\N	2555	858	639
12916	\N	2555	860	639
12917	\N	2555	223	639
12971	\N	\N	224	646
12972	\N	2583	875	646
12973	\N	2581	873	646
12974	\N	2583	874	646
12975	\N	2583	876	646
12976	\N	2583	877	646
12977	\N	2583	878	646
12978	\N	2582	226	646
12979	\N	\N	225	646
12980	\N	2583	879	646
12991	\N	2589	224	648
12992	\N	2592	875	648
12993	\N	2590	873	648
12994	\N	2592	874	648
12995	\N	2592	876	648
12996	\N	2589	877	648
12997	\N	2592	878	648
12998	\N	\N	226	648
12999	\N	2590	225	648
13000	\N	2589	879	648
13001	\N	2593	224	649
13002	\N	2593	875	649
13003	\N	2593	873	649
13004	\N	2596	874	649
13005	\N	2593	876	649
13006	\N	2593	877	649
13007	\N	2593	878	649
13008	\N	2593	226	649
13009	\N	2593	225	649
13010	\N	2593	879	649
13011	\N	2599	224	650
13012	\N	2599	875	650
13013	\N	2599	873	650
13014	\N	2599	874	650
13015	\N	2599	876	650
13016	\N	2599	877	650
13017	\N	2599	878	650
13018	\N	2599	226	650
13019	\N	2599	225	650
13020	\N	2599	879	650
13046	\N	\N	227	653
13065	\N	\N	228	654
13081	\N	2622	892	656
13082	\N	2624	900	656
13083	\N	2622	894	656
13084	\N	2622	895	656
13085	\N	2622	896	656
13086	\N	2622	898	656
13087	\N	2621	891	656
13088	\N	2622	893	656
13089	\N	2621	890	656
13090	\N	2622	897	656
13091	\N	2622	899	656
13092	\N	2626	892	657
13093	\N	2628	900	657
13094	\N	2628	894	657
13095	\N	2626	895	657
13096	\N	2628	896	657
13097	\N	2628	898	657
13098	\N	2625	891	657
13099	\N	2628	893	657
13100	\N	2626	890	657
13101	\N	2628	897	657
13102	\N	2628	899	657
13125	\N	2639	892	660
13126	\N	2638	900	660
13127	\N	2638	894	660
13128	\N	2638	895	660
13129	\N	2638	896	660
13130	\N	2639	898	660
13131	\N	2638	891	660
13132	\N	2637	893	660
13133	\N	2638	890	660
13134	\N	2638	897	660
13135	\N	2638	899	660
13158	\N	\N	229	662
13165	\N	\N	230	662
13201	\N	\N	231	664
13205	\N	\N	230	664
13276	\N	\N	232	668
13278	\N	\N	229	668
13297	\N	\N	233	669
13307	\N	\N	234	669
13316	\N	\N	232	670
13317	\N	\N	233	670
13157	\N	2791	233	662
13160	\N	2791	239	662
13162	\N	2791	241	662
13172	\N	2791	903	662
13173	\N	2791	242	662
13174	\N	2791	236	662
13176	\N	2794	232	663
13178	\N	2794	229	663
13181	\N	2794	231	663
13183	\N	2794	237	663
13184	\N	2794	240	663
13185	\N	2794	230	663
13187	\N	2794	234	663
13179	\N	2796	901	663
13189	\N	2796	243	663
13177	\N	2795	233	663
13186	\N	2795	235	663
13188	\N	2795	905	663
13191	\N	2795	902	663
13194	\N	2795	236	663
13180	\N	2793	239	663
13182	\N	2793	241	663
13190	\N	2793	238	663
13192	\N	2793	903	663
13193	\N	2793	242	663
13195	\N	2793	904	663
13196	\N	3948	232	664
13197	\N	3948	233	664
13198	\N	3948	229	664
13321	\N	\N	231	670
13326	\N	\N	235	670
13397	\N	\N	233	674
13405	\N	\N	230	674
13436	\N	\N	232	676
13477	\N	\N	233	678
13494	\N	\N	236	678
13497	\N	\N	233	679
13523	\N	\N	237	680
13530	\N	\N	238	680
13547	\N	\N	234	681
13556	\N	\N	232	682
13557	\N	\N	233	682
13558	\N	\N	229	682
13560	\N	\N	239	682
13561	\N	\N	231	682
13564	\N	\N	240	682
13566	\N	\N	235	682
13584	\N	\N	240	683
13596	\N	\N	232	684
13614	\N	\N	236	684
13618	\N	\N	229	685
13634	\N	\N	236	685
13654	\N	\N	236	686
13660	\N	\N	239	687
13681	\N	\N	231	688
13686	\N	\N	235	688
13696	\N	\N	232	689
13697	\N	\N	233	689
13698	\N	\N	229	689
13700	\N	\N	239	689
13702	\N	\N	241	689
13707	\N	\N	234	689
13710	\N	\N	238	689
13713	\N	\N	242	689
13716	\N	\N	232	690
13717	\N	\N	233	690
13722	\N	\N	241	690
13725	\N	\N	230	690
13726	\N	\N	235	690
13727	\N	\N	234	690
13729	\N	\N	243	690
13756	\N	\N	244	691
13761	\N	\N	245	692
13767	\N	\N	246	692
13770	\N	\N	247	692
13772	\N	\N	248	692
13794	\N	\N	249	693
13812	\N	\N	250	694
13836	\N	2777	245	695
13837	\N	\N	250	695
13838	\N	2779	253	695
13839	\N	2780	908	695
13840	\N	2777	254	695
13841	\N	2780	255	695
13842	\N	2780	246	695
13843	\N	2780	252	695
13844	\N	2780	249	695
13845	\N	2780	247	695
13846	\N	2780	251	695
13847	\N	2777	248	695
13848	\N	2779	909	695
13849	\N	2780	910	695
13850	\N	2778	907	695
13851	\N	2780	257	695
13852	\N	2778	256	695
13853	\N	2780	258	695
13854	\N	2780	260	695
13855	\N	2780	911	695
13856	\N	2780	244	695
13857	\N	2780	912	695
13858	\N	2780	259	695
13859	\N	2780	261	695
13860	\N	2780	906	695
13861	\N	2784	245	696
13862	\N	2784	250	696
13863	\N	2781	253	696
13864	\N	2784	908	696
13865	\N	2781	254	696
13866	\N	2781	255	696
13867	\N	2784	246	696
13868	\N	2781	252	696
13869	\N	2784	249	696
13870	\N	2781	247	696
13871	\N	\N	251	696
13872	\N	2781	248	696
13873	\N	2781	909	696
13874	\N	2781	910	696
13875	\N	2781	907	696
13762	\N	4498	250	692
13764	\N	4498	908	692
13765	\N	4498	254	692
13766	\N	4498	255	692
13768	\N	4498	252	692
13771	\N	4498	251	692
13773	\N	4498	909	692
13776	\N	4498	257	692
13778	\N	4498	258	692
13779	\N	4498	260	692
13780	\N	4498	911	692
13782	\N	4498	912	692
13783	\N	4498	259	692
13784	\N	4498	261	692
13769	\N	4500	249	692
13774	\N	4500	910	692
13785	\N	4500	906	692
13775	\N	4497	907	692
13777	\N	4497	256	692
13781	\N	4497	244	692
13763	\N	4499	253	692
13798	\N	4036	909	693
13786	\N	4035	245	693
13797	\N	4035	248	693
13788	\N	4033	253	693
13802	\N	4033	256	693
13808	\N	4033	259	693
13787	\N	4034	250	693
13789	\N	4034	908	693
13790	\N	4034	254	693
13791	\N	4034	255	693
13792	\N	4034	246	693
13793	\N	4034	252	693
13795	\N	4034	247	693
13796	\N	4034	251	693
13799	\N	4034	910	693
13800	\N	4034	907	693
13801	\N	4034	257	693
13803	\N	4034	258	693
13804	\N	4034	260	693
13876	\N	2781	257	696
13877	\N	2781	256	696
13878	\N	2781	258	696
13879	\N	2781	260	696
13880	\N	2784	911	696
13881	\N	2781	244	696
13882	\N	2781	912	696
13883	\N	2781	259	696
13884	\N	2781	261	696
13885	\N	2781	906	696
13886	\N	2785	245	697
13887	\N	2785	250	697
13888	\N	2785	253	697
13889	\N	2788	908	697
13890	\N	2785	254	697
13891	\N	2785	255	697
13892	\N	2785	246	697
13893	\N	2785	252	697
13894	\N	2785	249	697
13895	\N	2785	247	697
13896	\N	2785	251	697
13897	\N	2785	248	697
13898	\N	2785	909	697
13899	\N	2785	910	697
13900	\N	2785	907	697
13901	\N	2785	257	697
13902	\N	2785	256	697
13903	\N	2785	258	697
13904	\N	2785	260	697
13905	\N	2785	911	697
13906	\N	2785	244	697
13907	\N	2785	912	697
13908	\N	2785	259	697
13909	\N	2785	261	697
13910	\N	2788	906	697
13911	\N	2792	245	698
13912	\N	2791	250	698
13913	\N	2791	253	698
13914	\N	2792	908	698
13915	\N	2792	254	698
13916	\N	2792	255	698
13917	\N	\N	246	698
13918	\N	2790	252	698
13919	\N	\N	249	698
13920	\N	2792	247	698
13921	\N	2792	251	698
13922	\N	2792	248	698
13923	\N	2791	909	698
13924	\N	2792	910	698
13925	\N	2791	907	698
13926	\N	2792	257	698
13927	\N	2792	256	698
13928	\N	2791	258	698
13929	\N	2790	260	698
13930	\N	2790	911	698
13931	\N	2792	244	698
13932	\N	2791	912	698
13933	\N	2792	259	698
13934	\N	2791	261	698
13935	\N	2791	906	698
13936	\N	2793	245	699
13937	\N	2795	250	699
13938	\N	2794	253	699
13939	\N	2794	908	699
13940	\N	2795	254	699
13941	\N	2796	255	699
13942	\N	2794	246	699
13943	\N	2795	252	699
13944	\N	2795	249	699
13945	\N	2795	247	699
13946	\N	2793	251	699
13947	\N	2794	248	699
13948	\N	2793	909	699
13949	\N	2795	910	699
13950	\N	2793	907	699
13951	\N	2794	257	699
13952	\N	2794	256	699
13953	\N	2795	258	699
13954	\N	2795	260	699
13955	\N	2794	911	699
13956	\N	2794	244	699
13957	\N	2793	912	699
13958	\N	2794	259	699
13959	\N	2794	261	699
13960	\N	2794	906	699
13986	\N	2801	245	701
13987	\N	2801	250	701
13988	\N	2801	253	701
13989	\N	2801	908	701
13990	\N	2801	254	701
13991	\N	2801	255	701
13992	\N	2801	246	701
13993	\N	2801	252	701
13994	\N	2801	249	701
13995	\N	2801	247	701
13996	\N	2801	251	701
13997	\N	2801	248	701
13998	\N	2801	909	701
13999	\N	2801	910	701
14000	\N	2801	907	701
14001	\N	2801	257	701
14002	\N	2801	256	701
14003	\N	2801	258	701
14004	\N	2801	260	701
14005	\N	2801	911	701
14006	\N	2801	244	701
14007	\N	2801	912	701
14008	\N	2801	259	701
14009	\N	2801	261	701
14010	\N	2801	906	701
14011	\N	2808	245	702
14012	\N	2805	250	702
14013	\N	2807	253	702
14014	\N	2805	908	702
14015	\N	2806	254	702
14016	\N	2806	255	702
14017	\N	2805	246	702
14018	\N	2806	252	702
14019	\N	2806	249	702
14020	\N	2805	247	702
14021	\N	2805	251	702
14022	\N	2806	248	702
14023	\N	2808	909	702
14024	\N	2805	910	702
14025	\N	2805	907	702
14026	\N	2806	257	702
14027	\N	2806	256	702
14028	\N	2806	258	702
14029	\N	2806	260	702
14030	\N	2805	911	702
14031	\N	2806	244	702
14032	\N	2806	912	702
14033	\N	2807	259	702
14034	\N	2808	261	702
14035	\N	2806	906	702
14036	\N	2812	245	703
14037	\N	2812	250	703
14038	\N	2809	253	703
14039	\N	2812	908	703
14040	\N	2812	254	703
14041	\N	2812	255	703
14042	\N	2812	246	703
14043	\N	2812	252	703
14044	\N	2812	249	703
14045	\N	2812	247	703
14046	\N	2812	251	703
14047	\N	2812	248	703
14048	\N	2812	909	703
14049	\N	2812	910	703
14050	\N	2812	907	703
14051	\N	2812	257	703
14052	\N	2812	256	703
14053	\N	2812	258	703
14054	\N	2812	260	703
14055	\N	2812	911	703
14056	\N	2812	244	703
14057	\N	2809	912	703
14058	\N	2812	259	703
14059	\N	2811	261	703
14060	\N	2812	906	703
14071	\N	\N	251	704
14086	\N	2820	245	705
14087	\N	2820	250	705
14088	\N	2817	253	705
14089	\N	2817	908	705
14090	\N	2820	254	705
14091	\N	2820	255	705
14092	\N	2817	246	705
14093	\N	2820	252	705
14094	\N	2817	249	705
14095	\N	2820	247	705
14096	\N	2820	251	705
14097	\N	2817	248	705
14098	\N	2820	909	705
14099	\N	2820	910	705
14100	\N	2820	907	705
14101	\N	2820	257	705
14102	\N	2820	256	705
14103	\N	2820	258	705
14104	\N	2819	260	705
14105	\N	2820	911	705
14106	\N	2820	244	705
14107	\N	2817	912	705
14108	\N	2817	259	705
14109	\N	2817	261	705
14110	\N	2820	906	705
14111	\N	2821	245	706
14112	\N	2821	250	706
14113	\N	2821	253	706
14114	\N	2821	908	706
14115	\N	2821	254	706
14116	\N	2821	255	706
14117	\N	2821	246	706
14118	\N	2821	252	706
14119	\N	2821	249	706
14120	\N	2821	247	706
14121	\N	2821	251	706
14122	\N	2821	248	706
14123	\N	2821	909	706
14124	\N	2821	910	706
14125	\N	2821	907	706
14126	\N	2821	257	706
14127	\N	2821	256	706
14128	\N	2821	258	706
14129	\N	2821	260	706
14130	\N	2821	911	706
14131	\N	2821	244	706
14132	\N	2821	912	706
14133	\N	2821	259	706
14134	\N	2821	261	706
14135	\N	2821	906	706
14143	\N	\N	252	707
14161	\N	2829	245	708
14162	\N	2831	250	708
14163	\N	2831	253	708
14164	\N	2831	908	708
14165	\N	2829	254	708
14166	\N	2831	255	708
14167	\N	2829	246	708
14168	\N	2829	252	708
14169	\N	2832	249	708
14170	\N	\N	247	708
14171	\N	\N	251	708
14172	\N	2831	248	708
14173	\N	2831	909	708
14174	\N	2829	910	708
14175	\N	2829	907	708
14176	\N	2831	257	708
14177	\N	2830	256	708
14178	\N	2831	258	708
14179	\N	2829	260	708
14180	\N	2830	911	708
14181	\N	2829	244	708
14182	\N	2831	912	708
14183	\N	2831	259	708
14184	\N	2830	261	708
14185	\N	2830	906	708
14186	\N	2835	245	709
14187	\N	2836	250	709
14188	\N	2836	253	709
14189	\N	2833	908	709
14190	\N	2835	254	709
14191	\N	2835	255	709
14192	\N	2835	246	709
14193	\N	\N	252	709
14194	\N	\N	249	709
14195	\N	2834	247	709
14196	\N	2835	251	709
14197	\N	2835	248	709
14198	\N	2836	909	709
14199	\N	2836	910	709
14200	\N	2835	907	709
14201	\N	2835	257	709
14202	\N	2835	256	709
14203	\N	2834	258	709
14204	\N	2836	260	709
14205	\N	2834	911	709
14206	\N	2835	244	709
14207	\N	2834	912	709
14208	\N	2833	259	709
14209	\N	2836	261	709
14210	\N	2834	906	709
14213	\N	\N	253	710
14220	\N	\N	247	710
14236	\N	\N	245	711
14237	\N	2842	250	711
14238	\N	2842	253	711
14239	\N	2842	908	711
14240	\N	2843	254	711
14241	\N	2842	255	711
14242	\N	2843	246	711
14243	\N	2842	252	711
14244	\N	2841	249	711
14245	\N	2842	247	711
14246	\N	2841	251	711
14247	\N	2842	248	711
14248	\N	2842	909	711
14249	\N	2842	910	711
14250	\N	2842	907	711
14251	\N	2842	257	711
14252	\N	2841	256	711
14253	\N	2842	258	711
14254	\N	2841	260	711
14255	\N	2841	911	711
14256	\N	2841	244	711
14257	\N	2842	912	711
14258	\N	2842	259	711
14259	\N	2842	261	711
14260	\N	2843	906	711
14261	\N	\N	245	712
14262	\N	\N	250	712
14263	\N	\N	253	712
14264	\N	2847	908	712
14265	\N	\N	254	712
14266	\N	\N	255	712
14267	\N	2848	246	712
14268	\N	2848	252	712
14269	\N	\N	249	712
14270	\N	2848	247	712
14271	\N	2848	251	712
14272	\N	2846	248	712
14273	\N	2846	909	712
14274	\N	2847	910	712
14275	\N	2848	907	712
14276	\N	2847	257	712
14277	\N	\N	256	712
14278	\N	2848	258	712
14279	\N	2848	260	712
14280	\N	2847	911	712
14281	\N	\N	244	712
14282	\N	2848	912	712
14283	\N	2848	259	712
14284	\N	2848	261	712
14285	\N	2848	906	712
14286	\N	\N	245	713
14287	\N	\N	250	713
14288	\N	\N	253	713
14289	\N	2852	908	713
14290	\N	\N	254	713
14291	\N	2852	255	713
14292	\N	2852	246	713
14293	\N	\N	252	713
14294	\N	\N	249	713
14295	\N	2852	247	713
14296	\N	2851	251	713
14297	\N	\N	248	713
14298	\N	2849	909	713
14299	\N	2849	910	713
14300	\N	2852	907	713
14301	\N	\N	257	713
14302	\N	2852	256	713
14303	\N	\N	258	713
14304	\N	2852	260	713
14305	\N	2849	911	713
14306	\N	\N	244	713
14307	\N	2852	912	713
14308	\N	\N	259	713
14309	\N	2851	261	713
14310	\N	2849	906	713
14311	\N	2853	245	714
14312	\N	2853	250	714
14313	\N	\N	253	714
14314	\N	2855	908	714
14315	\N	2853	254	714
14316	\N	2853	255	714
14317	\N	2853	246	714
14318	\N	2853	252	714
14319	\N	\N	249	714
14320	\N	2853	247	714
14321	\N	2853	251	714
14322	\N	2855	248	714
14323	\N	2853	909	714
14324	\N	2853	910	714
14325	\N	2853	907	714
14326	\N	2853	257	714
14327	\N	2853	256	714
14328	\N	2853	258	714
14329	\N	2853	260	714
14330	\N	2853	911	714
14331	\N	\N	244	714
14332	\N	2854	912	714
14333	\N	2853	259	714
14334	\N	2855	261	714
14335	\N	2853	906	714
14336	\N	2860	245	715
14337	\N	2860	250	715
14338	\N	\N	253	715
14339	\N	2860	908	715
14340	\N	\N	254	715
14341	\N	2857	255	715
14342	\N	2857	246	715
14343	\N	2859	252	715
14344	\N	2858	249	715
14345	\N	2858	247	715
14346	\N	\N	251	715
14347	\N	2859	248	715
14348	\N	2857	909	715
14349	\N	2858	910	715
14350	\N	2859	907	715
14351	\N	2858	257	715
14352	\N	2858	256	715
14353	\N	2858	258	715
14354	\N	\N	260	715
14355	\N	2860	911	715
14356	\N	\N	244	715
14357	\N	2859	912	715
14358	\N	2858	259	715
14359	\N	\N	261	715
14360	\N	2857	906	715
14361	\N	2864	245	716
14362	\N	2864	250	716
14363	\N	2864	253	716
14364	\N	2864	908	716
14365	\N	2864	254	716
14366	\N	2861	255	716
14367	\N	2864	246	716
14368	\N	2864	252	716
14369	\N	2864	249	716
14370	\N	2864	247	716
14371	\N	2864	251	716
14372	\N	2864	248	716
14373	\N	2864	909	716
14374	\N	2864	910	716
14375	\N	2864	907	716
14376	\N	2864	257	716
14377	\N	2864	256	716
14378	\N	2864	258	716
14379	\N	2861	260	716
14380	\N	2864	911	716
14381	\N	2861	244	716
14382	\N	2862	912	716
14383	\N	2864	259	716
14384	\N	2864	261	716
14385	\N	2864	906	716
14418	\N	\N	252	718
14441	\N	\N	255	719
14442	\N	\N	246	719
14444	\N	\N	249	719
14446	\N	\N	251	719
14452	\N	\N	256	719
14459	\N	\N	261	719
14542	\N	\N	262	728
14543	\N	\N	263	728
14544	\N	\N	264	728
14598	\N	\N	262	735
14706	\N	\N	265	748
14708	\N	\N	266	748
14710	\N	\N	262	749
14713	\N	\N	267	749
14719	\N	\N	263	750
14721	\N	\N	267	750
14726	\N	3001	915	751
14727	\N	3001	269	751
14728	\N	3001	270	751
14729	\N	\N	268	751
14730	\N	3004	919	751
14731	\N	3001	271	751
14732	\N	3001	918	751
14733	\N	3001	916	751
14734	\N	3001	917	751
14735	\N	3006	915	752
14736	\N	\N	269	752
14737	\N	3006	270	752
14738	\N	\N	268	752
14739	\N	3006	919	752
14740	\N	3006	271	752
14741	\N	3006	918	752
14742	\N	3006	916	752
14743	\N	3005	917	752
14744	\N	3009	915	753
14745	\N	3010	269	753
14746	\N	3009	270	753
14747	\N	3009	268	753
14748	\N	3010	919	753
14749	\N	3010	271	753
14750	\N	3010	918	753
14751	\N	3012	916	753
14752	\N	3009	917	753
14753	\N	3013	915	754
14754	\N	\N	269	754
14755	\N	3013	270	754
14756	\N	3016	268	754
14757	\N	3016	919	754
14758	\N	3013	271	754
14759	\N	3014	918	754
14760	\N	3013	916	754
14761	\N	3013	917	754
14762	\N	3018	915	755
14763	\N	3017	269	755
14764	\N	3018	270	755
14765	\N	3018	268	755
14766	\N	3019	919	755
14767	\N	3018	271	755
14768	\N	3017	918	755
14769	\N	3018	916	755
14770	\N	3018	917	755
14771	\N	3021	915	756
14772	\N	\N	269	756
14773	\N	3023	270	756
14774	\N	3021	268	756
14775	\N	3023	919	756
14776	\N	3021	271	756
14777	\N	3023	918	756
14778	\N	3021	916	756
14779	\N	3021	917	756
14780	\N	3025	915	757
14781	\N	\N	269	757
14782	\N	3026	270	757
14783	\N	3027	268	757
14784	\N	3028	919	757
14785	\N	3026	271	757
14786	\N	3025	918	757
14787	\N	3025	916	757
14788	\N	3025	917	757
14789	\N	3032	915	758
14790	\N	3032	269	758
14791	\N	3030	270	758
14792	\N	\N	268	758
14793	\N	3032	919	758
14794	\N	3032	271	758
14795	\N	3031	918	758
14796	\N	3032	916	758
14797	\N	3032	917	758
14798	\N	3033	915	759
14799	\N	\N	269	759
14800	\N	3033	270	759
14638	\N	3097	262	740
14639	\N	3097	263	740
14642	\N	3097	265	740
14643	\N	3097	914	740
14644	\N	3097	266	740
14640	\N	3100	264	740
14641	\N	3100	267	740
14645	\N	3100	913	740
14646	\N	3041	262	741
14647	\N	3041	263	741
14648	\N	3041	264	741
14649	\N	3041	267	741
14650	\N	3041	265	741
14651	\N	3041	914	741
14652	\N	3041	266	741
14653	\N	3044	913	741
14654	\N	3119	262	742
14655	\N	3119	263	742
14658	\N	3119	265	742
14659	\N	3119	914	742
14801	\N	3033	268	759
14802	\N	3035	919	759
14803	\N	3033	271	759
14804	\N	3033	918	759
14805	\N	3033	916	759
14806	\N	3033	917	759
14807	\N	3040	915	760
14808	\N	3040	269	760
14809	\N	3040	270	760
14810	\N	3040	268	760
14811	\N	3037	919	760
14812	\N	3040	271	760
14813	\N	3040	918	760
14814	\N	3037	916	760
14815	\N	3040	917	760
14816	\N	3041	915	761
14817	\N	3041	269	761
14818	\N	\N	270	761
14819	\N	3043	268	761
14820	\N	3041	919	761
14821	\N	3043	271	761
14822	\N	3041	918	761
14823	\N	3041	916	761
14824	\N	3041	917	761
14825	\N	3048	915	762
14826	\N	3048	269	762
14827	\N	3048	270	762
14828	\N	3048	268	762
14829	\N	3048	919	762
14830	\N	3047	271	762
14831	\N	3046	918	762
14832	\N	3047	916	762
14833	\N	3048	917	762
14834	\N	3050	915	763
14835	\N	3049	269	763
14836	\N	3050	270	763
14837	\N	3050	268	763
14838	\N	3049	919	763
14839	\N	\N	271	763
14840	\N	3050	918	763
14841	\N	3052	916	763
14842	\N	3050	917	763
14843	\N	3055	915	764
14844	\N	3054	269	764
14845	\N	3055	270	764
14846	\N	3054	268	764
14847	\N	3054	919	764
14848	\N	\N	271	764
14849	\N	3053	918	764
14850	\N	3054	916	764
14851	\N	3054	917	764
14852	\N	3058	915	765
14853	\N	\N	269	765
14854	\N	3057	270	765
14855	\N	3058	268	765
14856	\N	3057	919	765
14857	\N	3058	271	765
14858	\N	3058	918	765
14859	\N	3058	916	765
14860	\N	3058	917	765
14870	\N	3068	915	767
14871	\N	3066	269	767
14872	\N	3065	270	767
14873	\N	3065	268	767
14874	\N	3065	919	767
14875	\N	\N	271	767
14876	\N	3068	918	767
14877	\N	3065	916	767
14878	\N	3065	917	767
14879	\N	3069	915	768
14880	\N	3072	269	768
14881	\N	3069	270	768
14882	\N	3071	268	768
14883	\N	3072	919	768
14884	\N	3069	271	768
14885	\N	3069	918	768
14886	\N	3072	916	768
14887	\N	3069	917	768
14888	\N	3075	915	769
14889	\N	3075	269	769
14890	\N	3075	270	769
14891	\N	3075	268	769
14892	\N	3075	919	769
14893	\N	3075	271	769
14894	\N	3076	918	769
14895	\N	3075	916	769
14896	\N	3075	917	769
14897	\N	3077	915	770
14898	\N	3077	269	770
14899	\N	3079	270	770
14900	\N	\N	268	770
14901	\N	3079	919	770
14902	\N	\N	271	770
14903	\N	3077	918	770
14904	\N	3077	916	770
14905	\N	3077	917	770
14909	\N	\N	268	771
14915	\N	3087	915	772
14916	\N	3085	269	772
14917	\N	3085	270	772
14918	\N	3087	268	772
14919	\N	3087	919	772
14920	\N	3085	271	772
14921	\N	3085	918	772
14922	\N	3085	916	772
14923	\N	3085	917	772
14924	\N	3090	915	773
14925	\N	\N	269	773
14926	\N	3089	270	773
14927	\N	3092	268	773
14928	\N	3092	919	773
14929	\N	3089	271	773
14930	\N	3090	918	773
14931	\N	3089	916	773
14932	\N	3089	917	773
14934	\N	\N	269	774
14942	\N	3100	915	775
14943	\N	3100	269	775
14944	\N	3097	270	775
14945	\N	3100	268	775
14946	\N	3100	919	775
14947	\N	3100	271	775
14948	\N	3098	918	775
14949	\N	3100	916	775
14950	\N	3100	917	775
14952	\N	\N	269	776
14954	\N	\N	268	776
14960	\N	3105	915	777
14961	\N	\N	269	777
14962	\N	3105	270	777
14963	\N	3106	268	777
14964	\N	3108	919	777
14965	\N	3105	271	777
14966	\N	3108	918	777
14967	\N	3105	916	777
14968	\N	3106	917	777
14969	\N	3110	915	778
14970	\N	\N	269	778
14971	\N	\N	270	778
14972	\N	3110	268	778
14973	\N	3110	919	778
14974	\N	3110	271	778
14975	\N	3110	918	778
14976	\N	3112	916	778
14977	\N	3111	917	778
14978	\N	3116	915	779
14979	\N	\N	269	779
14980	\N	3114	270	779
14981	\N	\N	268	779
14982	\N	3116	919	779
14983	\N	\N	271	779
14984	\N	3114	918	779
14985	\N	3115	916	779
14986	\N	3116	917	779
14987	\N	3119	915	780
14988	\N	\N	269	780
14989	\N	3117	270	780
14990	\N	3119	268	780
14991	\N	3117	919	780
14992	\N	3120	271	780
14993	\N	3119	918	780
14994	\N	3120	916	780
14995	\N	3117	917	780
14998	\N	\N	272	781
15014	\N	\N	272	783
15030	\N	\N	272	785
15036	\N	3141	927	786
15037	\N	3144	932	786
15038	\N	3144	933	786
15039	\N	3142	929	786
15040	\N	3144	934	786
15041	\N	3142	928	786
15042	\N	3142	930	786
15043	\N	3142	931	786
15044	\N	3148	927	787
15045	\N	3145	932	787
15046	\N	3145	933	787
15047	\N	3146	929	787
15048	\N	3148	934	787
15049	\N	3147	928	787
15050	\N	3146	930	787
15051	\N	3145	931	787
15052	\N	3151	927	788
15053	\N	3150	932	788
15054	\N	3152	933	788
15055	\N	3150	929	788
15056	\N	3150	934	788
15057	\N	3150	928	788
15058	\N	3150	930	788
15059	\N	3151	931	788
15060	\N	3155	927	789
15061	\N	3154	932	789
15062	\N	3156	933	789
15063	\N	3155	929	789
15064	\N	3155	934	789
15065	\N	3154	928	789
15066	\N	3155	930	789
15067	\N	3154	931	789
15068	\N	3158	927	790
15069	\N	3159	932	790
15070	\N	3157	933	790
15071	\N	3160	929	790
15072	\N	3160	934	790
15073	\N	3160	928	790
15074	\N	3160	930	790
15075	\N	3160	931	790
15076	\N	\N	273	791
15110	\N	\N	276	793
15112	\N	\N	274	793
15115	\N	\N	275	793
15141	\N	\N	277	795
15146	\N	\N	278	795
15156	\N	3184	949	796
15157	\N	3184	950	796
15158	\N	3184	955	796
15159	\N	3182	947	796
15160	\N	\N	279	796
15161	\N	3184	953	796
15162	\N	3184	954	796
15163	\N	3184	951	796
15164	\N	3181	945	796
15165	\N	3181	281	796
15166	\N	3181	946	796
15167	\N	3184	952	796
15168	\N	3184	956	796
15169	\N	3184	957	796
15170	\N	\N	280	796
15171	\N	3182	948	796
15172	\N	3188	949	797
15173	\N	3186	950	797
15174	\N	3186	955	797
15175	\N	3187	947	797
15176	\N	3188	279	797
15177	\N	3187	953	797
15178	\N	3185	954	797
15179	\N	3186	951	797
15180	\N	3188	945	797
15181	\N	3186	281	797
15182	\N	3186	946	797
15183	\N	3186	952	797
15184	\N	3187	956	797
15185	\N	3186	957	797
15186	\N	3187	280	797
15187	\N	3187	948	797
15188	\N	3191	949	798
15189	\N	3190	950	798
15190	\N	3190	955	798
15191	\N	3192	947	798
15192	\N	3191	279	798
15193	\N	3190	953	798
15194	\N	3192	954	798
15195	\N	3192	951	798
15196	\N	3190	945	798
15197	\N	\N	281	798
15198	\N	3190	946	798
15199	\N	3191	952	798
15200	\N	3190	956	798
15201	\N	3190	957	798
15202	\N	3190	280	798
15203	\N	3190	948	798
15204	\N	3195	949	799
15205	\N	3195	950	799
15206	\N	3195	955	799
15207	\N	3195	947	799
15208	\N	3195	279	799
15209	\N	3195	953	799
15210	\N	3195	954	799
15211	\N	3195	951	799
15212	\N	3195	945	799
15213	\N	3195	281	799
15214	\N	3195	946	799
15215	\N	3195	952	799
15216	\N	3195	956	799
15217	\N	3195	957	799
15218	\N	3195	280	799
15219	\N	3195	948	799
15220	\N	3200	949	800
15221	\N	3200	950	800
15222	\N	3200	955	800
15223	\N	3200	947	800
15224	\N	3200	279	800
15225	\N	3199	953	800
15226	\N	3200	954	800
15227	\N	3200	951	800
15228	\N	3200	945	800
15229	\N	3200	281	800
15230	\N	3200	946	800
15231	\N	3200	952	800
15232	\N	3200	956	800
15233	\N	3200	957	800
15234	\N	3197	280	800
15235	\N	3200	948	800
15253	\N	\N	282	803
15281	\N	3227	967	807
15282	\N	3225	968	807
15283	\N	3225	969	807
15284	\N	3228	966	807
15285	\N	3227	965	807
15286	\N	3229	967	808
15287	\N	3229	968	808
15288	\N	3229	969	808
15289	\N	3229	966	808
15290	\N	3229	965	808
15291	\N	3236	967	809
15292	\N	3234	968	809
15293	\N	3234	969	809
15294	\N	3234	966	809
15295	\N	3234	965	809
15296	\N	3240	967	810
15297	\N	3240	968	810
15298	\N	3240	969	810
15299	\N	3240	966	810
15300	\N	3239	965	810
15302	\N	\N	283	811
15334	\N	\N	284	813
15344	\N	3261	980	813
15345	\N	3261	975	813
15331	\N	3262	970	813
15332	\N	3262	283	813
15333	\N	3262	977	813
15335	\N	3262	971	813
15336	\N	3262	285	813
15337	\N	3262	286	813
15338	\N	3262	974	813
15339	\N	3262	976	813
15340	\N	3262	978	813
15341	\N	3262	979	813
15342	\N	3262	972	813
15343	\N	3262	973	813
15346	\N	4622	970	814
15347	\N	4622	283	814
15348	\N	4622	977	814
15349	\N	4622	284	814
15350	\N	4622	971	814
15351	\N	4622	285	814
15352	\N	4622	286	814
15353	\N	4622	974	814
15354	\N	4622	976	814
15355	\N	4621	978	814
15366	\N	\N	285	815
15367	\N	\N	286	815
15376	\N	3262	290	816
15377	\N	3262	289	816
15378	\N	3261	981	816
15379	\N	3262	992	816
15380	\N	3262	982	816
15381	\N	3262	986	816
15382	\N	3262	988	816
15383	\N	3262	991	816
15384	\N	3262	287	816
15385	\N	3262	288	816
15386	\N	3262	983	816
15387	\N	3262	984	816
15388	\N	3262	985	816
15389	\N	3262	987	816
15390	\N	3264	993	816
15391	\N	3262	989	816
15392	\N	3262	990	816
15393	\N	3264	994	816
15402	\N	\N	287	817
15412	\N	3270	290	818
15413	\N	\N	289	818
15414	\N	3269	981	818
15415	\N	3272	992	818
15416	\N	3272	982	818
15417	\N	3271	986	818
15418	\N	3271	988	818
15419	\N	3271	991	818
15420	\N	3270	287	818
15421	\N	\N	288	818
15422	\N	3270	983	818
15423	\N	3271	984	818
15424	\N	3272	985	818
15425	\N	3271	987	818
15426	\N	3271	993	818
15427	\N	3271	989	818
15428	\N	3270	990	818
15429	\N	3271	994	818
15448	\N	\N	290	820
15449	\N	\N	289	820
15450	\N	3279	981	820
15451	\N	3277	992	820
15452	\N	3278	982	820
15453	\N	3279	986	820
15454	\N	3277	988	820
15455	\N	3279	991	820
15456	\N	3277	287	820
15457	\N	3279	288	820
15458	\N	3279	983	820
15459	\N	3277	984	820
15460	\N	3277	985	820
15461	\N	3279	987	820
15462	\N	3277	993	820
15463	\N	3279	989	820
15464	\N	3277	990	820
15465	\N	3277	994	820
15527	\N	3315	1003	829
15528	\N	3315	1005	829
15529	\N	\N	291	829
15530	\N	3314	1007	829
15531	\N	3315	1004	829
15532	\N	3314	1006	829
15533	\N	3315	1008	829
15559	\N	\N	292	832
15612	\N	\N	293	835
15636	\N	3348	1027	837
15637	\N	3347	1029	837
15638	\N	3348	295	837
15639	\N	3348	1032	837
15640	\N	3348	1024	837
15641	\N	3348	1025	837
15642	\N	3346	1026	837
15643	\N	3348	294	837
15644	\N	3348	1028	837
15645	\N	3345	1030	837
15646	\N	3348	1031	837
15647	\N	3348	1033	837
15648	\N	3347	1034	837
15649	\N	3347	1035	837
15650	\N	3347	1023	837
15658	\N	\N	294	838
15668	\N	\N	295	839
15673	\N	\N	294	839
15681	\N	3360	1027	840
15682	\N	3360	1029	840
15683	\N	3359	295	840
15684	\N	3360	1032	840
15685	\N	3360	1024	840
15686	\N	3360	1025	840
15687	\N	3360	1026	840
15688	\N	3359	294	840
15689	\N	3360	1028	840
15690	\N	3360	1030	840
15691	\N	3360	1031	840
15692	\N	3360	1033	840
15693	\N	3360	1034	840
15694	\N	3360	1035	840
15695	\N	3360	1023	840
15718	\N	\N	296	843
15741	\N	3384	1045	846
15742	\N	3384	1047	846
15743	\N	3384	1048	846
15744	\N	3384	1049	846
15745	\N	3384	1050	846
15746	\N	3384	1044	846
15747	\N	3384	1046	846
15748	\N	3384	1051	846
15749	\N	3384	1052	846
15759	\N	3391	1045	848
15760	\N	3392	1047	848
15761	\N	3390	1048	848
15762	\N	3389	1049	848
15763	\N	3390	1050	848
15764	\N	3390	1044	848
15765	\N	3390	1046	848
15766	\N	3390	1051	848
15767	\N	3390	1052	848
15787	\N	\N	297	851
15818	\N	\N	298	853
15856	\N	3422	1065	856
15857	\N	3424	1075	856
15858	\N	3424	1067	856
15859	\N	3423	1066	856
15860	\N	3424	1071	856
15861	\N	3424	1072	856
15862	\N	3424	1068	856
15863	\N	3424	1069	856
15864	\N	3424	1070	856
15865	\N	3424	1073	856
15866	\N	3424	1074	856
15867	\N	3424	1076	856
15868	\N	3425	1065	857
15869	\N	3425	1075	857
15870	\N	3425	1067	857
15871	\N	3428	1066	857
15872	\N	3428	1071	857
15873	\N	3426	1072	857
15874	\N	3425	1068	857
15875	\N	3425	1069	857
15876	\N	3425	1070	857
15877	\N	3425	1073	857
15878	\N	3425	1074	857
15879	\N	3425	1076	857
15880	\N	3432	1065	858
15881	\N	3432	1075	858
15882	\N	3432	1067	858
15883	\N	3432	1066	858
15884	\N	3432	1071	858
15885	\N	3432	1072	858
15886	\N	3432	1068	858
15887	\N	3432	1069	858
15888	\N	3432	1070	858
15889	\N	3432	1073	858
15890	\N	3432	1074	858
15891	\N	3432	1076	858
15904	\N	3438	1065	860
15905	\N	3439	1075	860
15906	\N	3439	1067	860
15907	\N	3439	1066	860
15908	\N	3439	1071	860
15909	\N	3439	1072	860
15910	\N	3437	1068	860
15800	\N	3438	1056	852
15803	\N	3438	1053	852
15813	\N	3438	1061	852
15801	\N	3439	297	852
15802	\N	3439	1055	852
15804	\N	3439	298	852
15805	\N	3439	1062	852
15806	\N	3439	1054	852
15807	\N	3439	1063	852
15808	\N	3439	1057	852
15809	\N	3439	1058	852
15810	\N	3439	1064	852
15811	\N	3439	1059	852
15812	\N	3439	1060	852
15814	\N	3425	1056	853
15815	\N	3425	297	853
15817	\N	3425	1053	853
15819	\N	3425	1062	853
15820	\N	3425	1054	853
15822	\N	3425	1057	853
15823	\N	3425	1058	853
15825	\N	3425	1059	853
15826	\N	3425	1060	853
15827	\N	3425	1061	853
15821	\N	3426	1063	853
15816	\N	3427	1055	853
15824	\N	3427	1064	853
15828	\N	3432	1056	854
15830	\N	3432	1055	854
15832	\N	3432	298	854
15834	\N	3432	1054	854
15836	\N	3432	1057	854
15837	\N	3432	1058	854
15838	\N	3432	1064	854
15839	\N	3432	1059	854
15840	\N	3432	1060	854
15841	\N	3432	1061	854
15829	\N	3429	297	854
15831	\N	3429	1053	854
15833	\N	3429	1062	854
15835	\N	3429	1063	854
15842	\N	3424	1056	855
15843	\N	3424	297	855
15844	\N	3424	1055	855
15845	\N	3424	1053	855
15846	\N	3424	298	855
15911	\N	3439	1069	860
15912	\N	3438	1070	860
15913	\N	3438	1073	860
15914	\N	3439	1074	860
15915	\N	3439	1076	860
15917	\N	\N	299	861
15987	\N	\N	300	863
16023	\N	\N	301	864
16051	\N	\N	300	865
16060	\N	\N	302	865
16077	\N	\N	299	866
16083	\N	\N	300	866
16087	\N	\N	301	866
16092	\N	\N	302	866
16141	\N	\N	299	868
16142	\N	\N	303	868
16171	\N	\N	304	868
16173	\N	\N	299	869
16175	\N	\N	305	869
16179	\N	\N	300	869
16186	\N	\N	306	869
16204	\N	\N	307	870
16205	\N	\N	299	870
16207	\N	\N	305	870
16208	\N	\N	308	870
16211	\N	\N	300	870
16213	\N	\N	309	870
16217	\N	\N	310	870
16228	\N	\N	311	870
16230	\N	\N	312	870
16235	\N	\N	304	870
16237	\N	\N	299	871
16238	\N	\N	303	871
16240	\N	\N	308	871
16243	\N	\N	300	871
16245	\N	\N	309	871
16247	\N	\N	301	871
16260	\N	\N	311	871
16264	\N	\N	313	871
16265	\N	\N	314	871
16266	\N	\N	315	871
16278	\N	\N	316	872
16307	\N	\N	300	873
16324	\N	\N	311	873
16332	\N	\N	307	874
16335	\N	\N	305	874
16365	\N	\N	299	875
16367	\N	\N	305	875
16371	\N	\N	300	875
16403	\N	\N	300	876
16430	\N	\N	303	877
16438	\N	\N	316	877
16442	\N	\N	306	877
16444	\N	\N	302	877
16452	\N	\N	311	877
16455	\N	\N	317	877
16461	\N	\N	299	878
16462	\N	\N	303	878
16463	\N	\N	305	878
16469	\N	\N	309	878
16478	\N	\N	318	878
16485	\N	\N	319	878
16488	\N	\N	313	878
16490	\N	\N	315	878
16493	\N	\N	299	879
16494	\N	\N	303	879
16499	\N	\N	300	879
16501	\N	\N	309	879
16503	\N	\N	301	879
16506	\N	\N	306	879
16507	\N	\N	320	879
16510	\N	\N	318	879
16518	\N	\N	312	879
16520	\N	\N	313	879
16521	\N	\N	314	879
16522	\N	\N	315	879
16525	\N	\N	299	880
16556	\N	3523	1095	881
16557	\N	3521	331	881
16558	\N	3523	1096	881
16559	\N	3523	1097	881
16560	\N	3521	1087	881
16561	\N	3521	332	881
16562	\N	3521	1088	881
16563	\N	3523	324	881
16564	\N	3523	1098	881
16565	\N	3521	333	881
16566	\N	3521	327	881
16567	\N	\N	321	881
16568	\N	3521	328	881
16569	\N	3521	329	881
16570	\N	3523	325	881
16571	\N	3524	326	881
16572	\N	3523	334	881
16573	\N	3521	1089	881
16574	\N	3523	1099	881
16575	\N	3521	1090	881
16576	\N	3521	1091	881
16577	\N	3523	335	881
16578	\N	3523	336	881
16579	\N	3521	337	881
16580	\N	3521	1092	881
16581	\N	3523	1100	881
16582	\N	3523	338	881
16583	\N	\N	322	881
16584	\N	3521	330	881
16585	\N	3523	1101	881
16586	\N	3523	339	881
16587	\N	3524	340	881
16588	\N	3521	1093	881
16589	\N	\N	323	881
16590	\N	3523	1102	881
16591	\N	3522	1094	881
16628	\N	3529	1095	883
16629	\N	3530	331	883
16630	\N	3532	1096	883
16631	\N	3532	1097	883
16632	\N	3529	1087	883
16633	\N	3529	332	883
16634	\N	3532	1088	883
16635	\N	\N	324	883
16636	\N	3530	1098	883
16637	\N	3529	333	883
16638	\N	3530	327	883
16639	\N	3529	321	883
16640	\N	3532	328	883
16641	\N	3532	329	883
16642	\N	\N	325	883
16643	\N	\N	326	883
16644	\N	3529	334	883
16645	\N	3529	1089	883
16646	\N	3532	1099	883
16647	\N	3531	1090	883
16648	\N	3529	1091	883
16649	\N	3529	335	883
16650	\N	3529	336	883
16651	\N	3529	337	883
16652	\N	3529	1092	883
16653	\N	3529	1100	883
16654	\N	3529	338	883
16655	\N	3529	322	883
16656	\N	3529	330	883
16657	\N	3529	1101	883
16658	\N	3531	339	883
16659	\N	3529	340	883
16660	\N	3529	1093	883
16661	\N	3530	323	883
16662	\N	3529	1102	883
16663	\N	3529	1094	883
16664	\N	3534	1095	884
16665	\N	3536	331	884
16666	\N	3534	1096	884
16667	\N	3536	1097	884
16668	\N	3536	1087	884
16669	\N	3536	332	884
16670	\N	3536	1088	884
16671	\N	3536	324	884
16672	\N	3536	1098	884
16673	\N	3536	333	884
16674	\N	3536	327	884
16675	\N	3536	321	884
16676	\N	3534	328	884
16677	\N	3536	329	884
16678	\N	3536	325	884
16679	\N	3536	326	884
16680	\N	3536	334	884
16681	\N	3536	1089	884
16682	\N	3533	1099	884
16683	\N	3536	1090	884
16684	\N	3536	1091	884
16685	\N	3536	335	884
16686	\N	3536	336	884
16687	\N	3536	337	884
16688	\N	3536	1092	884
16689	\N	3533	1100	884
16690	\N	3536	338	884
16691	\N	3536	322	884
16692	\N	3535	330	884
16693	\N	3536	1101	884
16694	\N	3536	339	884
16695	\N	3536	340	884
16696	\N	3536	1093	884
16697	\N	3536	323	884
16698	\N	3536	1102	884
16699	\N	3536	1094	884
16700	\N	3539	1095	885
16701	\N	3539	331	885
16702	\N	3538	1096	885
16703	\N	3540	1097	885
16704	\N	3540	1087	885
16705	\N	3538	332	885
16706	\N	3539	1088	885
16707	\N	\N	324	885
16708	\N	3540	1098	885
16709	\N	3539	333	885
16710	\N	\N	327	885
16711	\N	3539	321	885
16712	\N	\N	328	885
16713	\N	\N	329	885
16714	\N	3539	325	885
16715	\N	\N	326	885
16716	\N	3539	334	885
16717	\N	3540	1089	885
16718	\N	3539	1099	885
16719	\N	3539	1090	885
16720	\N	3539	1091	885
16721	\N	3540	335	885
16722	\N	3540	336	885
16723	\N	3540	337	885
16724	\N	3539	1092	885
16725	\N	3539	1100	885
16726	\N	3539	338	885
16727	\N	\N	322	885
16728	\N	\N	330	885
16729	\N	3539	1101	885
16730	\N	3539	339	885
16731	\N	3537	340	885
16732	\N	3539	1093	885
16733	\N	\N	323	885
16734	\N	3539	1102	885
16735	\N	3539	1094	885
16736	\N	3543	1095	886
16737	\N	3543	331	886
16738	\N	3543	1096	886
16739	\N	3541	1097	886
16740	\N	3544	1087	886
16741	\N	3541	332	886
16742	\N	3543	1088	886
16743	\N	3543	324	886
16744	\N	3541	1098	886
16745	\N	3541	333	886
16746	\N	3543	327	886
16747	\N	3543	321	886
16748	\N	3543	328	886
16749	\N	3543	329	886
16750	\N	3543	325	886
16751	\N	3543	326	886
16752	\N	3543	334	886
16753	\N	3543	1089	886
16754	\N	3543	1099	886
16755	\N	3543	1090	886
16756	\N	3543	1091	886
16757	\N	3543	335	886
16758	\N	3543	336	886
16759	\N	3543	337	886
16760	\N	3543	1092	886
16761	\N	3543	1100	886
16762	\N	3543	338	886
16763	\N	3543	322	886
16764	\N	\N	330	886
16765	\N	3543	1101	886
16766	\N	3544	339	886
16767	\N	3543	340	886
16768	\N	3543	1093	886
16769	\N	3543	323	886
16770	\N	3543	1102	886
16771	\N	3543	1094	886
16772	\N	3548	1095	887
16773	\N	3548	331	887
16774	\N	3548	1096	887
16775	\N	3548	1097	887
16776	\N	3548	1087	887
16777	\N	3548	332	887
16778	\N	3547	1088	887
16779	\N	3548	324	887
16780	\N	3548	1098	887
16781	\N	3547	333	887
16782	\N	3548	327	887
16783	\N	3548	321	887
16784	\N	3545	328	887
16785	\N	3548	329	887
16786	\N	3548	325	887
16787	\N	3548	326	887
16788	\N	3548	334	887
16789	\N	3548	1089	887
16790	\N	3548	1099	887
16791	\N	3548	1090	887
16792	\N	3548	1091	887
16793	\N	3548	335	887
16794	\N	3548	336	887
16795	\N	3548	337	887
16796	\N	3548	1092	887
16797	\N	3548	1100	887
16798	\N	3548	338	887
16799	\N	3548	322	887
16800	\N	3545	330	887
16801	\N	3548	1101	887
16802	\N	3548	339	887
16803	\N	3548	340	887
16804	\N	3548	1093	887
16805	\N	3548	323	887
16806	\N	3548	1102	887
16807	\N	3548	1094	887
16808	\N	3551	1095	888
16809	\N	3551	331	888
16810	\N	3551	1096	888
16811	\N	3552	1097	888
16812	\N	3552	1087	888
16813	\N	3551	332	888
16814	\N	3551	1088	888
16815	\N	3551	324	888
16816	\N	3551	1098	888
16817	\N	3549	333	888
16818	\N	3551	327	888
16819	\N	3549	321	888
16820	\N	3549	328	888
16821	\N	3551	329	888
16822	\N	3551	325	888
16823	\N	3551	326	888
16824	\N	3551	334	888
16825	\N	3551	1089	888
16826	\N	3551	1099	888
16827	\N	3551	1090	888
16828	\N	3551	1091	888
16829	\N	3551	335	888
16830	\N	3551	336	888
16831	\N	3551	337	888
16832	\N	3551	1092	888
16833	\N	3551	1100	888
16834	\N	3551	338	888
16835	\N	3551	322	888
16836	\N	3549	330	888
16837	\N	3551	1101	888
16838	\N	3551	339	888
16839	\N	3551	340	888
16840	\N	3551	1093	888
16841	\N	3551	323	888
16842	\N	3551	1102	888
16843	\N	3551	1094	888
16844	\N	3554	1095	889
16845	\N	3554	331	889
16846	\N	3554	1096	889
16847	\N	3554	1097	889
16848	\N	3555	1087	889
16849	\N	3554	332	889
16850	\N	3553	1088	889
16851	\N	3553	324	889
16852	\N	3553	1098	889
16853	\N	3554	333	889
16854	\N	3554	327	889
16855	\N	3554	321	889
16856	\N	3554	328	889
16857	\N	3553	329	889
16858	\N	3554	325	889
16859	\N	3554	326	889
16860	\N	3554	334	889
16861	\N	3554	1089	889
16862	\N	3554	1099	889
16863	\N	3554	1090	889
16864	\N	3554	1091	889
16865	\N	3554	335	889
16866	\N	3554	336	889
16867	\N	3554	337	889
16868	\N	3554	1092	889
16869	\N	3554	1100	889
16870	\N	3554	338	889
16871	\N	3553	322	889
16872	\N	3554	330	889
16873	\N	3554	1101	889
16874	\N	3554	339	889
16875	\N	3553	340	889
16876	\N	3554	1093	889
16877	\N	3554	323	889
16878	\N	3553	1102	889
16879	\N	3556	1094	889
16880	\N	3558	1095	890
16881	\N	3560	331	890
16882	\N	3560	1096	890
16883	\N	3559	1097	890
16884	\N	3558	1087	890
16885	\N	3560	332	890
16886	\N	3560	1088	890
16887	\N	3558	324	890
16888	\N	3560	1098	890
16889	\N	3560	333	890
16890	\N	3560	327	890
16891	\N	3560	321	890
16892	\N	3558	328	890
16893	\N	3560	329	890
16894	\N	3560	325	890
16895	\N	3560	326	890
16896	\N	3560	334	890
16897	\N	3560	1089	890
16898	\N	3560	1099	890
16899	\N	3560	1090	890
16900	\N	3560	1091	890
16901	\N	3560	335	890
16902	\N	3560	336	890
16903	\N	3560	337	890
16904	\N	3560	1092	890
16905	\N	3560	1100	890
16906	\N	3558	338	890
16907	\N	3558	322	890
16908	\N	\N	330	890
16909	\N	3560	1101	890
16910	\N	3560	339	890
16911	\N	3558	340	890
16912	\N	3560	1093	890
16913	\N	3560	323	890
16914	\N	3560	1102	890
16915	\N	3558	1094	890
16916	\N	3561	1095	891
16917	\N	\N	331	891
16918	\N	3561	1096	891
16919	\N	3561	1097	891
16920	\N	3561	1087	891
16921	\N	\N	332	891
16922	\N	3562	1088	891
16923	\N	\N	324	891
16924	\N	3564	1098	891
16925	\N	\N	333	891
16926	\N	\N	327	891
16927	\N	\N	321	891
16928	\N	3561	328	891
16929	\N	\N	329	891
16930	\N	3561	325	891
16931	\N	3561	326	891
16932	\N	\N	334	891
16933	\N	3561	1089	891
16934	\N	3562	1099	891
16935	\N	3561	1090	891
16936	\N	3564	1091	891
16937	\N	\N	335	891
16938	\N	\N	336	891
16939	\N	\N	337	891
16940	\N	3564	1092	891
16941	\N	3564	1100	891
16942	\N	\N	338	891
16943	\N	3562	322	891
16944	\N	\N	330	891
16945	\N	3562	1101	891
16946	\N	\N	339	891
16947	\N	3561	340	891
16948	\N	3561	1093	891
16949	\N	\N	323	891
16950	\N	3563	1102	891
16951	\N	3561	1094	891
16952	\N	3568	1095	892
16953	\N	3568	331	892
16954	\N	3568	1096	892
16955	\N	3568	1097	892
16956	\N	3567	1087	892
16957	\N	3568	332	892
16958	\N	3567	1088	892
16959	\N	3568	324	892
16960	\N	3568	1098	892
16961	\N	3568	333	892
16962	\N	3568	327	892
16963	\N	3568	321	892
16964	\N	3568	328	892
16965	\N	3568	329	892
16966	\N	3568	325	892
16967	\N	3568	326	892
16968	\N	3567	334	892
16969	\N	3568	1089	892
16970	\N	3568	1099	892
16971	\N	3566	1090	892
16972	\N	3568	1091	892
16973	\N	3568	335	892
16974	\N	3568	336	892
16975	\N	3568	337	892
16976	\N	3568	1092	892
16977	\N	3568	1100	892
16978	\N	3568	338	892
16979	\N	3568	322	892
16980	\N	3567	330	892
16981	\N	3568	1101	892
16982	\N	3568	339	892
16983	\N	3567	340	892
16984	\N	3568	1093	892
16985	\N	3566	323	892
16986	\N	3568	1102	892
16987	\N	3568	1094	892
16988	\N	3570	1095	893
16989	\N	\N	331	893
16990	\N	3571	1096	893
16991	\N	3570	1097	893
16992	\N	3572	1087	893
16993	\N	3570	332	893
16994	\N	3572	1088	893
16995	\N	3570	324	893
16996	\N	3571	1098	893
16997	\N	3572	333	893
16998	\N	3570	327	893
16999	\N	3572	321	893
17000	\N	3570	328	893
17001	\N	3570	329	893
17002	\N	3572	325	893
17003	\N	\N	326	893
17004	\N	\N	334	893
17005	\N	3571	1089	893
17006	\N	3570	1099	893
17007	\N	3570	1090	893
17008	\N	3570	1091	893
17009	\N	3572	335	893
17010	\N	3570	336	893
17011	\N	3571	337	893
17012	\N	3572	1092	893
17013	\N	3572	1100	893
17014	\N	\N	338	893
17015	\N	\N	322	893
17016	\N	3571	330	893
17017	\N	3570	1101	893
17018	\N	3572	339	893
17019	\N	\N	340	893
17020	\N	3570	1093	893
17021	\N	\N	323	893
17022	\N	3570	1102	893
17023	\N	3570	1094	893
17024	\N	3575	1095	894
17025	\N	3575	331	894
17026	\N	3573	1096	894
17027	\N	3576	1097	894
17028	\N	3575	1087	894
17029	\N	3573	332	894
17030	\N	3575	1088	894
17031	\N	3573	324	894
17032	\N	3573	1098	894
17033	\N	3573	333	894
17034	\N	\N	327	894
17035	\N	3574	321	894
17036	\N	3576	328	894
17037	\N	3573	329	894
17038	\N	3573	325	894
17039	\N	3575	326	894
17040	\N	3575	334	894
17041	\N	3573	1089	894
17042	\N	3574	1099	894
17043	\N	3573	1090	894
17044	\N	3573	1091	894
17045	\N	3575	335	894
17046	\N	3574	336	894
17047	\N	3573	337	894
17048	\N	3575	1092	894
17049	\N	3575	1100	894
17050	\N	3573	338	894
17051	\N	3574	322	894
17052	\N	3573	330	894
17053	\N	3573	1101	894
17054	\N	3574	339	894
17055	\N	3573	340	894
17056	\N	3573	1093	894
17057	\N	3573	323	894
17058	\N	3575	1102	894
17059	\N	3573	1094	894
17060	\N	3580	1095	895
17061	\N	3579	331	895
17062	\N	3579	1096	895
17063	\N	3580	1097	895
17064	\N	3577	1087	895
17065	\N	3579	332	895
17066	\N	3579	1088	895
17067	\N	3580	324	895
17068	\N	3579	1098	895
17069	\N	3580	333	895
17070	\N	3580	327	895
17071	\N	3579	321	895
17072	\N	3579	328	895
17073	\N	3579	329	895
17074	\N	3579	325	895
17075	\N	3579	326	895
17076	\N	3579	334	895
17077	\N	3580	1089	895
17078	\N	3577	1099	895
17079	\N	3579	1090	895
17080	\N	3579	1091	895
17081	\N	3579	335	895
17082	\N	3580	336	895
17083	\N	3579	337	895
17084	\N	3579	1092	895
17085	\N	3579	1100	895
17086	\N	3579	338	895
17087	\N	3579	322	895
17088	\N	3579	330	895
17089	\N	3580	1101	895
17090	\N	3580	339	895
17091	\N	3580	340	895
17092	\N	3579	1093	895
17093	\N	3579	323	895
17094	\N	3579	1102	895
17095	\N	3579	1094	895
17096	\N	3584	1095	896
17097	\N	3584	331	896
17098	\N	3584	1096	896
17099	\N	3584	1097	896
17100	\N	3584	1087	896
17101	\N	3584	332	896
17102	\N	3584	1088	896
17103	\N	3584	324	896
17104	\N	3584	1098	896
17105	\N	3584	333	896
17106	\N	3584	327	896
17107	\N	3584	321	896
17108	\N	3583	328	896
17109	\N	3584	329	896
17110	\N	3584	325	896
17111	\N	3584	326	896
17112	\N	3584	334	896
17113	\N	3584	1089	896
17114	\N	3584	1099	896
17115	\N	3584	1090	896
17116	\N	3584	1091	896
17117	\N	3584	335	896
17118	\N	3584	336	896
17119	\N	3584	337	896
17120	\N	3584	1092	896
17121	\N	3584	1100	896
17122	\N	3584	338	896
17123	\N	3584	322	896
17124	\N	3584	330	896
17125	\N	3584	1101	896
17126	\N	3584	339	896
17127	\N	3584	340	896
17128	\N	3584	1093	896
17129	\N	3584	323	896
17130	\N	3584	1102	896
17131	\N	3584	1094	896
17132	\N	3587	1095	897
17133	\N	3587	331	897
17134	\N	3586	1096	897
17135	\N	3586	1097	897
17136	\N	3587	1087	897
17137	\N	3586	332	897
17138	\N	3586	1088	897
17139	\N	3586	324	897
17140	\N	3587	1098	897
17141	\N	3586	333	897
17142	\N	3587	327	897
17143	\N	3587	321	897
17144	\N	3587	328	897
17145	\N	3587	329	897
17146	\N	3586	325	897
17147	\N	3586	326	897
17148	\N	3586	334	897
17149	\N	3586	1089	897
17150	\N	3585	1099	897
17151	\N	3586	1090	897
17152	\N	3586	1091	897
17153	\N	3586	335	897
17154	\N	3586	336	897
17155	\N	3586	337	897
17156	\N	3586	1092	897
17157	\N	3587	1100	897
17158	\N	3586	338	897
17159	\N	3586	322	897
17160	\N	\N	330	897
17161	\N	3586	1101	897
17162	\N	3586	339	897
17163	\N	3586	340	897
17164	\N	3586	1093	897
17165	\N	3586	323	897
17166	\N	3586	1102	897
17167	\N	3586	1094	897
17169	\N	\N	331	898
17173	\N	\N	332	898
17175	\N	\N	324	898
17196	\N	\N	330	898
17204	\N	3593	1095	899
17205	\N	3593	331	899
17206	\N	3596	1096	899
17207	\N	3593	1097	899
17208	\N	3593	1087	899
17209	\N	3593	332	899
17210	\N	3593	1088	899
17211	\N	3593	324	899
17212	\N	3596	1098	899
17213	\N	3596	333	899
17214	\N	3593	327	899
17215	\N	3593	321	899
17216	\N	3593	328	899
17217	\N	3593	329	899
17218	\N	3596	325	899
17219	\N	3593	326	899
17220	\N	3596	334	899
17221	\N	3593	1089	899
17222	\N	3593	1099	899
17223	\N	3593	1090	899
17224	\N	3593	1091	899
17225	\N	\N	335	899
17226	\N	3593	336	899
17227	\N	3596	337	899
17228	\N	3593	1092	899
17229	\N	3595	1100	899
17230	\N	3595	338	899
17231	\N	3593	322	899
17232	\N	3593	330	899
17233	\N	3593	1101	899
17234	\N	3593	339	899
17235	\N	3594	340	899
17236	\N	3595	1093	899
17237	\N	3596	323	899
17238	\N	3593	1102	899
17239	\N	3593	1094	899
17240	\N	3600	1095	900
17241	\N	\N	331	900
17242	\N	3597	1096	900
17243	\N	3599	1097	900
17244	\N	3600	1087	900
17245	\N	\N	332	900
17246	\N	3600	1088	900
17247	\N	3599	324	900
17248	\N	3599	1098	900
17249	\N	3599	333	900
17250	\N	\N	327	900
17251	\N	\N	321	900
17252	\N	3599	328	900
17253	\N	3598	329	900
17254	\N	\N	325	900
17255	\N	3599	326	900
17256	\N	3600	334	900
17257	\N	3598	1089	900
17258	\N	3599	1099	900
17259	\N	3599	1090	900
17260	\N	3599	1091	900
17261	\N	3600	335	900
17262	\N	3599	336	900
17263	\N	3599	337	900
17264	\N	3599	1092	900
17265	\N	3600	1100	900
17266	\N	\N	338	900
17267	\N	3598	322	900
17268	\N	3599	330	900
17269	\N	3600	1101	900
17270	\N	\N	339	900
17271	\N	3600	340	900
17272	\N	3600	1093	900
17273	\N	\N	323	900
17274	\N	3599	1102	900
17275	\N	3600	1094	900
17286	\N	\N	341	901
17294	\N	\N	342	902
17310	\N	\N	343	903
17341	\N	\N	344	905
17354	\N	\N	345	906
17359	\N	\N	346	906
17383	\N	\N	344	908
17387	\N	\N	346	908
17392	\N	\N	342	909
17407	\N	\N	347	910
17422	\N	\N	343	911
17425	\N	\N	344	911
17426	\N	\N	341	911
17449	\N	\N	347	913
17450	\N	\N	343	913
17453	\N	\N	344	913
17478	\N	\N	343	915
17481	\N	\N	344	915
17484	\N	\N	348	915
17513	\N	\N	346	917
17527	\N	\N	346	918
17537	\N	\N	344	919
17541	\N	\N	346	919
17548	\N	\N	343	920
17556	\N	3681	351	921
17557	\N	3681	1109	921
17558	\N	3684	1112	921
17559	\N	3684	354	921
17560	\N	3683	352	921
17561	\N	3684	1111	921
17562	\N	3684	1113	921
17563	\N	3684	1114	921
17564	\N	3684	1110	921
17565	\N	\N	349	921
17566	\N	3684	355	921
17567	\N	3683	353	921
17568	\N	\N	350	921
17569	\N	3688	351	922
17570	\N	3686	1109	922
17571	\N	3685	1112	922
17572	\N	3685	354	922
17573	\N	3686	352	922
17574	\N	3687	1111	922
17575	\N	3685	1113	922
17576	\N	3685	1114	922
17577	\N	3687	1110	922
17578	\N	\N	349	922
17579	\N	3687	355	922
17580	\N	3685	353	922
17581	\N	3687	350	922
17582	\N	\N	351	923
17583	\N	3691	1109	923
17584	\N	3690	1112	923
17585	\N	3689	354	923
17586	\N	3692	352	923
17587	\N	3691	1111	923
17588	\N	3690	1113	923
17589	\N	3691	1114	923
17590	\N	3692	1110	923
17591	\N	3691	349	923
17592	\N	3692	355	923
17593	\N	3691	353	923
17594	\N	3691	350	923
17595	\N	3696	351	924
17596	\N	3695	1109	924
17597	\N	3696	1112	924
17598	\N	3696	354	924
17599	\N	3696	352	924
17600	\N	3696	1111	924
17601	\N	3696	1113	924
17602	\N	3696	1114	924
17603	\N	3696	1110	924
17604	\N	3696	349	924
17605	\N	3696	355	924
17606	\N	3696	353	924
17607	\N	3696	350	924
17608	\N	\N	351	925
17634	\N	3706	351	927
17635	\N	3706	1109	927
17636	\N	3706	1112	927
17637	\N	3706	354	927
17638	\N	3706	352	927
17639	\N	3706	1111	927
17640	\N	3706	1113	927
17641	\N	3706	1114	927
17642	\N	3706	1110	927
17643	\N	3706	349	927
17644	\N	3706	355	927
17645	\N	3706	353	927
17646	\N	3706	350	927
17647	\N	3711	351	928
17648	\N	3710	1109	928
17649	\N	3710	1112	928
17650	\N	3709	354	928
17651	\N	3709	352	928
17652	\N	3710	1111	928
17653	\N	3710	1113	928
17654	\N	3709	1114	928
17655	\N	3709	1110	928
17656	\N	3710	349	928
17657	\N	3709	355	928
17658	\N	3710	353	928
17659	\N	3709	350	928
17660	\N	3713	351	929
17661	\N	3715	1109	929
17662	\N	3715	1112	929
17663	\N	3715	354	929
17664	\N	3715	352	929
17665	\N	3715	1111	929
17666	\N	3715	1113	929
17667	\N	3715	1114	929
17668	\N	3715	1110	929
17669	\N	3715	349	929
17670	\N	3715	355	929
17671	\N	3715	353	929
17672	\N	3714	350	929
17673	\N	3719	351	930
17674	\N	3719	1109	930
17675	\N	3719	1112	930
17676	\N	3718	354	930
17677	\N	\N	352	930
17678	\N	3718	1111	930
17679	\N	3719	1113	930
17680	\N	3719	1114	930
17681	\N	3719	1110	930
17682	\N	\N	349	930
17683	\N	3718	355	930
17684	\N	3719	353	930
17685	\N	3720	350	930
17686	\N	3722	351	931
17687	\N	3723	1109	931
17688	\N	3722	1112	931
17689	\N	3723	354	931
17690	\N	3723	352	931
17691	\N	3722	1111	931
17692	\N	3723	1113	931
17693	\N	3721	1114	931
17694	\N	3723	1110	931
17695	\N	3723	349	931
17696	\N	3723	355	931
17697	\N	3723	353	931
17698	\N	3723	350	931
17710	\N	\N	353	932
17712	\N	3731	351	933
17713	\N	3732	1109	933
17714	\N	3731	1112	933
17715	\N	3729	354	933
17716	\N	3731	352	933
17717	\N	3729	1111	933
17718	\N	3731	1113	933
17719	\N	3731	1114	933
17720	\N	3731	1110	933
17721	\N	\N	349	933
17722	\N	3729	355	933
17723	\N	3731	353	933
17724	\N	\N	350	933
17725	\N	3736	351	934
17726	\N	3734	1109	934
17727	\N	3734	1112	934
17728	\N	\N	354	934
17729	\N	3733	352	934
17730	\N	3736	1111	934
17731	\N	3733	1113	934
17732	\N	3734	1114	934
17733	\N	3734	1110	934
17734	\N	\N	349	934
17735	\N	\N	355	934
17736	\N	3736	353	934
17737	\N	3733	350	934
17738	\N	\N	351	935
17742	\N	\N	352	935
17747	\N	\N	349	935
17750	\N	\N	350	935
17751	\N	3743	351	936
17752	\N	3744	1109	936
17753	\N	3744	1112	936
17754	\N	3744	354	936
17755	\N	3743	352	936
17756	\N	3741	1111	936
17757	\N	3744	1113	936
17758	\N	3743	1114	936
17759	\N	3744	1110	936
17760	\N	3744	349	936
17761	\N	3744	355	936
17762	\N	3744	353	936
17763	\N	3743	350	936
17764	\N	\N	351	937
17765	\N	3747	1109	937
17766	\N	3747	1112	937
17767	\N	\N	354	937
17768	\N	3746	352	937
17769	\N	3747	1111	937
17770	\N	3747	1113	937
17771	\N	3747	1114	937
17772	\N	3746	1110	937
17773	\N	3746	349	937
17774	\N	3747	355	937
17775	\N	3747	353	937
17776	\N	\N	350	937
17777	\N	3751	351	938
17778	\N	3749	1109	938
17779	\N	3749	1112	938
17780	\N	3751	354	938
17781	\N	3752	352	938
17782	\N	3752	1111	938
17783	\N	3749	1113	938
17784	\N	3749	1114	938
17785	\N	3749	1110	938
17786	\N	3752	349	938
17787	\N	3749	355	938
17788	\N	3749	353	938
17789	\N	3749	350	938
17803	\N	3759	351	940
17804	\N	3758	1109	940
17805	\N	3759	1112	940
17806	\N	3758	354	940
17807	\N	3759	352	940
17808	\N	3759	1111	940
17809	\N	3759	1113	940
17810	\N	3758	1114	940
17811	\N	3759	1110	940
17812	\N	\N	349	940
17813	\N	3758	355	940
17814	\N	3759	353	940
17815	\N	3758	350	940
17891	\N	3783	356	946
17892	\N	3783	1131	946
17893	\N	3783	1132	946
17894	\N	3783	1133	946
17895	\N	3781	1130	946
17896	\N	3783	1140	946
17897	\N	3783	1141	946
17898	\N	3783	358	946
17899	\N	3783	1134	946
17900	\N	3783	1135	946
17901	\N	3783	1136	946
17902	\N	3783	1137	946
17903	\N	3783	1138	946
17904	\N	3783	357	946
17905	\N	3783	1139	946
17906	\N	3788	356	947
17907	\N	3788	1131	947
17908	\N	3788	1132	947
17909	\N	3788	1133	947
17910	\N	3788	1130	947
17911	\N	3788	1140	947
17912	\N	3788	1141	947
17913	\N	3787	358	947
17914	\N	3786	1134	947
17915	\N	3788	1135	947
17916	\N	3787	1136	947
17917	\N	3788	1137	947
17918	\N	3788	1138	947
17919	\N	3788	357	947
17920	\N	3788	1139	947
17921	\N	\N	356	948
17922	\N	3792	1131	948
17923	\N	3790	1132	948
17924	\N	3790	1133	948
17925	\N	3792	1130	948
17926	\N	3791	1140	948
17927	\N	3791	1141	948
17928	\N	\N	358	948
17929	\N	3791	1134	948
17930	\N	3790	1135	948
17931	\N	3790	1136	948
17932	\N	3790	1137	948
17933	\N	3790	1138	948
17934	\N	\N	357	948
17935	\N	3790	1139	948
17936	\N	3793	356	949
17937	\N	3794	1131	949
17938	\N	3794	1132	949
17939	\N	3794	1133	949
17940	\N	3794	1130	949
17941	\N	3794	1140	949
17942	\N	3794	1141	949
17943	\N	3795	358	949
17944	\N	3795	1134	949
17945	\N	3794	1135	949
17946	\N	3794	1136	949
17947	\N	3795	1137	949
17948	\N	3794	1138	949
17949	\N	3794	357	949
17950	\N	3795	1139	949
17951	\N	3798	356	950
17952	\N	3797	1131	950
17953	\N	3797	1132	950
17954	\N	3800	1133	950
17955	\N	3800	1130	950
17956	\N	3800	1140	950
17957	\N	3797	1141	950
17958	\N	3797	358	950
17959	\N	3800	1134	950
17960	\N	3800	1135	950
17961	\N	3797	1136	950
17962	\N	3800	1137	950
17963	\N	3797	1138	950
17964	\N	3800	357	950
17965	\N	3797	1139	950
17990	\N	\N	359	952
18039	\N	\N	360	955
18041	\N	3822	1157	956
18042	\N	3824	1165	956
18043	\N	3822	1162	956
18044	\N	\N	361	956
18045	\N	3821	1155	956
18046	\N	3824	363	956
18047	\N	3822	1160	956
18048	\N	3822	1158	956
18049	\N	3822	362	956
18050	\N	3823	1163	956
18051	\N	3822	1159	956
18052	\N	3822	1161	956
18053	\N	3821	1156	956
18054	\N	3823	1164	956
18055	\N	3825	1157	957
18056	\N	3827	1165	957
18057	\N	3828	1162	957
18058	\N	3828	361	957
18059	\N	3828	1155	957
18060	\N	3828	363	957
18061	\N	3828	1160	957
18062	\N	3828	1158	957
18063	\N	3828	362	957
18064	\N	3827	1163	957
18065	\N	3827	1159	957
18066	\N	3828	1161	957
18067	\N	3828	1156	957
18068	\N	3828	1164	957
18069	\N	3830	1157	958
18070	\N	3829	1165	958
18071	\N	3832	1162	958
18072	\N	3829	361	958
18073	\N	3831	1155	958
18074	\N	3829	363	958
18075	\N	3831	1160	958
18076	\N	3831	1158	958
18077	\N	3830	362	958
18078	\N	3830	1163	958
18079	\N	3829	1159	958
18080	\N	3832	1161	958
18081	\N	3830	1156	958
18082	\N	3832	1164	958
18083	\N	3835	1157	959
18084	\N	3836	1165	959
18085	\N	3836	1162	959
18086	\N	3836	361	959
18087	\N	3836	1155	959
18088	\N	3836	363	959
18089	\N	3836	1160	959
18090	\N	3836	1158	959
18091	\N	3836	362	959
18092	\N	3836	1163	959
18093	\N	3836	1159	959
18094	\N	3836	1161	959
18095	\N	3836	1156	959
18096	\N	3836	1164	959
18097	\N	3838	1157	960
18098	\N	3838	1165	960
18099	\N	3838	1162	960
18100	\N	3838	361	960
18101	\N	3838	1155	960
18102	\N	\N	363	960
18103	\N	3839	1160	960
18104	\N	3838	1158	960
18105	\N	\N	362	960
18106	\N	3838	1163	960
18107	\N	3839	1159	960
18108	\N	3838	1161	960
18109	\N	3838	1156	960
18110	\N	3837	1164	960
18011	\N	3822	1144	954
18012	\N	3822	1142	954
18014	\N	3822	1146	954
18016	\N	3822	1152	954
18017	\N	3822	1153	954
18018	\N	3822	1154	954
18019	\N	3822	1143	954
18022	\N	3822	1148	954
18023	\N	3822	1150	954
18024	\N	3822	360	954
18020	\N	3821	359	954
18021	\N	3821	1147	954
18035	\N	3837	359	955
18038	\N	3837	1150	955
18026	\N	3838	1144	955
18029	\N	3838	1146	955
18031	\N	3838	1152	955
18033	\N	3838	1154	955
18034	\N	3838	1143	955
18036	\N	3838	1147	955
18037	\N	3838	1148	955
18040	\N	3838	1151	955
18027	\N	3839	1142	955
18028	\N	3839	1145	955
18030	\N	3839	1149	955
18032	\N	3839	1153	955
18111	\N	3865	1166	961
18112	\N	3865	1171	961
18113	\N	3865	1173	961
18114	\N	3865	1168	961
18115	\N	3865	1167	961
18116	\N	3865	1169	961
18117	\N	3865	1170	961
18118	\N	3865	1172	961
18119	\N	3871	1166	962
18120	\N	3871	1171	962
18121	\N	3871	1173	962
18122	\N	3871	1168	962
18123	\N	3871	1167	962
18124	\N	3871	1169	962
18125	\N	3871	1170	962
18126	\N	3871	1172	962
18128	\N	3877	1171	963
18151	\N	\N	364	966
18152	\N	3862	1174	966
18153	\N	3863	1178	966
18154	\N	3863	1179	966
18155	\N	3862	1175	966
18156	\N	3863	1176	966
18157	\N	3863	1177	966
18158	\N	3865	364	967
18159	\N	3865	1174	967
18160	\N	3865	1178	967
18161	\N	3865	1179	967
18162	\N	3865	1175	967
18163	\N	3865	1176	967
18164	\N	3865	1177	967
18165	\N	3871	364	968
18166	\N	3871	1174	968
18167	\N	3871	1178	968
18168	\N	3869	1179	968
18169	\N	3871	1175	968
18170	\N	3871	1176	968
18171	\N	3872	1177	968
18172	\N	3873	364	969
18173	\N	3875	1174	969
18174	\N	3876	1178	969
18175	\N	3875	1179	969
18176	\N	3876	1175	969
18177	\N	3876	1176	969
18178	\N	3875	1177	969
18179	\N	3877	364	970
18180	\N	3878	1174	970
18181	\N	3877	1178	970
18182	\N	3877	1179	970
18183	\N	3877	1175	970
18184	\N	3879	1176	970
18185	\N	3877	1177	970
18215	\N	\N	365	973
18229	\N	\N	365	974
18244	\N	\N	366	975
18249	\N	\N	367	975
18256	\N	3903	369	976
18257	\N	3901	1192	976
18258	\N	3904	1201	976
18259	\N	3901	1193	976
18260	\N	3901	1194	976
18261	\N	3901	1197	976
18262	\N	3901	1191	976
18263	\N	3903	1199	976
18264	\N	3903	1200	976
18265	\N	\N	368	976
18266	\N	3904	1202	976
18267	\N	3902	1198	976
18268	\N	3901	1195	976
18269	\N	3901	1196	976
18270	\N	3904	1203	976
18271	\N	3906	369	977
18272	\N	3908	1192	977
18273	\N	3908	1201	977
18274	\N	3907	1193	977
18275	\N	3905	1194	977
18276	\N	3907	1197	977
18277	\N	3907	1191	977
18278	\N	3907	1199	977
18279	\N	3908	1200	977
18280	\N	3905	368	977
18281	\N	3907	1202	977
18282	\N	3906	1198	977
18283	\N	3906	1195	977
18284	\N	3905	1196	977
18285	\N	3907	1203	977
18301	\N	3914	369	979
18302	\N	3916	1192	979
18303	\N	3916	1201	979
18304	\N	3916	1193	979
18305	\N	3916	1194	979
18306	\N	3915	1197	979
18307	\N	3915	1191	979
18308	\N	3916	1199	979
18309	\N	3916	1200	979
18310	\N	3914	368	979
18311	\N	3916	1202	979
18312	\N	3916	1198	979
18313	\N	3916	1195	979
18314	\N	3914	1196	979
18315	\N	3916	1203	979
18316	\N	\N	369	980
18317	\N	3919	1192	980
18318	\N	3918	1201	980
18319	\N	3920	1193	980
18320	\N	3920	1194	980
18321	\N	3920	1197	980
18322	\N	3920	1191	980
18323	\N	3920	1199	980
18324	\N	3920	1200	980
18325	\N	3920	368	980
18326	\N	3920	1202	980
18327	\N	3920	1198	980
18328	\N	3918	1195	980
18329	\N	3920	1196	980
18330	\N	3920	1203	980
18348	\N	\N	370	982
18373	\N	\N	371	984
18374	\N	\N	372	984
18393	\N	\N	373	986
18404	\N	3948	1217	987
18405	\N	3948	1213	987
18406	\N	3948	373	987
18407	\N	3948	1220	987
18408	\N	3948	376	987
18409	\N	3946	1221	987
18410	\N	\N	374	987
18411	\N	3948	375	987
18412	\N	3945	1218	987
18413	\N	3946	1219	987
18414	\N	3946	1214	987
18415	\N	3946	1215	987
18416	\N	3948	1216	987
18424	\N	\N	375	988
18430	\N	3954	1217	989
18431	\N	3954	1213	989
18432	\N	3954	373	989
18433	\N	3954	1220	989
18434	\N	3954	376	989
18435	\N	3954	1221	989
18436	\N	3953	374	989
18437	\N	3954	375	989
18438	\N	3954	1218	989
18439	\N	3956	1219	989
18440	\N	3956	1214	989
18441	\N	3954	1215	989
18442	\N	3953	1216	989
18447	\N	\N	376	990
18476	\N	\N	377	993
18491	\N	\N	378	994
18509	\N	3985	1232	997
18510	\N	3988	1234	997
18511	\N	3985	1233	997
18512	\N	3988	1230	997
18513	\N	3988	1229	997
18514	\N	\N	379	997
18515	\N	3986	1231	997
18516	\N	\N	380	997
18517	\N	3992	1232	998
18518	\N	3989	1234	998
18519	\N	3989	1233	998
18520	\N	3990	1230	998
18521	\N	3990	1229	998
18522	\N	3989	379	998
18523	\N	3989	1231	998
18524	\N	3990	380	998
18533	\N	3998	1232	1000
18534	\N	4000	1234	1000
18535	\N	3997	1233	1000
18536	\N	3997	1230	1000
18537	\N	3997	1229	1000
18538	\N	3997	379	1000
18539	\N	4000	1231	1000
18540	\N	3997	380	1000
18548	\N	\N	381	1001
18584	\N	\N	382	1004
18585	\N	\N	383	1004
18611	\N	4023	1248	1006
18612	\N	4024	384	1006
18613	\N	4021	1247	1006
18614	\N	4024	1252	1006
18615	\N	4024	1253	1006
18616	\N	4023	1249	1006
18617	\N	4023	1250	1006
18618	\N	4024	1251	1006
18619	\N	4021	1246	1006
18620	\N	4024	1254	1006
18621	\N	4024	1255	1006
18622	\N	4028	1248	1007
18623	\N	4027	384	1007
18624	\N	4025	1247	1007
18625	\N	4028	1252	1007
18626	\N	4027	1253	1007
18627	\N	4027	1249	1007
18628	\N	4025	1250	1007
18629	\N	4028	1251	1007
18630	\N	4025	1246	1007
18631	\N	4026	1254	1007
18632	\N	4027	1255	1007
18634	\N	\N	384	1008
18644	\N	4033	1248	1009
18645	\N	4034	384	1009
18646	\N	4034	1247	1009
18647	\N	4034	1252	1009
18648	\N	4034	1253	1009
18649	\N	4034	1249	1009
18650	\N	4034	1250	1009
18651	\N	4034	1251	1009
18652	\N	4034	1246	1009
18653	\N	4034	1254	1009
18654	\N	4034	1255	1009
18655	\N	4038	1248	1010
18656	\N	4039	384	1010
18657	\N	4039	1247	1010
18658	\N	4039	1252	1010
18659	\N	4039	1253	1010
18660	\N	4039	1249	1010
18661	\N	4039	1250	1010
18662	\N	4039	1251	1010
18663	\N	4039	1246	1010
18664	\N	4039	1254	1010
18665	\N	4039	1255	1010
18601	\N	4033	1240	1005
18607	\N	4033	1237	1005
18597	\N	4034	1235	1005
18598	\N	4034	382	1005
18599	\N	4034	383	1005
18602	\N	4034	1236	1005
18603	\N	4034	1243	1005
18604	\N	4034	381	1005
18605	\N	4034	1241	1005
18606	\N	4034	1242	1005
18608	\N	4034	1238	1005
18609	\N	4034	1239	1005
18610	\N	4034	1245	1005
18666	\N	4065	1256	1011
18667	\N	4065	1258	1011
18668	\N	4065	1259	1011
18669	\N	4065	1262	1011
18670	\N	4065	1257	1011
18671	\N	4065	385	1011
18673	\N	4065	1260	1011
18674	\N	4065	1261	1011
18675	\N	4065	1263	1011
18672	\N	4066	1264	1011
18676	\N	4066	1265	1011
18678	\N	4063	1258	1012
18677	\N	4061	1256	1012
18679	\N	4061	1259	1012
18680	\N	4061	1262	1012
18681	\N	4061	1257	1012
18682	\N	4061	385	1012
18683	\N	4061	1264	1012
18684	\N	4061	1260	1012
18685	\N	4061	1261	1012
18633	\N	4491	1248	1008
18635	\N	4491	1247	1008
18638	\N	4491	1249	1008
18639	\N	4491	1250	1008
18640	\N	4491	1251	1008
18641	\N	4491	1246	1008
18642	\N	4491	1254	1008
18643	\N	4491	1255	1008
18636	\N	4490	1252	1008
18637	\N	4490	1253	1008
18693	\N	\N	385	1013
18721	\N	4063	386	1016
18722	\N	4064	1277	1016
18723	\N	4061	1267	1016
18724	\N	4061	1268	1016
18725	\N	4061	1269	1016
18726	\N	4061	1271	1016
18727	\N	4063	1276	1016
18728	\N	4062	1273	1016
18729	\N	4063	1274	1016
18730	\N	4063	387	1016
18731	\N	4061	1266	1016
18732	\N	4061	1270	1016
18733	\N	4061	1272	1016
18734	\N	4063	1275	1016
18735	\N	4064	1278	1016
18736	\N	4065	386	1017
18737	\N	4065	1277	1017
18738	\N	4065	1267	1017
18739	\N	4065	1268	1017
18740	\N	4065	1269	1017
18741	\N	4065	1271	1017
18742	\N	4066	1276	1017
18743	\N	4066	1273	1017
18744	\N	4065	1274	1017
18745	\N	4065	387	1017
18746	\N	4065	1266	1017
18747	\N	4065	1270	1017
18748	\N	4065	1272	1017
18749	\N	4065	1275	1017
18750	\N	4065	1278	1017
18751	\N	4071	386	1018
18752	\N	4071	1277	1018
18753	\N	4070	1267	1018
18754	\N	4071	1268	1018
18755	\N	4070	1269	1018
18756	\N	4071	1271	1018
18757	\N	4070	1276	1018
18758	\N	4071	1273	1018
18759	\N	4070	1274	1018
18760	\N	4069	387	1018
18761	\N	4071	1266	1018
18762	\N	4071	1270	1018
18763	\N	4069	1272	1018
18764	\N	4070	1275	1018
18765	\N	4069	1278	1018
18766	\N	\N	386	1019
18775	\N	\N	387	1019
18781	\N	4080	386	1020
18782	\N	4080	1277	1020
18783	\N	4078	1267	1020
18784	\N	4078	1268	1020
18785	\N	4078	1269	1020
18786	\N	4080	1271	1020
18787	\N	4080	1276	1020
18788	\N	4080	1273	1020
18789	\N	4078	1274	1020
18790	\N	4078	387	1020
18791	\N	4080	1266	1020
18792	\N	4080	1270	1020
18793	\N	4078	1272	1020
18794	\N	4079	1275	1020
18795	\N	4078	1278	1020
18836	\N	4104	1290	1026
18837	\N	4102	1288	1026
18838	\N	4102	1289	1026
18839	\N	4104	1292	1026
18840	\N	4104	1293	1026
18841	\N	4104	1291	1026
18842	\N	4101	1287	1026
18843	\N	4104	1294	1026
18844	\N	4101	388	1026
18845	\N	4108	1290	1027
18846	\N	4105	1288	1027
18847	\N	4108	1289	1027
18848	\N	4106	1292	1027
18849	\N	4106	1293	1027
18850	\N	4105	1291	1027
18851	\N	4107	1287	1027
18852	\N	4106	1294	1027
18853	\N	\N	388	1027
18854	\N	4109	1290	1028
18855	\N	4110	1288	1028
18856	\N	4109	1289	1028
18857	\N	4112	1292	1028
18858	\N	4110	1293	1028
18859	\N	4110	1291	1028
18860	\N	4109	1287	1028
18861	\N	4109	1294	1028
18862	\N	4109	388	1028
18863	\N	4114	1290	1029
18864	\N	4116	1288	1029
18865	\N	4114	1289	1029
18866	\N	4116	1292	1029
18867	\N	4116	1293	1029
18868	\N	4116	1291	1029
18869	\N	4116	1287	1029
18870	\N	4116	1294	1029
18871	\N	4114	388	1029
18872	\N	4118	1290	1030
18873	\N	4118	1288	1030
18874	\N	4120	1289	1030
18875	\N	4119	1292	1030
18876	\N	4118	1293	1030
18877	\N	4118	1291	1030
18878	\N	4118	1287	1030
18879	\N	4118	1294	1030
18880	\N	4117	388	1030
18936	\N	\N	389	1035
18942	\N	\N	390	1035
18945	\N	\N	391	1035
18946	\N	4142	1311	1036
18947	\N	4141	397	1036
18948	\N	4142	395	1036
18949	\N	4141	1306	1036
18950	\N	4141	1308	1036
18951	\N	4141	1309	1036
18952	\N	4141	394	1036
18953	\N	4141	1305	1036
18954	\N	4141	1307	1036
18955	\N	4142	392	1036
18956	\N	4141	393	1036
18957	\N	4143	396	1036
18958	\N	4142	1312	1036
18959	\N	4141	1310	1036
18960	\N	4145	1311	1037
18961	\N	4147	397	1037
18962	\N	4147	395	1037
18963	\N	4146	1306	1037
18964	\N	4148	1308	1037
18965	\N	4147	1309	1037
18966	\N	\N	394	1037
18967	\N	4146	1305	1037
18968	\N	4146	1307	1037
18969	\N	\N	392	1037
18970	\N	\N	393	1037
18971	\N	4145	396	1037
18972	\N	4146	1312	1037
18973	\N	4147	1310	1037
18976	\N	\N	395	1038
18988	\N	4155	1311	1039
18989	\N	4156	397	1039
18990	\N	4155	395	1039
18991	\N	4155	1306	1039
18992	\N	4155	1308	1039
18993	\N	4155	1309	1039
18994	\N	\N	394	1039
18995	\N	4155	1305	1039
18996	\N	4155	1307	1039
18997	\N	4155	392	1039
18998	\N	4155	393	1039
18999	\N	\N	396	1039
19000	\N	4156	1312	1039
19001	\N	4155	1310	1039
19002	\N	4157	1311	1040
19003	\N	\N	397	1040
19004	\N	4157	395	1040
19005	\N	4158	1306	1040
19006	\N	4157	1308	1040
19007	\N	4157	1309	1040
19008	\N	4157	394	1040
19009	\N	4159	1305	1040
19010	\N	4159	1307	1040
19011	\N	4159	392	1040
19012	\N	4157	393	1040
19013	\N	4158	396	1040
19014	\N	4157	1312	1040
19015	\N	4157	1310	1040
19017	\N	\N	398	1041
19028	\N	\N	399	1041
19042	\N	\N	399	1042
19049	\N	\N	400	1043
19074	\N	\N	401	1045
19076	\N	\N	402	1045
19083	\N	\N	403	1045
19086	\N	4184	1324	1046
19087	\N	4182	1321	1046
19088	\N	4183	1322	1046
19089	\N	4184	1327	1046
19090	\N	4184	1328	1046
19091	\N	4183	1323	1046
19092	\N	4184	404	1046
19093	\N	4184	1325	1046
19094	\N	4184	1326	1046
19095	\N	4183	405	1046
19096	\N	4187	1324	1047
19097	\N	4187	1321	1047
19098	\N	4185	1322	1047
19099	\N	4187	1327	1047
19100	\N	4187	1328	1047
19101	\N	4187	1323	1047
19102	\N	\N	404	1047
19103	\N	4187	1325	1047
19104	\N	4187	1326	1047
19105	\N	4187	405	1047
19106	\N	4191	1324	1048
19107	\N	4191	1321	1048
19108	\N	4191	1322	1048
19109	\N	4191	1327	1048
19110	\N	4191	1328	1048
19111	\N	4191	1323	1048
19112	\N	4191	404	1048
19113	\N	4191	1325	1048
19114	\N	4190	1326	1048
19115	\N	4191	405	1048
19116	\N	4195	1324	1049
19117	\N	4194	1321	1049
19118	\N	4193	1322	1049
19119	\N	4196	1327	1049
19120	\N	4195	1328	1049
19121	\N	4195	1323	1049
19122	\N	4195	404	1049
19123	\N	4193	1325	1049
19124	\N	4193	1326	1049
19125	\N	4195	405	1049
19126	\N	4199	1324	1050
19127	\N	4200	1321	1050
19128	\N	4199	1322	1050
19129	\N	4200	1327	1050
19130	\N	4200	1328	1050
19131	\N	4199	1323	1050
19132	\N	4200	404	1050
19133	\N	4199	1325	1050
19134	\N	4200	1326	1050
19135	\N	\N	405	1050
19171	\N	\N	406	1055
19176	\N	4223	407	1056
19177	\N	4222	1337	1056
19178	\N	4222	1339	1056
19179	\N	4221	1336	1056
19180	\N	4222	1340	1056
19181	\N	4224	1341	1056
19182	\N	4222	1338	1056
19183	\N	4225	407	1057
19184	\N	4227	1337	1057
19185	\N	4226	1339	1057
19186	\N	4227	1336	1057
19187	\N	4226	1340	1057
19188	\N	4226	1341	1057
19189	\N	4227	1338	1057
19190	\N	4229	407	1058
19191	\N	4231	1337	1058
19192	\N	4231	1339	1058
19193	\N	4231	1336	1058
19194	\N	4229	1340	1058
19195	\N	4231	1341	1058
19196	\N	4229	1338	1058
19197	\N	4234	407	1059
19198	\N	4236	1337	1059
19199	\N	4236	1339	1059
19200	\N	4236	1336	1059
19201	\N	4235	1340	1059
19202	\N	4235	1341	1059
19203	\N	4236	1338	1059
19204	\N	\N	407	1060
19205	\N	4240	1337	1060
19206	\N	4239	1339	1060
19207	\N	4240	1336	1060
19208	\N	4240	1340	1060
19209	\N	4239	1341	1060
19210	\N	4240	1338	1060
19277	\N	\N	408	1063
19327	\N	\N	409	1065
19332	\N	\N	410	1065
19334	\N	\N	411	1065
19339	\N	\N	412	1065
19360	\N	\N	413	1066
19422	\N	\N	408	1068
19381	\N	4330	1350	1066
19356	\N	4331	409	1066
19362	\N	4331	1343	1066
19363	\N	4331	411	1066
19364	\N	4331	408	1066
19365	\N	4331	1354	1066
19368	\N	4331	412	1066
19369	\N	4331	420	1066
19379	\N	4331	1349	1066
19380	\N	4331	1355	1066
19384	\N	4331	1342	1066
19357	\N	4332	422	1066
19358	\N	4332	1353	1066
19359	\N	4332	414	1066
19361	\N	4332	410	1066
19366	\N	4332	1344	1066
19367	\N	4332	1345	1066
19370	\N	4332	1346	1066
19371	\N	4332	1347	1066
19372	\N	4332	416	1066
19373	\N	4332	1348	1066
19374	\N	4332	421	1066
19375	\N	4332	415	1066
19376	\N	4332	419	1066
19377	\N	4332	417	1066
19378	\N	4332	418	1066
19382	\N	4332	1351	1066
19383	\N	4332	1352	1066
19387	\N	4333	1353	1067
19388	\N	4333	414	1067
19389	\N	4333	413	1067
19390	\N	4333	410	1067
19393	\N	4333	408	1067
19394	\N	4333	1354	1067
19395	\N	4333	1344	1067
19396	\N	4333	1345	1067
19397	\N	4333	412	1067
19398	\N	4333	420	1067
19399	\N	4333	1346	1067
19400	\N	4333	1347	1067
19401	\N	4333	416	1067
19402	\N	4333	1348	1067
19403	\N	4333	421	1067
19404	\N	4333	415	1067
19405	\N	4333	419	1067
19406	\N	4333	417	1067
19407	\N	4333	418	1067
19410	\N	4333	1350	1067
19411	\N	4333	1351	1067
19413	\N	4333	1342	1067
19385	\N	4334	409	1067
19392	\N	4334	411	1067
19408	\N	4334	1349	1067
19409	\N	4334	1355	1067
19386	\N	4336	422	1067
19391	\N	4336	1343	1067
19412	\N	4335	1352	1067
19414	\N	4340	409	1068
19425	\N	4340	1345	1068
19419	\N	4339	410	1068
19415	\N	4338	422	1068
19416	\N	4338	1353	1068
19417	\N	4338	414	1068
19418	\N	4338	413	1068
19420	\N	4338	1343	1068
19421	\N	4338	411	1068
19423	\N	4338	1354	1068
19424	\N	4338	1344	1068
19504	\N	\N	414	1071
19520	\N	\N	415	1071
19533	\N	\N	414	1072
19535	\N	\N	410	1072
19546	\N	\N	416	1072
19551	\N	\N	417	1072
19610	\N	\N	418	1074
19622	\N	\N	410	1075
19639	\N	\N	418	1075
19666	\N	\N	419	1076
19712	\N	\N	408	1078
19716	\N	\N	412	1078
19717	\N	\N	420	1078
19720	\N	\N	416	1078
19722	\N	\N	421	1078
19724	\N	\N	419	1078
19726	\N	\N	418	1078
19740	\N	\N	411	1079
19755	\N	\N	418	1079
19763	\N	\N	422	1080
19765	\N	\N	414	1080
19774	\N	\N	412	1080
19778	\N	\N	416	1080
19780	\N	\N	421	1080
19782	\N	\N	419	1080
19784	\N	\N	418	1080
19791	\N	4321	1356	1081
19792	\N	4321	1357	1081
19793	\N	4324	1372	1081
19794	\N	4321	1358	1081
19795	\N	4322	424	1081
19796	\N	4321	429	1081
19797	\N	4321	1359	1081
19798	\N	4321	1360	1081
19799	\N	4321	1361	1081
19800	\N	4321	430	1081
19801	\N	4321	1362	1081
19802	\N	4321	1363	1081
19803	\N	4321	1364	1081
19804	\N	4321	423	1081
19805	\N	4321	426	1081
19806	\N	4321	431	1081
19807	\N	4321	1365	1081
19808	\N	4321	1366	1081
19809	\N	4321	432	1081
19810	\N	4321	1367	1081
19811	\N	4321	1368	1081
19812	\N	4321	425	1081
19813	\N	4321	433	1081
19814	\N	4321	1369	1081
19815	\N	4321	1370	1081
19816	\N	4321	428	1081
19817	\N	4321	1371	1081
19818	\N	4321	427	1081
19819	\N	4325	1356	1082
19820	\N	4327	1357	1082
19821	\N	4325	1372	1082
19822	\N	4325	1358	1082
19823	\N	4327	424	1082
19824	\N	4327	429	1082
19825	\N	4327	1359	1082
19826	\N	4327	1360	1082
19827	\N	4326	1361	1082
19828	\N	4327	430	1082
19829	\N	4326	1362	1082
19830	\N	4328	1363	1082
19831	\N	4327	1364	1082
19832	\N	\N	423	1082
19833	\N	4327	426	1082
19834	\N	4326	431	1082
19835	\N	4325	1365	1082
19836	\N	4327	1366	1082
19837	\N	4327	432	1082
19838	\N	4327	1367	1082
19839	\N	4325	1368	1082
19840	\N	4327	425	1082
19841	\N	4327	433	1082
19842	\N	4327	1369	1082
19843	\N	4327	1370	1082
19844	\N	4326	428	1082
19845	\N	4327	1371	1082
19846	\N	4327	427	1082
19847	\N	4332	1356	1083
19848	\N	4332	1357	1083
19849	\N	4331	1372	1083
19850	\N	4331	1358	1083
19851	\N	\N	424	1083
19852	\N	4332	429	1083
19853	\N	4332	1359	1083
19854	\N	4332	1360	1083
19855	\N	4332	1361	1083
19856	\N	4331	430	1083
19857	\N	4332	1362	1083
19858	\N	4331	1363	1083
19859	\N	4332	1364	1083
19860	\N	4332	423	1083
19861	\N	4331	426	1083
19862	\N	4332	431	1083
19863	\N	4331	1365	1083
19864	\N	4331	1366	1083
19865	\N	4329	432	1083
19866	\N	4332	1367	1083
19867	\N	4330	1368	1083
19868	\N	4332	425	1083
19869	\N	4329	433	1083
19870	\N	4329	1369	1083
19871	\N	4332	1370	1083
19872	\N	4329	428	1083
19873	\N	4332	1371	1083
19874	\N	4332	427	1083
19875	\N	4333	1356	1084
19876	\N	4334	1357	1084
19877	\N	4336	1372	1084
19878	\N	4333	1358	1084
19879	\N	4333	424	1084
19880	\N	4333	429	1084
19881	\N	4334	1359	1084
19882	\N	4333	1360	1084
19883	\N	4333	1361	1084
19884	\N	4334	430	1084
19885	\N	4333	1362	1084
19886	\N	4333	1363	1084
19887	\N	4333	1364	1084
19888	\N	4333	423	1084
19889	\N	4333	426	1084
19890	\N	4333	431	1084
19891	\N	4334	1365	1084
19892	\N	4333	1366	1084
19893	\N	4333	432	1084
19894	\N	4333	1367	1084
19895	\N	4333	1368	1084
19896	\N	\N	425	1084
19897	\N	4333	433	1084
19898	\N	4336	1369	1084
19899	\N	4333	1370	1084
19900	\N	4333	428	1084
19901	\N	4333	1371	1084
19902	\N	4333	427	1084
19903	\N	4339	1356	1085
19904	\N	4338	1357	1085
19905	\N	4340	1372	1085
19906	\N	4338	1358	1085
19907	\N	4339	424	1085
19908	\N	4338	429	1085
19909	\N	4339	1359	1085
19910	\N	4338	1360	1085
19911	\N	4339	1361	1085
19912	\N	4339	430	1085
19913	\N	4338	1362	1085
19914	\N	4340	1363	1085
19915	\N	4339	1364	1085
19916	\N	4339	423	1085
19917	\N	4340	426	1085
19918	\N	4338	431	1085
19919	\N	4340	1365	1085
19920	\N	4339	1366	1085
19921	\N	4338	432	1085
19922	\N	4337	1367	1085
19923	\N	4339	1368	1085
19924	\N	\N	425	1085
19925	\N	4338	433	1085
19926	\N	4338	1369	1085
19927	\N	4339	1370	1085
19928	\N	4339	428	1085
19929	\N	4338	1371	1085
19930	\N	4338	427	1085
19931	\N	4344	1356	1086
19932	\N	4341	1357	1086
19933	\N	4343	1372	1086
19934	\N	4342	1358	1086
19935	\N	4344	424	1086
19936	\N	4341	429	1086
19937	\N	4341	1359	1086
19938	\N	4341	1360	1086
19939	\N	4344	1361	1086
19940	\N	4344	430	1086
19941	\N	4341	1362	1086
19942	\N	4342	1363	1086
19943	\N	4344	1364	1086
19944	\N	4341	423	1086
19945	\N	\N	426	1086
19946	\N	4344	431	1086
19947	\N	4342	1365	1086
19948	\N	4342	1366	1086
19949	\N	4341	432	1086
19950	\N	4341	1367	1086
19951	\N	4341	1368	1086
19952	\N	4341	425	1086
19953	\N	4341	433	1086
19954	\N	4341	1369	1086
19955	\N	4341	1370	1086
19956	\N	4344	428	1086
19957	\N	4341	1371	1086
19958	\N	\N	427	1086
19959	\N	4348	1356	1087
19960	\N	4347	1357	1087
19961	\N	4345	1372	1087
19962	\N	4347	1358	1087
19963	\N	4348	424	1087
19964	\N	4347	429	1087
19965	\N	4348	1359	1087
19966	\N	4347	1360	1087
19967	\N	4347	1361	1087
19968	\N	4347	430	1087
19969	\N	4347	1362	1087
19970	\N	4345	1363	1087
19971	\N	4347	1364	1087
19972	\N	4347	423	1087
19973	\N	4348	426	1087
19974	\N	4348	431	1087
19975	\N	4348	1365	1087
19976	\N	4347	1366	1087
19977	\N	4348	432	1087
19978	\N	4348	1367	1087
19979	\N	4348	1368	1087
19980	\N	4347	425	1087
19981	\N	4347	433	1087
19982	\N	4347	1369	1087
19983	\N	4348	1370	1087
19984	\N	4347	428	1087
19985	\N	4347	1371	1087
19986	\N	4348	427	1087
19987	\N	4351	1356	1088
19988	\N	4351	1357	1088
19989	\N	4351	1372	1088
19990	\N	4351	1358	1088
19991	\N	4351	424	1088
19992	\N	4351	429	1088
19993	\N	4351	1359	1088
19994	\N	4351	1360	1088
19995	\N	4351	1361	1088
19996	\N	4351	430	1088
19997	\N	4351	1362	1088
19998	\N	4351	1363	1088
19999	\N	4351	1364	1088
20000	\N	4351	423	1088
20001	\N	4351	426	1088
20002	\N	4351	431	1088
20003	\N	4351	1365	1088
20004	\N	4351	1366	1088
20005	\N	4351	432	1088
20006	\N	4351	1367	1088
20007	\N	4351	1368	1088
20008	\N	4351	425	1088
20009	\N	4351	433	1088
20010	\N	4351	1369	1088
20011	\N	4351	1370	1088
20012	\N	4351	428	1088
20013	\N	4351	1371	1088
20014	\N	4351	427	1088
20015	\N	4355	1356	1089
20016	\N	4355	1357	1089
20017	\N	4355	1372	1089
20018	\N	4354	1358	1089
20019	\N	4353	424	1089
20020	\N	4353	429	1089
20021	\N	4353	1359	1089
20022	\N	4353	1360	1089
20023	\N	4355	1361	1089
20024	\N	4353	430	1089
20025	\N	4353	1362	1089
20026	\N	4354	1363	1089
20027	\N	4353	1364	1089
20028	\N	4353	423	1089
20029	\N	4356	426	1089
20030	\N	4353	431	1089
20031	\N	4353	1365	1089
20032	\N	4353	1366	1089
20033	\N	4353	432	1089
20034	\N	4353	1367	1089
20035	\N	4356	1368	1089
20036	\N	\N	425	1089
20037	\N	4353	433	1089
20038	\N	4353	1369	1089
20039	\N	4353	1370	1089
20040	\N	\N	428	1089
20041	\N	4353	1371	1089
20042	\N	4353	427	1089
20043	\N	4360	1356	1090
20044	\N	4360	1357	1090
20045	\N	4358	1372	1090
20046	\N	4359	1358	1090
20047	\N	4360	424	1090
20048	\N	\N	429	1090
20049	\N	4360	1359	1090
20050	\N	4360	1360	1090
20051	\N	4357	1361	1090
20052	\N	\N	430	1090
20053	\N	4359	1362	1090
20054	\N	4360	1363	1090
20055	\N	4360	1364	1090
20056	\N	\N	423	1090
20057	\N	4360	426	1090
20058	\N	\N	431	1090
20059	\N	4360	1365	1090
20060	\N	4360	1366	1090
20061	\N	\N	432	1090
20062	\N	4360	1367	1090
20063	\N	4357	1368	1090
20064	\N	\N	425	1090
20065	\N	\N	433	1090
20066	\N	4357	1369	1090
20067	\N	4360	1370	1090
20068	\N	\N	428	1090
20069	\N	4360	1371	1090
20070	\N	\N	427	1090
20071	\N	4362	1356	1091
20072	\N	4361	1357	1091
20073	\N	4364	1372	1091
20074	\N	4364	1358	1091
20075	\N	4364	424	1091
20076	\N	4361	429	1091
20077	\N	4364	1359	1091
20078	\N	4362	1360	1091
20079	\N	4362	1361	1091
20080	\N	4364	430	1091
20081	\N	4364	1362	1091
20082	\N	4361	1363	1091
20083	\N	4364	1364	1091
20084	\N	4364	423	1091
20085	\N	4364	426	1091
20086	\N	4361	431	1091
20087	\N	4363	1365	1091
20088	\N	4364	1366	1091
20089	\N	4364	432	1091
20090	\N	4364	1367	1091
20091	\N	4364	1368	1091
20092	\N	\N	425	1091
20093	\N	4364	433	1091
20094	\N	4364	1369	1091
20095	\N	4362	1370	1091
20096	\N	4364	428	1091
20097	\N	4364	1371	1091
20098	\N	4364	427	1091
20099	\N	4367	1356	1092
20100	\N	4367	1357	1092
20101	\N	4365	1372	1092
20102	\N	4367	1358	1092
20103	\N	4365	424	1092
20104	\N	4367	429	1092
20105	\N	4367	1359	1092
20106	\N	4367	1360	1092
20107	\N	4367	1361	1092
20108	\N	4367	430	1092
20109	\N	4367	1362	1092
20110	\N	4367	1363	1092
20111	\N	4367	1364	1092
20112	\N	4367	423	1092
20113	\N	4367	426	1092
20114	\N	4367	431	1092
20115	\N	4367	1365	1092
20116	\N	4367	1366	1092
20117	\N	4367	432	1092
20118	\N	4367	1367	1092
20119	\N	4367	1368	1092
20120	\N	\N	425	1092
20121	\N	4367	433	1092
20122	\N	4367	1369	1092
20123	\N	4365	1370	1092
20124	\N	4367	428	1092
20125	\N	4367	1371	1092
20126	\N	\N	427	1092
20127	\N	4371	1356	1093
20128	\N	4372	1357	1093
20129	\N	4369	1372	1093
20130	\N	4371	1358	1093
20131	\N	4372	424	1093
20132	\N	4372	429	1093
20133	\N	4371	1359	1093
20134	\N	4371	1360	1093
20135	\N	4371	1361	1093
20136	\N	4372	430	1093
20137	\N	4372	1362	1093
20138	\N	4370	1363	1093
20139	\N	4371	1364	1093
20140	\N	4371	423	1093
20141	\N	4371	426	1093
20142	\N	4370	431	1093
20143	\N	4370	1365	1093
20144	\N	4372	1366	1093
20145	\N	4371	432	1093
20146	\N	4371	1367	1093
20147	\N	4371	1368	1093
20148	\N	4371	425	1093
20149	\N	\N	433	1093
20150	\N	4369	1369	1093
20151	\N	4371	1370	1093
20152	\N	4369	428	1093
20153	\N	4371	1371	1093
20154	\N	4371	427	1093
20155	\N	4373	1356	1094
20156	\N	4375	1357	1094
20157	\N	4376	1372	1094
20158	\N	4375	1358	1094
20159	\N	4373	424	1094
20160	\N	4373	429	1094
20161	\N	4373	1359	1094
20162	\N	4373	1360	1094
20163	\N	4373	1361	1094
20164	\N	4373	430	1094
20165	\N	4373	1362	1094
20166	\N	4373	1363	1094
20167	\N	4373	1364	1094
20168	\N	4373	423	1094
20169	\N	4373	426	1094
20170	\N	4373	431	1094
20171	\N	4375	1365	1094
20172	\N	4373	1366	1094
20173	\N	4374	432	1094
20174	\N	4373	1367	1094
20175	\N	4375	1368	1094
20176	\N	4373	425	1094
20177	\N	4373	433	1094
20178	\N	4373	1369	1094
20179	\N	4375	1370	1094
20180	\N	4373	428	1094
20181	\N	4373	1371	1094
20182	\N	4373	427	1094
20183	\N	4377	1356	1095
20184	\N	4377	1357	1095
20185	\N	4378	1372	1095
20186	\N	4380	1358	1095
20187	\N	4377	424	1095
20188	\N	4377	429	1095
20189	\N	4377	1359	1095
20190	\N	4380	1360	1095
20191	\N	4377	1361	1095
20192	\N	4380	430	1095
20193	\N	4380	1362	1095
20194	\N	4377	1363	1095
20195	\N	4379	1364	1095
20196	\N	4377	423	1095
20197	\N	\N	426	1095
20198	\N	4378	431	1095
20199	\N	4377	1365	1095
20200	\N	4377	1366	1095
20201	\N	4377	432	1095
20202	\N	4377	1367	1095
20203	\N	4380	1368	1095
20204	\N	4377	425	1095
20205	\N	4377	433	1095
20206	\N	4377	1369	1095
20207	\N	4377	1370	1095
20208	\N	4377	428	1095
20209	\N	4377	1371	1095
20210	\N	4377	427	1095
20211	\N	4382	1356	1096
20212	\N	4382	1357	1096
20213	\N	4382	1372	1096
20214	\N	4381	1358	1096
20215	\N	4383	424	1096
20216	\N	4382	429	1096
20217	\N	4381	1359	1096
20218	\N	4381	1360	1096
20219	\N	4382	1361	1096
20220	\N	4384	430	1096
20221	\N	4384	1362	1096
20222	\N	4384	1363	1096
20223	\N	4381	1364	1096
20224	\N	4384	423	1096
20225	\N	4382	426	1096
20226	\N	4383	431	1096
20227	\N	4382	1365	1096
20228	\N	4383	1366	1096
20229	\N	4384	432	1096
20230	\N	4382	1367	1096
20231	\N	4381	1368	1096
20232	\N	\N	425	1096
20233	\N	4383	433	1096
20234	\N	4384	1369	1096
20235	\N	4382	1370	1096
20236	\N	4382	428	1096
20237	\N	4381	1371	1096
20238	\N	4382	427	1096
20239	\N	4386	1356	1097
20240	\N	4386	1357	1097
20241	\N	4388	1372	1097
20242	\N	4388	1358	1097
20243	\N	4387	424	1097
20244	\N	4386	429	1097
20245	\N	4388	1359	1097
20246	\N	4386	1360	1097
20247	\N	4385	1361	1097
20248	\N	4386	430	1097
20249	\N	4386	1362	1097
20250	\N	4388	1363	1097
20251	\N	4388	1364	1097
20252	\N	4386	423	1097
20253	\N	4385	426	1097
20254	\N	4388	431	1097
20255	\N	4388	1365	1097
20256	\N	4385	1366	1097
20257	\N	4385	432	1097
20258	\N	4386	1367	1097
20259	\N	4385	1368	1097
20260	\N	4386	425	1097
20261	\N	\N	433	1097
20262	\N	4386	1369	1097
20263	\N	4386	1370	1097
20264	\N	4386	428	1097
20265	\N	4386	1371	1097
20266	\N	\N	427	1097
20267	\N	4391	1356	1098
20268	\N	4391	1357	1098
20269	\N	4391	1372	1098
20270	\N	4391	1358	1098
20271	\N	4392	424	1098
20272	\N	4391	429	1098
20273	\N	4392	1359	1098
20274	\N	4391	1360	1098
20275	\N	4391	1361	1098
20276	\N	4390	430	1098
20277	\N	4391	1362	1098
20278	\N	4390	1363	1098
20279	\N	4391	1364	1098
20280	\N	4392	423	1098
20281	\N	\N	426	1098
20282	\N	4392	431	1098
20283	\N	4392	1365	1098
20284	\N	4390	1366	1098
20285	\N	4392	432	1098
20286	\N	4391	1367	1098
20287	\N	4390	1368	1098
20288	\N	4392	425	1098
20289	\N	4391	433	1098
20290	\N	4391	1369	1098
20291	\N	4391	1370	1098
20292	\N	4391	428	1098
20293	\N	4392	1371	1098
20294	\N	4391	427	1098
20295	\N	4394	1356	1099
20296	\N	4394	1357	1099
20297	\N	4394	1372	1099
20298	\N	4394	1358	1099
20299	\N	\N	424	1099
20300	\N	4394	429	1099
20301	\N	4394	1359	1099
20302	\N	4394	1360	1099
20303	\N	4395	1361	1099
20304	\N	4394	430	1099
20305	\N	4394	1362	1099
20306	\N	4394	1363	1099
20307	\N	4394	1364	1099
20308	\N	4394	423	1099
20309	\N	4394	426	1099
20310	\N	4396	431	1099
20311	\N	4394	1365	1099
20312	\N	4394	1366	1099
20313	\N	4394	432	1099
20314	\N	4394	1367	1099
20315	\N	4394	1368	1099
20316	\N	4394	425	1099
20317	\N	4394	433	1099
20318	\N	4396	1369	1099
20319	\N	4394	1370	1099
20320	\N	4394	428	1099
20321	\N	4394	1371	1099
20322	\N	4394	427	1099
20323	\N	4399	1356	1100
20324	\N	4399	1357	1100
20325	\N	4400	1372	1100
20326	\N	4400	1358	1100
20327	\N	4400	424	1100
20328	\N	4399	429	1100
20329	\N	4400	1359	1100
20330	\N	4398	1360	1100
20331	\N	4397	1361	1100
20332	\N	4400	430	1100
20333	\N	4399	1362	1100
20334	\N	4399	1363	1100
20335	\N	4400	1364	1100
20336	\N	\N	423	1100
20337	\N	4397	426	1100
20338	\N	\N	431	1100
20339	\N	4399	1365	1100
20340	\N	4399	1366	1100
20341	\N	4399	432	1100
20342	\N	4399	1367	1100
20343	\N	4400	1368	1100
20344	\N	4397	425	1100
20345	\N	4399	433	1100
20346	\N	4400	1369	1100
20347	\N	4399	1370	1100
20348	\N	4400	428	1100
20349	\N	4400	1371	1100
20350	\N	4399	427	1100
20362	\N	\N	435	1102
20364	\N	\N	434	1102
20365	\N	\N	436	1102
20401	\N	\N	437	1106
20404	\N	\N	434	1106
20432	\N	\N	435	1109
20434	\N	\N	434	1109
20445	\N	\N	436	1110
20451	\N	\N	437	1111
20452	\N	\N	435	1111
20454	\N	\N	434	1111
20461	\N	\N	437	1112
20469	\N	\N	438	1112
20490	\N	\N	439	1114
20493	\N	\N	440	1115
20500	\N	\N	439	1115
20521	\N	\N	437	1118
20522	\N	\N	435	1118
20525	\N	\N	436	1118
20530	\N	\N	439	1118
20477	\N	4539	1373	1113
20481	\N	4542	437	1114
20482	\N	4542	435	1114
20483	\N	4542	440	1114
20484	\N	4542	434	1114
20485	\N	4542	436	1114
20486	\N	4542	1375	1114
20487	\N	4542	1373	1114
20488	\N	4541	1374	1114
20489	\N	4541	438	1114
20492	\N	4547	435	1115
20495	\N	4547	436	1115
20499	\N	4547	438	1115
20494	\N	4546	434	1115
20496	\N	4545	1375	1115
20491	\N	4548	437	1115
20497	\N	4548	1373	1115
20498	\N	4548	1374	1115
20501	\N	4550	437	1116
20502	\N	4550	435	1116
20503	\N	4550	440	1116
20504	\N	4550	434	1116
20505	\N	4550	436	1116
20506	\N	4550	1375	1116
20507	\N	4550	1373	1116
20508	\N	4550	1374	1116
20509	\N	4550	438	1116
20510	\N	4550	439	1116
20512	\N	4554	435	1117
20513	\N	4554	440	1117
20514	\N	4554	434	1117
20516	\N	4554	1375	1117
20518	\N	4554	1374	1117
20519	\N	4554	438	1117
20511	\N	4553	437	1117
20515	\N	4553	436	1117
20517	\N	4553	1373	1117
20520	\N	4553	439	1117
20523	\N	4559	440	1118
20524	\N	4559	434	1118
20526	\N	4559	1375	1118
20527	\N	4559	1373	1118
20528	\N	4559	1374	1118
20529	\N	4559	438	1118
20532	\N	4524	435	1119
20535	\N	4524	436	1119
20531	\N	4521	437	1119
20533	\N	4521	440	1119
20534	\N	4521	434	1119
20544	\N	\N	434	1120
20550	\N	\N	439	1120
20551	\N	4482	1378	1121
20552	\N	4481	446	1121
20553	\N	4481	443	1121
20554	\N	4484	447	1121
20555	\N	4484	441	1121
20556	\N	4481	1376	1121
20557	\N	4481	1377	1121
20558	\N	4481	445	1121
20559	\N	4481	444	1121
20560	\N	4481	442	1121
20571	\N	4491	1378	1123
20572	\N	4491	446	1123
20573	\N	4491	443	1123
20574	\N	4491	447	1123
20575	\N	\N	441	1123
20576	\N	4491	1376	1123
20577	\N	4491	1377	1123
20578	\N	4491	445	1123
20579	\N	4491	444	1123
20580	\N	4492	442	1123
20581	\N	4494	1378	1124
20582	\N	4493	446	1124
20583	\N	4493	443	1124
20584	\N	4496	447	1124
20585	\N	4495	441	1124
20586	\N	4493	1376	1124
20587	\N	4493	1377	1124
20588	\N	4493	445	1124
20589	\N	4493	444	1124
20590	\N	\N	442	1124
20591	\N	4498	1378	1125
20592	\N	4498	446	1125
20593	\N	4498	443	1125
20594	\N	4498	447	1125
20595	\N	4498	441	1125
20596	\N	4498	1376	1125
20597	\N	4497	1377	1125
20598	\N	4498	445	1125
20599	\N	4498	444	1125
20600	\N	\N	442	1125
20601	\N	4504	1378	1126
20602	\N	4504	446	1126
20603	\N	4502	443	1126
20604	\N	4501	447	1126
20605	\N	4502	441	1126
20606	\N	4504	1376	1126
20607	\N	4502	1377	1126
20608	\N	4504	445	1126
20609	\N	4504	444	1126
20610	\N	4502	442	1126
20611	\N	4506	1378	1127
20612	\N	4505	446	1127
20613	\N	\N	443	1127
20614	\N	4506	447	1127
20615	\N	4505	441	1127
20616	\N	4506	1376	1127
20617	\N	4505	1377	1127
20618	\N	4505	445	1127
20619	\N	\N	444	1127
20620	\N	4507	442	1127
20621	\N	4511	1378	1128
20622	\N	4512	446	1128
20623	\N	4512	443	1128
20624	\N	4510	447	1128
20625	\N	4512	441	1128
20626	\N	4512	1376	1128
20627	\N	4512	1377	1128
20628	\N	4512	445	1128
20629	\N	4512	444	1128
20630	\N	4512	442	1128
20631	\N	4515	1378	1129
20632	\N	4513	446	1129
20633	\N	4514	443	1129
20634	\N	4513	447	1129
20635	\N	4515	441	1129
20636	\N	4514	1376	1129
20637	\N	4514	1377	1129
20638	\N	4515	445	1129
20639	\N	4514	444	1129
20640	\N	\N	442	1129
20641	\N	4518	1378	1130
20642	\N	4519	446	1130
20643	\N	4519	443	1130
20644	\N	4519	447	1130
20645	\N	4518	441	1130
20646	\N	4519	1376	1130
20647	\N	4519	1377	1130
20648	\N	4519	445	1130
20649	\N	4519	444	1130
20650	\N	4519	442	1130
20651	\N	4521	1378	1131
20652	\N	4521	446	1131
20653	\N	4524	443	1131
20654	\N	4521	447	1131
20655	\N	4524	441	1131
20656	\N	4524	1376	1131
20657	\N	4522	1377	1131
20658	\N	4521	445	1131
20659	\N	4524	444	1131
20660	\N	4524	442	1131
20661	\N	4528	1378	1132
20662	\N	4528	446	1132
20663	\N	4527	443	1132
20664	\N	4527	447	1132
20665	\N	4527	441	1132
20666	\N	4527	1376	1132
20667	\N	4527	1377	1132
20668	\N	\N	445	1132
20669	\N	4527	444	1132
20670	\N	\N	442	1132
20671	\N	4530	1378	1133
20672	\N	4530	446	1133
20673	\N	4532	443	1133
20674	\N	4530	447	1133
20675	\N	4532	441	1133
20676	\N	4532	1376	1133
20677	\N	4532	1377	1133
20678	\N	4532	445	1133
20679	\N	4532	444	1133
20680	\N	4532	442	1133
20681	\N	4533	1378	1134
20682	\N	4533	446	1134
20683	\N	\N	443	1134
20684	\N	4534	447	1134
20685	\N	4535	441	1134
20686	\N	4534	1376	1134
20687	\N	4533	1377	1134
20688	\N	4534	445	1134
20689	\N	4536	444	1134
20690	\N	\N	442	1134
20691	\N	4537	1378	1135
20692	\N	4538	446	1135
20693	\N	4537	443	1135
20694	\N	4537	447	1135
20695	\N	4537	441	1135
20696	\N	4537	1376	1135
20697	\N	4537	1377	1135
20698	\N	4538	445	1135
20699	\N	4537	444	1135
20700	\N	4537	442	1135
20701	\N	4542	1378	1136
20702	\N	4542	446	1136
20703	\N	4542	443	1136
20704	\N	4541	447	1136
20705	\N	4541	441	1136
20706	\N	4542	1376	1136
20707	\N	4541	1377	1136
20708	\N	\N	445	1136
20709	\N	4542	444	1136
20710	\N	\N	442	1136
20711	\N	4548	1378	1137
20712	\N	\N	446	1137
20713	\N	4547	443	1137
20714	\N	4546	447	1137
20715	\N	\N	441	1137
20716	\N	4547	1376	1137
20717	\N	4547	1377	1137
20718	\N	4548	445	1137
20719	\N	4546	444	1137
20720	\N	\N	442	1137
20721	\N	4550	1378	1138
20722	\N	4550	446	1138
20723	\N	\N	443	1138
20724	\N	4550	447	1138
20725	\N	4550	441	1138
20726	\N	4550	1376	1138
20727	\N	4550	1377	1138
20728	\N	4550	445	1138
20729	\N	4550	444	1138
20730	\N	4551	442	1138
20731	\N	4556	1378	1139
20732	\N	4553	446	1139
20733	\N	\N	443	1139
20734	\N	4554	447	1139
20735	\N	4554	441	1139
20736	\N	4553	1376	1139
20737	\N	4554	1377	1139
20738	\N	\N	445	1139
20739	\N	4556	444	1139
20740	\N	4554	442	1139
20741	\N	4559	1378	1140
20742	\N	4559	446	1140
20743	\N	\N	443	1140
20744	\N	\N	447	1140
20745	\N	\N	441	1140
20746	\N	4559	1376	1140
20747	\N	4559	1377	1140
20748	\N	4558	445	1140
20749	\N	\N	444	1140
20750	\N	\N	442	1140
20876	\N	4622	1407	1156
20877	\N	4621	1404	1156
20878	\N	4622	1413	1156
20879	\N	4622	1405	1156
20880	\N	4622	1406	1156
20881	\N	4622	1408	1156
20882	\N	4622	1409	1156
20883	\N	4622	1410	1156
20884	\N	4622	1411	1156
20885	\N	4622	1412	1156
20886	\N	4626	1407	1157
20887	\N	4627	1404	1157
20888	\N	4627	1413	1157
20889	\N	4627	1405	1157
20890	\N	4627	1406	1157
20891	\N	4627	1408	1157
20892	\N	4627	1409	1157
20893	\N	4627	1410	1157
20894	\N	4627	1411	1157
20895	\N	4627	1412	1157
20896	\N	4632	1407	1158
20897	\N	4629	1404	1158
20898	\N	4632	1413	1158
20899	\N	4632	1405	1158
20900	\N	4632	1406	1158
20901	\N	4632	1408	1158
20902	\N	4632	1409	1158
20903	\N	4632	1410	1158
20904	\N	4632	1411	1158
20905	\N	4632	1412	1158
20906	\N	4635	1407	1159
20907	\N	4633	1404	1159
20908	\N	4633	1413	1159
20909	\N	4633	1405	1159
20910	\N	4633	1406	1159
20911	\N	4636	1408	1159
20912	\N	4633	1409	1159
20913	\N	4633	1410	1159
20914	\N	4633	1411	1159
20915	\N	4633	1412	1159
20916	\N	4638	1407	1160
20917	\N	4638	1404	1160
20918	\N	4640	1413	1160
20919	\N	4640	1405	1160
20920	\N	4638	1406	1160
20921	\N	4638	1408	1160
20922	\N	4639	1409	1160
20923	\N	4639	1410	1160
20924	\N	4638	1411	1160
20925	\N	4638	1412	1160
20956	\N	\N	448	1164
20964	\N	\N	449	1164
21014	\N	\N	450	1172
21030	\N	\N	451	1174
21041	\N	4703	452	1176
21042	\N	4701	1433	1176
21043	\N	4701	1434	1176
21044	\N	4702	1437	1176
21045	\N	4701	1435	1176
21046	\N	4701	1436	1176
21047	\N	4703	1438	1176
21048	\N	4708	452	1177
21049	\N	4708	1433	1177
21050	\N	4708	1434	1177
21051	\N	4708	1437	1177
21052	\N	4705	1435	1177
21053	\N	4708	1436	1177
21054	\N	4708	1438	1177
21055	\N	4710	452	1178
21056	\N	4712	1433	1178
21057	\N	4711	1434	1178
21058	\N	4710	1437	1178
21059	\N	4711	1435	1178
21060	\N	4711	1436	1178
21061	\N	4710	1438	1178
21062	\N	4715	452	1179
21063	\N	4714	1433	1179
21064	\N	4715	1434	1179
21065	\N	4715	1437	1179
21066	\N	4715	1435	1179
21067	\N	4715	1436	1179
21068	\N	4715	1438	1179
21069	\N	\N	452	1180
21070	\N	4720	1433	1180
21071	\N	4718	1434	1180
21072	\N	4720	1437	1180
21073	\N	4718	1435	1180
21074	\N	4718	1436	1180
21075	\N	4719	1438	1180
21009	\N	4719	451	1171
21010	\N	4719	1430	1171
21006	\N	4720	1431	1171
21007	\N	4720	450	1171
21011	\N	4720	1432	1171
21012	\N	4717	1428	1171
21008	\N	4718	1429	1171
21017	\N	4703	1430	1172
21018	\N	4703	1432	1172
21019	\N	4702	1428	1172
21013	\N	4701	1431	1172
21015	\N	4701	1429	1172
21016	\N	4701	451	1172
21020	\N	4708	1431	1173
21021	\N	4708	450	1173
21022	\N	4708	1429	1173
21023	\N	4708	451	1173
21024	\N	4708	1430	1173
21025	\N	4708	1432	1173
21026	\N	4708	1428	1173
21028	\N	4711	450	1174
21031	\N	4711	1430	1174
21032	\N	4711	1432	1174
21027	\N	4710	1431	1174
21029	\N	4710	1429	1174
21033	\N	4710	1428	1174
21037	\N	4714	451	1175
21034	\N	4716	1431	1175
21035	\N	4715	450	1175
21038	\N	4715	1430	1175
21039	\N	4715	1432	1175
21040	\N	4715	1428	1175
21036	\N	4713	1429	1175
21083	\N	4752	1453	1181
21084	\N	4752	1452	1181
21077	\N	4750	1442	1181
21082	\N	4750	1446	1181
21085	\N	4750	1443	1181
21086	\N	4750	1444	1181
21089	\N	4750	1445	1181
21076	\N	4751	1447	1181
21081	\N	4751	1450	1181
21087	\N	4751	1448	1181
21088	\N	4751	1449	1181
21090	\N	4751	1451	1181
21078	\N	4749	1439	1181
21079	\N	4749	1440	1181
21080	\N	4749	1441	1181
21151	\N	4741	1455	1186
21152	\N	4741	1457	1186
21153	\N	4741	1458	1186
21154	\N	4741	1459	1186
21155	\N	4741	1463	1186
21156	\N	4741	1467	1186
21157	\N	4741	1454	1186
21158	\N	4743	1468	1186
21159	\N	4741	1456	1186
21160	\N	4741	1460	1186
21161	\N	4741	1461	1186
21162	\N	4741	1462	1186
21163	\N	4743	1469	1186
21164	\N	4744	1470	1186
21165	\N	4741	1464	1186
21166	\N	4741	1465	1186
21167	\N	4741	1466	1186
21168	\N	4746	1455	1187
21169	\N	4746	1457	1187
21170	\N	4746	1458	1187
21171	\N	4746	1459	1187
21172	\N	4748	1463	1187
21173	\N	4746	1467	1187
21174	\N	4745	1454	1187
21175	\N	4748	1468	1187
21176	\N	4746	1456	1187
21177	\N	4746	1460	1187
21178	\N	4746	1461	1187
21179	\N	4746	1462	1187
21180	\N	4748	1469	1187
21181	\N	4746	1470	1187
21182	\N	4746	1464	1187
21183	\N	4746	1465	1187
21184	\N	4746	1466	1187
21185	\N	4751	1455	1188
21186	\N	4751	1457	1188
21187	\N	4751	1458	1188
21188	\N	4749	1459	1188
21189	\N	4751	1463	1188
21190	\N	4749	1467	1188
21191	\N	4752	1454	1188
21192	\N	4749	1468	1188
21193	\N	4752	1456	1188
21194	\N	4751	1460	1188
21195	\N	4751	1461	1188
21196	\N	4750	1462	1188
21197	\N	4749	1469	1188
21198	\N	4751	1470	1188
21199	\N	4751	1464	1188
21200	\N	4749	1465	1188
21201	\N	4751	1466	1188
21202	\N	4753	1455	1189
21203	\N	4755	1457	1189
21204	\N	4756	1458	1189
21205	\N	4756	1459	1189
21206	\N	4756	1463	1189
21207	\N	4756	1467	1189
21208	\N	4756	1454	1189
21209	\N	4756	1468	1189
21210	\N	4756	1456	1189
21211	\N	4756	1460	1189
21212	\N	4756	1461	1189
21213	\N	4753	1462	1189
21214	\N	4756	1469	1189
21215	\N	4756	1470	1189
21216	\N	4756	1464	1189
21217	\N	4756	1465	1189
21218	\N	4756	1466	1189
21219	\N	4758	1455	1190
21220	\N	4758	1457	1190
21221	\N	4758	1458	1190
21222	\N	4758	1459	1190
21223	\N	4758	1463	1190
21224	\N	4758	1467	1190
21225	\N	4758	1454	1190
21226	\N	4758	1468	1190
21227	\N	4758	1456	1190
21228	\N	4758	1460	1190
21229	\N	4758	1461	1190
21230	\N	4758	1462	1190
21231	\N	4758	1469	1190
21232	\N	4758	1470	1190
21233	\N	4758	1464	1190
21234	\N	4758	1465	1190
21235	\N	4758	1466	1190
21243	\N	\N	453	1191
21305	\N	\N	454	1195
21311	\N	4783	455	1196
21312	\N	4783	456	1196
21313	\N	4783	1485	1196
21314	\N	4783	1486	1196
21315	\N	4783	457	1196
21316	\N	4783	1488	1196
21317	\N	4783	1489	1196
21318	\N	4783	1493	1196
21319	\N	4783	1494	1196
21320	\N	4783	1487	1196
21321	\N	4782	1484	1196
21322	\N	4783	1490	1196
21323	\N	4783	1491	1196
21324	\N	4783	1492	1196
21325	\N	4785	455	1197
21326	\N	4785	456	1197
21327	\N	4785	1485	1197
21328	\N	4785	1486	1197
21329	\N	4785	457	1197
21330	\N	4785	1488	1197
21331	\N	4785	1489	1197
21332	\N	4785	1493	1197
21333	\N	4785	1494	1197
21334	\N	4785	1487	1197
21335	\N	4785	1484	1197
21336	\N	4785	1490	1197
21337	\N	4785	1491	1197
21338	\N	4785	1492	1197
21339	\N	\N	455	1198
21340	\N	\N	456	1198
21341	\N	4789	1485	1198
21342	\N	4789	1486	1198
21343	\N	\N	457	1198
21344	\N	4789	1488	1198
21345	\N	4791	1489	1198
21346	\N	4789	1493	1198
21347	\N	4789	1494	1198
21348	\N	4790	1487	1198
21349	\N	4789	1484	1198
21350	\N	4792	1490	1198
21351	\N	4789	1491	1198
21352	\N	4789	1492	1198
21353	\N	4796	455	1199
21354	\N	4796	456	1199
21355	\N	4796	1485	1199
21356	\N	4796	1486	1199
21357	\N	4796	457	1199
21358	\N	4796	1488	1199
21359	\N	4796	1489	1199
21360	\N	4796	1493	1199
21361	\N	4796	1494	1199
21362	\N	4796	1487	1199
21363	\N	4796	1484	1199
21364	\N	4796	1490	1199
21365	\N	4796	1491	1199
21366	\N	4796	1492	1199
21367	\N	4799	455	1200
21368	\N	4799	456	1200
21369	\N	4798	1485	1200
21370	\N	4797	1486	1200
21371	\N	4799	457	1200
21372	\N	4799	1488	1200
21373	\N	4797	1489	1200
21374	\N	4798	1493	1200
21375	\N	4799	1494	1200
21376	\N	4799	1487	1200
21377	\N	4798	1484	1200
21378	\N	4799	1490	1200
21379	\N	4798	1491	1200
21380	\N	4797	1492	1200
21456	\N	4821	1512	1206
21457	\N	4821	1511	1206
21458	\N	4823	1515	1206
21459	\N	4823	1516	1206
21460	\N	4823	1519	1206
21461	\N	4823	1520	1206
21462	\N	4823	1521	1206
21463	\N	4823	1522	1206
21464	\N	4823	1523	1206
21465	\N	4821	1510	1206
21466	\N	4824	1524	1206
21467	\N	4823	1517	1206
21468	\N	4823	1518	1206
21469	\N	4821	1513	1206
21470	\N	4822	1514	1206
21471	\N	4826	1512	1207
21472	\N	4826	1511	1207
21473	\N	4826	1515	1207
21474	\N	4826	1516	1207
21475	\N	4826	1519	1207
21476	\N	4826	1520	1207
21477	\N	4826	1521	1207
21478	\N	4828	1522	1207
21479	\N	4826	1523	1207
21480	\N	4826	1510	1207
21481	\N	4826	1524	1207
21482	\N	4826	1517	1207
21483	\N	4826	1518	1207
21484	\N	4826	1513	1207
21485	\N	4825	1514	1207
21486	\N	4829	1512	1208
21487	\N	4829	1511	1208
21488	\N	4829	1515	1208
21489	\N	4829	1516	1208
21490	\N	4829	1519	1208
21491	\N	4829	1520	1208
21492	\N	4829	1521	1208
21493	\N	4829	1522	1208
21494	\N	4829	1523	1208
21495	\N	4829	1510	1208
21496	\N	4829	1524	1208
21497	\N	4829	1517	1208
21498	\N	4829	1518	1208
21499	\N	4829	1513	1208
21500	\N	4832	1514	1208
21501	\N	4835	1512	1209
21502	\N	4835	1511	1209
21503	\N	4835	1515	1209
21504	\N	4835	1516	1209
21505	\N	4835	1519	1209
21506	\N	4835	1520	1209
21507	\N	4835	1521	1209
21508	\N	4835	1522	1209
21509	\N	4835	1523	1209
21510	\N	4835	1510	1209
21511	\N	4834	1524	1209
21512	\N	4834	1517	1209
21513	\N	4835	1518	1209
21514	\N	4835	1513	1209
21515	\N	4835	1514	1209
21516	\N	4837	1512	1210
21517	\N	4837	1511	1210
21518	\N	4837	1515	1210
21519	\N	4837	1516	1210
21520	\N	4837	1519	1210
21521	\N	4837	1520	1210
21522	\N	4837	1521	1210
21523	\N	4838	1522	1210
21524	\N	4837	1523	1210
21525	\N	4840	1510	1210
21526	\N	4837	1524	1210
21527	\N	4837	1517	1210
21528	\N	4837	1518	1210
21529	\N	4837	1513	1210
21530	\N	4839	1514	1210
21591	\N	\N	458	1215
21592	\N	\N	459	1215
21606	\N	\N	460	1216
21607	\N	4862	1539	1216
21608	\N	4862	1540	1216
21609	\N	4862	1541	1216
21610	\N	4862	1542	1216
21611	\N	4861	1538	1216
21612	\N	4862	461	1216
21613	\N	4862	462	1216
21614	\N	4864	1548	1216
21615	\N	4862	1545	1216
21616	\N	4864	1547	1216
21617	\N	4862	1543	1216
21618	\N	4862	1544	1216
21619	\N	4863	1546	1216
21620	\N	\N	460	1217
21621	\N	4867	1539	1217
21622	\N	4867	1540	1217
21623	\N	4867	1541	1217
21624	\N	4867	1542	1217
21625	\N	4867	1538	1217
21626	\N	\N	461	1217
21627	\N	4867	462	1217
21628	\N	4867	1548	1217
21629	\N	4867	1545	1217
21630	\N	4867	1547	1217
21631	\N	4867	1543	1217
21632	\N	4867	1544	1217
21633	\N	4867	1546	1217
21634	\N	4872	460	1218
21635	\N	4872	1539	1218
21636	\N	4872	1540	1218
21637	\N	4872	1541	1218
21638	\N	4872	1542	1218
21639	\N	4872	1538	1218
21640	\N	4872	461	1218
21641	\N	4870	462	1218
21642	\N	4872	1548	1218
21643	\N	4872	1545	1218
21644	\N	4870	1547	1218
21645	\N	4871	1543	1218
21646	\N	4872	1544	1218
21647	\N	4871	1546	1218
21648	\N	4876	460	1219
21649	\N	4876	1539	1219
21650	\N	4875	1540	1219
21651	\N	4876	1541	1219
21652	\N	4876	1542	1219
21653	\N	4876	1538	1219
21654	\N	4876	461	1219
21655	\N	4876	462	1219
21656	\N	4876	1548	1219
21657	\N	4876	1545	1219
21658	\N	4875	1547	1219
21659	\N	4876	1543	1219
21660	\N	4876	1544	1219
21661	\N	4876	1546	1219
21662	\N	4877	460	1220
21663	\N	4877	1539	1220
21664	\N	4877	1540	1220
21665	\N	4877	1541	1220
21666	\N	4879	1542	1220
21667	\N	4877	1538	1220
21668	\N	4877	461	1220
21669	\N	\N	462	1220
21670	\N	4877	1548	1220
21671	\N	4877	1545	1220
21672	\N	4877	1547	1220
21673	\N	4877	1543	1220
21674	\N	4877	1544	1220
21675	\N	4877	1546	1220
21729	\N	\N	463	1225
21731	\N	4902	1563	1226
21732	\N	4902	1561	1226
21733	\N	4902	1562	1226
21734	\N	4902	1564	1226
21735	\N	\N	464	1226
21736	\N	4901	1559	1226
21737	\N	4903	1565	1226
21738	\N	4903	1566	1226
21739	\N	4903	1567	1226
21740	\N	4901	1560	1226
21741	\N	4904	1568	1226
21742	\N	4908	1563	1227
21743	\N	4907	1561	1227
21744	\N	4907	1562	1227
21745	\N	4908	1564	1227
21746	\N	4905	464	1227
21747	\N	4908	1559	1227
21748	\N	4908	1565	1227
21749	\N	4908	1566	1227
21750	\N	4907	1567	1227
21751	\N	4907	1560	1227
21752	\N	4908	1568	1227
21753	\N	4911	1563	1228
21754	\N	4911	1561	1228
21755	\N	4911	1562	1228
21756	\N	4911	1564	1228
21757	\N	4911	464	1228
21758	\N	4911	1559	1228
21759	\N	4911	1565	1228
21760	\N	4911	1566	1228
21761	\N	4911	1567	1228
21762	\N	4911	1560	1228
21763	\N	4911	1568	1228
21764	\N	4916	1563	1229
21765	\N	4916	1561	1229
21766	\N	4916	1562	1229
21767	\N	4916	1564	1229
21768	\N	4916	464	1229
21769	\N	4916	1559	1229
21770	\N	4915	1565	1229
21771	\N	4916	1566	1229
21772	\N	4916	1567	1229
21773	\N	4916	1560	1229
21774	\N	4916	1568	1229
21775	\N	4918	1563	1230
21776	\N	4917	1561	1230
21777	\N	4917	1562	1230
21778	\N	4917	1564	1230
21779	\N	4919	464	1230
21780	\N	4918	1559	1230
21781	\N	4917	1565	1230
21782	\N	4918	1566	1230
21783	\N	4917	1567	1230
21784	\N	4917	1560	1230
21785	\N	4918	1568	1230
21790	\N	\N	465	1231
21802	\N	\N	465	1232
21826	\N	\N	465	1234
21720	\N	4905	1550	1225
21728	\N	4905	1553	1225
21723	\N	4907	1555	1225
21724	\N	4907	1557	1225
21725	\N	4907	1558	1225
21788	\N	4955	1574	1231
21789	\N	4955	1575	1231
21791	\N	4955	1577	1231
21795	\N	4955	1576	1231
21796	\N	4955	1578	1231
21792	\N	4953	1570	1231
21794	\N	4953	1569	1231
21786	\N	4954	466	1231
21787	\N	4954	1571	1231
21793	\N	4954	1572	1231
21797	\N	4954	1573	1231
21798	\N	4957	466	1232
21799	\N	4957	1571	1232
21800	\N	4957	1574	1232
21803	\N	4957	1577	1232
21804	\N	4957	1570	1232
21805	\N	4957	1572	1232
21806	\N	4957	1569	1232
21808	\N	4957	1578	1232
21809	\N	4957	1573	1232
21807	\N	4959	1576	1232
21801	\N	4958	1575	1232
21821	\N	4941	1573	1233
21810	\N	4942	466	1233
21811	\N	4942	1571	1233
21812	\N	4942	1574	1233
21813	\N	4942	1575	1233
21814	\N	4942	465	1233
21815	\N	4942	1577	1233
21816	\N	4942	1570	1233
21817	\N	4942	1572	1233
21819	\N	4942	1576	1233
21820	\N	4942	1578	1233
21818	\N	4943	1569	1233
21823	\N	4946	1571	1234
21824	\N	4946	1574	1234
21825	\N	4946	1575	1234
21827	\N	4946	1577	1234
21828	\N	4946	1570	1234
21829	\N	4946	1572	1234
21834	\N	\N	466	1235
21838	\N	\N	465	1235
21846	\N	4942	1579	1236
21847	\N	4942	1580	1236
21848	\N	4942	1581	1236
21849	\N	4941	467	1236
21850	\N	4942	1582	1236
21851	\N	4942	1583	1236
21852	\N	4942	1584	1236
21853	\N	4942	468	1236
21854	\N	4943	469	1236
21855	\N	4942	470	1236
21856	\N	4942	471	1236
21857	\N	4942	1585	1236
21858	\N	4942	1586	1236
21859	\N	4946	1579	1237
21860	\N	4946	1580	1237
21861	\N	4945	1581	1237
21862	\N	\N	467	1237
21863	\N	4948	1582	1237
21864	\N	4946	1583	1237
21865	\N	4946	1584	1237
21866	\N	\N	468	1237
21867	\N	\N	469	1237
21868	\N	4948	470	1237
21869	\N	4946	471	1237
21870	\N	4946	1585	1237
21871	\N	4946	1586	1237
21872	\N	4951	1579	1238
21873	\N	4951	1580	1238
21874	\N	4952	1581	1238
21875	\N	4951	467	1238
21876	\N	4949	1582	1238
21877	\N	4951	1583	1238
21878	\N	4951	1584	1238
21879	\N	4952	468	1238
21880	\N	4950	469	1238
21881	\N	4951	470	1238
21882	\N	4952	471	1238
21883	\N	4951	1585	1238
21884	\N	4949	1586	1238
21885	\N	4955	1579	1239
21886	\N	4955	1580	1239
21887	\N	4954	1581	1239
21888	\N	4954	467	1239
21889	\N	4953	1582	1239
21890	\N	4954	1583	1239
21891	\N	4955	1584	1239
21892	\N	4955	468	1239
21893	\N	4954	469	1239
21894	\N	\N	470	1239
21895	\N	4955	471	1239
21896	\N	4953	1585	1239
21897	\N	4955	1586	1239
21898	\N	4957	1579	1240
21899	\N	4957	1580	1240
21900	\N	4957	1581	1240
21901	\N	4957	467	1240
21902	\N	4957	1582	1240
21903	\N	4959	1583	1240
21904	\N	4957	1584	1240
21905	\N	4957	468	1240
21906	\N	4957	469	1240
21907	\N	4957	470	1240
21908	\N	\N	471	1240
21909	\N	4957	1585	1240
21910	\N	4957	1586	1240
1082	\N	3041	1	39
1083	\N	3041	482	39
1084	\N	3041	26	39
1086	\N	3041	483	39
841	\N	3270	475	35
854	\N	3270	30	35
862	\N	3270	24	35
869	\N	3270	479	35
871	\N	3270	485	35
874	\N	3270	31	35
844	\N	3269	26	35
851	\N	3269	473	35
859	\N	3269	477	35
864	\N	3269	12	35
870	\N	3269	45	35
875	\N	3269	474	35
876	\N	3269	480	35
884	\N	3269	39	35
885	\N	3269	21	35
892	\N	3269	481	35
897	\N	3269	33	35
898	\N	3269	44	35
900	\N	3269	34	35
842	\N	3271	1	35
843	\N	3271	482	35
845	\N	3271	35	35
846	\N	3271	483	35
847	\N	3271	2	35
848	\N	3271	476	35
849	\N	3271	29	35
850	\N	3271	16	35
852	\N	3271	36	35
855	\N	3271	3	35
857	\N	3271	37	35
858	\N	3271	4	35
860	\N	3271	18	35
861	\N	3271	41	35
866	\N	3271	25	35
867	\N	3271	478	35
872	\N	3271	7	35
878	\N	3271	19	35
879	\N	3271	40	35
880	\N	3271	14	35
887	\N	3271	15	35
894	\N	3271	9	35
896	\N	3271	10	35
853	\N	3272	472	35
863	\N	3272	484	35
883	\N	3272	38	35
886	\N	3272	486	35
889	\N	3272	22	35
893	\N	3272	28	35
21831	\N	4946	1576	1234
21832	\N	4946	1578	1234
21833	\N	4946	1573	1234
21844	\N	4952	1578	1235
21835	\N	4951	1571	1235
21837	\N	4951	1575	1235
21839	\N	4951	1577	1235
21840	\N	4951	1570	1235
21841	\N	4951	1572	1235
21843	\N	4951	1576	1235
21845	\N	4951	1573	1235
21836	\N	4949	1574	1235
21842	\N	4949	1569	1235
1087	\N	3041	2	39
1088	\N	3041	476	39
1090	\N	3041	16	39
1092	\N	3041	36	39
1093	\N	3041	472	39
1094	\N	3041	30	39
1097	\N	3041	37	39
1100	\N	3041	18	39
1101	\N	3041	41	39
1102	\N	3041	24	39
1103	\N	3041	484	39
1106	\N	3041	25	39
1108	\N	3041	6	39
1109	\N	3041	479	39
1110	\N	3041	45	39
1114	\N	3041	31	39
1119	\N	3041	40	39
1122	\N	3041	27	39
1123	\N	3041	38	39
1127	\N	3041	15	39
1130	\N	3041	32	39
1132	\N	3041	481	39
1135	\N	3041	23	39
1138	\N	3041	44	39
1139	\N	3041	11	39
1081	\N	3043	475	39
1085	\N	3043	35	39
1089	\N	3043	29	39
1091	\N	3043	473	39
1095	\N	3043	3	39
1098	\N	3043	4	39
1104	\N	3043	12	39
1107	\N	3043	478	39
1111	\N	3043	485	39
1112	\N	3043	7	39
1099	\N	3044	477	39
1115	\N	3044	474	39
1116	\N	3044	480	39
1117	\N	3044	42	39
1118	\N	3044	19	39
1120	\N	3044	14	39
1121	\N	3044	20	39
1124	\N	3044	39	39
1125	\N	3044	21	39
1126	\N	3044	486	39
1128	\N	3044	43	39
1129	\N	3044	22	39
1131	\N	3044	8	39
1133	\N	3044	28	39
1134	\N	3044	9	39
1137	\N	3044	33	39
1140	\N	3044	34	39
2517	\N	3107	74	150
2477	\N	3106	50	150
2478	\N	3106	51	150
2485	\N	3106	56	150
2486	\N	3106	57	150
2487	\N	3106	58	150
2488	\N	3106	59	150
2928	\N	4373	100	160
2929	\N	4373	101	160
2931	\N	4373	85	160
2932	\N	4373	102	160
2935	\N	4373	88	160
2936	\N	4373	105	160
2939	\N	4373	108	160
2482	\N	3105	54	150
2493	\N	3105	82	150
2498	\N	3105	76	150
2499	\N	3105	79	150
2508	\N	3105	489	150
2511	\N	3105	48	150
2490	\N	3106	80	150
2492	\N	3106	491	150
2500	\N	3106	47	150
2501	\N	3106	78	150
2502	\N	3106	62	150
2507	\N	3106	488	150
2509	\N	3106	70	150
2510	\N	3106	65	150
2512	\N	3106	71	150
2513	\N	3106	49	150
2514	\N	3106	490	150
2516	\N	3106	67	150
2518	\N	3106	68	150
2519	\N	3106	69	150
2520	\N	3106	77	150
2480	\N	3108	73	150
2489	\N	3108	487	150
2257	\N	3948	50	145
2258	\N	3948	51	145
2262	\N	3948	54	145
2265	\N	3948	56	145
2269	\N	3948	487	145
2273	\N	3948	82	145
2275	\N	3948	84	145
2926	\N	4375	99	160
2930	\N	4375	91	160
2933	\N	4375	103	160
2937	\N	4375	106	160
2938	\N	4375	107	160
2942	\N	4375	111	160
2952	\N	4375	115	160
2954	\N	4375	117	160
2955	\N	4375	118	160
2957	\N	4375	119	160
2958	\N	4375	120	160
2960	\N	4375	122	160
2940	\N	4373	109	160
2941	\N	4373	110	160
2943	\N	4373	95	160
2944	\N	4373	86	160
2945	\N	4373	112	160
2946	\N	4373	113	160
2947	\N	4373	89	160
2948	\N	4373	96	160
2949	\N	4373	114	160
2950	\N	4373	92	160
2616	\N	4547	85	153
2622	\N	4547	106	153
2624	\N	4547	108	153
2636	\N	4547	87	153
2640	\N	4547	118	153
2641	\N	4547	97	153
2646	\N	4547	123	153
2647	\N	4547	124	153
2611	\N	4546	99	153
2634	\N	4546	114	153
2645	\N	4546	122	153
2648	\N	4546	98	153
2653	\N	4546	128	153
2613	\N	4545	100	153
2614	\N	4545	101	153
2617	\N	4545	102	153
2618	\N	4545	103	153
2625	\N	4545	109	153
2626	\N	4545	110	153
2630	\N	4545	112	153
2631	\N	4545	113	153
2632	\N	4545	89	153
2633	\N	4545	96	153
2643	\N	4545	120	153
2644	\N	4545	121	153
2651	\N	4545	126	153
2619	\N	4548	104	153
2620	\N	4548	88	153
2621	\N	4548	105	153
2623	\N	4548	107	153
2627	\N	4548	111	153
2628	\N	4548	95	153
2637	\N	4548	115	153
2638	\N	4548	116	153
2639	\N	4548	117	153
2642	\N	4548	119	153
2649	\N	4548	125	153
2652	\N	4548	127	153
2349	\N	4837	53	147
2350	\N	4837	54	147
3983	\N	4558	516	183
4001	\N	4558	497	183
4002	\N	4558	525	183
4003	\N	4558	526	183
4004	\N	4558	527	183
4008	\N	4558	531	183
4025	\N	4558	540	183
3990	\N	4557	519	183
3991	\N	4557	506	183
4010	\N	4557	533	183
4018	\N	4557	537	183
3997	\N	4560	496	183
4000	\N	4560	524	183
3979	\N	4559	512	183
3980	\N	4559	513	183
3981	\N	4559	514	183
3982	\N	4559	515	183
3984	\N	4559	517	183
3985	\N	4559	492	183
3986	\N	4559	518	183
3987	\N	4559	505	183
3988	\N	4559	502	183
3989	\N	4559	493	183
3992	\N	4559	507	183
3993	\N	4559	494	183
3994	\N	4559	520	183
3995	\N	4559	495	183
3996	\N	4559	521	183
3998	\N	4559	522	183
3999	\N	4559	523	183
4005	\N	4559	528	183
4006	\N	4559	529	183
4007	\N	4559	530	183
3880	\N	894	502	181
3916	\N	894	503	181
3923	\N	894	504	181
3877	\N	893	492	181
3881	\N	893	493	181
3885	\N	893	494	181
3887	\N	893	495	181
3889	\N	893	496	181
3893	\N	893	497	181
3904	\N	893	498	181
3905	\N	893	499	181
3911	\N	893	130	181
3914	\N	893	500	181
3918	\N	893	501	181
3871	\N	896	512	181
3872	\N	896	513	181
3873	\N	896	514	181
3874	\N	896	515	181
3875	\N	896	516	181
3876	\N	896	517	181
3878	\N	896	518	181
3882	\N	896	519	181
3886	\N	896	520	181
3888	\N	896	521	181
3890	\N	896	522	181
3891	\N	896	523	181
3892	\N	896	524	181
3894	\N	896	525	181
3895	\N	896	526	181
3896	\N	896	527	181
3897	\N	896	528	181
3898	\N	896	529	181
3899	\N	896	530	181
3900	\N	896	531	181
3901	\N	896	532	181
3902	\N	896	533	181
3903	\N	896	534	181
3906	\N	896	535	181
3907	\N	896	536	181
3910	\N	896	537	181
3913	\N	896	538	181
3915	\N	896	539	181
3917	\N	896	540	181
3919	\N	896	541	181
3920	\N	896	542	181
3922	\N	896	543	181
3924	\N	896	544	181
3879	\N	895	505	181
3883	\N	895	506	181
3884	\N	895	507	181
3908	\N	895	508	181
3909	\N	895	509	181
3912	\N	895	510	181
3921	\N	895	511	181
3938	\N	899	507	182
3943	\N	899	496	182
3956	\N	899	533	182
3971	\N	899	540	182
3925	\N	900	512	182
3929	\N	900	516	182
3932	\N	900	518	182
3936	\N	900	519	182
3939	\N	900	494	182
3944	\N	900	522	182
3949	\N	900	526	182
3953	\N	900	530	182
3958	\N	900	498	182
3960	\N	900	535	182
3963	\N	900	509	182
3964	\N	900	537	182
3967	\N	900	538	182
3969	\N	900	539	182
3973	\N	900	541	182
3975	\N	900	511	182
3978	\N	900	544	182
3927	\N	897	514	182
3928	\N	897	515	182
3931	\N	897	492	182
3933	\N	897	505	182
3934	\N	897	502	182
3935	\N	897	493	182
3937	\N	897	506	182
3941	\N	897	495	182
3942	\N	897	521	182
3948	\N	897	525	182
3950	\N	897	527	182
3955	\N	897	532	182
3957	\N	897	534	182
3961	\N	897	536	182
3962	\N	897	508	182
3966	\N	897	510	182
3968	\N	897	500	182
3970	\N	897	503	182
3974	\N	897	542	182
3976	\N	897	543	182
3977	\N	897	504	182
3926	\N	898	513	182
3930	\N	898	517	182
3940	\N	898	520	182
3945	\N	898	523	182
3946	\N	898	524	182
3947	\N	898	497	182
3951	\N	898	528	182
3952	\N	898	529	182
3954	\N	898	531	182
3959	\N	898	499	182
3972	\N	898	501	182
4009	\N	4559	532	183
4011	\N	4559	534	183
4012	\N	4559	498	183
4013	\N	4559	499	183
4014	\N	4559	535	183
4015	\N	4559	536	183
4016	\N	4559	508	183
4017	\N	4559	509	183
4020	\N	4559	510	183
4021	\N	4559	538	183
4022	\N	4559	500	183
4023	\N	4559	539	183
4024	\N	4559	503	183
4026	\N	4559	501	183
4027	\N	4559	541	183
4028	\N	4559	542	183
4029	\N	4559	511	183
4030	\N	4559	543	183
4031	\N	4559	504	183
4032	\N	4559	544	183
4141	\N	905	512	186
4153	\N	905	506	186
4160	\N	907	522	186
4163	\N	907	497	186
4175	\N	907	499	186
4142	\N	908	513	186
4143	\N	908	514	186
4145	\N	908	516	186
4146	\N	908	517	186
4147	\N	908	492	186
4148	\N	908	518	186
4149	\N	908	505	186
4150	\N	908	502	186
4152	\N	908	519	186
4154	\N	908	507	186
4155	\N	908	494	186
4156	\N	908	520	186
4157	\N	908	495	186
4158	\N	908	521	186
4159	\N	908	496	186
4161	\N	908	523	186
4162	\N	908	524	186
4164	\N	908	525	186
4165	\N	908	526	186
4166	\N	908	527	186
4167	\N	908	528	186
4168	\N	908	529	186
4169	\N	908	530	186
4170	\N	908	531	186
4171	\N	908	532	186
4172	\N	908	533	186
4173	\N	908	534	186
4174	\N	908	498	186
4176	\N	908	535	186
4177	\N	908	536	186
4178	\N	908	508	186
4179	\N	908	509	186
4180	\N	908	537	186
4181	\N	908	130	186
4182	\N	908	510	186
4183	\N	908	538	186
4184	\N	908	500	186
4185	\N	908	539	186
4186	\N	908	503	186
4187	\N	908	540	186
4188	\N	908	501	186
4189	\N	908	541	186
4190	\N	908	542	186
4191	\N	908	511	186
4192	\N	908	543	186
4193	\N	908	504	186
4194	\N	908	544	186
4144	\N	906	515	186
4151	\N	906	493	186
4195	\N	857	512	187
4196	\N	857	513	187
4197	\N	857	514	187
4204	\N	857	502	187
4206	\N	857	519	187
4209	\N	857	494	187
4210	\N	857	520	187
4212	\N	857	521	187
4214	\N	857	522	187
4219	\N	857	526	187
4222	\N	857	529	187
4224	\N	857	531	187
4225	\N	857	532	187
4233	\N	857	509	187
4234	\N	857	537	187
4237	\N	857	538	187
4238	\N	857	500	187
4240	\N	857	503	187
4242	\N	857	501	187
4246	\N	857	543	187
4200	\N	858	517	187
4203	\N	858	505	187
4208	\N	858	507	187
4211	\N	858	495	187
4228	\N	858	498	187
4231	\N	858	536	187
4198	\N	860	515	187
4205	\N	860	493	187
4218	\N	860	525	187
4226	\N	860	533	187
4227	\N	860	534	187
4248	\N	860	544	187
4199	\N	859	516	187
4201	\N	859	492	187
4202	\N	859	518	187
4207	\N	859	506	187
4213	\N	859	496	187
4215	\N	859	523	187
4216	\N	859	524	187
4217	\N	859	497	187
4220	\N	859	527	187
4221	\N	859	528	187
4223	\N	859	530	187
4229	\N	859	499	187
4230	\N	859	535	187
4232	\N	859	508	187
4235	\N	859	130	187
4236	\N	859	510	187
4239	\N	859	539	187
4241	\N	859	540	187
4243	\N	859	541	187
4244	\N	859	542	187
4245	\N	859	511	187
4247	\N	859	504	187
4255	\N	864	492	188
4262	\N	864	507	188
4282	\N	864	498	188
4288	\N	864	537	188
4295	\N	864	540	188
4249	\N	863	512	188
4250	\N	863	513	188
4251	\N	863	514	188
4252	\N	863	515	188
4253	\N	863	516	188
4254	\N	863	517	188
4256	\N	863	518	188
4257	\N	863	505	188
4258	\N	863	502	188
4259	\N	863	493	188
4260	\N	863	519	188
4261	\N	863	506	188
4263	\N	863	494	188
4264	\N	863	520	188
4265	\N	863	495	188
4266	\N	863	521	188
4267	\N	863	496	188
4268	\N	863	522	188
4269	\N	863	523	188
4270	\N	863	524	188
4271	\N	863	497	188
4272	\N	863	525	188
4273	\N	863	526	188
4274	\N	863	527	188
4275	\N	863	528	188
4276	\N	863	529	188
4277	\N	863	530	188
4278	\N	863	531	188
4279	\N	863	532	188
4280	\N	863	533	188
4281	\N	863	534	188
4283	\N	863	499	188
4284	\N	863	535	188
4285	\N	863	536	188
4286	\N	863	508	188
4287	\N	863	509	188
4289	\N	863	130	188
4290	\N	863	510	188
4291	\N	863	538	188
4292	\N	863	500	188
4293	\N	863	539	188
4294	\N	863	503	188
4296	\N	863	501	188
4297	\N	863	541	188
4298	\N	863	542	188
4299	\N	863	511	188
4300	\N	863	543	188
4301	\N	863	504	188
4302	\N	863	544	188
4357	\N	871	512	190
4359	\N	871	514	190
4362	\N	871	517	190
4363	\N	871	492	190
4364	\N	871	518	190
4365	\N	871	505	190
4367	\N	871	493	190
4370	\N	871	507	190
4373	\N	871	495	190
4375	\N	871	496	190
4376	\N	871	522	190
4378	\N	871	524	190
4380	\N	871	525	190
4381	\N	871	526	190
4384	\N	871	529	190
4385	\N	871	530	190
4386	\N	871	531	190
4387	\N	871	532	190
4388	\N	871	533	190
4389	\N	871	534	190
4393	\N	871	536	190
4394	\N	871	508	190
4397	\N	871	130	190
4398	\N	871	510	190
4399	\N	871	538	190
4400	\N	871	500	190
4403	\N	871	540	190
4404	\N	871	501	190
4408	\N	871	543	190
4409	\N	871	504	190
4410	\N	871	544	190
4360	\N	872	515	190
4361	\N	872	516	190
4366	\N	872	502	190
4368	\N	872	519	190
4371	\N	872	494	190
4392	\N	872	535	190
4395	\N	872	509	190
4406	\N	872	542	190
4407	\N	872	511	190
4369	\N	870	506	190
4377	\N	870	523	190
4379	\N	870	497	190
4390	\N	870	498	190
4396	\N	870	537	190
4358	\N	869	513	190
4372	\N	869	520	190
4374	\N	869	521	190
4382	\N	869	527	190
4383	\N	869	528	190
4391	\N	869	499	190
4401	\N	869	539	190
4402	\N	869	503	190
4405	\N	869	541	190
4429	\N	874	496	191
4439	\N	874	530	191
4444	\N	874	498	191
4459	\N	874	541	191
4411	\N	876	512	191
4415	\N	876	516	191
4416	\N	876	517	191
4418	\N	876	518	191
4419	\N	876	505	191
4420	\N	876	502	191
4424	\N	876	507	191
4426	\N	876	520	191
4430	\N	876	522	191
4431	\N	876	523	191
4432	\N	876	524	191
4437	\N	876	528	191
4438	\N	876	529	191
4440	\N	876	531	191
4443	\N	876	534	191
4447	\N	876	536	191
4453	\N	876	538	191
4454	\N	876	500	191
4456	\N	876	503	191
4463	\N	876	504	191
4464	\N	876	544	191
4412	\N	875	513	191
4413	\N	875	514	191
4414	\N	875	515	191
4417	\N	875	492	191
4422	\N	875	519	191
4425	\N	875	494	191
4427	\N	875	495	191
4428	\N	875	521	191
4433	\N	875	497	191
4434	\N	875	525	191
4435	\N	875	526	191
4436	\N	875	527	191
4445	\N	875	499	191
4446	\N	875	535	191
4448	\N	875	508	191
4449	\N	875	509	191
4450	\N	875	537	191
4451	\N	875	130	191
4455	\N	875	539	191
4457	\N	875	540	191
4458	\N	875	501	191
4460	\N	875	542	191
4462	\N	875	543	191
4421	\N	873	493	191
4423	\N	873	506	191
4441	\N	873	532	191
4442	\N	873	533	191
4452	\N	873	510	191
4461	\N	873	511	191
4465	\N	2089	512	192
4466	\N	2089	513	192
4467	\N	2089	514	192
4468	\N	2089	515	192
4469	\N	2089	516	192
4470	\N	2089	517	192
4471	\N	2089	492	192
4473	\N	2089	505	192
4474	\N	2089	502	192
4476	\N	2089	519	192
4477	\N	2089	506	192
4478	\N	2089	507	192
4479	\N	2089	494	192
4480	\N	2089	520	192
4481	\N	2089	495	192
4482	\N	2089	521	192
4483	\N	2089	496	192
4484	\N	2089	522	192
4485	\N	2089	523	192
4487	\N	2089	497	192
4488	\N	2089	525	192
4489	\N	2089	526	192
4491	\N	2089	528	192
4492	\N	2089	529	192
4493	\N	2089	530	192
4494	\N	2089	531	192
4495	\N	2089	532	192
4496	\N	2089	533	192
4497	\N	2089	534	192
4498	\N	2089	498	192
4499	\N	2089	499	192
4501	\N	2089	536	192
4504	\N	2089	537	192
4508	\N	2089	500	192
4509	\N	2089	539	192
4510	\N	2089	503	192
4511	\N	2089	540	192
4512	\N	2089	501	192
4513	\N	2089	541	192
4514	\N	2089	542	192
4515	\N	2089	511	192
4516	\N	2089	543	192
4517	\N	2089	504	192
4518	\N	2089	544	192
4472	\N	2091	518	192
4627	\N	877	512	195
4630	\N	877	515	195
4631	\N	877	516	195
4636	\N	877	502	195
4638	\N	877	519	195
4641	\N	877	494	195
4642	\N	877	520	195
4644	\N	877	521	195
4648	\N	877	524	195
4651	\N	877	526	195
4654	\N	877	529	195
4663	\N	877	536	195
4664	\N	877	508	195
4666	\N	877	537	195
4668	\N	877	510	195
4671	\N	877	539	195
4672	\N	877	503	195
4676	\N	877	542	195
4677	\N	877	511	195
4680	\N	877	544	195
4629	\N	878	514	195
4632	\N	878	517	195
4634	\N	878	518	195
4635	\N	878	505	195
4639	\N	878	506	195
4643	\N	878	495	195
4645	\N	878	496	195
4646	\N	878	522	195
4647	\N	878	523	195
4649	\N	878	497	195
4650	\N	878	525	195
4652	\N	878	527	195
4653	\N	878	528	195
4655	\N	878	530	195
4656	\N	878	531	195
4657	\N	878	532	195
4658	\N	878	533	195
4659	\N	878	534	195
4661	\N	878	499	195
4662	\N	878	535	195
4665	\N	878	509	195
4669	\N	878	538	195
4678	\N	878	543	195
4633	\N	879	492	195
4640	\N	879	507	195
4660	\N	879	498	195
4667	\N	879	130	195
4674	\N	879	501	195
4679	\N	879	504	195
4628	\N	880	513	195
4637	\N	880	493	195
4670	\N	880	500	195
4673	\N	880	540	195
4675	\N	880	541	195
4747	\N	885	506	197
4748	\N	885	507	197
4749	\N	885	494	197
4753	\N	885	496	197
4754	\N	885	522	197
4759	\N	885	526	197
4760	\N	885	527	197
4762	\N	885	529	197
4764	\N	885	531	197
4769	\N	885	499	197
4770	\N	885	535	197
4771	\N	885	536	197
4772	\N	885	508	197
4774	\N	885	537	197
4777	\N	885	538	197
4778	\N	885	500	197
4781	\N	885	540	197
4782	\N	885	501	197
4783	\N	885	541	197
4784	\N	885	542	197
4785	\N	885	511	197
4786	\N	885	543	197
4787	\N	885	504	197
4735	\N	888	512	197
4737	\N	888	514	197
4743	\N	888	505	197
4750	\N	888	520	197
4766	\N	888	533	197
4773	\N	888	509	197
4736	\N	887	513	197
4738	\N	887	515	197
4739	\N	887	516	197
4740	\N	887	517	197
4752	\N	887	521	197
4755	\N	887	523	197
4756	\N	887	524	197
4757	\N	887	497	197
4758	\N	887	525	197
4761	\N	887	528	197
4763	\N	887	530	197
4765	\N	887	532	197
4767	\N	887	534	197
4776	\N	887	510	197
4779	\N	887	539	197
4780	\N	887	503	197
4788	\N	887	544	197
4792	\N	889	515	198
4793	\N	889	516	198
4794	\N	889	517	198
4798	\N	889	502	198
4800	\N	889	519	198
4802	\N	889	507	198
4803	\N	889	494	198
4804	\N	889	520	198
4805	\N	889	495	198
4806	\N	889	521	198
4807	\N	889	496	198
4808	\N	889	522	198
4814	\N	889	527	198
4815	\N	889	528	198
4816	\N	889	529	198
4818	\N	889	531	198
4821	\N	889	534	198
4822	\N	889	498	198
4829	\N	889	130	198
4831	\N	889	538	198
4832	\N	889	500	198
4834	\N	889	503	198
4835	\N	889	540	198
4838	\N	889	542	198
4839	\N	889	511	198
4841	\N	889	504	198
4789	\N	890	512	198
4790	\N	890	513	198
4791	\N	890	514	198
4795	\N	890	492	198
4809	\N	890	523	198
4813	\N	890	526	198
4824	\N	890	535	198
4825	\N	890	536	198
4826	\N	890	508	198
4828	\N	890	537	198
4833	\N	890	539	198
4836	\N	890	501	198
4837	\N	890	541	198
4840	\N	890	543	198
4842	\N	890	544	198
4796	\N	891	518	198
4797	\N	891	505	198
4810	\N	891	524	198
4799	\N	892	493	198
4801	\N	892	506	198
4811	\N	892	497	198
4812	\N	892	525	198
4817	\N	892	530	198
4819	\N	892	532	198
4820	\N	892	533	198
4823	\N	892	499	198
4827	\N	892	509	198
4830	\N	892	510	198
4955	\N	854	516	201
4957	\N	854	492	201
4958	\N	854	518	201
4963	\N	854	506	201
4970	\N	854	522	201
4973	\N	854	497	201
4982	\N	854	533	201
4984	\N	854	498	201
4995	\N	854	539	201
4997	\N	854	540	201
5000	\N	854	542	201
5001	\N	854	511	201
5003	\N	854	504	201
4953	\N	853	514	201
4954	\N	853	515	201
4959	\N	853	505	201
4960	\N	853	502	201
4961	\N	853	493	201
4962	\N	853	519	201
4965	\N	853	494	201
4967	\N	853	495	201
4969	\N	853	496	201
4971	\N	853	523	201
4972	\N	853	524	201
4974	\N	853	525	201
4977	\N	853	528	201
4978	\N	853	529	201
4980	\N	853	531	201
4981	\N	853	532	201
4986	\N	853	535	201
4989	\N	853	509	201
4991	\N	853	130	201
4992	\N	853	510	201
4994	\N	853	500	201
4996	\N	853	503	201
4998	\N	853	501	201
4999	\N	853	541	201
5002	\N	853	543	201
4951	\N	856	512	201
4952	\N	856	513	201
4956	\N	856	517	201
4964	\N	856	507	201
4966	\N	856	520	201
4968	\N	856	521	201
4975	\N	856	526	201
4983	\N	856	534	201
4987	\N	856	536	201
4988	\N	856	508	201
4990	\N	856	537	201
4993	\N	856	538	201
5004	\N	856	544	201
4976	\N	855	527	201
4979	\N	855	530	201
4985	\N	855	499	201
5005	\N	913	512	202
5015	\N	913	493	202
5016	\N	913	519	202
5018	\N	913	507	202
5032	\N	913	529	202
5036	\N	913	533	202
5053	\N	913	541	202
5008	\N	914	515	202
5011	\N	914	492	202
5012	\N	914	518	202
5014	\N	914	502	202
5021	\N	914	495	202
5022	\N	914	521	202
5028	\N	914	525	202
5029	\N	914	526	202
5034	\N	914	531	202
5035	\N	914	532	202
5037	\N	914	534	202
5038	\N	914	498	202
5039	\N	914	499	202
5040	\N	914	535	202
5049	\N	914	539	202
5050	\N	914	503	202
5054	\N	914	542	202
5057	\N	914	504	202
5006	\N	916	513	202
5007	\N	916	514	202
5013	\N	916	505	202
5017	\N	916	506	202
5023	\N	916	496	202
5024	\N	916	522	202
5025	\N	916	523	202
5026	\N	916	524	202
5027	\N	916	497	202
5030	\N	916	527	202
5041	\N	916	536	202
5043	\N	916	509	202
5044	\N	916	537	202
5045	\N	916	130	202
5046	\N	916	510	202
5047	\N	916	538	202
5048	\N	916	500	202
5051	\N	916	540	202
5052	\N	916	501	202
5055	\N	916	511	202
5056	\N	916	543	202
5009	\N	915	516	202
5010	\N	915	517	202
5019	\N	915	494	202
5020	\N	915	520	202
5031	\N	915	528	202
5033	\N	915	530	202
5042	\N	915	508	202
5058	\N	915	544	202
5066	\N	917	518	203
5068	\N	917	502	203
5076	\N	917	521	203
5081	\N	917	497	203
5110	\N	917	543	203
5112	\N	917	544	203
5093	\N	918	499	203
5063	\N	920	516	203
5069	\N	920	493	203
5071	\N	920	506	203
5072	\N	920	507	203
5078	\N	920	522	203
5080	\N	920	524	203
5086	\N	920	529	203
5097	\N	920	509	203
5100	\N	920	510	203
5102	\N	920	500	203
5107	\N	920	541	203
5059	\N	919	512	203
5060	\N	919	513	203
5061	\N	919	514	203
5062	\N	919	515	203
5064	\N	919	517	203
5065	\N	919	492	203
5067	\N	919	505	203
5070	\N	919	519	203
5073	\N	919	494	203
5074	\N	919	520	203
5075	\N	919	495	203
5077	\N	919	496	203
5079	\N	919	523	203
5082	\N	919	525	203
5083	\N	919	526	203
5084	\N	919	527	203
5085	\N	919	528	203
5087	\N	919	530	203
5088	\N	919	531	203
5089	\N	919	532	203
5090	\N	919	533	203
5091	\N	919	534	203
5092	\N	919	498	203
5094	\N	919	535	203
5095	\N	919	536	203
5096	\N	919	508	203
5098	\N	919	537	203
5101	\N	919	538	203
5103	\N	919	539	203
5104	\N	919	503	203
5105	\N	919	540	203
5106	\N	919	501	203
5108	\N	919	542	203
5109	\N	919	511	203
5111	\N	919	504	203
5115	\N	923	514	204
5134	\N	923	524	204
5147	\N	923	499	204
5148	\N	923	535	204
5151	\N	923	509	204
5124	\N	924	519	204
5125	\N	924	506	204
5127	\N	924	494	204
5131	\N	924	496	204
5133	\N	924	523	204
5139	\N	924	528	204
5141	\N	924	530	204
5158	\N	924	503	204
5165	\N	924	504	204
5113	\N	922	512	204
5114	\N	922	513	204
5116	\N	922	515	204
5117	\N	922	516	204
5118	\N	922	517	204
5119	\N	922	492	204
5120	\N	922	518	204
5121	\N	922	505	204
5122	\N	922	502	204
5123	\N	922	493	204
5126	\N	922	507	204
5128	\N	922	520	204
5129	\N	922	495	204
5130	\N	922	521	204
5132	\N	922	522	204
5135	\N	922	497	204
5136	\N	922	525	204
5137	\N	922	526	204
5138	\N	922	527	204
5140	\N	922	529	204
5142	\N	922	531	204
5144	\N	922	533	204
5145	\N	922	534	204
5146	\N	922	498	204
5149	\N	922	536	204
5150	\N	922	508	204
5152	\N	922	537	204
5154	\N	922	510	204
5155	\N	922	538	204
5156	\N	922	500	204
5157	\N	922	539	204
5159	\N	922	540	204
5160	\N	922	501	204
5161	\N	922	541	204
5162	\N	922	542	204
5164	\N	922	543	204
5166	\N	922	544	204
5143	\N	921	532	204
5163	\N	921	511	204
5178	\N	926	519	205
5180	\N	926	507	205
5183	\N	926	495	205
5195	\N	926	530	205
5201	\N	926	499	205
5204	\N	926	508	205
5205	\N	926	509	205
5206	\N	926	537	205
5212	\N	926	503	205
5217	\N	926	511	205
5218	\N	926	543	205
5167	\N	925	512	205
5170	\N	925	515	205
5171	\N	925	516	205
5172	\N	925	517	205
5174	\N	925	518	205
5176	\N	925	502	205
5179	\N	925	506	205
5181	\N	925	494	205
5184	\N	925	521	205
5185	\N	925	496	205
5186	\N	925	522	205
5187	\N	925	523	205
5188	\N	925	524	205
5190	\N	925	525	205
5191	\N	925	526	205
5194	\N	925	529	205
5196	\N	925	531	205
5197	\N	925	532	205
5200	\N	925	498	205
5203	\N	925	536	205
5208	\N	925	510	205
5209	\N	925	538	205
5210	\N	925	500	205
5211	\N	925	539	205
5213	\N	925	540	205
5216	\N	925	542	205
5220	\N	925	544	205
5175	\N	928	505	205
5202	\N	928	535	205
5207	\N	928	130	205
5215	\N	928	541	205
5168	\N	927	513	205
5169	\N	927	514	205
5173	\N	927	492	205
5177	\N	927	493	205
5182	\N	927	520	205
5189	\N	927	497	205
5192	\N	927	527	205
5193	\N	927	528	205
5198	\N	927	533	205
5199	\N	927	534	205
5214	\N	927	501	205
5219	\N	927	504	205
5222	\N	931	513	206
5228	\N	931	518	206
5236	\N	931	520	206
5242	\N	931	524	206
5244	\N	931	525	206
5247	\N	931	528	206
5248	\N	931	529	206
5256	\N	931	535	206
5257	\N	931	536	206
5259	\N	931	509	206
5261	\N	931	130	206
5262	\N	931	510	206
5263	\N	931	538	206
5264	\N	931	500	206
5267	\N	931	540	206
5269	\N	931	541	206
5229	\N	932	505	206
5231	\N	932	493	206
5235	\N	932	494	206
5237	\N	932	495	206
5249	\N	932	530	206
5252	\N	932	533	206
5274	\N	932	544	206
5221	\N	929	512	206
5223	\N	929	514	206
5225	\N	929	516	206
5232	\N	929	519	206
5234	\N	929	507	206
5238	\N	929	521	206
5239	\N	929	496	206
5240	\N	929	522	206
5243	\N	929	497	206
5246	\N	929	527	206
5250	\N	929	531	206
5251	\N	929	532	206
5253	\N	929	534	206
5254	\N	929	498	206
5255	\N	929	499	206
5258	\N	929	508	206
5260	\N	929	537	206
5266	\N	929	503	206
5268	\N	929	501	206
5270	\N	929	542	206
5271	\N	929	511	206
5272	\N	929	543	206
5273	\N	929	504	206
5224	\N	930	515	206
5226	\N	930	517	206
5227	\N	930	492	206
5230	\N	930	502	206
5233	\N	930	506	206
5241	\N	930	523	206
5245	\N	930	526	206
5265	\N	930	539	206
5285	\N	935	493	207
5303	\N	935	530	207
5311	\N	935	536	207
5313	\N	935	509	207
5321	\N	935	540	207
5322	\N	935	501	207
5327	\N	935	504	207
5275	\N	934	512	207
5276	\N	934	513	207
5277	\N	934	514	207
5278	\N	934	515	207
5279	\N	934	516	207
5281	\N	934	492	207
5283	\N	934	505	207
5284	\N	934	502	207
5286	\N	934	519	207
5288	\N	934	507	207
5289	\N	934	494	207
5290	\N	934	520	207
5291	\N	934	495	207
5292	\N	934	521	207
5293	\N	934	496	207
5294	\N	934	522	207
5296	\N	934	524	207
5297	\N	934	497	207
5298	\N	934	525	207
5299	\N	934	526	207
5300	\N	934	527	207
5301	\N	934	528	207
5302	\N	934	529	207
5304	\N	934	531	207
5307	\N	934	534	207
5308	\N	934	498	207
5309	\N	934	499	207
5312	\N	934	508	207
5315	\N	934	130	207
5316	\N	934	510	207
5317	\N	934	538	207
5318	\N	934	500	207
5319	\N	934	539	207
5320	\N	934	503	207
5325	\N	934	511	207
5326	\N	934	543	207
5328	\N	934	544	207
5280	\N	933	517	207
5282	\N	933	518	207
5324	\N	933	542	207
5287	\N	936	506	207
5295	\N	936	523	207
5305	\N	936	532	207
5306	\N	936	533	207
5310	\N	936	535	207
5314	\N	936	537	207
5323	\N	936	541	207
5329	\N	938	512	208
5331	\N	938	514	208
5336	\N	938	518	208
5339	\N	938	493	208
5340	\N	938	519	208
5342	\N	938	507	208
5343	\N	938	494	208
5346	\N	938	521	208
5347	\N	938	496	208
5348	\N	938	522	208
5350	\N	938	524	208
5352	\N	938	525	208
5354	\N	938	527	208
5358	\N	938	531	208
5360	\N	938	533	208
5362	\N	938	498	208
5367	\N	938	509	208
5371	\N	938	538	208
5372	\N	938	500	208
5373	\N	938	539	208
5376	\N	938	501	208
5332	\N	940	515	208
5351	\N	940	497	208
5355	\N	940	528	208
5374	\N	940	503	208
5380	\N	940	543	208
5330	\N	937	513	208
5333	\N	937	516	208
5335	\N	937	492	208
5359	\N	937	532	208
5361	\N	937	534	208
5334	\N	939	517	208
5337	\N	939	505	208
5338	\N	939	502	208
5341	\N	939	506	208
5344	\N	939	520	208
5345	\N	939	495	208
5349	\N	939	523	208
5353	\N	939	526	208
5356	\N	939	529	208
5357	\N	939	530	208
5363	\N	939	499	208
5364	\N	939	535	208
5365	\N	939	536	208
5366	\N	939	508	208
5368	\N	939	537	208
5370	\N	939	510	208
5375	\N	939	540	208
5377	\N	939	541	208
5378	\N	939	542	208
5379	\N	939	511	208
5381	\N	939	504	208
5382	\N	939	544	208
5383	\N	843	512	209
5386	\N	843	515	209
5387	\N	843	516	209
5388	\N	843	517	209
5389	\N	843	492	209
5390	\N	843	518	209
5393	\N	843	493	209
5395	\N	843	506	209
5401	\N	843	496	209
5404	\N	843	524	209
5405	\N	843	497	209
5406	\N	843	525	209
5409	\N	843	528	209
5413	\N	843	532	209
5414	\N	843	533	209
5416	\N	843	498	209
5417	\N	843	499	209
5418	\N	843	535	209
5424	\N	843	510	209
5427	\N	843	539	209
5428	\N	843	503	209
5429	\N	843	540	209
5430	\N	843	501	209
5431	\N	843	541	209
5435	\N	843	504	209
5436	\N	843	544	209
5385	\N	842	514	209
5391	\N	842	505	209
5392	\N	842	502	209
5396	\N	842	507	209
5399	\N	842	495	209
5403	\N	842	523	209
5407	\N	842	526	209
5408	\N	842	527	209
5410	\N	842	529	209
5411	\N	842	530	209
5415	\N	842	534	209
5419	\N	842	536	209
5420	\N	842	508	209
5421	\N	842	509	209
5422	\N	842	537	209
5423	\N	842	130	209
5425	\N	842	538	209
5432	\N	842	542	209
5434	\N	842	543	209
5384	\N	844	513	209
5402	\N	844	522	209
5426	\N	844	500	209
5433	\N	844	511	209
5394	\N	841	519	209
5397	\N	841	494	209
5398	\N	841	520	209
5400	\N	841	521	209
5412	\N	841	531	209
5437	\N	847	512	210
5439	\N	847	514	210
5440	\N	847	515	210
5444	\N	847	518	210
5448	\N	847	519	210
5451	\N	847	494	210
5452	\N	847	520	210
5453	\N	847	495	210
5454	\N	847	521	210
5456	\N	847	522	210
5460	\N	847	525	210
5462	\N	847	527	210
5463	\N	847	528	210
5466	\N	847	531	210
5468	\N	847	533	210
5470	\N	847	498	210
5472	\N	847	535	210
5473	\N	847	536	210
5475	\N	847	509	210
5479	\N	847	538	210
5480	\N	847	500	210
5484	\N	847	501	210
5488	\N	847	543	210
5441	\N	848	516	210
5461	\N	848	526	210
5487	\N	848	511	210
5442	\N	846	517	210
5447	\N	846	493	210
5449	\N	846	506	210
5450	\N	846	507	210
5455	\N	846	496	210
5457	\N	846	523	210
5467	\N	846	532	210
5477	\N	846	130	210
5478	\N	846	510	210
5489	\N	846	504	210
5438	\N	845	513	210
5443	\N	845	492	210
5445	\N	845	505	210
5446	\N	845	502	210
5458	\N	845	524	210
5459	\N	845	497	210
5464	\N	845	529	210
5465	\N	845	530	210
5469	\N	845	534	210
5471	\N	845	499	210
5474	\N	845	508	210
5476	\N	845	537	210
5481	\N	845	539	210
5482	\N	845	503	210
5483	\N	845	540	210
5485	\N	845	541	210
5486	\N	845	542	210
5490	\N	845	544	210
4687	\N	4492	492	196
4691	\N	4492	493	196
4694	\N	4492	507	196
5874	\N	4747	564	217
5907	\N	4747	592	217
5917	\N	4747	581	217
4342	\N	4747	537	189
4350	\N	4747	501	189
5859	\N	4745	555	217
5868	\N	4745	597	217
5871	\N	4745	584	217
5880	\N	4745	548	217
5887	\N	4745	588	217
5901	\N	4745	133	217
5905	\N	4745	578	217
5910	\N	4745	579	217
5911	\N	4745	600	217
4308	\N	4745	517	189
4316	\N	4745	507	189
4344	\N	4745	510	189
6120	\N	4489	586	221
6134	\N	4489	571	221
6137	\N	4489	573	221
6145	\N	4489	133	221
6151	\N	4489	592	221
6152	\N	4489	593	221
6156	\N	4489	595	221
6157	\N	4489	601	221
6158	\N	4489	553	221
4711	\N	4489	532	196
4713	\N	4489	534	196
4714	\N	4489	498	196
4719	\N	4489	509	196
4732	\N	4489	543	196
5857	\N	4746	554	217
5858	\N	4746	582	217
5860	\N	4746	556	217
5861	\N	4746	557	217
5862	\N	4746	545	217
5863	\N	4746	558	217
5864	\N	4746	559	217
5865	\N	4746	546	217
5866	\N	4746	583	217
5867	\N	4746	560	217
5869	\N	4746	561	217
5872	\N	4746	563	217
5873	\N	4746	585	217
5875	\N	4746	547	217
5877	\N	4746	565	217
5878	\N	4746	587	217
5879	\N	4746	566	217
5882	\N	4746	567	217
5883	\N	4746	568	217
5884	\N	4746	569	217
5885	\N	4746	570	217
5886	\N	4746	550	217
5889	\N	4746	589	217
5890	\N	4746	571	217
5891	\N	4746	131	217
5893	\N	4746	573	217
5894	\N	4746	574	217
5895	\N	4746	134	217
5898	\N	4746	575	217
5899	\N	4746	598	217
4698	\N	4492	521	196
4700	\N	4492	522	196
4702	\N	4492	524	196
4706	\N	4492	527	196
4707	\N	4492	528	196
4716	\N	4492	535	196
4720	\N	4492	537	196
4722	\N	4492	510	196
4725	\N	4492	539	196
4726	\N	4492	503	196
4727	\N	4492	540	196
4729	\N	4492	541	196
7077	\N	3017	554	237
7081	\N	3017	557	237
7085	\N	3017	546	237
7088	\N	3017	597	237
7090	\N	3017	562	237
7092	\N	3017	563	237
7093	\N	3017	585	237
7096	\N	3017	586	237
7099	\N	3017	566	237
7101	\N	3017	549	237
7103	\N	3017	568	237
7109	\N	3017	589	237
7112	\N	3017	572	237
7113	\N	3017	573	237
7114	\N	3017	574	237
7123	\N	3017	577	237
7124	\N	3017	599	237
7129	\N	3017	594	237
7130	\N	3017	579	237
7131	\N	3017	600	237
7132	\N	3017	595	237
7133	\N	3017	601	237
7135	\N	3017	596	237
4080	\N	3020	501	184
7199	\N	3558	554	239
7203	\N	3558	557	239
7204	\N	3558	545	239
7207	\N	3558	546	239
7208	\N	3558	583	239
7210	\N	3558	597	239
7214	\N	3558	563	239
7216	\N	3558	564	239
7217	\N	3558	547	239
7220	\N	3558	587	239
7221	\N	3558	566	239
7223	\N	3558	549	239
7228	\N	3558	550	239
7230	\N	3558	132	239
7231	\N	3558	589	239
7235	\N	3558	573	239
7241	\N	3558	598	239
7242	\N	3558	576	239
7243	\N	3558	133	239
7246	\N	3558	599	239
7249	\N	3558	592	239
7250	\N	3558	593	239
7251	\N	3558	594	239
7253	\N	3558	600	239
7256	\N	3558	553	239
7259	\N	3558	581	239
4519	\N	3558	512	193
4525	\N	3558	492	193
4527	\N	3558	505	193
4528	\N	3558	502	193
4529	\N	3558	493	193
4531	\N	3558	506	193
4534	\N	3558	520	193
4536	\N	3558	521	193
4537	\N	3558	496	193
4544	\N	3558	527	193
4546	\N	3558	529	193
4550	\N	3558	533	193
4552	\N	3558	498	193
4556	\N	3558	508	193
4559	\N	3558	130	193
4560	\N	3558	510	193
4563	\N	3558	539	193
4565	\N	3558	540	193
4571	\N	3558	504	193
4572	\N	3558	544	193
7244	\N	3557	591	239
7252	\N	3557	579	239
7255	\N	3557	601	239
4545	\N	3557	528	193
4568	\N	3557	542	193
7200	\N	3560	582	239
7201	\N	3560	555	239
7202	\N	3560	556	239
7205	\N	3560	558	239
7206	\N	3560	559	239
7211	\N	3560	561	239
7212	\N	3560	562	239
7213	\N	3560	584	239
7215	\N	3560	585	239
7219	\N	3560	565	239
7225	\N	3560	568	239
7226	\N	3560	569	239
7227	\N	3560	570	239
7326	\N	1123	607	241
7335	\N	1123	606	241
7349	\N	1123	608	241
7328	\N	1122	602	241
7334	\N	1122	603	241
7345	\N	1122	604	241
7346	\N	1122	605	241
7321	\N	1124	609	241
7322	\N	1124	610	241
7323	\N	1124	614	241
7324	\N	1124	620	241
7325	\N	1124	621	241
7327	\N	1124	135	241
7329	\N	1124	612	241
7330	\N	1124	613	241
7331	\N	1124	615	241
7332	\N	1124	618	241
7333	\N	1124	619	241
7336	\N	1124	624	241
7337	\N	1124	627	241
7338	\N	1124	628	241
7339	\N	1124	631	241
7340	\N	1124	611	241
7341	\N	1124	616	241
7342	\N	1124	617	241
7343	\N	1124	622	241
7344	\N	1124	623	241
7347	\N	1124	625	241
7348	\N	1124	626	241
7350	\N	1124	629	241
7351	\N	1124	630	241
7352	\N	1127	609	242
7353	\N	1127	610	242
7354	\N	1127	614	242
7356	\N	1127	621	242
7358	\N	1127	135	242
7359	\N	1127	602	242
7360	\N	1127	612	242
7361	\N	1127	613	242
7362	\N	1127	615	242
7363	\N	1127	618	242
7364	\N	1127	619	242
7365	\N	1127	603	242
7369	\N	1127	628	242
7370	\N	1127	631	242
7372	\N	1127	616	242
7373	\N	1127	617	242
7374	\N	1127	622	242
7378	\N	1127	625	242
7381	\N	1127	629	242
7382	\N	1127	630	242
7366	\N	1125	606	242
7355	\N	1128	620	242
7357	\N	1128	607	242
7367	\N	1128	624	242
7368	\N	1128	627	242
7371	\N	1128	611	242
7375	\N	1128	623	242
7376	\N	1128	604	242
7379	\N	1128	626	242
7380	\N	1128	608	242
7377	\N	1126	605	242
7408	\N	1129	605	243
7411	\N	1129	608	243
7383	\N	1132	609	243
7384	\N	1132	610	243
7385	\N	1132	614	243
7386	\N	1132	620	243
7387	\N	1132	621	243
7388	\N	1132	607	243
7389	\N	1132	135	243
7390	\N	1132	602	243
7391	\N	1132	612	243
7392	\N	1132	613	243
7393	\N	1132	615	243
7394	\N	1132	618	243
7395	\N	1132	619	243
7396	\N	1132	603	243
7397	\N	1132	606	243
7398	\N	1132	624	243
7400	\N	1132	628	243
7401	\N	1132	631	243
7402	\N	1132	611	243
7403	\N	1132	616	243
7404	\N	1132	617	243
7405	\N	1132	622	243
7406	\N	1132	623	243
7407	\N	1132	604	243
7409	\N	1132	625	243
7412	\N	1132	629	243
7413	\N	1132	630	243
7410	\N	1131	626	243
7399	\N	1130	627	243
7415	\N	1133	610	244
7416	\N	1133	614	244
7417	\N	1133	620	244
7418	\N	1133	621	244
7420	\N	1133	135	244
7421	\N	1133	602	244
7422	\N	1133	612	244
7423	\N	1133	613	244
7424	\N	1133	615	244
7425	\N	1133	618	244
7426	\N	1133	619	244
7427	\N	1133	603	244
7430	\N	1133	627	244
7432	\N	1133	631	244
7433	\N	1133	611	244
7435	\N	1133	617	244
7436	\N	1133	622	244
7440	\N	1133	625	244
7444	\N	1133	630	244
7446	\N	1142	610	245
7450	\N	1142	607	245
7451	\N	1142	135	245
7452	\N	1142	602	245
7454	\N	1142	613	245
7456	\N	1142	618	245
7464	\N	1142	611	245
7465	\N	1142	616	245
7467	\N	1142	622	245
7471	\N	1142	625	245
7472	\N	1142	626	245
7475	\N	1142	630	245
7445	\N	1141	609	245
7453	\N	1141	612	245
7455	\N	1141	615	245
7463	\N	1141	631	245
7473	\N	1141	608	245
7458	\N	1144	603	245
7459	\N	1144	606	245
7461	\N	1144	627	245
7468	\N	1144	623	245
7474	\N	1144	629	245
7447	\N	1143	614	245
7448	\N	1143	620	245
7449	\N	1143	621	245
7457	\N	1143	619	245
7460	\N	1143	624	245
7462	\N	1143	628	245
7466	\N	1143	617	245
7469	\N	1143	604	245
7470	\N	1143	605	245
7508	\N	1098	610	247
7509	\N	1098	614	247
7510	\N	1098	620	247
7512	\N	1098	607	247
7518	\N	1098	618	247
7519	\N	1098	619	247
7522	\N	1098	624	247
7523	\N	1098	627	247
7525	\N	1098	631	247
7526	\N	1098	611	247
7529	\N	1098	622	247
7515	\N	1100	612	247
7527	\N	1100	616	247
7532	\N	1100	605	247
7520	\N	1099	603	247
7521	\N	1099	606	247
7528	\N	1099	617	247
7535	\N	1099	608	247
7507	\N	1097	609	247
7511	\N	1097	621	247
7513	\N	1097	135	247
7514	\N	1097	602	247
7516	\N	1097	613	247
7517	\N	1097	615	247
7524	\N	1097	628	247
7530	\N	1097	623	247
7531	\N	1097	604	247
7533	\N	1097	625	247
7534	\N	1097	626	247
7536	\N	1097	629	247
7537	\N	1097	630	247
7542	\N	1102	621	248
7551	\N	1102	603	248
7552	\N	1102	606	248
7553	\N	1102	624	248
7554	\N	1102	627	248
7558	\N	1102	616	248
7562	\N	1102	604	248
7563	\N	1102	605	248
7539	\N	1104	610	248
7541	\N	1104	620	248
7544	\N	1104	135	248
7545	\N	1104	602	248
7546	\N	1104	612	248
7547	\N	1104	613	248
7548	\N	1104	615	248
7549	\N	1104	618	248
7550	\N	1104	619	248
7555	\N	1104	628	248
7556	\N	1104	631	248
7557	\N	1104	611	248
7564	\N	1104	625	248
7565	\N	1104	626	248
7567	\N	1104	629	248
7568	\N	1104	630	248
7560	\N	1103	622	248
7561	\N	1103	623	248
7566	\N	1103	608	248
7538	\N	1101	609	248
7540	\N	1101	614	248
7543	\N	1101	607	248
7559	\N	1101	617	248
7607	\N	1111	602	250
7608	\N	1111	612	250
7623	\N	1111	623	250
7627	\N	1111	626	250
7601	\N	1109	610	250
7610	\N	1109	615	250
7621	\N	1109	617	250
7629	\N	1109	629	250
7600	\N	1110	609	250
7612	\N	1110	619	250
7616	\N	1110	627	250
7618	\N	1110	631	250
7624	\N	1110	604	250
7630	\N	1110	630	250
7602	\N	1112	614	250
7603	\N	1112	620	250
7604	\N	1112	621	250
7605	\N	1112	607	250
7609	\N	1112	613	250
7611	\N	1112	618	250
7613	\N	1112	603	250
7614	\N	1112	606	250
7615	\N	1112	624	250
7617	\N	1112	628	250
7619	\N	1112	611	250
7620	\N	1112	616	250
7622	\N	1112	622	250
7625	\N	1112	605	250
7626	\N	1112	625	250
7628	\N	1112	608	250
7634	\N	1114	620	251
7636	\N	1114	607	251
7644	\N	1114	603	251
7646	\N	1114	624	251
7648	\N	1114	628	251
7653	\N	1114	622	251
7654	\N	1114	623	251
7659	\N	1114	608	251
7631	\N	1116	609	251
7633	\N	1116	614	251
7649	\N	1116	631	251
7651	\N	1116	616	251
7652	\N	1116	617	251
7660	\N	1116	629	251
7661	\N	1116	630	251
7635	\N	1115	621	251
7647	\N	1115	627	251
7632	\N	1113	610	251
7637	\N	1113	135	251
7638	\N	1113	602	251
7639	\N	1113	612	251
7640	\N	1113	613	251
7641	\N	1113	615	251
7642	\N	1113	618	251
7643	\N	1113	619	251
7645	\N	1113	606	251
7650	\N	1113	611	251
7655	\N	1113	604	251
7656	\N	1113	605	251
7657	\N	1113	625	251
7658	\N	1113	626	251
7685	\N	1138	623	252
7690	\N	1138	608	252
7687	\N	1137	605	252
7692	\N	1137	630	252
7662	\N	1140	609	252
7663	\N	1140	610	252
7664	\N	1140	614	252
7665	\N	1140	620	252
7666	\N	1140	621	252
7667	\N	1140	607	252
7668	\N	1140	135	252
7669	\N	1140	602	252
7670	\N	1140	612	252
7671	\N	1140	613	252
7672	\N	1140	615	252
7673	\N	1140	618	252
7674	\N	1140	619	252
7675	\N	1140	603	252
7676	\N	1140	606	252
7677	\N	1140	624	252
7678	\N	1140	627	252
7679	\N	1140	628	252
7680	\N	1140	631	252
7681	\N	1140	611	252
7682	\N	1140	616	252
7683	\N	1140	617	252
7684	\N	1140	622	252
7686	\N	1140	604	252
7688	\N	1140	625	252
7689	\N	1140	626	252
7691	\N	1140	629	252
7704	\N	1118	618	253
7706	\N	1118	603	253
7712	\N	1118	611	253
7713	\N	1118	616	253
7717	\N	1118	604	253
7720	\N	1118	626	253
7693	\N	1117	609	253
7694	\N	1117	610	253
7695	\N	1117	614	253
7696	\N	1117	620	253
7697	\N	1117	621	253
7698	\N	1117	607	253
7700	\N	1117	602	253
7702	\N	1117	613	253
7703	\N	1117	615	253
7705	\N	1117	619	253
7707	\N	1117	606	253
7708	\N	1117	624	253
7709	\N	1117	627	253
7710	\N	1117	628	253
7711	\N	1117	631	253
7714	\N	1117	617	253
7715	\N	1117	622	253
7718	\N	1117	605	253
7719	\N	1117	625	253
7721	\N	1117	608	253
7722	\N	1117	629	253
7723	\N	1117	630	253
7699	\N	1119	135	253
7716	\N	1119	623	253
7701	\N	1120	612	253
7748	\N	1146	604	254
7751	\N	1146	626	254
7752	\N	1146	608	254
7724	\N	1147	609	254
7725	\N	1147	610	254
7726	\N	1147	614	254
7727	\N	1147	620	254
7728	\N	1147	621	254
7729	\N	1147	607	254
7730	\N	1147	135	254
7731	\N	1147	602	254
7732	\N	1147	612	254
7733	\N	1147	613	254
7734	\N	1147	615	254
7735	\N	1147	618	254
7736	\N	1147	619	254
7738	\N	1147	606	254
7739	\N	1147	624	254
7740	\N	1147	627	254
7743	\N	1147	611	254
7744	\N	1147	616	254
7745	\N	1147	617	254
7746	\N	1147	622	254
7750	\N	1147	625	254
7754	\N	1147	630	254
7737	\N	1148	603	254
7741	\N	1148	628	254
7742	\N	1148	631	254
7749	\N	1148	605	254
7753	\N	1148	629	254
7747	\N	1145	623	254
7755	\N	1152	609	255
7757	\N	1152	614	255
7759	\N	1152	621	255
7762	\N	1152	602	255
7772	\N	1152	628	255
7774	\N	1152	611	255
7777	\N	1152	622	255
7780	\N	1152	605	255
7782	\N	1152	626	255
7783	\N	1152	608	255
7756	\N	1151	610	255
7758	\N	1151	620	255
7761	\N	1151	135	255
7763	\N	1151	612	255
7767	\N	1151	619	255
7773	\N	1151	631	255
7776	\N	1151	617	255
7781	\N	1151	625	255
7784	\N	1151	629	255
7785	\N	1151	630	255
7764	\N	1150	613	255
7765	\N	1150	615	255
7769	\N	1150	606	255
7770	\N	1150	624	255
7778	\N	1150	623	255
7779	\N	1150	604	255
7760	\N	1149	607	255
7766	\N	1149	618	255
7768	\N	1149	603	255
7771	\N	1149	627	255
7775	\N	1149	616	255
7820	\N	1173	620	257
7826	\N	1173	613	257
7829	\N	1173	619	257
7830	\N	1173	603	257
7831	\N	1173	606	257
7836	\N	1173	611	257
7838	\N	1173	617	257
7840	\N	1173	623	257
7841	\N	1173	604	257
7845	\N	1173	608	257
7817	\N	1176	609	257
7822	\N	1176	607	257
7823	\N	1176	135	257
7827	\N	1176	615	257
7843	\N	1176	625	257
7844	\N	1176	626	257
7818	\N	1174	610	257
7819	\N	1174	614	257
7821	\N	1174	621	257
7824	\N	1174	602	257
7825	\N	1174	612	257
7828	\N	1174	618	257
7832	\N	1174	624	257
7833	\N	1174	627	257
7834	\N	1174	628	257
7835	\N	1174	631	257
7837	\N	1174	616	257
7839	\N	1174	622	257
7842	\N	1174	605	257
7846	\N	1174	629	257
7847	\N	1174	630	257
7848	\N	1180	609	258
7851	\N	1180	620	258
7860	\N	1180	619	258
7862	\N	1180	606	258
7863	\N	1180	624	258
7864	\N	1180	627	258
7865	\N	1180	628	258
7868	\N	1180	616	258
7869	\N	1180	617	258
7871	\N	1180	623	258
7872	\N	1180	604	258
7873	\N	1180	605	258
7875	\N	1180	626	258
7876	\N	1180	608	258
7877	\N	1180	629	258
7852	\N	1177	621	258
7855	\N	1177	602	258
7867	\N	1177	611	258
7850	\N	1178	614	258
7854	\N	1178	135	258
7856	\N	1178	612	258
7866	\N	1178	631	258
7874	\N	1178	625	258
7878	\N	1178	630	258
7849	\N	1179	610	258
7853	\N	1179	607	258
7857	\N	1179	613	258
7858	\N	1179	615	258
7859	\N	1179	618	258
7861	\N	1179	603	258
7870	\N	1179	622	258
7946	\N	1191	607	261
7947	\N	1191	135	261
7952	\N	1191	618	261
7955	\N	1191	606	261
7964	\N	1191	623	261
7965	\N	1191	604	261
7942	\N	1189	610	261
7943	\N	1189	614	261
7944	\N	1189	620	261
7945	\N	1189	621	261
7948	\N	1189	602	261
7951	\N	1189	615	261
7953	\N	1189	619	261
7958	\N	1189	628	261
7960	\N	1189	611	261
7962	\N	1189	617	261
7963	\N	1189	622	261
7966	\N	1189	605	261
7967	\N	1189	625	261
7968	\N	1189	626	261
7970	\N	1189	629	261
7971	\N	1189	630	261
7954	\N	1190	603	261
7956	\N	1190	624	261
7959	\N	1190	631	261
7941	\N	1192	609	261
7949	\N	1192	612	261
7950	\N	1192	613	261
7957	\N	1192	627	261
7961	\N	1192	616	261
7969	\N	1192	608	261
7972	\N	1196	609	262
7981	\N	1196	613	262
7986	\N	1196	606	262
7990	\N	1196	631	262
7996	\N	1196	604	262
7999	\N	1196	626	262
7973	\N	1195	610	262
7976	\N	1195	621	262
7978	\N	1195	135	262
7979	\N	1195	602	262
7982	\N	1195	615	262
7983	\N	1195	618	262
7984	\N	1195	619	262
7985	\N	1195	603	262
7989	\N	1195	628	262
7991	\N	1195	611	262
7993	\N	1195	617	262
7994	\N	1195	622	262
7997	\N	1195	605	262
7998	\N	1195	625	262
8001	\N	1195	629	262
8002	\N	1195	630	262
7975	\N	1194	620	262
7974	\N	1193	614	262
7977	\N	1193	607	262
7980	\N	1193	612	262
7987	\N	1193	624	262
7988	\N	1193	627	262
7992	\N	1193	616	262
7995	\N	1193	623	262
8000	\N	1193	608	262
8003	\N	1200	609	263
8004	\N	1200	610	263
8005	\N	1200	614	263
8007	\N	1200	621	263
8009	\N	1200	135	263
8010	\N	1200	602	263
8011	\N	1200	612	263
8013	\N	1200	615	263
8014	\N	1200	618	263
8015	\N	1200	619	263
8020	\N	1200	628	263
8021	\N	1200	631	263
8023	\N	1200	616	263
8024	\N	1200	617	263
8026	\N	1200	623	263
8027	\N	1200	604	263
8031	\N	1200	608	263
8032	\N	1200	629	263
8033	\N	1200	630	263
8008	\N	1198	607	263
8012	\N	1198	613	263
8018	\N	1198	624	263
8019	\N	1198	627	263
8022	\N	1198	611	263
8028	\N	1198	605	263
8030	\N	1198	626	263
8006	\N	1197	620	263
8016	\N	1197	603	263
8017	\N	1197	606	263
8025	\N	1197	622	263
8029	\N	1197	625	263
8088	\N	1156	623	265
8092	\N	1156	626	265
8090	\N	1154	605	265
8065	\N	1153	609	265
8066	\N	1153	610	265
8067	\N	1153	614	265
8068	\N	1153	620	265
8070	\N	1153	607	265
8071	\N	1153	135	265
8073	\N	1153	612	265
8074	\N	1153	613	265
8075	\N	1153	615	265
8076	\N	1153	618	265
8080	\N	1153	624	265
8082	\N	1153	628	265
8083	\N	1153	631	265
8085	\N	1153	616	265
8086	\N	1153	617	265
8087	\N	1153	622	265
8089	\N	1153	604	265
8091	\N	1153	625	265
8093	\N	1153	608	265
8094	\N	1153	629	265
8095	\N	1153	630	265
8069	\N	1155	621	265
8072	\N	1155	602	265
8077	\N	1155	619	265
8078	\N	1155	603	265
8079	\N	1155	606	265
8081	\N	1155	627	265
8084	\N	1155	611	265
8096	\N	1158	609	266
8097	\N	1158	610	266
8098	\N	1158	614	266
8099	\N	1158	620	266
8100	\N	1158	621	266
8101	\N	1158	607	266
8103	\N	1158	602	266
8107	\N	1158	618	266
8110	\N	1158	606	266
8112	\N	1158	627	266
8114	\N	1158	631	266
8116	\N	1158	616	266
8117	\N	1158	617	266
8118	\N	1158	622	266
8119	\N	1158	623	266
8122	\N	1158	625	266
8123	\N	1158	626	266
8124	\N	1158	608	266
8125	\N	1158	629	266
8126	\N	1158	630	266
8102	\N	1157	135	266
8104	\N	1157	612	266
8106	\N	1157	615	266
8108	\N	1157	619	266
8111	\N	1157	624	266
8113	\N	1157	628	266
8115	\N	1157	611	266
8121	\N	1157	605	266
8105	\N	1160	613	266
8109	\N	1160	603	266
8120	\N	1160	604	266
8144	\N	1162	628	267
8146	\N	1162	611	267
8153	\N	1162	625	267
8150	\N	1161	623	267
8152	\N	1161	605	267
8154	\N	1161	626	267
8141	\N	1164	606	267
8142	\N	1164	624	267
8143	\N	1164	627	267
8127	\N	1163	609	267
8128	\N	1163	610	267
8129	\N	1163	614	267
8130	\N	1163	620	267
8131	\N	1163	621	267
8132	\N	1163	607	267
8133	\N	1163	135	267
8134	\N	1163	602	267
8135	\N	1163	612	267
8136	\N	1163	613	267
8137	\N	1163	615	267
8138	\N	1163	618	267
8139	\N	1163	619	267
8140	\N	1163	603	267
8145	\N	1163	631	267
8147	\N	1163	616	267
8148	\N	1163	617	267
8149	\N	1163	622	267
8151	\N	1163	604	267
8155	\N	1163	608	267
8156	\N	1163	629	267
8157	\N	1163	630	267
8162	\N	1171	621	268
8172	\N	1171	606	268
8175	\N	1171	628	268
8159	\N	1172	610	268
8160	\N	1172	614	268
8161	\N	1172	620	268
8163	\N	1172	607	268
8164	\N	1172	135	268
8166	\N	1172	612	268
8168	\N	1172	615	268
8169	\N	1172	618	268
8170	\N	1172	619	268
8176	\N	1172	631	268
8177	\N	1172	611	268
8179	\N	1172	617	268
8180	\N	1172	622	268
8181	\N	1172	623	268
8183	\N	1172	605	268
8184	\N	1172	625	268
8187	\N	1172	629	268
8188	\N	1172	630	268
8167	\N	1170	613	268
8171	\N	1170	603	268
8173	\N	1170	624	268
8178	\N	1170	616	268
8186	\N	1170	608	268
8158	\N	1169	609	268
8165	\N	1169	602	268
8174	\N	1169	627	268
8182	\N	1169	604	268
8185	\N	1169	626	268
8189	\N	1088	609	269
8190	\N	1088	610	269
8191	\N	1088	614	269
8193	\N	1088	621	269
8196	\N	1088	602	269
8198	\N	1088	613	269
8200	\N	1088	618	269
8201	\N	1088	619	269
8206	\N	1088	628	269
8207	\N	1088	631	269
8209	\N	1088	616	269
8210	\N	1088	617	269
8211	\N	1088	622	269
8212	\N	1088	623	269
8214	\N	1088	605	269
8215	\N	1088	625	269
8216	\N	1088	626	269
8219	\N	1088	630	269
8192	\N	1086	620	269
8194	\N	1086	607	269
8204	\N	1086	624	269
8208	\N	1086	611	269
8218	\N	1086	629	269
8199	\N	1085	615	269
8202	\N	1085	603	269
8203	\N	1085	606	269
8205	\N	1085	627	269
8213	\N	1085	604	269
8195	\N	1087	135	269
8197	\N	1087	612	269
8217	\N	1087	608	269
8222	\N	1090	614	270
8223	\N	1090	620	270
8224	\N	1090	621	270
8226	\N	1090	135	270
8235	\N	1090	624	270
8243	\N	1090	623	270
8244	\N	1090	604	270
8247	\N	1090	626	270
8248	\N	1090	608	270
8249	\N	1090	629	270
8220	\N	1092	609	270
8227	\N	1092	602	270
8228	\N	1092	612	270
8230	\N	1092	615	270
8231	\N	1092	618	270
8232	\N	1092	619	270
8234	\N	1092	606	270
8236	\N	1092	627	270
8237	\N	1092	628	270
8238	\N	1092	631	270
8239	\N	1092	611	270
8240	\N	1092	616	270
8241	\N	1092	617	270
8246	\N	1092	625	270
8034	\N	3583	609	264
8044	\N	3583	615	264
8048	\N	3583	606	264
8054	\N	3583	616	264
8059	\N	3583	605	264
8061	\N	3581	626	264
8050	\N	3582	627	264
8055	\N	3582	617	264
8062	\N	3582	608	264
8035	\N	3584	610	264
8036	\N	3584	614	264
8037	\N	3584	620	264
8038	\N	3584	621	264
8039	\N	3584	607	264
8040	\N	3584	135	264
8041	\N	3584	602	264
8042	\N	3584	612	264
8043	\N	3584	613	264
8045	\N	3584	618	264
8046	\N	3584	619	264
8047	\N	3584	603	264
8049	\N	3584	624	264
8051	\N	3584	628	264
8052	\N	3584	631	264
8053	\N	3584	611	264
8056	\N	3584	622	264
8057	\N	3584	623	264
8058	\N	3584	604	264
8060	\N	3584	625	264
8063	\N	3584	629	264
8064	\N	3584	630	264
8905	\N	3438	640	292
8906	\N	3438	648	292
8909	\N	3438	657	292
8911	\N	3438	636	292
8915	\N	3438	637	292
8925	\N	3438	656	292
8927	\N	3438	635	292
7789	\N	3438	620	256
7795	\N	3438	613	256
7803	\N	3438	628	256
7806	\N	3438	616	256
8902	\N	3439	645	292
8903	\N	3439	646	292
8904	\N	3439	639	292
8907	\N	3439	651	292
8910	\N	3439	641	292
8912	\N	3439	647	292
8913	\N	3439	633	292
8914	\N	3439	653	292
8916	\N	3439	642	292
8917	\N	3439	644	292
8918	\N	3439	659	292
8919	\N	3439	632	292
8920	\N	3439	649	292
8921	\N	3439	634	292
8922	\N	3439	650	292
8923	\N	3439	652	292
8924	\N	3439	655	292
8926	\N	3439	658	292
8928	\N	3439	136	292
8929	\N	3439	643	292
8932	\N	3439	661	292
7786	\N	3439	609	256
7787	\N	3439	610	256
7788	\N	3439	614	256
7790	\N	3439	621	256
7791	\N	3439	607	256
7792	\N	3439	135	256
7793	\N	3439	602	256
7794	\N	3439	612	256
7796	\N	3439	615	256
7797	\N	3439	618	256
7798	\N	3439	619	256
7799	\N	3439	603	256
7801	\N	3439	624	256
7804	\N	3439	631	256
7807	\N	3439	617	256
7808	\N	3439	622	256
7809	\N	3439	623	256
7810	\N	3439	604	256
7811	\N	3439	605	256
7812	\N	3439	625	256
7813	\N	3439	626	256
7814	\N	3439	608	256
7815	\N	3439	629	256
7816	\N	3439	630	256
8465	\N	4800	638	277
7571	\N	4800	614	249
7585	\N	4800	627	249
8443	\N	4799	654	277
8450	\N	4799	637	277
8453	\N	4799	659	277
8454	\N	4799	632	277
8455	\N	4799	649	277
8456	\N	4799	634	277
8461	\N	4799	658	277
8462	\N	4799	635	277
8463	\N	4799	136	277
8464	\N	4799	643	277
8467	\N	4799	661	277
7569	\N	4799	609	249
7570	\N	4799	610	249
7579	\N	4799	615	249
7580	\N	4799	618	249
7587	\N	4799	631	249
7588	\N	4799	611	249
7591	\N	4799	622	249
7592	\N	4799	623	249
7593	\N	4799	604	249
7595	\N	4799	625	249
7597	\N	4799	608	249
7598	\N	4799	629	249
7599	\N	4799	630	249
8437	\N	4797	645	277
8438	\N	4797	646	277
8439	\N	4797	639	277
8444	\N	4797	657	277
8445	\N	4797	641	277
8446	\N	4797	636	277
8460	\N	4797	656	277
8466	\N	4797	660	277
7573	\N	4797	621	249
7575	\N	4797	135	249
7576	\N	4797	602	249
7583	\N	4797	606	249
7586	\N	4797	628	249
7594	\N	4797	605	249
8440	\N	4798	640	277
8441	\N	4798	648	277
8442	\N	4798	651	277
8447	\N	4798	647	277
8448	\N	4798	633	277
8449	\N	4798	653	277
8451	\N	4798	642	277
8452	\N	4798	644	277
8457	\N	4798	650	277
9187	\N	1898	671	421
9188	\N	1898	148	421
9191	\N	1898	672	421
9195	\N	1898	673	421
9197	\N	1898	674	421
9199	\N	1898	675	421
9201	\N	1898	142	421
9202	\N	1898	676	421
9205	\N	1898	145	421
9209	\N	1898	677	421
9214	\N	1898	678	421
9203	\N	1900	146	421
9206	\N	1900	141	421
9181	\N	1897	140	421
9182	\N	1897	662	421
9183	\N	1897	149	421
9184	\N	1897	138	421
9186	\N	1897	663	421
9190	\N	1897	664	421
9192	\N	1897	139	421
9193	\N	1897	150	421
9194	\N	1897	143	421
9198	\N	1897	665	421
9200	\N	1897	666	421
9204	\N	1897	667	421
9207	\N	1897	668	421
9208	\N	1897	144	421
9210	\N	1897	669	421
9211	\N	1897	670	421
9212	\N	1897	147	421
9185	\N	1899	679	421
9189	\N	1899	680	421
9213	\N	1899	681	421
9216	\N	1904	662	422
9220	\N	1904	663	422
9221	\N	1904	671	422
9222	\N	1904	148	422
9223	\N	1904	680	422
9225	\N	1904	672	422
9228	\N	1904	143	422
9230	\N	1904	137	422
9233	\N	1904	675	422
9235	\N	1904	142	422
9239	\N	1904	145	422
9242	\N	1904	144	422
9243	\N	1904	677	422
9244	\N	1904	669	422
9245	\N	1904	670	422
9248	\N	1904	678	422
9240	\N	1902	141	422
9215	\N	1903	140	422
9217	\N	1903	149	422
9219	\N	1903	679	422
9224	\N	1903	664	422
9227	\N	1903	150	422
9229	\N	1903	673	422
9231	\N	1903	674	422
9232	\N	1903	665	422
9234	\N	1903	666	422
9236	\N	1903	676	422
9237	\N	1903	146	422
9241	\N	1903	668	422
9246	\N	1903	147	422
9247	\N	1903	681	422
9238	\N	1901	667	422
9274	\N	1905	141	423
9273	\N	1906	145	423
9281	\N	1906	681	423
9266	\N	1908	665	423
9271	\N	1908	146	423
9272	\N	1908	667	423
9275	\N	1908	668	423
9249	\N	1907	140	423
9250	\N	1907	662	423
9251	\N	1907	149	423
9252	\N	1907	138	423
9253	\N	1907	679	423
9254	\N	1907	663	423
9255	\N	1907	671	423
9256	\N	1907	148	423
9257	\N	1907	680	423
9258	\N	1907	664	423
9259	\N	1907	672	423
9260	\N	1907	139	423
9261	\N	1907	150	423
9262	\N	1907	143	423
9263	\N	1907	673	423
9264	\N	1907	137	423
9265	\N	1907	674	423
9267	\N	1907	675	423
9268	\N	1907	666	423
9269	\N	1907	142	423
9270	\N	1907	676	423
9276	\N	1907	144	423
9277	\N	1907	677	423
9278	\N	1907	669	423
9279	\N	1907	670	423
9280	\N	1907	147	423
9282	\N	1907	678	423
9286	\N	2788	138	424
9287	\N	2788	679	424
9307	\N	2788	145	424
9308	\N	2788	141	424
9283	\N	2785	140	424
9284	\N	2785	662	424
9285	\N	2785	149	424
9355	\N	1919	679	426
9356	\N	1919	663	426
9362	\N	1919	139	426
9365	\N	1919	673	426
9366	\N	1919	137	426
9369	\N	1919	675	426
9375	\N	1919	145	426
9376	\N	1919	141	426
9381	\N	1919	670	426
9383	\N	1919	681	426
9351	\N	1920	140	426
9357	\N	1920	671	426
9364	\N	1920	143	426
9372	\N	1920	676	426
9374	\N	1920	667	426
9379	\N	1920	677	426
9380	\N	1920	669	426
9352	\N	1918	662	426
9354	\N	1918	138	426
9358	\N	1918	148	426
9359	\N	1918	680	426
9360	\N	1918	664	426
9361	\N	1918	672	426
9367	\N	1918	674	426
9368	\N	1918	665	426
9370	\N	1918	666	426
9371	\N	1918	142	426
9373	\N	1918	146	426
9377	\N	1918	668	426
9378	\N	1918	144	426
9384	\N	1918	678	426
9353	\N	1917	149	426
9363	\N	1917	150	426
9382	\N	1917	147	426
9397	\N	1838	150	427
9406	\N	1838	676	427
9409	\N	1838	145	427
9416	\N	1838	147	427
9387	\N	1839	149	427
9389	\N	1839	679	427
9398	\N	1839	143	427
9417	\N	1839	681	427
9396	\N	1840	139	427
9400	\N	1840	137	427
9407	\N	1840	146	427
9415	\N	1840	670	427
9385	\N	1837	140	427
9386	\N	1837	662	427
9390	\N	1837	663	427
9391	\N	1837	671	427
9392	\N	1837	148	427
9393	\N	1837	680	427
9394	\N	1837	664	427
9395	\N	1837	672	427
9399	\N	1837	673	427
9401	\N	1837	674	427
9402	\N	1837	665	427
9403	\N	1837	675	427
9404	\N	1837	666	427
9408	\N	1837	667	427
9410	\N	1837	141	427
9411	\N	1837	668	427
9412	\N	1837	144	427
9413	\N	1837	677	427
9414	\N	1837	669	427
9418	\N	1837	678	427
9422	\N	1842	138	428
9423	\N	1842	679	428
9424	\N	1842	663	428
9425	\N	1842	671	428
9427	\N	1842	680	428
9428	\N	1842	664	428
9430	\N	1842	139	428
9435	\N	1842	674	428
9437	\N	1842	675	428
9440	\N	1842	676	428
9442	\N	1842	667	428
9444	\N	1842	141	428
9445	\N	1842	668	428
9448	\N	1842	669	428
9449	\N	1842	670	428
9451	\N	1842	681	428
9420	\N	1843	662	428
9421	\N	1843	149	428
9426	\N	1843	148	428
9429	\N	1843	672	428
9431	\N	1843	150	428
9433	\N	1843	673	428
9434	\N	1843	137	428
9436	\N	1843	665	428
9438	\N	1843	666	428
9439	\N	1843	142	428
9441	\N	1843	146	428
9443	\N	1843	145	428
9446	\N	1843	144	428
9447	\N	1843	677	428
9450	\N	1843	147	428
9452	\N	1843	678	428
9458	\N	1846	663	429
9463	\N	1846	672	429
9473	\N	1846	142	429
9476	\N	1846	667	429
9477	\N	1846	145	429
9485	\N	1846	681	429
9456	\N	1845	138	429
9460	\N	1845	148	429
9461	\N	1845	680	429
9462	\N	1845	664	429
9469	\N	1845	674	429
9479	\N	1845	668	429
9481	\N	1845	677	429
9486	\N	1845	678	429
9453	\N	1847	140	429
9457	\N	1847	679	429
9467	\N	1847	673	429
9471	\N	1847	675	429
9474	\N	1847	676	429
9482	\N	1847	669	429
9454	\N	1848	662	429
9455	\N	1848	149	429
9459	\N	1848	671	429
9464	\N	1848	139	429
9465	\N	1848	150	429
9466	\N	1848	143	429
9470	\N	1848	665	429
9472	\N	1848	666	429
9475	\N	1848	146	429
9478	\N	1848	141	429
9483	\N	1848	670	429
9484	\N	1848	147	429
9490	\N	1851	138	430
9492	\N	1851	663	430
9497	\N	1851	672	430
9498	\N	1851	139	430
9510	\N	1851	667	430
9513	\N	1851	668	430
9516	\N	1851	669	430
9518	\N	1851	147	430
9520	\N	1851	678	430
9488	\N	1852	662	430
9493	\N	1852	671	430
9499	\N	1852	150	430
9501	\N	1852	673	430
9504	\N	1852	665	430
9505	\N	1852	675	430
9506	\N	1852	666	430
9511	\N	1852	145	430
9512	\N	1852	141	430
9514	\N	1852	144	430
9517	\N	1852	670	430
9487	\N	1850	140	430
9489	\N	1850	149	430
9491	\N	1850	679	430
9494	\N	1850	148	430
9495	\N	1850	680	430
9496	\N	1850	664	430
9502	\N	1850	137	430
9503	\N	1850	674	430
9507	\N	1850	142	430
9508	\N	1850	676	430
9509	\N	1850	146	430
9515	\N	1850	677	430
9519	\N	1850	681	430
9543	\N	1804	146	431
9522	\N	1802	662	431
9526	\N	1802	663	431
9528	\N	1802	148	431
9539	\N	1802	675	431
9542	\N	1802	676	431
9546	\N	1802	141	431
9545	\N	1803	145	431
9521	\N	1801	140	431
9523	\N	1801	149	431
9524	\N	1801	138	431
9525	\N	1801	679	431
9527	\N	1801	671	431
9529	\N	1801	680	431
9530	\N	1801	664	431
9531	\N	1801	672	431
9532	\N	1801	139	431
9533	\N	1801	150	431
9534	\N	1801	143	431
9535	\N	1801	673	431
9536	\N	1801	137	431
9537	\N	1801	674	431
9538	\N	1801	665	431
9540	\N	1801	666	431
9541	\N	1801	142	431
9544	\N	1801	667	431
9547	\N	1801	668	431
9548	\N	1801	144	431
9549	\N	1801	677	431
9550	\N	1801	669	431
9551	\N	1801	670	431
9552	\N	1801	147	431
9553	\N	1801	681	431
9554	\N	1801	678	431
9555	\N	1805	140	432
9579	\N	1805	145	432
9580	\N	1805	141	432
9556	\N	1807	662	432
9557	\N	1807	149	432
9558	\N	1807	138	432
9559	\N	1807	679	432
9560	\N	1807	663	432
9561	\N	1807	671	432
9562	\N	1807	148	432
9564	\N	1807	664	432
9565	\N	1807	672	432
9566	\N	1807	139	432
9567	\N	1807	150	432
9568	\N	1807	143	432
9569	\N	1807	673	432
9570	\N	1807	137	432
9571	\N	1807	674	432
9572	\N	1807	665	432
9573	\N	1807	675	432
9574	\N	1807	666	432
9575	\N	1807	142	432
9576	\N	1807	676	432
9577	\N	1807	146	432
9578	\N	1807	667	432
9581	\N	1807	668	432
9582	\N	1807	144	432
9583	\N	1807	677	432
9584	\N	1807	669	432
9585	\N	1807	670	432
9587	\N	1807	681	432
9588	\N	1807	678	432
9563	\N	1808	680	432
9586	\N	1808	147	432
9614	\N	1823	141	433
9589	\N	1824	140	433
9590	\N	1824	662	433
9591	\N	1824	149	433
9592	\N	1824	138	433
9593	\N	1824	679	433
9594	\N	1824	663	433
9595	\N	1824	671	433
9596	\N	1824	148	433
9597	\N	1824	680	433
9598	\N	1824	664	433
9599	\N	1824	672	433
9600	\N	1824	139	433
9601	\N	1824	150	433
9602	\N	1824	143	433
9603	\N	1824	673	433
9604	\N	1824	137	433
9605	\N	1824	674	433
9606	\N	1824	665	433
9607	\N	1824	675	433
9608	\N	1824	666	433
9609	\N	1824	142	433
9610	\N	1824	676	433
9611	\N	1824	146	433
9612	\N	1824	667	433
9615	\N	1824	668	433
9616	\N	1824	144	433
9617	\N	1824	677	433
9618	\N	1824	669	433
9619	\N	1824	670	433
9620	\N	1824	147	433
9621	\N	1824	681	433
9622	\N	1824	678	433
9729	\N	1853	679	437
9754	\N	1853	669	437
9750	\N	1856	141	437
9757	\N	1856	681	437
9725	\N	1855	140	437
9726	\N	1855	662	437
9728	\N	1855	138	437
9730	\N	1855	663	437
9731	\N	1855	671	437
9732	\N	1855	148	437
9733	\N	1855	680	437
9734	\N	1855	664	437
9735	\N	1855	672	437
9736	\N	1855	139	437
9737	\N	1855	150	437
9739	\N	1855	673	437
9741	\N	1855	674	437
9742	\N	1855	665	437
9743	\N	1855	675	437
9745	\N	1855	142	437
9746	\N	1855	676	437
9748	\N	1855	667	437
9751	\N	1855	668	437
9752	\N	1855	144	437
9753	\N	1855	677	437
9755	\N	1855	670	437
9756	\N	1855	147	437
9758	\N	1855	678	437
9727	\N	1854	149	437
9744	\N	1854	666	437
9747	\N	1854	146	437
9749	\N	1854	145	437
9767	\N	1858	680	438
9781	\N	1858	146	438
1144	\N	1858	26	40
1147	\N	1858	2	40
1150	\N	1858	16	40
1161	\N	1858	41	40
1170	\N	1858	45	40
1173	\N	1858	13	40
1175	\N	1858	474	40
1177	\N	1858	42	40
1178	\N	1858	19	40
1181	\N	1858	20	40
1183	\N	1858	38	40
1185	\N	1858	21	40
1186	\N	1858	486	40
1189	\N	1858	22	40
1191	\N	1858	8	40
1199	\N	1858	11	40
9759	\N	1860	140	438
9763	\N	1860	679	438
9765	\N	1860	671	438
9769	\N	1860	672	438
9773	\N	1860	673	438
9775	\N	1860	674	438
9779	\N	1860	142	438
9782	\N	1860	667	438
9785	\N	1860	668	438
9786	\N	1860	144	438
9788	\N	1860	669	438
9789	\N	1860	670	438
9792	\N	1860	678	438
1141	\N	1860	475	40
1142	\N	1860	1	40
1143	\N	1860	482	40
1146	\N	1860	483	40
1153	\N	1860	472	40
1155	\N	1860	3	40
1167	\N	1860	478	40
1169	\N	1860	479	40
9760	\N	1859	662	438
9761	\N	1859	149	438
9762	\N	1859	138	438
9764	\N	1859	663	438
9766	\N	1859	148	438
9768	\N	1859	664	438
9772	\N	1859	143	438
9774	\N	1859	137	438
9776	\N	1859	665	438
9777	\N	1859	675	438
9778	\N	1859	666	438
9780	\N	1859	676	438
9783	\N	1859	145	438
9784	\N	1859	141	438
9787	\N	1859	677	438
9790	\N	1859	147	438
9791	\N	1859	681	438
1145	\N	1859	35	40
1148	\N	1859	476	40
1149	\N	1859	29	40
1151	\N	1859	473	40
1152	\N	1859	36	40
1154	\N	1859	30	40
1156	\N	1859	17	40
1157	\N	1859	37	40
1158	\N	1859	4	40
1159	\N	1859	477	40
1160	\N	1859	18	40
1163	\N	1859	484	40
1164	\N	1859	12	40
1165	\N	1859	5	40
1168	\N	1859	6	40
1171	\N	1859	485	40
1172	\N	1859	7	40
1174	\N	1859	31	40
1188	\N	1859	43	40
1193	\N	1859	28	40
1197	\N	1859	33	40
9771	\N	1857	150	438
1162	\N	1857	24	40
1166	\N	1857	25	40
1176	\N	1857	480	40
1179	\N	1857	40	40
1180	\N	1857	14	40
1182	\N	1857	27	40
1184	\N	1857	39	40
1187	\N	1857	15	40
1192	\N	1857	481	40
1195	\N	1857	23	40
1198	\N	1857	44	40
1200	\N	1857	34	40
9851	\N	1867	145	440
9859	\N	1867	681	440
9838	\N	1865	139	440
9844	\N	1865	665	440
9852	\N	1865	141	440
9827	\N	1866	140	440
9828	\N	1866	662	440
9829	\N	1866	149	440
9830	\N	1866	138	440
9831	\N	1866	679	440
9832	\N	1866	663	440
9833	\N	1866	671	440
9834	\N	1866	148	440
9835	\N	1866	680	440
9836	\N	1866	664	440
9837	\N	1866	672	440
9839	\N	1866	150	440
9840	\N	1866	143	440
9841	\N	1866	673	440
9842	\N	1866	137	440
9843	\N	1866	674	440
9845	\N	1866	675	440
9846	\N	1866	666	440
9847	\N	1866	142	440
9848	\N	1866	676	440
9849	\N	1866	146	440
9850	\N	1866	667	440
9853	\N	1866	668	440
9854	\N	1866	144	440
9855	\N	1866	677	440
9856	\N	1866	669	440
9857	\N	1866	670	440
9858	\N	1866	147	440
9860	\N	1866	678	440
9991	\N	1882	677	444
9979	\N	1883	674	444
9982	\N	1883	666	444
9983	\N	1883	142	444
9984	\N	1883	676	444
9986	\N	1883	667	444
9987	\N	1883	145	444
9988	\N	1883	141	444
9990	\N	1883	144	444
9992	\N	1883	669	444
9994	\N	1883	147	444
9996	\N	1883	678	444
9972	\N	1881	664	444
9976	\N	1881	143	444
9985	\N	1881	146	444
9995	\N	1881	681	444
9981	\N	1884	675	444
9993	\N	1884	670	444
10009	\N	1810	150	445
10022	\N	1810	141	445
10029	\N	1810	681	445
10030	\N	1810	678	445
9999	\N	1809	149	445
10002	\N	1809	663	445
10004	\N	1809	148	445
10008	\N	1809	139	445
10010	\N	1809	143	445
10011	\N	1809	673	445
10017	\N	1809	142	445
10027	\N	1809	670	445
10018	\N	1811	676	445
10021	\N	1811	145	445
9997	\N	1812	140	445
9998	\N	1812	662	445
10000	\N	1812	138	445
10001	\N	1812	679	445
10003	\N	1812	671	445
10005	\N	1812	680	445
10006	\N	1812	664	445
10007	\N	1812	672	445
10013	\N	1812	674	445
10014	\N	1812	665	445
10015	\N	1812	675	445
10016	\N	1812	666	445
10019	\N	1812	146	445
10020	\N	1812	667	445
10023	\N	1812	668	445
10024	\N	1812	144	445
10025	\N	1812	677	445
10026	\N	1812	669	445
10028	\N	1812	147	445
10049	\N	1813	675	446
10062	\N	1813	147	446
10063	\N	1813	681	446
10047	\N	1814	674	446
10031	\N	1816	140	446
10032	\N	1816	662	446
10033	\N	1816	149	446
10034	\N	1816	138	446
10035	\N	1816	679	446
10036	\N	1816	663	446
10037	\N	1816	671	446
10038	\N	1816	148	446
10039	\N	1816	680	446
10040	\N	1816	664	446
10041	\N	1816	672	446
10042	\N	1816	139	446
10043	\N	1816	150	446
10044	\N	1816	143	446
10045	\N	1816	673	446
10046	\N	1816	137	446
10048	\N	1816	665	446
10050	\N	1816	666	446
10051	\N	1816	142	446
10052	\N	1816	676	446
10053	\N	1816	146	446
10054	\N	1816	667	446
10055	\N	1816	145	446
10056	\N	1816	141	446
10057	\N	1816	668	446
10059	\N	1816	677	446
10060	\N	1816	669	446
10061	\N	1816	670	446
10064	\N	1816	678	446
10098	\N	1818	678	447
10087	\N	1820	146	447
10065	\N	1817	140	447
10066	\N	1817	662	447
10067	\N	1817	149	447
10069	\N	1817	679	447
10070	\N	1817	663	447
10071	\N	1817	671	447
10072	\N	1817	148	447
10073	\N	1817	680	447
10074	\N	1817	664	447
10075	\N	1817	672	447
10076	\N	1817	139	447
10077	\N	1817	150	447
10079	\N	1817	673	447
10080	\N	1817	137	447
10081	\N	1817	674	447
10088	\N	1817	667	447
10089	\N	1817	145	447
10092	\N	1817	144	447
10093	\N	1817	677	447
10095	\N	1817	670	447
10096	\N	1817	147	447
10097	\N	1817	681	447
10068	\N	1819	138	447
10082	\N	1819	665	447
10083	\N	1819	675	447
10084	\N	1819	666	447
10085	\N	1819	142	447
10086	\N	1819	676	447
10091	\N	1819	668	447
10094	\N	1819	669	447
10099	\N	1885	140	448
10100	\N	1885	662	448
10103	\N	1885	679	448
10104	\N	1885	663	448
10107	\N	1885	680	448
10109	\N	1885	672	448
10114	\N	1885	137	448
10115	\N	1885	674	448
10116	\N	1885	665	448
10117	\N	1885	675	448
10127	\N	1885	677	448
10131	\N	1885	681	448
10132	\N	1885	678	448
10105	\N	1887	671	448
10113	\N	1887	673	448
10102	\N	1886	138	448
10128	\N	1886	669	448
10101	\N	1888	149	448
10108	\N	1888	664	448
10110	\N	1888	139	448
10112	\N	1888	143	448
10118	\N	1888	666	448
10120	\N	1888	676	448
10121	\N	1888	146	448
10122	\N	1888	667	448
10123	\N	1888	145	448
10125	\N	1888	668	448
10129	\N	1888	670	448
10130	\N	1888	147	448
10133	\N	1890	140	449
10137	\N	1890	679	449
10144	\N	1890	139	449
10150	\N	1890	665	449
10151	\N	1890	675	449
10153	\N	1890	142	449
10155	\N	1890	146	449
10135	\N	1889	149	449
10136	\N	1889	138	449
10138	\N	1889	663	449
10140	\N	1889	148	449
10141	\N	1889	680	449
10142	\N	1889	664	449
10146	\N	1889	143	449
10147	\N	1889	673	449
10149	\N	1889	674	449
10154	\N	1889	676	449
10157	\N	1889	145	449
10161	\N	1889	677	449
10162	\N	1889	669	449
10163	\N	1889	670	449
10165	\N	1889	681	449
10134	\N	1891	662	449
10139	\N	1891	671	449
10143	\N	1891	672	449
10152	\N	1891	666	449
10156	\N	1891	667	449
10159	\N	1891	668	449
10160	\N	1891	144	449
10164	\N	1891	147	449
10166	\N	1891	678	449
10158	\N	1892	141	449
10375	\N	4325	165	457
10376	\N	4325	152	457
10381	\N	4325	159	457
10383	\N	4325	155	457
10393	\N	4325	688	457
9628	\N	4325	663	434
9640	\N	4325	665	434
9643	\N	4325	142	434
9644	\N	4325	676	434
9645	\N	4325	146	434
9655	\N	4325	681	434
10370	\N	4327	682	457
10371	\N	4327	160	457
10372	\N	4327	683	457
10373	\N	4327	684	457
10377	\N	4327	685	457
10378	\N	4327	158	457
10379	\N	4327	686	457
10380	\N	4327	151	457
10382	\N	4327	690	457
10384	\N	4327	167	457
10385	\N	4327	162	457
10386	\N	4327	691	457
10387	\N	4327	687	457
10388	\N	4327	166	457
10389	\N	4327	153	457
10390	\N	4327	164	457
10391	\N	4327	157	457
10394	\N	4327	689	457
10406	\N	3073	158	458
10422	\N	3073	689	458
9661	\N	3073	679	435
9671	\N	3073	673	435
7270	\N	3073	560	240
7278	\N	3073	547	240
7284	\N	3073	549	240
7286	\N	3073	568	240
7288	\N	3073	570	240
7296	\N	3073	573	240
7304	\N	3073	133	240
7308	\N	3073	578	240
7314	\N	3073	600	240
7318	\N	3073	596	240
4573	\N	3073	512	194
4583	\N	3073	493	194
4591	\N	3073	496	194
4600	\N	3073	529	194
4603	\N	3073	532	194
4612	\N	3073	537	194
4613	\N	3073	130	194
4618	\N	3073	503	194
4623	\N	3073	511	194
10426	\N	4107	682	459
10428	\N	4107	683	459
10429	\N	4107	684	459
10431	\N	4107	165	459
10432	\N	4107	152	459
10434	\N	4107	158	459
10436	\N	4107	151	459
10438	\N	4107	690	459
10439	\N	4107	155	459
10441	\N	4107	162	459
10443	\N	4107	687	459
10449	\N	4107	688	459
10450	\N	4107	689	459
10451	\N	4107	168	459
9692	\N	4107	662	436
9693	\N	4107	149	436
9694	\N	4107	138	436
9703	\N	4107	150	436
9707	\N	4107	674	436
9708	\N	4107	665	436
9710	\N	4107	666	436
9711	\N	4107	142	436
9712	\N	4107	676	436
9714	\N	4107	667	436
9715	\N	4107	145	436
9718	\N	4107	144	436
10425	\N	4105	163	459
10433	\N	4105	685	459
10435	\N	4105	686	459
10437	\N	4105	159	459
10440	\N	4105	167	459
10442	\N	4105	691	459
10444	\N	4105	166	459
10445	\N	4105	153	459
10446	\N	4105	164	459
10452	\N	4105	161	459
9691	\N	4105	140	436
9696	\N	4105	663	436
9697	\N	4105	671	436
9698	\N	4105	148	436
9699	\N	4105	680	436
9700	\N	4105	664	436
9701	\N	4105	672	436
9704	\N	4105	143	436
9705	\N	4105	673	436
9706	\N	4105	137	436
9717	\N	4105	668	436
9720	\N	4105	669	436
9721	\N	4105	670	436
9722	\N	4105	147	436
9723	\N	4105	681	436
9724	\N	4105	678	436
10427	\N	4106	160	459
10448	\N	4106	154	459
9695	\N	4106	679	436
9702	\N	4106	139	436
9716	\N	4106	141	436
9709	\N	4108	675	436
9719	\N	4108	677	436
9801	\N	4349	680	439
9029	\N	4349	640	296
9034	\N	4349	641	296
9036	\N	4349	647	296
9037	\N	4349	633	296
9044	\N	4349	649	296
9053	\N	4349	643	296
7884	\N	4349	607	259
7895	\N	4349	627	259
7903	\N	4349	604	259
7906	\N	4349	626	259
7907	\N	4349	608	259
10678	\N	4955	682	468
10679	\N	4955	160	468
10680	\N	4955	683	468
10681	\N	4955	684	468
10687	\N	4955	686	468
10697	\N	4955	153	468
10704	\N	4955	161	468
9864	\N	4955	138	441
9866	\N	4955	663	441
9878	\N	4955	665	441
9881	\N	4955	142	441
9884	\N	4955	667	441
9886	\N	4955	141	441
9889	\N	4955	677	441
9891	\N	4955	670	441
9863	\N	4953	149	441
10677	\N	4954	163	468
10683	\N	4954	165	468
10685	\N	4954	685	468
10686	\N	4954	158	468
10688	\N	4954	151	468
10689	\N	4954	159	468
10690	\N	4954	690	468
10692	\N	4954	167	468
10694	\N	4954	691	468
10695	\N	4954	687	468
10696	\N	4954	166	468
10698	\N	4954	164	468
10700	\N	4954	154	468
10701	\N	4954	688	468
10703	\N	4954	168	468
9862	\N	4954	662	441
9867	\N	4954	671	441
9868	\N	4954	148	441
9869	\N	4954	680	441
9870	\N	4954	664	441
9871	\N	4954	672	441
9873	\N	4954	150	441
9875	\N	4954	673	441
9876	\N	4954	137	441
9877	\N	4954	674	441
9879	\N	4954	675	441
9880	\N	4954	666	441
9882	\N	4954	676	441
9885	\N	4954	145	441
9887	\N	4954	668	441
9888	\N	4954	144	441
9890	\N	4954	669	441
9893	\N	4954	681	441
9894	\N	4954	678	441
9865	\N	4956	679	441
9883	\N	4956	146	441
10684	\N	4956	152	468
10699	\N	4956	157	468
10702	\N	4956	689	468
11041	\N	2088	173	481
11042	\N	2088	694	481
11044	\N	2088	174	481
11045	\N	2088	695	481
11047	\N	2088	172	481
11049	\N	2088	177	481
11050	\N	2088	178	481
11051	\N	2088	179	481
11052	\N	2088	176	481
11054	\N	2088	696	481
11055	\N	2088	175	481
11057	\N	2088	697	481
11048	\N	2086	180	481
11043	\N	2085	692	481
11046	\N	2085	693	481
11053	\N	2085	171	481
11059	\N	2089	173	482
11060	\N	2089	694	482
11061	\N	2089	692	482
11062	\N	2089	174	482
11063	\N	2089	695	482
11064	\N	2089	693	482
11065	\N	2089	172	482
11066	\N	2089	180	482
11067	\N	2089	177	482
11068	\N	2089	178	482
11069	\N	2089	179	482
11070	\N	2089	176	482
11071	\N	2089	171	482
11072	\N	2089	696	482
11073	\N	2089	175	482
11074	\N	2089	169	482
11075	\N	2089	697	482
11076	\N	2089	170	482
9296	\N	2785	143	424
9297	\N	2785	673	424
9299	\N	2785	674	424
9300	\N	2785	665	424
9301	\N	2785	675	424
9302	\N	2785	666	424
9303	\N	2785	142	424
9304	\N	2785	676	424
9305	\N	2785	146	424
9306	\N	2785	667	424
8361	\N	3022	632	274
8362	\N	3022	649	274
8363	\N	3022	634	274
8372	\N	3022	638	274
7478	\N	3022	614	246
7499	\N	3022	623	246
7504	\N	3022	608	246
10989	\N	3023	684	479
10994	\N	3023	158	479
10999	\N	3023	155	479
9318	\N	3023	662	425
9325	\N	3023	680	425
9326	\N	3023	664	425
9329	\N	3023	150	425
9333	\N	3023	674	425
9340	\N	3023	667	425
9344	\N	3023	144	425
11095	\N	2099	173	484
11096	\N	2099	694	484
11099	\N	2099	695	484
11102	\N	2099	180	484
11109	\N	2099	175	484
11110	\N	2099	169	484
11111	\N	2099	697	484
11097	\N	2100	692	484
11098	\N	2100	174	484
11100	\N	2100	693	484
11101	\N	2100	172	484
11103	\N	2100	177	484
11104	\N	2100	178	484
11105	\N	2100	179	484
11106	\N	2100	176	484
11108	\N	2100	696	484
11112	\N	2100	170	484
11118	\N	2104	693	485
6410	\N	2104	557	226
6422	\N	2104	585	226
6432	\N	2104	568	226
6447	\N	2104	575	226
6453	\N	2104	599	226
4101	\N	2104	494	185
4110	\N	2104	525	185
4125	\N	2104	509	185
4139	\N	2104	504	185
11113	\N	2102	173	485
11114	\N	2102	694	485
11115	\N	2102	692	485
11116	\N	2102	174	485
11117	\N	2102	695	485
11120	\N	2102	180	485
11121	\N	2102	177	485
11122	\N	2102	178	485
11123	\N	2102	179	485
11124	\N	2102	176	485
11125	\N	2102	171	485
11126	\N	2102	696	485
11127	\N	2102	175	485
11128	\N	2102	169	485
11129	\N	2102	697	485
11130	\N	2102	170	485
6406	\N	2102	554	226
6407	\N	2102	582	226
6408	\N	2102	555	226
6409	\N	2102	556	226
6411	\N	2102	545	226
6412	\N	2102	558	226
6413	\N	2102	559	226
6414	\N	2102	546	226
6416	\N	2102	560	226
6418	\N	2102	561	226
6419	\N	2102	562	226
6420	\N	2102	584	226
6421	\N	2102	563	226
6423	\N	2102	564	226
6424	\N	2102	547	226
6425	\N	2102	586	226
6426	\N	2102	565	226
6429	\N	2102	548	226
6431	\N	2102	567	226
6433	\N	2102	569	226
6434	\N	2102	570	226
6435	\N	2102	550	226
6436	\N	2102	588	226
6437	\N	2102	132	226
6438	\N	2102	589	226
6439	\N	2102	571	226
6440	\N	2102	131	226
6441	\N	2102	572	226
6442	\N	2102	573	226
6443	\N	2102	574	226
6444	\N	2102	134	226
6446	\N	2102	551	226
6448	\N	2102	598	226
6451	\N	2102	591	226
6456	\N	2102	592	226
6457	\N	2102	593	226
6458	\N	2102	594	226
6459	\N	2102	579	226
6460	\N	2102	600	226
6461	\N	2102	595	226
6463	\N	2102	553	226
6464	\N	2102	596	226
6465	\N	2102	580	226
6466	\N	2102	581	226
4087	\N	2102	512	185
4088	\N	2102	513	185
4089	\N	2102	514	185
4090	\N	2102	515	185
4091	\N	2102	516	185
4092	\N	2102	517	185
4094	\N	2102	518	185
4096	\N	2102	502	185
4097	\N	2102	493	185
4098	\N	2102	519	185
4099	\N	2102	506	185
4102	\N	2102	520	185
4103	\N	2102	495	185
4106	\N	2102	522	185
4108	\N	2102	524	185
4109	\N	2102	497	185
4111	\N	2102	526	185
4112	\N	2102	527	185
4113	\N	2102	528	185
4114	\N	2102	529	185
4115	\N	2102	530	185
4116	\N	2102	531	185
4117	\N	2102	532	185
4119	\N	2102	534	185
4123	\N	2102	536	185
4124	\N	2102	508	185
4127	\N	2102	130	185
4128	\N	2102	510	185
4129	\N	2102	538	185
4130	\N	2102	500	185
4131	\N	2102	539	185
4132	\N	2102	503	185
4133	\N	2102	540	185
4134	\N	2102	501	185
4136	\N	2102	542	185
4137	\N	2102	511	185
4138	\N	2102	543	185
4140	\N	2102	544	185
6428	\N	2101	566	226
6430	\N	2101	549	226
6445	\N	2101	590	226
6450	\N	2101	133	226
6452	\N	2101	577	226
6455	\N	2101	552	226
4093	\N	2101	492	185
4095	\N	2101	505	185
4100	\N	2101	507	185
4104	\N	2101	521	185
4107	\N	2101	523	185
4121	\N	2101	499	185
4122	\N	2101	535	185
4135	\N	2101	541	185
6415	\N	2103	583	226
6417	\N	2103	597	226
6427	\N	2103	587	226
6449	\N	2103	576	226
6454	\N	2103	578	226
6462	\N	2103	601	226
4105	\N	2103	496	185
4118	\N	2103	533	185
4120	\N	2103	498	185
4126	\N	2103	537	185
11136	\N	2106	693	486
11138	\N	2106	180	486
11147	\N	2105	697	486
11148	\N	2105	170	486
11132	\N	2108	694	486
11135	\N	2108	695	486
11137	\N	2108	172	486
11140	\N	2108	178	486
11143	\N	2108	171	486
11144	\N	2108	696	486
11145	\N	2108	175	486
11133	\N	2107	692	486
11139	\N	2107	177	486
11141	\N	2107	179	486
11142	\N	2107	176	486
11166	\N	2130	170	487
11149	\N	2129	173	487
11150	\N	2129	694	487
11157	\N	2129	177	487
11159	\N	2129	179	487
11161	\N	2129	171	487
11155	\N	2132	172	487
11151	\N	2131	692	487
11152	\N	2131	174	487
11153	\N	2131	695	487
11154	\N	2131	693	487
11156	\N	2131	180	487
11158	\N	2131	178	487
11160	\N	2131	176	487
11162	\N	2131	696	487
11164	\N	2131	169	487
11165	\N	2131	697	487
11198	\N	2137	696	489
11185	\N	2139	173	489
11186	\N	2139	694	489
11187	\N	2139	692	489
11189	\N	2139	695	489
11190	\N	2139	693	489
11191	\N	2139	172	489
11192	\N	2139	180	489
11193	\N	2139	177	489
11194	\N	2139	178	489
11196	\N	2139	176	489
11199	\N	2139	175	489
11200	\N	2139	169	489
11202	\N	2139	170	489
11188	\N	2138	174	489
11195	\N	2138	179	489
11201	\N	2138	697	489
11213	\N	2146	179	490
11211	\N	2145	177	490
11212	\N	2145	178	490
11216	\N	2145	696	490
11220	\N	2145	170	490
11203	\N	2147	173	490
11205	\N	2147	692	490
11206	\N	2147	174	490
11207	\N	2147	695	490
11208	\N	2147	693	490
11209	\N	2147	172	490
11210	\N	2147	180	490
11215	\N	2147	171	490
11218	\N	2147	169	490
11204	\N	2148	694	490
11219	\N	2148	697	490
11228	\N	2152	180	491
11235	\N	2149	175	491
11227	\N	2151	172	491
11231	\N	2151	179	491
11221	\N	2150	173	491
11222	\N	2150	694	491
11223	\N	2150	692	491
11224	\N	2150	174	491
11225	\N	2150	695	491
11226	\N	2150	693	491
11229	\N	2150	177	491
11230	\N	2150	178	491
11232	\N	2150	176	491
11233	\N	2150	171	491
11234	\N	2150	696	491
11236	\N	2150	169	491
11237	\N	2150	697	491
11238	\N	2150	170	491
11291	\N	2141	697	494
11292	\N	2141	170	494
11286	\N	2143	176	494
11287	\N	2143	171	494
11288	\N	2143	696	494
11289	\N	2143	175	494
11290	\N	2143	169	494
11293	\N	2111	173	495
11294	\N	2111	694	495
11295	\N	2111	692	495
11296	\N	2111	174	495
11297	\N	2111	695	495
11298	\N	2111	693	495
11300	\N	2111	180	495
11302	\N	2111	178	495
11304	\N	2111	176	495
11306	\N	2111	696	495
11308	\N	2111	169	495
11309	\N	2111	697	495
11310	\N	2111	170	495
11299	\N	2112	172	495
11305	\N	2112	171	495
11301	\N	2109	177	495
11303	\N	2109	179	495
11307	\N	2109	175	495
11347	\N	2122	173	498
11354	\N	2122	180	498
11357	\N	2122	179	498
11360	\N	2122	696	498
11361	\N	2122	175	498
11364	\N	2122	170	498
11352	\N	2121	693	498
11348	\N	2124	694	498
11353	\N	2124	172	498
11349	\N	2123	692	498
11350	\N	2123	174	498
11351	\N	2123	695	498
11355	\N	2123	177	498
11356	\N	2123	178	498
11358	\N	2123	176	498
11362	\N	2123	169	498
11363	\N	2123	697	498
11368	\N	2128	174	499
11372	\N	2128	180	499
11373	\N	2128	177	499
11376	\N	2128	176	499
11379	\N	2128	175	499
11380	\N	2128	169	499
11381	\N	2128	697	499
11382	\N	2128	170	499
11369	\N	2127	695	499
11374	\N	2127	178	499
11365	\N	2126	173	499
11367	\N	2126	692	499
11371	\N	2126	172	499
11377	\N	2126	171	499
11378	\N	2126	696	499
11366	\N	2125	694	499
11370	\N	2125	693	499
11375	\N	2125	179	499
11421	\N	2050	692	502
11424	\N	2050	693	502
11434	\N	2051	169	502
11435	\N	2051	697	502
11419	\N	2049	173	502
11423	\N	2049	695	502
11432	\N	2049	696	502
11420	\N	2052	694	502
11425	\N	2052	172	502
11455	\N	2053	173	504
11457	\N	2053	692	504
11458	\N	2053	174	504
11459	\N	2053	695	504
11460	\N	2053	693	504
11462	\N	2053	180	504
11464	\N	2053	178	504
11465	\N	2053	179	504
11466	\N	2053	176	504
11469	\N	2053	175	504
11472	\N	2053	170	504
11468	\N	2054	696	504
11456	\N	2055	694	504
11461	\N	2055	172	504
11471	\N	2055	697	504
11479	\N	2058	172	505
11474	\N	2059	694	505
11480	\N	2059	180	505
11482	\N	2059	178	505
11483	\N	2059	179	505
11489	\N	2059	697	505
11475	\N	2060	692	505
11476	\N	2060	174	505
11477	\N	2060	695	505
11486	\N	2060	696	505
11473	\N	2057	173	505
11478	\N	2057	693	505
11484	\N	2057	176	505
11485	\N	2057	171	505
11487	\N	2057	175	505
11512	\N	2068	174	507
11511	\N	2067	692	507
11525	\N	2067	697	507
11509	\N	2065	173	507
11510	\N	2065	694	507
11513	\N	2065	695	507
11514	\N	2065	693	507
11515	\N	2065	172	507
11516	\N	2065	180	507
11517	\N	2065	177	507
11518	\N	2065	178	507
11519	\N	2065	179	507
11520	\N	2065	176	507
11521	\N	2065	171	507
11522	\N	2065	696	507
11523	\N	2065	175	507
11526	\N	2065	170	507
11528	\N	2071	694	508
11530	\N	2071	174	508
11537	\N	2071	179	508
11539	\N	2071	171	508
11541	\N	2071	175	508
11543	\N	2071	697	508
11529	\N	2069	692	508
11534	\N	2069	180	508
11535	\N	2069	177	508
11538	\N	2069	176	508
11531	\N	2070	695	508
11532	\N	2070	693	508
11533	\N	2070	172	508
11540	\N	2070	696	508
11544	\N	2070	170	508
11527	\N	2072	173	508
11551	\N	2073	172	509
11545	\N	2074	173	509
11546	\N	2074	694	509
11548	\N	2074	174	509
11549	\N	2074	695	509
11550	\N	2074	693	509
11552	\N	2074	180	509
11553	\N	2074	177	509
11554	\N	2074	178	509
11556	\N	2074	176	509
11557	\N	2074	171	509
11558	\N	2074	696	509
11559	\N	2074	175	509
11561	\N	2074	697	509
11547	\N	2076	692	509
11555	\N	2076	179	509
11560	\N	2076	169	509
11581	\N	3732	183	511
11584	\N	3732	184	511
11592	\N	3732	703	511
11384	\N	3729	694	500
11386	\N	3729	174	500
11388	\N	3729	693	500
11389	\N	3729	172	500
11391	\N	3729	177	500
11396	\N	3729	696	500
11582	\N	3731	698	511
11583	\N	3731	181	511
11585	\N	3731	699	511
11586	\N	3731	700	511
11587	\N	3731	182	511
11588	\N	3731	701	511
11589	\N	3731	702	511
11590	\N	3731	185	511
11591	\N	3731	186	511
11385	\N	3731	692	500
11387	\N	3731	695	500
11390	\N	3731	180	500
11392	\N	3731	178	500
11593	\N	4353	183	512
11597	\N	4353	699	512
11599	\N	4353	182	512
11601	\N	4353	702	512
11603	\N	4353	186	512
9062	\N	4353	651	297
9063	\N	4353	654	297
9065	\N	4353	641	297
9075	\N	4353	649	297
9077	\N	4353	650	297
9078	\N	4353	652	297
9080	\N	4353	656	297
9081	\N	4353	658	297
9082	\N	4353	635	297
9086	\N	4353	660	297
9087	\N	4353	661	297
7910	\N	4353	609	260
7911	\N	4353	610	260
7912	\N	4353	614	260
7913	\N	4353	620	260
11402	\N	4353	694	501
11404	\N	4353	174	501
11405	\N	4353	695	501
11407	\N	4353	172	501
11409	\N	4353	177	501
11410	\N	4353	178	501
11689	\N	4622	183	520
11690	\N	4622	698	520
11691	\N	4622	181	520
11692	\N	4622	184	520
11693	\N	4622	699	520
11694	\N	4622	700	520
11695	\N	4622	182	520
11697	\N	4622	702	520
11698	\N	4622	185	520
11699	\N	4622	186	520
11563	\N	4622	173	510
11948	\N	2190	704	541
11941	\N	2192	709	541
11942	\N	2192	710	541
11943	\N	2192	711	541
11944	\N	2192	705	541
11945	\N	2192	706	541
11946	\N	2192	707	541
11947	\N	2192	708	541
11950	\N	2194	709	542
11952	\N	2194	711	542
11953	\N	2194	705	542
11954	\N	2194	706	542
11955	\N	2194	707	542
11956	\N	2194	708	542
11739	\N	3146	181	524
11745	\N	3146	702	524
11746	\N	3146	185	524
11078	\N	3146	694	483
11083	\N	3146	172	483
11744	\N	3145	701	524
11747	\N	3145	186	524
11077	\N	3145	173	483
11082	\N	3145	693	483
11091	\N	3145	175	483
11738	\N	3148	698	524
11741	\N	3148	699	524
11742	\N	3148	700	524
11743	\N	3148	182	524
11748	\N	3148	703	524
11080	\N	3148	174	483
11081	\N	3148	695	483
11084	\N	3148	180	483
11086	\N	3148	178	483
11087	\N	3148	179	483
11088	\N	3148	176	483
11089	\N	3148	171	483
11090	\N	3148	696	483
11092	\N	3148	169	483
11093	\N	3148	697	483
11094	\N	3148	170	483
11079	\N	3147	692	483
11085	\N	3147	177	483
11701	\N	3432	183	521
11702	\N	3432	698	521
11703	\N	3432	181	521
11705	\N	3432	699	521
11707	\N	3432	182	521
11708	\N	3432	701	521
11710	\N	3432	185	521
11711	\N	3432	186	521
11712	\N	3432	703	521
11437	\N	3432	173	503
11438	\N	3432	694	503
11439	\N	3432	692	503
11440	\N	3432	174	503
11441	\N	3432	695	503
11442	\N	3432	693	503
11444	\N	3432	180	503
11445	\N	3432	177	503
11447	\N	3432	179	503
11448	\N	3432	176	503
11451	\N	3432	175	503
11452	\N	3432	169	503
11453	\N	3432	697	503
11454	\N	3432	170	503
11706	\N	3429	700	521
11709	\N	3429	702	521
11449	\N	3429	171	503
11450	\N	3429	696	503
11704	\N	3431	184	521
11446	\N	3431	178	503
11957	\N	2194	704	542
11958	\N	2194	187	542
11951	\N	2196	710	542
11960	\N	2199	710	543
11961	\N	2199	711	543
11962	\N	2199	705	543
11963	\N	2199	706	543
11965	\N	2199	708	543
11959	\N	2200	709	543
11964	\N	2200	707	543
11966	\N	2200	704	543
11973	\N	2183	707	544
12041	\N	2225	721	551
12044	\N	2225	724	551
12045	\N	2225	725	551
12046	\N	2225	726	551
12047	\N	2225	191	551
12048	\N	2225	722	551
12050	\N	2225	723	551
12042	\N	2227	727	551
12043	\N	2227	728	551
12054	\N	2236	724	552
12052	\N	2233	727	552
12051	\N	2234	721	552
12053	\N	2234	728	552
12055	\N	2234	725	552
12056	\N	2234	726	552
12058	\N	2234	722	552
12059	\N	2234	190	552
12060	\N	2234	723	552
12067	\N	2230	191	553
12068	\N	2230	722	553
12069	\N	2230	190	553
12061	\N	2232	721	553
12062	\N	2232	727	553
12063	\N	2232	728	553
12064	\N	2232	724	553
12065	\N	2232	725	553
12066	\N	2232	726	553
12070	\N	2232	723	553
12075	\N	2240	725	554
12071	\N	2237	721	554
12072	\N	2237	727	554
12074	\N	2237	724	554
12076	\N	2237	726	554
12078	\N	2237	722	554
12079	\N	2237	190	554
12080	\N	2237	723	554
12073	\N	2238	728	554
12077	\N	2238	191	554
12144	\N	2269	735	561
12146	\N	2269	736	561
12137	\N	2271	197	561
12138	\N	2271	198	561
12140	\N	2271	741	561
12142	\N	2271	742	561
12147	\N	2271	196	561
12148	\N	2271	743	561
12136	\N	2270	737	561
12139	\N	2270	738	561
12141	\N	2270	739	561
12143	\N	2270	740	561
12145	\N	2270	195	561
12157	\N	2275	735	562
12150	\N	2273	197	562
12149	\N	2274	737	562
12151	\N	2274	198	562
12152	\N	2274	738	562
12153	\N	2274	741	562
12154	\N	2274	739	562
12155	\N	2274	742	562
12156	\N	2274	740	562
12158	\N	2274	195	562
12159	\N	2274	736	562
12160	\N	2274	196	562
12161	\N	2274	743	562
12168	\N	2261	742	563
12170	\N	2261	735	563
12173	\N	2261	196	563
12162	\N	2262	737	563
12165	\N	2262	738	563
12167	\N	2262	739	563
12164	\N	2263	198	563
12166	\N	2263	741	563
12163	\N	2264	197	563
12169	\N	2264	740	563
12172	\N	2264	736	563
12174	\N	2264	743	563
12191	\N	2277	738	565
12192	\N	2277	741	565
12193	\N	2277	739	565
12194	\N	2277	742	565
12197	\N	2277	195	565
12198	\N	2277	736	565
12199	\N	2277	196	565
12200	\N	2277	743	565
12188	\N	2280	737	565
12196	\N	2280	735	565
12195	\N	2278	740	565
12215	\N	2791	752	567
12217	\N	2791	745	567
12222	\N	2791	754	567
12225	\N	2791	748	567
12227	\N	2791	750	567
12176	\N	2791	197	564
12178	\N	2791	738	564
12095	\N	4391	733	556
12096	\N	4391	194	556
12099	\N	4391	734	556
12081	\N	4391	721	555
12082	\N	4391	727	555
12282	\N	2320	759	572
12287	\N	2320	762	572
12285	\N	2317	764	572
12281	\N	2319	763	572
12284	\N	2319	766	572
12286	\N	2319	761	572
12289	\N	2319	758	572
12290	\N	2319	199	572
12283	\N	2318	760	572
12288	\N	2318	765	572
12296	\N	2307	761	573
12291	\N	2305	763	573
12292	\N	2305	759	573
12226	\N	2789	757	567
12175	\N	2789	737	564
12179	\N	2789	741	564
12182	\N	2789	740	564
12183	\N	2789	735	564
12216	\N	2792	744	567
12219	\N	2792	746	567
12220	\N	2792	747	567
12221	\N	2792	749	567
12224	\N	2792	755	567
12228	\N	2792	751	567
12181	\N	2792	742	564
12184	\N	2792	195	564
12218	\N	2790	756	567
12223	\N	2790	753	567
12177	\N	2790	198	564
12180	\N	2791	739	564
12185	\N	2791	736	564
12187	\N	2791	743	564
12083	\N	4391	728	555
12084	\N	4391	724	555
12085	\N	4391	725	555
12086	\N	4391	726	555
12088	\N	4391	722	555
12089	\N	4391	190	555
12090	\N	4391	723	555
12091	\N	4390	730	556
12092	\N	4390	729	556
12093	\N	4390	731	556
12094	\N	4390	732	556
12097	\N	4390	193	556
12293	\N	2305	760	573
12294	\N	2305	766	573
12295	\N	2305	764	573
12297	\N	2305	762	573
12298	\N	2305	765	573
12299	\N	2305	758	573
12303	\N	2316	760	574
12304	\N	2316	766	574
12307	\N	2316	762	574
12302	\N	2313	759	574
12305	\N	2315	764	574
12308	\N	2315	765	574
12309	\N	2315	758	574
12310	\N	2315	199	574
12301	\N	2314	763	574
12306	\N	2314	761	574
12312	\N	2304	759	575
12314	\N	2304	766	575
12315	\N	2304	764	575
12316	\N	2304	761	575
12320	\N	2304	199	575
12311	\N	2301	763	575
12318	\N	2302	765	575
12319	\N	2302	758	575
12313	\N	2303	760	575
12317	\N	2303	762	575
12361	\N	2360	775	581
12365	\N	2360	777	581
12367	\N	2360	776	581
12368	\N	2360	204	581
12362	\N	2357	203	581
12369	\N	2357	205	581
12363	\N	2359	772	581
12364	\N	2359	773	581
12366	\N	2359	774	581
12370	\N	2346	775	582
12374	\N	2346	777	582
12375	\N	2346	774	582
12376	\N	2346	776	582
12378	\N	2346	205	582
12371	\N	2345	203	582
12372	\N	2345	772	582
12373	\N	2345	773	582
12377	\N	2345	204	582
12382	\N	2355	773	583
12385	\N	2355	776	583
12384	\N	2353	774	583
12379	\N	2356	775	583
12381	\N	2356	772	583
12383	\N	2356	777	583
12387	\N	2356	205	583
12396	\N	2349	205	584
12388	\N	2351	775	584
12389	\N	2351	203	584
12390	\N	2351	772	584
12391	\N	2351	773	584
12392	\N	2351	777	584
12393	\N	2351	774	584
12394	\N	2351	776	584
12395	\N	2351	204	584
12397	\N	2341	775	585
12398	\N	2343	203	585
12399	\N	2343	772	585
12400	\N	2343	773	585
12401	\N	2343	777	585
12402	\N	2343	774	585
12403	\N	2343	776	585
12404	\N	2343	204	585
12465	\N	2389	785	591
12469	\N	2389	786	591
12456	\N	2390	210	591
12457	\N	2390	787	591
12458	\N	2390	788	591
12459	\N	2390	790	591
12460	\N	2390	209	591
12461	\N	2390	791	591
12462	\N	2390	792	591
12463	\N	2390	795	591
12464	\N	2390	211	591
12466	\N	2390	789	591
12467	\N	2390	793	591
12468	\N	2390	794	591
12471	\N	2398	787	592
12477	\N	2397	795	592
12479	\N	2397	785	592
12483	\N	2397	786	592
12470	\N	2399	210	592
12472	\N	2399	788	592
12473	\N	2399	790	592
12475	\N	2399	791	592
12476	\N	2399	792	592
12478	\N	2399	211	592
12480	\N	2399	789	592
12481	\N	2399	793	592
12482	\N	2399	794	592
12485	\N	2382	787	593
12486	\N	2382	788	593
12487	\N	2382	790	593
12489	\N	2382	791	593
12496	\N	2382	794	593
12497	\N	2382	786	593
12490	\N	2384	792	593
12491	\N	2384	795	593
12493	\N	2384	785	593
12494	\N	2384	789	593
12495	\N	2384	793	593
12488	\N	2383	209	593
12498	\N	2387	210	594
12499	\N	2387	787	594
12500	\N	2387	788	594
12501	\N	2387	790	594
12503	\N	2387	791	594
12504	\N	2387	792	594
12506	\N	2387	211	594
12508	\N	2387	789	594
12509	\N	2387	793	594
12510	\N	2387	794	594
12511	\N	2387	786	594
12502	\N	2388	209	594
12505	\N	2388	795	594
12507	\N	2388	785	594
12521	\N	2394	785	595
12512	\N	2393	210	595
12514	\N	2393	788	595
12515	\N	2393	790	595
12517	\N	2393	791	595
12518	\N	2393	792	595
12520	\N	2393	211	595
12522	\N	2393	789	595
12523	\N	2393	793	595
12524	\N	2393	794	595
12513	\N	2396	787	595
12516	\N	2396	209	595
12519	\N	2396	795	595
12525	\N	2396	786	595
12602	\N	2431	809	601
12604	\N	2431	810	601
12606	\N	2431	215	601
12601	\N	2429	808	601
12603	\N	2432	811	601
12605	\N	2432	812	601
12610	\N	2426	810	602
12609	\N	2428	811	602
12608	\N	2425	809	602
12611	\N	2425	812	602
12607	\N	2427	808	602
12616	\N	2433	810	603
12613	\N	2434	808	603
12614	\N	2434	809	603
12615	\N	2434	811	603
12617	\N	2434	812	603
12618	\N	2434	215	603
12619	\N	2438	808	604
12621	\N	2438	811	604
12620	\N	2439	809	604
12622	\N	2439	810	604
12623	\N	2439	812	604
12624	\N	2439	215	604
12628	\N	2421	810	605
12625	\N	2422	808	605
12626	\N	2422	809	605
12627	\N	2423	811	605
12629	\N	2423	812	605
12630	\N	2423	215	605
12341	\N	4516	769	578
12338	\N	4514	771	578
12340	\N	4514	768	578
12343	\N	4514	770	578
12271	\N	4514	763	571
12275	\N	4514	764	571
12342	\N	4513	767	578
12785	\N	2513	219	622
12786	\N	2513	221	622
12787	\N	2513	846	622
12788	\N	2513	839	622
12790	\N	2513	842	622
12792	\N	2513	838	622
12793	\N	2513	845	622
12794	\N	2513	847	622
12795	\N	2513	840	622
12798	\N	2513	220	622
12789	\N	2516	841	622
12791	\N	2516	848	622
12796	\N	2516	843	622
12797	\N	2516	844	622
12799	\N	2505	219	623
12800	\N	2505	221	623
12801	\N	2505	846	623
12803	\N	2505	841	623
12804	\N	2505	842	623
12805	\N	2505	848	623
12806	\N	2505	838	623
12807	\N	2505	845	623
12808	\N	2505	847	623
12809	\N	2505	840	623
12810	\N	2505	843	623
12811	\N	2505	844	623
12812	\N	2505	220	623
12802	\N	2508	839	623
12815	\N	2501	846	624
12822	\N	2501	847	624
12824	\N	2501	843	624
12814	\N	2502	221	624
12821	\N	2502	845	624
12823	\N	2502	840	624
12816	\N	2504	839	624
12817	\N	2504	841	624
12818	\N	2504	842	624
12819	\N	2504	848	624
12820	\N	2504	838	624
12825	\N	2504	844	624
12830	\N	2509	839	625
12831	\N	2509	841	625
12832	\N	2509	842	625
12833	\N	2509	848	625
12839	\N	2509	844	625
12827	\N	2510	219	625
12829	\N	2510	846	625
12834	\N	2510	838	625
12835	\N	2510	845	625
12836	\N	2510	847	625
12837	\N	2510	840	625
12838	\N	2510	843	625
12840	\N	2510	220	625
12743	\N	3585	831	618
12738	\N	3586	827	618
12740	\N	3586	836	618
12741	\N	3586	830	618
12745	\N	3586	833	618
12746	\N	3586	834	618
12747	\N	3586	828	618
12667	\N	3586	818	611
12669	\N	3586	819	611
12671	\N	3586	820	611
12672	\N	3586	821	611
12673	\N	3586	822	611
12675	\N	3586	823	611
12744	\N	3588	835	618
12860	\N	2549	855	633
12861	\N	2549	853	633
12866	\N	2549	851	633
12859	\N	2550	852	633
12863	\N	2550	856	633
12864	\N	2550	849	633
12865	\N	2550	850	633
12862	\N	2552	854	633
12873	\N	2541	849	634
12874	\N	2541	850	634
12869	\N	2544	855	634
12870	\N	2544	853	634
12871	\N	2544	854	634
12872	\N	2544	856	634
12739	\N	3587	829	618
12742	\N	3587	837	618
12748	\N	3587	832	618
12666	\N	3587	824	611
12720	\N	4880	837	616
12716	\N	4877	827	616
12717	\N	4877	829	616
12719	\N	4877	830	616
12721	\N	4877	831	616
12723	\N	4877	833	616
12725	\N	4877	828	616
12726	\N	4877	832	616
12696	\N	4877	824	614
12697	\N	4877	818	614
12699	\N	4877	819	614
12700	\N	4877	825	614
12701	\N	4877	820	614
12703	\N	4877	822	614
12704	\N	4877	826	614
12705	\N	4877	823	614
12718	\N	4879	836	616
12698	\N	4879	218	614
12702	\N	4879	821	614
12734	\N	4869	833	617
12707	\N	4869	818	615
12708	\N	4869	218	615
12737	\N	4870	832	617
12709	\N	4870	819	615
12712	\N	4870	821	615
12715	\N	4870	823	615
12727	\N	4872	827	617
12728	\N	4872	829	617
12729	\N	4872	836	617
12730	\N	4872	830	617
12731	\N	4872	837	617
12732	\N	4872	831	617
12733	\N	4872	835	617
12735	\N	4872	834	617
12722	\N	4878	835	616
12724	\N	4878	834	616
12736	\N	4872	828	617
12706	\N	4872	824	615
12710	\N	4872	825	615
12711	\N	4872	820	615
12713	\N	4872	822	615
12714	\N	4872	826	615
12875	\N	2544	851	634
12876	\N	2544	222	634
12868	\N	2543	852	634
12878	\N	2555	855	635
12880	\N	2555	854	635
12881	\N	2555	856	635
12884	\N	2555	851	635
12877	\N	2556	852	635
12882	\N	2556	849	635
12883	\N	2556	850	635
12885	\N	2556	222	635
12879	\N	2554	853	635
12935	\N	2581	866	642
12937	\N	2583	867	642
12938	\N	2583	869	642
12940	\N	2583	872	642
12941	\N	2583	865	642
12943	\N	2583	864	642
12942	\N	2582	870	642
12936	\N	2584	868	642
12939	\N	2584	871	642
12948	\N	2591	871	643
12944	\N	2589	866	643
12945	\N	2589	868	643
12946	\N	2589	867	643
12949	\N	2589	872	643
12950	\N	2590	865	643
12952	\N	2590	864	643
12947	\N	2592	869	643
12951	\N	2592	870	643
12953	\N	2599	866	644
12957	\N	2599	871	644
12958	\N	2599	872	644
12959	\N	2599	865	644
12960	\N	2599	870	644
12954	\N	2598	868	644
12955	\N	2598	867	644
12956	\N	2598	869	644
12961	\N	2598	864	644
12962	\N	2593	866	645
12963	\N	2593	868	645
12964	\N	2593	867	645
12965	\N	2593	869	645
12966	\N	2593	871	645
12967	\N	2593	872	645
12968	\N	2593	865	645
12969	\N	2593	870	645
12970	\N	2593	864	645
12853	\N	3712	854	632
12855	\N	3712	849	632
12854	\N	3709	856	632
12856	\N	3709	850	632
12895	\N	3711	857	637
12894	\N	3710	861	637
12896	\N	3710	862	637
12897	\N	3710	863	637
12898	\N	3710	859	637
13050	\N	2637	888	653
13053	\N	2637	228	653
13048	\N	2638	882	653
13049	\N	2638	884	653
13051	\N	2638	886	653
13052	\N	2638	883	653
13055	\N	2638	885	653
13056	\N	2638	889	653
13047	\N	2639	881	653
13045	\N	2640	880	653
13054	\N	2640	887	653
13063	\N	2623	886	654
13064	\N	2621	883	654
13068	\N	2621	889	654
13057	\N	2624	880	654
13058	\N	2622	227	654
13059	\N	2622	881	654
13060	\N	2622	882	654
13061	\N	2622	884	654
13062	\N	2622	888	654
13066	\N	2622	887	654
13067	\N	2622	885	654
13071	\N	2628	881	655
13076	\N	2628	883	655
13077	\N	2628	228	655
13079	\N	2628	885	655
13080	\N	2628	889	655
13069	\N	2626	880	655
13070	\N	2626	227	655
13072	\N	2626	882	655
13073	\N	2626	884	655
13074	\N	2626	888	655
13075	\N	2626	886	655
13078	\N	2626	887	655
12934	\N	3421	864	641
12926	\N	3423	866	641
12928	\N	3423	867	641
12932	\N	3423	865	641
12981	\N	3424	224	647
12982	\N	3424	875	647
12983	\N	3424	873	647
12984	\N	3424	874	647
12985	\N	3424	876	647
13105	\N	3381	894	658
13106	\N	3381	895	658
13109	\N	3381	891	658
13111	\N	3381	890	658
13021	\N	3381	880	651
13022	\N	3381	227	651
13023	\N	3381	881	651
13024	\N	3381	882	651
13025	\N	3381	884	651
13028	\N	3381	883	651
13031	\N	3381	885	651
13107	\N	3382	896	658
13110	\N	3382	893	658
13027	\N	3382	886	651
13029	\N	3382	228	651
13104	\N	3383	900	658
13113	\N	3383	899	658
12986	\N	3424	877	647
12987	\N	3424	878	647
12988	\N	3424	226	647
12989	\N	3424	225	647
12990	\N	3424	879	647
12927	\N	3424	868	641
12929	\N	3424	869	641
12930	\N	3424	871	641
12931	\N	3424	872	641
12933	\N	3424	870	641
12899	\N	3710	858	637
12900	\N	3710	860	637
12901	\N	3710	223	637
12850	\N	3710	852	632
12851	\N	3710	855	632
12852	\N	3710	853	632
12857	\N	3710	851	632
12858	\N	3710	222	632
12918	\N	4829	861	640
12919	\N	4829	857	640
12920	\N	4829	862	640
12921	\N	4829	863	640
12922	\N	4829	859	640
12923	\N	4829	858	640
12924	\N	4829	860	640
12925	\N	4829	223	640
12841	\N	4829	852	631
12843	\N	4829	853	631
12844	\N	4829	854	631
12846	\N	4829	849	631
12847	\N	4829	850	631
12848	\N	4829	851	631
12849	\N	4829	222	631
12842	\N	4832	855	631
12845	\N	4832	856	631
13149	\N	2787	243	661
13138	\N	2788	229	661
13148	\N	2788	905	661
10977	\N	2788	153	478
10980	\N	2788	154	478
10981	\N	2788	688	478
13136	\N	2785	232	661
13137	\N	2785	233	661
13139	\N	2785	901	661
13140	\N	2785	239	661
13141	\N	2785	231	661
13142	\N	2785	241	661
13143	\N	2785	237	661
13144	\N	2785	240	661
13145	\N	2785	230	661
13146	\N	2785	235	661
13147	\N	2785	234	661
13150	\N	2785	238	661
13151	\N	2785	902	661
13152	\N	2785	903	661
13153	\N	2785	242	661
13154	\N	2785	236	661
13155	\N	2785	904	661
10957	\N	2785	163	478
10958	\N	2785	682	478
10959	\N	2785	160	478
10960	\N	2785	683	478
10961	\N	2785	684	478
10962	\N	2785	156	478
10963	\N	2785	165	478
10964	\N	2785	152	478
10965	\N	2785	685	478
10966	\N	2785	158	478
10967	\N	2785	686	478
10969	\N	2785	159	478
10970	\N	2785	690	478
10972	\N	2785	167	478
10973	\N	2785	162	478
10974	\N	2785	691	478
10975	\N	2785	687	478
10976	\N	2785	166	478
10978	\N	2785	164	478
10979	\N	2785	157	478
10982	\N	2785	689	478
10984	\N	2785	161	478
9288	\N	2785	663	424
9289	\N	2785	671	424
9290	\N	2785	148	424
9291	\N	2785	680	424
9292	\N	2785	664	424
9293	\N	2785	672	424
9295	\N	2785	150	424
9309	\N	2785	668	424
9310	\N	2785	144	424
9311	\N	2785	677	424
9312	\N	2785	669	424
9313	\N	2785	670	424
9314	\N	2785	147	424
9315	\N	2785	681	424
9316	\N	2785	678	424
13161	\N	2789	231	662
13175	\N	2789	904	662
13156	\N	2792	232	662
13163	\N	2792	237	662
13164	\N	2792	240	662
13166	\N	2792	235	662
13167	\N	2792	234	662
13168	\N	2792	905	662
13169	\N	2792	243	662
13170	\N	2792	238	662
13159	\N	2790	901	662
13171	\N	2790	902	662
13216	\N	2801	232	665
13217	\N	2801	233	665
13218	\N	2801	229	665
13219	\N	2801	901	665
13220	\N	2801	239	665
13221	\N	2801	231	665
13222	\N	2801	241	665
13224	\N	2801	240	665
13225	\N	2801	230	665
13227	\N	2801	234	665
13228	\N	2801	905	665
13230	\N	2801	238	665
13232	\N	2801	903	665
13233	\N	2801	242	665
13234	\N	2801	236	665
13235	\N	2801	904	665
13223	\N	2802	237	665
13226	\N	2802	235	665
13229	\N	2802	243	665
13231	\N	2802	902	665
13237	\N	2808	233	666
13252	\N	2808	903	666
13255	\N	2808	904	666
13236	\N	2806	232	666
13238	\N	2806	229	666
13240	\N	2806	239	666
13241	\N	2806	231	666
13246	\N	2806	235	666
13247	\N	2806	234	666
13248	\N	2806	905	666
13250	\N	2806	238	666
13253	\N	2806	242	666
13242	\N	2805	241	666
13243	\N	2805	237	666
13245	\N	2805	230	666
13249	\N	2805	243	666
13239	\N	2807	901	666
13244	\N	2807	240	666
13251	\N	2807	902	666
13254	\N	2807	236	666
13268	\N	2811	905	667
13269	\N	2811	243	667
13103	\N	3384	892	658
13108	\N	3384	898	658
13112	\N	3384	897	658
13026	\N	3384	888	651
13030	\N	3384	887	651
13032	\N	3384	889	651
13116	\N	4502	894	659
13119	\N	4502	898	659
13033	\N	4502	880	652
13037	\N	4502	884	652
13041	\N	4502	228	652
13043	\N	4502	885	652
13115	\N	4504	900	659
13117	\N	4504	895	659
13123	\N	4504	897	659
13034	\N	4504	227	652
13035	\N	4504	881	652
13036	\N	4504	882	652
13038	\N	4504	888	652
13039	\N	4504	886	652
13040	\N	4504	883	652
13044	\N	4504	889	652
13120	\N	4503	891	659
13114	\N	4501	892	659
13118	\N	4501	896	659
13121	\N	4501	893	659
13122	\N	4501	890	659
13124	\N	4501	899	659
13042	\N	4501	887	652
11860	\N	2811	184	534
11863	\N	2811	182	534
11864	\N	2811	701	534
11168	\N	2811	694	488
11172	\N	2811	693	488
11175	\N	2811	177	488
11177	\N	2811	179	488
11179	\N	2811	171	488
11183	\N	2811	697	488
11184	\N	2811	170	488
11861	\N	2809	699	534
11868	\N	2809	703	534
11171	\N	2809	695	488
11173	\N	2809	172	488
13256	\N	2812	232	667
13257	\N	2812	233	667
13258	\N	2812	229	667
13259	\N	2812	901	667
13260	\N	2812	239	667
13261	\N	2812	231	667
13262	\N	2812	241	667
13263	\N	2812	237	667
13264	\N	2812	240	667
13265	\N	2812	230	667
13266	\N	2812	235	667
13267	\N	2812	234	667
13270	\N	2812	238	667
13271	\N	2812	902	667
13272	\N	2812	903	667
13273	\N	2812	242	667
13274	\N	2812	236	667
13275	\N	2812	904	667
11857	\N	2812	183	534
11858	\N	2812	698	534
11859	\N	2812	181	534
11862	\N	2812	700	534
11865	\N	2812	702	534
11866	\N	2812	185	534
11867	\N	2812	186	534
11167	\N	2812	173	488
11169	\N	2812	692	488
11170	\N	2812	174	488
11174	\N	2812	180	488
11176	\N	2812	178	488
11178	\N	2812	176	488
11180	\N	2812	696	488
11181	\N	2812	175	488
11182	\N	2812	169	488
13308	\N	2854	905	669
13305	\N	2856	230	669
13309	\N	2856	243	669
13312	\N	2856	903	669
13313	\N	2856	242	669
13299	\N	2855	901	669
13300	\N	2855	239	669
13310	\N	2855	238	669
13311	\N	2855	902	669
13315	\N	2855	904	669
13296	\N	2853	232	669
13298	\N	2853	229	669
13301	\N	2853	231	669
13302	\N	2853	241	669
13303	\N	2853	237	669
13304	\N	2853	240	669
13306	\N	2853	235	669
13314	\N	2853	236	669
13325	\N	2857	230	670
13327	\N	2857	234	670
13330	\N	2857	238	670
13334	\N	2857	236	670
13324	\N	2858	240	670
13329	\N	2858	243	670
13333	\N	2858	242	670
13335	\N	2858	904	670
13318	\N	2859	229	670
13322	\N	2859	241	670
13323	\N	2859	237	670
13331	\N	2859	902	670
13332	\N	2859	903	670
13319	\N	2860	901	670
13320	\N	2860	239	670
13328	\N	2860	905	670
13336	\N	2864	232	671
13337	\N	2864	233	671
13338	\N	2864	229	671
13339	\N	2864	901	671
13340	\N	2864	239	671
13342	\N	2864	241	671
13344	\N	2864	240	671
13345	\N	2864	230	671
13346	\N	2864	235	671
13347	\N	2864	234	671
13349	\N	2864	243	671
13350	\N	2864	238	671
13352	\N	2864	903	671
13354	\N	2864	236	671
13355	\N	2864	904	671
13343	\N	2861	237	671
13348	\N	2861	905	671
13341	\N	2863	231	671
13351	\N	2863	902	671
13353	\N	2863	242	671
13448	\N	2819	905	676
13449	\N	2819	243	676
13451	\N	2819	902	676
13452	\N	2819	903	676
13439	\N	2817	901	676
13444	\N	2817	240	676
13453	\N	2817	242	676
13437	\N	2820	233	676
13438	\N	2820	229	676
13440	\N	2820	239	676
13441	\N	2820	231	676
13442	\N	2820	241	676
13443	\N	2820	237	676
13445	\N	2820	230	676
13446	\N	2820	235	676
13447	\N	2820	234	676
13450	\N	2820	238	676
13454	\N	2820	236	676
13455	\N	2820	904	676
13469	\N	2823	243	677
13473	\N	2823	242	677
13456	\N	2821	232	677
13457	\N	2821	233	677
13458	\N	2821	229	677
13459	\N	2821	901	677
13461	\N	2821	231	677
13462	\N	2821	241	677
13463	\N	2821	237	677
13464	\N	2821	240	677
13465	\N	2821	230	677
13466	\N	2821	235	677
13467	\N	2821	234	677
13468	\N	2821	905	677
13470	\N	2821	238	677
13471	\N	2821	902	677
13472	\N	2821	903	677
13474	\N	2821	236	677
13475	\N	2821	904	677
13460	\N	2824	239	677
13516	\N	4498	232	680
13518	\N	4498	229	680
13519	\N	4498	901	680
13520	\N	4498	239	680
13521	\N	4498	231	680
13522	\N	4498	241	680
13524	\N	4498	240	680
13525	\N	4498	230	680
13526	\N	4498	235	680
13532	\N	4498	903	680
13533	\N	4498	242	680
13534	\N	4498	236	680
13535	\N	4498	904	680
13531	\N	4500	902	680
13517	\N	4497	233	680
13527	\N	4497	234	680
13592	\N	2779	903	683
1061	\N	2779	20	38
1072	\N	2779	481	38
1080	\N	2779	34	38
13586	\N	2778	235	683
1029	\N	2778	29	38
1044	\N	2778	12	38
1054	\N	2778	31	38
1055	\N	2778	474	38
1056	\N	2778	480	38
1057	\N	2778	42	38
1058	\N	2778	19	38
1059	\N	2778	40	38
1062	\N	2778	27	38
1063	\N	2778	38	38
1064	\N	2778	39	38
1065	\N	2778	21	38
1066	\N	2778	486	38
1067	\N	2778	15	38
1068	\N	2778	43	38
1069	\N	2778	22	38
1071	\N	2778	8	38
1073	\N	2778	28	38
1074	\N	2778	9	38
1075	\N	2778	23	38
1076	\N	2778	10	38
1077	\N	2778	33	38
1078	\N	2778	44	38
1079	\N	2778	11	38
13576	\N	2780	232	683
13577	\N	2780	233	683
13578	\N	2780	229	683
13579	\N	2780	901	683
13580	\N	2780	239	683
13581	\N	2780	231	683
13583	\N	2780	237	683
13585	\N	2780	230	683
13588	\N	2780	905	683
13590	\N	2780	238	683
13594	\N	2780	236	683
13595	\N	2780	904	683
1021	\N	2780	475	38
1022	\N	2780	1	38
1023	\N	2780	482	38
1024	\N	2780	26	38
1025	\N	2780	35	38
1026	\N	2780	483	38
1027	\N	2780	2	38
1028	\N	2780	476	38
1030	\N	2780	16	38
1031	\N	2780	473	38
1032	\N	2780	36	38
1033	\N	2780	472	38
1035	\N	2780	3	38
1036	\N	2780	17	38
1039	\N	2780	477	38
1041	\N	2780	41	38
1042	\N	2780	24	38
1043	\N	2780	484	38
1046	\N	2780	25	38
1047	\N	2780	478	38
1048	\N	2780	6	38
1049	\N	2780	479	38
1051	\N	2780	485	38
1052	\N	2780	7	38
13582	\N	2777	241	683
13587	\N	2777	234	683
13589	\N	2777	243	683
13591	\N	2777	902	683
13593	\N	2777	242	683
1040	\N	2777	18	38
1053	\N	2777	13	38
13609	\N	2782	243	684
13610	\N	2782	238	684
13597	\N	2781	233	684
13598	\N	2781	229	684
13599	\N	2781	901	684
13601	\N	2781	231	684
13604	\N	2781	240	684
13605	\N	2781	230	684
13606	\N	2781	235	684
13607	\N	2781	234	684
13608	\N	2781	905	684
13611	\N	2781	902	684
13613	\N	2781	242	684
13600	\N	2784	239	684
13602	\N	2784	241	684
13603	\N	2784	237	684
13612	\N	2784	903	684
13615	\N	2784	904	684
13630	\N	2832	238	685
13616	\N	2829	232	685
13617	\N	2829	233	685
13635	\N	2829	904	685
13619	\N	2831	901	685
13620	\N	2831	239	685
13621	\N	2831	231	685
13622	\N	2831	241	685
13623	\N	2831	237	685
13624	\N	2831	240	685
13625	\N	2831	230	685
13626	\N	2831	235	685
13627	\N	2831	234	685
13628	\N	2831	905	685
13629	\N	2831	243	685
13631	\N	2831	902	685
13632	\N	2831	903	685
13633	\N	2831	242	685
13636	\N	2834	232	686
13644	\N	2834	240	686
13646	\N	2834	235	686
13647	\N	2834	234	686
13649	\N	2834	243	686
13655	\N	2834	904	686
13638	\N	2836	229	686
13642	\N	2836	241	686
13645	\N	2836	230	686
13648	\N	2836	905	686
13651	\N	2836	902	686
13652	\N	2836	903	686
13653	\N	2836	242	686
13637	\N	2835	233	686
13639	\N	2835	901	686
13641	\N	2835	231	686
13643	\N	2835	237	686
13640	\N	2833	239	686
13650	\N	2833	238	686
13680	\N	2841	239	688
13689	\N	2841	243	688
13692	\N	2841	903	688
13676	\N	2842	232	688
13677	\N	2842	233	688
13678	\N	2842	229	688
13679	\N	2842	901	688
13682	\N	2842	241	688
13683	\N	2842	237	688
13684	\N	2842	240	688
13685	\N	2842	230	688
13687	\N	2842	234	688
13688	\N	2842	905	688
13690	\N	2842	238	688
13691	\N	2842	902	688
13693	\N	2842	242	688
13694	\N	2842	236	688
13695	\N	2842	904	688
13709	\N	2847	243	689
13712	\N	2847	903	689
13715	\N	2847	904	689
13699	\N	2848	901	689
13701	\N	2848	231	689
13703	\N	2848	237	689
13704	\N	2848	240	689
13705	\N	2848	230	689
13711	\N	2848	902	689
13706	\N	2846	235	689
13708	\N	2846	905	689
13714	\N	2846	236	689
13723	\N	2851	237	690
13728	\N	2851	905	690
13732	\N	2851	903	690
13733	\N	2851	242	690
13721	\N	2849	231	690
13735	\N	2849	904	690
13724	\N	2850	240	690
13718	\N	2852	229	690
13719	\N	2852	901	690
13720	\N	2852	239	690
13730	\N	2852	238	690
13731	\N	2852	902	690
13734	\N	2852	236	690
13971	\N	3946	251	700
13973	\N	3946	909	700
13961	\N	3948	245	700
13962	\N	3948	250	700
13963	\N	3948	253	700
13964	\N	3948	908	700
13966	\N	3948	255	700
13967	\N	3948	246	700
13968	\N	3948	252	700
13969	\N	3948	249	700
13970	\N	3948	247	700
13974	\N	3948	910	700
13975	\N	3948	907	700
13976	\N	3948	257	700
13977	\N	3948	256	700
13978	\N	3948	258	700
13979	\N	3948	260	700
13981	\N	3948	244	700
13983	\N	3948	259	700
13984	\N	3948	261	700
13985	\N	3948	906	700
13199	\N	3948	901	664
13202	\N	3948	241	664
13203	\N	3948	237	664
13204	\N	3948	240	664
13206	\N	3948	235	664
13207	\N	3948	234	664
13208	\N	3948	905	664
13209	\N	3948	243	664
13210	\N	3948	238	664
13211	\N	3948	902	664
13212	\N	3948	903	664
13213	\N	3948	242	664
2276	\N	3948	83	145
2279	\N	3948	79	145
13980	\N	3946	911	700
13542	\N	4033	241	681
13548	\N	4033	905	681
13536	\N	4034	232	681
13537	\N	4034	233	681
13538	\N	4034	229	681
13539	\N	4034	901	681
13540	\N	4034	239	681
13541	\N	4034	231	681
13543	\N	4034	237	681
13545	\N	4034	230	681
13546	\N	4034	235	681
13549	\N	4034	243	681
13550	\N	4034	238	681
13551	\N	4034	902	681
13552	\N	4034	903	681
13553	\N	4034	242	681
13554	\N	4034	236	681
13289	\N	4520	243	668
13291	\N	4520	902	668
13811	\N	4496	245	694
13822	\N	4496	248	694
13835	\N	4496	906	694
13813	\N	4493	253	694
13814	\N	4493	908	694
13815	\N	4493	254	694
13817	\N	4493	246	694
13818	\N	4493	252	694
13819	\N	4493	249	694
13820	\N	4493	247	694
13821	\N	4493	251	694
13823	\N	4493	909	694
13825	\N	4493	907	694
13826	\N	4493	257	694
13827	\N	4493	256	694
13828	\N	4493	258	694
13829	\N	4493	260	694
13831	\N	4493	244	694
13832	\N	4493	912	694
13834	\N	4493	261	694
13562	\N	4493	241	682
13563	\N	4493	237	682
13565	\N	4493	230	682
13567	\N	4493	234	682
13569	\N	4493	243	682
13570	\N	4493	238	682
13574	\N	4493	236	682
13575	\N	4493	904	682
13833	\N	4495	259	694
13559	\N	4495	901	682
13571	\N	4495	902	682
13816	\N	4494	255	694
13824	\N	4494	910	694
13830	\N	4494	911	694
13568	\N	4494	905	682
13572	\N	4494	903	682
13573	\N	4494	242	682
13528	\N	4497	905	680
13529	\N	4499	243	680
13372	\N	4324	903	672
14387	\N	4321	250	717
14388	\N	4321	253	717
14390	\N	4321	254	717
14391	\N	4321	255	717
14392	\N	4321	246	717
14393	\N	4321	252	717
14394	\N	4321	249	717
14395	\N	4321	247	717
14396	\N	4321	251	717
14397	\N	4321	248	717
14399	\N	4321	910	717
14400	\N	4321	907	717
14401	\N	4321	257	717
14402	\N	4321	256	717
14403	\N	4321	258	717
14404	\N	4321	260	717
14405	\N	4321	911	717
14406	\N	4321	244	717
14407	\N	4321	912	717
14408	\N	4321	259	717
14409	\N	4321	261	717
14410	\N	4321	906	717
13356	\N	4321	232	672
13357	\N	4321	233	672
13358	\N	4321	229	672
13360	\N	4321	239	672
13361	\N	4321	231	672
13362	\N	4321	241	672
13363	\N	4321	237	672
13364	\N	4321	240	672
13365	\N	4321	230	672
13366	\N	4321	235	672
13367	\N	4321	234	672
13368	\N	4321	905	672
13370	\N	4321	238	672
13373	\N	4321	242	672
13374	\N	4321	236	672
13295	\N	4520	904	668
14062	\N	4518	250	704
14067	\N	4518	246	704
14068	\N	4518	252	704
14069	\N	4518	249	704
14070	\N	4518	247	704
14077	\N	4518	256	704
14078	\N	4518	258	704
14079	\N	4518	260	704
14080	\N	4518	911	704
14084	\N	4518	261	704
13279	\N	4518	901	668
13282	\N	4518	241	668
13284	\N	4518	240	668
13286	\N	4518	235	668
13287	\N	4518	234	668
13292	\N	4518	903	668
13293	\N	4518	242	668
13294	\N	4518	236	668
14063	\N	4519	253	704
14064	\N	4519	908	704
14065	\N	4519	254	704
14066	\N	4519	255	704
14072	\N	4519	248	704
14073	\N	4519	909	704
14074	\N	4519	910	704
14075	\N	4519	907	704
14076	\N	4519	257	704
14081	\N	4519	244	704
14082	\N	4519	912	704
14083	\N	4519	259	704
14085	\N	4519	906	704
13280	\N	4519	239	668
13283	\N	4519	237	668
13285	\N	4519	230	668
13288	\N	4519	905	668
13290	\N	4519	238	668
14061	\N	4517	245	704
13277	\N	4517	233	668
13281	\N	4517	231	668
14137	\N	4915	250	707
14144	\N	4915	249	707
14149	\N	4915	910	707
13490	\N	4915	238	678
14138	\N	4916	253	707
14139	\N	4916	908	707
14140	\N	4916	254	707
14141	\N	4916	255	707
14142	\N	4916	246	707
14145	\N	4916	247	707
14146	\N	4916	251	707
14147	\N	4916	248	707
14148	\N	4916	909	707
14150	\N	4916	907	707
14151	\N	4916	257	707
14153	\N	4916	258	707
14154	\N	4916	260	707
14155	\N	4916	911	707
14156	\N	4916	244	707
14157	\N	4916	912	707
14158	\N	4916	259	707
14159	\N	4916	261	707
14160	\N	4916	906	707
13476	\N	4916	232	678
13478	\N	4916	229	678
13479	\N	4916	901	678
13480	\N	4916	239	678
13481	\N	4916	231	678
14486	\N	3017	262	721
14489	\N	3017	267	721
14493	\N	3017	913	721
7136	\N	3017	580	237
4033	\N	3017	512	184
4034	\N	3017	513	184
4039	\N	3017	492	184
4044	\N	3017	519	184
4045	\N	3017	506	184
4046	\N	3017	507	184
4051	\N	3017	496	184
4055	\N	3017	497	184
4056	\N	3017	525	184
4057	\N	3017	526	184
4061	\N	3017	530	184
14461	\N	4481	245	720
14462	\N	4481	250	720
14465	\N	4481	254	720
14466	\N	4481	255	720
14469	\N	4481	249	720
14470	\N	4481	247	720
14472	\N	4481	248	720
14473	\N	4481	909	720
14474	\N	4481	910	720
14475	\N	4481	907	720
14476	\N	4481	257	720
14477	\N	4481	256	720
14478	\N	4481	258	720
14479	\N	4481	260	720
14480	\N	4481	911	720
14481	\N	4481	244	720
14482	\N	4481	912	720
14483	\N	4481	259	720
14485	\N	4481	906	720
13416	\N	4481	232	675
13418	\N	4481	229	675
13420	\N	4481	239	675
13422	\N	4481	241	675
13423	\N	4481	237	675
13424	\N	4481	240	675
13425	\N	4481	230	675
13426	\N	4481	235	675
13427	\N	4481	234	675
13430	\N	4481	238	675
13431	\N	4481	902	675
13433	\N	4481	242	675
13434	\N	4481	236	675
13435	\N	4481	904	675
11797	\N	4481	183	529
11808	\N	4481	703	529
11311	\N	4481	173	496
11313	\N	4481	692	496
11322	\N	4481	176	496
11324	\N	4481	696	496
11325	\N	4481	175	496
11328	\N	4481	170	496
14464	\N	4482	908	720
14471	\N	4482	251	720
13421	\N	4482	231	675
11799	\N	4482	181	529
11800	\N	4482	184	529
11801	\N	4482	699	529
11802	\N	4482	700	529
11803	\N	4482	182	529
11312	\N	4482	694	496
11321	\N	4482	179	496
11326	\N	4482	169	496
13428	\N	4483	905	675
4063	\N	3017	532	184
4066	\N	3017	498	184
4067	\N	3017	499	184
4076	\N	3017	500	184
4079	\N	3017	540	184
4082	\N	3017	542	184
4086	\N	3017	544	184
7120	\N	3020	576	237
4053	\N	3020	523	184
4064	\N	3020	533	184
4081	\N	3020	541	184
7078	\N	3019	582	237
7083	\N	3019	558	237
7086	\N	3019	583	237
7091	\N	3019	584	237
7097	\N	3019	565	237
7100	\N	3019	548	237
7117	\N	3019	551	237
7119	\N	3019	598	237
7127	\N	3019	592	237
4036	\N	3019	515	184
4047	\N	3019	494	184
4050	\N	3019	521	184
4052	\N	3019	522	184
4054	\N	3019	524	184
4058	\N	3019	527	184
4062	\N	3019	531	184
4070	\N	3019	508	184
4074	\N	3019	510	184
4084	\N	3019	543	184
4085	\N	3019	504	184
14487	\N	3018	263	721
14488	\N	3018	264	721
14490	\N	3018	265	721
14491	\N	3018	914	721
14492	\N	3018	266	721
7079	\N	3018	555	237
7080	\N	3018	556	237
7082	\N	3018	545	237
7084	\N	3018	559	237
7087	\N	3018	560	237
7089	\N	3018	561	237
7094	\N	3018	564	237
7095	\N	3018	547	237
7098	\N	3018	587	237
7102	\N	3018	567	237
7104	\N	3018	569	237
7105	\N	3018	570	237
7106	\N	3018	550	237
7107	\N	3018	588	237
7108	\N	3018	132	237
7110	\N	3018	571	237
7111	\N	3018	131	237
7115	\N	3018	134	237
7116	\N	3018	590	237
7118	\N	3018	575	237
7121	\N	3018	133	237
7122	\N	3018	591	237
7125	\N	3018	578	237
7126	\N	3018	552	237
7128	\N	3018	593	237
7134	\N	3018	553	237
7137	\N	3018	581	237
4035	\N	3018	514	184
4037	\N	3018	516	184
4038	\N	3018	517	184
4040	\N	3018	518	184
4041	\N	3018	505	184
4042	\N	3018	502	184
4043	\N	3018	493	184
4048	\N	3018	520	184
4049	\N	3018	495	184
4059	\N	3018	528	184
4060	\N	3018	529	184
4065	\N	3018	534	184
4068	\N	3018	535	184
4069	\N	3018	536	184
4071	\N	3018	509	184
4072	\N	3018	537	184
4073	\N	3018	130	184
4075	\N	3018	538	184
4077	\N	3018	539	184
4078	\N	3018	503	184
4083	\N	3018	511	184
14494	\N	3023	262	722
14500	\N	3023	266	722
9347	\N	3023	670	425
9348	\N	3023	147	425
9349	\N	3023	681	425
8345	\N	3023	646	274
8347	\N	3023	640	274
8348	\N	3023	648	274
8350	\N	3023	654	274
8351	\N	3023	657	274
8356	\N	3023	653	274
8357	\N	3023	637	274
8364	\N	3023	650	274
8365	\N	3023	652	274
8369	\N	3023	635	274
8370	\N	3023	136	274
8371	\N	3023	643	274
7476	\N	3023	609	246
7480	\N	3023	621	246
7487	\N	3023	618	246
7488	\N	3023	619	246
7489	\N	3023	603	246
7491	\N	3023	624	246
7493	\N	3023	628	246
7496	\N	3023	616	246
7500	\N	3023	604	246
7503	\N	3023	626	246
7506	\N	3023	630	246
10987	\N	3024	160	479
10991	\N	3024	165	479
11001	\N	3024	162	479
11010	\N	3024	689	479
9328	\N	3024	139	425
8352	\N	3024	641	274
8367	\N	3024	656	274
7479	\N	3024	620	246
7481	\N	3024	607	246
7482	\N	3024	135	246
7490	\N	3024	606	246
7492	\N	3024	627	246
7495	\N	3024	611	246
14495	\N	3021	263	722
14496	\N	3021	264	722
14497	\N	3021	267	722
14498	\N	3021	265	722
14499	\N	3021	914	722
14501	\N	3021	913	722
10985	\N	3021	163	479
10986	\N	3021	682	479
10988	\N	3021	683	479
10990	\N	3021	156	479
10992	\N	3021	152	479
10993	\N	3021	685	479
10995	\N	3021	686	479
10996	\N	3021	151	479
10997	\N	3021	159	479
10998	\N	3021	690	479
11000	\N	3021	167	479
11002	\N	3021	691	479
11003	\N	3021	687	479
11004	\N	3021	166	479
11005	\N	3021	153	479
11006	\N	3021	164	479
11007	\N	3021	157	479
11008	\N	3021	154	479
11009	\N	3021	688	479
11011	\N	3021	168	479
11012	\N	3021	161	479
9319	\N	3021	149	425
9320	\N	3021	138	425
9321	\N	3021	679	425
9322	\N	3021	663	425
9323	\N	3021	671	425
9324	\N	3021	148	425
9327	\N	3021	672	425
9330	\N	3021	143	425
9331	\N	3021	673	425
9332	\N	3021	137	425
9334	\N	3021	665	425
9335	\N	3021	675	425
9336	\N	3021	666	425
9337	\N	3021	142	425
9338	\N	3021	676	425
9339	\N	3021	146	425
9341	\N	3021	145	425
9343	\N	3021	668	425
9345	\N	3021	677	425
9346	\N	3021	669	425
9350	\N	3021	678	425
8344	\N	3021	645	274
8346	\N	3021	639	274
8349	\N	3021	651	274
8353	\N	3021	636	274
8354	\N	3021	647	274
8355	\N	3021	633	274
8358	\N	3021	642	274
8359	\N	3021	644	274
8360	\N	3021	659	274
8366	\N	3021	655	274
8368	\N	3021	658	274
8373	\N	3021	660	274
8374	\N	3021	661	274
7477	\N	3021	610	246
7483	\N	3021	602	246
7484	\N	3021	612	246
7485	\N	3021	613	246
7486	\N	3021	615	246
7494	\N	3021	631	246
7497	\N	3021	617	246
7498	\N	3021	622	246
7501	\N	3021	605	246
7502	\N	3021	625	246
7505	\N	3021	629	246
14503	\N	3026	263	723
14504	\N	3026	264	723
14506	\N	3026	265	723
14508	\N	3026	266	723
14505	\N	3027	267	723
14502	\N	3028	262	723
14507	\N	3028	914	723
14509	\N	3028	913	723
14512	\N	3032	264	724
14514	\N	3032	265	724
14515	\N	3032	914	724
14516	\N	3032	266	724
14517	\N	3032	913	724
13736	\N	3032	245	691
13737	\N	3032	250	691
13738	\N	3032	253	691
13739	\N	3032	908	691
13740	\N	3032	254	691
13741	\N	3032	255	691
13742	\N	3032	246	691
13744	\N	3032	249	691
13745	\N	3032	247	691
13747	\N	3032	248	691
13748	\N	3032	909	691
13749	\N	3032	910	691
13751	\N	3032	257	691
13752	\N	3032	256	691
13753	\N	3032	258	691
13755	\N	3032	911	691
13757	\N	3032	912	691
13758	\N	3032	259	691
13759	\N	3032	261	691
13496	\N	3032	232	679
13498	\N	3032	229	679
13502	\N	3032	241	679
13503	\N	3032	237	679
13504	\N	3032	240	679
13505	\N	3032	230	679
13510	\N	3032	238	679
13511	\N	3032	902	679
13512	\N	3032	903	679
13513	\N	3032	242	679
13514	\N	3032	236	679
14510	\N	3031	262	724
14511	\N	3031	263	724
14513	\N	3031	267	724
13746	\N	3031	251	691
13750	\N	3031	907	691
13499	\N	3031	901	679
13501	\N	3031	231	679
13506	\N	3031	235	679
13507	\N	3031	234	679
13508	\N	3031	905	679
13509	\N	3031	243	679
13515	\N	3031	904	679
13743	\N	3030	252	691
13754	\N	3029	260	691
13760	\N	3029	906	691
13500	\N	3029	239	679
14518	\N	3033	262	725
14519	\N	3033	263	725
14520	\N	3033	264	725
14522	\N	3033	265	725
14523	\N	3033	914	725
14524	\N	3033	266	725
14525	\N	3033	913	725
14521	\N	3036	267	725
14526	\N	3040	262	726
14528	\N	3040	264	726
14531	\N	3040	914	726
14533	\N	3040	913	726
14527	\N	3037	263	726
14529	\N	3037	267	726
14530	\N	3037	265	726
14532	\N	3037	266	726
14540	\N	3002	266	727
14534	\N	3001	262	727
14535	\N	3001	263	727
14538	\N	3001	265	727
14539	\N	3001	914	727
14541	\N	3001	913	727
14536	\N	3004	264	727
14537	\N	3004	267	727
14545	\N	3006	267	728
14546	\N	3006	265	728
14547	\N	3007	914	728
14548	\N	3007	266	728
14549	\N	3005	913	728
14550	\N	3009	262	729
14553	\N	3009	267	729
14551	\N	3010	263	729
14552	\N	3010	264	729
14554	\N	3010	265	729
14555	\N	3010	914	729
14556	\N	3010	266	729
14557	\N	3010	913	729
14560	\N	3013	264	730
14561	\N	3013	267	730
14562	\N	3013	265	730
14559	\N	3015	263	730
14558	\N	3014	262	730
14564	\N	3014	266	730
14563	\N	3016	914	730
14565	\N	3016	913	730
14574	\N	3066	262	732
14577	\N	3066	267	732
14576	\N	3065	264	732
14579	\N	3065	914	732
14581	\N	3065	913	732
14575	\N	3067	263	732
14578	\N	3068	265	732
14580	\N	3068	266	732
14587	\N	3070	914	733
14583	\N	3072	263	733
14588	\N	3072	266	733
14586	\N	3071	265	733
14582	\N	3069	262	733
14584	\N	3069	264	733
14585	\N	3069	267	733
14589	\N	3069	913	733
14590	\N	3074	262	734
10399	\N	3074	160	458
10417	\N	3074	153	458
9669	\N	3074	150	435
9672	\N	3074	137	435
9678	\N	3074	676	435
9680	\N	3074	667	435
9685	\N	3074	677	435
9686	\N	3074	669	435
9689	\N	3074	681	435
7261	\N	3074	582	240
7268	\N	3074	546	240
7269	\N	3074	583	240
7271	\N	3074	597	240
7279	\N	3074	586	240
7289	\N	3074	550	240
7292	\N	3074	589	240
7294	\N	3074	131	240
7302	\N	3074	598	240
7312	\N	3074	594	240
4584	\N	3074	519	194
4595	\N	3074	497	194
4597	\N	3074	526	194
4601	\N	3074	530	194
4604	\N	3074	533	194
4607	\N	3074	499	194
4608	\N	3074	535	194
4617	\N	3074	539	194
4619	\N	3074	540	194
4620	\N	3074	501	194
14591	\N	3075	263	734
14592	\N	3075	264	734
14593	\N	3075	267	734
14594	\N	3075	265	734
14595	\N	3075	914	734
14596	\N	3075	266	734
14597	\N	3075	913	734
10397	\N	3075	163	458
10398	\N	3075	682	458
10400	\N	3075	683	458
10401	\N	3075	684	458
10402	\N	3075	156	458
10403	\N	3075	165	458
10404	\N	3075	152	458
10405	\N	3075	685	458
10407	\N	3075	686	458
10408	\N	3075	151	458
10409	\N	3075	159	458
10410	\N	3075	690	458
10412	\N	3075	167	458
10413	\N	3075	162	458
10414	\N	3075	691	458
10415	\N	3075	687	458
10416	\N	3075	166	458
10418	\N	3075	164	458
10419	\N	3075	157	458
10420	\N	3075	154	458
10421	\N	3075	688	458
10423	\N	3075	168	458
10424	\N	3075	161	458
9658	\N	3075	662	435
9659	\N	3075	149	435
9660	\N	3075	138	435
9662	\N	3075	663	435
9663	\N	3075	671	435
9664	\N	3075	148	435
9665	\N	3075	680	435
9666	\N	3075	664	435
9667	\N	3075	672	435
9668	\N	3075	139	435
9670	\N	3075	143	435
9673	\N	3075	674	435
9674	\N	3075	665	435
9675	\N	3075	675	435
9676	\N	3075	666	435
9677	\N	3075	142	435
9679	\N	3075	146	435
9681	\N	3075	145	435
9682	\N	3075	141	435
9683	\N	3075	668	435
9684	\N	3075	144	435
9687	\N	3075	670	435
9688	\N	3075	147	435
9690	\N	3075	678	435
7260	\N	3075	554	240
7262	\N	3075	555	240
7265	\N	3075	545	240
7266	\N	3075	558	240
7267	\N	3075	559	240
7272	\N	3075	561	240
7273	\N	3075	562	240
7274	\N	3075	584	240
7275	\N	3075	563	240
7276	\N	3075	585	240
7280	\N	3075	565	240
7281	\N	3075	587	240
7282	\N	3075	566	240
7283	\N	3075	548	240
7287	\N	3075	569	240
7290	\N	3075	588	240
7293	\N	3075	571	240
7295	\N	3075	572	240
7297	\N	3075	574	240
7300	\N	3075	551	240
7301	\N	3075	575	240
7305	\N	3075	591	240
7306	\N	3075	577	240
7307	\N	3075	599	240
7309	\N	3075	552	240
7311	\N	3075	593	240
7315	\N	3075	595	240
7319	\N	3075	580	240
7320	\N	3075	581	240
4574	\N	3075	513	194
4575	\N	3075	514	194
4576	\N	3075	515	194
4580	\N	3075	518	194
4581	\N	3075	505	194
4587	\N	3075	494	194
4588	\N	3075	520	194
4589	\N	3075	495	194
4590	\N	3075	521	194
4592	\N	3075	522	194
4593	\N	3075	523	194
4594	\N	3075	524	194
4598	\N	3075	527	194
4599	\N	3075	528	194
4602	\N	3075	531	194
4605	\N	3075	534	194
4609	\N	3075	536	194
4610	\N	3075	508	194
4611	\N	3075	509	194
4615	\N	3075	538	194
4616	\N	3075	500	194
4621	\N	3075	541	194
4625	\N	3075	504	194
7263	\N	3076	556	240
7264	\N	3076	557	240
7277	\N	3076	564	240
7285	\N	3076	567	240
7291	\N	3076	132	240
7298	\N	3076	134	240
7299	\N	3076	590	240
7303	\N	3076	576	240
7310	\N	3076	592	240
7313	\N	3076	579	240
7316	\N	3076	601	240
7317	\N	3076	553	240
4577	\N	3076	516	194
4578	\N	3076	517	194
4579	\N	3076	492	194
4582	\N	3076	502	194
4585	\N	3076	506	194
4586	\N	3076	507	194
4596	\N	3076	525	194
4606	\N	3076	498	194
4614	\N	3076	510	194
4622	\N	3076	542	194
4624	\N	3076	543	194
4626	\N	3076	544	194
14602	\N	3077	265	735
14603	\N	3077	914	735
14605	\N	3079	913	735
14599	\N	3078	263	735
14600	\N	3078	264	735
14601	\N	3078	267	735
14604	\N	3078	266	735
14620	\N	3086	266	737
14614	\N	3085	262	737
14615	\N	3085	263	737
14616	\N	3085	264	737
14617	\N	3085	267	737
14618	\N	3085	265	737
14619	\N	3085	914	737
14621	\N	3085	913	737
14622	\N	3089	262	738
14624	\N	3089	264	738
14625	\N	3089	267	738
14627	\N	3089	914	738
14628	\N	3089	266	738
14629	\N	3089	913	738
14623	\N	3092	263	738
14626	\N	3092	265	738
14657	\N	3117	267	742
14661	\N	3117	913	742
14656	\N	3120	264	742
14660	\N	3120	266	742
14664	\N	3048	264	743
14665	\N	3048	267	743
14667	\N	3048	914	743
14668	\N	3048	266	743
14662	\N	3047	262	743
14663	\N	3046	263	743
14666	\N	3046	265	743
14669	\N	3046	913	743
14670	\N	3052	262	744
14675	\N	3052	914	744
5617	\N	3052	557	213
5621	\N	3052	546	213
5622	\N	3052	583	213
5625	\N	3052	561	213
5628	\N	3052	563	213
5632	\N	3052	586	213
5634	\N	3052	587	213
5635	\N	3052	566	213
5638	\N	3052	567	213
5639	\N	3052	568	213
5642	\N	3052	550	213
5646	\N	3052	571	213
5647	\N	3052	131	213
5650	\N	3052	574	213
5654	\N	3052	575	213
5655	\N	3052	598	213
5657	\N	3052	133	213
5658	\N	3052	591	213
5661	\N	3052	578	213
5663	\N	3052	592	213
5669	\N	3052	601	213
5673	\N	3052	581	213
4904	\N	3052	518	200
4907	\N	3052	493	200
4910	\N	3052	507	200
4911	\N	3052	494	200
4915	\N	3052	496	200
4916	\N	3052	522	200
4919	\N	3052	497	200
4920	\N	3052	525	200
4921	\N	3052	526	200
4924	\N	3052	529	200
4926	\N	3052	531	200
4932	\N	3052	535	200
4937	\N	3052	130	200
4942	\N	3052	503	200
4943	\N	3052	540	200
4946	\N	3052	542	200
4947	\N	3052	511	200
4948	\N	3052	543	200
4949	\N	3052	504	200
14671	\N	3051	263	744
14672	\N	3051	264	744
14673	\N	3051	267	744
14676	\N	3051	266	744
5618	\N	3051	545	213
5627	\N	3051	584	213
5629	\N	3051	585	213
5630	\N	3051	564	213
5633	\N	3051	565	213
5636	\N	3051	548	213
5640	\N	3051	569	213
5643	\N	3051	588	213
5645	\N	3051	589	213
5648	\N	3051	572	213
5649	\N	3051	573	213
5653	\N	3051	551	213
5659	\N	3051	577	213
5660	\N	3051	599	213
5670	\N	3051	553	213
5671	\N	3051	596	213
5672	\N	3051	580	213
4899	\N	3051	514	200
4901	\N	3051	516	200
4902	\N	3051	517	200
4903	\N	3051	492	200
4909	\N	3051	506	200
4923	\N	3051	528	200
4927	\N	3051	532	200
4929	\N	3051	534	200
4935	\N	3051	509	200
4941	\N	3051	539	200
14674	\N	3050	265	744
14677	\N	3050	913	744
5613	\N	3050	554	213
5614	\N	3050	582	213
5615	\N	3050	555	213
5616	\N	3050	556	213
5619	\N	3050	558	213
5620	\N	3050	559	213
5623	\N	3050	560	213
5624	\N	3050	597	213
5626	\N	3050	562	213
5631	\N	3050	547	213
5637	\N	3050	549	213
5641	\N	3050	570	213
5644	\N	3050	132	213
5652	\N	3050	590	213
5656	\N	3050	576	213
5662	\N	3050	552	213
5664	\N	3050	593	213
5665	\N	3050	594	213
5666	\N	3050	579	213
5667	\N	3050	600	213
5668	\N	3050	595	213
4897	\N	3050	512	200
4898	\N	3050	513	200
4900	\N	3050	515	200
4906	\N	3050	502	200
4908	\N	3050	519	200
4912	\N	3050	520	200
4913	\N	3050	495	200
4914	\N	3050	521	200
4918	\N	3050	524	200
4922	\N	3050	527	200
4928	\N	3050	533	200
4930	\N	3050	498	200
4931	\N	3050	499	200
4933	\N	3050	536	200
4934	\N	3050	508	200
4936	\N	3050	537	200
4939	\N	3050	538	200
4940	\N	3050	500	200
4944	\N	3050	501	200
4950	\N	3050	544	200
5651	\N	3049	134	213
4905	\N	3049	505	200
4917	\N	3049	523	200
4925	\N	3049	530	200
4938	\N	3049	510	200
4945	\N	3049	541	200
14679	\N	3055	263	745
14680	\N	3055	264	745
14683	\N	3055	914	745
14678	\N	3054	262	745
14681	\N	3054	267	745
14682	\N	3054	265	745
14684	\N	3054	266	745
14685	\N	3054	913	745
14687	\N	3058	263	746
14688	\N	3058	264	746
14689	\N	3058	267	746
14691	\N	3058	914	746
14692	\N	3058	266	746
14693	\N	3058	913	746
14690	\N	3059	265	746
14686	\N	3057	262	746
14707	\N	3105	914	748
14709	\N	3105	913	748
14702	\N	3106	262	748
14703	\N	3106	263	748
14704	\N	3106	264	748
14705	\N	3106	267	748
2505	\N	3108	81	150
14712	\N	3111	264	749
14717	\N	3111	913	749
14714	\N	3112	265	749
14711	\N	3110	263	749
14715	\N	3110	914	749
14716	\N	3110	266	749
14722	\N	3114	265	750
14718	\N	3115	262	750
14723	\N	3115	914	750
14724	\N	3115	266	750
14725	\N	3115	913	750
14720	\N	3116	264	750
14569	\N	3985	267	731
14861	\N	3988	915	766
14862	\N	3988	269	766
14863	\N	3988	270	766
14864	\N	3988	268	766
14865	\N	3988	919	766
14866	\N	3988	271	766
14933	\N	4394	915	774
14935	\N	4394	270	774
14936	\N	4394	268	774
14939	\N	4394	918	774
14940	\N	4394	916	774
14607	\N	4361	263	736
14906	\N	4362	915	771
14908	\N	4363	270	771
14907	\N	4364	269	771
14910	\N	4364	919	771
14911	\N	4364	271	771
14912	\N	4364	918	771
14913	\N	4364	916	771
14914	\N	4364	917	771
14606	\N	4364	262	736
14608	\N	4364	264	736
14609	\N	4364	267	736
14610	\N	4364	265	736
14611	\N	4364	914	736
14612	\N	4364	266	736
14997	\N	3146	924	781
14996	\N	3145	921	781
15000	\N	3145	920	781
15001	\N	3145	922	781
15003	\N	3145	923	781
14999	\N	3148	926	781
15002	\N	3148	925	781
15004	\N	3154	921	782
15008	\N	3156	920	782
15005	\N	3155	924	782
15006	\N	3155	272	782
15007	\N	3155	926	782
15009	\N	3155	922	782
15010	\N	3155	925	782
15011	\N	3155	923	782
15013	\N	3151	924	783
15016	\N	3149	920	783
15012	\N	3150	921	783
15015	\N	3150	926	783
15017	\N	3150	922	783
15019	\N	3150	923	783
15018	\N	3152	925	783
15026	\N	3159	925	784
15024	\N	3157	920	784
15025	\N	3157	922	784
15020	\N	3160	921	784
15021	\N	3160	924	784
15022	\N	3160	272	784
15023	\N	3160	926	784
15027	\N	3160	923	784
15032	\N	3142	920	785
15033	\N	3142	922	785
15035	\N	3143	923	785
15028	\N	3144	921	785
15029	\N	3144	924	785
15031	\N	3144	926	785
15034	\N	3144	925	785
15079	\N	3188	942	791
15081	\N	3188	944	791
15082	\N	3188	278	791
15083	\N	3188	275	791
15084	\N	3188	941	791
15086	\N	3188	943	791
15087	\N	3186	935	791
15088	\N	3186	936	791
15089	\N	3186	937	791
15090	\N	3186	938	791
15077	\N	3187	277	791
15078	\N	3187	276	791
15080	\N	3187	274	791
15085	\N	3187	939	791
15091	\N	3187	940	791
15105	\N	3193	937	792
15092	\N	3195	273	792
15093	\N	3195	277	792
15094	\N	3195	276	792
15095	\N	3195	942	792
15096	\N	3195	274	792
15097	\N	3195	944	792
15098	\N	3195	278	792
15099	\N	3195	275	792
15100	\N	3195	941	792
15101	\N	3195	939	792
15102	\N	3195	943	792
15103	\N	3195	935	792
15104	\N	3195	936	792
15106	\N	3195	938	792
15107	\N	3195	940	792
15109	\N	3191	277	793
15108	\N	3190	273	793
15111	\N	3190	942	793
15113	\N	3190	944	793
15114	\N	3190	278	793
15116	\N	3190	941	793
15117	\N	3190	939	793
15118	\N	3190	943	793
15119	\N	3190	935	793
15121	\N	3190	937	793
15122	\N	3190	938	793
15123	\N	3190	940	793
15120	\N	3192	936	793
15124	\N	3199	273	794
15129	\N	3199	944	794
15137	\N	3199	937	794
15125	\N	3200	277	794
15126	\N	3200	276	794
15127	\N	3200	942	794
15128	\N	3200	274	794
15130	\N	3200	278	794
15131	\N	3200	275	794
15132	\N	3200	941	794
15133	\N	3200	939	794
15134	\N	3200	943	794
15135	\N	3200	935	794
15136	\N	3200	936	794
15138	\N	3200	938	794
15139	\N	3200	940	794
15145	\N	3182	944	795
15148	\N	3182	941	795
15155	\N	3182	940	795
15140	\N	3184	273	795
15142	\N	3184	276	795
15143	\N	3184	942	795
15144	\N	3184	274	795
15147	\N	3184	275	795
15149	\N	3184	939	795
15150	\N	3184	943	795
15151	\N	3184	935	795
15152	\N	3184	936	795
15153	\N	3184	937	795
15154	\N	3184	938	795
15237	\N	3234	282	801
15238	\N	3234	959	801
15239	\N	3234	960	801
15240	\N	3234	958	801
15241	\N	3234	961	801
15243	\N	3234	962	801
15236	\N	3236	963	801
15242	\N	3236	964	801
15244	\N	3237	963	802
15245	\N	3240	282	802
15246	\N	3240	959	802
15247	\N	3240	960	802
15248	\N	3240	958	802
15249	\N	3240	961	802
15250	\N	3240	964	802
15251	\N	3240	962	802
15260	\N	3227	963	804
15262	\N	3227	959	804
15263	\N	3227	960	804
15264	\N	3227	958	804
15266	\N	3226	964	804
15261	\N	3225	282	804
15265	\N	3225	961	804
15267	\N	3225	962	804
15268	\N	3231	963	805
15269	\N	3231	282	805
15271	\N	3231	960	805
15272	\N	3231	958	805
15273	\N	3231	961	805
15274	\N	3231	964	805
15275	\N	3229	962	805
15270	\N	3232	959	805
14955	\N	3696	919	776
14956	\N	3696	271	776
14958	\N	3696	916	776
14959	\N	3696	917	776
14696	\N	3696	264	747
14697	\N	3696	267	747
14699	\N	3696	914	747
14941	\N	4394	917	774
14630	\N	4394	262	739
14631	\N	4394	263	739
14632	\N	4394	264	739
14634	\N	4394	265	739
14635	\N	4394	914	739
14636	\N	4394	266	739
14637	\N	4394	913	739
14937	\N	4395	919	774
14938	\N	4395	271	774
14633	\N	4393	267	739
15279	\N	4398	966	806
15280	\N	4397	965	806
15315	\N	3278	975	811
15301	\N	3277	970	811
15305	\N	3277	971	811
15308	\N	3277	974	811
15312	\N	3277	972	811
15313	\N	3277	973	811
15303	\N	3279	977	811
15304	\N	3279	284	811
15306	\N	3279	285	811
15307	\N	3279	286	811
15309	\N	3279	976	811
15310	\N	3279	978	811
15311	\N	3279	979	811
15314	\N	3279	980	811
15362	\N	3270	283	815
15365	\N	3270	971	815
15373	\N	3270	973	815
15374	\N	3270	980	815
15369	\N	3269	976	815
15361	\N	3271	970	815
15364	\N	3271	284	815
15371	\N	3271	979	815
15372	\N	3271	972	815
15375	\N	3271	975	815
15363	\N	3272	977	815
15368	\N	3272	974	815
15370	\N	3272	978	815
15257	\N	4400	961	803
15258	\N	4400	964	803
15259	\N	4400	962	803
15329	\N	4516	980	812
15430	\N	4514	290	819
15431	\N	4514	289	819
15432	\N	4514	981	819
15435	\N	4514	986	819
15436	\N	4514	988	819
15438	\N	4514	287	819
15439	\N	4514	288	819
15440	\N	4514	983	819
15441	\N	4514	984	819
15442	\N	4514	985	819
15443	\N	4514	987	819
15444	\N	4514	993	819
15446	\N	4514	990	819
15447	\N	4514	994	819
15318	\N	4514	977	812
15319	\N	4514	284	812
15323	\N	4514	974	812
15324	\N	4514	976	812
15325	\N	4514	978	812
15326	\N	4514	979	812
15327	\N	4514	972	812
15328	\N	4514	973	812
15330	\N	4514	975	812
15433	\N	4513	992	819
15434	\N	4513	982	819
15437	\N	4513	991	819
15445	\N	4513	989	819
15316	\N	4513	970	812
15317	\N	4513	283	812
15320	\N	4513	971	812
15321	\N	4513	285	812
15322	\N	4513	286	812
12272	\N	4513	759	571
12273	\N	4513	760	571
12276	\N	4513	761	571
12277	\N	4513	762	571
12279	\N	4513	758	571
12274	\N	4515	766	571
12278	\N	4515	765	571
15500	\N	3314	999	825
15499	\N	3315	997	825
15502	\N	3315	996	825
15503	\N	3315	1002	825
15504	\N	3315	998	825
15505	\N	3315	1000	825
15498	\N	3316	1001	825
15501	\N	3316	995	825
15515	\N	4538	291	827
15466	\N	4538	1001	821
15471	\N	4539	1002	821
15541	\N	3347	1011	831
15544	\N	3347	1015	831
15547	\N	3347	1012	831
15548	\N	3347	293	831
15550	\N	3347	1014	831
15553	\N	3347	1013	831
15543	\N	3346	292	831
15546	\N	3345	1009	831
15551	\N	3345	1010	831
15542	\N	3348	1016	831
15545	\N	3348	1021	831
15549	\N	3348	1019	831
15552	\N	3348	1017	831
15554	\N	3348	1018	831
15555	\N	3348	1020	831
15556	\N	3348	1022	831
15591	\N	3359	292	834
15593	\N	3359	1021	834
15589	\N	3360	1011	834
15590	\N	3360	1016	834
15592	\N	3360	1015	834
15594	\N	3360	1009	834
15595	\N	3360	1012	834
15596	\N	3360	293	834
15597	\N	3360	1019	834
15598	\N	3360	1014	834
15599	\N	3360	1010	834
15600	\N	3360	1017	834
15601	\N	3360	1013	834
15602	\N	3360	1018	834
15603	\N	3360	1020	834
15604	\N	3360	1022	834
15506	\N	3586	1003	826
15507	\N	3586	1005	826
15509	\N	3586	1007	826
15510	\N	3586	1004	826
15511	\N	3586	1006	826
15512	\N	3586	1008	826
15482	\N	3586	1001	823
15483	\N	3586	997	823
15484	\N	3586	999	823
15486	\N	3586	996	823
15487	\N	3586	1002	823
15488	\N	3586	998	823
15489	\N	3586	1000	823
15508	\N	3587	291	826
12670	\N	3587	825	611
12674	\N	3587	826	611
15485	\N	3587	995	823
15537	\N	4038	1007	830
15538	\N	4038	1004	830
15540	\N	4038	1008	830
15493	\N	4038	995	824
15496	\N	4038	998	824
15535	\N	4039	1005	830
15539	\N	4039	1006	830
15490	\N	4039	1001	824
15491	\N	4039	997	824
15494	\N	4039	996	824
15495	\N	4039	1002	824
15497	\N	4039	1000	824
15534	\N	4040	1003	830
15536	\N	4040	291	830
15492	\N	4037	999	824
15524	\N	4532	1004	828
15525	\N	4532	1006	828
15526	\N	4532	1008	828
15475	\N	4532	997	822
15476	\N	4532	999	822
15479	\N	4532	1002	822
15481	\N	4532	1000	822
15520	\N	4530	1003	828
15521	\N	4530	1005	828
15522	\N	4530	291	828
15523	\N	4530	1007	828
15474	\N	4530	1001	822
15477	\N	4530	995	822
15478	\N	4531	996	822
15480	\N	4531	998	822
15513	\N	4537	1003	827
15514	\N	4537	1005	827
15516	\N	4537	1007	827
15517	\N	4537	1004	827
15518	\N	4537	1006	827
15519	\N	4537	1008	827
15467	\N	4537	997	821
15468	\N	4537	999	821
15469	\N	4537	995	821
15470	\N	4537	996	821
15472	\N	4537	998	821
15473	\N	4537	1000	821
15671	\N	4542	1025	839
15672	\N	4542	1026	839
15676	\N	4542	1031	839
15679	\N	4542	1035	839
15605	\N	4542	1011	835
15607	\N	4542	292	835
15609	\N	4542	1021	835
15613	\N	4542	1019	835
15614	\N	4542	1014	835
15697	\N	3390	1038	841
15698	\N	3390	1040	841
15699	\N	3390	1041	841
15700	\N	3390	296	841
15701	\N	3390	1037	841
15703	\N	3390	1039	841
11811	\N	3390	181	530
11817	\N	3390	702	530
11819	\N	3390	186	530
11329	\N	3390	173	497
11332	\N	3390	174	497
11333	\N	3390	695	497
11336	\N	3390	180	497
11338	\N	3390	178	497
11339	\N	3390	179	497
11340	\N	3390	176	497
15696	\N	3392	1042	841
15702	\N	3392	1043	841
11813	\N	3392	699	530
11815	\N	3392	182	530
11331	\N	3392	692	497
11334	\N	3392	693	497
11341	\N	3392	171	497
11346	\N	3392	170	497
11816	\N	3391	701	530
11344	\N	3391	169	497
11345	\N	3391	697	497
15704	\N	3389	1036	841
11809	\N	3389	183	530
11810	\N	3389	698	530
11812	\N	3389	184	530
11814	\N	3389	700	530
11818	\N	3389	185	530
11820	\N	3389	703	530
11330	\N	3389	694	497
11335	\N	3389	172	497
11342	\N	3389	696	497
15615	\N	4542	1010	835
15616	\N	4542	1017	835
15620	\N	4542	1022	835
15669	\N	4543	1032	839
15611	\N	4543	1012	835
15651	\N	4789	1027	838
15652	\N	4789	1029	838
15653	\N	4789	295	838
15655	\N	4789	1024	838
15656	\N	4789	1025	838
15659	\N	4789	1028	838
15661	\N	4789	1031	838
15662	\N	4789	1033	838
15663	\N	4789	1034	838
15664	\N	4789	1035	838
15557	\N	4789	1011	832
15558	\N	4789	1016	832
15560	\N	4789	1015	832
15563	\N	4789	1012	832
15565	\N	4789	1019	832
15566	\N	4789	1014	832
15568	\N	4789	1017	832
15569	\N	4789	1013	832
15570	\N	4789	1018	832
15571	\N	4789	1020	832
15654	\N	4790	1032	838
15657	\N	4790	1026	838
15564	\N	4790	293	832
15665	\N	4791	1023	838
15562	\N	4791	1009	832
15660	\N	4792	1030	838
15561	\N	4792	1021	832
15567	\N	4792	1010	832
15572	\N	4792	1022	832
15635	\N	4834	1023	836
15573	\N	4834	1011	833
15574	\N	4834	1016	833
15576	\N	4834	1015	833
15583	\N	4834	1010	833
15584	\N	4834	1017	833
15588	\N	4834	1022	833
12771	\N	4834	219	621
12772	\N	4834	221	621
12774	\N	4834	839	621
12775	\N	4834	841	621
12776	\N	4834	842	621
12778	\N	4834	838	621
12781	\N	4834	840	621
12782	\N	4834	843	621
12783	\N	4834	844	621
12784	\N	4834	220	621
15621	\N	4835	1027	836
15622	\N	4835	1029	836
15623	\N	4835	295	836
15624	\N	4835	1032	836
15625	\N	4835	1024	836
15626	\N	4835	1025	836
15627	\N	4835	1026	836
15628	\N	4835	294	836
15736	\N	3381	296	845
15732	\N	3384	1042	845
15733	\N	3384	1038	845
15734	\N	3384	1040	845
15735	\N	3384	1041	845
15737	\N	3384	1037	845
15738	\N	3384	1043	845
15739	\N	3384	1039	845
15740	\N	3384	1036	845
15750	\N	4861	1045	847
15751	\N	4861	1047	847
15752	\N	4864	1048	847
15755	\N	4864	1044	847
15756	\N	4864	1046	847
15757	\N	4864	1051	847
15758	\N	4864	1052	847
15717	\N	4864	1041	843
15754	\N	4863	1050	847
15715	\N	4863	1038	843
15719	\N	4863	1037	843
15720	\N	4863	1043	843
15721	\N	4863	1039	843
15753	\N	4862	1049	847
15714	\N	4862	1042	843
15716	\N	4862	1040	843
15722	\N	4862	1036	843
6533	\N	4827	545	228
6550	\N	4827	566	228
6555	\N	4827	569	228
6560	\N	4827	589	228
6562	\N	4827	131	228
6568	\N	4827	551	228
6570	\N	4827	598	228
4865	\N	4827	497	199
4877	\N	4827	499	199
4879	\N	4827	536	199
15711	\N	4827	1043	842
15768	\N	4826	1045	849
15769	\N	4826	1047	849
15770	\N	4826	1048	849
15771	\N	4826	1049	849
15772	\N	4826	1050	849
15773	\N	4826	1044	849
15774	\N	4826	1046	849
10847	\N	4941	160	474
10189	\N	4941	146	450
10195	\N	4941	677	450
10198	\N	4941	147	450
15729	\N	4941	1043	844
1213	\N	4941	487	121
1231	\N	4941	488	121
1241	\N	4941	74	121
15780	\N	4942	1049	850
15781	\N	4942	1050	850
15782	\N	4942	1044	850
15784	\N	4942	1051	850
15785	\N	4942	1052	850
10845	\N	4942	163	474
10846	\N	4942	682	474
10848	\N	4942	683	474
10853	\N	4942	685	474
10855	\N	4942	686	474
10856	\N	4942	151	474
10858	\N	4942	690	474
10860	\N	4942	167	474
10861	\N	4942	162	474
10862	\N	4942	691	474
10863	\N	4942	687	474
10864	\N	4942	166	474
10866	\N	4942	164	474
10867	\N	4942	157	474
10868	\N	4942	154	474
10872	\N	4942	161	474
1220	\N	4942	83	121
1221	\N	4942	61	121
1223	\N	4942	79	121
1226	\N	4942	62	121
1227	\N	4942	72	121
1228	\N	4942	63	121
1229	\N	4942	81	121
1230	\N	4942	64	121
1232	\N	4942	489	121
15723	\N	4942	1042	844
15724	\N	4942	1038	844
15728	\N	4942	1037	844
15730	\N	4942	1039	844
15731	\N	4942	1036	844
1202	\N	4942	51	121
1205	\N	4942	53	121
1208	\N	4942	55	121
1210	\N	4942	57	121
1212	\N	4942	59	121
1214	\N	4942	80	121
1215	\N	4942	75	121
1218	\N	4942	60	121
1219	\N	4942	84	121
1233	\N	4942	70	121
1234	\N	4942	65	121
1238	\N	4942	490	121
1239	\N	4942	66	121
1242	\N	4942	68	121
1244	\N	4942	77	121
10170	\N	4942	138	450
10173	\N	4942	671	450
10176	\N	4942	664	450
10177	\N	4942	672	450
10181	\N	4942	673	450
10185	\N	4942	675	450
10186	\N	4942	666	450
10187	\N	4942	142	450
10188	\N	4942	676	450
10190	\N	4942	667	450
10191	\N	4942	145	450
10192	\N	4942	141	450
10194	\N	4942	144	450
10197	\N	4942	670	450
10199	\N	4942	681	450
10200	\N	4942	678	450
15783	\N	4944	1046	850
15725	\N	4944	1040	844
15726	\N	4944	1041	844
1204	\N	4944	73	121
1222	\N	4944	76	121
8931	\N	3440	660	292
8908	\N	3437	654	292
8930	\N	3437	638	292
7800	\N	3437	606	256
7802	\N	3437	627	256
7805	\N	3437	611	256
15847	\N	3424	1062	855
15848	\N	3424	1054	855
15849	\N	3424	1063	855
15850	\N	3424	1057	855
15851	\N	3424	1058	855
15852	\N	3424	1064	855
15853	\N	3424	1059	855
15854	\N	3424	1060	855
15855	\N	3424	1061	855
15898	\N	4489	1068	859
15923	\N	3541	300	861
15934	\N	3541	318	861
15939	\N	3541	1077	861
15941	\N	3541	319	861
15946	\N	3541	315	861
15918	\N	3543	303	861
15919	\N	3543	305	861
15921	\N	3543	1078	861
15924	\N	3543	1079	861
15925	\N	3543	309	861
15926	\N	3543	316	861
15927	\N	3543	301	861
15928	\N	3543	1080	861
15789	\N	4489	1053	851
15790	\N	4489	298	851
15897	\N	4492	1072	859
4730	\N	4492	542	196
4733	\N	4492	504	196
6102	\N	4492	582	221
6109	\N	4492	546	221
6113	\N	4492	561	221
6121	\N	4492	565	221
6140	\N	4492	590	221
1236	\N	4944	71	121
10184	\N	4944	665	450
15777	\N	4943	1045	850
15778	\N	4943	1047	850
15779	\N	4943	1048	850
15727	\N	4943	296	844
10849	\N	4943	684	474
10850	\N	4943	156	474
10851	\N	4943	165	474
10852	\N	4943	152	474
10854	\N	4943	158	474
10857	\N	4943	159	474
10869	\N	4943	688	474
10870	\N	4943	689	474
10871	\N	4943	168	474
1201	\N	4943	50	121
1203	\N	4943	52	121
1206	\N	4943	54	121
1209	\N	4943	56	121
1211	\N	4943	58	121
1216	\N	4943	491	121
1217	\N	4943	82	121
1225	\N	4943	78	121
1240	\N	4943	67	121
1243	\N	4943	69	121
10167	\N	4943	140	450
10168	\N	4943	662	450
10171	\N	4943	679	450
10172	\N	4943	663	450
10174	\N	4943	148	450
10175	\N	4943	680	450
10179	\N	4943	150	450
10180	\N	4943	143	450
10182	\N	4943	137	450
10183	\N	4943	674	450
10193	\N	4943	668	450
10196	\N	4943	669	450
15929	\N	3543	310	861
15930	\N	3543	306	861
15931	\N	3543	320	861
15932	\N	3543	302	861
15933	\N	3543	1081	861
15935	\N	3543	1082	861
15937	\N	3543	1083	861
15938	\N	3543	1084	861
15940	\N	3543	311	861
15942	\N	3543	312	861
15943	\N	3543	317	861
15944	\N	3543	313	861
15945	\N	3543	314	861
15947	\N	3543	304	861
15916	\N	3544	307	861
15920	\N	3544	308	861
15922	\N	3544	1085	861
15936	\N	3544	1086	861
15976	\N	3547	313	862
15978	\N	3547	315	862
15953	\N	3545	1078	862
15964	\N	3545	302	862
15948	\N	3548	307	862
15949	\N	3548	299	862
15950	\N	3548	303	862
15951	\N	3548	305	862
15952	\N	3548	308	862
15954	\N	3548	1085	862
15955	\N	3548	300	862
15956	\N	3548	1079	862
15957	\N	3548	309	862
15958	\N	3548	316	862
15959	\N	3548	301	862
15960	\N	3548	1080	862
15961	\N	3548	310	862
15962	\N	3548	306	862
15963	\N	3548	320	862
15965	\N	3548	1081	862
15966	\N	3548	318	862
15967	\N	3548	1082	862
15968	\N	3548	1086	862
15969	\N	3548	1083	862
15970	\N	3548	1084	862
15971	\N	3548	1077	862
15972	\N	3548	311	862
15973	\N	3548	319	862
15974	\N	3548	312	862
15975	\N	3548	317	862
15977	\N	3548	314	862
15979	\N	3548	304	862
15980	\N	3551	307	863
15981	\N	3551	299	863
15983	\N	3551	305	863
15984	\N	3551	308	863
15985	\N	3551	1078	863
15986	\N	3551	1085	863
15988	\N	3551	1079	863
15989	\N	3551	309	863
15990	\N	3551	316	863
15991	\N	3551	301	863
15992	\N	3551	1080	863
15993	\N	3551	310	863
15994	\N	3551	306	863
15995	\N	3551	320	863
15996	\N	3551	302	863
15997	\N	3551	1081	863
15998	\N	3551	318	863
15999	\N	3551	1082	863
16000	\N	3551	1086	863
16001	\N	3551	1083	863
16002	\N	3551	1084	863
16004	\N	3551	311	863
16005	\N	3551	319	863
16006	\N	3551	312	863
16007	\N	3551	317	863
16008	\N	3551	313	863
16009	\N	3551	314	863
16010	\N	3551	315	863
16011	\N	3551	304	863
15982	\N	3549	303	863
16003	\N	3549	1077	863
16017	\N	3553	1078	864
16019	\N	3553	300	864
16020	\N	3553	1079	864
16021	\N	3553	309	864
16030	\N	3553	318	864
16032	\N	3553	1086	864
16033	\N	3553	1083	864
16039	\N	3553	317	864
16040	\N	3553	313	864
16043	\N	3553	304	864
16036	\N	3555	311	864
16026	\N	3556	306	864
16012	\N	3554	307	864
16013	\N	3554	299	864
16014	\N	3554	303	864
16015	\N	3554	305	864
16016	\N	3554	308	864
16018	\N	3554	1085	864
16022	\N	3554	316	864
16024	\N	3554	1080	864
16025	\N	3554	310	864
16027	\N	3554	320	864
16028	\N	3554	302	864
16029	\N	3554	1081	864
16031	\N	3554	1082	864
16034	\N	3554	1084	864
16035	\N	3554	1077	864
16037	\N	3554	319	864
16038	\N	3554	312	864
16041	\N	3554	314	864
16042	\N	3554	315	864
16049	\N	3558	1078	865
16052	\N	3558	1079	865
16059	\N	3558	320	865
16062	\N	3558	318	865
16063	\N	3558	1082	865
16064	\N	3558	1086	865
16074	\N	3558	315	865
16044	\N	3560	307	865
16045	\N	3560	299	865
16046	\N	3560	303	865
16047	\N	3560	305	865
16048	\N	3560	308	865
16050	\N	3560	1085	865
16053	\N	3560	309	865
16054	\N	3560	316	865
16055	\N	3560	301	865
16056	\N	3560	1080	865
16057	\N	3560	310	865
16058	\N	3560	306	865
16061	\N	3560	1081	865
16065	\N	3560	1083	865
16066	\N	3560	1084	865
16067	\N	3560	1077	865
16068	\N	3560	311	865
16069	\N	3560	319	865
16071	\N	3560	317	865
16072	\N	3560	313	865
16075	\N	3560	304	865
7229	\N	3560	588	239
7232	\N	3560	571	239
7234	\N	3560	572	239
7236	\N	3560	574	239
7237	\N	3560	134	239
7239	\N	3560	551	239
7245	\N	3560	577	239
7247	\N	3560	578	239
7248	\N	3560	552	239
7254	\N	3560	595	239
7257	\N	3560	596	239
7258	\N	3560	580	239
4520	\N	3560	513	193
4521	\N	3560	514	193
4524	\N	3560	517	193
4526	\N	3560	518	193
4532	\N	3560	507	193
4533	\N	3560	494	193
4535	\N	3560	495	193
4538	\N	3560	522	193
4539	\N	3560	523	193
4540	\N	3560	524	193
4541	\N	3560	497	193
4543	\N	3560	526	193
4547	\N	3560	530	193
4548	\N	3560	531	193
4553	\N	3560	499	193
4554	\N	3560	535	193
4558	\N	3560	537	193
4561	\N	3560	538	193
4562	\N	3560	500	193
4564	\N	3560	503	193
4566	\N	3560	501	193
4569	\N	3560	511	193
16070	\N	3559	312	865
16073	\N	3559	314	865
7209	\N	3559	560	239
7218	\N	3559	586	239
7222	\N	3559	548	239
7224	\N	3559	567	239
7238	\N	3559	590	239
7240	\N	3559	575	239
4522	\N	3559	515	193
4523	\N	3559	516	193
4530	\N	3559	519	193
4542	\N	3559	525	193
4549	\N	3559	532	193
4551	\N	3559	534	193
4555	\N	3559	536	193
4557	\N	3559	509	193
4567	\N	3559	541	193
4570	\N	3559	543	193
16079	\N	3523	305	866
16089	\N	3523	310	866
16090	\N	3523	306	866
16093	\N	3523	1081	866
16095	\N	3523	1082	866
16096	\N	3523	1086	866
16102	\N	3523	312	866
16103	\N	3523	317	866
16107	\N	3523	304	866
16080	\N	3522	308	866
16097	\N	3522	1083	866
16076	\N	3521	307	866
16078	\N	3521	303	866
16081	\N	3521	1078	866
16082	\N	3521	1085	866
16084	\N	3521	1079	866
16085	\N	3521	309	866
16086	\N	3521	316	866
16088	\N	3521	1080	866
16091	\N	3521	320	866
16094	\N	3521	318	866
16098	\N	3521	1084	866
16100	\N	3521	311	866
16101	\N	3521	319	866
16104	\N	3521	313	866
16105	\N	3521	314	866
16106	\N	3521	315	866
16099	\N	3524	1077	866
16145	\N	3534	1078	868
16146	\N	3534	1085	868
16163	\N	3534	1077	868
16165	\N	3534	319	868
16170	\N	3534	315	868
16140	\N	3533	307	868
16148	\N	3533	1079	868
16161	\N	3533	1083	868
16143	\N	3536	305	868
16144	\N	3536	308	868
16147	\N	3536	300	868
16149	\N	3536	309	868
16150	\N	3536	316	868
16151	\N	3536	301	868
16152	\N	3536	1080	868
16153	\N	3536	310	868
16154	\N	3536	306	868
16155	\N	3536	320	868
16156	\N	3536	302	868
16157	\N	3536	1081	868
16158	\N	3536	318	868
16159	\N	3536	1082	868
16160	\N	3536	1086	868
16162	\N	3536	1084	868
16164	\N	3536	311	868
16166	\N	3536	312	868
16167	\N	3536	317	868
16168	\N	3536	313	868
16169	\N	3536	314	868
16172	\N	3530	307	869
16180	\N	3530	1079	869
16190	\N	3530	318	869
16200	\N	3530	313	869
16174	\N	3532	303	869
16182	\N	3532	316	869
16192	\N	3532	1086	869
16193	\N	3532	1083	869
16195	\N	3532	1077	869
16197	\N	3532	319	869
16202	\N	3532	315	869
16176	\N	3529	308	869
16178	\N	3529	1085	869
16181	\N	3529	309	869
16183	\N	3529	301	869
16184	\N	3529	1080	869
16185	\N	3529	310	869
16187	\N	3529	320	869
16188	\N	3529	302	869
16189	\N	3529	1081	869
16191	\N	3529	1082	869
16194	\N	3529	1084	869
16196	\N	3529	311	869
16198	\N	3529	312	869
16199	\N	3529	317	869
16201	\N	3529	314	869
16203	\N	3529	304	869
16177	\N	3531	1078	869
16209	\N	3569	1078	870
16219	\N	3569	320	870
16229	\N	3569	319	870
16210	\N	3572	1085	870
16212	\N	3572	1079	870
16215	\N	3572	301	870
16216	\N	3572	1080	870
16218	\N	3572	306	870
16221	\N	3572	1081	870
16227	\N	3572	1077	870
16231	\N	3572	317	870
16223	\N	3571	1082	870
16224	\N	3571	1086	870
16232	\N	3571	313	870
16233	\N	3571	314	870
16206	\N	3570	303	870
16214	\N	3570	316	870
16220	\N	3570	302	870
16222	\N	3570	318	870
16225	\N	3570	1083	870
16226	\N	3570	1084	870
16234	\N	3570	315	870
16251	\N	3576	320	871
16252	\N	3575	302	871
16254	\N	3575	318	871
16256	\N	3575	1086	871
16257	\N	3575	1083	871
16236	\N	3573	307	871
16239	\N	3573	305	871
16241	\N	3573	1078	871
16242	\N	3573	1085	871
16244	\N	3573	1079	871
16246	\N	3573	316	871
16248	\N	3573	1080	871
16250	\N	3573	306	871
16253	\N	3573	1081	871
16255	\N	3573	1082	871
16258	\N	3573	1084	871
16259	\N	3573	1077	871
16261	\N	3573	319	871
16267	\N	3573	304	871
16249	\N	3574	310	871
16262	\N	3574	312	871
16263	\N	3574	317	871
16268	\N	3580	307	872
16271	\N	3580	305	872
16273	\N	3580	1078	872
16274	\N	3580	1085	872
16277	\N	3580	309	872
16283	\N	3580	320	872
16291	\N	3580	1077	872
16292	\N	3580	311	872
16298	\N	3580	315	872
16269	\N	3579	299	872
16275	\N	3579	300	872
16279	\N	3579	301	872
16280	\N	3579	1080	872
16281	\N	3579	310	872
16282	\N	3579	306	872
16284	\N	3579	302	872
16285	\N	3579	1081	872
16286	\N	3579	318	872
16288	\N	3579	1086	872
16289	\N	3579	1083	872
16290	\N	3579	1084	872
16295	\N	3579	317	872
16296	\N	3579	313	872
16297	\N	3579	314	872
16299	\N	3579	304	872
16287	\N	3578	1082	872
16270	\N	3577	303	872
16272	\N	3577	308	872
16276	\N	3577	1079	872
16293	\N	3577	319	872
16294	\N	3577	312	872
16308	\N	3583	1079	873
16310	\N	3583	316	873
16312	\N	3583	1080	873
16300	\N	3584	307	873
16301	\N	3584	299	873
16302	\N	3584	303	873
16303	\N	3584	305	873
16304	\N	3584	308	873
16305	\N	3584	1078	873
16306	\N	3584	1085	873
16309	\N	3584	309	873
16311	\N	3584	301	873
16313	\N	3584	310	873
16314	\N	3584	306	873
16315	\N	3584	320	873
16316	\N	3584	302	873
16317	\N	3584	1081	873
16318	\N	3584	318	873
16319	\N	3584	1082	873
16320	\N	3584	1086	873
16321	\N	3584	1083	873
16322	\N	3584	1084	873
16323	\N	3584	1077	873
16325	\N	3584	319	873
16326	\N	3584	312	873
16327	\N	3584	317	873
16328	\N	3584	313	873
16329	\N	3584	314	873
16330	\N	3584	315	873
16331	\N	3584	304	873
16360	\N	3585	313	874
16336	\N	3586	308	874
16341	\N	3586	309	874
16342	\N	3586	316	874
16343	\N	3586	301	874
16344	\N	3586	1080	874
16345	\N	3586	310	874
16346	\N	3586	306	874
16347	\N	3586	320	874
16348	\N	3586	302	874
16349	\N	3586	1081	874
16350	\N	3586	318	874
16351	\N	3586	1082	874
16355	\N	3586	1077	874
16356	\N	3586	311	874
16359	\N	3586	317	874
16361	\N	3586	314	874
16363	\N	3586	304	874
16333	\N	3587	299	874
16334	\N	3587	303	874
16337	\N	3587	1078	874
16338	\N	3587	1085	874
16339	\N	3587	300	874
16340	\N	3587	1079	874
16352	\N	3587	1086	874
16353	\N	3587	1083	874
16354	\N	3587	1084	874
16357	\N	3587	319	874
16358	\N	3587	312	874
16362	\N	3587	315	874
16399	\N	3593	305	876
16400	\N	3593	308	876
16402	\N	3593	1085	876
16404	\N	3593	1079	876
16405	\N	3593	309	876
16406	\N	3593	316	876
16407	\N	3593	301	876
16408	\N	3593	1080	876
16410	\N	3593	306	876
16413	\N	3593	1081	876
16415	\N	3593	1082	876
16417	\N	3593	1083	876
16419	\N	3593	1077	876
16420	\N	3593	311	876
16422	\N	3593	312	876
16423	\N	3593	317	876
16425	\N	3593	314	876
16427	\N	3593	304	876
16409	\N	3596	310	876
16411	\N	3596	320	876
16414	\N	3596	318	876
16416	\N	3596	1086	876
16426	\N	3596	315	876
16401	\N	3595	1078	876
16418	\N	3595	1084	876
16424	\N	3595	313	876
16396	\N	3594	307	876
16397	\N	3594	299	876
16398	\N	3594	303	876
16412	\N	3594	302	876
16421	\N	3594	319	876
16445	\N	3597	1081	877
16436	\N	3600	1079	877
16440	\N	3600	1080	877
16441	\N	3600	310	877
16443	\N	3600	320	877
16446	\N	3600	318	877
16447	\N	3600	1082	877
16450	\N	3600	1084	877
16458	\N	3600	315	877
16451	\N	3598	1077	877
16454	\N	3598	312	877
16456	\N	3598	313	877
16428	\N	3599	307	877
16429	\N	3599	299	877
16431	\N	3599	305	877
16432	\N	3599	308	877
16433	\N	3599	1078	877
16434	\N	3599	1085	877
16435	\N	3599	300	877
16437	\N	3599	309	877
16439	\N	3599	301	877
16448	\N	3599	1086	877
16449	\N	3599	1083	877
16453	\N	3599	319	877
16457	\N	3599	314	877
16459	\N	3599	304	877
16477	\N	3538	1081	878
16484	\N	3537	311	878
16460	\N	3539	307	878
16464	\N	3539	308	878
16466	\N	3539	1085	878
16467	\N	3539	300	878
16468	\N	3539	1079	878
16470	\N	3539	316	878
16471	\N	3539	301	878
16472	\N	3539	1080	878
16473	\N	3539	310	878
16474	\N	3539	306	878
16475	\N	3539	320	878
16476	\N	3539	302	878
16479	\N	3539	1082	878
16480	\N	3539	1086	878
16481	\N	3539	1083	878
16482	\N	3539	1084	878
16483	\N	3539	1077	878
16486	\N	3539	312	878
16487	\N	3539	317	878
16489	\N	3539	314	878
16491	\N	3539	304	878
16465	\N	3540	1078	878
16495	\N	3561	305	879
16497	\N	3561	1078	879
16498	\N	3561	1085	879
16504	\N	3561	1080	879
16508	\N	3561	302	879
16511	\N	3561	1082	879
16514	\N	3561	1084	879
16515	\N	3561	1077	879
16517	\N	3561	319	879
16519	\N	3561	317	879
16523	\N	3561	304	879
16496	\N	3563	308	879
16500	\N	3563	1079	879
16513	\N	3563	1083	879
16502	\N	3564	316	879
16509	\N	3564	1081	879
16492	\N	3562	307	879
16505	\N	3562	310	879
16512	\N	3562	1086	879
16516	\N	3562	311	879
16531	\N	3565	300	880
14223	\N	3566	909	710
13657	\N	3566	233	687
13668	\N	3566	905	687
16529	\N	3567	1078	880
16530	\N	3567	1085	880
16554	\N	3567	315	880
14211	\N	3567	245	710
14215	\N	3567	254	710
14216	\N	3567	255	710
14217	\N	3567	246	710
14219	\N	3567	249	710
14221	\N	3567	251	710
14222	\N	3567	248	710
14224	\N	3567	910	710
14226	\N	3567	257	710
14229	\N	3567	260	710
14230	\N	3567	911	710
14233	\N	3567	259	710
13659	\N	3567	901	687
13661	\N	3567	231	687
13663	\N	3567	237	687
13664	\N	3567	240	687
13666	\N	3567	235	687
13669	\N	3567	243	687
13670	\N	3567	238	687
13672	\N	3567	903	687
16524	\N	3568	307	880
16526	\N	3568	303	880
16527	\N	3568	305	880
16528	\N	3568	308	880
16532	\N	3568	1079	880
16533	\N	3568	309	880
16534	\N	3568	316	880
16535	\N	3568	301	880
16536	\N	3568	1080	880
16537	\N	3568	310	880
16538	\N	3568	306	880
16539	\N	3568	320	880
16540	\N	3568	302	880
16541	\N	3568	1081	880
16542	\N	3568	318	880
16543	\N	3568	1082	880
16544	\N	3568	1086	880
16545	\N	3568	1083	880
16546	\N	3568	1084	880
16547	\N	3568	1077	880
16548	\N	3568	311	880
16549	\N	3568	319	880
16550	\N	3568	312	880
16551	\N	3568	317	880
16552	\N	3568	313	880
16553	\N	3568	314	880
16555	\N	3568	304	880
14212	\N	3568	250	710
14214	\N	3568	908	710
14218	\N	3568	252	710
14225	\N	3568	907	710
14227	\N	3568	256	710
14228	\N	3568	258	710
14231	\N	3568	244	710
14232	\N	3568	912	710
14234	\N	3568	261	710
14235	\N	3568	906	710
13656	\N	3568	232	687
13658	\N	3568	229	687
13662	\N	3568	241	687
13665	\N	3568	230	687
13667	\N	3568	234	687
16387	\N	4499	1077	875
13671	\N	3568	902	687
13673	\N	3568	242	687
13674	\N	3568	236	687
13675	\N	3568	904	687
17278	\N	3688	1108	901
17289	\N	3688	346	901
17277	\N	3685	1103	901
17281	\N	3685	347	901
17282	\N	3685	343	901
17285	\N	3685	344	901
17283	\N	3686	1105	901
17284	\N	3686	345	901
17287	\N	3686	1104	901
17276	\N	3687	1106	901
17279	\N	3687	1107	901
17280	\N	3687	342	901
17288	\N	3687	348	901
17290	\N	3691	1106	902
17291	\N	3691	1103	902
17293	\N	3691	1107	902
17295	\N	3691	347	902
17296	\N	3691	343	902
17298	\N	3691	345	902
17299	\N	3691	344	902
17300	\N	3691	341	902
17302	\N	3691	348	902
17303	\N	3689	346	902
17301	\N	3692	1104	902
17292	\N	3690	1108	902
17297	\N	3690	1105	902
17336	\N	3757	342	905
17332	\N	3759	1106	905
17333	\N	3759	1103	905
17342	\N	3759	341	905
17344	\N	3759	348	905
17335	\N	3758	1107	905
17339	\N	3758	1105	905
17345	\N	3758	346	905
17334	\N	3760	1108	905
17337	\N	3760	347	905
17338	\N	3760	343	905
17340	\N	3760	345	905
17343	\N	3760	1104	905
17349	\N	3750	1107	906
17350	\N	3750	342	906
17352	\N	3750	343	906
17351	\N	3752	347	906
17357	\N	3752	1104	906
17358	\N	3752	348	906
17356	\N	3751	341	906
17346	\N	3749	1106	906
17347	\N	3749	1103	906
17348	\N	3749	1108	906
17353	\N	3749	1105	906
17355	\N	3749	344	906
17171	\N	4498	1097	898
17172	\N	4498	1087	898
17176	\N	4498	1098	898
17177	\N	4498	333	898
17178	\N	4498	327	898
17179	\N	4498	321	898
17180	\N	4498	328	898
17181	\N	4498	329	898
17182	\N	4498	325	898
17183	\N	4498	326	898
17185	\N	4498	1089	898
17186	\N	4498	1099	898
17187	\N	4498	1090	898
17188	\N	4498	1091	898
17189	\N	4498	335	898
17190	\N	4498	336	898
17191	\N	4498	337	898
17192	\N	4498	1092	898
17193	\N	4498	1100	898
17195	\N	4498	322	898
17197	\N	4498	1101	898
17198	\N	4498	339	898
17199	\N	4498	340	898
17200	\N	4498	1093	898
17201	\N	4498	323	898
17202	\N	4498	1102	898
17203	\N	4498	1094	898
16364	\N	4498	307	875
16366	\N	4498	303	875
16368	\N	4498	308	875
16372	\N	4498	1079	875
16373	\N	4498	309	875
16374	\N	4498	316	875
16375	\N	4498	301	875
16376	\N	4498	1080	875
16377	\N	4498	310	875
16378	\N	4498	306	875
16379	\N	4498	320	875
16380	\N	4498	302	875
16381	\N	4498	1081	875
16382	\N	4498	318	875
16383	\N	4498	1082	875
16385	\N	4498	1083	875
16386	\N	4498	1084	875
16388	\N	4498	311	875
16389	\N	4498	319	875
16390	\N	4498	312	875
16393	\N	4498	314	875
16394	\N	4498	315	875
16395	\N	4498	304	875
16370	\N	4500	1085	875
17174	\N	4497	1088	898
17184	\N	4497	334	898
17194	\N	4497	338	898
16369	\N	4497	1078	875
16384	\N	4497	1086	875
16391	\N	4497	317	875
16392	\N	4497	313	875
17376	\N	3714	1108	908
17382	\N	3714	345	908
17374	\N	3713	1106	908
17375	\N	3715	1103	908
17377	\N	3715	1107	908
17378	\N	3715	342	908
17379	\N	3715	347	908
17380	\N	3715	343	908
17381	\N	3715	1105	908
17384	\N	3715	341	908
17385	\N	3715	1104	908
17386	\N	3715	348	908
17388	\N	3706	1106	909
17389	\N	3706	1103	909
17390	\N	3706	1108	909
17391	\N	3706	1107	909
17393	\N	3706	347	909
17394	\N	3706	343	909
17395	\N	3706	1105	909
17396	\N	3706	345	909
17398	\N	3706	341	909
17399	\N	3706	1104	909
17400	\N	3706	348	909
17401	\N	3706	346	909
17397	\N	3705	344	909
17404	\N	3712	1108	910
17405	\N	3712	1107	910
17413	\N	3712	1104	910
17402	\N	3710	1106	910
17403	\N	3710	1103	910
17406	\N	3710	342	910
17408	\N	3710	343	910
17409	\N	3710	1105	910
17410	\N	3710	345	910
17411	\N	3710	344	910
17412	\N	3710	341	910
17414	\N	3710	348	910
17415	\N	3710	346	910
17421	\N	3720	347	911
17429	\N	3717	346	911
17417	\N	3719	1103	911
17419	\N	3719	1107	911
17420	\N	3719	342	911
17423	\N	3719	1105	911
17424	\N	3719	345	911
17416	\N	3718	1106	911
17418	\N	3718	1108	911
17427	\N	3718	1104	911
17428	\N	3718	348	911
17431	\N	3723	1103	912
17435	\N	3723	347	912
17436	\N	3723	343	912
17437	\N	3723	1105	912
17440	\N	3723	341	912
17443	\N	3723	346	912
17430	\N	3722	1106	912
17432	\N	3722	1108	912
17433	\N	3722	1107	912
17434	\N	3722	342	912
17439	\N	3722	344	912
17441	\N	3722	1104	912
17442	\N	3722	348	912
17438	\N	3721	345	912
17459	\N	3732	1103	914
17469	\N	3732	1104	914
17458	\N	3729	1106	914
17460	\N	3729	1108	914
17468	\N	3729	341	914
17461	\N	3731	1107	914
17462	\N	3731	342	914
17463	\N	3731	347	914
17464	\N	3731	343	914
17465	\N	3731	1105	914
17466	\N	3731	345	914
17467	\N	3731	344	914
17470	\N	3731	348	914
17471	\N	3731	346	914
11393	\N	3731	179	500
11394	\N	3731	176	500
11395	\N	3731	171	500
11397	\N	3731	175	500
11398	\N	3731	169	500
11399	\N	3731	697	500
11400	\N	3731	170	500
17472	\N	3735	1106	915
17476	\N	3735	342	915
17480	\N	3735	345	915
17483	\N	3735	1104	915
17479	\N	3733	1105	915
17473	\N	3734	1103	915
17474	\N	3734	1108	915
17477	\N	3734	347	915
17482	\N	3734	341	915
17475	\N	3736	1107	915
17485	\N	3736	346	915
17488	\N	3743	1108	916
17489	\N	3743	1107	916
17495	\N	3743	344	916
17496	\N	3743	341	916
17499	\N	3743	346	916
17487	\N	3744	1103	916
17491	\N	3744	347	916
17492	\N	3744	343	916
17493	\N	3744	1105	916
17494	\N	3744	345	916
17498	\N	3744	348	916
17486	\N	3741	1106	916
17490	\N	3741	342	916
17497	\N	3741	1104	916
17500	\N	3747	1106	917
17501	\N	3747	1103	917
17503	\N	3747	1107	917
17505	\N	3747	347	917
17506	\N	3747	343	917
17509	\N	3747	344	917
17510	\N	3747	341	917
17511	\N	3747	1104	917
17512	\N	3747	348	917
17507	\N	3745	1105	917
17502	\N	3746	1108	917
17508	\N	3746	345	917
17504	\N	3748	342	917
17528	\N	3684	1106	919
17529	\N	3684	1103	919
17530	\N	3684	1108	919
17531	\N	3684	1107	919
17532	\N	3684	342	919
17533	\N	3684	347	919
17534	\N	3684	343	919
17535	\N	3684	1105	919
17538	\N	3684	341	919
17540	\N	3684	348	919
17536	\N	3683	345	919
17539	\N	3681	1104	919
17543	\N	3696	1103	920
17544	\N	3696	1108	920
17547	\N	3696	347	920
17549	\N	3696	1105	920
17551	\N	3696	344	920
17552	\N	3696	341	920
17553	\N	3696	1104	920
17554	\N	3696	348	920
14701	\N	3696	913	747
17542	\N	3695	1106	920
17545	\N	3695	1107	920
17546	\N	3695	342	920
17550	\N	3695	345	920
17555	\N	3695	346	920
14951	\N	3695	915	776
14957	\N	3695	918	776
14694	\N	3695	262	747
14695	\N	3695	263	747
14698	\N	3695	265	747
14953	\N	3694	270	776
14700	\N	3693	266	747
17620	\N	4752	350	925
17304	\N	4752	1106	903
17307	\N	4752	1107	903
17308	\N	4752	342	903
17312	\N	4752	345	903
17612	\N	4750	352	925
17613	\N	4750	1111	925
17615	\N	4750	1114	925
17306	\N	4750	1108	903
17315	\N	4750	1104	903
17610	\N	4751	1112	925
17616	\N	4751	1110	925
17617	\N	4751	349	925
17748	\N	3919	355	935
17515	\N	3919	1103	918
17739	\N	3920	1109	935
17740	\N	3920	1112	935
17741	\N	3920	354	935
17743	\N	3920	1111	935
17744	\N	3920	1113	935
17745	\N	3920	1114	935
17746	\N	3920	1110	935
17749	\N	3920	353	935
17514	\N	3920	1106	918
17516	\N	3920	1108	918
17618	\N	4751	355	925
17305	\N	4751	1103	903
17309	\N	4751	347	903
17313	\N	4751	344	903
17314	\N	4751	341	903
17316	\N	4751	348	903
17609	\N	4749	1109	925
17611	\N	4749	354	925
17622	\N	4957	1109	926
17624	\N	4957	354	926
17625	\N	4957	352	926
17628	\N	4957	1114	926
17629	\N	4957	1110	926
17630	\N	4957	349	926
17632	\N	4957	353	926
17319	\N	4957	1103	904
17320	\N	4957	1108	904
17322	\N	4957	342	904
17323	\N	4957	347	904
17324	\N	4957	343	904
17325	\N	4957	1105	904
17326	\N	4957	345	904
17327	\N	4957	344	904
17329	\N	4957	1104	904
17330	\N	4957	348	904
17331	\N	4957	346	904
17621	\N	4960	351	926
17623	\N	4960	1112	926
17626	\N	4960	1111	926
17627	\N	4960	1113	926
17631	\N	4960	355	926
17633	\N	4960	350	926
17318	\N	4960	1106	904
17321	\N	4960	1107	904
17328	\N	4960	341	904
17816	\N	3797	1117	941
17818	\N	3797	1115	941
17819	\N	3797	1119	941
17820	\N	3797	1121	941
17821	\N	3797	1122	941
17823	\N	3797	1116	941
17825	\N	3797	1118	941
17826	\N	3797	1120	941
17827	\N	3797	1123	941
17828	\N	3797	1124	941
17830	\N	3797	1125	941
17824	\N	3798	1126	941
17817	\N	3800	1128	941
17822	\N	3800	1127	941
17829	\N	3800	1129	941
17831	\N	3783	1117	942
17832	\N	3783	1128	942
17833	\N	3783	1115	942
17834	\N	3783	1119	942
17835	\N	3783	1121	942
17836	\N	3783	1122	942
17837	\N	3783	1127	942
17838	\N	3783	1116	942
17840	\N	3783	1118	942
17841	\N	3783	1120	942
17842	\N	3783	1123	942
17843	\N	3783	1124	942
17844	\N	3783	1129	942
17845	\N	3783	1125	942
17839	\N	3781	1126	942
17855	\N	3792	1118	943
17846	\N	3791	1117	943
17850	\N	3791	1121	943
17856	\N	3791	1120	943
17858	\N	3791	1124	943
17859	\N	3791	1129	943
17860	\N	3791	1125	943
17847	\N	3790	1128	943
17848	\N	3790	1115	943
17849	\N	3790	1119	943
17851	\N	3790	1122	943
17852	\N	3790	1127	943
17853	\N	3790	1116	943
17854	\N	3790	1126	943
17857	\N	3790	1123	943
17862	\N	3795	1128	944
17863	\N	3795	1115	944
17867	\N	3795	1127	944
17868	\N	3795	1116	944
17871	\N	3795	1120	944
17872	\N	3795	1123	944
17875	\N	3795	1125	944
17869	\N	3793	1126	944
17861	\N	3796	1117	944
17864	\N	3794	1119	944
17865	\N	3794	1121	944
17866	\N	3794	1122	944
17870	\N	3794	1118	944
17873	\N	3794	1124	944
17874	\N	3794	1129	944
17876	\N	3788	1117	945
17877	\N	3788	1128	945
17878	\N	3788	1115	945
17879	\N	3788	1119	945
17880	\N	3788	1121	945
17881	\N	3788	1122	945
17882	\N	3788	1127	945
17883	\N	3788	1116	945
17884	\N	3788	1126	945
17885	\N	3788	1118	945
17886	\N	3788	1120	945
17887	\N	3788	1123	945
17888	\N	3788	1124	945
17889	\N	3788	1129	945
17890	\N	3788	1125	945
17966	\N	3828	1144	951
17967	\N	3828	1142	951
17968	\N	3828	1145	951
17969	\N	3828	1146	951
17970	\N	3828	1149	951
17971	\N	3828	1152	951
17972	\N	3828	1153	951
17973	\N	3828	1154	951
17974	\N	3828	1143	951
17975	\N	3828	359	951
17976	\N	3828	1147	951
17977	\N	3828	1148	951
17978	\N	3828	1150	951
17979	\N	3828	360	951
17980	\N	3828	1151	951
17993	\N	3830	1150	952
17986	\N	3831	1152	952
17994	\N	3831	360	952
17981	\N	3829	1144	952
17982	\N	3829	1142	952
17985	\N	3829	1149	952
17987	\N	3829	1153	952
17988	\N	3829	1154	952
17989	\N	3829	1143	952
17992	\N	3829	1148	952
17995	\N	3829	1151	952
17983	\N	3832	1145	952
17984	\N	3832	1146	952
17991	\N	3832	1147	952
17996	\N	3836	1144	953
17997	\N	3836	1142	953
17998	\N	3836	1145	953
17999	\N	3836	1146	953
18000	\N	3836	1149	953
18001	\N	3836	1152	953
18002	\N	3836	1153	953
18003	\N	3836	1154	953
18004	\N	3836	1143	953
18005	\N	3836	359	953
18006	\N	3836	1147	953
18007	\N	3836	1148	953
18008	\N	3836	1150	953
18009	\N	3836	360	953
18010	\N	3836	1151	953
18013	\N	3823	1145	954
18015	\N	3823	1149	954
18025	\N	3823	1151	954
18133	\N	3878	1170	963
18134	\N	3878	1172	963
18129	\N	3877	1173	963
18130	\N	3877	1168	963
18131	\N	3877	1167	963
18132	\N	3877	1169	963
18127	\N	3880	1166	963
18135	\N	3863	1166	964
18138	\N	3863	1168	964
18140	\N	3863	1169	964
18141	\N	3863	1170	964
18136	\N	3862	1171	964
18137	\N	3862	1173	964
18139	\N	3862	1167	964
18142	\N	3861	1172	964
18145	\N	3874	1173	965
18143	\N	3876	1166	965
18150	\N	3876	1172	965
18144	\N	3875	1171	965
18146	\N	3875	1168	965
18147	\N	3875	1167	965
18148	\N	3875	1169	965
18149	\N	3873	1170	965
18206	\N	3915	1188	972
18202	\N	3916	366	972
18203	\N	3916	1183	972
18204	\N	3916	1186	972
18205	\N	3916	1187	972
18207	\N	3916	367	972
18208	\N	3916	1182	972
18210	\N	3916	1184	972
18211	\N	3916	1185	972
18212	\N	3916	1189	972
18213	\N	3916	1190	972
18200	\N	3914	1181	972
18201	\N	3914	365	972
18209	\N	3914	1180	972
18216	\N	3918	366	973
18223	\N	3919	1180	973
18214	\N	3920	1181	973
18217	\N	3920	1183	973
18218	\N	3920	1186	973
18219	\N	3920	1187	973
18220	\N	3920	1188	973
18221	\N	3920	367	973
18222	\N	3920	1182	973
18224	\N	3920	1184	973
18225	\N	3920	1185	973
18226	\N	3920	1189	973
18227	\N	3920	1190	973
17517	\N	3920	1107	918
17518	\N	3920	342	918
17519	\N	3920	347	918
17520	\N	3920	343	918
17521	\N	3920	1105	918
17522	\N	3920	345	918
17523	\N	3920	344	918
17524	\N	3920	341	918
17525	\N	3920	1104	918
17526	\N	3920	348	918
18228	\N	3901	1181	974
18233	\N	3901	1187	974
18234	\N	3901	1188	974
18238	\N	3901	1184	974
18239	\N	3901	1185	974
18230	\N	3904	366	974
18231	\N	3904	1183	974
18232	\N	3904	1186	974
18235	\N	3904	367	974
18237	\N	3904	1180	974
18240	\N	3904	1189	974
18241	\N	3904	1190	974
18236	\N	3903	1182	974
18242	\N	3907	1181	975
18245	\N	3907	1183	975
18254	\N	3907	1189	975
18255	\N	3907	1190	975
18246	\N	3906	1186	975
18252	\N	3906	1184	975
18243	\N	3905	365	975
18250	\N	3905	1182	975
18251	\N	3905	1180	975
18247	\N	3908	1187	975
18248	\N	3908	1188	975
18253	\N	3908	1185	975
18331	\N	3948	1212	981
18332	\N	3948	1204	981
18333	\N	3948	1206	981
18334	\N	3948	1207	981
18335	\N	3948	1210	981
18336	\N	3948	370	981
18339	\N	3948	1205	981
18340	\N	3948	1208	981
18341	\N	3948	1209	981
18342	\N	3948	1211	981
18288	\N	4781	1201	978
18293	\N	4781	1199	978
18294	\N	4781	1200	978
18188	\N	4781	366	971
18195	\N	4781	1180	971
18186	\N	4782	1181	971
18286	\N	4783	369	978
18287	\N	4783	1192	978
18289	\N	4783	1193	978
18290	\N	4783	1194	978
18291	\N	4783	1197	978
18292	\N	4783	1191	978
18295	\N	4783	368	978
2284	\N	3948	63	145
2285	\N	3948	81	145
2289	\N	3948	70	145
2290	\N	3948	65	145
2294	\N	3948	490	145
2295	\N	3948	66	145
2299	\N	3948	69	145
18337	\N	3946	371	981
18338	\N	3946	372	981
13982	\N	3946	912	700
13200	\N	3946	239	664
13214	\N	3946	236	664
13215	\N	3946	904	664
2259	\N	3946	52	145
2260	\N	3946	73	145
2263	\N	3946	46	145
2264	\N	3946	55	145
2266	\N	3946	57	145
2267	\N	3946	58	145
2268	\N	3946	59	145
2270	\N	3946	80	145
2272	\N	3946	491	145
2274	\N	3946	60	145
2280	\N	3946	47	145
2282	\N	3946	62	145
2283	\N	3946	72	145
2287	\N	3946	488	145
2288	\N	3946	489	145
2298	\N	3946	68	145
2281	\N	3947	78	145
13965	\N	3945	254	700
13972	\N	3945	248	700
2293	\N	3945	49	145
18343	\N	3954	1212	982
18344	\N	3954	1204	982
18346	\N	3954	1207	982
18347	\N	3954	1210	982
18349	\N	3954	371	982
18350	\N	3954	372	982
18351	\N	3954	1205	982
18352	\N	3954	1208	982
18353	\N	3954	1209	982
18345	\N	3953	1206	982
18354	\N	3955	1211	982
18391	\N	4703	1217	986
18394	\N	4703	1220	986
18396	\N	4703	1221	986
18398	\N	4703	375	986
18399	\N	4703	1218	986
18400	\N	4703	1219	986
18382	\N	4703	1207	985
18383	\N	4703	1210	985
18385	\N	4703	371	985
18386	\N	4703	372	985
18387	\N	4703	1205	985
18390	\N	4703	1211	985
18470	\N	3998	1228	992
18465	\N	3997	1226	992
18467	\N	3997	377	992
18468	\N	3997	1227	992
18471	\N	3997	1225	992
18473	\N	3997	378	992
18466	\N	4000	1224	992
18469	\N	4000	1223	992
18472	\N	4000	1222	992
18486	\N	3987	1227	994
18490	\N	3987	1222	994
18485	\N	3985	377	994
18489	\N	3985	1225	994
18483	\N	3988	1226	994
18484	\N	3988	1224	994
18487	\N	3988	1223	994
18488	\N	3988	1228	994
14867	\N	3988	918	766
14868	\N	3988	916	766
14869	\N	3988	917	766
18397	\N	4702	374	986
18381	\N	4702	1206	985
18389	\N	4702	1209	985
18392	\N	4701	1213	986
18395	\N	4701	376	986
18401	\N	4701	1214	986
18402	\N	4701	1215	986
18403	\N	4701	1216	986
18379	\N	4701	1212	985
18380	\N	4701	1204	985
18384	\N	4701	370	985
18444	\N	4760	1213	990
18455	\N	4760	1216	990
18378	\N	4760	1211	984
18445	\N	4758	373	990
18446	\N	4758	1220	990
18448	\N	4758	1221	990
18449	\N	4758	374	990
18450	\N	4758	375	990
18451	\N	4758	1218	990
18452	\N	4758	1219	990
18453	\N	4758	1214	990
18454	\N	4758	1215	990
18367	\N	4758	1212	984
18368	\N	4758	1204	984
18369	\N	4758	1206	984
18370	\N	4758	1207	984
18371	\N	4758	1210	984
18372	\N	4758	370	984
18375	\N	4758	1205	984
18376	\N	4758	1208	984
18377	\N	4758	1209	984
18443	\N	4757	1217	990
18423	\N	4788	374	988
14566	\N	3988	262	731
14568	\N	3988	264	731
14570	\N	3988	265	731
14571	\N	3988	914	731
14572	\N	3988	266	731
14573	\N	3988	913	731
14567	\N	3986	263	731
18492	\N	3989	1226	995
18494	\N	3989	377	995
18497	\N	3989	1228	995
18493	\N	3990	1224	995
18496	\N	3990	1223	995
18498	\N	3990	1225	995
18500	\N	3990	378	995
18495	\N	3992	1227	995
18499	\N	3992	1222	995
18541	\N	4025	1235	1001
18546	\N	4025	1236	1001
18551	\N	4025	1237	1001
18552	\N	4025	1238	1001
18553	\N	4025	1239	1001
18543	\N	4027	383	1001
18547	\N	4027	1243	1001
18549	\N	4027	1241	1001
18550	\N	4027	1242	1001
18545	\N	4026	1240	1001
18542	\N	4028	382	1001
18544	\N	4028	1244	1001
18554	\N	4028	1245	1001
18557	\N	4021	383	1002
18555	\N	4023	1235	1002
18556	\N	4023	382	1002
18563	\N	4023	1241	1002
18558	\N	4024	1244	1002
18559	\N	4024	1240	1002
18560	\N	4024	1236	1002
18561	\N	4024	1243	1002
18562	\N	4024	381	1002
18564	\N	4024	1242	1002
18565	\N	4024	1237	1002
18566	\N	4024	1238	1002
18567	\N	4024	1239	1002
18568	\N	4024	1245	1002
18577	\N	4038	1241	1003
18569	\N	4039	1235	1003
18570	\N	4039	382	1003
18571	\N	4039	383	1003
18572	\N	4039	1244	1003
18573	\N	4039	1240	1003
18574	\N	4039	1236	1003
18575	\N	4039	1243	1003
18576	\N	4039	381	1003
18578	\N	4039	1242	1003
18579	\N	4039	1237	1003
18580	\N	4039	1238	1003
18581	\N	4039	1239	1003
18582	\N	4039	1245	1003
18525	\N	4512	1232	999
18526	\N	4512	1234	999
18527	\N	4512	1233	999
18528	\N	4512	1230	999
18529	\N	4512	1229	999
18531	\N	4512	1231	999
18456	\N	4512	1226	991
18459	\N	4512	1227	991
18461	\N	4512	1228	991
18462	\N	4512	1225	991
18464	\N	4512	378	991
18457	\N	4511	1224	991
18460	\N	4511	1223	991
18463	\N	4511	1222	991
18530	\N	4510	379	999
18532	\N	4510	380	999
18458	\N	4510	377	991
18501	\N	4756	1232	996
18502	\N	4756	1234	996
18503	\N	4756	1233	996
18508	\N	4756	380	996
18480	\N	4756	1225	993
18481	\N	4756	1222	993
18482	\N	4756	378	993
18504	\N	4755	1230	996
18505	\N	4755	1229	996
18506	\N	4755	379	996
18507	\N	4755	1231	996
18477	\N	4755	1227	993
18479	\N	4755	1228	993
18474	\N	4753	1226	993
18475	\N	4753	1224	993
18478	\N	4753	1223	993
18600	\N	4036	1244	1005
13544	\N	4035	240	681
13555	\N	4035	904	681
13805	\N	4034	911	693
13806	\N	4034	244	693
13807	\N	4034	912	693
13809	\N	4034	261	693
13810	\N	4034	906	693
18767	\N	4542	1277	1019
18768	\N	4542	1267	1019
18769	\N	4542	1268	1019
18771	\N	4542	1271	1019
18772	\N	4542	1276	1019
18773	\N	4542	1273	1019
18774	\N	4542	1274	1019
18777	\N	4542	1270	1019
18778	\N	4542	1272	1019
18779	\N	4542	1275	1019
18780	\N	4542	1278	1019
18688	\N	4542	1256	1013
18689	\N	4542	1258	1013
18690	\N	4542	1259	1013
18686	\N	4063	1263	1012
18687	\N	4061	1265	1012
18699	\N	4078	1256	1014
18702	\N	4078	1262	1014
18707	\N	4078	1261	1014
18708	\N	4078	1263	1014
18700	\N	4080	1258	1014
18701	\N	4080	1259	1014
18703	\N	4080	1257	1014
18704	\N	4080	385	1014
18705	\N	4080	1264	1014
18706	\N	4080	1260	1014
18709	\N	4080	1265	1014
18715	\N	4069	385	1015
18717	\N	4069	1260	1015
18716	\N	4070	1264	1015
18710	\N	4071	1256	1015
18711	\N	4071	1258	1015
18712	\N	4071	1259	1015
18713	\N	4071	1262	1015
18714	\N	4071	1257	1015
18718	\N	4071	1261	1015
18719	\N	4071	1263	1015
18720	\N	4071	1265	1015
18586	\N	4489	1244	1004
18583	\N	4492	1235	1004
18593	\N	4492	1237	1004
6141	\N	4492	551	221
6143	\N	4492	598	221
6144	\N	4492	576	221
6153	\N	4492	594	221
6154	\N	4492	579	221
6155	\N	4492	600	221
6159	\N	4492	596	221
15791	\N	4492	1062	851
15793	\N	4492	1063	851
15796	\N	4492	1064	851
18587	\N	4491	1240	1004
18588	\N	4491	1236	1004
18589	\N	4491	1243	1004
18590	\N	4491	381	1004
18591	\N	4491	1241	1004
18592	\N	4491	1242	1004
18594	\N	4491	1238	1004
18595	\N	4491	1239	1004
18596	\N	4491	1245	1004
18691	\N	4542	1262	1013
18692	\N	4542	1257	1013
18694	\N	4542	1264	1013
18695	\N	4542	1260	1013
18770	\N	4541	1269	1019
18776	\N	4541	1266	1019
18798	\N	4107	1284	1021
18800	\N	4107	1286	1021
18802	\N	4107	1285	1021
18796	\N	4105	1282	1021
18797	\N	4105	1279	1021
18799	\N	4105	1280	1021
18801	\N	4105	1283	1021
18803	\N	4105	1281	1021
18806	\N	4104	1284	1022
18807	\N	4104	1280	1022
18810	\N	4104	1285	1022
18811	\N	4104	1281	1022
18804	\N	4102	1282	1022
18805	\N	4102	1279	1022
18808	\N	4102	1286	1022
18809	\N	4102	1283	1022
18814	\N	4110	1284	1023
18819	\N	4110	1281	1023
18812	\N	4109	1282	1023
18813	\N	4109	1279	1023
18815	\N	4109	1280	1023
18816	\N	4109	1286	1023
18817	\N	4109	1283	1023
18818	\N	4109	1285	1023
18821	\N	4118	1279	1024
18823	\N	4118	1280	1024
18826	\N	4118	1285	1024
18827	\N	4118	1281	1024
18820	\N	4117	1282	1024
18822	\N	4117	1284	1024
18824	\N	4117	1286	1024
18825	\N	4117	1283	1024
18833	\N	4115	1283	1025
18828	\N	4116	1282	1025
18829	\N	4116	1279	1025
18830	\N	4116	1284	1025
18831	\N	4116	1280	1025
18832	\N	4116	1286	1025
18834	\N	4116	1285	1025
18835	\N	4116	1281	1025
18896	\N	4156	1298	1032
18902	\N	4156	1296	1032
18905	\N	4156	1302	1032
18894	\N	4155	1297	1032
18895	\N	4155	1303	1032
18897	\N	4155	389	1032
18898	\N	4155	1299	1032
18899	\N	4155	1304	1032
18900	\N	4155	1301	1032
18901	\N	4155	1295	1032
18903	\N	4155	390	1032
18904	\N	4155	1300	1032
18906	\N	4155	391	1032
18907	\N	4143	1297	1033
18913	\N	4142	1301	1033
18915	\N	4142	1296	1033
18908	\N	4141	1303	1033
18909	\N	4141	1298	1033
18910	\N	4141	389	1033
18911	\N	4141	1299	1033
18912	\N	4141	1304	1033
18914	\N	4141	1295	1033
18916	\N	4141	390	1033
18917	\N	4141	1300	1033
18918	\N	4141	1302	1033
18919	\N	4141	391	1033
18920	\N	4157	1297	1034
18921	\N	4157	1303	1034
18922	\N	4157	1298	1034
18923	\N	4157	389	1034
18924	\N	4157	1299	1034
18925	\N	4157	1304	1034
18926	\N	4157	1301	1034
18928	\N	4157	1296	1034
18929	\N	4157	390	1034
18930	\N	4157	1300	1034
18931	\N	4157	1302	1034
18932	\N	4157	391	1034
18927	\N	4158	1295	1034
18937	\N	4147	1299	1035
18941	\N	4147	1296	1035
18943	\N	4147	1300	1035
18934	\N	4148	1303	1035
18935	\N	4145	1298	1035
18939	\N	4145	1301	1035
18933	\N	4146	1297	1035
18938	\N	4146	1304	1035
18940	\N	4146	1295	1035
18944	\N	4146	1302	1035
19016	\N	4195	1317	1041
19018	\N	4195	401	1041
19019	\N	4195	1320	1041
19021	\N	4195	400	1041
19024	\N	4195	1318	1041
19026	\N	4195	1319	1041
19020	\N	4193	402	1041
19022	\N	4193	1313	1041
19023	\N	4193	1314	1041
19027	\N	4193	403	1041
19025	\N	4194	1315	1041
19029	\N	4194	1316	1041
19031	\N	4198	398	1042
19033	\N	4198	1320	1042
19041	\N	4198	403	1042
19030	\N	4200	1317	1042
19032	\N	4200	401	1042
19034	\N	4200	402	1042
19035	\N	4200	400	1042
19036	\N	4200	1313	1042
19037	\N	4200	1314	1042
19038	\N	4200	1318	1042
19039	\N	4200	1315	1042
19040	\N	4200	1319	1042
19043	\N	4200	1316	1042
19045	\N	4184	398	1043
19046	\N	4184	401	1043
19047	\N	4184	1320	1043
19048	\N	4184	402	1043
19051	\N	4184	1314	1043
19052	\N	4184	1318	1043
19054	\N	4184	1319	1043
19055	\N	4183	403	1043
19057	\N	4183	1316	1043
19044	\N	4182	1317	1043
19050	\N	4182	1313	1043
19053	\N	4182	1315	1043
19056	\N	4182	399	1043
19059	\N	4185	398	1044
19067	\N	4185	1315	1044
15666	\N	4541	1027	839
15667	\N	4541	1029	839
15670	\N	4541	1024	839
15674	\N	4541	1028	839
15675	\N	4541	1030	839
15677	\N	4541	1033	839
15678	\N	4541	1034	839
15680	\N	4541	1023	839
15606	\N	4541	1016	835
15608	\N	4541	1015	835
15610	\N	4541	1009	835
15617	\N	4541	1013	835
18974	\N	4864	1311	1038
18982	\N	4864	1307	1038
18985	\N	4864	396	1038
18882	\N	4864	1303	1031
18886	\N	4864	1304	1031
18893	\N	4864	391	1031
18975	\N	4862	397	1038
18977	\N	4862	1306	1038
18978	\N	4862	1308	1038
18979	\N	4862	1309	1038
18980	\N	4862	394	1038
18981	\N	4862	1305	1038
18983	\N	4862	392	1038
18984	\N	4862	393	1038
18986	\N	4862	1312	1038
19058	\N	4187	1317	1044
19060	\N	4187	401	1044
19062	\N	4187	402	1044
19063	\N	4187	400	1044
19064	\N	4187	1313	1044
19065	\N	4187	1314	1044
19066	\N	4187	1318	1044
19068	\N	4187	1319	1044
19069	\N	4187	403	1044
19070	\N	4187	399	1044
19071	\N	4187	1316	1044
19061	\N	4186	1320	1044
19079	\N	4189	1314	1045
19077	\N	4190	400	1045
19072	\N	4191	1317	1045
19073	\N	4191	398	1045
19075	\N	4191	1320	1045
19078	\N	4191	1313	1045
19080	\N	4191	1318	1045
19081	\N	4191	1315	1045
19082	\N	4191	1319	1045
19084	\N	4191	399	1045
19085	\N	4191	1316	1045
19136	\N	4227	1329	1051
19137	\N	4227	1334	1051
19138	\N	4227	1332	1051
19139	\N	4227	406	1051
19140	\N	4227	1330	1051
19141	\N	4227	1331	1051
19142	\N	4227	1333	1051
19143	\N	4227	1335	1051
19146	\N	4229	1332	1052
19147	\N	4229	406	1052
19144	\N	4231	1329	1052
19145	\N	4231	1334	1052
19149	\N	4231	1331	1052
19151	\N	4231	1335	1052
19148	\N	4232	1330	1052
19150	\N	4230	1333	1052
19159	\N	4233	1335	1053
19152	\N	4235	1329	1053
19153	\N	4235	1334	1053
19154	\N	4235	1332	1053
19156	\N	4235	1330	1053
19155	\N	4236	406	1053
19157	\N	4236	1331	1053
19158	\N	4236	1333	1053
19161	\N	4224	1334	1054
19160	\N	4222	1329	1054
19162	\N	4222	1332	1054
19164	\N	4222	1330	1054
19165	\N	4222	1331	1054
19166	\N	4222	1333	1054
19163	\N	4221	406	1054
19167	\N	4223	1335	1054
19168	\N	4240	1329	1055
19172	\N	4240	1330	1055
19173	\N	4240	1331	1055
19174	\N	4240	1333	1055
19175	\N	4240	1335	1055
19169	\N	4239	1334	1055
19170	\N	4239	1332	1055
19211	\N	4391	409	1061
19212	\N	4391	422	1061
19214	\N	4391	414	1061
19215	\N	4391	413	1061
19216	\N	4391	410	1061
19217	\N	4391	1343	1061
19218	\N	4391	411	1061
19219	\N	4391	408	1061
19221	\N	4391	1344	1061
19222	\N	4391	1345	1061
19224	\N	4391	420	1061
19225	\N	4391	1346	1061
19226	\N	4391	1347	1061
19227	\N	4391	416	1061
19228	\N	4391	1348	1061
19230	\N	4391	415	1061
19232	\N	4391	417	1061
19234	\N	4391	1349	1061
19236	\N	4391	1350	1061
19237	\N	4391	1351	1061
19238	\N	4391	1352	1061
19213	\N	4392	1353	1061
19220	\N	4392	1354	1061
19223	\N	4392	412	1061
19229	\N	4392	421	1061
19231	\N	4392	419	1061
19233	\N	4392	418	1061
19235	\N	4392	1355	1061
19239	\N	4389	1342	1061
19240	\N	4394	409	1062
19241	\N	4394	422	1062
19243	\N	4394	414	1062
19244	\N	4394	413	1062
19245	\N	4394	410	1062
19246	\N	4394	1343	1062
19247	\N	4394	411	1062
19248	\N	4394	408	1062
19250	\N	4394	1344	1062
19251	\N	4394	1345	1062
19252	\N	4394	412	1062
19253	\N	4394	420	1062
19254	\N	4394	1346	1062
19255	\N	4394	1347	1062
19256	\N	4394	416	1062
19257	\N	4394	1348	1062
19258	\N	4394	421	1062
19259	\N	4394	415	1062
19260	\N	4394	419	1062
19261	\N	4394	417	1062
19262	\N	4394	418	1062
19263	\N	4394	1349	1062
19264	\N	4394	1355	1062
19265	\N	4394	1350	1062
19266	\N	4394	1351	1062
19267	\N	4394	1352	1062
19268	\N	4394	1342	1062
19242	\N	4395	1353	1062
19249	\N	4396	1354	1062
19271	\N	4397	1353	1063
19272	\N	4397	414	1063
15256	\N	4397	958	803
19270	\N	4399	422	1063
19276	\N	4399	411	1063
19278	\N	4399	1354	1063
19279	\N	4399	1344	1063
19280	\N	4399	1345	1063
19281	\N	4399	412	1063
19282	\N	4399	420	1063
19283	\N	4399	1346	1063
19284	\N	4399	1347	1063
19286	\N	4399	1348	1063
19287	\N	4399	421	1063
19289	\N	4399	419	1063
19290	\N	4399	417	1063
19291	\N	4399	418	1063
19292	\N	4399	1349	1063
19296	\N	4399	1352	1063
15276	\N	4399	967	806
15277	\N	4399	968	806
15254	\N	4399	959	803
19269	\N	4400	409	1063
19273	\N	4400	413	1063
19274	\N	4400	410	1063
19275	\N	4400	1343	1063
19285	\N	4400	416	1063
19288	\N	4400	415	1063
19293	\N	4400	1355	1063
19294	\N	4400	1350	1063
19295	\N	4400	1351	1063
19297	\N	4400	1342	1063
15278	\N	4400	969	806
15252	\N	4400	963	803
15255	\N	4400	960	803
19304	\N	4324	1343	1064
19298	\N	4321	409	1064
19299	\N	4321	422	1064
19300	\N	4321	1353	1064
19301	\N	4321	414	1064
19302	\N	4321	413	1064
19303	\N	4321	410	1064
19305	\N	4321	411	1064
19306	\N	4321	408	1064
19307	\N	4321	1354	1064
19308	\N	4321	1344	1064
19309	\N	4321	1345	1064
19310	\N	4321	412	1064
19311	\N	4321	420	1064
19312	\N	4321	1346	1064
19313	\N	4321	1347	1064
19314	\N	4321	416	1064
19315	\N	4321	1348	1064
19316	\N	4321	421	1064
19317	\N	4321	415	1064
19318	\N	4321	419	1064
19319	\N	4321	417	1064
19321	\N	4321	1349	1064
19322	\N	4321	1355	1064
19323	\N	4321	1350	1064
19324	\N	4321	1351	1064
19325	\N	4321	1352	1064
19326	\N	4321	1342	1064
13375	\N	4321	904	672
13369	\N	4323	243	672
19320	\N	4322	418	1064
14386	\N	4322	245	717
14389	\N	4322	908	717
14398	\N	4322	909	717
13359	\N	4322	901	672
13371	\N	4322	902	672
19349	\N	4325	418	1065
14415	\N	4325	254	718
13379	\N	4325	901	673
13384	\N	4325	240	673
19329	\N	4327	1353	1065
19330	\N	4327	414	1065
19333	\N	4327	1343	1065
19335	\N	4327	408	1065
19336	\N	4327	1354	1065
19337	\N	4327	1344	1065
19338	\N	4327	1345	1065
19340	\N	4327	420	1065
19341	\N	4327	1346	1065
19342	\N	4327	1347	1065
19343	\N	4327	416	1065
19345	\N	4327	421	1065
19346	\N	4327	415	1065
19347	\N	4327	419	1065
19348	\N	4327	417	1065
19350	\N	4327	1349	1065
19351	\N	4327	1355	1065
19352	\N	4327	1350	1065
19353	\N	4327	1351	1065
19354	\N	4327	1352	1065
19355	\N	4327	1342	1065
14411	\N	4327	245	718
14412	\N	4327	250	718
14416	\N	4327	255	718
14417	\N	4327	246	718
14419	\N	4327	249	718
14420	\N	4327	247	718
14423	\N	4327	909	718
14424	\N	4327	910	718
14425	\N	4327	907	718
14426	\N	4327	257	718
14428	\N	4327	258	718
14429	\N	4327	260	718
14430	\N	4327	911	718
14431	\N	4327	244	718
14432	\N	4327	912	718
14433	\N	4327	259	718
14434	\N	4327	261	718
14435	\N	4327	906	718
13376	\N	4327	232	673
13377	\N	4327	233	673
13378	\N	4327	229	673
13381	\N	4327	231	673
13382	\N	4327	241	673
13383	\N	4327	237	673
13385	\N	4327	230	673
13386	\N	4327	235	673
13387	\N	4327	234	673
13390	\N	4327	238	673
13391	\N	4327	902	673
13392	\N	4327	903	673
13393	\N	4327	242	673
13394	\N	4327	236	673
13395	\N	4327	904	673
10395	\N	4327	168	457
10396	\N	4327	161	457
9623	\N	4327	140	434
9624	\N	4327	662	434
9625	\N	4327	149	434
9626	\N	4327	138	434
9629	\N	4327	671	434
9630	\N	4327	148	434
9632	\N	4327	664	434
9633	\N	4327	672	434
9634	\N	4327	139	434
9635	\N	4327	150	434
9636	\N	4327	143	434
9637	\N	4327	673	434
9639	\N	4327	674	434
9641	\N	4327	675	434
9642	\N	4327	666	434
9649	\N	4327	668	434
9651	\N	4327	677	434
9652	\N	4327	669	434
9653	\N	4327	670	434
9656	\N	4327	678	434
19328	\N	4326	422	1065
19344	\N	4326	1348	1065
14413	\N	4326	253	718
14422	\N	4326	248	718
13380	\N	4326	239	673
10369	\N	4326	163	457
10374	\N	4326	156	457
9631	\N	4326	680	434
9646	\N	4326	667	434
9654	\N	4326	147	434
19331	\N	4328	413	1065
14414	\N	4328	908	718
14421	\N	4328	251	718
14427	\N	4328	256	718
13388	\N	4328	905	673
13389	\N	4328	243	673
9627	\N	4328	679	434
9647	\N	4328	145	434
9648	\N	4328	141	434
19427	\N	4340	420	1068
19433	\N	4340	415	1068
19436	\N	4340	418	1068
19442	\N	4340	1342	1068
19432	\N	4339	421	1068
19434	\N	4339	419	1068
19437	\N	4339	1349	1068
19426	\N	4338	412	1068
19428	\N	4338	1346	1068
19429	\N	4338	1347	1068
19430	\N	4338	416	1068
19431	\N	4338	1348	1068
19435	\N	4338	417	1068
19438	\N	4338	1355	1068
19439	\N	4338	1350	1068
19440	\N	4338	1351	1068
19441	\N	4338	1352	1068
16617	\N	4352	1100	882
10645	\N	4352	688	466
9817	\N	4352	145	439
9818	\N	4352	141	439
9825	\N	4352	681	439
9049	\N	4352	656	296
9055	\N	4352	660	296
7890	\N	4352	618	259
7893	\N	4352	606	259
16109	\N	4352	299	867
16130	\N	4352	1084	867
16131	\N	4352	1077	867
16134	\N	4352	312	867
19447	\N	4350	413	1069
10625	\N	4350	684	466
10644	\N	4350	154	466
9823	\N	4350	670	439
9047	\N	4350	652	296
9054	\N	4350	638	296
16133	\N	4350	319	867
19443	\N	4351	409	1069
19444	\N	4351	422	1069
19445	\N	4351	1353	1069
19446	\N	4351	414	1069
19448	\N	4351	410	1069
19449	\N	4351	1343	1069
19450	\N	4351	411	1069
19451	\N	4351	408	1069
19452	\N	4351	1354	1069
19453	\N	4351	1344	1069
19454	\N	4351	1345	1069
19455	\N	4351	412	1069
19456	\N	4351	420	1069
19457	\N	4351	1346	1069
19458	\N	4351	1347	1069
19459	\N	4351	416	1069
19460	\N	4351	1348	1069
19461	\N	4351	421	1069
19462	\N	4351	415	1069
19463	\N	4351	419	1069
19464	\N	4351	417	1069
19465	\N	4351	418	1069
19466	\N	4351	1349	1069
19467	\N	4351	1355	1069
19468	\N	4351	1350	1069
19469	\N	4351	1351	1069
19470	\N	4351	1352	1069
19471	\N	4351	1342	1069
16592	\N	4351	1095	882
16593	\N	4351	331	882
16594	\N	4351	1096	882
16595	\N	4351	1097	882
16596	\N	4351	1087	882
16597	\N	4351	332	882
16598	\N	4351	1088	882
16599	\N	4351	324	882
16600	\N	4351	1098	882
16601	\N	4351	333	882
16602	\N	4351	327	882
16603	\N	4351	321	882
16605	\N	4351	329	882
16606	\N	4351	325	882
16607	\N	4351	326	882
16608	\N	4351	334	882
16609	\N	4351	1089	882
16610	\N	4351	1099	882
16611	\N	4351	1090	882
16612	\N	4351	1091	882
16613	\N	4351	335	882
16614	\N	4351	336	882
16615	\N	4351	337	882
16616	\N	4351	1092	882
16618	\N	4351	338	882
16619	\N	4351	322	882
16620	\N	4351	330	882
16621	\N	4351	1101	882
16622	\N	4351	339	882
16623	\N	4351	340	882
16624	\N	4351	1093	882
16625	\N	4351	323	882
16626	\N	4351	1102	882
16627	\N	4351	1094	882
10621	\N	4351	163	466
16108	\N	4351	307	867
16110	\N	4351	303	867
16111	\N	4351	305	867
16112	\N	4351	308	867
16113	\N	4351	1078	867
16115	\N	4351	300	867
16116	\N	4351	1079	867
16117	\N	4351	309	867
16118	\N	4351	316	867
16119	\N	4351	301	867
16120	\N	4351	1080	867
16121	\N	4351	310	867
16122	\N	4351	306	867
16123	\N	4351	320	867
16124	\N	4351	302	867
16125	\N	4351	1081	867
16126	\N	4351	318	867
16127	\N	4351	1082	867
16128	\N	4351	1086	867
16129	\N	4351	1083	867
16132	\N	4351	311	867
16135	\N	4351	317	867
16136	\N	4351	313	867
16137	\N	4351	314	867
16138	\N	4351	315	867
16139	\N	4351	304	867
10622	\N	4351	682	466
10623	\N	4351	160	466
10624	\N	4351	683	466
10626	\N	4351	156	466
10627	\N	4351	165	466
10628	\N	4351	152	466
10629	\N	4351	685	466
10630	\N	4351	158	466
10631	\N	4351	686	466
10633	\N	4351	159	466
10634	\N	4351	690	466
10636	\N	4351	167	466
10637	\N	4351	162	466
10638	\N	4351	691	466
10639	\N	4351	687	466
10640	\N	4351	166	466
10641	\N	4351	153	466
10642	\N	4351	164	466
10643	\N	4351	157	466
10646	\N	4351	689	466
10647	\N	4351	168	466
10648	\N	4351	161	466
9793	\N	4351	140	439
9794	\N	4351	662	439
9795	\N	4351	149	439
9796	\N	4351	138	439
9797	\N	4351	679	439
9798	\N	4351	663	439
9799	\N	4351	671	439
9800	\N	4351	148	439
9802	\N	4351	664	439
9803	\N	4351	672	439
9804	\N	4351	139	439
9805	\N	4351	150	439
9806	\N	4351	143	439
9807	\N	4351	673	439
9808	\N	4351	137	439
9809	\N	4351	674	439
9810	\N	4351	665	439
9811	\N	4351	675	439
9812	\N	4351	666	439
9813	\N	4351	142	439
9814	\N	4351	676	439
9815	\N	4351	146	439
9816	\N	4351	667	439
9819	\N	4351	668	439
9820	\N	4351	144	439
9821	\N	4351	677	439
9822	\N	4351	669	439
9824	\N	4351	147	439
9826	\N	4351	678	439
9026	\N	4351	645	296
9027	\N	4351	646	296
9028	\N	4351	639	296
9030	\N	4351	648	296
9031	\N	4351	651	296
9032	\N	4351	654	296
9033	\N	4351	657	296
9035	\N	4351	636	296
9038	\N	4351	653	296
9039	\N	4351	637	296
9040	\N	4351	642	296
9041	\N	4351	644	296
9042	\N	4351	659	296
9043	\N	4351	632	296
9045	\N	4351	634	296
9046	\N	4351	650	296
9048	\N	4351	655	296
9050	\N	4351	658	296
9051	\N	4351	635	296
9052	\N	4351	136	296
9056	\N	4351	661	296
7879	\N	4351	609	259
7880	\N	4351	610	259
7881	\N	4351	614	259
7882	\N	4351	620	259
7883	\N	4351	621	259
7885	\N	4351	135	259
7886	\N	4351	602	259
7887	\N	4351	612	259
7888	\N	4351	613	259
7889	\N	4351	615	259
7891	\N	4351	619	259
7892	\N	4351	603	259
7894	\N	4351	624	259
7896	\N	4351	628	259
7897	\N	4351	631	259
7898	\N	4351	611	259
7899	\N	4351	616	259
7900	\N	4351	617	259
7901	\N	4351	622	259
7902	\N	4351	623	259
7904	\N	4351	605	259
7905	\N	4351	625	259
7908	\N	4351	629	259
7909	\N	4351	630	259
16604	\N	4349	328	882
16114	\N	4349	1085	867
10632	\N	4349	151	466
19472	\N	4353	409	1070
19474	\N	4353	1353	1070
19477	\N	4353	410	1070
19480	\N	4353	408	1070
19481	\N	4353	1354	1070
19483	\N	4353	1345	1070
19485	\N	4353	420	1070
19486	\N	4353	1346	1070
19487	\N	4353	1347	1070
19489	\N	4353	1348	1070
19490	\N	4353	421	1070
19492	\N	4353	419	1070
19493	\N	4353	417	1070
19495	\N	4353	1349	1070
19496	\N	4353	1355	1070
19497	\N	4353	1350	1070
19498	\N	4353	1351	1070
19499	\N	4353	1352	1070
19500	\N	4353	1342	1070
11412	\N	4353	176	501
11413	\N	4353	171	501
11414	\N	4353	696	501
11417	\N	4353	697	501
11418	\N	4353	170	501
7918	\N	4353	612	260
7920	\N	4353	615	260
7922	\N	4353	619	260
7926	\N	4353	627	260
7927	\N	4353	628	260
7928	\N	4353	631	260
7939	\N	4353	629	260
7940	\N	4353	630	260
19475	\N	4356	414	1070
19476	\N	4356	413	1070
19479	\N	4356	411	1070
19488	\N	4356	416	1070
11594	\N	4356	698	512
11595	\N	4356	181	512
11598	\N	4356	700	512
11604	\N	4356	703	512
11403	\N	4356	692	501
11408	\N	4356	180	501
11411	\N	4356	179	501
9060	\N	4356	640	297
9061	\N	4356	648	297
9064	\N	4356	657	297
9069	\N	4356	653	297
9079	\N	4356	655	297
9083	\N	4356	136	297
9084	\N	4356	643	297
9085	\N	4356	638	297
7914	\N	4356	621	260
7919	\N	4356	613	260
7929	\N	4356	611	260
7931	\N	4356	617	260
7936	\N	4356	625	260
19473	\N	4355	422	1070
19482	\N	4355	1344	1070
19491	\N	4355	415	1070
19494	\N	4355	418	1070
11596	\N	4355	184	512
11600	\N	4355	701	512
11406	\N	4355	693	501
9059	\N	4355	639	297
9067	\N	4355	647	297
9070	\N	4355	637	297
7921	\N	4355	618	260
7925	\N	4355	624	260
7930	\N	4355	616	260
7932	\N	4355	622	260
7934	\N	4355	604	260
7938	\N	4355	608	260
19478	\N	4354	1343	1070
19484	\N	4354	412	1070
11602	\N	4354	185	512
11415	\N	4354	175	501
11416	\N	4354	169	501
9057	\N	4354	645	297
9058	\N	4354	646	297
9066	\N	4354	636	297
9068	\N	4354	633	297
9071	\N	4354	642	297
9072	\N	4354	644	297
9073	\N	4354	659	297
9074	\N	4354	632	297
9076	\N	4354	634	297
7915	\N	4354	607	260
7916	\N	4354	135	260
7917	\N	4354	602	260
7923	\N	4354	603	260
7924	\N	4354	606	260
7933	\N	4354	623	260
7935	\N	4354	605	260
7937	\N	4354	626	260
19513	\N	4370	412	1071
19518	\N	4370	1348	1071
19502	\N	4371	422	1071
19505	\N	4371	413	1071
19509	\N	4371	408	1071
19511	\N	4371	1344	1071
19512	\N	4371	1345	1071
19514	\N	4371	420	1071
19515	\N	4371	1346	1071
19524	\N	4371	1349	1071
19525	\N	4371	1355	1071
19527	\N	4371	1351	1071
19529	\N	4371	1342	1071
11652	\N	4371	703	516
11491	\N	4371	173	506
11493	\N	4371	692	506
11494	\N	4371	174	506
11495	\N	4371	695	506
11496	\N	4371	693	506
11498	\N	4371	180	506
11502	\N	4371	176	506
19503	\N	4372	1353	1071
19506	\N	4372	410	1071
19510	\N	4372	1354	1071
19516	\N	4372	1347	1071
19517	\N	4372	416	1071
19519	\N	4372	421	1071
19523	\N	4372	418	1071
19528	\N	4372	1352	1071
11642	\N	4372	698	516
11643	\N	4372	181	516
11644	\N	4372	184	516
11646	\N	4372	700	516
11648	\N	4372	701	516
11649	\N	4372	702	516
11492	\N	4372	694	506
11497	\N	4372	172	506
11499	\N	4372	177	506
11500	\N	4372	178	506
11503	\N	4372	171	506
11504	\N	4372	696	506
11506	\N	4372	169	506
11508	\N	4372	170	506
19501	\N	4369	409	1071
19507	\N	4369	1343	1071
19508	\N	4369	411	1071
19521	\N	4369	419	1071
19522	\N	4369	417	1071
19526	\N	4369	1350	1071
11641	\N	4369	183	516
11645	\N	4369	699	516
11647	\N	4369	182	516
11650	\N	4369	185	516
11651	\N	4369	186	516
11501	\N	4369	179	506
11507	\N	4369	697	506
19536	\N	4341	1343	1072
19537	\N	4341	411	1072
19538	\N	4341	408	1072
19541	\N	4341	1345	1072
19542	\N	4341	412	1072
19543	\N	4341	420	1072
19545	\N	4341	1347	1072
19547	\N	4341	1348	1072
19548	\N	4341	421	1072
19553	\N	4341	1349	1072
19554	\N	4341	1355	1072
19556	\N	4341	1351	1072
19531	\N	4343	422	1072
19532	\N	4343	1353	1072
19555	\N	4343	1350	1072
19534	\N	4342	413	1072
19544	\N	4342	1346	1072
19552	\N	4342	418	1072
19530	\N	4344	409	1072
19539	\N	4344	1354	1072
19540	\N	4344	1344	1072
19549	\N	4344	415	1072
19550	\N	4344	419	1072
19557	\N	4344	1352	1072
19558	\N	4344	1342	1072
19559	\N	4347	409	1073
19564	\N	4347	410	1073
19566	\N	4347	411	1073
19567	\N	4347	408	1073
19569	\N	4347	1344	1073
19570	\N	4347	1345	1073
19571	\N	4347	412	1073
19572	\N	4347	420	1073
19573	\N	4347	1346	1073
19577	\N	4347	421	1073
19579	\N	4347	419	1073
19580	\N	4347	417	1073
19581	\N	4347	418	1073
19584	\N	4347	1350	1073
19586	\N	4347	1352	1073
19565	\N	4346	1343	1073
19562	\N	4345	414	1073
19587	\N	4345	1342	1073
19560	\N	4348	422	1073
19561	\N	4348	1353	1073
19563	\N	4348	413	1073
19568	\N	4348	1354	1073
19574	\N	4348	1347	1073
19575	\N	4348	416	1073
19576	\N	4348	1348	1073
19578	\N	4348	415	1073
19582	\N	4348	1349	1073
19583	\N	4348	1355	1073
19585	\N	4348	1351	1073
19588	\N	4361	409	1074
19594	\N	4361	1343	1074
19605	\N	4361	1348	1074
19607	\N	4361	415	1074
19593	\N	4362	410	1074
19595	\N	4362	411	1074
19589	\N	4364	422	1074
19590	\N	4364	1353	1074
19591	\N	4364	414	1074
19592	\N	4364	413	1074
19596	\N	4364	408	1074
19597	\N	4364	1354	1074
19598	\N	4364	1344	1074
19599	\N	4364	1345	1074
19600	\N	4364	412	1074
19601	\N	4364	420	1074
19602	\N	4364	1346	1074
19603	\N	4364	1347	1074
19604	\N	4364	416	1074
19606	\N	4364	421	1074
19608	\N	4364	419	1074
19609	\N	4364	417	1074
19611	\N	4364	1349	1074
19612	\N	4364	1355	1074
19613	\N	4364	1350	1074
19614	\N	4364	1351	1074
19615	\N	4364	1352	1074
19616	\N	4364	1342	1074
14613	\N	4364	913	736
19618	\N	4367	422	1075
19620	\N	4367	414	1075
19621	\N	4367	413	1075
19623	\N	4367	1343	1075
19624	\N	4367	411	1075
19625	\N	4367	408	1075
19626	\N	4367	1354	1075
19627	\N	4367	1344	1075
19628	\N	4367	1345	1075
19629	\N	4367	412	1075
19630	\N	4367	420	1075
19631	\N	4367	1346	1075
19632	\N	4367	1347	1075
19634	\N	4367	1348	1075
19635	\N	4367	421	1075
19637	\N	4367	419	1075
19638	\N	4367	417	1075
19640	\N	4367	1349	1075
19641	\N	4367	1355	1075
19642	\N	4367	1350	1075
19643	\N	4367	1351	1075
19645	\N	4367	1342	1075
19617	\N	4365	409	1075
19619	\N	4365	1353	1075
19633	\N	4365	416	1075
19636	\N	4365	415	1075
19644	\N	4365	1352	1075
19673	\N	4374	1352	1076
19655	\N	4375	1354	1076
19656	\N	4375	1344	1076
19657	\N	4375	1345	1076
19667	\N	4376	417	1076
19646	\N	4373	409	1076
19647	\N	4373	422	1076
19648	\N	4373	1353	1076
19649	\N	4373	414	1076
19650	\N	4373	413	1076
19651	\N	4373	410	1076
19652	\N	4373	1343	1076
19653	\N	4373	411	1076
19654	\N	4373	408	1076
19658	\N	4373	412	1076
19659	\N	4373	420	1076
19660	\N	4373	1346	1076
19661	\N	4373	1347	1076
19662	\N	4373	416	1076
19663	\N	4373	1348	1076
19664	\N	4373	421	1076
19665	\N	4373	415	1076
19668	\N	4373	418	1076
19669	\N	4373	1349	1076
19670	\N	4373	1355	1076
19671	\N	4373	1350	1076
19672	\N	4373	1351	1076
19674	\N	4373	1342	1076
2951	\N	4373	87	160
2956	\N	4373	97	160
2959	\N	4373	121	160
2961	\N	4373	123	160
2962	\N	4373	124	160
2963	\N	4373	98	160
2964	\N	4373	125	160
2965	\N	4373	93	160
2966	\N	4373	126	160
2967	\N	4373	127	160
2968	\N	4373	128	160
2969	\N	4373	94	160
2970	\N	4373	129	160
19675	\N	4380	409	1077
19678	\N	4380	414	1077
19698	\N	4380	1349	1077
19690	\N	4379	1347	1077
19694	\N	4379	415	1077
19676	\N	4377	422	1077
19679	\N	4377	413	1077
19680	\N	4377	410	1077
19681	\N	4377	1343	1077
19683	\N	4377	408	1077
19684	\N	4377	1354	1077
19685	\N	4377	1344	1077
19686	\N	4377	1345	1077
19687	\N	4377	412	1077
19688	\N	4377	420	1077
19689	\N	4377	1346	1077
19691	\N	4377	416	1077
19692	\N	4377	1348	1077
19693	\N	4377	421	1077
19695	\N	4377	419	1077
19697	\N	4377	418	1077
19701	\N	4377	1351	1077
19702	\N	4377	1352	1077
19703	\N	4377	1342	1077
19677	\N	4378	1353	1077
19682	\N	4378	411	1077
19696	\N	4378	417	1077
19699	\N	4378	1355	1077
19700	\N	4378	1350	1077
19707	\N	4384	414	1078
19708	\N	4384	413	1078
19719	\N	4384	1347	1078
19723	\N	4384	415	1078
19731	\N	4384	1352	1078
19704	\N	4382	409	1078
19705	\N	4382	422	1078
19709	\N	4382	410	1078
19711	\N	4382	411	1078
19714	\N	4382	1344	1078
19715	\N	4382	1345	1078
19718	\N	4382	1346	1078
19721	\N	4382	1348	1078
19725	\N	4382	417	1078
19727	\N	4382	1349	1078
19728	\N	4382	1355	1078
19730	\N	4382	1351	1078
19710	\N	4381	1343	1078
19729	\N	4381	1350	1078
19706	\N	4383	1353	1078
19713	\N	4383	1354	1078
19732	\N	4383	1342	1078
19739	\N	4388	1343	1079
19744	\N	4388	1345	1079
19750	\N	4388	1348	1079
19752	\N	4388	415	1079
19757	\N	4388	1355	1079
19758	\N	4388	1350	1079
19733	\N	4386	409	1079
19734	\N	4386	422	1079
19735	\N	4386	1353	1079
19737	\N	4386	413	1079
19738	\N	4386	410	1079
19742	\N	4386	1354	1079
19743	\N	4386	1344	1079
19745	\N	4386	412	1079
19746	\N	4386	420	1079
19747	\N	4386	1346	1079
19751	\N	4386	421	1079
19753	\N	4386	419	1079
19754	\N	4386	417	1079
19756	\N	4386	1349	1079
19759	\N	4386	1351	1079
19761	\N	4386	1342	1079
19736	\N	4387	414	1079
19741	\N	4387	408	1079
19748	\N	4387	1347	1079
19749	\N	4387	416	1079
19760	\N	4387	1352	1079
19769	\N	4357	411	1080
19762	\N	4360	409	1080
19764	\N	4360	1353	1080
19766	\N	4360	413	1080
19767	\N	4360	410	1080
19768	\N	4360	1343	1080
19771	\N	4360	1354	1080
19772	\N	4360	1344	1080
19773	\N	4360	1345	1080
19775	\N	4360	420	1080
19776	\N	4360	1346	1080
19777	\N	4360	1347	1080
19779	\N	4360	1348	1080
19781	\N	4360	415	1080
19786	\N	4360	1355	1080
19787	\N	4360	1350	1080
19788	\N	4360	1351	1080
19789	\N	4360	1352	1080
19790	\N	4360	1342	1080
19770	\N	4358	408	1080
19783	\N	4358	417	1080
19785	\N	4358	1349	1080
20351	\N	4512	437	1101
20352	\N	4512	435	1101
20355	\N	4512	436	1101
20356	\N	4512	1375	1101
20357	\N	4512	1373	1101
20358	\N	4512	1374	1101
20360	\N	4512	439	1101
20354	\N	4511	434	1101
20353	\N	4509	440	1101
20359	\N	4510	438	1101
20361	\N	4514	437	1102
20363	\N	4514	440	1102
20367	\N	4514	1373	1102
20370	\N	4514	439	1102
20366	\N	4513	1375	1102
20368	\N	4513	1374	1102
20369	\N	4513	438	1102
20371	\N	4520	437	1103
20374	\N	4518	434	1103
20372	\N	4519	435	1103
20373	\N	4519	440	1103
20375	\N	4519	436	1103
20376	\N	4519	1375	1103
20377	\N	4519	1373	1103
20379	\N	4519	438	1103
20380	\N	4519	439	1103
20378	\N	4517	1374	1103
20382	\N	4532	435	1104
20383	\N	4532	440	1104
20385	\N	4532	436	1104
20386	\N	4532	1375	1104
20390	\N	4532	439	1104
20384	\N	4530	434	1104
20387	\N	4530	1373	1104
20389	\N	4530	438	1104
20381	\N	4531	437	1104
20388	\N	4531	1374	1104
20391	\N	4481	437	1105
20392	\N	4481	435	1105
20393	\N	4481	440	1105
20395	\N	4481	436	1105
20396	\N	4481	1375	1105
20397	\N	4481	1373	1105
20400	\N	4481	439	1105
20394	\N	4483	434	1105
20398	\N	4483	1374	1105
13429	\N	4483	243	675
11798	\N	4483	698	529
11807	\N	4483	186	529
11315	\N	4483	695	496
11320	\N	4483	178	496
11323	\N	4483	171	496
20399	\N	4484	438	1105
14463	\N	4484	253	720
14467	\N	4484	246	720
14468	\N	4484	252	720
14484	\N	4484	261	720
13417	\N	4484	233	675
13419	\N	4484	901	675
13432	\N	4484	903	675
11804	\N	4484	701	529
11805	\N	4484	702	529
11806	\N	4484	185	529
11314	\N	4484	174	496
11316	\N	4484	693	496
11317	\N	4484	172	496
11318	\N	4484	180	496
11319	\N	4484	177	496
11327	\N	4484	697	496
20418	\N	4492	1374	1107
20411	\N	4491	437	1107
20412	\N	4491	435	1107
20413	\N	4491	440	1107
20415	\N	4491	436	1107
20416	\N	4491	1375	1107
20419	\N	4491	438	1107
20420	\N	4491	439	1107
15894	\N	4491	1067	859
15895	\N	4491	1066	859
15896	\N	4491	1071	859
15899	\N	4491	1069	859
15900	\N	4491	1070	859
15901	\N	4491	1073	859
15902	\N	4491	1074	859
4681	\N	4491	512	196
4682	\N	4491	513	196
4683	\N	4491	514	196
4684	\N	4491	515	196
4685	\N	4491	516	196
4686	\N	4491	517	196
4688	\N	4491	518	196
4689	\N	4491	505	196
4690	\N	4491	502	196
4692	\N	4491	519	196
4693	\N	4491	506	196
4696	\N	4491	520	196
4697	\N	4491	495	196
4699	\N	4491	496	196
4705	\N	4491	526	196
4708	\N	4491	529	196
4710	\N	4491	531	196
4712	\N	4491	533	196
4715	\N	4491	499	196
4717	\N	4491	536	196
4718	\N	4491	508	196
4723	\N	4491	538	196
4724	\N	4491	500	196
4728	\N	4491	501	196
4731	\N	4491	511	196
4734	\N	4491	544	196
6101	\N	4491	554	221
6103	\N	4491	555	221
6105	\N	4491	557	221
6106	\N	4491	545	221
6107	\N	4491	558	221
6108	\N	4491	559	221
6110	\N	4491	583	221
6111	\N	4491	560	221
6112	\N	4491	597	221
6114	\N	4491	562	221
6115	\N	4491	584	221
6116	\N	4491	563	221
6117	\N	4491	585	221
6118	\N	4491	564	221
6119	\N	4491	547	221
6122	\N	4491	587	221
6123	\N	4491	566	221
6125	\N	4491	549	221
6126	\N	4491	567	221
6127	\N	4491	568	221
6128	\N	4491	569	221
6129	\N	4491	570	221
6130	\N	4491	550	221
6131	\N	4491	588	221
6136	\N	4491	572	221
6139	\N	4491	134	221
6142	\N	4491	575	221
6146	\N	4491	591	221
6149	\N	4491	578	221
6160	\N	4491	580	221
15786	\N	4491	1056	851
15794	\N	4491	1057	851
15795	\N	4491	1058	851
15797	\N	4491	1059	851
15798	\N	4491	1060	851
15799	\N	4491	1061	851
6161	\N	4491	581	221
20414	\N	4490	434	1107
20417	\N	4490	1373	1107
15892	\N	4490	1065	859
15893	\N	4490	1075	859
15903	\N	4490	1076	859
15788	\N	4490	1055	851
15792	\N	4490	1054	851
4695	\N	4490	494	196
4701	\N	4490	523	196
4703	\N	4490	497	196
4704	\N	4490	525	196
4709	\N	4490	530	196
6104	\N	4490	556	221
6124	\N	4490	548	221
6133	\N	4490	589	221
6138	\N	4490	574	221
6147	\N	4490	577	221
6148	\N	4490	599	221
6150	\N	4490	552	221
20421	\N	4493	437	1108
20422	\N	4493	435	1108
20424	\N	4493	434	1108
20426	\N	4493	1375	1108
20427	\N	4493	1373	1108
20430	\N	4493	439	1108
20425	\N	4495	436	1108
20423	\N	4494	440	1108
20428	\N	4494	1374	1108
20429	\N	4494	438	1108
20431	\N	4498	437	1109
20433	\N	4498	440	1109
20435	\N	4498	436	1109
20436	\N	4498	1375	1109
20437	\N	4498	1373	1109
20438	\N	4498	1374	1109
20439	\N	4498	438	1109
20440	\N	4498	439	1109
17168	\N	4498	1095	898
17170	\N	4498	1096	898
20441	\N	4502	437	1110
20444	\N	4502	434	1110
20447	\N	4502	1373	1110
20448	\N	4502	1374	1110
20442	\N	4504	435	1110
20443	\N	4504	440	1110
20446	\N	4504	1375	1110
20449	\N	4504	438	1110
20450	\N	4504	439	1110
20453	\N	4505	440	1111
20455	\N	4505	436	1111
20456	\N	4505	1375	1111
20458	\N	4505	1374	1111
20459	\N	4505	438	1111
20457	\N	4506	1373	1111
20460	\N	4506	439	1111
20463	\N	4536	440	1112
20467	\N	4536	1373	1112
20470	\N	4536	439	1112
20462	\N	4534	435	1112
20468	\N	4534	1374	1112
20464	\N	4533	434	1112
20465	\N	4533	436	1112
20466	\N	4533	1375	1112
20474	\N	4540	434	1113
20475	\N	4540	436	1113
20471	\N	4537	437	1113
20472	\N	4537	435	1113
20473	\N	4537	440	1113
20476	\N	4537	1375	1113
20478	\N	4537	1374	1113
20479	\N	4537	438	1113
20480	\N	4537	439	1113
15618	\N	4541	1018	835
15619	\N	4541	1020	835
18696	\N	4541	1261	1013
18697	\N	4541	1263	1013
18698	\N	4541	1265	1013
2655	\N	4548	129	153
3124	\N	4552	86	164
3132	\N	4552	115	164
3140	\N	4552	122	164
3146	\N	4552	126	164
3107	\N	4551	90	164
3129	\N	4551	114	164
3133	\N	4551	116	164
3138	\N	4551	120	164
3141	\N	4551	123	164
3106	\N	4550	99	164
3108	\N	4550	100	164
3109	\N	4550	101	164
3110	\N	4550	91	164
3111	\N	4550	85	164
3112	\N	4550	102	164
3113	\N	4550	103	164
3114	\N	4550	104	164
3115	\N	4550	88	164
3116	\N	4550	105	164
3117	\N	4550	106	164
3118	\N	4550	107	164
3119	\N	4550	108	164
3120	\N	4550	109	164
3121	\N	4550	110	164
3122	\N	4550	111	164
3123	\N	4550	95	164
3125	\N	4550	112	164
3126	\N	4550	113	164
3127	\N	4550	89	164
3128	\N	4550	96	164
3130	\N	4550	92	164
3131	\N	4550	87	164
3134	\N	4550	117	164
3135	\N	4550	118	164
3136	\N	4550	97	164
3137	\N	4550	119	164
3139	\N	4550	121	164
3142	\N	4550	124	164
3143	\N	4550	98	164
3144	\N	4550	125	164
3145	\N	4550	93	164
3147	\N	4550	127	164
3148	\N	4550	128	164
3149	\N	4550	94	164
3150	\N	4550	129	164
20536	\N	4521	1375	1119
20537	\N	4521	1373	1119
20538	\N	4521	1374	1119
20539	\N	4521	438	1119
20540	\N	4522	439	1119
20543	\N	4526	440	1120
20546	\N	4526	1375	1120
20549	\N	4526	438	1120
20541	\N	4527	437	1120
20542	\N	4527	435	1120
20545	\N	4527	436	1120
20547	\N	4527	1373	1120
20548	\N	4527	1374	1120
13411	\N	4867	902	674
13412	\N	4867	903	674
13413	\N	4867	242	674
13415	\N	4867	904	674
20830	\N	4639	1399	1151
20835	\N	4639	1400	1151
20827	\N	4638	1394	1151
20828	\N	4638	1395	1151
20829	\N	4638	1396	1151
20831	\N	4638	1397	1151
20832	\N	4638	1398	1151
20806	\N	4638	1389	1147
20807	\N	4638	1391	1147
20808	\N	4638	1390	1147
20809	\N	4638	1392	1147
20810	\N	4638	1393	1147
20771	\N	4638	1384	1143
20773	\N	4638	1385	1143
20777	\N	4638	1386	1143
20779	\N	4638	1379	1143
20826	\N	4640	1402	1151
20833	\N	4640	1401	1151
20834	\N	4640	1403	1151
20772	\N	4640	1382	1143
20774	\N	4640	1380	1143
20775	\N	4640	1381	1143
20776	\N	4640	1383	1143
20778	\N	4640	1387	1143
20780	\N	4640	1388	1143
20836	\N	4622	1402	1152
20837	\N	4622	1394	1152
20838	\N	4622	1395	1152
20839	\N	4622	1396	1152
20840	\N	4622	1399	1152
20841	\N	4622	1397	1152
20842	\N	4622	1398	1152
20843	\N	4622	1401	1152
20844	\N	4622	1403	1152
20845	\N	4622	1400	1152
20811	\N	4622	1389	1148
20812	\N	4622	1391	1148
20813	\N	4622	1390	1148
20814	\N	4622	1392	1148
20815	\N	4622	1393	1148
11564	\N	4622	694	510
11565	\N	4622	692	510
11566	\N	4622	174	510
11568	\N	4622	693	510
11570	\N	4622	180	510
11572	\N	4622	178	510
11573	\N	4622	179	510
11574	\N	4622	176	510
11575	\N	4622	171	510
11576	\N	4622	696	510
11577	\N	4622	175	510
11578	\N	4622	169	510
11579	\N	4622	697	510
11580	\N	4622	170	510
1329	\N	4622	74	123
1330	\N	4622	68	123
1331	\N	4622	69	123
1332	\N	4622	77	123
15394	\N	4622	290	817
15395	\N	4622	289	817
15396	\N	4622	981	817
15397	\N	4622	992	817
15398	\N	4622	982	817
15399	\N	4622	986	817
15400	\N	4622	988	817
15401	\N	4622	991	817
15403	\N	4622	288	817
15404	\N	4622	983	817
15405	\N	4622	984	817
15406	\N	4622	985	817
15407	\N	4622	987	817
15408	\N	4622	993	817
15409	\N	4622	989	817
15410	\N	4622	990	817
15411	\N	4622	994	817
1289	\N	4622	50	123
1290	\N	4622	51	123
1291	\N	4622	52	123
1292	\N	4622	73	123
1293	\N	4622	53	123
1294	\N	4622	54	123
15356	\N	4622	979	814
15357	\N	4622	972	814
15358	\N	4622	973	814
15359	\N	4622	980	814
15360	\N	4622	975	814
1295	\N	4622	46	123
1297	\N	4622	56	123
1298	\N	4622	57	123
1299	\N	4622	58	123
1301	\N	4622	487	123
1302	\N	4622	80	123
1303	\N	4622	75	123
1304	\N	4622	491	123
1305	\N	4622	82	123
1306	\N	4622	60	123
1307	\N	4622	84	123
1308	\N	4622	83	123
1310	\N	4622	76	123
1311	\N	4622	79	123
1312	\N	4622	47	123
1313	\N	4622	78	123
1314	\N	4622	62	123
1315	\N	4622	72	123
1316	\N	4622	63	123
1317	\N	4622	81	123
1318	\N	4622	64	123
1319	\N	4622	488	123
20781	\N	4622	1384	1144
20782	\N	4622	1382	1144
20783	\N	4622	1385	1144
20784	\N	4622	1380	1144
20785	\N	4622	1381	1144
20786	\N	4622	1383	1144
20787	\N	4622	1386	1144
20788	\N	4622	1387	1144
20789	\N	4622	1379	1144
20790	\N	4622	1388	1144
1320	\N	4622	489	123
1321	\N	4622	70	123
1322	\N	4622	65	123
1323	\N	4622	48	123
1324	\N	4622	71	123
1325	\N	4622	49	123
1327	\N	4622	66	123
1328	\N	4622	67	123
11696	\N	4621	701	520
11700	\N	4621	703	520
11567	\N	4621	695	510
11569	\N	4621	172	510
11571	\N	4621	177	510
1296	\N	4621	55	123
1309	\N	4621	61	123
1326	\N	4621	490	123
20846	\N	4627	1402	1153
20847	\N	4627	1394	1153
20848	\N	4627	1395	1153
20849	\N	4627	1396	1153
20851	\N	4627	1397	1153
20852	\N	4627	1398	1153
20854	\N	4627	1403	1153
20855	\N	4627	1400	1153
20818	\N	4627	1390	1149
20819	\N	4627	1392	1149
20820	\N	4627	1393	1149
20791	\N	4627	1384	1145
20792	\N	4627	1382	1145
20793	\N	4627	1385	1145
20794	\N	4627	1380	1145
20795	\N	4627	1381	1145
20796	\N	4627	1383	1145
20797	\N	4627	1386	1145
20798	\N	4627	1387	1145
20799	\N	4627	1379	1145
20850	\N	4626	1399	1153
20853	\N	4626	1401	1153
20816	\N	4626	1389	1149
20817	\N	4626	1391	1149
20800	\N	4626	1388	1145
20856	\N	4630	1402	1154
20857	\N	4632	1394	1154
20858	\N	4632	1395	1154
20859	\N	4632	1396	1154
20860	\N	4632	1399	1154
20861	\N	4632	1397	1154
20862	\N	4632	1398	1154
20863	\N	4632	1401	1154
20864	\N	4632	1403	1154
20821	\N	4632	1389	1150
20822	\N	4632	1391	1150
20823	\N	4632	1390	1150
20824	\N	4632	1392	1150
20825	\N	4632	1393	1150
20751	\N	4632	1384	1141
20752	\N	4632	1382	1141
20753	\N	4632	1385	1141
20755	\N	4632	1381	1141
20756	\N	4632	1383	1141
20757	\N	4632	1386	1141
20758	\N	4632	1387	1141
20760	\N	4632	1388	1141
20865	\N	4629	1400	1154
20759	\N	4629	1379	1141
20754	\N	4631	1380	1141
20870	\N	4635	1399	1155
20764	\N	4635	1380	1142
20871	\N	4634	1397	1155
20874	\N	4634	1403	1155
20767	\N	4634	1386	1142
20867	\N	4633	1394	1155
20868	\N	4633	1395	1155
20869	\N	4633	1396	1155
20872	\N	4633	1398	1155
20873	\N	4633	1401	1155
20875	\N	4633	1400	1155
20801	\N	4633	1389	1146
20802	\N	4633	1391	1146
20803	\N	4633	1390	1146
20804	\N	4633	1392	1146
20805	\N	4633	1393	1146
20761	\N	4633	1384	1142
20762	\N	4633	1382	1142
20763	\N	4633	1385	1142
20765	\N	4633	1381	1142
20766	\N	4633	1383	1142
20768	\N	4633	1387	1142
20770	\N	4633	1388	1142
20866	\N	4636	1402	1155
20769	\N	4636	1379	1142
20951	\N	4719	1417	1163
20982	\N	4720	1423	1167
20983	\N	4720	1424	1167
20946	\N	4720	448	1163
20948	\N	4720	1416	1163
20953	\N	4720	1419	1163
20954	\N	4720	449	1163
20955	\N	4720	1420	1163
20984	\N	4717	1425	1167
20950	\N	4717	1421	1163
20985	\N	4718	1422	1167
20986	\N	4718	1426	1167
20987	\N	4718	1427	1167
20947	\N	4718	1414	1163
20949	\N	4718	1415	1163
20952	\N	4718	1418	1163
20957	\N	4703	1414	1164
20988	\N	4702	1423	1168
20992	\N	4702	1426	1168
20959	\N	4702	1415	1164
20961	\N	4702	1417	1164
20963	\N	4702	1419	1164
20989	\N	4701	1424	1168
20990	\N	4701	1425	1168
20991	\N	4701	1422	1168
20993	\N	4701	1427	1168
18388	\N	4701	1208	985
20958	\N	4701	1416	1164
20960	\N	4701	1421	1164
20962	\N	4701	1418	1164
20965	\N	4701	1420	1164
20972	\N	4705	1418	1165
20966	\N	4707	448	1165
20994	\N	4708	1423	1169
20995	\N	4708	1424	1169
20996	\N	4708	1425	1169
20997	\N	4708	1422	1169
20998	\N	4708	1426	1169
20999	\N	4708	1427	1169
20967	\N	4708	1414	1165
20968	\N	4708	1416	1165
20969	\N	4708	1415	1165
20970	\N	4708	1421	1165
20971	\N	4708	1417	1165
20973	\N	4708	1419	1165
20974	\N	4708	449	1165
20975	\N	4708	1420	1165
20926	\N	4709	448	1161
20927	\N	4709	1414	1161
20934	\N	4709	449	1161
21000	\N	4711	1423	1170
21001	\N	4711	1424	1170
21003	\N	4711	1422	1170
21004	\N	4711	1426	1170
21005	\N	4711	1427	1170
20928	\N	4711	1416	1161
20931	\N	4711	1417	1161
20932	\N	4711	1418	1161
20933	\N	4711	1419	1161
20935	\N	4711	1420	1161
21002	\N	4710	1425	1170
20929	\N	4710	1415	1161
20930	\N	4712	1421	1161
20979	\N	4714	1422	1166
20936	\N	4714	448	1162
20940	\N	4716	1421	1162
20941	\N	4716	1417	1162
20945	\N	4716	1420	1162
20976	\N	4715	1423	1166
20977	\N	4715	1424	1166
20978	\N	4715	1425	1166
20980	\N	4715	1426	1166
20981	\N	4715	1427	1166
20937	\N	4715	1414	1162
20939	\N	4715	1415	1162
20942	\N	4715	1418	1162
20943	\N	4715	1419	1162
20944	\N	4715	449	1162
20938	\N	4713	1416	1162
17614	\N	4749	1113	925
17619	\N	4749	353	925
17311	\N	4749	1105	903
17317	\N	4749	346	903
21091	\N	4756	1447	1182
21092	\N	4756	1442	1182
21094	\N	4756	1440	1182
21095	\N	4756	1441	1182
21096	\N	4756	1450	1182
21097	\N	4756	1446	1182
21098	\N	4756	1453	1182
21099	\N	4756	1452	1182
21100	\N	4756	1443	1182
21101	\N	4756	1444	1182
21102	\N	4756	1448	1182
21103	\N	4756	1449	1182
21104	\N	4756	1445	1182
21105	\N	4756	1451	1182
21093	\N	4753	1439	1182
21119	\N	4760	1445	1183
21106	\N	4758	1447	1183
21107	\N	4758	1442	1183
21108	\N	4758	1439	1183
21109	\N	4758	1440	1183
21110	\N	4758	1441	1183
21111	\N	4758	1450	1183
21112	\N	4758	1446	1183
21113	\N	4758	1453	1183
21114	\N	4758	1452	1183
21115	\N	4758	1443	1183
21116	\N	4758	1444	1183
21117	\N	4758	1448	1183
21118	\N	4758	1449	1183
21120	\N	4758	1451	1183
21132	\N	4743	1448	1184
21127	\N	4742	1446	1184
21121	\N	4741	1447	1184
21122	\N	4741	1442	1184
21123	\N	4741	1439	1184
21124	\N	4741	1440	1184
21125	\N	4741	1441	1184
21126	\N	4741	1450	1184
21128	\N	4741	1453	1184
21129	\N	4741	1452	1184
21130	\N	4741	1443	1184
21131	\N	4741	1444	1184
21133	\N	4741	1449	1184
21134	\N	4741	1445	1184
21135	\N	4741	1451	1184
21137	\N	4746	1442	1185
21138	\N	4746	1439	1185
21139	\N	4746	1440	1185
21141	\N	4746	1450	1185
21144	\N	4746	1452	1185
21145	\N	4746	1443	1185
21146	\N	4746	1444	1185
21147	\N	4746	1448	1185
21148	\N	4746	1449	1185
21149	\N	4746	1445	1185
21150	\N	4746	1451	1185
5900	\N	4746	576	217
5902	\N	4746	591	217
5903	\N	4746	577	217
5904	\N	4746	599	217
5906	\N	4746	552	217
5908	\N	4746	593	217
5909	\N	4746	594	217
5912	\N	4746	595	217
5913	\N	4746	601	217
5914	\N	4746	553	217
5915	\N	4746	596	217
5916	\N	4746	580	217
4303	\N	4746	512	189
4305	\N	4746	514	189
4306	\N	4746	515	189
4307	\N	4746	516	189
4309	\N	4746	492	189
4310	\N	4746	518	189
4311	\N	4746	505	189
4313	\N	4746	493	189
4314	\N	4746	519	189
4315	\N	4746	506	189
4317	\N	4746	494	189
4318	\N	4746	520	189
4319	\N	4746	495	189
4320	\N	4746	521	189
4321	\N	4746	496	189
4323	\N	4746	523	189
4325	\N	4746	497	189
4326	\N	4746	525	189
4327	\N	4746	526	189
4328	\N	4746	527	189
4329	\N	4746	528	189
4330	\N	4746	529	189
4333	\N	4746	532	189
4334	\N	4746	533	189
4337	\N	4746	499	189
4338	\N	4746	535	189
4339	\N	4746	536	189
4340	\N	4746	508	189
4341	\N	4746	509	189
4343	\N	4746	130	189
4345	\N	4746	538	189
4346	\N	4746	500	189
4347	\N	4746	539	189
4348	\N	4746	503	189
4349	\N	4746	540	189
4351	\N	4746	541	189
4352	\N	4746	542	189
4355	\N	4746	504	189
4356	\N	4746	544	189
21143	\N	4747	1453	1185
21140	\N	4745	1441	1185
21136	\N	4748	1447	1185
21142	\N	4748	1446	1185
5870	\N	4748	562	217
5876	\N	4748	586	217
5881	\N	4748	549	217
5888	\N	4748	132	217
5892	\N	4748	572	217
5896	\N	4748	590	217
5897	\N	4748	551	217
4304	\N	4748	513	189
4312	\N	4748	502	189
4322	\N	4748	522	189
4324	\N	4748	524	189
4331	\N	4748	530	189
4332	\N	4748	531	189
4335	\N	4748	534	189
4336	\N	4748	498	189
4353	\N	4748	511	189
4354	\N	4748	543	189
21237	\N	4789	1477	1191
21238	\N	4789	1471	1191
21242	\N	4789	1472	1191
21245	\N	4789	454	1191
21246	\N	4789	1473	1191
21247	\N	4789	1474	1191
21248	\N	4789	1475	1191
21249	\N	4789	1476	1191
21250	\N	4789	1478	1191
21240	\N	4791	1479	1191
21236	\N	4792	1480	1191
21239	\N	4792	1481	1191
21241	\N	4792	1482	1191
21244	\N	4792	1483	1191
21259	\N	4795	1483	1192
21260	\N	4795	454	1192
21261	\N	4795	1473	1192
21258	\N	4793	453	1192
21251	\N	4796	1480	1192
21252	\N	4796	1477	1192
21253	\N	4796	1471	1192
21254	\N	4796	1481	1192
21255	\N	4796	1479	1192
21256	\N	4796	1482	1192
21257	\N	4796	1472	1192
21262	\N	4796	1474	1192
21263	\N	4796	1475	1192
21264	\N	4796	1476	1192
21265	\N	4796	1478	1192
21276	\N	4781	1473	1193
21266	\N	4783	1480	1193
21267	\N	4783	1477	1193
21268	\N	4783	1471	1193
21269	\N	4783	1481	1193
21270	\N	4783	1479	1193
21271	\N	4783	1482	1193
21272	\N	4783	1472	1193
21273	\N	4783	453	1193
21274	\N	4783	1483	1193
21275	\N	4783	454	1193
21277	\N	4783	1474	1193
21278	\N	4783	1475	1193
21279	\N	4783	1476	1193
21280	\N	4783	1478	1193
18296	\N	4783	1202	978
18297	\N	4783	1198	978
18298	\N	4783	1195	978
18299	\N	4783	1196	978
18300	\N	4783	1203	978
18187	\N	4783	365	971
18189	\N	4783	1183	971
18190	\N	4783	1186	971
18191	\N	4783	1187	971
18192	\N	4783	1188	971
18193	\N	4783	367	971
18194	\N	4783	1182	971
18196	\N	4783	1184	971
18197	\N	4783	1185	971
18198	\N	4783	1189	971
18199	\N	4783	1190	971
21282	\N	4788	1477	1194
18426	\N	4788	1219	988
18429	\N	4788	1216	988
18355	\N	4788	1212	983
18360	\N	4788	370	983
18363	\N	4788	1205	983
18364	\N	4788	1208	983
18366	\N	4788	1211	983
18422	\N	4787	1221	988
18362	\N	4787	372	983
21281	\N	4785	1480	1194
21283	\N	4785	1471	1194
21284	\N	4785	1481	1194
21285	\N	4785	1479	1194
21286	\N	4785	1482	1194
21287	\N	4785	1472	1194
21288	\N	4785	453	1194
21289	\N	4785	1483	1194
21290	\N	4785	454	1194
21291	\N	4785	1473	1194
21292	\N	4785	1474	1194
21293	\N	4785	1475	1194
21294	\N	4785	1476	1194
21295	\N	4785	1478	1194
18418	\N	4785	1213	988
18419	\N	4785	373	988
18420	\N	4785	1220	988
18421	\N	4785	376	988
18425	\N	4785	1218	988
18427	\N	4785	1214	988
18428	\N	4785	1215	988
18356	\N	4785	1204	983
18357	\N	4785	1206	983
18358	\N	4785	1207	983
18359	\N	4785	1210	983
18361	\N	4785	371	983
18365	\N	4785	1209	983
18417	\N	4786	1217	988
21298	\N	4800	1471	1195
21301	\N	4800	1482	1195
21300	\N	4799	1479	1195
21303	\N	4799	453	1195
21304	\N	4799	1483	1195
21307	\N	4799	1474	1195
21309	\N	4799	1476	1195
21310	\N	4799	1478	1195
21296	\N	4797	1480	1195
21297	\N	4798	1477	1195
21299	\N	4798	1481	1195
21302	\N	4798	1472	1195
21306	\N	4798	1473	1195
21308	\N	4798	1475	1195
8458	\N	4798	652	277
8459	\N	4798	655	277
7572	\N	4798	620	249
7574	\N	4798	607	249
7577	\N	4798	612	249
7578	\N	4798	613	249
7581	\N	4798	619	249
7582	\N	4798	603	249
7584	\N	4798	624	249
7589	\N	4798	616	249
7590	\N	4798	617	249
7596	\N	4798	626	249
21395	\N	4834	1495	1201
21381	\N	4835	1497	1201
21382	\N	4835	1498	1201
21383	\N	4835	1499	1201
21384	\N	4835	1502	1201
21385	\N	4835	1507	1201
21386	\N	4835	1496	1201
21387	\N	4835	1500	1201
21388	\N	4835	1501	1201
21389	\N	4835	1503	1201
21390	\N	4835	1504	1201
21391	\N	4835	1505	1201
21392	\N	4835	1506	1201
21393	\N	4835	1508	1201
21394	\N	4835	1509	1201
15629	\N	4835	1028	836
15630	\N	4835	1030	836
15631	\N	4835	1031	836
15632	\N	4835	1033	836
15633	\N	4835	1034	836
15634	\N	4835	1035	836
15575	\N	4835	292	833
15577	\N	4835	1021	833
15578	\N	4835	1009	833
15579	\N	4835	1012	833
15580	\N	4835	293	833
15581	\N	4835	1019	833
15582	\N	4835	1014	833
15585	\N	4835	1013	833
15586	\N	4835	1018	833
15587	\N	4835	1020	833
12773	\N	4835	846	621
12777	\N	4835	848	621
12779	\N	4835	845	621
12780	\N	4835	847	621
21397	\N	4837	1498	1202
21400	\N	4837	1507	1202
21401	\N	4837	1496	1202
21403	\N	4837	1501	1202
21406	\N	4837	1505	1202
21407	\N	4837	1506	1202
21408	\N	4837	1508	1202
2356	\N	4837	59	147
2360	\N	4837	491	147
2369	\N	4837	78	147
2372	\N	4837	63	147
2373	\N	4837	81	147
2374	\N	4837	64	147
2377	\N	4837	70	147
2378	\N	4837	65	147
2381	\N	4837	49	147
2384	\N	4837	67	147
2385	\N	4837	74	147
21399	\N	4838	1502	1202
2347	\N	4838	52	147
2353	\N	4838	56	147
2362	\N	4838	60	147
2363	\N	4838	84	147
2364	\N	4838	83	147
21398	\N	4840	1499	1202
21402	\N	4840	1500	1202
21404	\N	4840	1503	1202
21405	\N	4840	1504	1202
21409	\N	4840	1509	1202
21410	\N	4840	1495	1202
2345	\N	4840	50	147
2357	\N	4840	487	147
2358	\N	4840	80	147
2368	\N	4840	47	147
2375	\N	4840	488	147
2380	\N	4840	71	147
2387	\N	4840	69	147
21396	\N	4839	1497	1202
2346	\N	4839	51	147
2355	\N	4839	58	147
2367	\N	4839	79	147
2371	\N	4839	72	147
2376	\N	4839	489	147
2382	\N	4839	490	147
2386	\N	4839	68	147
21412	\N	4823	1498	1203
21413	\N	4823	1499	1203
21414	\N	4823	1502	1203
21415	\N	4823	1507	1203
21416	\N	4823	1496	1203
21418	\N	4823	1501	1203
21419	\N	4823	1503	1203
21421	\N	4823	1505	1203
21422	\N	4823	1506	1203
21423	\N	4823	1508	1203
21424	\N	4823	1509	1203
21411	\N	4821	1497	1203
21417	\N	4821	1500	1203
21420	\N	4821	1504	1203
21425	\N	4821	1495	1203
17363	\N	4827	1107	907
17371	\N	4827	1104	907
21426	\N	4826	1497	1204
21427	\N	4826	1498	1204
21428	\N	4826	1499	1204
21429	\N	4826	1502	1204
21430	\N	4826	1507	1204
21431	\N	4826	1496	1204
21433	\N	4826	1501	1204
21434	\N	4826	1503	1204
21435	\N	4826	1504	1204
21436	\N	4826	1505	1204
21437	\N	4826	1506	1204
21438	\N	4826	1508	1204
21439	\N	4826	1509	1204
21440	\N	4826	1495	1204
15775	\N	4826	1051	849
15776	\N	4826	1052	849
6528	\N	4826	554	228
6529	\N	4826	582	228
6530	\N	4826	555	228
6531	\N	4826	556	228
6532	\N	4826	557	228
6534	\N	4826	558	228
6535	\N	4826	559	228
17790	\N	4826	351	939
17791	\N	4826	1109	939
17792	\N	4826	1112	939
17793	\N	4826	354	939
17794	\N	4826	352	939
17795	\N	4826	1111	939
17796	\N	4826	1113	939
17797	\N	4826	1114	939
17798	\N	4826	1110	939
17799	\N	4826	349	939
17800	\N	4826	355	939
17801	\N	4826	353	939
17802	\N	4826	350	939
17360	\N	4826	1106	907
17361	\N	4826	1103	907
17362	\N	4826	1108	907
17364	\N	4826	342	907
17365	\N	4826	347	907
17366	\N	4826	343	907
17367	\N	4826	1105	907
17368	\N	4826	345	907
17369	\N	4826	344	907
17370	\N	4826	341	907
17372	\N	4826	348	907
6536	\N	4826	546	228
6538	\N	4826	560	228
6539	\N	4826	597	228
6540	\N	4826	561	228
6541	\N	4826	562	228
6542	\N	4826	584	228
6543	\N	4826	563	228
6544	\N	4826	585	228
6545	\N	4826	564	228
6546	\N	4826	547	228
6547	\N	4826	586	228
6548	\N	4826	565	228
6551	\N	4826	548	228
6554	\N	4826	568	228
6556	\N	4826	570	228
6557	\N	4826	550	228
6558	\N	4826	588	228
6559	\N	4826	132	228
6561	\N	4826	571	228
6563	\N	4826	572	228
6564	\N	4826	573	228
6565	\N	4826	574	228
6566	\N	4826	134	228
6567	\N	4826	590	228
15705	\N	4826	1042	842
15706	\N	4826	1038	842
15707	\N	4826	1040	842
15708	\N	4826	1041	842
15709	\N	4826	296	842
15710	\N	4826	1037	842
15712	\N	4826	1039	842
15713	\N	4826	1036	842
6569	\N	4826	575	228
6571	\N	4826	576	228
6572	\N	4826	133	228
6573	\N	4826	591	228
6574	\N	4826	577	228
6575	\N	4826	599	228
6576	\N	4826	578	228
6577	\N	4826	552	228
6578	\N	4826	592	228
6579	\N	4826	593	228
6580	\N	4826	594	228
6581	\N	4826	579	228
6582	\N	4826	600	228
6583	\N	4826	595	228
6584	\N	4826	601	228
6585	\N	4826	553	228
6586	\N	4826	596	228
6587	\N	4826	580	228
6588	\N	4826	581	228
4843	\N	4826	512	199
4844	\N	4826	513	199
4845	\N	4826	514	199
4846	\N	4826	515	199
4847	\N	4826	516	199
4848	\N	4826	517	199
4849	\N	4826	492	199
4850	\N	4826	518	199
4851	\N	4826	505	199
4852	\N	4826	502	199
4853	\N	4826	493	199
4854	\N	4826	519	199
4855	\N	4826	506	199
4856	\N	4826	507	199
4857	\N	4826	494	199
4858	\N	4826	520	199
4859	\N	4826	495	199
4860	\N	4826	521	199
4861	\N	4826	496	199
4862	\N	4826	522	199
4864	\N	4826	524	199
4866	\N	4826	525	199
4867	\N	4826	526	199
4868	\N	4826	527	199
4869	\N	4826	528	199
4870	\N	4826	529	199
4871	\N	4826	530	199
4872	\N	4826	531	199
4873	\N	4826	532	199
4874	\N	4826	533	199
4875	\N	4826	534	199
4878	\N	4826	535	199
4880	\N	4826	508	199
4882	\N	4826	537	199
4883	\N	4826	130	199
4884	\N	4826	510	199
4885	\N	4826	538	199
4886	\N	4826	500	199
4887	\N	4826	539	199
4888	\N	4826	503	199
4889	\N	4826	540	199
4890	\N	4826	501	199
4891	\N	4826	541	199
4892	\N	4826	542	199
4893	\N	4826	511	199
4894	\N	4826	543	199
4895	\N	4826	504	199
4896	\N	4826	544	199
21432	\N	4828	1500	1204
17373	\N	4828	346	907
6537	\N	4828	583	228
6549	\N	4828	587	228
6552	\N	4828	549	228
6553	\N	4828	567	228
4863	\N	4828	523	199
4876	\N	4828	498	199
4881	\N	4828	509	199
21453	\N	4830	1508	1205
21441	\N	4829	1497	1205
21442	\N	4829	1498	1205
21443	\N	4829	1499	1205
21444	\N	4829	1502	1205
21445	\N	4829	1507	1205
21446	\N	4829	1496	1205
21447	\N	4829	1500	1205
21448	\N	4829	1501	1205
21449	\N	4829	1503	1205
21450	\N	4829	1504	1205
21452	\N	4829	1506	1205
21454	\N	4829	1509	1205
21455	\N	4829	1495	1205
21451	\N	4832	1505	1205
21533	\N	4868	1537	1211
21534	\N	4868	1535	1211
21540	\N	4868	1536	1211
20561	\N	4868	1378	1122
20408	\N	4868	1374	1106
14440	\N	4868	254	719
14447	\N	4868	248	719
14457	\N	4868	912	719
13402	\N	4868	241	674
13404	\N	4868	240	674
13414	\N	4868	236	674
21531	\N	4865	458	1211
21539	\N	4865	1525	1211
20409	\N	4865	438	1106
14460	\N	4865	906	719
13398	\N	4865	229	674
21537	\N	4866	1526	1211
13408	\N	4866	905	674
13409	\N	4866	243	674
21532	\N	4867	459	1211
21535	\N	4867	1527	1211
21536	\N	4867	1528	1211
21538	\N	4867	1529	1211
21541	\N	4867	1530	1211
21542	\N	4867	1531	1211
21543	\N	4867	1532	1211
21544	\N	4867	1533	1211
21545	\N	4867	1534	1211
20562	\N	4867	446	1122
20563	\N	4867	443	1122
20564	\N	4867	447	1122
20565	\N	4867	441	1122
20566	\N	4867	1376	1122
20567	\N	4867	1377	1122
20568	\N	4867	445	1122
20569	\N	4867	444	1122
20570	\N	4867	442	1122
20402	\N	4867	435	1106
20403	\N	4867	440	1106
20405	\N	4867	436	1106
20406	\N	4867	1375	1106
20407	\N	4867	1373	1106
20410	\N	4867	439	1106
14436	\N	4867	245	719
14437	\N	4867	250	719
14438	\N	4867	253	719
14439	\N	4867	908	719
14443	\N	4867	252	719
14445	\N	4867	247	719
14448	\N	4867	909	719
14449	\N	4867	910	719
14450	\N	4867	907	719
14451	\N	4867	257	719
14453	\N	4867	258	719
14454	\N	4867	260	719
14455	\N	4867	911	719
14456	\N	4867	244	719
14458	\N	4867	259	719
13396	\N	4867	232	674
13399	\N	4867	901	674
13400	\N	4867	239	674
13401	\N	4867	231	674
13403	\N	4867	237	674
13406	\N	4867	235	674
13407	\N	4867	234	674
13410	\N	4867	238	674
21552	\N	4874	1526	1212
17448	\N	4874	342	913
21546	\N	4876	458	1212
21547	\N	4876	459	1212
21548	\N	4876	1537	1212
21549	\N	4876	1535	1212
21550	\N	4876	1527	1212
21551	\N	4876	1528	1212
21553	\N	4876	1529	1212
21554	\N	4876	1525	1212
21555	\N	4876	1536	1212
21556	\N	4876	1530	1212
21557	\N	4876	1531	1212
21558	\N	4876	1532	1212
21560	\N	4876	1534	1212
17699	\N	4876	351	932
17700	\N	4876	1109	932
17701	\N	4876	1112	932
17702	\N	4876	354	932
17703	\N	4876	352	932
17704	\N	4876	1111	932
17705	\N	4876	1113	932
17706	\N	4876	1114	932
17707	\N	4876	1110	932
17708	\N	4876	349	932
17709	\N	4876	355	932
17711	\N	4876	350	932
17444	\N	4876	1106	913
17445	\N	4876	1103	913
17446	\N	4876	1108	913
17447	\N	4876	1107	913
17451	\N	4876	1105	913
17452	\N	4876	345	913
17454	\N	4876	341	913
17455	\N	4876	1104	913
17456	\N	4876	348	913
17457	\N	4876	346	913
21559	\N	4873	1533	1212
21575	\N	4880	1534	1213
21561	\N	4877	458	1213
21562	\N	4877	459	1213
21563	\N	4877	1537	1213
21564	\N	4877	1535	1213
21565	\N	4877	1527	1213
21566	\N	4877	1528	1213
21567	\N	4877	1526	1213
21568	\N	4877	1529	1213
21569	\N	4877	1525	1213
21570	\N	4877	1536	1213
21571	\N	4877	1530	1213
21572	\N	4877	1531	1213
21573	\N	4877	1532	1213
21574	\N	4877	1533	1213
21578	\N	4870	1537	1214
21579	\N	4870	1535	1214
21583	\N	4870	1529	1214
21586	\N	4870	1530	1214
21590	\N	4870	1534	1214
21576	\N	4872	458	1214
21577	\N	4872	459	1214
21580	\N	4872	1527	1214
21581	\N	4872	1528	1214
21582	\N	4872	1526	1214
21584	\N	4872	1525	1214
21585	\N	4872	1536	1214
21587	\N	4872	1531	1214
21588	\N	4872	1532	1214
21589	\N	4872	1533	1214
21593	\N	4864	1537	1215
21594	\N	4864	1535	1215
21597	\N	4864	1526	1215
21604	\N	4864	1533	1215
21598	\N	4863	1529	1215
21595	\N	4862	1527	1215
21596	\N	4862	1528	1215
21599	\N	4862	1525	1215
21600	\N	4862	1536	1215
21601	\N	4862	1530	1215
21602	\N	4862	1531	1215
21603	\N	4862	1532	1215
21605	\N	4862	1534	1215
18987	\N	4862	1310	1038
18881	\N	4862	1297	1031
18883	\N	4862	1298	1031
18884	\N	4862	389	1031
18885	\N	4862	1299	1031
18887	\N	4862	1301	1031
18888	\N	4862	1295	1031
18889	\N	4862	1296	1031
18890	\N	4862	390	1031
18891	\N	4862	1300	1031
18892	\N	4862	1302	1031
21676	\N	4911	1550	1221
21677	\N	4911	1552	1221
21678	\N	4911	1554	1221
21679	\N	4911	1555	1221
21680	\N	4911	1557	1221
21681	\N	4911	1558	1221
21682	\N	4911	1549	1221
21683	\N	4911	1551	1221
21684	\N	4911	1553	1221
21686	\N	4911	1556	1221
21685	\N	4909	463	1221
21687	\N	4916	1550	1222
21688	\N	4916	1552	1222
21689	\N	4916	1554	1222
21690	\N	4916	1555	1222
21691	\N	4916	1557	1222
21692	\N	4916	1558	1222
21693	\N	4916	1549	1222
21695	\N	4916	1553	1222
21696	\N	4916	463	1222
21697	\N	4916	1556	1222
13483	\N	4916	237	678
13484	\N	4916	240	678
13485	\N	4916	230	678
13486	\N	4916	235	678
13487	\N	4916	234	678
13488	\N	4916	905	678
13489	\N	4916	243	678
13491	\N	4916	902	678
13492	\N	4916	903	678
13493	\N	4916	242	678
13495	\N	4916	904	678
21694	\N	4914	1551	1222
14136	\N	4914	245	707
14152	\N	4914	256	707
13482	\N	4914	241	678
21698	\N	4917	1550	1223
21703	\N	4917	1558	1223
21699	\N	4918	1552	1223
21700	\N	4918	1554	1223
21701	\N	4918	1555	1223
21702	\N	4918	1557	1223
21704	\N	4918	1549	1223
21705	\N	4918	1551	1223
21706	\N	4918	1553	1223
21708	\N	4920	1556	1223
21707	\N	4919	463	1223
21710	\N	4902	1552	1224
21714	\N	4902	1558	1224
21715	\N	4902	1549	1224
21719	\N	4902	1556	1224
21709	\N	4904	1550	1224
21711	\N	4901	1554	1224
21716	\N	4901	1551	1224
21717	\N	4901	1553	1224
21718	\N	4901	463	1224
21712	\N	4903	1555	1224
21713	\N	4903	1557	1224
21721	\N	4908	1552	1225
21722	\N	4908	1554	1225
21726	\N	4908	1549	1225
21727	\N	4908	1551	1225
21730	\N	4908	1556	1225
21822	\N	4947	466	1234
21830	\N	4948	1569	1234
7171	\N	2089	571	238
7172	\N	2089	131	238
7173	\N	2089	572	238
7174	\N	2089	573	238
7175	\N	2089	574	238
7176	\N	2089	134	238
7177	\N	2089	590	238
7178	\N	2089	551	238
7180	\N	2089	598	238
7182	\N	2089	133	238
7184	\N	2089	577	238
7185	\N	2089	599	238
7186	\N	2089	578	238
7187	\N	2089	552	238
7190	\N	2089	594	238
7191	\N	2089	579	238
7192	\N	2089	600	238
7193	\N	2089	595	238
7195	\N	2089	553	238
7197	\N	2089	580	238
7198	\N	2089	581	238
7161	\N	2091	548	238
7181	\N	2091	576	238
\.


--
-- Data for Name: questions; Type: TABLE DATA; Schema: public; Owner: pedro
--

COPY public.questions (id, active, content, number, number_of_answers, number_of_correct, title) FROM stdin;
71	t	The main architectural driver for the nginx system was	71	0	0	nginxFirstADINGLES
101	t	The document describing the Glasgow Haskell Compiler presents two design decisions about the development of the *Runtime System*. The first of those decisions is described like this:  \n>"The garbage collector is built on top of a block layer that manages memory in units of blocks, where a block is a multiple of 4 KB in size. The block layer has a very simple API: [...]. This is the only API used by the garbage collector for allocating and deallocating memory. Blocks of memory are allocated with `allocGroup` and freed with `freeGroup`."  \n  \nWhich architectural style is more adequate to represent this design decision?	101	0	0	GHCBlockLayerINGLES
316	t	Frank Buschmann states that:  \n>"Architects use flexibility as a cover for uncertainty."  \n  \n	316	0	0	Prioritize
317	t	In his article *On Hammers and Nails, and Falling in Love with Technology and Design* what is the main type of influence on the architecture?	317	0	0	HammersNails
610	t	Consider the Component-and-Connector viewtype	610	0	0	ComponentAndConnectorOne
301	t	Frank Buschmann states that:  \n>"Featuritis is the tendency to trade functional coverage for quality - the more functions the earlier they're delivered, the better."  \n  \n	301	0	0	Featuritis
303	t	On the course slides you can find the following definition of architecture:  \n>"The software architecture of a program or computing system is the structure or structures of the system, which comprise software elements, the externally visible properties of those elements, and the relationships among them."  \n  \nHowever, in the book you can find another definition:  \n>"The software architecture of a system is the set of structures needed to reason about the system, which comprise the software elements, relations among them, and the properties of both."  \n  \n	303	0	0	ArchitectureDefinition
304	t	Martin Fowler, *Who Needs and Architect?*, cites Ralph Johnson sentence:  \n>"In most successful software projects, the expert developers working on that project have a shared understanding of the system design. This shared understanding is called architecture."  \n  \n	304	0	0	SharedUnderstanding
306	t	Frank Buschmann states that:  \n>"Overly flexible systems are hard to configure, and when they're finally configured, they lack qualities like performance or security."  \n  \n	306	0	0	Flexibilitis
307	t	In his article, *Featuritis, Performitis, and Other Deseases*, Frank Buschmann claims that:	307	0	0	FeaturitisPerformitisFlexibilities
308	t	In wikipedia you can find the following fragment of a definition:  \n>"An individual software component is a software package, or a module that encapsulates a set of related functions."  \n  \nAccording to the definitions taught in the course the above *individual software component* corresponds to:	308	0	0	ComponentvsModule
310	t	Frank Buschmann, *Introducing the Pragmatic Architect*, defines the *architecture astronauts*. This kind of architect	310	0	0	ArchitectAstronauts
311	t	Frank Buschmann states that:  \n>"There's only one escape from such situations: architects must actively break the cycle of mutual misunderstanding and mistrust!"  \n  \n	311	0	0	Explicit
312	t	The *Walking Skeleton* referred in Frank Buschmann's article, *Featuritis, Performitis, and Other Deseases*:	312	0	0	WalkingSkeleton
313	t	In the Java documentation you can find:  \n>"`public abstract class Component`\\*`extends Object`\\*`implements ImageObserver, MenuContainer, Serializable`"  \n  \nClass `Component` is:	313	0	0	ComponentvsModuleTwo
315	t	Frank Buschmann, *Introducing the Pragmatic Architect*, defines the *architecture dwarves*. These kind of architects	315	0	0	ArchitectDwarves
401	t	When comparing Amazon Silk with Google Chrome in the context of mobile devices	401	0	0	SilkMobileDevices
402	t	Consider the architectural views for the ThousandParsec system. The following diagram depicts a proposal of application-specific types for the architectural components, where the names of the ports are missing. Between the GameServer and Repository component   \n![image][image]  \n	402	0	0	ThousandParsecReadWriteConnector
403	t	Consider the architecture of the Morrison's OrderPad. The connector between the client component, executing in the Pad, and the server component, executing in the OrderPadDatabase	403	0	0	OrderPadReliability
404	t	Consider the architectural views for the SocialCalc system. The following diagram depicts a proposal for a component-and-connector view of the client Spreadsheet. A ConflictResolution module is used when local commands conflict with remote commands.   \n![image][image]  \n	404	0	0	SocialCalcConflictResolution
405	t	When the domain logic is organized using a Table Module pattern	405	0	0	LogicAccessTableModule
406	t	When comparing Amazon Silk with Google Chrome	406	0	0	SilkConnections
407	t	Consider the architectural views for the ThousandParsec system. The following diagram depicts a fragment of a proposal for the decomposition view of the system. The ThousandParsec protocol   \n![image][image]  \n	407	0	0	ThousandParsecModule
409	t	Consider the architectural views for the SocialCalc system. The following diagram depicts a proposal for a component-and-connector view of the client Spreadsheet. It can be read in the case description: *A simple improvement is for each client to broadcast its cursor position to other users, so everyone can see which cells are being worked on.*   \n![image][image]  \n	409	0	0	SocialCalcRemoteCursor
410	t	When the domain logic is organized using a Transaction Script pattern the domain objects	410	0	0	LogicAccessTransactionScriptDomainObjects
411	t	When comparing Amazon Silk with Google Chrome in the context of the prediction of pages the user is going to access	411	0	0	SilkPredictor
412	t	Consider the architectural views for the ThousandParsec system. The following diagram depicts a proposal of application-specific types for the architectural components, where the names of the ports are missing. Between the GameClient and GameServer components   \n![image][image]  \n	412	0	0	ThousandParsecTPConnector
413	t	Consider the architecture of the Morrison's OrderPad. The decision between the use of a Native application or HTML5 on the implementation of the client in the Pad	413	0	0	OrderPadPortability
557	t	Suppose that after designing a successful architecture for a particular client the software house management decides to create a cross-functional internal department to start providing products for this particular segment of the market.	557	0	0	ArchitecturalInfluenceCycle
606	t	When describing their system people refer to a part of it as containing a database server. Applying the component-and-connector styles learned in the course we can say that this system uses	606	0	0	CCStyleOne
607	t	In the Continuous Integration case study can be read  \n>"The space of architectures for continuous integration systems seems to be dominated by two extremes: master/slave architectures, in which a central server directs and controls remote builds; and reporting architectures, in which a central server aggregates build reports contributed by clients. All of the continuous integration systems of which we are aware have chosen some combination of features from these two architectures."  \n  \nThe tactic that is referred in both architectures is	607	0	0	ContinuousIntegrationOne
608	t	Consider the Decomposition architectural style of the Module viewtype	608	0	0	ModuleViewtypeOne
609	t	The Infinispan system can be used as a library, in which case it is embedded into a Java application, or as a server, in which case it is a remote data grid.	609	0	0	InfinispanOne
1081	t	The quality that is more relevant to views of the module viewtype is:	1081	0	0	ModuleViewTypeOne
701	t	Consider the following fragment of the *MediaWiki* system description:  \n*To optimize the delivery of JavaScript and CSS assets, the ResourceLoader module was developed to optimize delivery of JS and CSS. Started in 2009, it was completed in 2011 and has been a core feature of MediaWiki since version 1.17. ResourceLoader works by loading JS and CSS assets on demand, thus reducing loading and parsing time when features are unused, for example by older browsers. It also minifies the code, groups resources to save requests, and can embed images as data URIs*  \nThe *ResourceLoader* supports a quality	701	0	0	MWResourceLoaderTacticEEEN
702	t	In which performance tactic it may occur that not all the inputs are processed	702	0	0	PerfomanceTacticOne
703	t	Several of the cases studied in this course had scalability requirements. That means that those systems should be designed in such a way that they	703	0	0	ScalabilityINGLES
705	t	Consider the following architectural view of the Adventure Builder system, designed around the Order Processing Center   \n![image][image]  \n The views **does not** use the architectural style	705	0	0	AdventureBuilderThree
706	t	Consider the following requirement for availability of the Adventure Builder system  \n>"The Consumer Web site sent a purchase order request to the order processing center (OPC). The OPC processed that request but didn't reply to Consumer Web site within five seconds, so the Consumer Web site resends the request to the OPC."  \n  \nIf we represent this requirement as a scenario	706	0	0	AdventureBuilderFive
807	t	In the Graphite system the component *carbon* provides to *webapp* components an access interface to the *buffers* in order to improve the quality of	807	0	0	GraphiteScenarioTacticsOne
808	t	In the HDFS system when the *CheckpointNode* and the *NameNode* are deployed in different nodes, the *CheckpointNode* provides:	808	0	0	HadoopCheckpoint
897	t	Consider the Uses architectural style of the Module viewtype	897	0	0	Layered
1099	t	To analyse the performance of a system	1099	0	0	PerformanceOne
1128	t	A voting tactic can be used to	1128	0	0	AvailabilityTwo
415	t	When the domain logic is organized using a Transaction Script pattern the most suitable data source patterns are	415	0	0	LogicAccessTransactionScript
63	t	One of the most important decisions during the development of the Glasgow Haskell Compiler was to perform the type-checking before the desugaring of an Haskell program into a program in the Core language (*type-check-before-desugar*). This design decision	63	0	0	GHCDesugaringINGLES
2	t	There are other factors that affect the development of a software system, besides its functional requirements and quality attributes. For example, factors such as budget or available time. These factors	2	0	0	AtrQualNegocio
3	t	Suppose that you are developing a new software system and that you want some part of the system's functionality to be easily reusable in future systems. Which of the following architectural styles are more suitable to show that the system architecture meets this requirement.	3	0	0	Reutilizar
4	t	Suppose you have a system with a client-server architecture that was designed to support the simultaneous existence of at most 100 clients, without specific requirements for availability. The solution adopted and put into operation four years ago is a single server component to which all clients connect to. This solution satisfies the initial requirements but with the recent increase in the maximum number of clients to 200, the system no longer has acceptable performance. Not knowing anything else about the system's architecture, which solution do you propose to solve the system's performance problems?	4	0	0	AumentarDesempenhoClienteServidor
5	t	According to the SEI model, there are three different architectural viewtypes that are usually necessary to describe completely a software architecture.	5	0	0	TresTiposVista
8	t	The requirements for complex systems are usually very numerous and conflicting among them, making it impossible to satisfy all the requirements in a given implementation of the system. Therefore, the recommended process for making the design of a software architecture involves the identification of the *architectural drivers* that will shape the design of architecture. These *architectural drivers* should be chosen so that they are	8	0	0	ArchitecturalDrivers
9	t	The email system is composed of various types of components playing different roles. For example, to send an email, a user can use a program such as Microsoft Outlook or Mozilla Thunderbird, generically designed a *mail user agent* (MUA), to compose his message and send it. To send the message, the MUA typically connects to a *mail transfer agent* (MTA) that receives the message, analyzes the message's headers to determine the recipients and, after querying the DNS system to determine the MTA responsible for each recipient, it connects to the MTAs responsible for the destination addresses to deliver the message. Each of these MTAs receives the message and stores it locally or forwards it to others MTAs (for example, when there are forwards or aliases configured, or when the MTA that receives the message is not the ultimately responsible for the email address of the recipient). Given this simplified description of the operation of the email system, which of the following architectural styles is more appropriate to represent the pattern of interaction between the MTAs?	9	0	0	ArqEmailMTA
10	t	Considering yet the example of the email system, MUAs are used not only to compose and to send messages, but also for users to read the email messages sent to them. For this, the MUAs have to get those messages from the component that stores them to show them to the user. Two different ways of doing this is by using the POP and IMAP protocols. In the first case, messages are moved from the POP server to the user's computer. In second case, the messages are always stored on the IMAP server, allowing the user to access email from different computers, as long as they are able to connect to the same IMAP server. Which of the following architectural styles is more appropriate to represent the pattern of interaction between the MUAs and a IMAP server?	10	0	0	ArqEmailIMAP
11	t	The recent developments in web applications that made them provide a richer user interface led to a change in its architecture: part of the application's computation has to be done in the web browser used by users to access the application. How is this change in the architecture manifested in the different types of views that describe the software architecture of a web application?	11	0	0	AlteracaoWebDois
12	t	Given the complexity of building a good automatic Chess player, programs that play chess usually make use of existing chess engines, as shown by the following excerpt from Wikipedia:  \n>"A chess engine is a computer program that can play the game of chess. Most chess engines do not have their own graphical user interface (GUI) but are rather console applications that communicate with a GUI such as XBoard (Linux) and WinBoard (Windows) via a standard protocol."  \n  \nIn the web page for XBoard, we may read the following:  \n>"XBoard is a graphical user interface for chess [...]. It displays a chessboard on the screen, accepts moves made with the mouse, and loads and saves games in Portable Game Notation (PGN). It serves as a front-end for many different chess services, including:  \n-  Chess engines that will run on your machine and play a game against you or help you analyze, such as GNU Chess, Crafty, or many others.  \n-  [...]  \n"  \n  \nGiven the above information on XBoard, chess engines, and how they interact at runtime, which of the following architectural styles best represents the of architecture of a software system based on XBoard and one of the engines?	12	0	0	XBoardChess
13	t	Suppose that you join the development team of a very large software system, and that you are assigned some tasks to change some existing features. Which of the following architectural views would be, in principle, more useful to you to perform those tasks quickly?	13	0	0	AlterarFuncionalidadesExistentes
14	t	Consider the following excerpt from the Wikipedia page on *black-box testing*:  \n>"Black-box testing is a method of software testing that tests the functionality of an application as opposed to its internal structures or workings. Specific knowledge of the application's code/internal structure and programming knowledge in general is not required. Test cases are built around specifications and requirements, i.e., what the application is supposed to do."  \n  \nAssuming that you belong to the team testing a complex system and that you are responsible for performing black box tests on the system, which of the following architectural views of the system would be most useful to you?	14	0	0	BlackBoxTesting
416	t	When comparing Amazon Silk with Google Chrome	416	0	0	SilkCaching
15	t	Consider an enterprise application that needs to keep its data persistently, but for which no one knows yet what is the volume of information that will be handled by the application. Therefore, the system's architect intends to develop the system such that it is possible to change easily the relational database (RDBMS) component used to store the application's data, replacing it with an RDBMS from another manufacturer. Given that this is a common requirement, the recommended software architecture for such applications fulfills this requirement by using a particular architectural style. Which style is it?	15	0	0	TrocarBDCamadas
16	t	Suppose that you decided to use the Google App Engine (GAE) in the development of a web application. The GAE is described in the Wikipedia as follows:  \n>"Google App Engine is a platform for developing and hosting web applications in Google-managed data centers. Google App Engine is cloud computing technology. It virtualizes applications across multiple servers and data centers. [...] Google App Engine is free up to a certain level of used resources. Fees are charged for additional storage, bandwidth, or CPU cycles required by the application."  \n  \nOn the other hand, the GAE documentation reads the following:  \n>"With App Engine, you can build web applications using standard Java technologies and run them on Google's scalable infrastructure. The Java environment provides a Java 6 JVM, a Java Servlets interface, and support for standard interfaces to the App Engine scalable datastore and services, such as JDO, JPA, JavaMail, and JCache. Standards support makes developing your application easy and familiar, and also makes porting your application to and from your own servlet environment straightforward."  \n  \nTaking into account these two perspectives on the GAE, which architectural styles are more appropriate to represent the use of GAE in the software architecture of your web application?	16	0	0	GoogleAppEngine
17	t	The Eclipse IDE is an open source application written in Java, and is extensible through the use of plug-ins. In the document that describes the existing plug-ins architecture in Eclipse, we may read the following:  \n>"A plug-in in Eclipse is a component that provides a certain type of service within the context of the Eclipse workbench. [...] The plug-in class provides configuration and management support for the plug-in. A plug-in class in Eclipse must extend `org.eclipse.core.runtime.Plugin`, which is an abstract class that provides generic facilities for managing plug-ins."  \n  \nConsidering the model and terminology used in the course to describe a software architecture, what kind of views are more appropriate to represent the plug-ins architecture of Eclipse described above?	17	0	0	PluginsEclipse
18	t	When someone uses the Domain Model pattern to implement the domain logic layer of an enterprise application, it is common to use also the Service Layer pattern. The Service Layer pattern is used in these cases	18	0	0	ServiceLayer
19	t	The Unit of Work pattern is often used in enterprise applications	19	0	0	UnitOfWork
20	t	The Identity Map pattern is typically used in enterprise applications	20	0	0	IdentityMap
21	t	The scalability quality is achieved in the Hadoop system only because	21	0	0	HadoopEscalabilidadePossivelINGLES
22	t	In the Hadoop system:	22	0	0	HadoopDisponibilidadeDesempenhoINGLES
23	t	In the Hadoop system the fault recovery tactics are:	23	0	0	HadoopTacticasRecuperacaoFaltasINGLES
24	t	In the Hadoop system the tactics used to reintroduce a DataNode after its failure are:	24	0	0	HadoopTacticasRecuperacaoFaltasDoisINGLES
25	t	The security tactics used in The Hadoop system deployed at Yahoo! are:	25	0	0	HadoopTacticasSegurancaINGLES
27	t	A layer, in the layers architectural style, is a module:	27	0	0	ModulosCamadasINGLES
28	t	The uses architectural style allows to assess the impact of changes in modules	28	0	0	UtilizacaoImpactoAlteracoesINGLES
29	t	In the uses architectural style a call does not necessarily correspond to a uses relationship because:	29	0	0	UtilizacaoNotificaINGLES
30	t	According to the attribute-driven design process, we should design the software architecture for a system based on a selected list of requirements, which are called the *architectural drivers*. These architectural drivers should be sorted according to their importance for the system's stakeholders because	30	0	0	UtilizacaoNotificaDoisINGLES
31	t	In the HDFS system, the main responsibility of the DataNode component is to store the data blocks corresponding to the client's files, and usually there are several instances of this component on each system. The architectural style that best describes the interaction pattern among the various instances of DataNode is	31	0	0	HadoopInteraccaoDataNodesINGLES
32	t	According to Section 8.2.3, the NameNode component issues commands to the DataNodes so that they execute some operations on their blocks, whereas DataNodes have to send reports regularly to the NameNode. The architecture that best describes how these two types of components interact in the HDFS system is	32	0	0	HadoopInteraccaoNameNodeDataNodesINGLES
33	t	Imagine that you intend to describe how a client reads a file from an HDFS system while supporting sporadic failures in the hardware of some DataNodes, but without affecting the availability of the system. To accomplish that, you want to use a component-and-connector view containing only two types of components: the HDFS Client, and the DataNode.	33	0	0	HadoopNameNodeComoConectorINGLES
34	t	The last paragraph of Section 8.2.2 describes the solution used by the NameNode to obtain a certain level of performance while writing to disk. Which architectural style is more adequate to represent the solution used?	34	0	0	HadoopNameNodeThreadsINGLES
36	t	When someone uses the Domain Model pattern to implement the domain logic layer of an enterprise application, it is common to use also the Service Layer pattern. The Service Layer pattern is used in these cases	36	0	0	ServiceLayerINGLES
37	t	Imagine that you are developing an architectural view where you are using the Shared Data style and that a member of your team proposes that two of Data Accessors communicate directly between them. In your opinion	37	0	0	SharedDataAccessorsDirectINGLES
62	t	According to the document that describes the Glasgow Haskell Compiler:  \n>"At the highest level, GHC can be divided into three distinct chunks:  \n-  The compiler itself.  \n-  The Boot Libraries.  \n-  The Runtime System (RTS).  \n"  \n  \nWhat is the most architecturally correct way of classifying the three *chunks* that this text refers to?	62	0	0	GHCChunksINGLES
41	t	From the stakeholders perspective the use of low cost servers to build the clusters is:	41	0	0	HadoopStakeholdersEurosINGLES
42	t	In the Hadoop system the use of a *BackupNode* instead of a *CheckpointNode*:	42	0	0	HadoopCheckpointBackupNodeINGLES
43	t	In the Hadoop system when the *CheckpointNode* and the *NameNode* are deployed in different nodes, the *CheckpointNode* provides:	43	0	0	HadoopCheckpointINGLES
44	t	The Hadoop system support of different block placement policies:	44	0	0	HadoopPoliticaLocalizacaoReplicasINGLES
45	t	In the Hadoop system, during normal operation, *NameNode* could use a ping tactic to know whether *DataNodes* are available	45	0	0	HadoopPingINGLES
46	t	Knowing the deployment structure in the Hadoop system is critical to the effective system operation. Therefore, for each deployment, the administrator can configure a script that returns a node's rack identification given a node's address (see section 8.3.2).	46	0	0	HadoopInstalacaoINGLES
47	t	The *Checkpoint/rollback* tactic is a tactic for	47	0	0	TacticaCheckpointRollbackINGLES
49	t	A view of the *Uses* style that contains a loop in the uses relationships	49	0	0	UsaCircularINGLES
50	t	The main difference between the *Uses* relation of the Uses style and the *Allowed to Use* relation of the Layers style	50	0	0	UsaPodeUsarINGLES
51	t	The third paragraph of section 8.3.1 describes the buffering mechanism used by an HDFS client when it is writing to a file. How would you describe this mechanism using an architectural view?	51	0	0	HadoopFileWriteBufferedINGLES
54	t	Suppose that you are implementing a web application and that you decided to use an HDFS system to store the data of your application---that is, your web application will be a client of the HDFS system. How does this decision affects the architecture of your web application?	54	0	0	HadoopComoDatabaseINGLES
55	t	Considering the description of the *CheckpointNode* made in Section~8.2.5, which architectural style best represents the interaction between the *CheckpointNode* and the *NameNode* components?	55	0	0	HadoopCheckpointNodeINGLES
56	t	*Domain Model* and *Transaction Script* are two of the existing patterns to implement the domain logic layer of an enterprise application. Choosing one or the other	56	0	0	DomainModelINGLES
57	t	Which of the following sentences best captures the restrictions regarding which components may execute in which machines in the Deployment style?	57	0	0	RelacaoComponentesMaquinasINGLES
60	t	Suppose that you are designing the software architecture for an enterprise application that has security requirements about the confidentiality of some of its data. To show to the stakeholders that your system satisfies the security requirements you have to use views of which architectural style?	60	0	0	SegurancaINGLES
61	t	Two of the *stakeholders* for the Glasgow Haskell Compiler were the UK government and the researchers that want to do research on functional programming languages. Which of these *stakeholders* had a more significant influence in the software architecture of the system?	61	0	0	GHCStakeholdersINGLES
64	t	According to the document that describes the Glasgow Haskell Compiler:  \n>"The Runtime System is a library of mostly C code that is linked into every Haskell program. It provides the support infrastructure needed for running the compiled Haskell code, including the following main components:  \n-  Memory management, including a parallel, generational, garbage collector;  \n-  Thread management and scheduling;  \n-  The primitive operations provided by GHC;  \n-  A bytecode interpreter and dynamic linker for GHCi.  \n"  \n  \nWhich system qualities are improved by the design decision of creating the Runtime System, described above?	64	0	0	GHCRTSINGLES
65	t	Like many other compilers, the compilation of an Haskell program with the Glasgow Haskell Compiler uses the Pipe-and-Filter style, creating a *pipeline* composed of several compilation phases. The goal of using this architectural style in GHC is	65	0	0	GHCPipeAndFilterINGLES
66	t	Which of the following sentences better describes the ZeroMQ system?	66	0	0	ZeroMQAppsINGLES
67	t	According to the document that describes ZeroMQ:  \n>"The idea was to launch one worker thread per CPU core---having two threads sharing the same core would only mean a lot of context switching for no particular advantage."  \n  \nWhich architectural style is more adequate to represent the information presented above?	67	0	0	ZeroMQWorkersPerCoreINGLES
68	t	According to the document that describes ZeroMQ:  \n>"Messaging patterns form a layer (the so-called "scalability layer") on top of the transport layer (TCP and friends). Individual messaging patterns are implementations of this layer."  \n  \nWhat is the main advantage of this layered architecture adopted by ZeroMQ?	68	0	0	ZeroMQMessagingPatternsINGLES
69	t	ZeroMQ uses dynamic batching to control the performance of the system. The goal of this approach is	69	0	0	ZeroMQBatchingINGLES
70	t	According to the document that describes ZeroMQ:  \n>"ØMQ uses a lock-free queue in pipe objects to pass messages between the user's threads and ØMQ's worker threads. There are two interesting aspects to how ØMQ uses the lock-free queue. First, each queue has exactly one writer thread and exactly one reader thread. If there's a need for 1-to-N communication, multiple queues are created. Given that this way the queue doesn't have to take care of synchronising the writers (there's only one writer) or readers (there's only one reader) it can be implemented in an extra-efficient way."  \n  \nThe architectural style that better represents the interaction pattern described above is	70	0	0	ZeroMQLockFreeINGLES
72	t	According to the document that describes nginx:  \n>"Traditional process- or thread-based models of handling concurrent connections involve handling each connection with a separate process or thread, and blocking on network or input/output operations. nginx followed a different model. It was actually inspired by the ongoing development of advanced event-based mechanisms in a variety of operating systems. What resulted is a modular, event-driven, asynchronous, single-threaded, non-blocking architecture which became the foundation of nginx code."  \n  \nThe decision of turning nginx into an *event-driven*, *asynchronous*, *single-threaded*, and *non-blocking* system was made because	72	0	0	nginxEventDrivenINGLES
74	t	According to the document that describes nginx:  \n>"While handling a variety of actions associated with accepting, processing and managing network connections and content retrieval, nginx uses event notification mechanisms and a number of disk I/O performance enhancements in Linux, Solaris and BSD-based operating systems, like kqueue, epoll, and event ports. The goal is to provide as many hints to the operating system as possible, in regards to obtaining timely asynchronous feedback for inbound and outbound traffic, disk operations, reading from or writing to sockets, timeouts and so on."  \n  \nThe goal of this approach used in the development of nginx was	74	0	0	nginxOSOptimizationsINGLES
75	t	According to the document that describes nginx:  \n>"Traditional process- or thread-based models of handling concurrent connections involve handling each connection with a separate process or thread, and blocking on network or input/output operations."  \n  \nThe architectural style that better describes the model presented above for processing requests is	75	0	0	nginxProcessThreadINGLES
76	t	According to the document that describes the architecture of web services (attached at the end of this document), one of the approaches introduced in Section~1.2 is *partitioning*, shown in Figure~1.4. The use of *partitioning*	76	0	0	WebPartioningINGLES
77	t	Considering again the case of the previous question, compare the architectures sketched in Figure~1.3 and Figure~1.4. The difference between the two shows	77	0	0	WebPartitioningDoisINGLES
78	t	Consider again the architecture shown in Figure~1.3, where redundancy was introduced into the system. In this particular case, introducing redundancy into the architecture has the goal of	78	0	0	WebRedundancyINGLES
79	t	The typical software architecture of an enterprise application is composed of three tiers and three layers. Yet, we may have variations of this architecture. For instance, by separating the middle tier in two tiers. In this case, which other changes exist on the architecture that are related with the layers?	79	0	0	ThreeVsFourTiersINGLES
81	t	The first architecture of the Fénix system, corresponding to its first years of development, could be described as a three-layered architecture, typical of an enterprise application. One of those layers was the *domain logic* layer. Which of the following sentences best describes the Fénix architecture in what concerns that layer?	81	0	0	DomainLogicFenixINGLES
1210	t	The detail that can be used in a view of the Data Model viewtype can be conceptual, logical or physical.	1210	0	0	DataModelOne
82	t	To achieve a faster time-to-market, software companies are increasingly using a strategy of incremental releases of their software, where each new release has a set of new features. Which architectural style is better to analyse whether the system's software architecture is adequate for the planned incremental releases?	82	0	0	IncrementalReleasesINGLES
86	t	The web page that describes the architecture of Chromium OS (an open source project to implement a new operating system) starts like this:  \n>"Chromium OS consists of three major components:  \n-  The Chromium-based browser and the window manager  \n-  System-level software and user-land services: the kernel, drivers, connection manager, and so on  \n-  Firmware  \n"  \n  \nConsidering this brief description of the software architecture of Chromium OS, which architectural style is more adequate to represent it?	86	0	0	ChromiumDecompositionINGLES
87	t	Suppose that, to satisfy a security requirement related with possible attacks coming from users that access your system through the Internet, you want to use the tactic named *Limit Exposure*. How does the use of that tactic manifests in the architectural views of your system?	87	0	0	SecurityINGLES
88	t	One of the best practices in the design of a software architecture is to create a skeleton system. What is its purpose?	88	0	0	SkeletonSystemINGLES
90	t	The Aspects style was introduced recently as a new style of the module viewtype. Using this style in the software architecture of a system	90	0	0	AspectsINGLES
91	t	According to the document that describes ZeroMQ:  \n>"One of the requirements for ØMQ was to take advantage of multi-core boxes; in other words, to scale the throughput linearly with the number of available CPU cores."  \n  \nTo satisfy this requirement, the solution adopted by ZeroMQ was	91	0	0	ZeroMQScaleMulticoreINGLES
92	t	Knowing that in the document describing ZeroMQ there is the following statement:  \n>"ØMQ is a library, not a messaging server."  \n  \nWhich views are needed to describe the software architecture of ZeroMQ?	92	0	0	ZeroMQAsLibraryINGLES
93	t	According to the document that describes ZeroMQ:  \n>"It took us several years working on AMQP protocol [...] to realise that there's something wrong with the classic client/server model of smart messaging server (broker) and dumb messaging clients."  \n  \nWhat is the main problem, according to the authors, of the *broker*-based model?	93	0	0	ZeroMQBrokerINGLES
94	t	According to the document that describes ZeroMQ:  \n>"The objects that handle data transfer are composed of two parts: the session object is responsible for interacting with the ØMQ socket, and the engine object is responsible for communication with the network. There's only one kind of the session object, but there's a different engine type for each underlying protocol ØMQ supports. Thus, we have TCP engines, IPC engines, PGM engines, etc. The set of engines is extensible---in the future we may choose to implement, say, a WebSocket engine or an SCTP engine."  \n  \nSupposing that the code implementing the *session object* does not need to be changed when a new type of *engine* is added to the system, which architectural views are better to show this extensibility aspect of the system?	94	0	0	ZeroMQExtensibleEnginesINGLES
95	t	According to the document that describes ZeroMQ:  \n>"In early versions of ØMQ the API was based on AMQP's model of exchanges and queues. I spent the end of 2009 rewriting it almost from scratch to use the BSD Socket API instead."  \n  \nWhich requirements were targeted by this change in the system?	95	0	0	ZeroMQBSDSocketsINGLES
96	t	According to the document that describes nginx:  \n>"nginx runs several processes in memory; there is a single master process and several worker processes. There are also a couple of special purpose processes, specifically a cache loader and cache manager. All processes are single-threaded in version 1.x of nginx. All processes primarily use shared-memory mechanisms for inter-process communication."  \n  \nAssuming that you want to highlight how the various nginx processes communicate among themselves, which architectural style is more adequate to represent the above information?	96	0	0	nginxProcessesINGLES
97	t	In the continuation of the description presented in the previous question, later in the document there is this passage:  \n>"Caching in nginx is implemented in the form of hierarchical data storage on a filesystem. Cache keys are configurable, and different request-specific parameters can be used to control what gets into the cache. Cache keys and cache metadata are stored in the shared memory segments, which the cache loader, cache manager and workers can access."  \n  \nWhich architectural style is more adequate to represent the use of cache in nginx?	97	0	0	nginxCachingINGLES
98	t	As mentioned in the previous questions, the use of *workers* is one of the crucial elements in the software architecture of nginx. Which of the following sentences best describes how *workers* work in nginx?	98	0	0	nginxWorkersINGLES
99	t	Given that a *worker* processes various requests during its life, how does it do it?	99	0	0	nginxWorkerParallelINGLES
100	t	The main *architectural driver* for the nginx system was to solve the *C10K problem*: being able to maintain 10.000 simultaneous connections with a single server running on conventional hardware. For this o happen, nginx must	100	0	0	nginxCTenKProblemINGLES
102	t	What was the main goal of the GHC's authors that led them to the design decision described in the previous question?	102	0	0	GHCBlockLayerQualitiesINGLES
103	t	Some of the *architectural drivers* of the Glasgow Haskell Compiler are related with the system's extensibility, and one of the solutions adopted by its authors to provide that extensibility was the introduction of *user-defined rewrite rules*, described in the document as follows:  \n>"The core of GHC is a long sequence of optimisation passes, each of which performs some semantics-preserving transformation, `Core` into `Core`. But the author of a library defines functions that often have some non-trivial, domain-specific transformations of their own, ones that cannot possibly be predicted by GHC. So GHC allows library authors to define rewrite rules that are used to rewrite the program during optimisation. In this way, programmers can, in effect, extend GHC with domain-specific optimisations."  \n  \nHow does this solution manifests in the software architecture of the system?	103	0	0	GHCRewriteRulesINGLES
104	t	According to the document that describes the Glasgow Haskell Compiler:  \n>"As the popularity of the Haskell language has grown, there has been an increasing need for tools and infrastructure that understand Haskell source code, and GHC of course contains a lot of the functionality necessary for building these tools: a Haskell parser, abstract syntax, type checker and so on. With this in mind, we made a simple change to GHC: rather than building GHC as a monolithic program, we build GHC as a library, that is then linked with a small Main module to make the GHC executable itself, but also shipped in library form so that users can call it from their own programs. At the same time we built an API to expose GHC's functionality to clients."  \n  \nWhich architectural diagram is more adequate to represent the information presented above?	104	0	0	GHCAsLibraryINGLES
105	t	According to the document that describes the Glasgow Haskell Compiler:  \n>"Once the `Core` program has been optimised, the process of code generation begins. The code generator first converts the `Core` into a language called `STG`, which is essentially just `Core` annotated with more information required by the code generator. Then, `STG` is translated to `Cmm`, a low-level imperative language with an explicit stack. From here, the code takes one of three routes:  \n-  Native code generation: [...]  \n-  LLVM code generation: [...]  \n-  C code generation: [...]  \n"  \n  \nThat is, GHC may use one of three alternative code generators, which have different qualities (omitted in the excerpt presented above). Supposing that you want to present an architectural diagram to represent the description presented above, which one seems more adequate?	105	0	0	GHCCodeGenerationINGLES
106	t	Consider the Figure~1.8 in the document that describes the use of caches in web services (see annex). In that Figure, there is a rectangle with the name *Cache* within another rectangle with the name *Request Node*. Taking into account the description made in the text and the goal of that Figure, those rectangles correspond to which type of software elements?	106	0	0	WebCacheModuleINGLES
107	t	Consider the change in the architecture introduced from Figure~1.9 to Figure~1.10 in the document that describes the use of caches in web services (see annex). That change has the goal and the consequence of, respectively	107	0	0	WebCacheGlobalINGLES
108	t	Consider the paragraph marked with the number 1 in the document that describes the use of caches in web services (see annex), where the concept of *distributed cache* is introduced. Which architectural style better represents the interaction pattern that exists among the various request nodes?	108	0	0	WebDistributedCacheINGLES
109	t	Consider the paragraph marked with the number 2 in the document that describes the use of caches in web services (see annex), where the failure of a node in the distributed cache is discussed. When that happens, what are the consequences for the system?	109	0	0	WebMissingCacheNodeINGLES
111	t	One of the major changes introduced in the software architecture of the Fénix system, compared to its first architecture, was	111	0	0	DomainLogicFenixINGLES
112	t	Several of the cases studied in this course had performance requirements. Which architectural views are typically needed to show that those requirements are satisfied?	112	0	0	PerformanceINGLES
136	t	Consider the following fragment of GNU Mailman case study:  \n*Mailman 3 has adopted the Representational State Transfer (REST) model for external administrative control. REST is based on HTTP, and Mailman's default object representation is JSON. These protocols are ubiquitous and well-supported in a large variety of programming languages and environments, making it fairly easy to integrate Mailman with third party systems. REST was the perfect fit for Mailman 3, and now much of its functionality is exposed through a REST API.*  \nThis solution allowed:	136	0	0	GMRestModularityEN
116	t	The email system is composed of various types of components playing different roles. For example, to send an email, a user uses a *mail user agent* (MUA), to compose his message and send it. To send the message, the MUA typically connects to a *mail transfer agent* (MTA) that receives the message, analyzes the message's headers to determine the recipients and, after querying the DNS system to determine the MTA responsible for each recipient, it connects to the MTAs responsible for the destination addresses to deliver the message. Each of these MTAs receives the message and stores it locally or forwards it to others MTAs until the message reaches its destination MTA. The recipient user of the message will then use his MUA to see the messages that were sent to him. To do it, the MUA connects to an IMAP or POP server to obtain the user's messages. Those IMAP and POP servers obtain the messages for a user by reading the messages stored by the MTA. Given this simplified description of the operation of the email system, which of the following architectural styles is more appropriate to represent the pattern of interaction between the MUA and the MTA?	116	0	0	ArqEmailMUAMTAINGLES
118	t	Suppose that you are developing a web application that keeps in a database some information that is introduced by the users and that one of the requirements is that the information should be kept confidential, such that no one but the author of the information should be able to see it (but the author may access that information whenever he wants it). How would you satisfy this requirement?	118	0	0	SecurityINGLES
119	t	Web applications went through several evolutions over the last years. One of those evolutions was to make their user interfaces more sophisticated, by leveraging on new technologies available in the browsers, such as, for example, Javascript, to provide a more satisfying user experience. What were the most visible consequences of such an evolution on the typical software architecture of a web application?	119	0	0	WebEvolutionINGLES
120	t	One of the terms often used to describe the software architecture of a system is the term *tier*, being common, for instance, to talk about *multi-tier* systems. Taking into account the various types of software elements that compose a software architecture, a *tier* is	120	0	0	TiersINGLES
122	t	In the Graphite system the component *carbon-relay* implements a tactic	122	0	0	GPCarbonRelayINGLES
124	t	Which are the most significant qualities of the MediaWiki system?	124	0	0	MWQualitiesINGLES
125	t	The architectural styles which are more suitable to describe the MediaWiki system from the end user viewpoint are	125	0	0	MWArchitecuralStyleINGLES
126	t	The MediaWiki system tries to enforce a reliability criteria that all the changes done by a writer are consistently visualized in her subsequent reads	126	0	0	MWReliabilityTacticsINGLES
127	t	The MediaWiki system tries to guarantee a reliability criteria where all information is available to be read by any reader in less than 30 seconds after being written. To achieve this criteria the load balancer	127	0	0	MWReliabilityReadsImplementationINGLES
128	t	Consider the following fragment of the MediaWiki system description:  \n*To optimize the delivery of JavaScript and CSS assets, the ResourceLoader module was developed to optimize delivery of JS and CSS. Started in 2009, it was completed in 2011 and has been a core feature of MediaWiki since version 1.17. ResourceLoader works by loading JS and CSS assets on demand, thus reducing loading and parsing time when features are unused, for example by older browsers. It also minifies the code, groups resources to save requests, and can embed images as data URIs.*  \nThe *ResourceLoader* implements a tactic	128	0	0	MWResourceLoaderTacticINGLES
129	t	In Chrome, to accomplish the security quality, the Browser Process implements a tactic	129	0	0	CHSecurityQualityINGLES
130	t	In Chrome it is possible to associate a Renderer Process to each Tab, which results in the increase of performance due to a tactic of	130	0	0	CHPerformanceQualityINGLES
131	t	In the description of the Chrome case you can read:  \n*On Android devices, Chrome leverages the same multi-process architecture as the desktop version - there is a browser process, and one or more renderer processes. The one difference is that due to memory constraints of the mobile device, Chrome may not be able to run a dedicated renderer for each open tab. Instead, Chrome determines the optimal number of renderer processes based on available memory, and other constraints of the device, and shares the renderer process between the multiple tabs.*  \nThis description can be represented by a view of viewtype Component-and-Connector using the architectural style	131	0	0	CHMobilityArchitecturalStyleINGLES
132	t	An advantage of Chrome when compared with Amazon Silk is	132	0	0	CHAmazonSilkTwoEN
133	t	One of the qualities of Chrome is the execution of the JavaScript code inside a process, which allows the isolation against possible interferences between the execution of JavaScript programs that are loaded from different sites. The isolation level	133	0	0	CHSecurityLevelEN
134	t	In the description of the Chrome case study you can read:  \n*Typing in the Omnibox (URL) bar triggers high-likelihood suggestions, which may similarly kick off a DNS lookup, TCP pre-connect, and even prerender the page in a hidden tab.*  \nThis description refers to	134	0	0	CHOmniboxTacticsEN
135	t	Consider the following fragment of GNU Mailman  \n*In Mailman 2, the MailList object's state is stored in a file called config.pck, which is just the pickled representation of the MailList object's dictionary. Every Python object has an attribute dictionary called __dict__. So saving a mailing list object then is simply a matter of pickling its __dict__ to a file, and loading it just involves reading the pickle from the file and reconstituting its __dict__.*  \nAlthough simple, this solution resulted in several problems which had a negative impact on performance. This is due to:	135	0	0	GMPicklePerformanceEN
137	t	Consider the following fragment of GNU Mailman case study:  \n*Once a message has made its way through the chains and rules and is accepted for posting, the message must be further processed before it can be delivered to the final recipients. For example, some headers may get added or deleted, and some messages may get some extra decorations that provide important disclaimers or information, such as how to leave the mailing list.*  \nThe Pipes-and-Filters architectural style is used in the handling of messages. In this context the data type which is sent among the filters is	137	0	0	GMPipesFiltersDataEN
138	t	In the description of the GNU Mailman case study it is proposed a solution that, when there are several queue runners executing on the same queue, the delivery of messages is done according to arrival order (FIFO).  \n*There's another side effect of this algorithm that did cause problems during the early design of this system. Despite the unpredictability of email delivery in general, the best user experience is provided by processing the queue files in FIFO order, so that replies to a mailing list get sent out in roughly chronological order.*  \nThe proposed solution	138	0	0	GMReliabilityFIFOEN
139	t	The function of Master Runner component of GNU Mailman can be represented using an architecture style of	139	0	0	GMMasterRunnerEN
140	t	In Mailman 3 messages are still being persistently stored using pickle because	140	0	0	GMMessagesPersistenceEN
141	t	The Install and Implementation architectural styles	141	0	0	InstallImplementationStylesEN
142	t	The architecturally significant qualities of the second Fénix architecture are:	142	0	0	FenixTwoEN
143	t	The elasticity of a system, defined as its capability to easily adapt to load changes, is often represented as a required property of the scalability quality. For this level of easiness contribute the architectural solutions associated with the following tactic(s)	143	0	0	ElasticityDeferBindingEN
144	t	The Unit of Work pattern can be implemented in an application server, while it is still necessary to use transactions in the repository to access to the data. In this situation	144	0	0	UnitOfWorkEN
146	t	When the source of an attack is internal to an organization the tactics which are more efective are	146	0	0	SecurityInternalSourceEN
148	t	In the Publish-Subscribe architectural style, the components, from the point of view of the modules they execute	148	0	0	PublishSubscribeEN
149	t	Some usability qualities are not architectural because	149	0	0	UsabilityNonArchitecturalEN
151	t	In the Graphite system the component *carbon* provides to *webapp* components an access interface to the *buffers* in order to improve the quality(ies) of	151	0	0	GPCarbonBufferInterfaceEN
154	t	The design of the MediaWiki architecture was constrained the requirement that the solution should have relatively low cost. Due to this restriction it was taken the architectural decision of	154	0	0	MWLowCostEN
155	t	Consider a Component-and-Connector architectural view of the MediaWiki system where all the clients are connected to a server through a request-reply connector. This connector implements the tactics	155	0	0	MWTacticsEN
156	t	The MediaWiki system tries to enforce a reliability criteria that all the changes done by a writer are consistently visualized in her subsequent reads. This criteria is implemented	156	0	0	MWReliabilityImplementationEN
157	t	The MediaWiki system tries to guarantee a reliability criteria where all information is available to be read by any reader in less than 30 seconds after being written. To achieve this criteria it is implemented a tactic of	157	0	0	MWReliabilityReadsTacticEN
158	t	In the description of MediaWiki system we can read:  \n*The first revision of the blob is stored in full, and following revisions to the same page are stored as diffs relative to the previous revision; the blobs are then gzipped. Because the revisions are grouped per page, they tend to be similar, so the diffs are relatively small and gzip works well. The compression ratio achieved on Wikimedia sites nears 98%.*  \nThis description refers to a tactic of	158	0	0	MWVerBlobTacticEN
159	t	Chrome, as described in the case study, was designed to support the accomplish the following architectural qualities:	159	0	0	CHQualitiesEN
161	t	An advantage of Amazon Silk when compared with Chrome is	161	0	0	CHAmazonSilkEN
162	t	An architectural view of the Component-and-Connector viewtype that describes the interactions within the Renderer Process component of Chrome, uses the architectural style	162	0	0	CHRenderStyleEN
163	t	In some situations Chrome prerenders a page. To do it	163	0	0	CHPrerenderTacticsEN
165	t	Consider the following fragment of GNU Mailman case study:  \n*Mailman 3 has adopted the Representational State Transfer (REST) model for external administrative control. REST is based on HTTP, and Mailman's default object representation is JSON. These protocols are ubiquitous and well-supported in a large variety of programming languages and environments, making it fairly easy to integrate Mailman with third party systems. REST was the perfect fit for Mailman 3, and now much of its functionality is exposed through a REST API.*  \nThis solution allowed increased interoperability because	165	0	0	GMRestInteroperabilityEN
166	t	Consider the following fragment of GNU Mailman case study:  \n*Once a message has made its way through the chains and rules and is accepted for posting, the message must be further processed before it can be delivered to the final recipients. For example, some headers may get added or deleted, and some messages may get some extra decorations that provide important disclaimers or information, such as how to leave the mailing list.*  \nThe architectural style that is more accurate to describe the flexible processing of messages is	166	0	0	GMPipesFiltersEN
167	t	Consider the following fragment of GNU Mailman case study:  \n*Email messages can act as containers for other types of data, as defined in the various MIME standards. A container message part can encode an image, some audio, or just about any type of binary or text data, including other container parts.*  \nThe architectural style that is more accurate to describe this transcription is	167	0	0	GMDataModelEN
168	t	Consider the following transcription of the GNU Mailman system:  \n*...Mailman supports running more than one runner process per queue directory...*  \nIt has the goal to support	168	0	0	GMPerformanceEN
169	t	Consider the following description of the GNU Mailman system:  \n*VERP stands for Variable Envelope Return Path, and it is a well-known technique that mailing lists use to unambiguously determine bouncing recipient addresses. When an address on a mailing list is no longer active, the recipient's mail server will send a notification back to the sender. In the case of a mailing list, you want this bounce to go back to the mailing list, not to the original author of the message; the author can't do anything about the bounce, and worse, sending the bounce back to the author can leak information about who is subscribed to the mailing list. When the mailing list gets the bounce, however, it can do something useful, such as disable the bouncing address or remove it from the list's membership.*  \nThis transcription describes the quality(ies) of	169	0	0	GMReliabilityBounceEN
170	t	Consider the following description of the GNU Mailman system:  \n*There is a core Mailman class called Switchboard which provides an interface for enqueuing (i.e., writing) and dequeuing (i.e., reading) the message object tree and metadata dictionary to files in a specific queue directory. Every queue directory has at least one switchboard instance, and every queue runner instance has exactly one switchboard.*  \nThis transcription contains relevant information for viewtypes of	170	0	0	GMSwitchboardEN
171	t	The architecturally significant requirements of the third architecture of Fénix are	171	0	0	FenixThreeEN
172	t	In the Fénix first architecture it was common programmers forget to lock objects in the context of transactions. A solution for this problem can be architecturally described using a view of the architectural style	172	0	0	FenixOneEN
173	t	The internationalization of the user interface is supported by the tactic(s)	173	0	0	InternationalizationTacticsEN
174	t	To implement the Identity Map pattern	174	0	0	IdentityMapEN
175	t	In defensive programming the programmer checks that the conditions under which modules are invoked comply with their specification, and if they don't an exception is raised to avoid failure propagation. When defensive programming is followed, in the context of availability quality, we are using a tactic of	175	0	0	AvailabilityDefensiveEN
176	t	In the Observer design pattern, where the model invokes a notification method on all its observers whenever it is changed, can be said, in what concerns the Uses relation of the Uses architectural style, that	176	0	0	ObserverUsesEN
177	t	Consider the Uses and Layered architectural styles.	177	0	0	UsesLayersEN
178	t	An architectural view	178	0	0	SeveralStylesViewEN
179	t	The Uses architectural style	179	0	0	ApplyUsesEN
180	t	In a enterprise-wide system, like Fénix system,	180	0	0	EnterpriseWideEN
211	t	The Service Layer pattern is typically used in conjunction with	211	0	0	ServiceLayer
212	t	The Active Record pattern is best used when we are also using	212	0	0	ActiveRecord
214	t	Consider the architectural views for the ThousandParsec system. In the case description can be read:  \n>"The Requirements function verifies that each component added to the design conforms to the rules of other previously added components."  \n  \nThe following diagram depicts a fragment of a proposal for the decomposition view of the system.   \n![image][image]  \n	214	0	0	ThounsandParsecView
215	t	Checksum is a technic that it is often used in architectural design. It can be used as	215	0	0	Checksum
216	t	An attack is	216	0	0	Attack
218	t	In the description of the Thousand Parsec case study can be read:  \n>"A ruleset designer thus has the ability to create new object types or store additional information in the existing object types as required by the ruleset, allowing for virtually unlimited extensibility in terms of the available physical objects in the game."  \n  \nThis excerpt can be represented as a modifiability scenario where	218	0	0	ThousandParsecTactics
219	t	In the description of the Git case study can be read:  \n>"Git tackles the storage space problem by packing objects in a compressed format, using an index file which points to offsets to locate specific objects in the corresponding packed file."  \n  \nThe tactic addressed in this fragments is:	219	0	0	GitTactics
220	t	Assume that one of the requirements for a graphical chess game is that it should be able to run both in Microsoft's Windows and Apple's Mac OS X operating systems. A good solution for this system would:	220	0	0	Layered
222	t	A requirement for a chess game is that it keeps a table with the best scores obtained in the game. Naturally, this information should be kept between two different executions of the system. Assuming that the game is a web-based application, then	222	0	0	Repository
223	t	An email client such as Mozilla's Thunderbird or Microsoft's Outlook allows a user both to read the emails that were sent to him and to send new emails to other people. To do that, the email client connects to other components (one or more): some of these components keep the user's mailboxes with all the emails that were sent to him, whereas other components know how to forward the emails sent by the user to their final destinations (associated with a new set of destinations). In either case, it is always the email client that makes a request to the other components, but whereas in the first case the email client receives all the information about the user's emails, in the second case only a success or failure error code is returned. The architectural patterns that best describe the interactions between the components from the client to the final destinations	223	0	0	PeerToPeer
224	t	The software architecture of a system	224	0	0	ArchitectureInfluenceCycle
225	t	Frank Buschmann, *Introducing the Pragmatic Architect*, defines the *techno-geeks* architects. This kind of architect	225	0	0	TechoGeeks
227	t	Consider the following scenario  \n>"Our vehicle information system send our current location to the traffic monitoring system. The traffic monitoring system combines our location with other information, overlays this information on a Google Map, and broadcasts it. Our location information is correctly included with a probability of 99.99%."  \n  \n	227	0	0	Scenario
229	t	Consider the architectural views for the SocialCalc system. In the case description can be read:  \n>"The save format is in standard MIME multipart/mixed format, consisting of four text/plain; charset=UTF-8 parts, each part containing  \n-delimited text with colon-delimited data fields. The parts are... This format is designed to be human-readable, as well as being relatively easy to generate programmatically. This makes it possible for Drupal's Sheetnode plugin to use PHP to convert between this format and other popular spreadsheet formats, such as Excel (.xls) and OpenDocument (.ods)."  \n  \nFrom the above excerpt can be inferred the need to have	229	0	0	SocialCalcView
230	t	The architectural style that best represents the runtime execution of a system Git installed for a small group of developers is	230	0	0	GitViews
231	t	In the OrderPad system they have decided to use a Row Data Gateway data access pattern because	231	0	0	OrderPad
232	t	Consider the architectural views of EtherCalc system. In the case study description can be read  \n>"The Socialtext platform has both behind-the-firewall and on-the-cloud deployment options, imposing unique constraints on EtherCalc's resource and performance requirements."  \n  \n	232	0	0	EtherCalcAllocation
233	t	In EtherCalc initial prototype clients send their local commands and snapshots to the server, which result on redundant information on the server about the state of the spreadsheet. This redundancy is an application of	233	0	0	EtherCalcRedundancy
234	t	In EtherCalc initial prototype clients send their local commands, cursor movements and snapshots to the server.	234	0	0	EtherCalcSnapshotPerformance
235	t	In the EtherCalc case description can be read  \n>"The in-browser SocialCalc engine is written in JavaScript. We considered translating that logic into Perl, but that would have carried the steep cost of maintaining two code bases."  \n  \nThe excerpt is referring to a quality of	235	0	0	EtherCalcModifiabilityTestability
638	t	Consider the Infinispan system when it is configured as a remote data grid. The relation between the Applications and the Grid is	638	0	0	InfinispanOne
272	t	Compared to the Transaction Script pattern, the Domain Logic pattern has a higher initial cost of adoption. That is, it is harder to start with the Domain Logic pattern than with the Transaction Script pattern. The reason for this is that the Domain Logic pattern	272	0	0	TransactionScript
273	t	Ruby on Rails is a popular full-stack framework for building web applications. One of the elements of this framework is the **model**, which is described in the Rails documentation in the following way:  \n>"A model represents the information (data) of the application and the rules to manipulate that data. In the case of Rails, models are primarily used for managing the rules of interaction with a corresponding database table. In most cases, one table in your database will correspond to one model in your application. The bulk of your application's business logic will be concentrated in the models."  \n  \nGiven this description, the Rails' model is best described as an instance of	273	0	0	ActiveRecordRuby
275	t	Ping-and-echo and Heartbeat are two availability tactics to detect faults.	275	0	0	AvailabilityPingEchoHeartbeat
276	t	Consider that when designing the architecture of a web application, the architect intends to guarantee of the confidentiality of persistent data in face of an attack from a system administrator.	276	0	0	SecurityDatabase
278	t	In the description of the ThousandParsec case study can be read:  \n>"The Thousand Parsec Component Language (TPCL) exists to allow clients to create designs locally without server interaction - allowing for instant feedback about the properties, makeup, and validity of the designs."  \n  \nFrom this sentence can be written	278	0	0	ThousandParsecScenario
279	t	In the description of GitHub case study can be read  \n>"Once the Smoke proxy has determined the user's route, it establishes a transparent proxy to the proper file server. We have four pairs of fileservers. Their names are fs1a, fs1b, ..., fs4a, fs4b. These are 8 core, 16GB RAM bare metal servers, each with six 300GB 15K RPM SAS drives arranged in RAID 10. At any given time one server in each pair is active and the other is waiting to take over should there be a fatal failure in the master. All repository data is constantly replicated from the master to the slave via DRBD."  \n  \nIn this description we can find the application of tactics like	279	0	0	GitTactic
280	t	Views of the module viewtype can be used to support requirements traceability analysis, determine how the functional requirements of a system are supported. This is represented by	280	0	0	ModuleTraceability
281	t	Assuming that you were asked to document the software architecture of an existing (and already developed) system, the best thing for you to do would be	281	0	0	ArchitectureKnowledge
282	t	Ralph Johnson says that  \n>"Architecture is the decisions that you wish you could get right early in a project."  \n  \nThis sentence reflects the fact that	282	0	0	ArchitectureEvolution
283	t	Marquardt characterizes performitis as:  \n>"Each part of the system is directly influenced by local performance tuning measures. There is no global performance strategy, or it ignores other qualities of the system such as testability and maintainability."  \n  \nThis means that	283	0	0	Performitis
284	t	The software architecture of a system is usually represented through several views because we need to	284	0	0	ArchitecturalViews
285	t	According to the attribute-driven design process, we should design the software architecture for a system based on a selected list of requirements, which are called the *architecturally significant requirements*. These architecturally significant requirements should be sorted according to their importance for the system's stakeholders because	285	0	0	ArchitecturallySignificantRequirements
286	t	On the web page of Memcached can be read:  \n>"..., high-performance, distributed memory object caching system, generic in nature, but intended for use in speeding up dynamic web applications by alleviating database load."  \n  \nAccording to this information, Memcached is	286	0	0	ModueComponent
287	t	You have to develop an application that collects news from different web sources and process that information to present a digest to the application users. The different sources provide similar information through different interfaces (APIs). Additionally, the new sources may change the interfaces, for instance to enhance their service. Which architectural style can be used to represent this requirements?	287	0	0	GeneralizationInterfaces
288	t	When designing the architecture for a system the architect realises that most of the modules have bidirectional uses relationships. This has impact on	288	0	0	UsesIncremental
289	t	In the description of EtherCalc case study can be read  \n>"Because all jsdom code runs in a single thread, subsequent REST API calls are blocked until the previous command's rendering completes. Under high concurrency, this queue eventually triggered a latent bug that ultimately resulted in server lock-up."  \n  \nThe above sentence is related to a quality for	289	0	0	EtherCalcPerformance
290	t	In the description of EtherCalc case study can be read  \n>"So, we removed jsdom from the RenderSheet function, re-implemented a minimal DOM in 20 lines of LiveScript for HTML export, then ran the profiler again. Much better! We have improved throughput by a factor of 4, HTML exporting is 20 times faster, and the lock-up problem is gone."  \n  \nThe above sentence describes a	290	0	0	EtherCalcTactic
291	t	In the description of EtherCalc case study can be read how the architect tried to increase the performance in a multi-core context  \n>"Is there a way to make use of all those spare CPUs in the multi-tenant server? For other Node.js services running on multi-core hosts, we utilized a pre-forking cluster server that creates a process for each CPU. However, while EtherCalc does support multi-server scaling with Redis, the interplay of Socket.io clustering with RedisStore in a single server would have massively complicated the logic, making debugging much more difficult."  \n  \nThis possible solution has impact on the	291	0	0	EtherCalcTestability
293	t	In the description of EtherCalc case study can be read how the architect increased the performance in a multi-core context  \n>"Instead of pre-forking a fixed number of processes, we sought a way to create one background thread for each server-side spreadsheet, thereby distributing the work of command execution among all CPU cores."  \n  \nWhich is represented by the diagram  \n  \n![image][image]  \n The above diagram, describing a server spreadsheet, can be represented using	293	0	0	EtherCalcViews
294	t	To increase the availability of a web application it is possible to use a load-balancer between the clients and the servers that detects server failures and transparently redirects the requests to the servers that are functioning properly. To represent this architecture	294	0	0	LoadBalancer
295	t	One way to increase the performance of a 3-tier enterprise application (with the standard separation in the web client, web server, and database tiers) is to replicate the web server tier and to add a load-balancer between the web clients and the web servers. Unfortunately, for some enterprise applications that option is not enough (or does not work at all), because	295	0	0	ThreeTiers
298	t	In the description of ThousandParsec case study can be read  \n>"The flagship server, tpserver-cpp, provides an abstract persistence interface and a modular plugin system to allow for various database back ends."  \n  \nThis above sentence can be diagrammatically represented using	298	0	0	ThousandParsecPersistence
299	t	Consider the (partial) component-and-connector view for the :SpreasdSheet component of the SocialCalc system   \n![image][image]  \n The sub1 port	299	0	0	SocialCalcBroadcastEvents
300	t	In the description of GitHub case study can be read  \n>"For requests to the main website, the load balancer ships your request off to one of the four frontend machines. Each of these is an 8 core, 16GB RAM bare metal server. Their names are fe1, ..., fe4. Nginx accepts the connection and sends it to a Unix domain socket upon which sixteen Unicorn worker processes are selecting. One of these workers grabs the request and runs the Rails code necessary to fulfill it."  \n  \nTo represent the above description it is necessary to use	300	0	0	GitHubViews
319	t	The quality(ies) that is(are) more relevant to views of the component-and-connector viewtype is(are):	319	0	0	ComponentViewType
320	t	The *Ensuring that the implementation conforms to the architecture* step of how to create an architecture	320	0	0	CreateArchitectureTwo
321	t	Consider the following scenario  \n>"When writing to the database the system receives an exception about a write failure. The system should stop interacting with data base and write a log message."  \n  \nThe quality addressed by this scenario is	321	0	0	AvailabilityScenario
322	t	A heartbeat monitor	322	0	0	PingEcho
323	t	Human-editable URL API for creating graphs is a usability design tactic used in the Graphite system. This tactic	323	0	0	GraphiteTechnicaAndNonTechnicalUsers
324	t	Having a single point of access to an intranet is a security tactic of	324	0	0	Firewall
326	t	In a quality scenario	326	0	0	Scenario
328	t	In the Graphite system description can be read:  \n>"We've got 600,000 metrics that update every minute and we're assuming our storage can only keep up with 60,000 write operations per minute. This means we will have approximately 10 minutes worth of data sitting in carbon's queues at any given time. To a user this means that the graphs they request from the Graphite webapp will be missing the most recent 10 minutes of data."  \n  \n	328	0	0	GraphiteReliability
329	t	In the Fenix system a checksum is associated to a set of grades. This is an application of the tactic	329	0	0	VerifyMessageIntegrity
330	t	In the Chrome system the following tactic is used to improve performance	330	0	0	ChromePerformance
331	t	Consider the following scenario  \n>"Our vehicle information system send our current location to the traffic monitoring system. The traffic monitoring system combines our location with other information, overlays this information on a Google Map, and broadcasts it. Our location information is correctly included with a probability of 99.99%."  \n  \nThe quality addressed by this scenario is	331	0	0	InteroperabilityScenario
332	t	In wikipedia you can find the following definition:  \n>"The garbage collector, or just collector, attempts to reclaim garbage, or memory occupied by objects that are no longer in use by the program."  \n  \nThe garbage collector is a component that implements an availability tactic of	332	0	0	GarbageCollector
333	t	To reduce the backend load (writes) the Graphite system uses	333	0	0	GraphiteBackend
334	t	In a system where there are sensitive data an appropriate tactic to be used is	334	0	0	SeparateEntities
335	t	In the description of the Chrome system can be read  \n>"As the user types, the Omnibox automatically proposes an action, which is either a URL based on your navigation history, or a search query."  \n  \nThe above sentence refers to	335	0	0	ChromeUsability
336	t	An architectural tactic	336	0	0	Tactics
337	t	Consider a enterprise web system, which provides services both on the company's intranet and to the company's clients on the internet, that when under a denial of service attack decides to stop providing internet services.	337	0	0	Degradation
338	t	In the Graphite system description can be read:  \n>"Making multiple Graphite servers appear to be a single system from a user perspective isn't terribly difficult, at least for a naive implementation."  \n  \n	338	0	0	GraphiteModifiability
339	t	In a system where the source of attacks can be internal, from authorized users, the appropriate tactics to be used are	339	0	0	InternalAttack
343	t	In the description of the SocialCalc case study can be read:  \n>"Even with race conditions resolved, it is still suboptimal to accidentally overwrite the cell another user is currently editing. A simple improvement is for each client to broadcast its cursor position to other users, so everyone can see which cells are being worked on."  \n  \nFrom this fragment can be identified a scenario for	343	0	0	SocialCalcUsability
344	t	In the description of the Thousand Parsec case study can be read:  \n>"Finding a public Thousand Parsec server to play on is much like locating a lone stealth scout in deep space - a daunting prospect if one doesn't know where to look. Fortunately, public servers can announce themselves to a metaserver, whose location, as a central hub, should ideally be well-known to players."  \n  \nFrom this fragment can be identified a scenario for	344	0	0	ThounsandParsecInteroperability
346	t	In the context of the FenixEdu case study the following scenario was identified.  \n>"The school management pretends that all the members of the school, students, administrative staff, faculty and management should be able to use the system to perform their activities efficiently without requiring the installation of any client software or a long learning process."  \n  \nThis is a	346	0	0	BusinessScenarioOne
348	t	In the description of the SocialCalc case study can be read:  \n>"To make this work across browsers and operating systems, we use the Web::Hippie4 framework, a high-level abstraction of JSON-over-WebSocket with convenient jQuery bindings."  \n  \nFrom this fragment can be identified a scenario for	348	0	0	SocialCalcModifiability
349	t	In the description of the Thousand Parsec case study can be read:  \n>"Next, the player is prompted to configure options for the ruleset and server, with sane defaults pulled from the metadata. Finally, if any compatible AI clients are installed, the player is prompted to configure one or more of them to play against."  \n  \nThe tactic referred in the fragments is	349	0	0	ThounsandParsecSystemInitiative
350	t	A criteria for the the application of the Decomposition architectural style of the Module viewtype is Build-vs-Buy decisions. The application of the criteria	350	0	0	DecompositionBuilvsBuy
351	t	A utility tree	351	0	0	UtilityTree
352	t	Consider an architecturally significant requirement (ASR) that has a high impact on the architecture but a low business value	352	0	0	HighBusinessValue
353	t	In the description of the SocialCalc case study can be read:  \n>"If users A and B simultaneously perform an operation affecting the same cells, then receive and execute commands broadcast from the other user, they will end up in different states."  \n  \nFrom this fragment can be identified a scenario for	353	0	0	SocialCalcAvailability
354	t	In the description of the Thousand Parsec case study can be read:  \n>"Turns also have a time limit imposed by the server, so that slow or unresponsive players cannot hold up a game."  \n  \nFrom this fragment can be identified a scenario for	354	0	0	ThounsandParsecAvailability
356	t	In the context of the FenixEdu case study the following scenario was identified.  \n>"The management intends that the system should be available to all users, even after offices close and classes finish because students may need courses material to study 24X7 and faculty and administrative staff may want to work from home."  \n  \nThis is a	356	0	0	BusinessScenarioTwo
359	t	In the description of the Thousand Parsec case study can be read:  \n>"Besides often running far longer than the circadian rhythms of the players' species, during this extended period the server process might be prematurely terminated for any number of reasons. To allow players to pick up a game where they left off, Thousand Parsec servers provide persistence by storing the entire state of the universe (or even multiple universes) in a database."  \n  \nThe tactic referred in the fragments is	359	0	0	ThounsandParsecRollback
361	t	Consider a view of the module viewtype where there is a uses loop, a cycle of uses dependences between several modules. It may be possible to break the dependence cycle by	361	0	0	UsesCycles
363	t	In the description of the GitHub case study can be read:  \n>"Of course, allowing arbitrary execution of commands is unsafe, so SSH includes the ability to restrict what commands can be executed. In a very simple case, you can restrict execution to git-shell which is included with Git. All this script does is check the command that you're trying to execute and ensure that it's one of git upload-pack, git receive-pack, or git upload-archive."  \n  \nThe tactic addressed in this fragments is:	363	0	0	GitHubSecurity
364	t	A connector may be attached to components of different types because	364	0	0	ConnectorAttach
367	t	In Facebook it is not possible to have the information about more that one bilion users in a single disk. Therefore, a sharding technique is applied, where the persistent information is split between several database servers, and applications are routed to the right servers for queries and updates. To describe this architecture	367	0	0	DataModelFacebook
368	t	In the description of the Git case study can be read how it efficiently compares content:  \n>"When a content (i.e., file or directory) node in the graph has the same reference identity (the SHA in Git) as that in a different commit, the two nodes are guaranteed to contain the same content, allowing Git to short-circuit content diffing efficiently."  \n  \nThe performance tactic addressed in this fragments is:	368	0	0	GitIncreaseResourceEfficiency
369	t	Consider an architect that is designing a component-and-connector view. In some point the architect decides that she does not need to decompose a connector with a demanding quality level. This may occur because	369	0	0	ConnectorDecomposition
370	t	In the client-server architectural style the request/reply connector is synchronous. Consider an architect that wants to describe an asynchronous interaction between clients and servers.	370	0	0	ClientServerSynchronous
371	t	The Uses architectural style of the Module viewtype	371	0	0	UsesFor
372	t	A CRUD matrix, which indicates whether each module creates, reads, updates, or deletes data (CRUD, for short) from each data entity. The CRUD matrix	372	0	0	UsesDataModel
373	t	In the description of the GitHub case study can be read:  \n>"Once the Smoke proxy has determined the user's route, it establishes a transparent proxy to the proper file server. We have four pairs of file servers. Their names are fs1a, fs1b, ..., fs4a, fs4b. These are 8 core, 16GB RAM bare metal servers, each with six 300GB 15K RPM SAS drives arranged in RAID 10. At any given time one server in each pair is active and the other is waiting to take over should there be a fatal failure in the master. All repository data is constantly replicated from the master to the slave via DRBD."  \n  \nThe four pairs of file servers implement:	373	0	0	GitHubComputationRedundancy
374	t	Consider the concepts of module interface and component port.	374	0	0	ComponentPorts
375	t	The client-server architectural style provides availability because	375	0	0	ClientServerAvailability
377	t	In the description of the Git case study can be read how to deal with the corruption of pack files in the context of the availability quality:  \n>"If an object was only copied partially or another form of data corruption occurred, recalculating the SHA of the current object will identify such corruption."  \n  \nThe tactic addressed in this fragments is:	377	0	0	GitConditionMonitoring
378	t	The repository architectural style provides modifiability because	378	0	0	RepositoryModifiability
380	t	According to the definition of the Layered architectural style, each layer represents a grouping of modules that offers a cohesive set of services.	380	0	0	LayeredVirtualMachine
382	t	The Service-Oriented Architecture style	382	0	0	SOAClientServerPeertoPeer
383	t	Consider the following application-specific types that were defined for a component-and-connector view that depicts the components within `Carbon` component.   \n![image][image]  \n	383	0	0	GraphiteCarbon
387	t	The Service-Oriented Architecture style improves modifiability because	387	0	0	SOAQualities
388	t	Consider the following application-specific types. Note that `Queue` components are within the `Carbon` components. In a view that contains components of these three types   \n![image][image]  \n	388	0	0	GraphiteDataPointSocket
389	t	An architecture can also be represented by the set of files which contains its modules code. A suitable architectural style to represent this set of files is	389	0	0	ImplementationStyle
391	t	The Tiers architectural style	391	0	0	Tiers
393	t	An important stage of the development of any system is its build into the set of executable files. A suitable architectural style which helps on the definition of the build process is	393	0	0	InstallStyle
395	t	Consider the following decomposition view of the Graphite system where module Store Graphs is responsible for managing the storage of datapoints and graphs and module Present Graphs for graphs generation and presentation. Memcache is a library that maintains datapoints in memory to reduce the overhead of obtaining them from the file system.   \n![image][image]  \n	395	0	0	GraphiteDecompositionMemcached
398	t	Consider the following decomposition view of the Graphite system where module Store Graphs is responsible for managing the storage of datapoints and graphs and module Present Graphs for graphs generation and presentation. Buffering is a library used to temporarily store incoming data point.   \n![image][image]  \n	398	0	0	GraphiteDecompositionBuffering
399	t	An architect needs to show that a security tactic of limit exposure will be effectively provided by the executing system. Therefore, she decides to design	399	0	0	DeploymentStyleLimitExposure
414	t	Consider the architectural views for the SocialCalc system. The following diagram depicts a proposal for a component-and-connector view of the system. According to this representation   \n![image][image]  \n	414	0	0	SocialCalcServer
417	t	Consider the architectural views for the ThousandParsec system. The following diagram depicts a fragment of a proposal for the decomposition view of the system. The AI players should be described   \n![image][image]  \n	417	0	0	ThousandParsecAI
418	t	Consider the architecture of the Morrison's OrderPad. The final interaction between the OrderPadDatabase component and Mainframe component is supported by	418	0	0	OrderPadMainframeConnector
419	t	Consider the architectural views for the SocialCalc system. The following diagram depicts a proposal for a component-and-connector view of the client Spreadsheet. A Parser module is used when loading a file   \n![image][image]  \n	419	0	0	SocialCalcParser
420	t	When the domain logic is organized using a Domain Model pattern the most suitable data source patterns are	420	0	0	LogicAccessDomainModel
451	t	In the Continous integration case study can be read about Jenkins  \n>"It takes advantage of the JUnit XML standard for unit test and code coverage reporting to integrate reports from a variety of test tools. Jenkins originated with Sun, but is very widely used and has a robust open-source community associated with it."  \n  \nConsider that a scenario is written from the above sentence	451	0	0	ContinousIntegrationScenariosTacticsOne
452	t	In the Infinispan case study can be read  \n>"When persisting for durability, persistence can either be online, where the application thread is blocked until data is safely written to disk, or offline, where data is flushed to disk periodically and asynchronously. In the latter case, the application thread is not blocked on the process of persistence, in exchange for uncertainty as to whether the data was successfully persisted to disk at all."  \n  \nFrom the description we can infer a trade-off between the qualities of	452	0	0	InfinispanScenariosTacticsOne
453	t	With the evolution of the web application technologies, it is now possible to develop web applications with a user interface similar to the interface of desktop applications. Yet, for this to happen, part of the code that was executing in the web server is now executing in the web browser. How does this change manifests in the software architecture of the system?	453	0	0	WebTwoOne
454	t	Consider the following figure depicting two different architectures for web applications   \n![image][image]  \n	454	0	0	MicroservicesArchitectureOne
639	t	Consider the following representation of the Buildbot system.   \n![image][image]  \n The architecture style between the Buildbot Master and the Clients is:	639	0	0	JenkinsOne
455	t	Consider the following excerpt about the Amazon system  \n>"Over time, this grew into hundreds of services and a number of application servers that aggregate the information from the services. The application that renders the Amazon.com Web pages is one such application server, but so are the applications that serve the Web-services interface, the customer service application, the seller interface, and the many third-party Web sites that run on our platform."  \n  \nThe architectural style that better represents these aspects of the Amazon architecture is	455	0	0	AmazonOne
456	t	According to the attribute-driven design process, we should design the software architecture for a system based on a selected list of requirements, which are called the *architecture significant requirements*. These requirements should be sorted according to their importance for the system's stakeholders because	456	0	0	DesigningArchitectureOne
460	t	There are several tactics to satisfy availability requirements, which may be applied depending on the concrete requirement that we want to satisfy. Assuming that you want to deal with faults of type *omission* in your system, which tactic is more adequate?	460	0	0	AvailabilityOne
461	t	Consider that an architect needs to design a system which interacts with two external sources of information, and it has to import some of the information to store it in the system's internal database. The stakeholders inform him that it will be necessary to include new sources of information in the future, besides the two already identified, but they cannot precisely define which they are. This changes will occur after the first version of the system is in production. Additionally, the stakeholders define a short period of time to integrate a new source of information. Given this requirements the architect should	461	0	0	ModifiabilityOneOne
462	t	Consider the change in the architecture associated with the use of caches in web services shown in the figure   \n![image][image]  \n That change has the goal and the consequence of, respectively	462	0	0	PerformanceOneOne
463	t	Consider the following excerpt from Nginx case study  \n>"nginx configuration is kept in a number of plain text files which typically reside in /usr/local/etc/nginx or /etc/nginx. The main configuration file is usually called nginx.conf. To keep it uncluttered, parts of the configuration can be put in separate files which can be automatically included in the main one. However, it should be noted here that nginx does not currently support Apache-style distributed configurations (i.e., .htaccess files). All of the configuration relevant to nginx web server behavior should reside in a centralized set of configuration files."  \n  \nWhen comparing the configuration in Nginx with the configuration in Apache we can say that	463	0	0	NginxScenariosTacticsOne
464	t	Suppose that there are certain performance requirements for a system, and that you want to show to the stakeholders of the system that the software architecture that you designed meet those requirements. To do this	464	0	0	ComponentConnectorOne
465	t	Suppose that you are designing the software architecture for an enterprise application that has requirements about the maximum response time for a certain type of requests. Moreover, assume that those requests arrive at the system periodically, whereas the remaining requests have an unpredictable frequency. Finally, assume that your system will have a single server that will be executing on a predefined machine with a 12-core AMD processor. To show to the stakeholders that your system satisfies the performance requirements you have to use views of which architectural style?	465	0	0	RepositoryClientServerOne
467	t	Imagine that you want to develop a system that is to be used in email servers, whose goal is to allow changing the emails that are received by the server (for example, to remove potential viruses or URLs linking to phishing sites). The goal is that the server feeds each received email through this system before processing it (e.g., forward it to another server, or store it locally). The system is supposed to be easily modifiable, to support new types of email transformations. Which architectural style is the most adequate to satisfy these requirements?	467	0	0	SOAPipesFiltersOne
469	t	Web servers implemented in Java, such as the Tomcat web server, typically use a thread-based model for processing requests. That is, they process each request on a different thread within the same JVM process, rather than on a different process. One of the reasons for this is that	469	0	0	nginxOne
470	t	Consider the following architectural view of the Pony-Build system as described in the Continous integration case study   \n![image][image]  \n According to this view the quality of performance is achieved through	470	0	0	ContinousIntegrationViewsOne
471	t	In the Infinispan case study can be read  \n>"Infinispan's core data structures make use of software transactional memory techniques for concurrent access to shared data. This minimizes the need for explicit locks, mutexes and other forms of synchronization, preferring techniques like compare-and-set operations within a loop to achieve correctness when updating shared data structures. Such techniques have been proven to improve CPU utilization in multi-core and SMP systems, and despite the increased code complexity, has paid off in overall performance when under load."  \n  \nThese properties of Infinispan can be represented by	471	0	0	InfinispanViewsOne
472	t	Consider the following excerpt about the Scalable web architecture and distributed systems case study about two different possible implementations of a global cache  \n>"The majority of applications leveraging global caches tend to use the first type, where the cache itself manages eviction and fetching data to prevent a flood of requests for the same data from the clients. However, there are some cases where the second implementation makes more sense. For example, if the cache is being used for very large files, a low cache hit percentage would cause the cache buffer to become overwhelmed with cache misses; in this situation it helps to have a large percentage of the total data set (or hot data set) in the cache."  \n  \n	472	0	0	ScalableArchitectureOne
473	t	In the Graphite system the component *carbon* provides to *webapp* components an access interface to the *buffers* in order to improve the quality of	473	0	0	GraphiteScenarioTacticsOne
475	t	Consider the following sentence by Melvin Conways, also known as Conway's Law  \n>"organizations which design systems ... are constrained to produce designs which are copies of the communication structures of these organizations"  \n  \n	475	0	0	ArchitectureInfluenceCycleOne
476	t	Consider the following architectural view of the Adventure Builder system   \n![image][image]  \n According to this view the stakeholders can see that the Adventure Builder system	476	0	0	AdventureBuilderOne
477	t	Frank Buschmann cites the characterization Marquardt does of Performitis:  \n>"Each part of the system is directly influenced by local performance tuning measures. There is no global performance strategy, or it ignores other qualities of the system as testability and maintainability."  \n  \nFrom this problem you can conclude that:	477	0	0	RequirementsOne
480	t	Consider the Figure that describes the use of caches in web services.   \n![image][image]  \n In that Figure, there is a rectangle with the name *Cache* within another rectangle with the name *Request Node*. Taking into account the description made in the text and the goal of that Figure, those rectangles correspond to which type of software elements?	480	0	0	ModuleComponentOne
513	t	In Nginx, given that a *worker* processes various requests during its life, how does it do it?	513	0	0	nginxTwo
514	t	In the Continous integration case study can be read about future features for Pony-Build  \n>"Currently, each continuous integration system reinvents the wheel by providing its own build configuration language, which is manifestly ridiculous; there are fewer than a dozen commonly used build systems, and probably only a few dozen test runners. Nonetheless, each CI system has a new and different way of specifying the build and test commands to be run. In fact, this seems to be one of the reasons why so many basically identical CI systems exist: each language and community implements their own configuration system, tailored to their own build and test systems, and then layers on the same set of features above that system. Therefore, building a domain-specific language (DSL) capable of representing the options used by the few dozen commonly used build and test tool chains would go a long way toward simplifying the CI landscape."  \n  \nSuppose that you are the architect that has to change the architecture to accomodate this new feature. Therefore, as an architect	514	0	0	ContinousIntegrationViewsTwo
515	t	In the Infinispan case study can be read  \n>"Infinispan uses its own serialization scheme, where full class definitions are not written to the stream. Instead, magic numbers are used for known types where each known type is represented by a single byte. This greatly improves not just serialization and de-serialization speed qualities, but also produces a much more compact byte stream for transmission across a network. An externalizer is registered for each known data type, registered against a magic number. This externalizer contains the logic to convert object to bytes and vice versa."  \n  \nThese properties of Infinispan can be represented by	515	0	0	InfinispanViewsTwo
536	t	Suppose that in the development of an enterprise application (which needs to access a database) it was decided to use the Hibernate framework to simplify the development of the data access code. Which architectural view is the most adequate to represent this decision?	536	0	0	ModuleViewtypeTwoOne
517	t	Consider the following figure depicting two different architectures for web applications   \n![image][image]  \n	517	0	0	MicroservicesArchitectureTwo
518	t	Consider the following excerpt about the Amazon system  \n>"Mainly, I think service orientation has helped us there. The stored data formats are decoupled from the format in which you communicate data items. If there is no need for sharing schemas of the actual storage layout, you can focus on making sure that the service interfaces can evolve in a way that allows you to handle variations of data formats. You could dictate a rigorous single format, but that would not be realistic if you are in Amazon's platform business. We have to make sure that the platform can be extended by our customers to meet their needs."  \n  \nThe architectural style that better represents these aspects of the Amazon architecture is	518	0	0	AmazonTwo
519	t	Consider the following excerpt about the Scalable web architecture and distributed systems case study  \n>"Employing such a strategy maximizes data locality for the requests, which can result in decreased request latency. For example, let's say a bunch of nodes request parts of B: partB1, partB2, etc. We can set up our proxy to recognize the spatial locality of the individual requests, collapsing them into a single request and returning only bigB, greatly minimizing the reads from the data origin."  \n  \nThe quality that is achieved with this tactic is	519	0	0	ScalableArchitectureTwo
522	t	Designing the software architecture for a complex system	522	0	0	ArchitectureInfluenceCycleTwo
523	t	The architecturally significant requirements are important in the process of creating the software architecture for a system because they are	523	0	0	RequirementsTwo
525	t	Consider the following architectural view of the Adventure Builder system   \n![image][image]  \n In this component-and-connector view the interactions the interactions between components follow the architectural style(s)	525	0	0	AdventureBuilderTwo
526	t	Which of the following phrases best describe the relationship between modules and components?	526	0	0	ModuleComponentTwo
527	t	General scenarios play an important role in the development of a software architecture because	527	0	0	ScenariosTacticsTwo
528	t	Suppose that in the process of designing a system's software architecture you come to the conclusion that there are uses relations in both directions in almost all of the system's modules. This means that	528	0	0	UsesGeneralizationTwo
531	t	The email system is composed of various types of components playing different roles. For example, to send an email, a user uses a *mail user agent* (MUA), to compose his message and send it. To send the message, the MUA typically connects to a *mail transfer agent* (MTA) that receives the message, analyzes the message's headers to determine the recipients and, after querying the DNS system to determine the MTA responsible for each recipient, it connects to the MTAs responsible for the destination addresses to deliver the message. Each of these MTAs receives the message and stores it locally or forwards it to others MTAs until the message reaches its destination MTA. The recipient user of the message will then use his MUA to see the messages that were sent to him. To do it, the MUA connects to an IMAP or POP server to obtain the user's messages. Those IMAP and POP servers obtain the messages for a user by reading the messages stored by the MTA. Given this simplified description of the operation of the email system, which of the following architectural styles is more appropriate to represent the pattern of interaction between the MTA and the servers IMAP and POP?	531	0	0	RepositoryClientServerTwo
532	t	Consider the following excerpt from the tutorial on the Hadoop MapReduce:  \n>"Hadoop MapReduce is a software framework for easily writing applications which process vast amounts of data (multi-terabyte data-sets) in-parallel on large clusters (thousands of nodes) of commodity hardware in a reliable, fault-tolerant manner. A MapReduce job usually splits the input data-set into independent chunks which are processed by the map tasks in a completely parallel manner. The framework sorts the outputs of the maps, which are then input to the reduce tasks. Typically both the input and the output of the job are stored in a file-system. The framework takes care of scheduling tasks, monitoring them and re-executes the failed tasks."  \n  \nWhich architectural style of the component-and-connector viewtype is more adequate to describe how the MapReduce works, taking into account its main advantages in solving a problem?	532	0	0	TiersDynamicreconfigurationPeertopeerPublishsubscribeTwo
533	t	There are several tactics to satisfy availability requirements, which may be applied depending on the concrete requirement that we want to satisfy. Assuming that you want to detect faults of type *response* in your system, which tactic is more adequate?	533	0	0	AvailabilityTwo
535	t	Web servers typically receive requests from different users concurrently (that is, either different users make requests simultaneously or they make them fast enough that it is not possible for the web server to answer one request from one user before receiving another request from another user). To process all the requests, web servers may use different implementation strategies. Assuming that we want to develop a web server to serve only static pages with more or less the same size to a set of clients on the same LAN network as the server, which of the following strategies would be better?	535	0	0	NginxScenariosTacticsTwo
537	t	Consider the change in the architecture associated with the use of caches in web services shown in the figure   \n![image][image]  \n Taking into consideration that this change involves adding a server, which has a larger storage capacity than the request Nodes, that change has the impact of	537	0	0	PerformanceTwoOne
538	t	During the different steps on how to create an architecture, the precise specification of architecture quality attributes is initially relevant to	538	0	0	DesigningArchitectureTwo
539	t	In the Continous integration case study can be read about Jenkins  \n>"It takes advantage of the JUnit XML standard for unit test and code coverage reporting to integrate reports from a variety of test tools. Jenkins originated with Sun, but is very widely used and has a robust open-source community associated with it."  \n  \nThe quality of Jenkins that is emphasized in the above sentence is	539	0	0	ContinousIntegrationScenariosTacticsTwo
540	t	In the Infinispan case study can be read  \n>"Infinispan supports several pluggable cache stores-adapters that can be used to persist data to disk or any form of secondary storage. The current default implementation is a simplistic hash bucket and linked list implementation, where each hash bucket is represented by a file on the filesystem. While easy to use and configure, this isn't the best-performing implementation."  \n  \nThe main architectural quality addressed in the above excerpt is	540	0	0	InfinispanScenariosTacticsTwo
546	t	A general scenario for a quality attribute	546	0	0	GeneralScenario
547	t	Considering the availability architectural quality, the tactic of retry	547	0	0	OmissionRetry
548	t	According to Frank Buschmann in the article *Introducing the Pragmatic Architect*	548	0	0	PragmaticArchitect
549	t	In his article *Who Needs an Architect?* Martin Fowler refers to the following architecture definition  \n>"*architecture is the set of design decisions that must be made early in a project*"  \n  \n	549	0	0	EarlydDecisions
550	t	Consider the following informal view of an Image Hosting System   \n![image][image]  \n	550	0	0	ImageHostingPerformance
558	t	Consider the following informal view of an Image Hosting System   \n![image][image]  \n	558	0	0	ImageHostingScalability
559	t	In his article *Who Needs an Architect?* Martin Fowler refers to the following architecture definition  \n>"*the expert developers working on that project have a shared understanding of the system design*"  \n  \n	559	0	0	SharedUnderstanding
560	t	In a scenario for interoperability	560	0	0	InteroperabilityStimulus
566	t	Consider the following informal view of an Image Hosting System   \n![image][image]  \n	566	0	0	ImageHostingReads
568	t	Very often, when a software architecture is being designed, conflicting requirements are identified, like between security and availability. The role of the software architect is to	568	0	0	Diplomat
569	t	The definition of software architecture, on the course book, is  \n>"*The software architecture of a system is the set of structures needed to reason about the system, which comprise software elements, relations among them, and properties of both.*"  \n  \nAccording to this definition	569	0	0	ASDefinition
570	t	Considering the availability architectural quality and the tactics of active redundancy, passive redundancy, and spare	570	0	0	RestartInRedundancy
576	t	Consider the following fragment in the description of the nginx case study.  \n>"nginx's configuration system was inspired by Igor Sysoev's experiences with Apache. His main insight was that a scalable configuration system is essential for a web server. The main scaling problem was encountered when maintaining large complicated configurations with lots of virtual servers, directories, locations and datasets. In a relatively big web setup it can be a nightmare if not done properly both at the application level and by the system engineer himself."  \n  \n	576	0	0	NginxOne
577	t	The Attribute-Driven Design method is characterized by	577	0	0	BusinessToDesignOne
579	t	Consider the following fragment in the description of the Graphite system:  \n>"The Graphite webapp allows users to request custom graphs with a simple URL-based API. Graphing parameters are specified in the query-string of an HTTP GET request, and a PNG image is returned in response."  \n  \n	579	0	0	GraphiteOne
580	t	The modifiability tactic Use an Intermediary between two modules	580	0	0	ModifiabilityOne
586	t	In the description of the nginx case study we can read:  \n>"nginx is event-based, so it does not follow Apache's style of spawning new processes or threads for each web page request. The end result is that even as load increases, memory and CPU usage remain manageable. nginx can now deliver tens of thousands of concurrent connections on a server with typical hardware."  \n  \nThe tactic nginx follows to achieve tens of thousands of concurrent connections is	586	0	0	NginxTwo
587	t	Consider the following scenario: *A system administrator simultaneously launches several instances of the system, each one using a different database, and is able to do it in less than 10 minutes.*	587	0	0	ModifiabilityTwo
588	t	Consider the following fragment in the description of the Graphite system:  \n>"To avoid this kind of catastrophe, I added several features to carbon including configurable limits on how many data points can be queued and rate-limits on how quickly various whisper operations can be performed. These features can protect carbon from spiraling out of control and instead impose less harsh effects like dropping some data points or refusing to accept more data points. However, proper values for those settings are system-specific and require a fair amount of testing to tune. They are useful but they do not fundamentally solve the problem. For that, we'll need more hardware."  \n  \nThe performance tactics referred in the above description are:	588	0	0	GraphiteTwo
589	t	Architecturally significant requirements (ASR) are captured in a utility tree where each one of the ASRs are classified in terms of its architectural impact and business value.	589	0	0	BusinessToDesignTwo
590	t	The two basic contributors for the response time are the processing time and the blocking time. Which tactic for performance may reduce the blocking time	590	0	0	PerformanceTwo
636	t	Consider the following distinction between Monoliths and Microservices made by Matin Fowler   \n![image][image]  \n If we try to map this figure into a set of views we will need.	636	0	0	MicroAndAmazonOne
596	t	It was decided that the Fénix system should be based on open-source software.	596	0	0	BusinessToDesignThree
597	t	Consider the following fragment in the description of the Graphite system:  \n>"Imagine that you have 60,000 metrics that you send to your Graphite server, and each of these metrics has one data point per minute. Remember that each metric has its own whisper file on the filesystem. This means carbon must do one write operation to 60,000 different files each minute. As long as carbon can write to one file each millisecond, it should be able to keep up. This isn't too far fetched, but let's say you have 600,000 metrics updating each minute, or your metrics are updating every second, or perhaps you simply cannot afford fast enough storage. Whatever the case, assume the rate of incoming data points exceeds the rate of write operations that your storage can keep up with. How should this situation be handled?"  \n  \n	597	0	0	GraphiteThree
598	t	Jeff Atwood wrote an article in its blog about performance of software systems that is entitled, *Hardware is Cheap, Programmers are Expensive*. Which performance tactic(s) is he suggesting	598	0	0	PerformanceThree
599	t	In the description of the nginx case study we can read:  \n>"nginx's modular architecture generally allows developers to extend the set of web server features without modifying the nginx core. nginx modules come in slightly different incarnations, namely core modules, event modules, phase handlers, protocols, variable handlers, filters, upstreams and load balancers. At this time, nginx doesn't support dynamically loaded modules; i.e., modules are compiled along with the core at build stage."  \n  \nThe above sentence corresponds to	599	0	0	NginxThree
600	t	Consider the modifiability quality and the cost of change.	600	0	0	ModifiabilityThree
619	t	In the Continuous Integration case study can be read  \n>"External resource coordination: Integration tests may depend on non-local resources such as a staging database or a remote web service. The CI system may therefore need to coordinate builds between multiple machines to organize access to these resources."  \n  \nThe referred tactic is	619	0	0	ContinuousIntegrationTwo
620	t	In the Infinispan case study can be read  \n>"This allows applications to theoretically address an unlimited amount of in-memory storage as nodes are added to the cluster, increasing overall capacity."  \n  \nThe quality that is referred is	620	0	0	InfinispanTwo
626	t	Consider the two following views   \n![image][image]  \n	626	0	0	ComponentAndConnectorThree
627	t	In the Infinispan case study can be read  \n>"Before putting data on the network, application objects need to be serialized into bytes so that they can be pushed across a network, into the grid, and then again between peers. The bytes then need to be de-serialized back into application objects, when read by the application. In most common configurations, about 20% of the time spent in processing a request is spent in serialization and de-serialization."  \n  \nThe above description can motivate a scenario for	627	0	0	InfinispanThree
628	t	Consider the shared-data style. Which of the following qualities does it support?	628	0	0	CCStyleThree
629	t	In the Continuous Integration case study can be read  \n>"It takes advantage of the JUnit XML standard for unit test and code coverage reporting to integrate reports from a variety of test tools."  \n  \nThe referred quality is	629	0	0	ContinuousIntegrationThree
646	t	In the description of Infinispan system can be read  \n>"Infinispan supports several pluggable cache stores-adapters that can be used to persist data to disk or any form of secondary storage. The current default implementation is a simplistic hash bucket and linked list implementation, where each hash bucket is represented by a file on the filesystem. While easy to use and configure, this isn't the best-performing implementation."  \n  \nThe architectural style(s) that should be used to illustrate the sentence is (are)	646	0	0	InfinispanTwo
648	t	Consider the deployment architectural style of the allocation viewtype.	648	0	0	AllocationTwo
649	t	In the Continuous Integration case can be read  \n>"Build notification: The outcomes of builds generally need to be communicated to interested clients, either via pull (Web, RSS, RPC, etc.) or push notification (e-mail, Twitter, etc.) This can include notification of all builds, or only failed builds, or builds that haven't been executed within a certain period."  \n  \nThe architectural style used in push notifications is	649	0	0	JenkinsTwo
650	t	Consider the following representation of Amazon's architecture (sorry for the figure's layout: **save trees**)   \n![image][image]  \n What is the most relevant architecture style that is used in this figure?	650	0	0	MicroAndAmazonTwo
656	t	In the interview Werner Vogels from Amazon gives to Jim Gray, Werner Vogels says that  \n>"The stored data formats are decoupled from the format in which you communicate data items. If there is no need for sharing schemas of the actual storage layout, you can focus on making sure that the service interfaces can evolve in a way that allows you to handle variations of data formats."  \n  \nWhich means that in the software architecture of Amazon's systems	656	0	0	MicroAndAmazonThree
657	t	Consider the following representation of the CDash system   \n![image][image]  \n The architecture style between the Dashboard and the Clients is:	657	0	0	JenkinsThree
660	t	In the description of Infinispan system can be read  \n>"When dealing with thread pools to process such asynchronous tasks, there is always a context switching overhead. That threads are not cheap resources is also noteworthy. Allocating appropriately sized and configured thread pools is important to any installation making use of any of the asynchronous features of Infinispan."  \n  \nThe architectural style that should be used to illustrate the sentence is	660	0	0	InfinispanThree
695	t	Consider the following excerpt from the Wikipedia page on *white-box testing*:  \n>"White-box testing is a method of testing software that tests internal structures or workings of an application, as opposed to its functionality. In white-box testing an internal perspective of the system (including the module's code), as well as programming skills, are required and used to design test cases. The tester chooses inputs to exercise paths through the code and determine the appropriate outputs."  \n  \nAssuming that you belong to the team testing a complex system and that you are responsible for performing white box tests on the system, which of the following architectural views of the system would be most useful to you?	695	0	0	WhiteBoxTestingINGLES
696	t	The Chromium is a web browser that introduced an innovative architecture. In the Chromium description we can read:  \n>"We use separate processes for browser tabs to protect the overall application from bugs and glitches in the rendering engine. We also restrict access from each rendering engine process to others and to the rest of the system. In some ways, this brings to web browsing the benefits that memory protection and access control brought to operating systems. We refer to the main process that runs the UI and manages tab and plugin processes as the "browser process" or "browser." Likewise, the tab-specific processes are called "render processes" or "renderers." The renderers use the WebKit open-source layout engine for interpreting and laying out HTML."  \n  \nWhich architectural style should we use to represent this aspect of Chromium?	696	0	0	ArqChrome
697	t	Consider that a software development team uses an agile methodology such as XP (Extreme Programming), where no documentation is produced. Then, the systems developed by that team	697	0	0	SoftwareArchitectureOne
698	t	The requirements impact on how an architecture is designed	698	0	0	RequirementsImpact
699	t	Consider the following scenario  \n>"If one of the application servers fails to respond when the system is in its normal operation state, the load balancer should redirect requests to another application server."  \n  \n	699	0	0	AvailabilityScenarioOne
708	t	Consider the following description of the behavior of Twitter ingestion mechanisms  \n>"Write. when a tweet comes in there's an O(n) process to write to Redis clusters, where n is the number of people following you. Painful for Lady Gaga and Barack Obama where they are doing 10s of millions of inserts across the cluster. All the Redis clusters are backing disk, the Flock cluster stores the user timeline to disk, but usually timelines are found in RAM in the Redis cluster."  \n  \n	708	0	0	TwitterOne
709	t	Consider the following description of the behavior of Twitter  \n>"Solution is a write based fanout approach. Do a lot of processing when tweets arrive to figure out where tweets should go. This makes read time access fast and easy. Don't do any computation on reads. With all the work being performed on the write path ingest rates are slower than the read path, on the order of 4000 QPS."  \n  \nTo describe the performance quality of this behavior, and considering that the number of reads is much higher than the number of writes, we need to have a view that includes	709	0	0	TwitterThree
711	t	In the interview Werner Vogels from Amazon gives to Jim Gray, Werner Vogels says that  \n>"The stored data formats are decoupled from the format in which you communicate data items. If there is no need for sharing schemas of the actual storage layout, you can focus on making sure that the service interfaces can evolve in a way that allows you to handle variations of data formats."  \n  \nWhich means that in the software architecture of Amazon's systems	711	0	0	MicroAndAmazonThree
712	t	Consider the following representation of a system following a microservices architecture,   \n![image][image]  \n After an invocation through the REST API	712	0	0	BoundedContextOne
713	t	Consider the following figure   \n![image][image]  \n	713	0	0	DomainDesignOne
714	t	One of the key requirements for the HDFS system is that the data stored in the system remains available, even in the presence of various types of failures (non simultaneous) in the hardware in which the system executes. To show that the system satisfies this requirement	714	0	0	HadoopDisponibilidadeDadosINGLES
715	t	In the HDFS system the fault recovery tactics are:	715	0	0	HadoopTacticasRecuperacaoFaltasINGLES
716	t	The documentation of the software architecture for a system is often composed of several views, because	716	0	0	SecondEEEN
796	t	In the description of Hadoop we can red.  \n>"The CheckpointNode periodically combines the existing checkpoint and journal to create a new checkpoint and an empty journal. The CheckpointNode usually runs on a different host from the NameNode since it has the same memory requirements as the NameNode."  \n  \n	796	0	0	HadoopCreateFile
751	t	Consider the following modifiability scenario  \n>"The effort necessary to successfully port the system to execute in a new browser should not be higher than 5 person/month."  \n  \n	751	0	0	ModifiabilityScenario
752	t	The main tactic associated with the aspects architectural style is:	752	0	0	AspectsTactics
753	t	In the HDFS system, in the stakeholders perspective, the use of low cost servers to build the clusters is:	753	0	0	HadoopStakeholdersEurosINGLES
754	t	In HDFS, during normal operation DataNodes use the heartbeat tactic	754	0	0	HadoopHeartbeatINGLES
755	t	The software architecture of a system	755	0	0	SoftwareArchitectureTwo
756	t	As part of the process of creating an architecture, we talked about a framework for capturing some of the requirements for a system. In this context, **concrete scenarios** are used for	756	0	0	ConcreteScenarios
757	t	Consider the following availability scenario  \n>"If one of the application servers fails to respond to a request when the system is in its normal operation state, the system should notify the operator and continue to operate normally."  \n  \n	757	0	0	AvailabilityScenarioTwo
758	t	Suppose that, to satisfy an availability requirement related with the occurrence of faults at the network infrastructure used by your system, you want to use the tactic named *Ping/Echo*. How does the use of that tactic manifests in the architectural views of your system?	758	0	0	AvailabilityINGLES
759	t	Consider the following description of the *Infinispan* system:  \n>"Before putting data on the network, application objects need to be serialized into bytes so that they can be pushed across a network, into the grid, and then again between peers. The bytes then need to be de-serialized back into application objects, when read by the application. In most common configurations, about 20% of the time spent in processing a request is spent in serialization and de-serialization."  \n  \nThe above description can motivate a scenario for	759	0	0	InfinispanThree
760	t	In which performance tactic it can occur that the inputs are not completely processed, even though they always start being processed	760	0	0	PerfomanceTacticTwo
761	t	Currently, the most popular architecture for an enterprise application is composed of 3 tiers. The three tiers are	761	0	0	TresTiersINGLES
762	t	The Service-Oriented Architecture style improves interoperability because	762	0	0	SOAInteroperability
763	t	Consider a system that will require a significative configuration effort during deployment, because it provides several variations of the same functionalities and it is necessary to choose which functionalities better fit in each case. The most helpful architectural view for this situation is	763	0	0	InstallView
764	t	Consider the following description of the behavior of Twitter  \n>"Solution is a write based fanout approach. Do a lot of processing when tweets arrive to figure out where tweets should go. This makes read time access fast and easy. Don't do any computation on reads. With all the work being performed on the write path ingest rates are slower than the read path, on the order of 4000 QPS."  \n  \nTo describe this behavior we need to	764	0	0	TwitterTwo
765	t	Consider the following description of the behavior of Twitter ingestion mechanisms  \n>"Write. when a tweet comes in there's an O(n) process to write to Redis clusters, where n is the number of people following you. Painful for Lady Gaga and Barack Obama where they are doing 10s of millions of inserts across the cluster. All the Redis clusters are backing disk, the Flock cluster stores the user timeline to disk, but usually timelines are found in RAM in the Redis cluster."  \n  \nThe view that represents this behavior should be of the	765	0	0	TwitterFour
767	t	When designing an architecture requirements can be split into functional, quality attributes, and constraints. Functional requirements have impact on:	767	0	0	FunctionalModule
768	t	Suppose that you are developing a software architecture for a new large scale system and that you intend to resort extensively to third party subcontractors for the development of various parts of the system. Which architectural styles are most useful to plan the development of the system in this case?	768	0	0	SubcontractorsINGLES
769	t	To achieve a faster time-to-market, software companies are increasingly using a strategy of incremental releases of their software, where each new release has a set of new features. Which architectural style is better to analyse whether the system's software architecture is adequate for the planned incremental releases?	769	0	0	UsesStyle
797	t	An architectural tactic for a system describes	797	0	0	ArchitecturalTactics
900	t	Consider a web application that was implemented using three layers: presentation, domain logic, and data access. How are these layers mapped into the components if it is a rich interface application.	900	0	0	WebAppsOne
770	t	According to the document that describes nginx:  \n>"nginx's modular architecture generally allows developers to extend the set of web server features without modifying the nginx core. nginx modules come in slightly different incarnations, namely core modules, event modules, phase handlers, protocols, variable handlers, filters, upstreams and load balancers. [...] Event modules provide a particular OS-dependent event notification mechanism like kqueue or epoll. Protocol modules allow nginx to communicate through HTTPS, TLS/SSL, SMTP, POP3 and IMAP."  \n  \nWhich architectural style is more adequate to represent the information presented above?	770	0	0	nginxModuleTypesINGLES
772	t	Consider the following requirement for availability of the Adventure Builder system  \n>"The Consumer Web site is available to the user 24x7. If an instance of OPC application fails, the fault is detected and the system administrator is notified in 30 seconds; the system continues taking order requests; another OPC instance is automatically created; and data remains in consistent state."  \n  \nIn order to support this quality it is necessary to	772	0	0	AdventureBuilderSix
773	t	Consider the module viewtype views of the DVDCatalog application. The architect knows about a new requirement  \n>"To support iPhone/iPad/Android version with sync, which allows offline use of the application in the mobile device and data synchronization to occur when a connection is available"  \n  \nThis requirement requires a change of	773	0	0	DVDCatalogMobile
775	t	The Peer-to-Peer architectural style provides high scalability and availability. In the context of a file sharing system	775	0	0	PeerToPeerSpace
777	t	In world-wide systems like Facebook or Amazon,	777	0	0	WorldWideEN
778	t	Consider the following representation of a system following a microservices architecture,   \n![image][image]  \n	778	0	0	BoundedContextTwo
779	t	Consider the following data model   \n![image][image]  \n	779	0	0	DomainDesignTwo
780	t	The Chromium is a web browser that introduced an innovative architecture. In the Chromium description we can read:  \n>"Chromium is a large and complex cross-platform product. We try to share as much code as possible between platforms, while implementing the UI and OS integration in the most appropriate way for each. While this gives a better user experience, it adds extra complexity to the code. This document describes the recommended practices for keeping such cross-platform code clean. We use a variety of different file naming suffixes to indicate when a file should be used:  \n-  Windows files use the `_win` suffix.  \n-  Cocoa (Mac UI) files use the `_cocoa` suffix, and lower-level Mac files use the `_mac` suffix.  \n-  Linux files use `_linux` for lower-level files, `_gtk` for GTK-specific files, and `_x` for X Windows (with no GTK) specific files.  \n-  Posix files shared between Mac and Linux use the `_posix` suffix.  \n-  Files for Chrome's ''Views'' UI (on Windows and experimental GTK) layout system use the `_views` suffix.  \nThe separate front-ends of the browser are contained in their own directories:  \n-  Windows Views (and the experimental GTK-views):`chrome/browser/ui/views`  \n-  Linux GTK: `chrome/browser/gtk`  \n-  Mac: `chrome/browser/cocoa`  \n"  \n  \nWhich architectural style should we use to represent this aspect of Chromium?	780	0	0	ChromeMultiPlatform
786	t	Consider the following figure that presents the Hadoop cluster topology.   \n![image][image]  \n	786	0	0	HadoopCluster
787	t	The software architecture of a system	787	0	0	SoftwareArchitecture
788	t	A general scenario for a quality attribute	788	0	0	GeneralScenario
789	t	In the description of the Twitter system we can read:  \n>"Twitter is optimized to be highly available on the read path on the home timeline. Read path is in the 10s of milliseconds."  \n  \nThis is achieved because:	789	0	0	TwitterScaleOne
790	t	Consider the following figure that presents a Proxy Server, which collapses requests from different users.   \n![image][image]  \n	790	0	0	ProxyServer
798	t	In the description of the Twitter system we can read:  \n>"On the search timeline: Write. when a tweet comes in and hits the Ingester only one Early Bird machine is hit. Write time path is O(1). A single tweet is ingested in under 5 seconds between the queuing and processing to find the one Early Bird to write it to."  \n  \n	798	0	0	TwitterScaleTwo
799	t	In his article, *Who Needs and Architect?*, Martin Fowler cites Ralph Johnson definition:  \n>"Architecture is the set of decisions that must be made early in a project."  \n  \nIn his opinion:	799	0	0	EarlyDecisions
800	t	Consider the following figure that presents a Queue where client applications write their requests to be served by a server.   \n![image][image]  \n	800	0	0	Queues
809	t	Considering the availability architectural quality, the tactic of retry	809	0	0	OmissionRetry
810	t	Several of the cases studied in this course have scalability requirements. That means that those systems should be designed in such a way that they	810	0	0	Scalability
816	t	Consider that a module, that contains a complex business logic, needs to invoke a remote entity using a particular communication protocol and it is needs to manage the invocation, like deal with the possible errors, delays and omissions in the invocation, transform the data before sending it, etc. Which tactic should be applied for a scenario where there will be changes in the communication protocol. Note that the business logic comprises a set of functionalities that is independent of the remote invocation technological aspects.	816	0	0	ModifiabilityTwo
818	t	The architecture of the HDFS system only allows the existence of one NameNode. Given the responsibilities of this component and the current architecture of HDFS, what would be the consequences of adding the possibility of having replicas of the NameNode in the system?	818	0	0	HadoopNameNodeReplica
820	t	There are several tactics to satisfy availability requirements, which may be applied depending on the concrete requirement that we want to satisfy. Assuming that you want to detect faults of type *response* in your system, which tactic is more adequate?	820	0	0	Availability
829	t	An architect is decomposing a system into a set of responsibilities using a view of the Decomposition style. However, she had already to backtrack several times and try new decompositions because she always end up with some responsibility that cannot fit within a single module.	829	0	0	Aspects
837	t	Consider the module viewtype views of the DVDCatalog application. The architect knows about a new requirement  \n>"To allow the share of catalogs with family and friends, including some access control."  \n  \nThis requirement requires	837	0	0	DVDCatalogAspects
840	t	In Facebook it is not possible to have the information about more that one bilion users in a single disk. Therefore, a sharding technique is applied, where the persistent information is split between several database servers, and applications are routed to the right servers for queries and updates. To describe this architecture	840	0	0	DataModelFacebook
846	t	Consider the following view of the Adventure Builder system   \n![image][image]  \n In this view the following architectural styles are used	846	0	0	AdventureBuilderOne
848	t	The connectors on component-and-connector view	848	0	0	ComponentConnectorTwo
856	t	Consider the following view of the Adventure Builder system   \n![image][image]  \n In this view the following architectural styles are used	856	0	0	AdventureBuilderTwo
857	t	In the Publish-Subscribe architectural style	857	0	0	PublishSubscribe
858	t	A high-level component-and-connect view of Graphite system can be designed using only the architectural style(s)	858	0	0	GraphiteViewsTwo
860	t	The Java web servers, like Tomcat, use threads to process requests. For each request they create (or reuse) a thread to process it. To draw a architectural view that describes this behaviour we should use	860	0	0	CommunicationProcesses
899	t	In the software architecture of a system, the Deployment architectural style of the allocation viewtype is best suited for	899	0	0	Deployment
881	t	In the component-and-connector viewtype connectors can be complex, which means that they provide a rich set of qualities to the interaction between the components that they connect. These complex connectors can be documented in another view using a set of components interacting through simpler connectors.	881	0	0	ComponentAndConnectorViewtypeOne
883	t	Consider the following fragment in the description of the Graphite system.  \n>"The Graphite webapp allows users to request custom graphs with a simple URL-based API. Graphing parameters are specified in the query-string of an HTTP GET request, and a PNG image is returned in response."  \n  \nTo describe this scenario it should be designed a view that applies the following architectural style	883	0	0	GraphiteOne
884	t	Consider the following figure that presents a Proxy Server that collapses requests from different users.   \n![image][image]  \n	884	0	0	ProxyServer
885	t	In the context of the FenixEdu case study, the business case was to	885	0	0	FenixOne
886	t	The stimulus of an availability scenario is	886	0	0	AvailabilityOne
887	t	A response measure of a performance scenario is	887	0	0	PerformanceOne
888	t	The layered architectural style applies the modifiability architectural tactic of	888	0	0	ModifiabilityExamOne
889	t	One of the advantages of having views of the module viewtype is that they allow to do an impact analysis to predict the effect of modifying the system. The architectural style of the module viewtype which provides richer information for this impact analysis is	889	0	0	ModuleViewtypeExamOne
890	t	Consider that a chess game should provide an automatic and intelligent chess player, and that to implement that player we will use some of the many chess engines already available in the market. Moreover, the system should allow the user to choose which engine to use for each new game. Given these requirements, which of the architectural styles from the module viewtype are best suited to satisfy them?	890	0	0	DecompositionGeneralization
891	t	In a microservices architecture, aggregates are used as a unit of processing	891	0	0	AggregateOne
892	t	Consider the Microservice architectural style. Which of the following sentences **does not** describe an advantage of microservices?	892	0	0	MicroservicesExamOne
893	t	Consider the following usability scenario of the Catalog of DVDs case study  \n>"The user intends to have up-to-date info about the movies and the system informs the user that the existing sources have new information about one of his DVDs, which helps to maintain an up-to-date catalog."  \n  \nThe tactic used to fulfill this scenario is	893	0	0	DVDOne
894	t	Consider the following view of the Adventure Builder case study that applies the tiers architectural style   \n![image][image]  \n	894	0	0	AdventureBuilderOne
895	t	Consider the following view of the Pulse case study   \n![image][image]  \n This view provides a solution that uses the following tactic	895	0	0	PulseOne
896	t	In the description of architecture of the OrderPad case study it can be read that the updates the user does on the OrderPad when it is offline are not lost. This availability quality is achieved through a	896	0	0	OrderPadOne
921	t	Consider the following decomposition of a domain model into 3 aggregates. If, instead of this decomposition, `Customer` and `Order` were in the same aggregate   \n![image][image]  \n	921	0	0	AggregateTwo
922	t	An availability tactic to prevent faults is	922	0	0	AvailabilityTwo
923	t	A performance tactic to control resource demand is	923	0	0	PerformanceTwo
924	t	Consider the following definition of Microservice architectural style by Martin Fowler  \n>"The microservice architectural style is an approach to developing a single application as a suite of small services, each running in its own process and communicating with lightweight mechanisms, often an HTTP resource API. These services are built around business capabilities and independently deployable by fully automated deployment machinery. There is a bare minimum of centralized management of these services, which may be written in different programming languages and use different data storage technologies."  \n  \nTo represent an architecture based on Microservices	924	0	0	MicroservicesExamTwo
927	t	Which quality, or qualities, of the Graphite system are described by the sentence: *Graphite's Composer UI provides a point-and-click method to create a graph from which you can simply copy and paste the URL.*	927	0	0	GraphiteTwo
928	t	Consider the Work Assignment architectural style of the allocation viewtype.	928	0	0	WorkAssigment
929	t	Consider the following figure that presents a Image Hosting System.   \n![image][image]  \n By adding another Image File Storage component, which contains a redundant copy of the data and provides read access to the clients, but without guaranteeing a ACID transactional behavior between reads and writes, it improves the quality(ies) of	929	0	0	ScalableArchitectureOne
930	t	Consider a web application that supports several types of user interface, e.g., web, mobile, etc. If it has to process a high volume of requests, which depend on the type of user interface, and a multi-tier architecture is followed. How many tiers should be used?	930	0	0	WebAppsTwo
931	t	Consider the following generalization view of the Catalog of DVD case study to fulfill a modifiability scenario   \n![image][image]  \n From this view the stakeholders can infer	931	0	0	DVDTwo
933	t	Suppose that you are developing the software architecture of a new system for an organization composed of several organizational units, each one with its own information systems, which have been developed independently of each other over the course of several years and depending on the particular needs of each unit. Your system has the goal of integrating the various existing systems, providing in this way not only a unified view of how the organization works, but also allowing the creation of new processes within the organization that involve more than one unit. Which architectural style is better suited to design such a system?	933	0	0	SOA
934	t	Consider the following view of the Adventure Builder case study   \n![image][image]  \n	934	0	0	AdventureBuilderTwo
936	t	Consider the following view of the Pulse case study   \n![image][image]  \n This view applies the following architectural styles	936	0	0	PulseTwo
937	t	Consider the architecture of the Morrison's OrderPad. The connector between the client component, executing in the Pad, and the server component, executing in the OrderPadDatabase	937	0	0	OrderPadTwo
938	t	One of the advantages of having views of the module viewtype is that they allow to do a traceability analysis of requirements, how the functional requirements of the system are supported by module responsibilities. The modifiability tactic that is involved in this mapping is	938	0	0	ModuleViewtypeExamTwo
940	t	Considered the following two views of a system that receive a stream of character and produce the same stream where the characters are alternately uppercase and lowercase.   \n![image][image]  \n	940	0	0	ComponentAndConnectorViewtypeTwo
946	t	In the Architect Elevator article by Gregor Hohpe can be read:  \n>"Finding the appropriate context requires the architect to visit many floors of the organization."  \n  \nThis sentence reflects the fact that an architecture is	946	0	0	ElevatorCommon
947	t	Consider the following scenario for performance  \n>"During the enrollment period the FenixEDU system should be able to completely enroll 5.000 students in less than 30 minutes."  \n  \n	947	0	0	PerformanceSenario
948	t	In the description of the Microservices Architecture by James Lewis and Martin Fowler can be read:  \n>"The microservice approach to division ..., splitting up into services organized around business capability. Such services take a broad-stack implementation of software for that business area, including user-interface, persistent storage, and any external collaborations. Consequently the teams are cross-functional, including the full range of skills required for the development: user-experience, database, and project management."  \n  \nConsidering the architecture influence cycle, which influence factor it is being considered?	948	0	0	MicroservicesProject
949	t	Consider the following figure that presents an architectural view of an *Image Hosting Application* which resulted from the enrichment of another architectural view by adding another *Image File Storage* pair, in the figure they are distinguished by 1 and 2.   \n![image][image]  \n Which quality results from this enrichment, that was not provided by the previous version of the architecture?	949	0	0	ScalablePartitioning
950	t	Consider the following figure that presents a Queue where client applications write their requests to be processed by a server.   \n![image][image]  \n This solution **does not** provide the following quality:	950	0	0	QueuesQualities
956	t	Consider the following figure that presents a Queue where client applications write their requests to be processed by a server (asynchronous) and compare with another architectural design (synchronous) where a thread is associated with each request.   \n![image][image]  \n	956	0	0	QueuesSyncAndAsync
957	t	In the Architect Elevator article by Gregor Hohpe can be read:  \n>"Once a developer approached our architecture team with an application that had "significant scalability demands". A quick look at the architecture diagram revealed numerous components communicating via XML messages. When I pointed out that this may be the very reason for the performance concerns, I was quickly informed that this was an architecture decision and couldn't be changed. Assuming the architects are smart and well-intentioned, they may have thought about interoperability when they made this decision but may be unaware of the negative impact on run-time performance and development velocity."  \n  \nFrom this sentence we can conclude that	957	0	0	ElevatorInteroperability
958	t	In the description of the Microservices Architecture by James Lewis and Martin Fowler can be read:  \n>"As well as the fact that services are independently deployable and scalable, each service also provides a firm module boundary, even allowing for different services to be written in different programming languages. They can also be managed by different teams."  \n  \nWhich is not necessarily an advantage of being independently deployable and scalable?	958	0	0	MicroservicesModularity
959	t	Consider the following figure that presents an architectural view of an *Image Hosting Application*.   \n![image][image]  \n	959	0	0	ReadsAndWrites
960	t	Which of the following tactics is not related with the management of resources	960	0	0	PerformanceTacticsOne
966	t	Consider the following figure that presents a Queue where client applications write their requests to be processed by a server (asynchronous) and compare with another architectural design (synchronous) where a thread is associated with each request.   \n![image][image]  \n Consider a situation where the server that processes the tasks crashes	966	0	0	QueuesCrash
967	t	Consider the following figure that presents an architectural view of an *Image Hosting Application*.   \n![image][image]  \n The replication between the Image File Storage *n* and Image File Storage *nb*	967	0	0	DataStorageAvailability
968	t	In the description of the Microservices Architecture by James Lewis and Martin Fowler can be read:  \n>"Decentralizing responsibility for data across microservices has implications for managing updates. The common approach to dealing with updates has been to use transactions to guarantee consistency when updating multiple resources. This approach is often used within monoliths."  \n  \nWhat is the impact of decentralizing responsibility for data across microservices?	968	0	0	MicroservicesConsistency
969	t	Which of the following tactics is not related with the control of resource demand	969	0	0	PerformanceTacticsTwo
970	t	In the Architect Elevator article by Gregor Hohpe can be read:  \n>"A lot of large companies have discovered the benefits of cloud computing but see it mainly as an infrastructure topic. I feel that's misguided: being able to get compute resources more quickly and cheaply is useful, but the real business benefit lies in a fully automated tool chain that minimizes the time in which a normal code change can go into production. Not quite coincidentally, this is my favorite definition of DevOps."  \n  \nIn the author's opinion	970	0	0	ElevatorDevops
976	t	Consider the Decomposition architectural style of the Module viewtype	976	0	0	ModuleViewtypeOne
977	t	The *Composer UI* component of Graphite system, described as - *Graphite's Composer UI provides a point-and-click method to create a graph from which you can simply copy and paste the URL* - to be effective needs to show to the user the changes she performs in the graph such that she has immediate feedback whenever she clicks on a option. To do so, the architecture needs to include	977	0	0	GraphiteComposerUIPerformance
1100	t	In a modifiability scenario the environment can be characterized as design time, compile time, build time, initiation time, and runtime.	1100	0	0	ModifiabilityOne
979	t	The modifiability tactic Use an Intermediary between two modules	979	0	0	ModifiabilityOne
980	t	When applying Attribute-Driven Design (ADD) to the FenixEdu system the creation of a view where there are redundant web servers, load balancers and database servers	980	0	0	FenixADD
987	t	The availability quality can be supported by a voting tactic in order to identify faults of	987	0	0	AvailabilityVotingSecond
989	t	Consider the following scenario: *A system administrator adds more copies of computation of the system, each one using a different database, and is able to do it in less than 10 minutes.*	989	0	0	ModifiabilityTwo
997	t	A software system is usually described using different architectural views	997	0	0	ArchitecturalViews
998	t	In the Graphite system description can be read:  \n>"We've got 600,000 metrics that update every minute and we're assuming our storage can only keep up with 60,000 write operations per minute. This means we will have approximately 10 minutes worth of data sitting in carbon's queues at any given time. To a user this means that the graphs they request from the Graphite webapp will be missing the most recent 10 minutes of data."  \n  \n	998	0	0	GraphiteReliability
1000	t	Consider the modifiability quality and the cost of change.	1000	0	0	ModifiabilityThree
1006	t	Consider the following view of the Adventure Builder system   \n![image][image]  \n In this view the following architectural styles are used	1006	0	0	AdventureBuilderComponentAndConnectorOne
1007	t	Suppose that in the development of an enterprise application (which needs to access a database) it was decided to use the FenixFramework library to simplify the development of the data access code. Which architectural style is the most adequate to represent this decision?	1007	0	0	ModuleStylesOne
1009	t	The Pipe-and-Filter style allows composition of filters	1009	0	0	PipeFilterComposition
1010	t	Consider the following modifiability scenario for the Adventure Builder system  \n>"A new business partner (airline, lodging, or activity provider) that uses its own web services interface is added to the system in no more than 10 person-days of effort for the implementation. The business goal is easy integration with new business partners."  \n  \nand the following architectural view   \n![image][image]  \n	1010	0	0	AdventureBuilderModuleOne
1016	t	Consider the following view of the Adventure Builder system   \n![image][image]  \n In this view it is possible to reason that	1016	0	0	AdventureBuilderComponentAndConnectorSecond
1017	t	In the Publish-Subscribe architectural style	1017	0	0	PublishSubscribe
1018	t	Which architectural style is adequate for planning incremental releases?	1018	0	0	UsesOne
1020	t	A connector may be attached to components of different types because	1020	0	0	ConnectorAttach
1026	t	Consider the following view of the Adventure Builder system   \n![image][image]  \n This view **does not** apply the architectural style	1026	0	0	AdventureBuilderComponentAndConnectorThird
1027	t	In a layered architecture composed by four layers, where the topmost layer is the layer number 1 and the bottommost layer is the layer number 4, which of the layers is more modifiable?	1027	0	0	LayeredAspectsDataModelOne
1028	t	Consider the following availability scenario for the Adventure Builder system  \n>"The Consumer Web site is available to the user 24x7. If an instance of OPC application fails, the fault is detected and the system administrator is notified in 30 seconds; the system continues taking order requests; another OPC instance is created; and data remains in consistent state."  \n  \nand the following architectural view   \n![image][image]  \n	1028	0	0	AdventureBuilderModuleTwo
1029	t	The Peer-to-Peer architectural style provides high scalability and availability. In the context of a file sharing system	1029	0	0	PeerToPeerSpace
1030	t	The quality(ies) that is(are) more relevant to views of the component-and-connector viewtype is(are):	1030	0	0	ComponentViewType
1036	t	In the context of the *Graphite* case study, consider the following view that represents the internal behavior of the *Carbon* component, where the components `r1,... , rn, w` are threads and `q1, ..., qn` are buffers. This view shows the Graphite's architecture support of   \n![image][image]  \n	1036	0	0	GraphitePerformanceScenario
1037	t	Consider the architectural solutions for microservices architectures that use the event sourcing technique. This technique has the following advantage	1037	0	0	MicroservicesOne
1039	t	Consider the following decomposition view of the Catalog of DVD case study.   \n![image][image]  \n	1039	0	0	DVDTopDecomposition
1040	t	Consider the architecture of the Morrison's OrderPad. The decision whether use a Native application or HTML5 for the implementation of the client in the Pad was taken because	1040	0	0	OrderPadPortability
1046	t	In the context of the *Graphite* case study, consider the following view that represents the internal behavior of the *Carbon* component, where the components `r1,... , rn, w` are threads and `q1, ..., qn` are buffers. The port *read*, which provides an interface to read the data points stored in the queue, can be used, in an enrichment of the view, to illustrate   \n![image][image]  \n	1046	0	0	GraphiteAvailabilityScenario
1047	t	In the Amazon Silk browser	1047	0	0	Silk
1048	t	Suppose that an architect needs to decide whether to follow a modular monolith architecture or a microservices architecture for a new large system. The system to be developed has a complex logic and high volume of requests.	1048	0	0	MicroservicesTwo
1049	t	In the web page of the NGINX HTTP server can be read  \n>"NGINX is a free, open-source, high-performance HTTP server and reverse proxy, as well as an IMAP/POP3 proxy server. (...) Unlike traditional servers, NGINX doesn't rely on threads to handle requests. Instead it uses a much more scalable event-driven (asynchronous) architecture. This architecture uses small, but more importantly, predictable amounts of memory under load."  \n  \nAccording to the above description the most adequate architectural style to represent the performance qualities of NGINX is	1049	0	0	CommunicatingProcesses
1050	t	Consider the following decomposition views of the Catalog of DVD case study were the *Autocomplete* module is implemented in javascript and executes in a browser.   \n![image][image]  \n	1050	0	0	DVDAutocomplete
1056	t	Consider the architecture of the Morrison's OrderPad. In the description of the system can be read:  \n>"The pilot version included some architectural short-cuts that would not work with the full complement of stores. One of these was using a file-transfer to send data to the mainframe rather than MQ, which wouldn't perform well once many stores were active."  \n  \nThis approach means that	1056	0	0	OrderPadIterative
1057	t	Consider a stakeholder that is particularly concerned about the total cost of the project. When it comes to describing the system using allocation viewtypes is interested in	1057	0	0	AllocationStylesCost
1058	t	Consider the following generalization view of the Catalog of DVD case study.   \n![image][image]  \n	1058	0	0	DVDGeneralization
1059	t	In the context of the *Graphite* case study, consider the following application-specific types that are used in a view to represent the internal behavior of the *Webapp* component.   \n![image][image]  \n This view can show that the architecture fulfills	1059	0	0	GraphiteWebapp
1060	t	Consider the architectural solutions for microservices architectures that use the Command Query Responsibility Segregation (CQRS) technique in the context of Event Sourcing. This technique has the following disadvantage	1060	0	0	MicroservicesThree
1082	t	Consider the following description of *Memcached*, which is adapted from its Wiki:  \n>"Memcached is an in-memory key-value store for small chunks of arbitrary data from results of database calls, API calls, or page rendering. It is made up of:  \n-  Client software, which is given a list of available memcached servers.  \n-  A client-based hashing algorithm, which chooses a server based on the "key" input.  \n-  Server software, which stores your values with their keys into an internal hash table.  \n-  Server algorithms, which determine when to throw out old data (if out of memory), or reuse memory.  \n"  \n  \nSuppose that you want to present an architectural view for *Memcached* that represents the above information. Which view is more adequate?	1082	0	0	ModuleStyleOne
1083	t	The Generalization architectural style of the module viewtype can be use to support the evolution of a system	1083	0	0	ModuleStyleThree
1084	t	According to the document that describes the architecture of web services:  \n>"Another critical piece of any distributed system is a load balancer. Load balancers are a principal part of any architecture, as their role is to distribute load across a set of nodes responsible for servicing requests. This allows multiple nodes to transparently service the same function in a system. Their main purpose is to handle a lot of simultaneous connections and route those connections to one of the request nodes, allowing the system to scale to service more requests by just adding nodes."  \n  \nBased on this description, what is the best way to represent the architecture of a system that is using a *load balancer*?	1084	0	0	ComponentAndConnectorViewtypeOne
1085	t	The repository architectural style provides performance because	1085	0	0	ComponentAndConnectorStyleOne
1086	t	Consider the following excerpt about the Scalable web architecture and distributed systems case study about two different possible implementations of a global cache  \n>"There are two common forms of global caches (...), when a cached response is not found in the cache, the cache itself becomes responsible for retrieving the missing piece of data from the underlying store. (...) it is the responsibility of request nodes to retrieve any data that is not found in the cache."  \n  \n	1086	0	0	ScalableArchitectureOne
1087	t	Consider the module viewtype views of the DVDCatalog application. The architect knows about a new requirement  \n>"To allow the share of catalogs with family and friends, including some access control."  \n  \nThis requirement requires	1087	0	0	DVDCatalogOne
1088	t	Typically, Instant Messaging clients have a window to list the contacts of the user, and show in that window the status of each contact (whether it is available, unavailable, busy, etc). Given that the status of a contact may be changed at any time, and that the contact's status is given by the Instant Messaging application of that contact, which architectural style represents best the interaction pattern between these components?	1088	0	0	ComponentAndConnectorStyleThree
1089	t	In the software architecture of a system, the Deployment view is best suited for	1089	0	0	AllocationOne
1090	t	Command Query Responsibility Segregation (CQRS) technique uses the following architectural styles	1090	0	0	EventSourcingOne
1091	t	Consider the following architectural view of the Adventure Builder system, designed around the Order Processing Center  \n![image][image]  \nThe views **does not** allow the reason about the quality of	1091	0	0	AdventureBuilderOne
1092	t	In the Graphite system, in order to improve performance the component *carbon* do not write directly on disk, it uses a buffer instead:	1092	0	0	GraphiteOne
1093	t	One of the evolutions in the development of web applications was the appearance of *mashups*, which are described in Wikipedia as follows:  \n>"In web development, a mashup is a web page or application that uses and combines data, presentation or functionality from two or more sources to create new services."  \n  \nKnowing that the sources used by *mashups* do not know about the existence of the *mashups* and that they change frequently, forcing the adaptation of the *mashups* to accommodate those changes, what is the best architecture to minimize the effects of those changes?	1093	0	0	WebApplicationsOne
1094	t	In the description of Chrome case study we can read:  \n*Chrome maintains a single instance of the resource dispatcher, which is shared across all render processes, and runs within the browser kernel process.*  \nThe *Resource Dispatcher* contributes to the performance quality because it implements a tactic of	1094	0	0	ChromeOne
1095	t	In the description of the SocialCalc case study can be read:  \n>"Therefore, on browsers with support for CSS3, we use the box-shadow property to represent multiple peer cursors in the same cell."  \n  \nThis corresponds to the application of	1095	0	0	SocialCalcOne
1096	t	Consider the architecture of the Morrison's OrderPad. In the description of the system can be read:  \n>"One of these was using a file-transfer to send data to the mainframe rather than MQ, which wouldn't perform well once many stores were active."  \n  \nThis approach means that	1096	0	0	OrderPadOne
1097	t	Consider the following decomposition of a domain model into 3 aggregates.  \n![image][image]  \n	1097	0	0	AggregatesOne
1098	t	Considering the availability architectural quality and the tactics of ping/echo and heartbeat	1098	0	0	AvailabilityOne
1121	t	Suppose that you are implementing a module in a system that has a two layered architecture. Knowing that your module belongs to the upper layer (assuming the usual notation for the layer style), this means that you	1121	0	0	ModuleStyleTwo
1123	t	Consider the concept of interface delegation	1123	0	0	ComponentAndConnectorViewtypeTwo
1124	t	In the Service Oriented Architecture style it is common to have a specialized component, named *Enterprise Service Bus* (ESB). The goal of using of an ESB in a system is	1124	0	0	ComponentAndConnectorStyleTwo
1125	t	In the description of the Gnutella system can be read:  \n>"The topology of the system changes at runtime as peer components connect and disconnect to the network."  \n  \n	1125	0	0	ComponentAndConnectorStyleFour
1126	t	Consider the install architectural style of the allocation viewtype.	1126	0	0	AllocationTwo
1127	t	Web applications went through several evolutions over the last years. One of those evolutions was to make their user interfaces more sophisticated, by leveraging on new technologies available in the browsers, such as, for example, Javascript, to provide a more satisfying user experience. What were the most visible consequences of such an evolution on the typical software architecture of a web application?	1127	0	0	WebApplicationsTwo
1129	t	Consider a scenario for performance where the arrival of events is stochastic with a distribution where there are peeks of events but the arrival of events over a long period is uniform. The best tactic to apply is	1129	0	0	PerformanceTwo
1130	t	The main tactic associated with the layered architectural style is:	1130	0	0	ModifiabilityTwo
1131	t	Consider the following decomposition of a domain model into 3 aggregates. If, instead of this decomposition, `Customer` and `Order` were in the same aggregate  \n![image][image]  \n	1131	0	0	AggregatesTwo
1132	t	Consider the architectural solutions for microservices architectures that use the Command Query Responsibility Segregation (CQRS) technique in the context of Event Sourcing. This technique has the following disadvantage	1132	0	0	EventSourcingTwo
1133	t	Consider the kind of relations between components and modules.	1133	0	0	ModuleViewTypeTwo
1134	t	Consider the use of a proxy to collapse requests. This corresponds to a tactic of	1134	0	0	ScalableArchitectureTwo
1135	t	Consider the module viewtype views of the DVDCatalog application. The architect knows about a new requirement  \n>"To support multi-platform (Mac, Windows, Linux)"  \n  \nThis requirement requires a change of	1135	0	0	DVDCatalogTwo
1136	t	Consider the following performance/scalability scenario for the Adventure Builder system  \n>"Up to 500 users click to see the catalog of adventure packages following a random distribution over 1 minute; the system is under normal operating conditions; the maximal latency to serve the first page of content is under 5 seconds; average latency for same is less than 2 seconds. If required, the system should easily support an increase in the number of simultaneous requests while maintaining the same latency per request."  \n  \nand the following architectural view  \n![image][image]  \n	1136	0	0	AdventureBuilderTwo
1137	t	The *Composer UI* component of Graphite system, described as - *Graphite's Composer UI provides a point-and-click method to create a graph from which you can simply copy and paste the URL* - to be effective needs to show to the user the changes she performs in the graph such that she has immediate feedback about the result of the changes. To do so, the system needs to implement the tactics of	1137	0	0	GraphiteTwo
1138	t	In the description of the Chrome case study you can read:  \n*Typing in the Omnibox (URL) bar triggers high-likelihood suggestions, which may similarly kick off a DNS lookup, TCP pre-connect, and even prerender the page in a hidden tab.*  \nThis description refers to the qualities of	1138	0	0	ChromeTwo
1139	t	Consider the architectural views for the SocialCalc system. In the case description can be read:  \n>"The save format is in standard MIME multipart/mixed format, consisting of four text/plain; charset=UTF-8 parts, each part containing  \n-delimited text with colon-delimited data fields. The parts are..."  \n  \nFrom the above excerpt can be inferred the need to have	1139	0	0	SocialCalcTwo
1140	t	Frank Buschmann defines Featuritis as  \n>"Featuritis is the tendency to trade functional coverage for quality - the more functions the earlier they're delivered, the better."  \n  \nIn the OrderPad system the architect regretted not getting performance tests going earlier. The OrderPad system	1140	0	0	OrderPadTwo
1156	t	Which quality, or qualities, of the Graphite system are described by the sentence: *Graphite's Composer UI provides a point-and-click method to create a graph from which you can simply copy and paste the URL.*	1156	0	0	GPComposerUIQuality
1157	t	Which quality(ies) of Chrome can be inferred from the sentence below?  \n>"By contrast, Chrome works on a multi-process model, which provides process and memory isolation, and a tight security sandbox for each tab. In an increasingly multi-core world, the ability to isolate the processes as well as shield each open tab from other misbehaving pages alone proves that Chrome has a significant performance edge over the competition. In fact, it is important to note that most other browsers have followed suit, or are in the process of migrating to similar architecture."  \n  \n	1157	0	0	ChromeQualities
1158	t	In the description of the Microservices Architecture by James Lewis and Martin Fowler can be read:  \n>"Microservice teams would expect to see (...) for each individual service such as dashboards showing up/down status and a variety of operational and business relevant metrics. Details on circuit breaker status, current throughput and latency are other examples we often encounter in the wild."  \n  \nWhich quality is being referred?	1158	0	0	MicroservicesMonitorability
1159	t	In the description of the *How Netflix works* can be read:  \n>"The Netflix app or website determines what particular device you are using to watch, and fetches the exact file for that show meant to specially play on your particular device, with a particular video quality based on how fast your internet is at that moment."  \n  \nWhich corresponds to the application of the following tactic	1159	0	0	NetflixTacticsOne
1160	t	To which performance tactic can a load balancer be associated?	1160	0	0	LoadBalancer
1176	t	In the Graphite system the component *carbon* provides to *webapp* components an access interface to the *buffers* in order to improve the quality of	1176	0	0	GraphiteScenarioTacticsOne
1177	t	Which performance tactic is referred in the following description of Chrome?  \n>"The ability of the browser to optimize the order, priority, and latency of each network resource is one of the most critical contributors to the overall user experience. You may not be aware of it, but Chrome's network stack is, quite literally, getting smarter every day, trying to hide or decrease the latency cost of each resource: it learns likely DNS lookups, it remembers the topology of the web, it pre-connects to likely destination targets, and more. From the outside, it presents itself as a simple resource fetching mechanism, but from the inside it is an elaborate and a fascinating case study for how to optimize web performance and deliver the best experience to the user."  \n  \n	1177	0	0	ChromeTactics
1178	t	In the description of the Microservices Architecture by James Lewis and Martin Fowler can be read:  \n>"As well as the fact that services are independently deployable and scalable, each service also provides a firm module boundary, even allowing for different services to be written in different programming languages. They can also be managed by different teams."  \n  \nFor this description it is relevant to consider the software architecture concept(s) of	1178	0	0	MicroservicesModuleAndComponent
1179	t	In the description of the *How Netflix works* can be read:  \n>"What CDNs basically do is, they take the original website and the media content it contains, and copy it across hundreds of servers spread all over the world. So when, say, you log in from Budapest, instead of connecting to the main Netflix server in the United States it will load a ditto copy of it from a CDN server that is the closest to Budapest."  \n  \nWhich corresponds to the application of the following tactic	1179	0	0	NetflixTacticsTwo
1180	t	To which performance tactic can the use of queues be associated?	1180	0	0	Queues
1186	t	In the Chrome system the use of a process per tab results form the application of a tactic of	1186	0	0	ChromeTabSecurity
1187	t	In the description of the SocialCalc case study can be read:  \n>"As the user scrolls the spreadsheet through our custom-drawn scroll bars, we dynamically update the innerHTML of the pre-drawn &lttd&gt elements. This means we don't need to create or destroy any &lttr&gt or &lttd&gt elements in many common cases, which greatly speeds up response time."  \n  \nThis corresponds to the application of	1187	0	0	SocialCalcTacticsOne
1188	t	A response measure of a modifiability scenario is	1188	0	0	ModifiabilityResponseMeasure
1189	t	Designing an architecture	1189	0	0	IterativeDesign
1190	t	The Decomposition architectural style of the Module viewtype	1190	0	0	Decomposition
1196	t	There are several tactics to satisfy availability requirements, which may be applied depending on the concrete requirement that we want to satisfy. Assuming that you want to detect faults of type *response* in your system, which tactic is more adequate?	1196	0	0	AvailabilityVotingFirst
1197	t	Consider an architecturally significant requirement (ASR) that has a low impact on the architecture but a high business value	1197	0	0	LowArchitecturalImpact
1198	t	A function call is not necessarily a uses relation of the Uses architectural style of the Module viewtype because	1198	0	0	UsesCalls
1199	t	In the description of the Chrome system can be read  \n>"The goal of the predictor is to evaluate the likelihood of its success, and then to trigger the activity if resources are available."  \n  \nThe above sentence refer to	1199	0	0	ChromePredictor
1200	t	In the description of the SocialCalc case study can be read:  \n>"A simple improvement is for each client to broadcast its cursor position to other users, so everyone can see which cells are being worked on."  \n  \nThis sentence describes a tactic for usability which is	1200	0	0	SocialCalcTacticsTwo
1206	t	In the component-and-connector viewtype connectors can be complex, which means that they provide a rich set of qualities to the interaction between the components that they connect. These complex connectors can be documented in another view using a set of components interacting through simpler connectors.	1206	0	0	ComponentAndConnectorOne
1207	t	Consider that you intend to develop a system where it is necessary to change the emails received by the server (for instance, to remove potential virus or URLs for phishing sites). The goal is that each email is processed by this system before it is sent to other servers or it is stored locally. Additionally, the system should be easily modified to support new kinds of transformations. Which style is more suitable to satisfy these requirements?	1207	0	0	PipesFilters
1208	t	Consider the Service-Oriented Architecture architectural style	1208	0	0	SOA
1209	t	Consider the Layered architectural style of the Module viewtype	1209	0	0	Layered
1216	t	The Tiers architectural style	1216	0	0	Tiers
1217	t	Using the Aspects architectural style promotes the modifiability of a system because	1217	0	0	Aspects
1218	t	Consider the peer-to-peer architectural style	1218	0	0	PeerToPeer
1219	t	In Facebook it is not possible to have the information about more that one bilion users in a single disk. Therefore, a sharding technique is applied, where the persistent information is split between several database servers, and requests are routed to the right servers for queries and updates. Additionally, due to performance requirements, the information needs to be replicated in several servers. To describe this architecture	1219	0	0	DataModelTwo
1220	t	Consider the Component-and-Connector viewtype	1220	0	0	ComponentAndConnectorTwo
1226	t	In Chrome system, to show that it provides availability when the javascript code executing in a tab crashes, and security when the javascript code executing in a tab tries to access the information in another tab, it is necessary to design	1226	0	0	ChromeCommunicatingProcesses
1227	t	How can be guaranteed that the update of an aggregate and the publishing of an event about the update is an atomic action	1227	0	0	AggregateAndEventSourcing
1228	t	Consider a stakeholder that is particularly concerned about the total cost of the project. When it comes to describing the system using allocation viewtypes she is interested in	1228	0	0	AllocationStylesCost
1229	t	Consider the module viewtype views of the DVDCatalog application. The architect knows about a new requirement  \n>"The application should support other kinds of catalogs (CDs, games, books, ...)."  \n  \nThis requirement requires a change of	1229	0	0	DVDCatalogMeta
1230	t	In Graphite system, in order to generate up-to-date graphs, the *WebApp* component interacts with the *Carbon* component. The interaction between these two components follows the architectural style	1230	0	0	GraphiteClientServer
1236	t	In Graphite system the *receiver* and the *writer threads* support asynchronous writing of metrics to optimize disk accesses. The interaction between these two components follow the architectural style	1236	0	0	GraphiteCommunicationProcesses
1237	t	In Chrome system, to show that it provides mobility qualities by managing the number of tab, it is necessary to use	1237	0	0	ChromeDynamicReconfiguration
1238	t	Event Sourcing is a technique that use the following architectural style	1238	0	0	EventSourcing
1239	t	The Work-assignment is an architectural style of the allocation viewtype, where	1239	0	0	WorkAssignment
1240	t	Consider the module viewtype views of the Catalog of DVD application. The architect knows about a new requirement  \n>"To support iPhone, iPad, Android versions with sync, which allows offline use of the application in the mobile device and data synchronization to occur when a connection is available"  \n  \nThis requirement requires a change of	1240	0	0	DVDCatalogMobile
\.


--
-- Data for Name: quiz_answers; Type: TABLE DATA; Schema: public; Owner: pedro
--

COPY public.quiz_answers (id, answer_date, assigned_date, completed, quiz_id, user_id) FROM stdin;
1	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	2
2	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	7
3	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	15
4	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	18
5	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	25
6	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	28
7	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	32
8	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	51
9	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	54
10	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	56
11	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	59
12	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	24
13	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	33
14	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	40
15	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	47
16	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	10
17	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	16
18	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	20
19	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	38
20	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	41
21	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	45
22	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	49
23	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	55
24	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	22
25	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	26
26	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	4
27	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	42
28	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	53
29	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	9
30	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	14
31	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	34
32	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	50
33	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	57
34	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	60
35	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	5
36	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	12
37	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	17
38	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	43
39	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	44
40	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	39
41	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	21
42	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	37
43	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	48
44	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	58
45	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	30
46	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	67
47	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	84
48	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	95
49	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	97
50	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	61
51	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	62
52	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	63
53	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	65
54	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	66
55	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	68
56	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	69
57	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	70
58	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	71
59	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	72
60	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	78
61	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	81
62	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	86
63	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	88
64	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	90
65	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	94
66	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	99
67	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	100
68	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	102
69	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	103
70	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	93
71	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	96
72	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	87
73	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	64
74	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	101
75	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	75
76	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	82
77	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	104
78	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	85
79	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	83
80	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	74
81	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	89
82	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	77
83	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	80
84	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	79
85	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	110
86	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	123
87	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	130
88	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	114
89	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	126
90	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	106
91	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	109
92	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	129
93	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	144
94	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	148
95	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	122
96	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	127
97	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	135
98	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	142
99	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	105
100	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	107
101	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	108
102	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	111
103	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	112
104	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	113
105	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	115
106	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	116
107	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	117
108	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	118
109	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	119
110	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	120
111	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	121
112	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	124
113	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	125
114	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	128
115	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	131
116	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	132
117	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	133
118	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	134
119	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	136
120	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	137
121	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	138
122	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	139
123	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	140
124	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	141
125	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	143
126	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	145
127	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	146
128	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	147
129	2013-01-01 00:00:00	2013-01-01 00:00:00	t	7	149
130	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	233
131	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	281
132	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	278
133	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	291
134	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	285
135	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	179
136	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	279
137	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	338
138	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	326
139	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	334
140	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	323
141	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	348
142	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	343
143	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	336
144	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	350
145	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	347
146	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	345
147	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	354
148	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	330
149	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	325
150	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	335
151	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	368
152	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	364
153	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	377
154	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	380
155	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	371
156	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	362
157	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	379
158	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	366
159	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	369
160	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	359
161	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	384
162	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	373
163	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	357
164	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	378
165	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	363
166	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	376
167	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	372
168	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	383
169	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	380
170	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	382
171	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	371
172	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	345
173	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	313
174	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	334
175	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	377
176	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	369
177	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	359
178	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	363
179	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	364
180	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	356
181	2015-01-01 00:00:00	2015-01-01 00:00:00	t	39	335
182	2015-01-01 00:00:00	2015-01-01 00:00:00	t	39	347
183	2015-01-01 00:00:00	2015-01-01 00:00:00	t	39	323
184	2015-01-01 00:00:00	2015-01-01 00:00:00	t	39	338
185	2015-01-01 00:00:00	2015-01-01 00:00:00	t	39	355
186	2015-01-01 00:00:00	2015-01-01 00:00:00	t	39	358
187	2015-01-01 00:00:00	2015-01-01 00:00:00	t	40	381
188	2015-01-01 00:00:00	2015-01-01 00:00:00	t	41	345
189	2015-01-01 00:00:00	2015-01-01 00:00:00	t	41	350
190	2015-01-01 00:00:00	2015-01-01 00:00:00	t	42	369
191	2015-01-01 00:00:00	2015-01-01 00:00:00	t	42	357
192	2015-01-01 00:00:00	2015-01-01 00:00:00	t	43	383
193	2015-01-01 00:00:00	2015-01-01 00:00:00	t	43	380
194	2015-01-01 00:00:00	2015-01-01 00:00:00	t	43	376
195	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	368
196	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	377
197	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	310
198	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	326
199	2015-01-01 00:00:00	2015-01-01 00:00:00	t	46	371
200	2015-01-01 00:00:00	2015-01-01 00:00:00	t	47	325
201	2015-01-01 00:00:00	2015-01-01 00:00:00	t	47	340
202	2015-01-01 00:00:00	2015-01-01 00:00:00	t	47	381
203	2015-01-01 00:00:00	2015-01-01 00:00:00	t	48	323
204	2015-01-01 00:00:00	2015-01-01 00:00:00	t	48	369
205	2015-01-01 00:00:00	2015-01-01 00:00:00	t	48	376
206	2015-01-01 00:00:00	2015-01-01 00:00:00	t	49	350
207	2015-01-01 00:00:00	2015-01-01 00:00:00	t	49	343
208	2015-01-01 00:00:00	2015-01-01 00:00:00	t	49	380
209	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	336
210	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	326
211	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	362
212	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	330
213	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	310
214	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	377
215	2015-01-01 00:00:00	2015-01-01 00:00:00	t	52	371
216	2015-01-01 00:00:00	2015-01-01 00:00:00	t	53	364
217	2015-01-01 00:00:00	2015-01-01 00:00:00	t	53	381
218	2015-01-01 00:00:00	2015-01-01 00:00:00	t	54	334
219	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	313
220	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	380
221	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	326
222	2015-01-01 00:00:00	2015-01-01 00:00:00	t	58	381
223	2015-01-01 00:00:00	2015-01-01 00:00:00	t	59	371
224	2015-01-01 00:00:00	2015-01-01 00:00:00	t	61	323
225	2015-01-01 00:00:00	2015-01-01 00:00:00	t	61	380
226	2015-01-01 00:00:00	2015-01-01 00:00:00	t	61	379
227	2015-01-01 00:00:00	2015-01-01 00:00:00	t	62	313
228	2015-01-01 00:00:00	2015-01-01 00:00:00	t	62	368
229	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	402
230	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	409
231	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	405
232	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	400
233	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	401
234	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	411
235	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	410
236	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	418
237	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	407
238	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	414
239	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	404
240	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	408
241	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	406
242	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	417
243	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	413
244	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	440
245	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	420
246	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	426
247	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	429
248	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	431
249	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	428
250	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	421
251	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	430
252	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	427
253	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	422
254	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	424
255	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	425
256	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	436
257	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	435
258	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	437
259	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	442
260	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	438
261	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	443
262	2016-01-01 00:00:00	2016-01-01 00:00:00	t	66	392
263	2016-01-01 00:00:00	2016-01-01 00:00:00	t	66	408
264	2016-01-01 00:00:00	2016-01-01 00:00:00	t	66	410
265	2016-01-01 00:00:00	2016-01-01 00:00:00	t	66	424
266	2016-01-01 00:00:00	2016-01-01 00:00:00	t	66	436
267	2016-01-01 00:00:00	2016-01-01 00:00:00	t	66	420
268	2016-01-01 00:00:00	2016-01-01 00:00:00	t	67	397
269	2016-01-01 00:00:00	2016-01-01 00:00:00	t	67	389
270	2016-01-01 00:00:00	2016-01-01 00:00:00	t	67	395
271	2016-01-01 00:00:00	2016-01-01 00:00:00	t	67	401
272	2016-01-01 00:00:00	2016-01-01 00:00:00	t	68	416
273	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	392
274	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	402
275	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	411
276	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	398
277	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	395
278	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	410
279	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	404
280	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	440
281	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	425
282	2016-01-01 00:00:00	2016-01-01 00:00:00	t	72	415
283	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	400
284	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	402
285	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	409
286	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	410
287	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	420
288	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	425
289	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	397
290	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	395
291	2016-01-01 00:00:00	2016-01-01 00:00:00	t	77	415
292	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	395
293	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	402
294	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	425
295	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	410
296	2016-01-01 00:00:00	2016-01-01 00:00:00	t	80	423
297	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	395
298	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	409
299	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	470
300	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	476
301	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	480
302	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	485
303	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	471
304	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	500
305	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	472
306	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	483
307	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	469
308	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	473
309	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	478
310	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	482
311	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	493
312	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	495
313	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	497
314	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	498
315	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	499
316	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	479
317	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	496
318	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	487
319	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	494
320	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	484
321	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	512
322	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	528
323	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	534
324	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	508
325	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	515
326	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	516
327	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	511
328	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	513
329	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	514
330	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	529
331	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	502
332	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	506
333	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	510
334	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	517
335	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	522
336	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	523
337	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	524
338	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	527
339	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	531
340	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	532
341	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	499
342	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	469
343	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	476
344	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	497
345	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	492
346	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	529
347	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	471
348	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	507
349	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	508
350	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	534
351	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	458
352	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	474
353	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	517
354	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	470
355	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	513
356	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	470
357	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	522
358	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	493
359	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	512
360	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	531
361	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	479
362	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	508
363	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	483
364	2017-01-01 00:00:00	2017-01-01 00:00:00	t	93	467
365	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	459
366	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	469
367	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	493
368	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	514
369	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	472
370	2017-01-01 00:00:00	2017-01-01 00:00:00	t	96	500
371	2017-01-01 00:00:00	2017-01-01 00:00:00	t	96	509
372	2017-01-01 00:00:00	2017-01-01 00:00:00	t	96	511
373	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	480
374	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	499
375	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	508
376	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	485
377	2017-01-01 00:00:00	2017-01-01 00:00:00	t	98	470
378	2017-01-01 00:00:00	2017-01-01 00:00:00	t	98	527
379	2017-01-01 00:00:00	2017-01-01 00:00:00	t	99	528
380	2017-01-01 00:00:00	2017-01-01 00:00:00	t	99	531
381	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	493
382	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	471
383	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	472
384	2017-01-01 00:00:00	2017-01-01 00:00:00	t	101	478
385	2017-01-01 00:00:00	2017-01-01 00:00:00	t	102	511
386	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	458
387	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	508
388	2017-01-01 00:00:00	2017-01-01 00:00:00	t	105	529
389	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	478
390	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	517
391	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	530
392	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	518
393	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	519
394	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	493
395	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	472
396	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	520
397	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	471
398	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	479
399	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	534
400	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	496
401	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	480
402	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	483
403	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	533
404	2017-01-01 00:00:00	2017-01-01 00:00:00	t	109	508
405	2017-01-01 00:00:00	2017-01-01 00:00:00	t	109	532
406	2017-01-01 00:00:00	2017-01-01 00:00:00	t	110	501
407	2017-01-01 00:00:00	2017-01-01 00:00:00	t	111	467
408	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	565
409	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	557
410	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	562
411	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	564
412	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	569
413	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	561
414	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	560
415	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	576
416	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	573
417	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	578
418	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	579
419	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	577
420	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	570
421	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	575
422	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	558
423	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	599
424	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	590
425	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	607
426	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	600
427	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	613
428	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	611
429	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	591
430	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	595
431	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	601
432	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	604
433	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	608
434	2018-01-01 00:00:00	2018-01-01 00:00:00	t	114	565
435	2018-01-01 00:00:00	2018-01-01 00:00:00	t	114	549
436	2018-01-01 00:00:00	2018-01-01 00:00:00	t	114	569
437	2018-01-01 00:00:00	2018-01-01 00:00:00	t	114	544
438	2018-01-01 00:00:00	2018-01-01 00:00:00	t	114	606
439	2018-01-01 00:00:00	2018-01-01 00:00:00	t	114	611
440	2018-01-01 00:00:00	2018-01-01 00:00:00	t	114	563
441	2018-01-01 00:00:00	2018-01-01 00:00:00	t	115	590
442	2018-01-01 00:00:00	2018-01-01 00:00:00	t	115	607
443	2018-01-01 00:00:00	2018-01-01 00:00:00	t	115	576
444	2018-01-01 00:00:00	2018-01-01 00:00:00	t	115	597
445	2018-01-01 00:00:00	2018-01-01 00:00:00	t	115	594
446	2018-01-01 00:00:00	2018-01-01 00:00:00	t	115	559
447	2018-01-01 00:00:00	2018-01-01 00:00:00	t	115	588
448	2018-01-01 00:00:00	2018-01-01 00:00:00	t	120	544
449	2018-01-01 00:00:00	2018-01-01 00:00:00	t	120	590
450	2018-01-01 00:00:00	2018-01-01 00:00:00	t	122	559
451	2018-01-01 00:00:00	2018-01-01 00:00:00	t	122	576
452	2018-01-01 00:00:00	2018-01-01 00:00:00	t	123	545
453	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	572
454	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	577
455	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	544
456	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	546
457	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	570
458	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	544
459	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	546
460	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	545
461	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	576
462	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	577
463	2018-01-01 00:00:00	2018-01-01 00:00:00	t	132	606
464	2018-01-01 00:00:00	2018-01-01 00:00:00	t	133	578
465	2018-01-01 00:00:00	2018-01-01 00:00:00	t	134	576
466	2018-01-01 00:00:00	2018-01-01 00:00:00	t	134	544
467	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	565
468	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	575
469	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	577
470	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	590
471	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	600
472	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	13
473	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	11
474	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	35
475	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	1
476	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	8
477	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	19
478	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	27
479	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	29
480	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	36
481	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	52
482	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	3
483	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	6
484	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	23
485	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	31
486	2011-01-01 00:00:00	2011-01-01 00:00:00	t	2	46
487	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	73
488	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	91
489	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	92
490	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	98
491	2013-01-01 00:00:00	2013-01-01 00:00:00	t	6	76
492	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	199
493	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	203
494	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	207
495	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	209
496	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	211
497	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	215
498	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	226
499	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	227
500	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	236
501	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	240
502	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	202
503	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	238
504	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	245
505	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	201
506	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	205
507	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	206
508	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	230
509	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	231
510	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	234
511	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	243
512	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	193
513	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	194
514	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	195
515	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	196
516	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	197
517	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	198
518	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	200
519	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	204
520	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	208
521	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	210
522	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	212
523	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	213
524	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	214
525	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	216
526	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	217
527	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	218
528	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	219
529	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	220
530	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	221
531	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	222
532	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	223
533	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	224
534	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	225
535	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	228
536	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	229
537	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	232
538	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	235
539	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	237
540	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	239
541	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	241
542	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	242
543	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	244
544	2014-01-01 00:00:00	2014-01-01 00:00:00	t	8	246
545	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	252
546	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	255
547	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	265
548	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	270
549	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	271
550	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	276
551	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	287
552	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	296
553	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	304
554	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	247
555	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	249
556	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	250
557	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	251
558	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	253
559	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	254
560	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	257
561	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	259
562	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	260
563	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	262
564	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	264
565	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	267
566	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	269
567	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	272
568	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	273
569	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	274
570	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	275
571	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	280
572	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	282
573	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	283
574	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	284
575	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	288
576	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	290
577	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	293
578	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	295
579	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	300
580	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	306
581	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	307
582	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	248
583	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	256
584	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	261
585	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	263
586	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	266
587	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	268
588	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	277
589	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	279
590	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	286
591	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	292
592	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	297
593	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	298
594	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	299
595	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	302
596	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	305
597	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	258
598	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	289
599	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	294
600	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	301
601	2014-01-01 00:00:00	2014-01-01 00:00:00	t	9	303
602	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	197
603	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	224
604	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	285
605	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	287
606	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	226
607	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	175
608	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	290
609	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	154
610	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	157
611	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	248
612	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	200
613	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	206
614	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	162
615	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	208
616	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	264
617	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	271
618	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	215
619	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	216
620	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	170
621	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	171
622	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	281
623	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	283
624	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	228
625	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	288
626	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	289
627	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	232
628	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	233
629	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	299
630	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	301
631	2014-01-01 00:00:00	2014-01-01 00:00:00	t	10	245
632	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	247
633	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	203
634	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	256
635	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	278
636	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	177
637	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	223
638	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	291
639	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	159
640	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	160
641	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	172
642	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	227
643	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	286
644	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	234
645	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	156
646	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	158
647	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	199
648	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	161
649	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	255
650	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	258
651	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	164
652	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	263
653	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	210
654	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	166
655	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	265
656	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	269
657	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	168
658	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	270
659	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	241
660	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	297
661	2014-01-01 00:00:00	2014-01-01 00:00:00	t	11	304
662	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	324
663	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	328
664	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	332
665	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	340
666	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	342
667	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	346
668	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	349
669	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	352
670	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	353
671	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	329
672	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	333
673	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	337
674	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	339
675	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	341
676	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	344
677	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	351
678	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	356
679	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	327
680	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	331
681	2015-01-01 00:00:00	2015-01-01 00:00:00	t	36	355
682	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	358
683	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	360
684	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	361
685	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	365
686	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	367
687	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	375
688	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	381
689	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	382
690	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	370
691	2015-01-01 00:00:00	2015-01-01 00:00:00	t	37	374
692	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	332
693	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	344
694	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	315
695	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	337
696	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	376
697	2015-01-01 00:00:00	2015-01-01 00:00:00	t	38	381
698	2015-01-01 00:00:00	2015-01-01 00:00:00	t	39	327
699	2015-01-01 00:00:00	2015-01-01 00:00:00	t	39	341
700	2015-01-01 00:00:00	2015-01-01 00:00:00	t	39	346
701	2015-01-01 00:00:00	2015-01-01 00:00:00	t	39	348
702	2015-01-01 00:00:00	2015-01-01 00:00:00	t	39	354
703	2015-01-01 00:00:00	2015-01-01 00:00:00	t	39	361
704	2015-01-01 00:00:00	2015-01-01 00:00:00	t	40	371
705	2015-01-01 00:00:00	2015-01-01 00:00:00	t	40	358
706	2015-01-01 00:00:00	2015-01-01 00:00:00	t	40	360
707	2015-01-01 00:00:00	2015-01-01 00:00:00	t	40	361
708	2015-01-01 00:00:00	2015-01-01 00:00:00	t	40	365
709	2015-01-01 00:00:00	2015-01-01 00:00:00	t	40	339
710	2015-01-01 00:00:00	2015-01-01 00:00:00	t	40	340
711	2015-01-01 00:00:00	2015-01-01 00:00:00	t	40	356
712	2015-01-01 00:00:00	2015-01-01 00:00:00	t	41	331
713	2015-01-01 00:00:00	2015-01-01 00:00:00	t	41	351
714	2015-01-01 00:00:00	2015-01-01 00:00:00	t	41	352
715	2015-01-01 00:00:00	2015-01-01 00:00:00	t	41	349
716	2015-01-01 00:00:00	2015-01-01 00:00:00	t	41	355
717	2015-01-01 00:00:00	2015-01-01 00:00:00	t	41	335
718	2015-01-01 00:00:00	2015-01-01 00:00:00	t	41	325
719	2015-01-01 00:00:00	2015-01-01 00:00:00	t	41	318
720	2015-01-01 00:00:00	2015-01-01 00:00:00	t	41	354
721	2015-01-01 00:00:00	2015-01-01 00:00:00	t	42	315
722	2015-01-01 00:00:00	2015-01-01 00:00:00	t	42	363
723	2015-01-01 00:00:00	2015-01-01 00:00:00	t	42	374
724	2015-01-01 00:00:00	2015-01-01 00:00:00	t	42	342
725	2015-01-01 00:00:00	2015-01-01 00:00:00	t	42	343
726	2015-01-01 00:00:00	2015-01-01 00:00:00	t	42	353
727	2015-01-01 00:00:00	2015-01-01 00:00:00	t	42	324
728	2015-01-01 00:00:00	2015-01-01 00:00:00	t	42	328
729	2015-01-01 00:00:00	2015-01-01 00:00:00	t	43	323
730	2015-01-01 00:00:00	2015-01-01 00:00:00	t	43	312
731	2015-01-01 00:00:00	2015-01-01 00:00:00	t	43	334
732	2015-01-01 00:00:00	2015-01-01 00:00:00	t	43	348
733	2015-01-01 00:00:00	2015-01-01 00:00:00	t	43	375
734	2015-01-01 00:00:00	2015-01-01 00:00:00	t	43	384
735	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	362
736	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	373
737	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	309
738	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	329
739	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	332
740	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	347
741	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	330
742	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	341
743	2015-01-01 00:00:00	2015-01-01 00:00:00	t	44	382
744	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	327
745	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	333
746	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	337
747	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	338
748	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	370
749	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	344
750	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	378
751	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	379
752	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	313
753	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	366
754	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	364
755	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	367
756	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	336
757	2015-01-01 00:00:00	2015-01-01 00:00:00	t	45	372
758	2015-01-01 00:00:00	2015-01-01 00:00:00	t	46	364
759	2015-01-01 00:00:00	2015-01-01 00:00:00	t	46	331
760	2015-01-01 00:00:00	2015-01-01 00:00:00	t	46	335
761	2015-01-01 00:00:00	2015-01-01 00:00:00	t	46	352
762	2015-01-01 00:00:00	2015-01-01 00:00:00	t	46	356
763	2015-01-01 00:00:00	2015-01-01 00:00:00	t	46	318
764	2015-01-01 00:00:00	2015-01-01 00:00:00	t	46	351
765	2015-01-01 00:00:00	2015-01-01 00:00:00	t	46	358
766	2015-01-01 00:00:00	2015-01-01 00:00:00	t	46	349
767	2015-01-01 00:00:00	2015-01-01 00:00:00	t	47	360
768	2015-01-01 00:00:00	2015-01-01 00:00:00	t	47	354
769	2015-01-01 00:00:00	2015-01-01 00:00:00	t	47	355
770	2015-01-01 00:00:00	2015-01-01 00:00:00	t	47	365
771	2015-01-01 00:00:00	2015-01-01 00:00:00	t	47	339
772	2015-01-01 00:00:00	2015-01-01 00:00:00	t	48	324
773	2015-01-01 00:00:00	2015-01-01 00:00:00	t	48	328
774	2015-01-01 00:00:00	2015-01-01 00:00:00	t	48	353
775	2015-01-01 00:00:00	2015-01-01 00:00:00	t	48	315
776	2015-01-01 00:00:00	2015-01-01 00:00:00	t	48	361
777	2015-01-01 00:00:00	2015-01-01 00:00:00	t	48	342
778	2015-01-01 00:00:00	2015-01-01 00:00:00	t	49	363
779	2015-01-01 00:00:00	2015-01-01 00:00:00	t	49	334
780	2015-01-01 00:00:00	2015-01-01 00:00:00	t	49	374
781	2015-01-01 00:00:00	2015-01-01 00:00:00	t	49	375
782	2015-01-01 00:00:00	2015-01-01 00:00:00	t	49	383
783	2015-01-01 00:00:00	2015-01-01 00:00:00	t	49	384
784	2015-01-01 00:00:00	2015-01-01 00:00:00	t	49	357
785	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	366
786	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	379
787	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	327
788	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	329
789	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	367
790	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	333
791	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	337
792	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	338
793	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	372
794	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	378
795	2015-01-01 00:00:00	2015-01-01 00:00:00	t	50	348
796	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	345
797	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	312
798	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	313
799	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	368
800	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	309
801	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	341
802	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	332
803	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	370
804	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	373
805	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	344
806	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	347
807	2015-01-01 00:00:00	2015-01-01 00:00:00	t	51	382
808	2015-01-01 00:00:00	2015-01-01 00:00:00	t	52	331
809	2015-01-01 00:00:00	2015-01-01 00:00:00	t	52	335
810	2015-01-01 00:00:00	2015-01-01 00:00:00	t	52	352
811	2015-01-01 00:00:00	2015-01-01 00:00:00	t	52	350
812	2015-01-01 00:00:00	2015-01-01 00:00:00	t	52	355
813	2015-01-01 00:00:00	2015-01-01 00:00:00	t	53	340
814	2015-01-01 00:00:00	2015-01-01 00:00:00	t	53	351
815	2015-01-01 00:00:00	2015-01-01 00:00:00	t	53	354
816	2015-01-01 00:00:00	2015-01-01 00:00:00	t	53	365
817	2015-01-01 00:00:00	2015-01-01 00:00:00	t	53	349
818	2015-01-01 00:00:00	2015-01-01 00:00:00	t	54	324
819	2015-01-01 00:00:00	2015-01-01 00:00:00	t	54	357
820	2015-01-01 00:00:00	2015-01-01 00:00:00	t	54	369
821	2015-01-01 00:00:00	2015-01-01 00:00:00	t	54	374
822	2015-01-01 00:00:00	2015-01-01 00:00:00	t	54	375
823	2015-01-01 00:00:00	2015-01-01 00:00:00	t	54	383
824	2015-01-01 00:00:00	2015-01-01 00:00:00	t	54	315
825	2015-01-01 00:00:00	2015-01-01 00:00:00	t	54	361
826	2015-01-01 00:00:00	2015-01-01 00:00:00	t	54	376
827	2015-01-01 00:00:00	2015-01-01 00:00:00	t	55	323
828	2015-01-01 00:00:00	2015-01-01 00:00:00	t	55	363
829	2015-01-01 00:00:00	2015-01-01 00:00:00	t	55	328
830	2015-01-01 00:00:00	2015-01-01 00:00:00	t	55	342
831	2015-01-01 00:00:00	2015-01-01 00:00:00	t	55	348
832	2015-01-01 00:00:00	2015-01-01 00:00:00	t	55	384
833	2015-01-01 00:00:00	2015-01-01 00:00:00	t	55	356
834	2015-01-01 00:00:00	2015-01-01 00:00:00	t	55	358
835	2015-01-01 00:00:00	2015-01-01 00:00:00	t	55	353
836	2015-01-01 00:00:00	2015-01-01 00:00:00	t	55	339
837	2015-01-01 00:00:00	2015-01-01 00:00:00	t	55	343
838	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	360
839	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	336
840	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	373
841	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	341
842	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	345
843	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	378
844	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	379
845	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	367
846	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	333
847	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	372
848	2015-01-01 00:00:00	2015-01-01 00:00:00	t	56	347
849	2015-01-01 00:00:00	2015-01-01 00:00:00	t	58	361
850	2015-01-01 00:00:00	2015-01-01 00:00:00	t	58	362
851	2015-01-01 00:00:00	2015-01-01 00:00:00	t	58	365
852	2015-01-01 00:00:00	2015-01-01 00:00:00	t	58	340
853	2015-01-01 00:00:00	2015-01-01 00:00:00	t	58	352
854	2015-01-01 00:00:00	2015-01-01 00:00:00	t	58	354
855	2015-01-01 00:00:00	2015-01-01 00:00:00	t	58	350
856	2015-01-01 00:00:00	2015-01-01 00:00:00	t	58	355
857	2015-01-01 00:00:00	2015-01-01 00:00:00	t	59	335
858	2015-01-01 00:00:00	2015-01-01 00:00:00	t	59	360
859	2015-01-01 00:00:00	2015-01-01 00:00:00	t	59	358
860	2015-01-01 00:00:00	2015-01-01 00:00:00	t	59	364
861	2015-01-01 00:00:00	2015-01-01 00:00:00	t	59	331
862	2015-01-01 00:00:00	2015-01-01 00:00:00	t	59	349
863	2015-01-01 00:00:00	2015-01-01 00:00:00	t	59	351
864	2015-01-01 00:00:00	2015-01-01 00:00:00	t	60	383
865	2015-01-01 00:00:00	2015-01-01 00:00:00	t	60	357
866	2015-01-01 00:00:00	2015-01-01 00:00:00	t	60	334
867	2015-01-01 00:00:00	2015-01-01 00:00:00	t	60	339
868	2015-01-01 00:00:00	2015-01-01 00:00:00	t	60	336
869	2015-01-01 00:00:00	2015-01-01 00:00:00	t	60	342
870	2015-01-01 00:00:00	2015-01-01 00:00:00	t	60	376
871	2015-01-01 00:00:00	2015-01-01 00:00:00	t	60	353
872	2015-01-01 00:00:00	2015-01-01 00:00:00	t	60	356
873	2015-01-01 00:00:00	2015-01-01 00:00:00	t	61	343
874	2015-01-01 00:00:00	2015-01-01 00:00:00	t	61	363
875	2015-01-01 00:00:00	2015-01-01 00:00:00	t	61	328
876	2015-01-01 00:00:00	2015-01-01 00:00:00	t	61	369
877	2015-01-01 00:00:00	2015-01-01 00:00:00	t	61	374
878	2015-01-01 00:00:00	2015-01-01 00:00:00	t	61	375
879	2015-01-01 00:00:00	2015-01-01 00:00:00	t	61	384
880	2015-01-01 00:00:00	2015-01-01 00:00:00	t	62	310
881	2015-01-01 00:00:00	2015-01-01 00:00:00	t	62	326
882	2015-01-01 00:00:00	2015-01-01 00:00:00	t	62	330
883	2015-01-01 00:00:00	2015-01-01 00:00:00	t	62	367
884	2015-01-01 00:00:00	2015-01-01 00:00:00	t	62	337
885	2015-01-01 00:00:00	2015-01-01 00:00:00	t	62	377
886	2015-01-01 00:00:00	2015-01-01 00:00:00	t	62	344
887	2015-01-01 00:00:00	2015-01-01 00:00:00	t	62	372
888	2015-01-01 00:00:00	2015-01-01 00:00:00	t	62	341
889	2015-01-01 00:00:00	2015-01-01 00:00:00	t	62	378
890	2015-01-01 00:00:00	2015-01-01 00:00:00	t	63	370
891	2015-01-01 00:00:00	2015-01-01 00:00:00	t	63	348
892	2015-01-01 00:00:00	2015-01-01 00:00:00	t	63	325
893	2015-01-01 00:00:00	2015-01-01 00:00:00	t	63	366
894	2015-01-01 00:00:00	2015-01-01 00:00:00	t	63	332
895	2015-01-01 00:00:00	2015-01-01 00:00:00	t	63	333
896	2015-01-01 00:00:00	2015-01-01 00:00:00	t	63	338
897	2015-01-01 00:00:00	2015-01-01 00:00:00	t	63	373
898	2015-01-01 00:00:00	2015-01-01 00:00:00	t	63	347
899	2015-01-01 00:00:00	2015-01-01 00:00:00	t	63	382
900	2015-01-01 00:00:00	2015-01-01 00:00:00	t	63	327
901	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	403
902	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	415
903	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	416
904	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	419
905	2016-01-01 00:00:00	2016-01-01 00:00:00	t	64	412
906	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	444
907	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	434
908	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	423
909	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	432
910	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	433
911	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	439
912	2016-01-01 00:00:00	2016-01-01 00:00:00	t	65	441
913	2016-01-01 00:00:00	2016-01-01 00:00:00	t	66	439
914	2016-01-01 00:00:00	2016-01-01 00:00:00	t	66	432
915	2016-01-01 00:00:00	2016-01-01 00:00:00	t	67	388
916	2016-01-01 00:00:00	2016-01-01 00:00:00	t	67	431
917	2016-01-01 00:00:00	2016-01-01 00:00:00	t	67	434
918	2016-01-01 00:00:00	2016-01-01 00:00:00	t	67	412
919	2016-01-01 00:00:00	2016-01-01 00:00:00	t	67	398
920	2016-01-01 00:00:00	2016-01-01 00:00:00	t	68	422
921	2016-01-01 00:00:00	2016-01-01 00:00:00	t	68	405
922	2016-01-01 00:00:00	2016-01-01 00:00:00	t	68	434
923	2016-01-01 00:00:00	2016-01-01 00:00:00	t	68	442
924	2016-01-01 00:00:00	2016-01-01 00:00:00	t	68	414
925	2016-01-01 00:00:00	2016-01-01 00:00:00	t	68	439
926	2016-01-01 00:00:00	2016-01-01 00:00:00	t	68	419
927	2016-01-01 00:00:00	2016-01-01 00:00:00	t	69	399
928	2016-01-01 00:00:00	2016-01-01 00:00:00	t	69	436
929	2016-01-01 00:00:00	2016-01-01 00:00:00	t	69	417
930	2016-01-01 00:00:00	2016-01-01 00:00:00	t	69	441
931	2016-01-01 00:00:00	2016-01-01 00:00:00	t	69	444
932	2016-01-01 00:00:00	2016-01-01 00:00:00	t	69	412
933	2016-01-01 00:00:00	2016-01-01 00:00:00	t	69	415
934	2016-01-01 00:00:00	2016-01-01 00:00:00	t	69	418
935	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	429
936	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	431
937	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	432
938	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	433
939	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	424
940	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	435
941	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	423
942	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	400
943	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	427
944	2016-01-01 00:00:00	2016-01-01 00:00:00	t	70	406
945	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	421
946	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	428
947	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	401
948	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	443
949	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	389
950	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	391
951	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	420
952	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	430
953	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	408
954	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	409
955	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	397
956	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	437
957	2016-01-01 00:00:00	2016-01-01 00:00:00	t	71	438
958	2016-01-01 00:00:00	2016-01-01 00:00:00	t	72	434
959	2016-01-01 00:00:00	2016-01-01 00:00:00	t	72	416
960	2016-01-01 00:00:00	2016-01-01 00:00:00	t	72	417
961	2016-01-01 00:00:00	2016-01-01 00:00:00	t	72	441
962	2016-01-01 00:00:00	2016-01-01 00:00:00	t	72	444
963	2016-01-01 00:00:00	2016-01-01 00:00:00	t	72	405
964	2016-01-01 00:00:00	2016-01-01 00:00:00	t	72	442
965	2016-01-01 00:00:00	2016-01-01 00:00:00	t	73	436
966	2016-01-01 00:00:00	2016-01-01 00:00:00	t	73	423
967	2016-01-01 00:00:00	2016-01-01 00:00:00	t	73	418
968	2016-01-01 00:00:00	2016-01-01 00:00:00	t	73	419
969	2016-01-01 00:00:00	2016-01-01 00:00:00	t	73	422
970	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	391
971	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	406
972	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	433
973	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	438
974	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	414
975	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	443
976	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	421
977	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	401
978	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	424
979	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	428
980	2016-01-01 00:00:00	2016-01-01 00:00:00	t	74	440
981	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	398
982	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	404
983	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	427
984	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	429
985	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	430
986	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	408
987	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	431
988	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	411
989	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	435
990	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	437
991	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	412
992	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	399
993	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	432
994	2016-01-01 00:00:00	2016-01-01 00:00:00	t	75	439
995	2016-01-01 00:00:00	2016-01-01 00:00:00	t	76	423
996	2016-01-01 00:00:00	2016-01-01 00:00:00	t	76	434
997	2016-01-01 00:00:00	2016-01-01 00:00:00	t	76	417
998	2016-01-01 00:00:00	2016-01-01 00:00:00	t	76	441
999	2016-01-01 00:00:00	2016-01-01 00:00:00	t	76	418
1000	2016-01-01 00:00:00	2016-01-01 00:00:00	t	76	442
1001	2016-01-01 00:00:00	2016-01-01 00:00:00	t	76	416
1002	2016-01-01 00:00:00	2016-01-01 00:00:00	t	76	436
1003	2016-01-01 00:00:00	2016-01-01 00:00:00	t	77	405
1004	2016-01-01 00:00:00	2016-01-01 00:00:00	t	77	439
1005	2016-01-01 00:00:00	2016-01-01 00:00:00	t	77	414
1006	2016-01-01 00:00:00	2016-01-01 00:00:00	t	77	440
1007	2016-01-01 00:00:00	2016-01-01 00:00:00	t	77	419
1008	2016-01-01 00:00:00	2016-01-01 00:00:00	t	77	444
1009	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	400
1010	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	412
1011	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	389
1012	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	401
1013	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	428
1014	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	409
1015	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	397
1016	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	392
1017	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	421
1018	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	430
1019	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	408
1020	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	435
1021	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	398
1022	2016-01-01 00:00:00	2016-01-01 00:00:00	t	78	438
1023	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	443
1024	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	420
1025	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	422
1026	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	424
1027	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	404
1028	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	427
1029	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	406
1030	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	429
1031	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	431
1032	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	411
1033	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	432
1034	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	433
1035	2016-01-01 00:00:00	2016-01-01 00:00:00	t	79	437
1036	2016-01-01 00:00:00	2016-01-01 00:00:00	t	80	444
1037	2016-01-01 00:00:00	2016-01-01 00:00:00	t	80	434
1038	2016-01-01 00:00:00	2016-01-01 00:00:00	t	80	417
1039	2016-01-01 00:00:00	2016-01-01 00:00:00	t	80	441
1040	2016-01-01 00:00:00	2016-01-01 00:00:00	t	80	418
1041	2016-01-01 00:00:00	2016-01-01 00:00:00	t	80	419
1042	2016-01-01 00:00:00	2016-01-01 00:00:00	t	80	401
1043	2016-01-01 00:00:00	2016-01-01 00:00:00	t	80	439
1044	2016-01-01 00:00:00	2016-01-01 00:00:00	t	81	424
1045	2016-01-01 00:00:00	2016-01-01 00:00:00	t	81	405
1046	2016-01-01 00:00:00	2016-01-01 00:00:00	t	81	436
1047	2016-01-01 00:00:00	2016-01-01 00:00:00	t	81	412
1048	2016-01-01 00:00:00	2016-01-01 00:00:00	t	81	414
1049	2016-01-01 00:00:00	2016-01-01 00:00:00	t	81	415
1050	2016-01-01 00:00:00	2016-01-01 00:00:00	t	81	416
1051	2016-01-01 00:00:00	2016-01-01 00:00:00	t	81	440
1052	2016-01-01 00:00:00	2016-01-01 00:00:00	t	81	442
1053	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	406
1054	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	421
1055	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	404
1056	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	389
1057	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	425
1058	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	427
1059	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	432
1060	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	433
1061	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	443
1062	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	420
1063	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	422
1064	2016-01-01 00:00:00	2016-01-01 00:00:00	t	82	431
1065	2016-01-01 00:00:00	2016-01-01 00:00:00	t	83	392
1066	2016-01-01 00:00:00	2016-01-01 00:00:00	t	83	408
1067	2016-01-01 00:00:00	2016-01-01 00:00:00	t	83	402
1068	2016-01-01 00:00:00	2016-01-01 00:00:00	t	83	428
1069	2016-01-01 00:00:00	2016-01-01 00:00:00	t	83	429
1070	2016-01-01 00:00:00	2016-01-01 00:00:00	t	83	430
1071	2016-01-01 00:00:00	2016-01-01 00:00:00	t	83	410
1072	2016-01-01 00:00:00	2016-01-01 00:00:00	t	83	411
1073	2016-01-01 00:00:00	2016-01-01 00:00:00	t	83	435
1074	2016-01-01 00:00:00	2016-01-01 00:00:00	t	83	437
1075	2016-01-01 00:00:00	2016-01-01 00:00:00	t	83	398
1076	2016-01-01 00:00:00	2016-01-01 00:00:00	t	83	438
1077	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	492
1078	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	474
1079	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	477
1080	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	481
1081	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	486
1082	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	488
1083	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	490
1084	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	491
1085	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	475
1086	2017-01-01 00:00:00	2017-01-01 00:00:00	t	84	489
1087	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	505
1088	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	507
1089	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	518
1090	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	520
1091	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	521
1092	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	525
1093	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	533
1094	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	536
1095	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	501
1096	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	503
1097	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	504
1098	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	509
1099	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	519
1100	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	526
1101	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	530
1102	2017-01-01 00:00:00	2017-01-01 00:00:00	t	85	535
1103	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	453
1104	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	505
1105	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	486
1106	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	445
1107	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	463
1108	2017-01-01 00:00:00	2017-01-01 00:00:00	t	86	456
1109	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	460
1110	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	506
1111	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	475
1112	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	462
1113	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	490
1114	2017-01-01 00:00:00	2017-01-01 00:00:00	t	87	494
1115	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	469
1116	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	503
1117	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	453
1118	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	506
1119	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	478
1120	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	514
1121	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	484
1122	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	486
1123	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	524
1124	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	525
1125	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	535
1126	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	504
1127	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	488
1128	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	462
1129	2017-01-01 00:00:00	2017-01-01 00:00:00	t	88	530
1130	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	487
1131	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	472
1132	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	473
1133	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	477
1134	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	510
1135	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	517
1136	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	518
1137	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	519
1138	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	520
1139	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	523
1140	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	489
1141	2017-01-01 00:00:00	2017-01-01 00:00:00	t	89	490
1142	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	475
1143	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	509
1144	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	460
1145	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	480
1146	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	481
1147	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	515
1148	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	516
1149	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	485
1150	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	521
1151	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	532
1152	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	498
1153	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	499
1154	2017-01-01 00:00:00	2017-01-01 00:00:00	t	90	500
1155	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	482
1156	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	534
1157	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	458
1158	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	507
1159	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	513
1160	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	496
1161	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	533
1162	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	468
1163	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	511
1164	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	536
1165	2017-01-01 00:00:00	2017-01-01 00:00:00	t	91	461
1166	2017-01-01 00:00:00	2017-01-01 00:00:00	t	92	445
1167	2017-01-01 00:00:00	2017-01-01 00:00:00	t	92	526
1168	2017-01-01 00:00:00	2017-01-01 00:00:00	t	92	491
1169	2017-01-01 00:00:00	2017-01-01 00:00:00	t	92	527
1170	2017-01-01 00:00:00	2017-01-01 00:00:00	t	92	528
1171	2017-01-01 00:00:00	2017-01-01 00:00:00	t	92	463
1172	2017-01-01 00:00:00	2017-01-01 00:00:00	t	92	529
1173	2017-01-01 00:00:00	2017-01-01 00:00:00	t	92	464
1174	2017-01-01 00:00:00	2017-01-01 00:00:00	t	93	492
1175	2017-01-01 00:00:00	2017-01-01 00:00:00	t	93	497
1176	2017-01-01 00:00:00	2017-01-01 00:00:00	t	93	501
1177	2017-01-01 00:00:00	2017-01-01 00:00:00	t	93	505
1178	2017-01-01 00:00:00	2017-01-01 00:00:00	t	93	494
1179	2017-01-01 00:00:00	2017-01-01 00:00:00	t	93	495
1180	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	517
1181	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	453
1182	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	506
1183	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	478
1184	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	519
1185	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	520
1186	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	486
1187	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	487
1188	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	490
1189	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	525
1190	2017-01-01 00:00:00	2017-01-01 00:00:00	t	94	530
1191	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	503
1192	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	473
1193	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	484
1194	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	488
1195	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	523
1196	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	524
1197	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	489
1198	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	522
1199	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	504
1200	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	510
1201	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	477
1202	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	518
1203	2017-01-01 00:00:00	2017-01-01 00:00:00	t	95	535
1204	2017-01-01 00:00:00	2017-01-01 00:00:00	t	96	479
1205	2017-01-01 00:00:00	2017-01-01 00:00:00	t	96	513
1206	2017-01-01 00:00:00	2017-01-01 00:00:00	t	96	481
1207	2017-01-01 00:00:00	2017-01-01 00:00:00	t	96	482
1208	2017-01-01 00:00:00	2017-01-01 00:00:00	t	96	532
1209	2017-01-01 00:00:00	2017-01-01 00:00:00	t	96	533
1210	2017-01-01 00:00:00	2017-01-01 00:00:00	t	96	498
1211	2017-01-01 00:00:00	2017-01-01 00:00:00	t	96	534
1212	2017-01-01 00:00:00	2017-01-01 00:00:00	t	96	468
1213	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	460
1214	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	516
1215	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	521
1216	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	536
1217	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	458
1218	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	512
1219	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	515
1220	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	483
1221	2017-01-01 00:00:00	2017-01-01 00:00:00	t	97	496
1222	2017-01-01 00:00:00	2017-01-01 00:00:00	t	98	505
1223	2017-01-01 00:00:00	2017-01-01 00:00:00	t	98	494
1224	2017-01-01 00:00:00	2017-01-01 00:00:00	t	98	467
1225	2017-01-01 00:00:00	2017-01-01 00:00:00	t	98	501
1226	2017-01-01 00:00:00	2017-01-01 00:00:00	t	98	462
1227	2017-01-01 00:00:00	2017-01-01 00:00:00	t	98	492
1228	2017-01-01 00:00:00	2017-01-01 00:00:00	t	98	497
1229	2017-01-01 00:00:00	2017-01-01 00:00:00	t	99	526
1230	2017-01-01 00:00:00	2017-01-01 00:00:00	t	99	495
1231	2017-01-01 00:00:00	2017-01-01 00:00:00	t	99	529
1232	2017-01-01 00:00:00	2017-01-01 00:00:00	t	99	445
1233	2017-01-01 00:00:00	2017-01-01 00:00:00	t	99	491
1234	2017-01-01 00:00:00	2017-01-01 00:00:00	t	99	463
1235	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	469
1236	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	484
1237	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	520
1238	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	522
1239	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	524
1240	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	477
1241	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	510
1242	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	517
1243	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	489
1244	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	473
1245	2017-01-01 00:00:00	2017-01-01 00:00:00	t	100	535
1246	2017-01-01 00:00:00	2017-01-01 00:00:00	t	101	519
1247	2017-01-01 00:00:00	2017-01-01 00:00:00	t	101	486
1248	2017-01-01 00:00:00	2017-01-01 00:00:00	t	101	453
1249	2017-01-01 00:00:00	2017-01-01 00:00:00	t	101	506
1250	2017-01-01 00:00:00	2017-01-01 00:00:00	t	101	514
1251	2017-01-01 00:00:00	2017-01-01 00:00:00	t	101	518
1252	2017-01-01 00:00:00	2017-01-01 00:00:00	t	101	487
1253	2017-01-01 00:00:00	2017-01-01 00:00:00	t	101	490
1254	2017-01-01 00:00:00	2017-01-01 00:00:00	t	101	525
1255	2017-01-01 00:00:00	2017-01-01 00:00:00	t	101	530
1256	2017-01-01 00:00:00	2017-01-01 00:00:00	t	102	475
1257	2017-01-01 00:00:00	2017-01-01 00:00:00	t	102	509
1258	2017-01-01 00:00:00	2017-01-01 00:00:00	t	102	479
1259	2017-01-01 00:00:00	2017-01-01 00:00:00	t	102	481
1260	2017-01-01 00:00:00	2017-01-01 00:00:00	t	102	515
1261	2017-01-01 00:00:00	2017-01-01 00:00:00	t	102	516
1262	2017-01-01 00:00:00	2017-01-01 00:00:00	t	102	498
1263	2017-01-01 00:00:00	2017-01-01 00:00:00	t	102	534
1264	2017-01-01 00:00:00	2017-01-01 00:00:00	t	102	513
1265	2017-01-01 00:00:00	2017-01-01 00:00:00	t	102	536
1266	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	512
1267	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	482
1268	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	483
1269	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	485
1270	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	521
1271	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	496
1272	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	531
1273	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	500
1274	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	507
1275	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	532
1276	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	499
1277	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	480
1278	2017-01-01 00:00:00	2017-01-01 00:00:00	t	103	533
1279	2017-01-01 00:00:00	2017-01-01 00:00:00	t	104	470
1280	2017-01-01 00:00:00	2017-01-01 00:00:00	t	104	491
1281	2017-01-01 00:00:00	2017-01-01 00:00:00	t	104	527
1282	2017-01-01 00:00:00	2017-01-01 00:00:00	t	104	463
1283	2017-01-01 00:00:00	2017-01-01 00:00:00	t	104	497
1284	2017-01-01 00:00:00	2017-01-01 00:00:00	t	104	488
1285	2017-01-01 00:00:00	2017-01-01 00:00:00	t	104	526
1286	2017-01-01 00:00:00	2017-01-01 00:00:00	t	104	494
1287	2017-01-01 00:00:00	2017-01-01 00:00:00	t	105	523
1288	2017-01-01 00:00:00	2017-01-01 00:00:00	t	105	462
1289	2017-01-01 00:00:00	2017-01-01 00:00:00	t	105	467
1290	2017-01-01 00:00:00	2017-01-01 00:00:00	t	105	445
1291	2017-01-01 00:00:00	2017-01-01 00:00:00	t	105	505
1292	2017-01-01 00:00:00	2017-01-01 00:00:00	t	105	492
1293	2017-01-01 00:00:00	2017-01-01 00:00:00	t	105	495
1294	2017-01-01 00:00:00	2017-01-01 00:00:00	t	105	528
1295	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	503
1296	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	504
1297	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	453
1298	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	473
1299	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	486
1300	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	522
1301	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	490
1302	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	525
1303	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	470
1304	2017-01-01 00:00:00	2017-01-01 00:00:00	t	106	487
1305	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	506
1306	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	477
1307	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	514
1308	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	484
1309	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	489
1310	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	535
1311	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	469
1312	2017-01-01 00:00:00	2017-01-01 00:00:00	t	107	524
1313	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	507
1314	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	511
1315	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	513
1316	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	536
1317	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	460
1318	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	512
1319	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	515
1320	2017-01-01 00:00:00	2017-01-01 00:00:00	t	108	482
1321	2017-01-01 00:00:00	2017-01-01 00:00:00	t	109	481
1322	2017-01-01 00:00:00	2017-01-01 00:00:00	t	109	485
1323	2017-01-01 00:00:00	2017-01-01 00:00:00	t	109	500
1324	2017-01-01 00:00:00	2017-01-01 00:00:00	t	109	458
1325	2017-01-01 00:00:00	2017-01-01 00:00:00	t	109	516
1326	2017-01-01 00:00:00	2017-01-01 00:00:00	t	109	521
1327	2017-01-01 00:00:00	2017-01-01 00:00:00	t	109	498
1328	2017-01-01 00:00:00	2017-01-01 00:00:00	t	109	499
1329	2017-01-01 00:00:00	2017-01-01 00:00:00	t	110	462
1330	2017-01-01 00:00:00	2017-01-01 00:00:00	t	110	523
1331	2017-01-01 00:00:00	2017-01-01 00:00:00	t	110	527
1332	2017-01-01 00:00:00	2017-01-01 00:00:00	t	110	495
1333	2017-01-01 00:00:00	2017-01-01 00:00:00	t	110	528
1334	2017-01-01 00:00:00	2017-01-01 00:00:00	t	110	463
1335	2017-01-01 00:00:00	2017-01-01 00:00:00	t	110	529
1336	2017-01-01 00:00:00	2017-01-01 00:00:00	t	111	492
1337	2017-01-01 00:00:00	2017-01-01 00:00:00	t	111	488
1338	2017-01-01 00:00:00	2017-01-01 00:00:00	t	111	526
1339	2017-01-01 00:00:00	2017-01-01 00:00:00	t	111	491
1340	2017-01-01 00:00:00	2017-01-01 00:00:00	t	111	494
1341	2017-01-01 00:00:00	2017-01-01 00:00:00	t	111	497
1342	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	585
1343	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	563
1344	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	567
1345	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	568
1346	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	571
1347	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	572
1348	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	574
1349	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	580
1350	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	582
1351	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	583
1352	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	584
1353	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	559
1354	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	566
1355	2018-01-01 00:00:00	2018-01-01 00:00:00	t	112	581
1356	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	586
1357	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	587
1358	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	589
1359	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	592
1360	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	593
1361	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	594
1362	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	596
1363	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	597
1364	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	598
1365	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	602
1366	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	603
1367	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	605
1368	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	606
1369	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	609
1370	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	610
1371	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	612
1372	2018-01-01 00:00:00	2018-01-01 00:00:00	t	113	588
1373	2018-01-01 00:00:00	2018-01-01 00:00:00	t	114	589
1374	2018-01-01 00:00:00	2018-01-01 00:00:00	t	114	602
1375	2018-01-01 00:00:00	2018-01-01 00:00:00	t	114	579
1376	2018-01-01 00:00:00	2018-01-01 00:00:00	t	115	592
1377	2018-01-01 00:00:00	2018-01-01 00:00:00	t	115	593
1378	2018-01-01 00:00:00	2018-01-01 00:00:00	t	115	543
1379	2018-01-01 00:00:00	2018-01-01 00:00:00	t	116	604
1380	2018-01-01 00:00:00	2018-01-01 00:00:00	t	116	584
1381	2018-01-01 00:00:00	2018-01-01 00:00:00	t	116	587
1382	2018-01-01 00:00:00	2018-01-01 00:00:00	t	116	566
1383	2018-01-01 00:00:00	2018-01-01 00:00:00	t	116	589
1384	2018-01-01 00:00:00	2018-01-01 00:00:00	t	116	549
1385	2018-01-01 00:00:00	2018-01-01 00:00:00	t	116	568
1386	2018-01-01 00:00:00	2018-01-01 00:00:00	t	116	595
1387	2018-01-01 00:00:00	2018-01-01 00:00:00	t	116	599
1388	2018-01-01 00:00:00	2018-01-01 00:00:00	t	116	607
1389	2018-01-01 00:00:00	2018-01-01 00:00:00	t	117	571
1390	2018-01-01 00:00:00	2018-01-01 00:00:00	t	117	593
1391	2018-01-01 00:00:00	2018-01-01 00:00:00	t	117	581
1392	2018-01-01 00:00:00	2018-01-01 00:00:00	t	117	606
1393	2018-01-01 00:00:00	2018-01-01 00:00:00	t	117	611
1394	2018-01-01 00:00:00	2018-01-01 00:00:00	t	118	560
1395	2018-01-01 00:00:00	2018-01-01 00:00:00	t	118	564
1396	2018-01-01 00:00:00	2018-01-01 00:00:00	t	118	567
1397	2018-01-01 00:00:00	2018-01-01 00:00:00	t	118	578
1398	2018-01-01 00:00:00	2018-01-01 00:00:00	t	118	580
1399	2018-01-01 00:00:00	2018-01-01 00:00:00	t	118	575
1400	2018-01-01 00:00:00	2018-01-01 00:00:00	t	118	603
1401	2018-01-01 00:00:00	2018-01-01 00:00:00	t	118	586
1402	2018-01-01 00:00:00	2018-01-01 00:00:00	t	118	551
1403	2018-01-01 00:00:00	2018-01-01 00:00:00	t	118	600
1404	2018-01-01 00:00:00	2018-01-01 00:00:00	t	119	579
1405	2018-01-01 00:00:00	2018-01-01 00:00:00	t	119	588
1406	2018-01-01 00:00:00	2018-01-01 00:00:00	t	119	591
1407	2018-01-01 00:00:00	2018-01-01 00:00:00	t	119	573
1408	2018-01-01 00:00:00	2018-01-01 00:00:00	t	119	596
1409	2018-01-01 00:00:00	2018-01-01 00:00:00	t	119	601
1410	2018-01-01 00:00:00	2018-01-01 00:00:00	t	119	602
1411	2018-01-01 00:00:00	2018-01-01 00:00:00	t	119	605
1412	2018-01-01 00:00:00	2018-01-01 00:00:00	t	119	612
1413	2018-01-01 00:00:00	2018-01-01 00:00:00	t	119	585
1414	2018-01-01 00:00:00	2018-01-01 00:00:00	t	120	547
1415	2018-01-01 00:00:00	2018-01-01 00:00:00	t	120	556
1416	2018-01-01 00:00:00	2018-01-01 00:00:00	t	120	548
1417	2018-01-01 00:00:00	2018-01-01 00:00:00	t	120	569
1418	2018-01-01 00:00:00	2018-01-01 00:00:00	t	120	577
1419	2018-01-01 00:00:00	2018-01-01 00:00:00	t	120	582
1420	2018-01-01 00:00:00	2018-01-01 00:00:00	t	120	613
1421	2018-01-01 00:00:00	2018-01-01 00:00:00	t	120	561
1422	2018-01-01 00:00:00	2018-01-01 00:00:00	t	121	597
1423	2018-01-01 00:00:00	2018-01-01 00:00:00	t	121	557
1424	2018-01-01 00:00:00	2018-01-01 00:00:00	t	121	565
1425	2018-01-01 00:00:00	2018-01-01 00:00:00	t	121	594
1426	2018-01-01 00:00:00	2018-01-01 00:00:00	t	121	608
1427	2018-01-01 00:00:00	2018-01-01 00:00:00	t	121	609
1428	2018-01-01 00:00:00	2018-01-01 00:00:00	t	122	610
1429	2018-01-01 00:00:00	2018-01-01 00:00:00	t	122	570
1430	2018-01-01 00:00:00	2018-01-01 00:00:00	t	122	583
1431	2018-01-01 00:00:00	2018-01-01 00:00:00	t	122	546
1432	2018-01-01 00:00:00	2018-01-01 00:00:00	t	122	598
1433	2018-01-01 00:00:00	2018-01-01 00:00:00	t	123	558
1434	2018-01-01 00:00:00	2018-01-01 00:00:00	t	123	562
1435	2018-01-01 00:00:00	2018-01-01 00:00:00	t	123	572
1436	2018-01-01 00:00:00	2018-01-01 00:00:00	t	123	574
1437	2018-01-01 00:00:00	2018-01-01 00:00:00	t	123	563
1438	2018-01-01 00:00:00	2018-01-01 00:00:00	t	123	592
1439	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	566
1440	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	571
1441	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	578
1442	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	564
1443	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	591
1444	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	595
1445	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	601
1446	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	581
1447	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	549
1448	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	599
1449	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	600
1450	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	580
1451	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	607
1452	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	589
1453	2018-01-01 00:00:00	2018-01-01 00:00:00	t	124	584
1454	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	586
1455	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	560
1456	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	588
1457	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	567
1458	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	568
1459	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	573
1460	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	593
1461	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	596
1462	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	602
1463	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	579
1464	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	605
1465	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	606
1466	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	612
1467	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	585
1468	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	587
1469	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	603
1470	2018-01-01 00:00:00	2018-01-01 00:00:00	t	125	604
1471	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	557
1472	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	569
1473	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	590
1474	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	609
1475	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	610
1476	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	611
1477	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	556
1478	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	613
1479	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	562
1480	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	545
1481	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	558
1482	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	563
1483	2018-01-01 00:00:00	2018-01-01 00:00:00	t	126	576
1484	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	594
1485	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	561
1486	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	565
1487	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	592
1488	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	574
1489	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	575
1490	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	597
1491	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	598
1492	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	608
1493	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	582
1494	2018-01-01 00:00:00	2018-01-01 00:00:00	t	127	583
1495	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	612
1496	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	586
1497	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	560
1498	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	564
1499	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	566
1500	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	588
1501	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	591
1502	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	573
1503	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	596
1504	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	601
1505	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	604
1506	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	605
1507	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	581
1508	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	606
1509	2018-01-01 00:00:00	2018-01-01 00:00:00	t	128	607
1510	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	587
1511	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	567
1512	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	549
1513	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	599
1514	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	603
1515	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	568
1516	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	571
1517	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	593
1518	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	595
1519	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	578
1520	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	579
1521	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	580
1522	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	584
1523	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	585
1524	2018-01-01 00:00:00	2018-01-01 00:00:00	t	129	589
1525	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	592
1526	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	565
1527	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	558
1528	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	561
1529	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	575
1530	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	608
1531	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	609
1532	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	610
1533	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	611
1534	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	613
1535	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	557
1536	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	597
1537	2018-01-01 00:00:00	2018-01-01 00:00:00	t	130	556
1538	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	574
1539	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	563
1540	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	569
1541	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	570
1542	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	572
1543	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	594
1544	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	598
1545	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	583
1546	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	600
1547	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	590
1548	2018-01-01 00:00:00	2018-01-01 00:00:00	t	131	582
1549	2018-01-01 00:00:00	2018-01-01 00:00:00	t	132	587
1550	2018-01-01 00:00:00	2018-01-01 00:00:00	t	132	566
1551	2018-01-01 00:00:00	2018-01-01 00:00:00	t	132	588
1552	2018-01-01 00:00:00	2018-01-01 00:00:00	t	132	571
1553	2018-01-01 00:00:00	2018-01-01 00:00:00	t	132	591
1554	2018-01-01 00:00:00	2018-01-01 00:00:00	t	132	579
1555	2018-01-01 00:00:00	2018-01-01 00:00:00	t	132	581
1556	2018-01-01 00:00:00	2018-01-01 00:00:00	t	132	612
1557	2018-01-01 00:00:00	2018-01-01 00:00:00	t	132	584
1558	2018-01-01 00:00:00	2018-01-01 00:00:00	t	132	585
1559	2018-01-01 00:00:00	2018-01-01 00:00:00	t	133	586
1560	2018-01-01 00:00:00	2018-01-01 00:00:00	t	133	603
1561	2018-01-01 00:00:00	2018-01-01 00:00:00	t	133	564
1562	2018-01-01 00:00:00	2018-01-01 00:00:00	t	133	567
1563	2018-01-01 00:00:00	2018-01-01 00:00:00	t	133	549
1564	2018-01-01 00:00:00	2018-01-01 00:00:00	t	133	568
1565	2018-01-01 00:00:00	2018-01-01 00:00:00	t	133	589
1566	2018-01-01 00:00:00	2018-01-01 00:00:00	t	133	593
1567	2018-01-01 00:00:00	2018-01-01 00:00:00	t	133	602
1568	2018-01-01 00:00:00	2018-01-01 00:00:00	t	133	604
1569	2018-01-01 00:00:00	2018-01-01 00:00:00	t	134	594
1570	2018-01-01 00:00:00	2018-01-01 00:00:00	t	134	583
1571	2018-01-01 00:00:00	2018-01-01 00:00:00	t	134	546
1572	2018-01-01 00:00:00	2018-01-01 00:00:00	t	134	592
1573	2018-01-01 00:00:00	2018-01-01 00:00:00	t	134	611
1574	2018-01-01 00:00:00	2018-01-01 00:00:00	t	134	562
1575	2018-01-01 00:00:00	2018-01-01 00:00:00	t	134	563
1576	2018-01-01 00:00:00	2018-01-01 00:00:00	t	134	597
1577	2018-01-01 00:00:00	2018-01-01 00:00:00	t	134	582
1578	2018-01-01 00:00:00	2018-01-01 00:00:00	t	134	610
1579	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	557
1580	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	558
1581	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	561
1582	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	569
1583	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	570
1584	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	574
1585	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	608
1586	2018-01-01 00:00:00	2018-01-01 00:00:00	t	135	609
\.


--
-- Data for Name: quiz_questions; Type: TABLE DATA; Schema: public; Owner: pedro
--

COPY public.quiz_questions (id, sequence, question_id, quiz_id) FROM stdin;
2	1	2	1
3	2	3	1
4	3	4	1
5	4	5	1
26	5	754	2
8	7	8	1
9	8	9	1
10	9	10	1
11	10	11	1
12	11	12	1
13	12	13	1
14	13	14	1
15	14	15	1
16	15	16	1
17	16	17	1
18	17	18	1
19	18	19	1
20	19	20	1
21	0	21	2
22	1	22	2
23	2	23	2
24	3	24	2
25	4	25	2
27	6	27	2
28	7	28	2
29	8	29	2
30	9	30	2
31	10	31	2
32	11	32	2
33	12	33	2
34	13	34	2
152	1	858	7
36	15	36	2
37	16	37	2
41	0	41	3
42	1	42	3
43	2	43	3
44	3	44	3
45	4	45	3
46	5	46	3
47	6	47	3
49	8	49	3
50	9	50	3
51	10	51	3
54	13	54	3
55	14	55	3
56	15	56	3
57	16	57	3
60	19	60	3
61	0	61	4
62	1	62	4
63	2	63	4
64	3	64	4
65	4	65	4
66	5	66	4
67	6	67	4
68	7	68	4
69	8	69	4
70	9	70	4
71	10	71	4
72	11	72	4
74	13	74	4
75	14	75	4
76	15	76	4
77	16	77	4
78	17	78	4
79	18	79	4
81	20	81	4
82	21	82	4
86	25	86	4
87	26	87	4
88	27	88	4
90	29	90	4
91	0	91	5
92	1	92	5
93	2	93	5
94	3	94	5
95	4	95	5
96	5	96	5
97	6	97	5
98	7	98	5
99	8	99	5
100	9	100	5
101	10	101	5
102	11	102	5
103	12	103	5
104	13	104	5
105	14	105	5
106	15	106	5
107	16	107	5
108	17	108	5
109	18	109	5
111	20	111	5
112	21	112	5
116	25	116	5
118	27	118	5
119	28	119	5
120	29	120	5
122	1	122	6
124	3	124	6
125	4	125	6
126	5	126	6
127	6	127	6
128	7	128	6
129	8	129	6
130	9	130	6
131	10	131	6
132	11	132	6
133	12	133	6
134	13	134	6
135	14	135	6
136	15	136	6
137	16	137	6
138	17	138	6
139	18	139	6
140	19	140	6
141	20	141	6
142	21	142	6
143	22	143	6
144	23	144	6
146	25	146	6
148	27	148	6
149	28	149	6
151	0	151	7
154	3	154	7
155	4	155	7
156	5	156	7
157	6	157	7
158	7	158	7
159	8	159	7
161	10	161	7
162	11	162	7
163	12	163	7
165	14	165	7
166	15	166	7
167	16	167	7
168	17	168	7
169	18	169	7
170	19	170	7
171	20	171	7
172	21	172	7
173	22	173	7
174	23	174	7
175	24	175	7
176	25	176	7
177	26	177	7
178	27	178	7
179	28	179	7
180	29	180	7
238	27	523	9
362	1	1010	24
236	25	1140	9
211	0	211	9
212	1	212	9
214	3	214	9
215	4	215	9
216	5	216	9
218	7	218	9
219	8	219	9
220	9	220	9
222	11	222	9
223	12	223	9
224	13	224	9
225	14	225	9
227	16	227	9
229	18	229	9
230	19	230	9
231	20	231	9
232	21	232	9
233	22	233	9
234	23	234	9
235	24	235	9
272	1	272	11
273	2	273	11
275	4	275	11
276	5	276	11
278	7	278	11
279	8	279	11
280	9	280	11
281	10	281	11
282	11	282	11
283	12	283	11
284	13	284	11
285	14	285	11
286	15	286	11
287	16	287	11
288	17	288	11
289	18	289	11
290	19	290	11
291	20	291	11
293	22	293	11
294	23	294	11
295	24	295	11
298	27	298	11
299	28	299	11
300	29	300	11
301	0	301	12
303	2	303	12
304	3	304	12
306	0	306	13
307	1	307	13
308	2	308	13
310	4	310	13
311	0	311	14
312	1	312	14
313	2	313	14
315	4	315	14
316	0	316	15
317	1	317	15
319	3	319	15
320	4	320	15
321	0	321	16
322	1	322	16
323	2	323	16
324	3	324	16
326	0	326	17
328	2	328	17
329	3	329	17
330	4	330	17
331	0	331	18
332	1	332	18
333	2	333	18
334	3	334	18
335	4	335	18
336	0	336	19
337	1	337	19
338	2	338	19
339	3	339	19
343	2	343	20
344	3	344	20
346	0	346	21
348	2	348	21
349	3	349	21
350	4	350	21
351	0	351	22
352	1	352	22
353	2	353	22
354	3	354	22
356	0	356	23
359	3	359	23
361	0	361	24
363	2	363	24
364	3	364	24
367	1	367	25
368	2	368	25
369	3	369	25
370	4	370	25
345	4	350	20
302	1	477	12
305	4	538	12
274	3	756	11
318	2	767	15
309	3	799	13
292	21	860	11
341	0	885	20
357	1	980	23
314	3	1081	14
366	0	1083	25
365	4	1085	24
296	25	1088	11
371	0	371	26
372	1	372	26
373	2	373	26
374	3	374	26
375	4	375	26
377	1	377	27
378	2	378	27
380	4	380	27
382	1	382	28
383	2	383	28
387	1	387	29
388	2	388	29
389	3	389	29
391	0	391	30
393	2	393	30
395	4	395	30
398	2	398	31
399	3	399	31
401	0	401	32
402	1	402	32
403	2	403	32
404	3	404	32
405	4	405	32
406	0	406	33
407	1	407	33
409	3	409	33
410	4	410	33
411	0	411	34
412	1	412	34
413	2	413	34
414	3	414	34
415	4	415	34
416	0	416	35
417	1	417	35
418	2	418	35
419	3	419	35
420	4	420	35
451	0	451	37
452	1	452	37
453	2	453	37
454	3	454	37
455	4	455	37
456	5	456	37
460	9	460	37
461	10	461	37
462	11	462	37
463	12	463	37
464	13	464	37
465	14	465	37
467	16	467	37
469	18	469	37
470	19	470	37
471	20	471	37
472	21	472	37
473	22	473	37
475	24	475	37
476	25	476	37
477	26	477	37
480	29	480	37
513	2	513	39
514	3	514	39
515	4	515	39
517	6	517	39
518	7	518	39
519	8	519	39
522	11	522	39
523	12	523	39
525	14	525	39
526	15	526	39
527	16	527	39
528	17	528	39
531	20	531	39
532	21	532	39
533	22	533	39
535	24	535	39
536	25	536	39
537	26	537	39
538	27	538	39
539	28	539	39
540	29	540	39
546	0	546	41
547	1	547	41
548	2	548	41
549	3	549	41
550	4	550	41
557	1	557	43
558	2	558	43
559	3	559	43
560	4	560	43
566	0	566	45
568	2	568	45
569	3	569	45
570	4	570	45
576	0	576	47
577	1	577	47
579	3	579	47
580	4	580	47
586	0	586	49
587	1	587	49
588	2	588	49
589	3	589	49
590	4	590	49
596	0	596	51
597	1	597	51
598	2	598	51
599	3	599	51
600	4	600	51
606	0	606	53
607	1	607	53
608	2	608	53
609	3	609	53
610	4	610	53
619	3	619	55
620	4	620	55
626	0	626	57
627	1	627	57
628	2	628	57
629	3	629	57
636	0	636	59
638	2	638	59
639	3	639	59
646	0	646	61
648	2	648	61
649	3	649	61
650	4	650	61
656	0	656	63
657	1	657	63
660	4	660	63
695	4	695	65
696	5	696	65
697	6	697	65
698	7	698	65
699	8	699	65
701	10	701	65
702	11	702	65
703	12	703	65
705	14	705	65
706	15	706	65
708	17	708	65
709	18	709	65
711	20	711	65
712	21	712	65
713	22	713	65
714	23	714	65
715	24	715	65
716	25	716	65
751	0	751	67
752	1	752	67
753	2	753	67
754	3	754	67
755	4	755	67
756	5	756	67
757	6	757	67
758	7	758	67
759	8	759	67
760	9	760	67
761	10	761	67
762	11	762	67
763	12	763	67
764	13	764	67
765	14	765	67
767	16	767	67
768	17	768	67
769	18	769	67
770	19	770	67
772	21	772	67
773	22	773	67
775	24	775	67
777	26	777	67
778	27	778	67
779	28	779	67
780	29	780	67
786	0	786	69
787	1	787	69
788	2	788	69
789	3	789	69
790	4	790	69
796	0	796	71
797	1	797	71
798	2	798	71
799	3	799	71
800	4	800	71
807	1	807	73
808	2	808	73
809	3	809	73
810	4	810	73
816	0	816	75
818	2	818	75
820	4	820	75
829	3	829	77
837	1	837	79
840	4	840	79
846	0	846	81
848	2	848	81
856	0	856	83
857	1	857	83
858	2	858	83
860	4	860	83
881	0	881	85
883	2	883	85
884	3	884	85
885	4	885	85
886	5	886	85
887	6	887	85
888	7	888	85
889	8	889	85
890	9	890	85
891	10	891	85
892	11	892	85
893	12	893	85
894	13	894	85
895	14	895	85
896	15	896	85
897	16	897	85
899	18	899	85
900	19	900	85
921	0	921	87
922	1	922	87
923	2	923	87
924	3	924	87
927	6	927	87
928	7	928	87
929	8	929	87
930	9	930	87
931	10	931	87
933	12	933	87
934	13	934	87
936	15	936	87
937	16	937	87
938	17	938	87
940	19	940	87
946	0	946	89
947	1	947	89
948	2	948	89
949	3	949	89
950	4	950	89
956	0	956	91
957	1	957	91
958	2	958	91
959	3	959	91
960	4	960	91
966	0	966	93
967	1	967	93
968	2	968	93
969	3	969	93
970	4	970	93
976	0	976	95
977	1	977	95
979	3	979	95
980	4	980	95
987	1	987	97
989	3	989	97
997	1	997	99
998	2	998	99
1000	4	1000	99
1006	0	1006	101
1007	1	1007	101
1009	3	1009	101
1010	4	1010	101
1016	0	1016	103
1017	1	1017	103
1018	2	1018	103
1020	4	1020	103
1026	0	1026	105
1027	1	1027	105
1028	2	1028	105
1029	3	1029	105
1030	4	1030	105
1036	0	1036	107
1037	1	1037	107
1039	3	1039	107
1040	4	1040	107
1046	0	1046	109
1047	1	1047	109
1048	2	1048	109
1049	3	1049	109
1050	4	1050	109
1056	0	1056	111
1057	1	1057	111
1058	2	1058	111
1059	3	1059	111
1060	4	1060	111
1081	0	1081	113
1082	1	1082	113
1083	2	1083	113
1084	3	1084	113
1085	4	1085	113
1086	5	1086	113
1087	6	1087	113
1088	7	1088	113
1089	8	1089	113
1090	9	1090	113
1091	10	1091	113
1092	11	1092	113
1093	12	1093	113
1094	13	1094	113
1095	14	1095	113
1096	15	1096	113
1097	16	1097	113
1098	17	1098	113
1099	18	1099	113
1100	19	1100	113
192	11	523	8
183	2	1140	8
1121	0	1121	115
1123	2	1123	115
1124	3	1124	115
1125	4	1125	115
1126	5	1126	115
1127	6	1127	115
1128	7	1128	115
1129	8	1129	115
1130	9	1130	115
1131	10	1131	115
1132	11	1132	115
1133	12	1133	115
1134	13	1134	115
1135	14	1135	115
1136	15	1136	115
1137	16	1137	115
1138	17	1138	115
1139	18	1139	115
1140	19	1140	115
1156	0	1156	119
1157	1	1157	119
1158	2	1158	119
1159	3	1159	119
1160	4	1160	119
1176	0	1176	123
1177	1	1177	123
1178	2	1178	123
1179	3	1179	123
1180	4	1180	123
1186	0	1186	125
1187	1	1187	125
1188	2	1188	125
1189	3	1189	125
1190	4	1190	125
1196	0	1196	127
1197	1	1197	127
1198	2	1198	127
1199	3	1199	127
1200	4	1200	127
1206	0	1206	129
1207	1	1207	129
1208	2	1208	129
1209	3	1209	129
1210	4	1210	129
1216	0	1216	131
1217	1	1217	131
1218	2	1218	131
1219	3	1219	131
1220	4	1220	131
1226	0	1226	133
1227	1	1227	133
1228	2	1228	133
1229	3	1229	133
1230	4	1230	133
1236	0	1236	135
1237	1	1237	135
1238	2	1238	135
1239	3	1239	135
1240	4	1240	135
181	0	224	8
182	1	225	8
186	5	227	8
187	6	215	8
188	7	216	8
190	9	218	8
191	10	219	8
195	14	220	8
197	16	222	8
198	17	223	8
201	20	214	8
202	21	229	8
203	22	230	8
204	23	231	8
1	0	464	1
40	19	465	2
58	17	453	3
205	24	232	8
206	25	233	8
207	26	234	8
208	27	235	8
209	28	211	8
210	29	212	8
241	0	281	10
242	1	282	10
243	2	283	10
244	3	284	10
245	4	286	10
247	6	275	10
248	7	276	10
250	9	278	10
251	10	279	10
252	11	285	10
253	12	280	10
254	13	287	10
255	14	288	10
257	16	294	10
258	17	295	10
261	20	298	10
262	21	299	10
263	22	300	10
265	24	289	10
266	25	290	10
267	26	291	10
268	27	293	10
269	28	272	10
270	29	273	10
421	0	475	36
422	1	476	36
423	2	477	36
426	5	480	36
427	6	460	36
428	7	461	36
429	8	462	36
430	9	463	36
431	10	451	36
432	11	452	36
433	12	456	36
437	16	464	36
438	17	465	36
440	19	467	36
442	21	469	36
443	22	470	36
444	23	471	36
445	24	453	36
446	25	454	36
447	26	455	36
448	27	472	36
449	28	473	36
481	0	522	38
7	6	522	1
482	1	523	38
484	3	525	38
485	4	526	38
226	15	526	9
185	4	526	8
486	5	527	38
487	6	533	38
489	8	535	38
490	9	537	38
491	10	538	38
492	11	539	38
493	12	540	38
494	13	536	38
495	14	528	38
498	17	531	38
89	28	531	4
499	18	532	38
53	12	532	3
502	21	513	38
504	23	514	38
505	24	515	38
507	26	517	38
508	27	518	38
509	28	519	38
541	0	548	40
542	1	549	40
543	2	550	40
544	3	546	40
545	4	547	40
551	0	557	42
552	1	559	42
553	2	558	42
554	3	560	42
561	0	568	44
562	1	569	44
563	2	566	44
565	4	570	44
572	1	580	46
573	2	577	46
574	3	579	46
575	4	576	46
581	0	590	48
582	1	587	48
583	2	589	48
584	3	588	48
585	4	586	48
591	0	598	50
592	1	600	50
593	2	596	50
594	3	597	50
595	4	599	50
601	0	608	52
602	1	607	52
603	2	609	52
604	3	610	52
605	4	606	52
612	1	619	54
613	2	620	54
622	1	629	56
623	2	627	56
624	3	626	56
625	4	628	56
633	2	638	58
634	3	636	58
635	4	639	58
642	1	646	60
643	2	648	60
644	3	650	60
645	4	649	60
653	2	660	62
654	3	656	62
655	4	657	62
661	0	697	64
478	27	697	37
424	3	697	36
662	1	698	64
567	1	698	45
564	3	698	44
663	2	699	64
665	4	701	64
666	5	702	64
667	6	703	64
534	23	703	39
488	7	703	38
83	22	703	4
669	8	714	64
52	11	714	3
670	9	715	64
671	10	716	64
676	15	705	64
677	16	706	64
683	22	695	64
38	17	695	2
684	23	696	64
685	24	708	64
686	25	709	64
688	27	711	64
689	28	712	64
690	29	713	64
721	0	755	66
237	26	755	9
184	3	755	8
722	1	756	66
479	28	756	37
425	4	756	36
246	5	756	10
723	2	757	66
724	3	758	66
691	0	758	65
679	18	758	64
117	26	758	5
725	4	759	66
726	5	760	66
727	6	751	66
728	7	752	66
729	8	753	66
730	9	754	66
732	11	767	66
733	12	768	66
113	22	768	5
734	13	769	66
458	7	769	37
435	14	769	36
240	29	769	9
194	13	769	8
735	14	770	66
73	12	770	4
737	16	772	66
738	17	773	66
394	3	773	30
740	19	775	66
381	0	775	28
741	20	761	66
39	18	761	2
742	21	780	66
743	22	762	66
392	1	762	30
744	23	763	66
213	2	763	9
200	19	763	8
745	24	764	66
746	25	765	66
748	27	777	66
150	29	777	6
749	28	778	66
750	29	779	66
781	0	787	68
524	13	787	39
483	2	787	38
782	1	789	68
783	2	788	68
784	3	790	68
785	4	786	68
791	0	797	70
792	1	799	70
793	2	798	70
794	3	800	70
795	4	796	70
801	0	809	72
802	1	810	72
804	3	807	72
805	4	808	72
811	0	820	74
85	24	820	4
813	2	816	74
815	4	818	74
35	14	818	2
825	4	829	76
831	0	837	78
834	3	840	78
841	0	848	80
530	19	848	39
497	16	848	38
845	4	846	80
658	2	846	63
651	0	846	62
852	1	860	82
256	15	860	10
853	2	857	82
854	3	858	82
521	10	858	39
503	22	858	38
855	4	856	82
647	1	856	61
641	0	856	60
861	0	886	84
862	1	887	84
863	2	888	84
864	3	889	84
865	4	890	84
239	28	890	9
193	12	890	8
866	5	881	84
868	7	884	84
869	8	883	84
870	9	893	84
871	10	894	84
872	11	895	84
873	12	896	84
271	0	896	11
264	23	896	10
874	13	897	84
826	0	897	77
823	2	897	76
618	2	897	55
611	0	897	54
876	15	899	84
877	16	900	84
878	17	885	84
879	18	891	84
880	19	892	84
710	19	892	65
687	26	892	64
901	0	922	86
902	1	923	86
905	4	940	86
906	5	938	86
908	7	929	86
909	8	927	86
910	9	928	86
637	1	928	59
632	1	928	58
911	10	930	86
912	11	931	86
914	13	933	86
511	0	933	39
500	19	933	38
115	24	933	5
915	14	934	86
916	15	936	86
917	16	937	86
919	18	921	86
920	19	924	86
776	25	924	67
747	26	924	66
941	0	950	88
942	1	946	88
943	2	948	88
944	3	949	88
945	4	947	88
951	0	957	90
952	1	958	90
953	2	959	90
954	3	956	90
955	4	960	90
961	0	967	92
962	1	968	92
963	2	970	92
964	3	966	92
965	4	969	92
972	1	979	94
973	2	980	94
935	14	980	87
918	17	980	86
974	3	976	94
975	4	977	94
981	0	987	96
700	9	987	65
664	3	987	64
145	24	987	6
982	1	989	96
992	1	1000	98
994	3	997	98
766	15	997	67
731	10	997	66
995	4	998	98
1001	0	1007	100
1002	1	1006	100
1003	2	1010	100
830	4	1010	77
824	3	1010	76
1005	4	1009	100
693	2	1009	65
681	20	1009	64
397	1	1009	31
1011	0	1017	102
396	0	1017	31
1012	1	1016	102
1014	3	1020	102
1015	4	1018	102
1021	0	1027	104
459	8	1027	37
6	5	1027	1
436	15	1027	36
1022	1	1026	104
1023	2	1028	104
1024	3	1030	104
1025	4	1029	104
1032	1	1039	106
1033	2	1036	106
1034	3	1040	106
1035	4	1037	106
1041	0	1049	108
1042	1	1050	108
1043	2	1046	108
1044	3	1047	108
1045	4	1048	108
1051	0	1057	110
384	3	1057	28
1052	1	1058	110
1053	2	1059	110
1054	3	1056	110
1055	4	1060	110
1061	0	1098	112
556	0	1098	43
555	4	1098	42
1062	1	1099	112
774	23	1099	67
739	18	1099	66
48	7	1099	3
1063	2	1100	112
806	0	1100	73
803	2	1100	72
1064	3	1081	112
717	26	1081	65
672	11	1081	64
1065	4	1082	112
718	27	1082	65
673	12	1082	64
110	19	1082	5
457	6	1082	37
434	13	1082	36
1066	5	1083	112
1067	6	1084	112
80	19	1084	4
1068	7	1085	112
1069	8	1088	112
882	1	1088	85
867	6	1088	84
466	15	1088	37
439	18	1088	36
259	18	1088	10
297	26	1089	11
1070	9	1089	112
512	1	1089	39
501	20	1089	38
260	19	1089	10
1071	10	1093	112
516	5	1093	39
506	25	1093	38
59	18	1093	3
1072	11	1086	112
1073	12	1087	112
390	4	1087	29
1074	13	1091	112
771	20	1091	67
736	15	1091	66
1075	14	1092	112
1076	15	1094	112
160	9	1094	7
358	2	1095	23
1077	16	1095	112
1078	17	1096	112
408	2	1096	33
1079	18	1097	112
1080	19	1090	112
327	1	1128	17
1101	0	1128	114
999	3	1128	99
991	0	1128	98
1102	1	1129	114
819	3	1129	75
812	1	1129	74
578	2	1129	47
571	0	1129	46
1103	2	1130	114
704	13	1130	65
668	7	1130	64
1104	3	1133	114
828	2	1133	77
822	1	1133	76
376	0	1133	27
1105	4	1121	114
720	29	1121	65
675	14	1121	64
529	18	1121	39
496	15	1121	38
1107	6	1123	114
1008	2	1123	101
1004	3	1123	100
859	3	1123	83
851	0	1123	82
221	10	1123	9
196	15	1123	8
1108	7	1124	114
694	3	1124	65
682	21	1124	64
84	23	1124	4
1109	8	1125	114
898	17	1125	85
875	14	1125	84
692	1	1125	65
680	19	1125	64
386	0	1125	29
1110	9	1126	114
659	3	1126	63
652	1	1126	62
1111	10	1127	114
1112	11	1134	114
1113	12	1135	114
827	1	1135	77
821	0	1135	76
400	4	1135	31
1114	13	1136	114
1019	3	1136	103
1013	2	1136	102
839	3	1136	79
835	4	1136	78
1115	14	1137	114
153	2	1137	7
1116	15	1138	114
164	13	1138	7
1117	16	1139	114
1118	17	1140	114
1119	18	1131	114
1120	19	1132	114
1151	0	1160	118
1147	1	1160	117
1143	2	1160	116
1152	1	1156	118
1148	2	1156	117
1144	3	1156	116
817	1	1156	75
814	3	1156	74
520	9	1156	39
510	29	1156	38
123	2	1156	6
1153	2	1157	118
1149	3	1157	117
1145	4	1157	116
1154	3	1158	118
1150	4	1158	117
1141	0	1158	116
1155	4	1159	118
1146	0	1159	117
1142	1	1159	116
1171	0	1180	122
1167	1	1180	121
1163	2	1180	120
1172	1	1176	122
1168	2	1176	121
1164	3	1176	120
986	0	1176	97
985	4	1176	96
1173	2	1177	122
1169	3	1177	121
1165	4	1177	120
1174	3	1178	122
1170	4	1178	121
1161	0	1178	120
1175	4	1179	122
1166	0	1179	121
1162	1	1179	120
1181	0	1188	124
925	4	1188	87
903	2	1188	86
342	1	1189	20
1182	1	1189	124
996	0	1189	99
993	2	1189	98
360	4	1190	23
355	4	1190	22
1183	2	1190	124
990	4	1190	97
984	3	1190	96
325	4	1186	16
1184	3	1186	124
1185	4	1187	124
217	6	1187	9
189	8	1187	8
1191	0	1198	126
838	2	1198	79
832	1	1198	78
379	3	1198	27
340	4	1199	19
1192	1	1199	126
1193	2	1196	126
978	2	1196	95
971	0	1196	94
347	1	1197	21
1194	3	1197	126
988	2	1197	97
983	2	1197	96
277	6	1200	11
1195	4	1200	126
249	8	1200	10
1201	0	1209	128
836	0	1209	79
833	2	1209	78
630	4	1209	57
621	0	1209	56
1202	1	1210	128
147	26	1210	6
1203	2	1206	128
1204	3	1207	128
939	18	1207	87
907	6	1207	86
849	3	1207	81
842	1	1207	80
228	17	1207	9
199	18	1207	8
1205	4	1208	128
640	4	1208	59
631	0	1208	58
1211	0	1217	130
1122	1	1217	115
1106	5	1217	114
719	28	1217	65
674	13	1217	64
114	23	1217	5
1212	1	1219	130
932	11	1219	87
913	12	1219	86
1213	2	1220	130
616	0	1220	55
614	3	1220	54
1214	3	1218	130
617	1	1218	55
615	4	1218	54
1215	4	1216	130
1038	2	1216	107
1031	0	1216	106
847	1	1216	81
843	2	1216	80
1221	0	1228	132
1222	1	1229	132
707	16	1229	65
678	17	1229	64
385	4	1229	28
1223	2	1230	132
1224	3	1226	132
1225	4	1227	132
1231	0	1239	134
468	17	1239	37
441	20	1239	36
1232	1	1240	134
926	5	1240	87
904	3	1240	86
1233	2	1236	134
850	4	1236	81
844	3	1236	80
474	23	1236	37
121	0	1236	6
450	29	1236	36
1234	3	1237	134
1235	4	1238	134
\.


--
-- Data for Name: quizzes; Type: TABLE DATA; Schema: public; Owner: pedro
--

COPY public.quizzes (id, available_date, generation_date, number, series, title, type, version, year) FROM stdin;
101	\N	\N	101	3	data-2017-test-3-test-31B	TEST	AB	2017
102	\N	\N	102	3	data-2017-test-3-test-32A	TEST	BA	2017
103	\N	\N	103	3	data-2017-test-3-test-32B	TEST	BB	2017
104	\N	\N	104	3	data-2017-test-3-test-33A	TEST	CA	2017
105	\N	\N	105	3	data-2017-test-3-test-33B	TEST	CB	2017
106	\N	\N	106	4	data-2017-test-4-test-41A	TEST	AA	2017
107	\N	\N	107	4	data-2017-test-4-test-41B	TEST	AB	2017
108	\N	\N	108	4	data-2017-test-4-test-42A	TEST	BA	2017
1	\N	\N	1	1	data-2010-primeiro-exame-20110113-companion-en	EXAM	A	2010
2	\N	\N	2	1	data-2011-exam1-exame-20120109-companion-en	EXAM	A	2011
3	\N	\N	3	2	data-2011-exam2-exame-20120203-companion-en	EXAM	A	2011
4	\N	\N	4	1	data-2012-exam1-exame-20130109-companion-en	EXAM	A	2012
5	\N	\N	5	2	data-2012-exam2-exame-20130201-companion-en	EXAM	A	2012
6	\N	\N	6	1	data-2013-exam1-exame-20140110-companion-en	EXAM	A	2013
7	\N	\N	7	2	data-2013-exam2-exame-20140130-companion-en	EXAM	A	2013
8	\N	\N	8	1	data-2014-exam-exam20150109-exame-20150109-companion-A	EXAM	A	2014
9	\N	\N	9	1	data-2014-exam-exam20150109-exame-20150109-companion-B	EXAM	B	2014
10	\N	\N	10	2	data-2014-exam-exam20150127-exame-20150127-companion-A	EXAM	A	2014
11	\N	\N	11	2	data-2014-exam-exam20150127-exame-20150127-companion-B	EXAM	B	2014
12	\N	\N	12	1	data-2014-test-1-test-11A-17co╠Бpias	TEST	AA	2014
13	\N	\N	13	1	data-2014-test-1-test-12A-24co╠Бpias	TEST	BA	2014
14	\N	\N	14	1	data-2014-test-1-test-13A-17co╠Бpias	TEST	CA	2014
15	\N	\N	15	1	data-2014-test-1-test-14A-24co╠Бpias	TEST	DA	2014
16	\N	\N	16	2	data-2014-test-2-test-21A-18co╠Бpias	TEST	AA	2014
17	\N	\N	17	2	data-2014-test-2-test-22A-18co╠Бpias	TEST	BA	2014
18	\N	\N	18	2	data-2014-test-2-test-23A-18co╠Бpias	TEST	CA	2014
19	\N	\N	19	2	data-2014-test-2-test-24A-18co╠Бpias	TEST	DA	2014
20	\N	\N	20	3	data-2014-test-3-test-31A-18copias	TEST	AA	2014
21	\N	\N	21	3	data-2014-test-3-test-32A-18copias	TEST	BA	2014
22	\N	\N	22	3	data-2014-test-3-test-33A-18copias	TEST	CA	2014
23	\N	\N	23	3	data-2014-test-3-test-34A-18copias	TEST	DA	2014
24	\N	\N	24	4	data-2014-test-4-test-41A-17copias	TEST	AA	2014
25	\N	\N	25	4	data-2014-test-4-test-42A-16copias	TEST	BA	2014
26	\N	\N	26	4	data-2014-test-4-test-43A-16copias	TEST	CA	2014
27	\N	\N	27	4	data-2014-test-4-test-44A-18copias	TEST	DA	2014
28	\N	\N	28	5	data-2014-test-5-test-51A-17copias	TEST	AA	2014
29	\N	\N	29	5	data-2014-test-5-test-52A-16copias	TEST	BA	2014
30	\N	\N	30	5	data-2014-test-5-test-53A-16copias	TEST	CA	2014
31	\N	\N	31	5	data-2014-test-5-test-54A-18copias	TEST	DA	2014
32	\N	\N	32	6	data-2014-test-6-test-61A-17copias	TEST	AA	2014
33	\N	\N	33	6	data-2014-test-6-test-62A-16copias	TEST	BA	2014
34	\N	\N	34	6	data-2014-test-6-test-63A-16copias	TEST	CA	2014
35	\N	\N	35	6	data-2014-test-6-test-64A-18copias	TEST	DA	2014
36	\N	\N	36	1	data-2015-exams-20160108Exam-exame-20160108-companion-A	EXAM	A	2015
37	\N	\N	37	1	data-2015-exams-20160108Exam-exame-20160108-companion-B	EXAM	B	2015
38	\N	\N	38	2	data-2015-exams-20160126Exam-exame-20160126-companion-A	EXAM	A	2015
39	\N	\N	39	2	data-2015-exams-20160126Exam-exame-20160126-companion-B	EXAM	B	2015
40	\N	\N	40	1	data-2015-test-1-test-11A	TEST	AA	2015
41	\N	\N	41	1	data-2015-test-1-test-11B	TEST	AB	2015
42	\N	\N	42	1	data-2015-test-1-test-12A	TEST	BA	2015
43	\N	\N	43	1	data-2015-test-1-test-12B	TEST	BB	2015
44	\N	\N	44	1	data-2015-test-1-test-13A	TEST	CA	2015
45	\N	\N	45	1	data-2015-test-1-test-13B	TEST	CB	2015
46	\N	\N	46	2	data-2015-test-2-test-21A	TEST	AA	2015
47	\N	\N	47	2	data-2015-test-2-test-21B	TEST	AB	2015
48	\N	\N	48	2	data-2015-test-2-test-22A	TEST	BA	2015
49	\N	\N	49	2	data-2015-test-2-test-22B	TEST	BB	2015
50	\N	\N	50	2	data-2015-test-2-test-23A	TEST	CA	2015
51	\N	\N	51	2	data-2015-test-2-test-23B	TEST	CB	2015
52	\N	\N	52	3	data-2015-test-3-test-31A	TEST	AA	2015
53	\N	\N	53	3	data-2015-test-3-test-31B	TEST	AB	2015
54	\N	\N	54	3	data-2015-test-3-test-32A	TEST	BA	2015
55	\N	\N	55	3	data-2015-test-3-test-32B	TEST	BB	2015
56	\N	\N	56	3	data-2015-test-3-test-33A	TEST	CA	2015
57	\N	\N	57	3	data-2015-test-3-test-33B	TEST	CC	2015
58	\N	\N	58	4	data-2015-test-4-test-41A	TEST	AA	2015
59	\N	\N	59	4	data-2015-test-4-test-41B	TEST	AB	2015
60	\N	\N	60	4	data-2015-test-4-test-42A	TEST	BA	2015
61	\N	\N	61	4	data-2015-test-4-test-42B	TEST	BB	2015
62	\N	\N	62	4	data-2015-test-4-test-43A	TEST	CA	2015
63	\N	\N	63	4	data-2015-test-4-test-43B	TEST	CB	2015
64	\N	\N	64	1	data-2016-20170113Exam-exame-20170113-companion-A	EXAM	A	2016
65	\N	\N	65	1	data-2016-20170113Exam-exame-20170113-companion-B	EXAM	B	2016
66	\N	\N	66	2	data-2016-20170131Exam-exame-20170131-companion-A	EXAM	A	2016
67	\N	\N	67	2	data-2016-20170131Exam-exame-20170131-companion-B	EXAM	B	2016
68	\N	\N	68	1	data-2016-test-1-test-11A	TEST	AA	2016
69	\N	\N	69	1	data-2016-test-1-test-11B	TEST	AB	2016
70	\N	\N	70	1	data-2016-test-1-test-12A	TEST	BA	2016
71	\N	\N	71	1	data-2016-test-1-test-12B	TEST	BB	2016
72	\N	\N	72	2	data-2016-test-2-test-21A	TEST	AA	2016
73	\N	\N	73	2	data-2016-test-2-test-21B	TEST	AB	2016
74	\N	\N	74	2	data-2016-test-2-test-22A	TEST	BA	2016
75	\N	\N	75	2	data-2016-test-2-test-22B	TEST	BB	2016
76	\N	\N	76	3	data-2016-test-3-test-31A	TEST	AA	2016
77	\N	\N	77	3	data-2016-test-3-test-31B	TEST	AB	2016
78	\N	\N	78	3	data-2016-test-3-test-32A	TEST	BA	2016
79	\N	\N	79	3	data-2016-test-3-test-32B	TEST	BB	2016
80	\N	\N	80	4	data-2016-test-4-test-41A	TEST	AA	2016
81	\N	\N	81	4	data-2016-test-4-test-41B	TEST	AB	2016
82	\N	\N	82	4	data-2016-test-4-test-42A	TEST	BA	2016
83	\N	\N	83	4	data-2016-test-4-test-42B	TEST	BB	2016
84	\N	\N	84	1	data-2017-20180112-Exam-exame-20180112-companion-A	EXAM	A	2017
85	\N	\N	85	1	data-2017-20180112-Exam-exame-20180112-companion-B	EXAM	B	2017
86	\N	\N	86	2	data-2017-20180130-Exam-exame-20180130-companion-A	EXAM	A	2017
87	\N	\N	87	2	data-2017-20180130-Exam-exame-20180130-companion-B	EXAM	B	2017
88	\N	\N	88	1	data-2017-test-1-test-11A	TEST	AA	2017
89	\N	\N	89	1	data-2017-test-1-test-11B	TEST	AB	2017
90	\N	\N	90	1	data-2017-test-1-test-12A	TEST	BA	2017
91	\N	\N	91	1	data-2017-test-1-test-12B	TEST	BB	2017
92	\N	\N	92	1	data-2017-test-1-test-13A	TEST	CA	2017
93	\N	\N	93	1	data-2017-test-1-test-13B	TEST	CB	2017
94	\N	\N	94	2	data-2017-test-2-test-21-A	TEST	AA	2017
95	\N	\N	95	2	data-2017-test-2-test-21-B	TEST	AB	2017
96	\N	\N	96	2	data-2017-test-2-test-22-A	TEST	BA	2017
97	\N	\N	97	2	data-2017-test-2-test-22-B	TEST	BB	2017
98	\N	\N	98	2	data-2017-test-2-test-23-A	TEST	CA	2017
99	\N	\N	99	2	data-2017-test-2-test-23-B	TEST	CB	2017
100	\N	\N	100	3	data-2017-test-3-test-31A	TEST	AA	2017
109	\N	\N	109	4	data-2017-test-4-test-42B	TEST	BB	2017
110	\N	\N	110	4	data-2017-test-4-test-43A	TEST	CA	2017
111	\N	\N	111	4	data-2017-test-4-test-43B	TEST	CB	2017
112	\N	\N	112	1	data-2018-20190111-Exam-exame-20190111-companion-A	EXAM	A	2018
113	\N	\N	113	1	data-2018-20190111-Exam-exame-20190111-companion-B	EXAM	B	2018
114	\N	\N	114	2	data-2018-20190129-Exam-exame-20190129-companion-A	EXAM	A	2018
115	\N	\N	115	2	data-2018-20190129-Exam-exame-20190129-companion-B	EXAM	B	2018
116	\N	\N	116	1	data-2018-test-1-test-11-A	TEST	AA	2018
117	\N	\N	117	1	data-2018-test-1-test-11-B	TEST	AB	2018
118	\N	\N	118	1	data-2018-test-1-test-11-C	TEST	AC	2018
119	\N	\N	119	1	data-2018-test-1-test-11-D	TEST	AD	2018
120	\N	\N	120	1	data-2018-test-1-test-12-A	TEST	BA	2018
121	\N	\N	121	1	data-2018-test-1-test-12-B	TEST	BB	2018
122	\N	\N	122	1	data-2018-test-1-test-12-C	TEST	BC	2018
123	\N	\N	123	1	data-2018-test-1-test-12-D	TEST	BD	2018
124	\N	\N	124	2	data-2018-test-2-test-21-A	TEST	AA	2018
125	\N	\N	125	2	data-2018-test-2-test-21-B	TEST	AB	2018
126	\N	\N	126	2	data-2018-test-2-test-22-A	TEST	BA	2018
127	\N	\N	127	2	data-2018-test-2-test-22-B	TEST	BB	2018
128	\N	\N	128	3	data-2018-test-3-test-31-A	TEST	AA	2018
129	\N	\N	129	3	data-2018-test-3-test-31-B	TEST	AB	2018
130	\N	\N	130	3	data-2018-test-3-test-32-A	TEST	BA	2018
131	\N	\N	131	3	data-2018-test-3-test-32-B	TEST	BB	2018
132	\N	\N	132	4	data-2018-test-4-test-41-A	TEST	AA	2018
133	\N	\N	133	4	data-2018-test-4-test-41-B	TEST	AB	2018
134	\N	\N	134	4	data-2018-test-4-test-42-A	TEST	BA	2018
135	\N	\N	135	4	data-2018-test-4-test-42-B	TEST	BB	2018
\.


--
-- Data for Name: topics; Type: TABLE DATA; Schema: public; Owner: pedro
--

COPY public.topics (id, name) FROM stdin;
\.


--
-- Data for Name: topics_questions; Type: TABLE DATA; Schema: public; Owner: pedro
--

COPY public.topics_questions (topics_id, questions_id) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: pedro
--

COPY public.users (id, name, number, role, username, year) FROM stdin;
150	1,0	\N	STUDENT	\N	2014
151	2,0	\N	STUDENT	\N	2014
152	3,0	\N	STUDENT	\N	2014
153	5,0	\N	STUDENT	\N	2014
155	7,0	\N	STUDENT	\N	2014
108	48,0	108	STUDENT	\N	2013
111	51,0	111	STUDENT	\N	2013
112	52,0	112	STUDENT	\N	2013
113	53,0	113	STUDENT	\N	2013
115	55,0	115	STUDENT	\N	2013
116	56,0	116	STUDENT	\N	2013
117	57,0	117	STUDENT	\N	2013
118	58,0	118	STUDENT	\N	2013
119	59,0	119	STUDENT	\N	2013
120	60,0	120	STUDENT	\N	2013
121	61,0	121	STUDENT	\N	2013
124	64,0	124	STUDENT	\N	2013
125	65,0	125	STUDENT	\N	2013
128	68,0	128	STUDENT	\N	2013
131	71,0	131	STUDENT	\N	2013
132	72,0	132	STUDENT	\N	2013
133	73,0	133	STUDENT	\N	2013
134	74,0	134	STUDENT	\N	2013
136	76,0	136	STUDENT	\N	2013
137	77,0	137	STUDENT	\N	2013
138	78,0	138	STUDENT	\N	2013
139	79,0	139	STUDENT	\N	2013
140	80,0	140	STUDENT	\N	2013
141	81,0	141	STUDENT	\N	2013
143	83,0	143	STUDENT	\N	2013
145	85,0	145	STUDENT	\N	2013
146	86,0	146	STUDENT	\N	2013
147	87,0	147	STUDENT	\N	2013
149	89,0	149	STUDENT	\N	2013
13	13,0	13	STUDENT	\N	2011
11	11,0	11	STUDENT	\N	2011
35	35,0	35	STUDENT	\N	2011
1	1,0	1	STUDENT	\N	2011
8	8,0	8	STUDENT	\N	2011
19	19,0	19	STUDENT	\N	2011
27	27,0	27	STUDENT	\N	2011
29	29,0	29	STUDENT	\N	2011
36	36,0	36	STUDENT	\N	2011
52	52,0	52	STUDENT	\N	2011
3	3,0	3	STUDENT	\N	2011
6	6,0	6	STUDENT	\N	2011
23	23,0	23	STUDENT	\N	2011
31	31,0	31	STUDENT	\N	2011
46	46,0	46	STUDENT	\N	2011
73	13,0	73	STUDENT	\N	2013
91	31,0	91	STUDENT	\N	2013
92	32,0	92	STUDENT	\N	2013
98	38,0	98	STUDENT	\N	2013
76	16,0	76	STUDENT	\N	2013
154	6,0	154	STUDENT	\N	2014
157	15,0	157	STUDENT	\N	2014
156	12,0	156	STUDENT	\N	2014
163	42,0	\N	STUDENT	\N	2014
165	50,0	\N	STUDENT	\N	2014
167	59,0	\N	STUDENT	\N	2014
169	64,0	\N	STUDENT	\N	2014
173	101,0	\N	STUDENT	\N	2014
174	102,0	\N	STUDENT	\N	2014
176	112,0	\N	STUDENT	\N	2014
178	117,0	\N	STUDENT	\N	2014
180	120,0	\N	STUDENT	\N	2014
181	121,0	\N	STUDENT	\N	2014
182	122,0	\N	STUDENT	\N	2014
183	123,0	\N	STUDENT	\N	2014
184	124,0	\N	STUDENT	\N	2014
185	128,0	\N	STUDENT	\N	2014
186	130,0	\N	STUDENT	\N	2014
187	135,0	\N	STUDENT	\N	2014
188	138,0	\N	STUDENT	\N	2014
189	145,0	\N	STUDENT	\N	2014
190	154,0	\N	STUDENT	\N	2014
191	156,0	\N	STUDENT	\N	2014
192	158,0	\N	STUDENT	\N	2014
308	2,0	\N	STUDENT	\N	2015
311	5,0	\N	STUDENT	\N	2015
314	13,0	\N	STUDENT	\N	2015
199	20,0	199	STUDENT	\N	2014
203	29,0	203	STUDENT	\N	2014
207	43,0	207	STUDENT	\N	2014
209	48,0	209	STUDENT	\N	2014
211	53,0	211	STUDENT	\N	2014
215	70,0	215	STUDENT	\N	2014
226	93,0	226	STUDENT	\N	2014
262	47,0	262	STUDENT	\N	2014
264	54,0	264	STUDENT	\N	2014
267	57,0	267	STUDENT	\N	2014
269	62,0	269	STUDENT	\N	2014
272	67,0	272	STUDENT	\N	2014
273	68,0	273	STUDENT	\N	2014
274	71,0	274	STUDENT	\N	2014
275	80,0	275	STUDENT	\N	2014
280	90,0	280	STUDENT	\N	2014
282	92,0	282	STUDENT	\N	2014
283	95,0	283	STUDENT	\N	2014
284	96,0	284	STUDENT	\N	2014
288	103,0	288	STUDENT	\N	2014
290	110,0	290	STUDENT	\N	2014
293	126,0	293	STUDENT	\N	2014
295	134,0	295	STUDENT	\N	2014
300	144,0	300	STUDENT	\N	2014
306	155,0	306	STUDENT	\N	2014
307	157,0	307	STUDENT	\N	2014
248	16,0	248	STUDENT	\N	2014
256	35,0	256	STUDENT	\N	2014
261	45,0	261	STUDENT	\N	2014
263	49,0	263	STUDENT	\N	2014
266	56,0	266	STUDENT	\N	2014
268	61,0	268	STUDENT	\N	2014
277	82,0	277	STUDENT	\N	2014
286	98,0	286	STUDENT	\N	2014
292	115,0	292	STUDENT	\N	2014
297	140,0	297	STUDENT	\N	2014
298	141,0	298	STUDENT	\N	2014
299	142,0	299	STUDENT	\N	2014
302	147,0	302	STUDENT	\N	2014
305	152,0	305	STUDENT	\N	2014
258	38,0	258	STUDENT	\N	2014
289	106,0	289	STUDENT	\N	2014
294	129,0	294	STUDENT	\N	2014
301	146,0	301	STUDENT	\N	2014
303	150,0	303	STUDENT	\N	2014
175	107,0	175	STUDENT	\N	2014
162	41,0	162	STUDENT	\N	2014
170	73,0	170	STUDENT	\N	2014
171	74,0	171	STUDENT	\N	2014
177	114,0	177	STUDENT	\N	2014
159	21,0	159	STUDENT	\N	2014
160	23,0	160	STUDENT	\N	2014
172	78,0	172	STUDENT	\N	2014
158	19,0	158	STUDENT	\N	2014
161	26,0	161	STUDENT	\N	2014
164	44,0	164	STUDENT	\N	2014
166	52,0	166	STUDENT	\N	2014
168	63,0	168	STUDENT	\N	2014
312	7,0	312	STUDENT	\N	2015
309	3,0	309	STUDENT	\N	2015
316	24,0	\N	STUDENT	\N	2015
317	25,0	\N	STUDENT	\N	2015
319	51,0	\N	STUDENT	\N	2015
320	55,0	\N	STUDENT	\N	2015
321	65,0	\N	STUDENT	\N	2015
322	71,0	\N	STUDENT	\N	2015
385	1,0	\N	STUDENT	\N	2016
386	2,0	\N	STUDENT	\N	2016
387	3,0	\N	STUDENT	\N	2016
390	6,0	\N	STUDENT	\N	2016
393	11,0	\N	STUDENT	\N	2016
394	15,0	\N	STUDENT	\N	2016
396	28,0	\N	STUDENT	\N	2016
446	3,0	\N	STUDENT	\N	2017
447	6,0	\N	STUDENT	\N	2017
448	8,0	\N	STUDENT	\N	2017
449	9,0	\N	STUDENT	\N	2017
450	10,0	\N	STUDENT	\N	2017
451	11,0	\N	STUDENT	\N	2017
452	14,0	\N	STUDENT	\N	2017
454	17,0	\N	STUDENT	\N	2017
455	18,0	\N	STUDENT	\N	2017
457	24,0	\N	STUDENT	\N	2017
465	81,0	\N	STUDENT	\N	2017
466	83,0	\N	STUDENT	\N	2017
422	12,0	422	STUDENT	\N	2016
424	17,0	424	STUDENT	\N	2016
425	19,0	425	STUDENT	\N	2016
436	42,0	436	STUDENT	\N	2016
435	41,0	435	STUDENT	\N	2016
437	43,0	437	STUDENT	\N	2016
442	57,0	442	STUDENT	\N	2016
438	47,0	438	STUDENT	\N	2016
443	58,0	443	STUDENT	\N	2016
392	8,0	392	STUDENT	\N	2016
397	35,0	397	STUDENT	\N	2016
389	5,0	389	STUDENT	\N	2016
395	21,0	395	STUDENT	\N	2016
416	51,0	416	STUDENT	\N	2016
398	46,0	398	STUDENT	\N	2016
415	50,0	415	STUDENT	\N	2016
423	13,0	423	STUDENT	\N	2016
470	13,0	470	STUDENT	\N	2017
471	15,0	471	STUDENT	\N	2017
469	5,0	469	STUDENT	\N	2017
458	25,0	458	STUDENT	\N	2017
467	86,0	467	STUDENT	\N	2017
459	32,0	459	STUDENT	\N	2017
324	6,0	324	STUDENT	\N	2015
328	21,0	328	STUDENT	\N	2015
332	30,0	332	STUDENT	\N	2015
342	50,0	342	STUDENT	\N	2015
346	57,0	346	STUDENT	\N	2015
349	68,0	349	STUDENT	\N	2015
352	72,0	352	STUDENT	\N	2015
353	74,0	353	STUDENT	\N	2015
329	27,0	329	STUDENT	\N	2015
333	32,0	333	STUDENT	\N	2015
337	39,0	337	STUDENT	\N	2015
339	43,0	339	STUDENT	\N	2015
341	49,0	341	STUDENT	\N	2015
344	54,0	344	STUDENT	\N	2015
351	70,0	351	STUDENT	\N	2015
327	19,0	327	STUDENT	\N	2015
331	29,0	331	STUDENT	\N	2015
360	15,0	360	STUDENT	\N	2015
361	17,0	361	STUDENT	\N	2015
365	23,0	365	STUDENT	\N	2015
367	31,0	367	STUDENT	\N	2015
375	48,0	375	STUDENT	\N	2015
370	41,0	370	STUDENT	\N	2015
374	47,0	374	STUDENT	\N	2015
315	14,0	315	STUDENT	\N	2015
318	36,0	318	STUDENT	\N	2015
403	20,0	403	STUDENT	\N	2016
419	56,0	419	STUDENT	\N	2016
412	44,0	412	STUDENT	\N	2016
444	60,0	444	STUDENT	\N	2016
434	40,0	434	STUDENT	\N	2016
432	38,0	432	STUDENT	\N	2016
433	39,0	433	STUDENT	\N	2016
439	48,0	439	STUDENT	\N	2016
441	54,0	441	STUDENT	\N	2016
388	4,0	388	STUDENT	\N	2016
399	59,0	399	STUDENT	\N	2016
391	7,0	391	STUDENT	\N	2016
453	16,0	453	STUDENT	\N	2017
445	1,0	445	STUDENT	\N	2017
463	75,0	463	STUDENT	\N	2017
456	23,0	456	STUDENT	\N	2017
460	36,0	460	STUDENT	\N	2017
462	60,0	462	STUDENT	\N	2017
468	90,0	468	STUDENT	\N	2017
461	54,0	461	STUDENT	\N	2017
464	77,0	464	STUDENT	\N	2017
537	1,0	\N	STUDENT	\N	2018
538	2,0	\N	STUDENT	\N	2018
539	3,0	\N	STUDENT	\N	2018
540	4,0	\N	STUDENT	\N	2018
541	5,0	\N	STUDENT	\N	2018
542	6,0	\N	STUDENT	\N	2018
550	32,0	\N	STUDENT	\N	2018
552	38,0	\N	STUDENT	\N	2018
553	39,0	\N	STUDENT	\N	2018
554	55,0	\N	STUDENT	\N	2018
555	65,0	\N	STUDENT	\N	2018
2	2,0	2	STUDENT	\N	2011
7	7,0	7	STUDENT	\N	2011
15	15,0	15	STUDENT	\N	2011
18	18,0	18	STUDENT	\N	2011
25	25,0	25	STUDENT	\N	2011
28	28,0	28	STUDENT	\N	2011
32	32,0	32	STUDENT	\N	2011
51	51,0	51	STUDENT	\N	2011
54	54,0	54	STUDENT	\N	2011
56	56,0	56	STUDENT	\N	2011
59	59,0	59	STUDENT	\N	2011
24	24,0	24	STUDENT	\N	2011
33	33,0	33	STUDENT	\N	2011
606	64,0	606	STUDENT	\N	2018
563	20,0	563	STUDENT	\N	2018
597	49,0	597	STUDENT	\N	2018
594	44,0	594	STUDENT	\N	2018
559	14,0	559	STUDENT	\N	2018
588	25,0	588	STUDENT	\N	2018
545	10,0	545	STUDENT	\N	2018
572	37,0	572	STUDENT	\N	2018
546	13,0	546	STUDENT	\N	2018
477	29,0	477	STUDENT	\N	2017
481	43,0	481	STUDENT	\N	2017
486	55,0	486	STUDENT	\N	2017
488	59,0	488	STUDENT	\N	2017
490	64,0	490	STUDENT	\N	2017
491	67,0	491	STUDENT	\N	2017
475	27,0	475	STUDENT	\N	2017
489	63,0	489	STUDENT	\N	2017
505	19,0	505	STUDENT	\N	2017
521	57,0	521	STUDENT	\N	2017
525	65,0	525	STUDENT	\N	2017
536	92,0	536	STUDENT	\N	2017
503	7,0	503	STUDENT	\N	2017
504	12,0	504	STUDENT	\N	2017
526	66,0	526	STUDENT	\N	2017
535	89,0	535	STUDENT	\N	2017
585	77,0	585	STUDENT	\N	2018
567	27,0	567	STUDENT	\N	2018
568	29,0	568	STUDENT	\N	2018
571	33,0	571	STUDENT	\N	2018
574	42,0	574	STUDENT	\N	2018
580	61,0	580	STUDENT	\N	2018
582	68,0	582	STUDENT	\N	2018
583	73,0	583	STUDENT	\N	2018
584	75,0	584	STUDENT	\N	2018
566	24,0	566	STUDENT	\N	2018
581	63,0	581	STUDENT	\N	2018
586	11,0	586	STUDENT	\N	2018
587	19,0	587	STUDENT	\N	2018
589	26,0	589	STUDENT	\N	2018
592	41,0	592	STUDENT	\N	2018
593	43,0	593	STUDENT	\N	2018
596	47,0	596	STUDENT	\N	2018
598	51,0	598	STUDENT	\N	2018
602	57,0	602	STUDENT	\N	2018
603	58,0	603	STUDENT	\N	2018
605	62,0	605	STUDENT	\N	2018
609	69,0	609	STUDENT	\N	2018
610	70,0	610	STUDENT	\N	2018
612	72,0	612	STUDENT	\N	2018
543	7,0	543	STUDENT	\N	2018
551	36,0	551	STUDENT	\N	2018
547	15,0	547	STUDENT	\N	2018
556	74,0	556	STUDENT	\N	2018
548	22,0	548	STUDENT	\N	2018
40	40,0	40	STUDENT	\N	2011
47	47,0	47	STUDENT	\N	2011
10	10,0	10	STUDENT	\N	2011
16	16,0	16	STUDENT	\N	2011
20	20,0	20	STUDENT	\N	2011
38	38,0	38	STUDENT	\N	2011
41	41,0	41	STUDENT	\N	2011
45	45,0	45	STUDENT	\N	2011
49	49,0	49	STUDENT	\N	2011
55	55,0	55	STUDENT	\N	2011
22	22,0	22	STUDENT	\N	2011
26	26,0	26	STUDENT	\N	2011
4	4,0	4	STUDENT	\N	2011
42	42,0	42	STUDENT	\N	2011
53	53,0	53	STUDENT	\N	2011
9	9,0	9	STUDENT	\N	2011
14	14,0	14	STUDENT	\N	2011
34	34,0	34	STUDENT	\N	2011
50	50,0	50	STUDENT	\N	2011
57	57,0	57	STUDENT	\N	2011
60	60,0	60	STUDENT	\N	2011
5	5,0	5	STUDENT	\N	2011
12	12,0	12	STUDENT	\N	2011
17	17,0	17	STUDENT	\N	2011
43	43,0	43	STUDENT	\N	2011
44	44,0	44	STUDENT	\N	2011
39	39,0	39	STUDENT	\N	2011
21	21,0	21	STUDENT	\N	2011
37	37,0	37	STUDENT	\N	2011
48	48,0	48	STUDENT	\N	2011
58	58,0	58	STUDENT	\N	2011
30	30,0	30	STUDENT	\N	2011
67	7,0	67	STUDENT	\N	2013
84	24,0	84	STUDENT	\N	2013
95	35,0	95	STUDENT	\N	2013
97	37,0	97	STUDENT	\N	2013
61	1,0	61	STUDENT	\N	2013
62	2,0	62	STUDENT	\N	2013
63	3,0	63	STUDENT	\N	2013
65	5,0	65	STUDENT	\N	2013
66	6,0	66	STUDENT	\N	2013
68	8,0	68	STUDENT	\N	2013
69	9,0	69	STUDENT	\N	2013
70	10,0	70	STUDENT	\N	2013
71	11,0	71	STUDENT	\N	2013
72	12,0	72	STUDENT	\N	2013
78	18,0	78	STUDENT	\N	2013
81	21,0	81	STUDENT	\N	2013
86	26,0	86	STUDENT	\N	2013
88	28,0	88	STUDENT	\N	2013
90	30,0	90	STUDENT	\N	2013
94	34,0	94	STUDENT	\N	2013
99	39,0	99	STUDENT	\N	2013
100	40,0	100	STUDENT	\N	2013
102	42,0	102	STUDENT	\N	2013
103	43,0	103	STUDENT	\N	2013
93	33,0	93	STUDENT	\N	2013
96	36,0	96	STUDENT	\N	2013
87	27,0	87	STUDENT	\N	2013
64	4,0	64	STUDENT	\N	2013
101	41,0	101	STUDENT	\N	2013
75	15,0	75	STUDENT	\N	2013
82	22,0	82	STUDENT	\N	2013
104	44,0	104	STUDENT	\N	2013
85	25,0	85	STUDENT	\N	2013
83	23,0	83	STUDENT	\N	2013
74	14,0	74	STUDENT	\N	2013
89	29,0	89	STUDENT	\N	2013
77	17,0	77	STUDENT	\N	2013
80	20,0	80	STUDENT	\N	2013
79	19,0	79	STUDENT	\N	2013
110	50,0	110	STUDENT	\N	2013
123	63,0	123	STUDENT	\N	2013
130	70,0	130	STUDENT	\N	2013
114	54,0	114	STUDENT	\N	2013
126	66,0	126	STUDENT	\N	2013
106	46,0	106	STUDENT	\N	2013
109	49,0	109	STUDENT	\N	2013
129	69,0	129	STUDENT	\N	2013
144	84,0	144	STUDENT	\N	2013
148	88,0	148	STUDENT	\N	2013
122	62,0	122	STUDENT	\N	2013
127	67,0	127	STUDENT	\N	2013
135	75,0	135	STUDENT	\N	2013
142	82,0	142	STUDENT	\N	2013
105	45,0	105	STUDENT	\N	2013
107	47,0	107	STUDENT	\N	2013
233	113,0	233	STUDENT	\N	2014
281	91,0	281	STUDENT	\N	2014
278	86,0	278	STUDENT	\N	2014
291	111,0	291	STUDENT	\N	2014
285	97,0	285	STUDENT	\N	2014
179	119,0	179	STUDENT	\N	2014
279	87,0	279	STUDENT	\N	2014
338	40,0	338	STUDENT	\N	2015
326	16,0	326	STUDENT	\N	2015
334	33,0	334	STUDENT	\N	2015
323	1,0	323	STUDENT	\N	2015
348	62,0	348	STUDENT	\N	2015
343	53,0	343	STUDENT	\N	2015
336	38,0	336	STUDENT	\N	2015
350	69,0	350	STUDENT	\N	2015
347	61,0	347	STUDENT	\N	2015
345	56,0	345	STUDENT	\N	2015
354	75,0	354	STUDENT	\N	2015
330	28,0	330	STUDENT	\N	2015
325	10,0	325	STUDENT	\N	2015
335	37,0	335	STUDENT	\N	2015
368	34,0	368	STUDENT	\N	2015
364	22,0	364	STUDENT	\N	2015
377	58,0	377	STUDENT	\N	2015
380	63,0	380	STUDENT	\N	2015
371	42,0	371	STUDENT	\N	2015
362	18,0	362	STUDENT	\N	2015
379	60,0	379	STUDENT	\N	2015
366	26,0	366	STUDENT	\N	2015
369	35,0	369	STUDENT	\N	2015
359	12,0	359	STUDENT	\N	2015
384	73,0	384	STUDENT	\N	2015
373	46,0	373	STUDENT	\N	2015
357	9,0	357	STUDENT	\N	2015
378	59,0	378	STUDENT	\N	2015
363	20,0	363	STUDENT	\N	2015
376	52,0	376	STUDENT	\N	2015
372	45,0	372	STUDENT	\N	2015
383	67,0	383	STUDENT	\N	2015
382	66,0	382	STUDENT	\N	2015
313	8,0	313	STUDENT	\N	2015
356	77,0	356	STUDENT	\N	2015
355	76,0	355	STUDENT	\N	2015
358	11,0	358	STUDENT	\N	2015
381	64,0	381	STUDENT	\N	2015
310	4,0	310	STUDENT	\N	2015
340	44,0	340	STUDENT	\N	2015
402	18,0	402	STUDENT	\N	2016
409	33,0	409	STUDENT	\N	2016
405	24,0	405	STUDENT	\N	2016
400	14,0	400	STUDENT	\N	2016
401	16,0	401	STUDENT	\N	2016
411	37,0	411	STUDENT	\N	2016
410	36,0	410	STUDENT	\N	2016
418	55,0	418	STUDENT	\N	2016
407	29,0	407	STUDENT	\N	2016
414	49,0	414	STUDENT	\N	2016
404	23,0	404	STUDENT	\N	2016
408	32,0	408	STUDENT	\N	2016
406	26,0	406	STUDENT	\N	2016
417	53,0	417	STUDENT	\N	2016
413	45,0	413	STUDENT	\N	2016
440	52,0	440	STUDENT	\N	2016
420	9,0	420	STUDENT	\N	2016
426	22,0	426	STUDENT	\N	2016
429	30,0	429	STUDENT	\N	2016
431	34,0	431	STUDENT	\N	2016
428	27,0	428	STUDENT	\N	2016
421	10,0	421	STUDENT	\N	2016
430	31,0	430	STUDENT	\N	2016
427	25,0	427	STUDENT	\N	2016
476	28,0	476	STUDENT	\N	2017
480	41,0	480	STUDENT	\N	2017
485	53,0	485	STUDENT	\N	2017
500	91,0	500	STUDENT	\N	2017
472	20,0	472	STUDENT	\N	2017
483	47,0	483	STUDENT	\N	2017
473	21,0	473	STUDENT	\N	2017
478	31,0	478	STUDENT	\N	2017
482	45,0	482	STUDENT	\N	2017
493	70,0	493	STUDENT	\N	2017
495	72,0	495	STUDENT	\N	2017
497	84,0	497	STUDENT	\N	2017
498	85,0	498	STUDENT	\N	2017
499	87,0	499	STUDENT	\N	2017
479	37,0	479	STUDENT	\N	2017
496	73,0	496	STUDENT	\N	2017
487	56,0	487	STUDENT	\N	2017
494	71,0	494	STUDENT	\N	2017
484	48,0	484	STUDENT	\N	2017
512	39,0	512	STUDENT	\N	2017
528	74,0	528	STUDENT	\N	2017
534	88,0	534	STUDENT	\N	2017
508	33,0	508	STUDENT	\N	2017
515	44,0	515	STUDENT	\N	2017
516	46,0	516	STUDENT	\N	2017
511	38,0	511	STUDENT	\N	2017
513	40,0	513	STUDENT	\N	2017
514	42,0	514	STUDENT	\N	2017
529	76,0	529	STUDENT	\N	2017
502	4,0	502	STUDENT	\N	2017
506	26,0	506	STUDENT	\N	2017
510	35,0	510	STUDENT	\N	2017
517	49,0	517	STUDENT	\N	2017
522	58,0	522	STUDENT	\N	2017
523	61,0	523	STUDENT	\N	2017
524	62,0	524	STUDENT	\N	2017
527	68,0	527	STUDENT	\N	2017
531	79,0	531	STUDENT	\N	2017
532	80,0	532	STUDENT	\N	2017
492	69,0	492	STUDENT	\N	2017
507	30,0	507	STUDENT	\N	2017
474	22,0	474	STUDENT	\N	2017
509	34,0	509	STUDENT	\N	2017
530	78,0	530	STUDENT	\N	2017
518	50,0	518	STUDENT	\N	2017
519	51,0	519	STUDENT	\N	2017
520	52,0	520	STUDENT	\N	2017
533	82,0	533	STUDENT	\N	2017
501	2,0	501	STUDENT	\N	2017
565	23,0	565	STUDENT	\N	2018
557	8,0	557	STUDENT	\N	2018
562	18,0	562	STUDENT	\N	2018
564	21,0	564	STUDENT	\N	2018
569	30,0	569	STUDENT	\N	2018
561	17,0	561	STUDENT	\N	2018
560	16,0	560	STUDENT	\N	2018
576	48,0	576	STUDENT	\N	2018
573	40,0	573	STUDENT	\N	2018
578	56,0	578	STUDENT	\N	2018
579	59,0	579	STUDENT	\N	2018
577	50,0	577	STUDENT	\N	2018
570	31,0	570	STUDENT	\N	2018
575	45,0	575	STUDENT	\N	2018
558	12,0	558	STUDENT	\N	2018
599	52,0	599	STUDENT	\N	2018
590	34,0	590	STUDENT	\N	2018
607	66,0	607	STUDENT	\N	2018
600	53,0	600	STUDENT	\N	2018
613	76,0	613	STUDENT	\N	2018
611	71,0	611	STUDENT	\N	2018
591	35,0	591	STUDENT	\N	2018
595	46,0	595	STUDENT	\N	2018
601	54,0	601	STUDENT	\N	2018
604	60,0	604	STUDENT	\N	2018
608	67,0	608	STUDENT	\N	2018
549	28,0	549	STUDENT	\N	2018
544	9,0	544	STUDENT	\N	2018
227	94,0	227	STUDENT	\N	2014
236	125,0	236	STUDENT	\N	2014
240	133,0	240	STUDENT	\N	2014
202	28,0	202	STUDENT	\N	2014
238	131,0	238	STUDENT	\N	2014
245	149,0	245	STUDENT	\N	2014
201	27,0	201	STUDENT	\N	2014
205	34,0	205	STUDENT	\N	2014
206	37,0	206	STUDENT	\N	2014
230	105,0	230	STUDENT	\N	2014
231	108,0	231	STUDENT	\N	2014
234	116,0	234	STUDENT	\N	2014
243	143,0	243	STUDENT	\N	2014
193	4,0	193	STUDENT	\N	2014
194	8,0	194	STUDENT	\N	2014
195	9,0	195	STUDENT	\N	2014
196	10,0	196	STUDENT	\N	2014
197	13,0	197	STUDENT	\N	2014
198	14,0	198	STUDENT	\N	2014
200	24,0	200	STUDENT	\N	2014
204	33,0	204	STUDENT	\N	2014
208	46,0	208	STUDENT	\N	2014
210	51,0	210	STUDENT	\N	2014
212	58,0	212	STUDENT	\N	2014
213	60,0	213	STUDENT	\N	2014
214	69,0	214	STUDENT	\N	2014
216	72,0	216	STUDENT	\N	2014
217	75,0	217	STUDENT	\N	2014
218	76,0	218	STUDENT	\N	2014
219	77,0	219	STUDENT	\N	2014
220	79,0	220	STUDENT	\N	2014
221	83,0	221	STUDENT	\N	2014
222	84,0	222	STUDENT	\N	2014
223	85,0	223	STUDENT	\N	2014
224	88,0	224	STUDENT	\N	2014
225	89,0	225	STUDENT	\N	2014
228	99,0	228	STUDENT	\N	2014
229	104,0	229	STUDENT	\N	2014
232	109,0	232	STUDENT	\N	2014
235	118,0	235	STUDENT	\N	2014
237	127,0	237	STUDENT	\N	2014
239	132,0	239	STUDENT	\N	2014
241	136,0	241	STUDENT	\N	2014
242	137,0	242	STUDENT	\N	2014
244	148,0	244	STUDENT	\N	2014
246	153,0	246	STUDENT	\N	2014
252	25,0	252	STUDENT	\N	2014
255	32,0	255	STUDENT	\N	2014
265	55,0	265	STUDENT	\N	2014
270	65,0	270	STUDENT	\N	2014
271	66,0	271	STUDENT	\N	2014
276	81,0	276	STUDENT	\N	2014
287	100,0	287	STUDENT	\N	2014
296	139,0	296	STUDENT	\N	2014
304	151,0	304	STUDENT	\N	2014
247	11,0	247	STUDENT	\N	2014
249	17,0	249	STUDENT	\N	2014
250	18,0	250	STUDENT	\N	2014
251	22,0	251	STUDENT	\N	2014
253	30,0	253	STUDENT	\N	2014
254	31,0	254	STUDENT	\N	2014
257	36,0	257	STUDENT	\N	2014
259	39,0	259	STUDENT	\N	2014
260	40,0	260	STUDENT	\N	2014
\.


--
-- Name: images_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pedro
--

SELECT pg_catalog.setval('public.images_id_seq', 134, true);


--
-- Name: options_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pedro
--

SELECT pg_catalog.setval('public.options_id_seq', 4960, true);


--
-- Name: question_answers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pedro
--

SELECT pg_catalog.setval('public.question_answers_id_seq', 21910, true);


--
-- Name: questions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pedro
--

SELECT pg_catalog.setval('public.questions_id_seq', 1240, true);


--
-- Name: quiz_answers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pedro
--

SELECT pg_catalog.setval('public.quiz_answers_id_seq', 1586, true);


--
-- Name: quiz_questions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pedro
--

SELECT pg_catalog.setval('public.quiz_questions_id_seq', 1240, true);


--
-- Name: quizzes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pedro
--

SELECT pg_catalog.setval('public.quizzes_id_seq', 135, true);


--
-- Name: topics_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pedro
--

SELECT pg_catalog.setval('public.topics_id_seq', 1, false);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pedro
--

SELECT pg_catalog.setval('public.users_id_seq', 613, true);


--
-- Name: images images_pkey; Type: CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_pkey PRIMARY KEY (id);


--
-- Name: options options_pkey; Type: CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.options
    ADD CONSTRAINT options_pkey PRIMARY KEY (id);


--
-- Name: question_answers question_answers_pkey; Type: CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.question_answers
    ADD CONSTRAINT question_answers_pkey PRIMARY KEY (id);


--
-- Name: questions questions_pkey; Type: CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.questions
    ADD CONSTRAINT questions_pkey PRIMARY KEY (id);


--
-- Name: quiz_answers quiz_answers_pkey; Type: CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.quiz_answers
    ADD CONSTRAINT quiz_answers_pkey PRIMARY KEY (id);


--
-- Name: quiz_questions quiz_questions_pkey; Type: CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.quiz_questions
    ADD CONSTRAINT quiz_questions_pkey PRIMARY KEY (id);


--
-- Name: quizzes quizzes_pkey; Type: CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.quizzes
    ADD CONSTRAINT quizzes_pkey PRIMARY KEY (id);


--
-- Name: topics topics_pkey; Type: CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.topics
    ADD CONSTRAINT topics_pkey PRIMARY KEY (id);


--
-- Name: topics_questions topics_questions_pkey; Type: CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.topics_questions
    ADD CONSTRAINT topics_questions_pkey PRIMARY KEY (topics_id, questions_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: question_indx_0; Type: INDEX; Schema: public; Owner: pedro
--

CREATE INDEX question_indx_0 ON public.questions USING btree (number);


--
-- Name: quizzes_indx_0; Type: INDEX; Schema: public; Owner: pedro
--

CREATE INDEX quizzes_indx_0 ON public.quizzes USING btree (number);


--
-- Name: images fk37qs4lwbc8u9pmjhtuxp0d264; Type: FK CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT fk37qs4lwbc8u9pmjhtuxp0d264 FOREIGN KEY (question_id) REFERENCES public.questions(id);


--
-- Name: topics_questions fk4lsm9y4c8dmvp5b2g5no87om7; Type: FK CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.topics_questions
    ADD CONSTRAINT fk4lsm9y4c8dmvp5b2g5no87om7 FOREIGN KEY (questions_id) REFERENCES public.questions(id);


--
-- Name: options fk5bmv46so2y5igt9o9n9w4fh6y; Type: FK CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.options
    ADD CONSTRAINT fk5bmv46so2y5igt9o9n9w4fh6y FOREIGN KEY (question_id) REFERENCES public.questions(id);


--
-- Name: quiz_answers fk8m696ke2wtle7jrw9fp3ey2yt; Type: FK CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.quiz_answers
    ADD CONSTRAINT fk8m696ke2wtle7jrw9fp3ey2yt FOREIGN KEY (quiz_id) REFERENCES public.quizzes(id);


--
-- Name: question_answers fkamgy9t1s8asrsl00l5p8lold4; Type: FK CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.question_answers
    ADD CONSTRAINT fkamgy9t1s8asrsl00l5p8lold4 FOREIGN KEY (quiz_answer_id) REFERENCES public.quiz_answers(id);


--
-- Name: quiz_questions fkanfmgf6ksbdnv7ojb0pfve54q; Type: FK CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.quiz_questions
    ADD CONSTRAINT fkanfmgf6ksbdnv7ojb0pfve54q FOREIGN KEY (quiz_id) REFERENCES public.quizzes(id);


--
-- Name: quiz_answers fkcqyj36ibcadfva2rc6psle7u1; Type: FK CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.quiz_answers
    ADD CONSTRAINT fkcqyj36ibcadfva2rc6psle7u1 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: question_answers fkdg6xjp7x8wn0pd8vctlqll5ok; Type: FK CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.question_answers
    ADD CONSTRAINT fkdg6xjp7x8wn0pd8vctlqll5ok FOREIGN KEY (option_id) REFERENCES public.options(id);


--
-- Name: quiz_questions fkev41c723fx659v28pjycox15o; Type: FK CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.quiz_questions
    ADD CONSTRAINT fkev41c723fx659v28pjycox15o FOREIGN KEY (question_id) REFERENCES public.questions(id);


--
-- Name: question_answers fkjy80p5uktr31ehg8d5v53nfq2; Type: FK CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.question_answers
    ADD CONSTRAINT fkjy80p5uktr31ehg8d5v53nfq2 FOREIGN KEY (quiz_question_id) REFERENCES public.quiz_questions(id);


--
-- Name: topics_questions fkpv4eapx6jhsr3fnqygajogkyc; Type: FK CONSTRAINT; Schema: public; Owner: pedro
--

ALTER TABLE ONLY public.topics_questions
    ADD CONSTRAINT fkpv4eapx6jhsr3fnqygajogkyc FOREIGN KEY (topics_id) REFERENCES public.topics(id);


--
-- PostgreSQL database dump complete
--

