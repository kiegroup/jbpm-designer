DROP VIEW access;

CREATE VIEW access AS
    SELECT context_name.id AS context_id, context_name.uri AS context_name, subject_name.id AS subject_id, subject_name.uri AS subject_name, object_name.id AS object_id, object_name.uri AS object_name, access.id AS access_id, access.scheme AS access_scheme, access.term AS access_term FROM interaction access, structure context, identity context_name, structure subject_axis, identity subject_name, structure object_axis, identity object_name WHERE ((((((access.subject = context.hierarchy) AND (context.ident_id = context_name.id)) AND ((access.subject = subject_axis.hierarchy) OR (access.subject_descend AND (subject_axis.hierarchy ~~ (access.subject || '_%'::text))))) AND ((((((NOT access.object_restrict_to_parent) AND access.object_self) AND (access.object = object_axis.hierarchy)) OR (((NOT access.object_restrict_to_parent) AND access.object_descend) AND (object_axis.hierarchy ~~ (access.object || '_%'::text)))) OR ((access.object_restrict_to_parent AND access.object_self) AND (object_axis.hierarchy = subject_axis.hierarchy))) OR ((access.object_restrict_to_parent AND access.object_descend) AND (parent(object_axis.hierarchy) = subject_axis.hierarchy)))) AND (subject_axis.ident_id = subject_name.id)) AND (object_axis.ident_id = object_name.id));


ALTER TABLE public.access OWNER TO poem;

--
-- Name: comment_id_seq; Type: SEQUENCE; Schema: public; Owner: poem
--

CREATE SEQUENCE comment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.comment_id_seq OWNER TO poem;

--
-- Name: comment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: poem
--

SELECT pg_catalog.setval('comment_id_seq', 1, false);


--
-- Name: comment; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE comment (
    id integer DEFAULT nextval('comment_id_seq'::regclass) NOT NULL,
    subject_id integer NOT NULL,
    title text,
    content text NOT NULL
);


ALTER TABLE public.comment OWNER TO poem;

--
-- Name: content; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE content (
    id integer NOT NULL,
    erdf text NOT NULL,
    svg text,
    png_large bytea,
    png_small bytea
);
ALTER TABLE ONLY content ALTER COLUMN erdf SET STORAGE MAIN;
ALTER TABLE ONLY content ALTER COLUMN svg SET STORAGE MAIN;


ALTER TABLE public.content OWNER TO poem;

--
-- Name: model_rating; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE model_rating (
    id integer NOT NULL,
    subject_id integer NOT NULL,
    object_id integer NOT NULL,
    score integer NOT NULL
);


ALTER TABLE public.model_rating OWNER TO poem;

--
-- Name: plugin; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

DROP TABLE plugin CASCADE;

CREATE TABLE plugin (
    rel text NOT NULL,
    title text NOT NULL,
    description text NOT NULL,
    java_class text NOT NULL,
    is_export boolean NOT NULL
);
ALTER TABLE ONLY plugin ALTER COLUMN rel SET STORAGE MAIN;
ALTER TABLE ONLY plugin ALTER COLUMN title SET STORAGE MAIN;
ALTER TABLE ONLY plugin ALTER COLUMN description SET STORAGE MAIN;
ALTER TABLE ONLY plugin ALTER COLUMN java_class SET STORAGE MAIN;


ALTER TABLE public.plugin OWNER TO poem;


CREATE SEQUENCE setting_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.setting_id_seq OWNER TO poem;

--
-- Name: setting_id_seq; Type: SEQUENCE SET; Schema: public; Owner: poem
--

SELECT pg_catalog.setval('setting_id_seq', 3, true);


--
-- Name: setting; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE setting (
    subject_id integer,
    id integer DEFAULT nextval('setting_id_seq'::regclass) NOT NULL,
    key text NOT NULL,
    value text NOT NULL
);


ALTER TABLE public.setting OWNER TO poem;

--
-- Name: subject; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE subject (
    ident_id integer NOT NULL,
    nickname text,
    email text,
    fullname text,
    dob date,
    gender text,
    postcode text,
    first_login date NOT NULL,
    last_login date NOT NULL,
    login_count integer DEFAULT 0 NOT NULL,
    language_code text,
    country_code text,
    password text,
    visibility text
);


ALTER TABLE public.subject OWNER TO poem;

--
-- Name: tag_definition_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tag_definition_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.tag_definition_id_seq OWNER TO postgres;

--
-- Name: tag_definition_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tag_definition_id_seq', 1, true);


--
-- Name: tag_definition; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE tag_definition (
    id integer DEFAULT nextval('tag_definition_id_seq'::regclass) NOT NULL,
    subject_id integer NOT NULL,
    name text NOT NULL
);


ALTER TABLE public.tag_definition OWNER TO poem;

--
-- Name: tag_relation_id_seq; Type: SEQUENCE; Schema: public; Owner: poem
--

CREATE SEQUENCE tag_relation_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.tag_relation_id_seq OWNER TO poem;

--
-- Name: tag_relation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: poem
--

SELECT pg_catalog.setval('tag_relation_id_seq', 1, true);


--
-- Name: tag_relation; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE tag_relation (
    id integer DEFAULT nextval('tag_relation_id_seq'::regclass) NOT NULL,
    tag_id integer NOT NULL,
    object_id integer NOT NULL
);


ALTER TABLE public.tag_relation OWNER TO poem;


--
-- Name: content_id_seq; Type: SEQUENCE; Schema: public; Owner: poem
--

CREATE SEQUENCE content_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.content_id_seq OWNER TO poem;

--
-- Name: content_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: poem
--

ALTER SEQUENCE content_id_seq OWNED BY content.id;


--
-- Name: content_id_seq; Type: SEQUENCE SET; Schema: public; Owner: poem
--

SELECT pg_catalog.setval('content_id_seq', 1, false);




CREATE SEQUENCE model_rating_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.model_rating_id_seq OWNER TO poem;

--
-- Name: model_rating_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: poem
--

ALTER SEQUENCE model_rating_id_seq OWNED BY model_rating.id;


--
-- Name: model_rating_id_seq; Type: SEQUENCE SET; Schema: public; Owner: poem
--

SELECT pg_catalog.setval('model_rating_id_seq', 1, false);



ALTER TABLE model_rating ALTER COLUMN id SET DEFAULT nextval('model_rating_id_seq'::regclass);

--
-- Data for Name: plugin; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY plugin (rel, title, description, java_class, is_export) FROM stdin;
/self	ModelHandler	Open model in the editor	org.b3mn.poem.handler.ModelHandler	t
/repository	RepositoryHandler	Returns the Repository base	org.b3mn.poem.handler.RepositoryHandler	f
/model_types	ModelHandler	Open model in the editor	org.b3mn.poem.handler.ModelHandler	f
/model	CollectionHandler	Open model in the editor	org.b3mn.poem.handler.CollectionHandler	f
/new	NewModelHandler	Open model in the editor	org.b3mn.poem.handler.NewModelHandler	f
/info	InfoHandler	edit info	org.b3mn.poem.handler.InfoHandler	f
/access	AccessHandler	edit access	org.b3mn.poem.handler.AccessHandler	f
/info-access	MetaHandler	About	org.b3mn.poem.handler.MetaHandler	t
/svg	ImageRenderer	Model as SVG	org.b3mn.poem.handler.ImageRenderer	t
/pdf	PdfRenderer	Model as PDF	org.b3mn.poem.handler.PdfRenderer	t
/png	PngRenderer	Model as PNG	org.b3mn.poem.handler.PngRenderer	t
/rdf	RdfExporter	Model as RDF	org.b3mn.poem.handler.RdfExporter	t
/login	OpenID 2.0 Login	Handles the login and stores OpenID attributes in the database	org.b3mn.poem.handler.LoginHandler	f
/user	UserHandler	Manages user meta data	org.b3mn.poem.handler.UserHandler	f
/repository2	Repository RELOADED	Returns initial Html page for the new repository	org.b3mn.poem.handler.Repository2Handler	f
/meta	Model Info Handler	Handles Requests from Repository2 concerning object meta data	org.b3mn.poem.handler.ModelInfoHandler	f
/tags	Tag Handler	Handles all requests concerning model tagging	org.b3mn.poem.handler.TagHandler	f
/filter	Filter Handler	Handles client request for server filter	org.b3mn.poem.handler.SortFilterHandler	f
\.


--
-- Data for Name: setting; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY setting (subject_id, id, key, value) FROM stdin;
0	1	UserManager.DefaultCountryCode	us
0	2	UserManager.DefaultLanguageCode	en
\.


--
-- Data for Name: subject; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY subject (ident_id, nickname, email, fullname, dob, gender, postcode, first_login, last_login, login_count, language_code, country_code, password, visibility) FROM stdin;
2	\N	\N	\N	\N	\N	\N	2008-01-01	2008-01-01	0	\N	\N	\N	\N
\.


ALTER TABLE ONLY comment
    ADD CONSTRAINT comment_pkey PRIMARY KEY (id);



ALTER TABLE ONLY model_rating
    ADD CONSTRAINT model_rating_pkey PRIMARY KEY (id);



ALTER TABLE ONLY setting
    ADD CONSTRAINT settings_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tag_definition
    ADD CONSTRAINT tag_definition_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT tag_relation_pkey PRIMARY KEY (id);


ALTER TABLE ONLY subject
    ADD CONSTRAINT user_pkey PRIMARY KEY (ident_id);


ALTER TABLE ONLY comment
    ADD CONSTRAINT comment_identity_fkey FOREIGN KEY (subject_id) REFERENCES identity(id);

ALTER TABLE ONLY model_rating
    ADD CONSTRAINT model_rating_object_fkey FOREIGN KEY (object_id) REFERENCES identity(id) ON DELETE CASCADE;

ALTER TABLE ONLY model_rating
    ADD CONSTRAINT model_rating_subject_fkey FOREIGN KEY (subject_id) REFERENCES identity(id) ON DELETE CASCADE;

ALTER TABLE ONLY setting
    ADD CONSTRAINT subject_id_fkey FOREIGN KEY (subject_id) REFERENCES identity(id) ON DELETE CASCADE;

ALTER TABLE ONLY tag_definition
    ADD CONSTRAINT tag_definition_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES identity(id) ON DELETE CASCADE;

ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT tag_relation_identity FOREIGN KEY (object_id) REFERENCES identity(id) ON DELETE CASCADE;

ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT tag_relation_tag_pkey FOREIGN KEY (tag_id) REFERENCES tag_definition(id) ON DELETE CASCADE;

ALTER TABLE ONLY subject
    ADD CONSTRAINT user_ident_id_pkey FOREIGN KEY (ident_id) REFERENCES identity(id) ON DELETE CASCADE;



ALTER TABLE "content"
   ADD COLUMN png_large bytea;

ALTER TABLE "content"
   ADD COLUMN png_small bytea;



--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;

-- replace old uris

UPDATE identity SET uri= replace(uri, '/data/model/', '/model/') WHERE uri LIKE '/data/model%'  ;




--
-- PostgreSQL database dump complete
--

