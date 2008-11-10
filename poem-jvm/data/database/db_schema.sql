--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: poem
--

CREATE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO poem;

--
-- Name: plpythonu; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: poem
--

CREATE PROCEDURAL LANGUAGE plpythonu;


ALTER PROCEDURAL LANGUAGE plpythonu OWNER TO poem;

SET search_path = public, pg_catalog;

--
-- Name: is_parent(text, text); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION is_parent(node text, subnode text) RETURNS boolean
    AS $$BEGIN
 PERFORM * FROM poem_path(subnode) WHERE poem_path=node;
 IF NOT FOUND THEN 
	RETURN FALSE;
 ELSE
	RETURN TRUE;
 END IF;
END;$$
    LANGUAGE plpgsql IMMUTABLE;


ALTER FUNCTION public.is_parent(node text, subnode text) OWNER TO poem;

--
-- Name: parent(text); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION parent(hierarchy text) RETURNS text
    AS $$
 def poem_path(hierarchy):
 	# Returns an ordered collection of strings. The first item of the collection is
 	# the path to the first item in the hierarchy and so on.
 	all = ["",""]
 	if (hierarchy == ""):
 		return all
 	else:
 		position = 0
 		# for each character in the input string
 		while position < len(hierarchy):
 			# count tildes
 			tilde_count = 0
 			while hierarchy[position+tilde_count] == "~":
 				tilde_count += 1
 			if tilde_count == 0:
 				all.append(hierarchy[:position+1])
 				position += 1
 			else:
 				all.append(hierarchy[:position+2*tilde_count])
 			position += 2*tilde_count;
 		return all

 # Returns path to parent item
 return poem_path(hierarchy)[-2]
 $$
    LANGUAGE plpythonu IMMUTABLE;


ALTER FUNCTION public.parent(hierarchy text) OWNER TO poem;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: identity; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE identity (
    id integer NOT NULL,
    uri text NOT NULL
);
ALTER TABLE ONLY identity ALTER COLUMN uri SET STORAGE MAIN;


ALTER TABLE public.identity OWNER TO poem;

--
-- Name: interaction; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE interaction (
    id integer NOT NULL,
    subject text NOT NULL,
    subject_descend boolean DEFAULT false NOT NULL,
    object text NOT NULL,
    object_self boolean DEFAULT true NOT NULL,
    object_descend boolean DEFAULT false NOT NULL,
    object_restrict_to_parent boolean DEFAULT false NOT NULL,
    scheme text NOT NULL,
    term text NOT NULL
);
ALTER TABLE ONLY interaction ALTER COLUMN subject SET STORAGE MAIN;
ALTER TABLE ONLY interaction ALTER COLUMN object SET STORAGE MAIN;
ALTER TABLE ONLY interaction ALTER COLUMN scheme SET STORAGE MAIN;
ALTER TABLE ONLY interaction ALTER COLUMN term SET STORAGE MAIN;


ALTER TABLE public.interaction OWNER TO poem;

--
-- Name: structure; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE structure (
    hierarchy text NOT NULL,
    ident_id integer NOT NULL
);
ALTER TABLE ONLY structure ALTER COLUMN hierarchy SET STORAGE MAIN;


ALTER TABLE public.structure OWNER TO poem;

--
-- Name: access; Type: VIEW; Schema: public; Owner: postgres
--

CREATE OR REPLACE VIEW access AS 
 SELECT context_name.id AS context_id, context_name.uri AS context_name, subject_name.id AS subject_id, subject_name.uri AS subject_name, object_name.id AS object_id, object_name.uri AS object_name, access.id AS access_id, access.scheme AS access_scheme, access.term AS access_term
   FROM interaction access, structure context, identity context_name, structure subject_axis, identity subject_name, structure object_axis, identity object_name
  WHERE access.subject = context.hierarchy AND context.ident_id = context_name.id AND (access.subject = subject_axis.hierarchy OR access.subject_descend AND is_parent( access.subject, subject_axis.hierarchy)) AND (NOT access.object_restrict_to_parent AND access.object_self AND access.object = object_axis.hierarchy OR NOT access.object_restrict_to_parent AND access.object_descend AND is_parent(access.object, object_axis.hierarchy) OR access.object_restrict_to_parent AND access.object_self AND object_axis.hierarchy = subject_axis.hierarchy OR access.object_restrict_to_parent AND access.object_descend AND parent(object_axis.hierarchy) = subject_axis.hierarchy) AND subject_axis.ident_id = subject_name.id AND object_axis.ident_id = object_name.id;


ALTER TABLE public.access OWNER TO postgres;

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
-- Name: friend; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE friend (
    id integer NOT NULL,
    subject_id integer NOT NULL,
    friend_id integer NOT NULL,
    model_count integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.friend OWNER TO poem;

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

--
-- Name: representation; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE representation (
    id integer NOT NULL,
    ident_id integer NOT NULL,
    mime_type text NOT NULL,
    language text DEFAULT 'en_US'::text NOT NULL,
    title text DEFAULT ''::text NOT NULL,
    summary text DEFAULT ''::text NOT NULL,
    created timestamp with time zone DEFAULT now() NOT NULL,
    updated timestamp with time zone DEFAULT now() NOT NULL,
    type text DEFAULT 'undefined'::text NOT NULL
);
ALTER TABLE ONLY representation ALTER COLUMN mime_type SET STORAGE MAIN;
ALTER TABLE ONLY representation ALTER COLUMN language SET STORAGE MAIN;
ALTER TABLE ONLY representation ALTER COLUMN title SET STORAGE MAIN;
ALTER TABLE ONLY representation ALTER COLUMN summary SET STORAGE MAIN;


ALTER TABLE public.representation OWNER TO poem;

--
-- Name: schema_info; Type: TABLE; Schema: public; Owner: poem; Tablespace: 
--

CREATE TABLE schema_info (
    version integer
);


ALTER TABLE public.schema_info OWNER TO poem;

--
-- Name: setting_id_seq; Type: SEQUENCE; Schema: public; Owner: poem
--

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
-- Name: child_position(text); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION child_position(hierarchy text) RETURNS integer
    AS $$
 def poem_path(hierarchy):
 	# Returns an ordered collection of strings. The first item of the collection is
 	# the path to the first item in the hierarchy and so on.
 	all = ["",""]
 	if (hierarchy == ""):
 		return all
 	else:
 		position = 0
 		# for each character in the input string
 		while position < len(hierarchy):
 			# count tildes
 			tilde_count = 0
 			while hierarchy[position+tilde_count] == "~":
 				tilde_count += 1
 			if tilde_count == 0:
 				all.append(hierarchy[:position+1])
 				position += 1
 			else:
 				all.append(hierarchy[:position+2*tilde_count])
 			position += 2*tilde_count;
 		return all

 def decode_position(code):
 	# Decodes the input code string to the original number
 	offset = 0
 	if (code == ""):
 		return None
 	while (code[offset] == "~"):
 	    offset+=1
 	digits = []    
 	for digit in code[offset:]:
 		digit_no = ord(digit)
 		if digit_no in range(48,58):
 			digits.append(digit_no-48)
 		if digit_no in range(65, 91):
 			digits.append(digit_no-55)
 		if digit_no in range(97,123):
 			digits.append(digit_no-61)

 	if offset ==  0:
 	    return digits[0]
 	if offset ==  1:
 	    return digits[0] + 62
 	if offset ==  2:
 	    return 124 + digits[0] * 62 + digits[1]
 	if offset == 3:
 		return 3968 + digits[0] * 62 * 62 + digits[1] * 62 + digits[2]

 path = poem_path(hierarchy)
 parent = -2
 current = -1
 local = path[current][len(path[parent]):] # == current-parent
 return decode_position(local)
 $$
    LANGUAGE plpythonu IMMUTABLE;


ALTER FUNCTION public.child_position(hierarchy text) OWNER TO poem;

--
-- Name: decode_position(text); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION decode_position(code text) RETURNS integer
    AS $$
 # Decodes the input code string to the original number
 offset = 0
 if (code == ""):
 	return None
 while (code[offset] == "~"):
     offset+=1
 digits = []    
 for digit in code[offset:]:
 	digit_no = ord(digit)
 	if digit_no in range(48,58):
 		digits.append(digit_no-48)
 	if digit_no in range(65, 91):
 		digits.append(digit_no-55)
 	if digit_no in range(97,123):
 		digits.append(digit_no-61)

 if offset ==  0:
     return digits[0]
 if offset ==  1:
     return digits[0] + 62
 if offset ==  2:
     return 124 + digits[0] * 62 + digits[1]
 if offset == 3:
 	return 3968 + digits[0] * 62 * 62 + digits[1] * 62 + digits[2]
 $$
    LANGUAGE plpythonu IMMUTABLE;


ALTER FUNCTION public.decode_position(code text) OWNER TO poem;

--
-- Name: encode_position(integer); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION encode_position(pos integer) RETURNS text
    AS $$
 def encode_pos(positio):
   # Encodes number to string representation
   # 0: "0" = 0, "z" = 61
   # 1: "~0" = 62, "~z" = 123
   # 2: "~~00" = 124, "~~zz" = 3967
   # 3: "~~~000" = 3968, "~~~zzz" = 242295
   position = positio
   if position in range(0,10):
    return str(position)
   if position in range(10,36):
      return chr(position+55)
   if position in range(16,62):
       return chr(position+61)
   if position in range(62,124):
       return "~" + encode_pos(position-62)
   if position in range(124, 3968):
       return "~~" + encode_pos((position-124)/62) + encode_pos((position-124)%62) 
   if position in range(3968, 242296):
    position -= 3968
    digit1 = position / (62 * 62)
    digit2 = (position % (62 * 62)) / 62
    digit3 = (position % (62 * 62)) % 62
    return "~~~" + encode_pos(digit1) + encode_pos(digit2) + encode_pos(digit3)
   raise "Stored Procedure: Encode Postition: Position out of range." 
 
 return encode_pos(pos)
 $$
    LANGUAGE plpythonu IMMUTABLE;


ALTER FUNCTION public.encode_position(pos integer) OWNER TO poem;

--
-- Name: ensure_descendant(text, integer); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION ensure_descendant(root_hierarchy text, target integer) RETURNS structure
    AS $$
         declare
           result "structure";
         begin
           select * into result
           from "structure"
           where hierarchy like root_hierarchy || '%'
                           and ident_id = target;

           if not found then
             insert into "structure"(hierarchy, ident_id) values(next_child_position(root_hierarchy), target) returning * into result;
           end if;

           return result;
         end;
       $$
    LANGUAGE plpgsql;


ALTER FUNCTION public.ensure_descendant(root_hierarchy text, target integer) OWNER TO poem;

--
-- Name: friend_dec_counter(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION friend_dec_counter(subject_id1 integer, subject_id2 integer, count integer) RETURNS void
    AS $$ DECLARE
	result friend;
BEGIN
	SELECT friend.* INTO result FROM friend WHERE 
		(friend.subject_id=subject_id1 AND friend.friend_id=subject_id2)
		OR (friend.friend_id=subject_id1 AND friend.subject_id=subject_id2);

	IF FOUND AND result.model_count > 0 THEN 
		UPDATE friend SET model_count=result.model_count - count 
		WHERE friend.subject_id=result.subject_id
		AND friend.friend_id=result.friend_id;
	END IF;
END;$$
    LANGUAGE plpgsql;


ALTER FUNCTION public.friend_dec_counter(subject_id1 integer, subject_id2 integer, count integer) OWNER TO poem;

--
-- Name: friend_inc_counter(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION friend_inc_counter(subject_id1 integer, subject_id2 integer, count integer) RETURNS void
    AS $$ DECLARE
	result friend;
BEGIN
	PERFORM * FROM identity WHERE ((identity.id=subject_id2 OR identity.id=subject_id1) AND identity.uri='public');
	IF (subject_id1=subject_id2 OR FOUND) THEN
		RETURN;
	END IF;
	
	SELECT friend.* INTO result FROM friend WHERE 
		(friend.subject_id=subject_id1 AND friend.friend_id=subject_id2)
		OR (friend.friend_id=subject_id1 AND friend.subject_id=subject_id2);

	IF NOT FOUND THEN 
		INSERT INTO friend (subject_id, friend_id, model_count) 
		VALUES (subject_id1, subject_id2, 1);
	ELSE 
		UPDATE friend SET model_count=result.model_count + count 
		WHERE friend.subject_id=result.subject_id
		AND friend.friend_id=result.friend_id;
	END IF;
END;$$
    LANGUAGE plpgsql;


ALTER FUNCTION public.friend_inc_counter(subject_id1 integer, subject_id2 integer, count integer) OWNER TO poem;

--
-- Name: friend_init(); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION friend_init() RETURNS void
    AS $$ DECLARE
	user_pair record;
BEGIN
	DELETE FROM friend;
	PERFORM friend_inc_counter(get_identity_id_from_hierarchy(user1.subject), 
			get_identity_id_from_hierarchy(user2.subject), 1)
		FROM interaction as user1, interaction as user2 
		WHERE user1.object=user2.object;
	-- halve the counter since all friends are counted twice
	UPDATE friend SET model_count=model_count / 2;
END;$$
    LANGUAGE plpgsql;


ALTER FUNCTION public.friend_init() OWNER TO poem;

--
-- Name: friend_trigger_interaction(); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION friend_trigger_interaction() RETURNS trigger
    AS $$ DECLARE
	model_hierarchy text;
	user_hierarchy text;
	subject identity;
	friend_id integer;
BEGIN
	IF (TG_OP = 'INSERT') THEN 
		model_hierarchy := NEW.object;
		user_hierarchy := NEW.subject;
	ELSEIF (TG_OP = 'DELETE') THEN
		model_hierarchy := OLD.object;
		user_hierarchy := OLD.subject;
	END IF;

	SELECT * INTO subject FROM get_identity_from_hierarchy(user_hierarchy);
	
	FOR friend_id IN SELECT identity.id FROM identity, structure, interaction 
			WHERE (identity.id=structure.ident_id AND interaction.object=model_hierarchy 
					AND structure.hierarchy=interaction.subject) LOOP
		
		IF (TG_OP = 'INSERT') THEN 
			PERFORM friend_inc_counter(subject.id, friend_id, 1);
		ELSEIF (TG_OP = 'DELETE') THEN
			PERFORM friend_inc_counter(subject.id, friend_id, 1);
		END IF;		
	END LOOP;
	RETURN NULL;
END;$$
    LANGUAGE plpgsql;


ALTER FUNCTION public.friend_trigger_interaction() OWNER TO poem;

--
-- Name: get_identity_from_hierarchy(text); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION get_identity_from_hierarchy(hierarchy text) RETURNS identity
    AS $$ DECLARE
	result identity;
BEGIN
	SELECT identity.* INTO result FROM identity, structure WHERE identity.id=structure.ident_id AND structure.hierarchy=hierarchy;
	RETURN result;
END;$$
    LANGUAGE plpgsql;


ALTER FUNCTION public.get_identity_from_hierarchy(hierarchy text) OWNER TO poem;

--
-- Name: get_identity_id_from_hierarchy(text); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION get_identity_id_from_hierarchy(hierarchy text) RETURNS integer
    AS $$ DECLARE
	result integer;
BEGIN
	SELECT identity.id INTO result FROM identity, structure WHERE identity.id=structure.ident_id AND structure.hierarchy=hierarchy;
	RETURN result;
END;$$
    LANGUAGE plpgsql;


ALTER FUNCTION public.get_identity_id_from_hierarchy(hierarchy text) OWNER TO poem;

--
-- Name: identity(text); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION identity(openid text) RETURNS identity
    AS $$
         declare
           result "identity";
         begin
           select * into result 
           from "identity" 
           where uri = openid;

           if not found then
             insert into "identity"(uri) values(openid) returning * into result;
           end if;

           return result;
         end;
       $$
    LANGUAGE plpgsql;


ALTER FUNCTION public.identity(openid text) OWNER TO poem;

--
-- Name: is_shared(integer); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION is_shared(id integer) RETURNS integer
    AS $$
          declare
            result integer;
          begin
            select count(*) into result 
            from interaction, structure as subject, structure as object
            where interaction.object=object.hierarchy and interaction.subject=subject.hierarchy and object.ident_id=id;

            return result;
          end;
        $$
    LANGUAGE plpgsql;


ALTER FUNCTION public.is_shared(id integer) OWNER TO poem;

--
-- Name: next_child_position(text); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION next_child_position(hierarchy text) RETURNS text
    AS $_$
         select $1 || encode_position(coalesce(max(child_position(hierarchy))+1,0)) from "structure" where parent(hierarchy) = $1;
       $_$
    LANGUAGE sql;


ALTER FUNCTION public.next_child_position(hierarchy text) OWNER TO poem;

--
-- Name: poem_path(text); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION poem_path(hierarchy text) RETURNS SETOF text
    AS $$
 # Returns an ordered collection of strings. The first item of the collection is
 # the path to the first item in the hierarchy and so on.
 all = ["",""]
 if (hierarchy == ""):
 	return all
 else:
 	position = 0
 	# for each character in the input string
 	while position < len(hierarchy):
 		# count tildes
 		tilde_count = 0
 		while hierarchy[position+tilde_count] == "~":
 			tilde_count += 1
 		if tilde_count == 0:
 			all.append(hierarchy[:position+1])
 			position += 1
 		else:
 			all.append(hierarchy[:position+2*tilde_count])
 		position += 2*tilde_count;
 	return all
 $$
    LANGUAGE plpythonu IMMUTABLE;


ALTER FUNCTION public.poem_path(hierarchy text) OWNER TO poem;

--
-- Name: work_around_path(text); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION work_around_path(hierarchy text) RETURNS SETOF text
    AS $$
 def poem_path(hierarchy):
 	# Returns an ordered collection of strings. The first item of the collection is
 	# the path to the first item in the hierarchy and so on.
 	all = ["",""]
 	if (hierarchy == ""):
 		return all
 	else:
 		position = 0
 		# for each character in the input string
 		while position < len(hierarchy):
 			# count tildes
 			tilde_count = 0
 			while hierarchy[position+tilde_count] == "~":
 				tilde_count += 1
 			if tilde_count == 0:
 				all.append(hierarchy[:position+1])
 				position += 1
 			else:
 				all.append(hierarchy[:position+2*tilde_count])
 			position += 2*tilde_count;
 		return all
   return poem_path(hierarchy)[2:-1]
 $$
    LANGUAGE plpythonu IMMUTABLE;


ALTER FUNCTION public.work_around_path(hierarchy text) OWNER TO poem;

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


--
-- Name: friend_id_seq; Type: SEQUENCE; Schema: public; Owner: poem
--

CREATE SEQUENCE friend_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.friend_id_seq OWNER TO poem;

--
-- Name: friend_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: poem
--

ALTER SEQUENCE friend_id_seq OWNED BY friend.id;


--
-- Name: friend_id_seq; Type: SEQUENCE SET; Schema: public; Owner: poem
--

SELECT pg_catalog.setval('friend_id_seq', 1, false);


--
-- Name: identity_id_seq; Type: SEQUENCE; Schema: public; Owner: poem
--

CREATE SEQUENCE identity_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.identity_id_seq OWNER TO poem;

--
-- Name: identity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: poem
--

ALTER SEQUENCE identity_id_seq OWNED BY identity.id;


--
-- Name: identity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: poem
--

SELECT pg_catalog.setval('identity_id_seq', 5, true);


--
-- Name: interaction_id_seq; Type: SEQUENCE; Schema: public; Owner: poem
--

CREATE SEQUENCE interaction_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.interaction_id_seq OWNER TO poem;

--
-- Name: interaction_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: poem
--

ALTER SEQUENCE interaction_id_seq OWNED BY interaction.id;


--
-- Name: interaction_id_seq; Type: SEQUENCE SET; Schema: public; Owner: poem
--

SELECT pg_catalog.setval('interaction_id_seq', 1, true);


--
-- Name: model_rating_id_seq; Type: SEQUENCE; Schema: public; Owner: poem
--

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


--
-- Name: representation_id_seq; Type: SEQUENCE; Schema: public; Owner: poem
--

CREATE SEQUENCE representation_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.representation_id_seq OWNER TO poem;

--
-- Name: representation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: poem
--

ALTER SEQUENCE representation_id_seq OWNED BY representation.id;


--
-- Name: representation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: poem
--

SELECT pg_catalog.setval('representation_id_seq', 1, true);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: poem
--

ALTER TABLE content ALTER COLUMN id SET DEFAULT nextval('content_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: poem
--

ALTER TABLE friend ALTER COLUMN id SET DEFAULT nextval('friend_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: poem
--

ALTER TABLE identity ALTER COLUMN id SET DEFAULT nextval('identity_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: poem
--

ALTER TABLE interaction ALTER COLUMN id SET DEFAULT nextval('interaction_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: poem
--

ALTER TABLE model_rating ALTER COLUMN id SET DEFAULT nextval('model_rating_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: poem
--

ALTER TABLE representation ALTER COLUMN id SET DEFAULT nextval('representation_id_seq'::regclass);


--
-- Data for Name: comment; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY comment (id, subject_id, title, content) FROM stdin;
\.


--
-- Data for Name: content; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY content (id, erdf, svg, png_large, png_small) FROM stdin;
\.


--
-- Data for Name: friend; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY friend (id, subject_id, friend_id, model_count) FROM stdin;
\.


--
-- Data for Name: identity; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY identity (id, uri) FROM stdin;
0	
1	root
2	public
3	groups
4	ownership
\.


--
-- Data for Name: interaction; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY interaction (id, subject, subject_descend, object, object_self, object_descend, object_restrict_to_parent, scheme, term) FROM stdin;
1	U2	t	U2	f	t	t	http://b3mn.org/http	owner
\.


--
-- Data for Name: model_rating; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY model_rating (id, subject_id, object_id, score) FROM stdin;
\.


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
-- Data for Name: representation; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY representation (id, ident_id, mime_type, language, title, summary, created, updated, type) FROM stdin;
\.


--
-- Data for Name: schema_info; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY schema_info (version) FROM stdin;
5
\.


--
-- Data for Name: setting; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY setting (subject_id, id, key, value) FROM stdin;
0	1	UserManager.DefaultCountryCode	us
0	2	UserManager.DefaultLanguageCode	en
\.


--
-- Data for Name: structure; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY structure (hierarchy, ident_id) FROM stdin;
U	1
U1	2
U2	4
U3	3
\.


--
-- Data for Name: subject; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY subject (ident_id, nickname, email, fullname, dob, gender, postcode, first_login, last_login, login_count, language_code, country_code, password, visibility) FROM stdin;
2	\N	\N	\N	\N	\N	\N	2008-01-01	2008-01-01	0	\N	\N	\N	\N
\.


--
-- Data for Name: tag_definition; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY tag_definition (id, subject_id, name) FROM stdin;
\.


--
-- Data for Name: tag_relation; Type: TABLE DATA; Schema: public; Owner: poem
--

COPY tag_relation (id, tag_id, object_id) FROM stdin;
\.


--
-- Name: comment_pkey; Type: CONSTRAINT; Schema: public; Owner: poem; Tablespace: 
--

ALTER TABLE ONLY comment
    ADD CONSTRAINT comment_pkey PRIMARY KEY (id);


--
-- Name: content_pkey; Type: CONSTRAINT; Schema: public; Owner: poem; Tablespace: 
--

ALTER TABLE ONLY content
    ADD CONSTRAINT content_pkey PRIMARY KEY (id);


--
-- Name: friend_pkey; Type: CONSTRAINT; Schema: public; Owner: poem; Tablespace: 
--

ALTER TABLE ONLY friend
    ADD CONSTRAINT friend_pkey PRIMARY KEY (id);


--
-- Name: identity_pkey; Type: CONSTRAINT; Schema: public; Owner: poem; Tablespace: 
--

ALTER TABLE ONLY identity
    ADD CONSTRAINT identity_pkey PRIMARY KEY (id);


--
-- Name: interaction_pkey; Type: CONSTRAINT; Schema: public; Owner: poem; Tablespace: 
--

ALTER TABLE ONLY interaction
    ADD CONSTRAINT interaction_pkey PRIMARY KEY (id);


--
-- Name: model_rating_pkey; Type: CONSTRAINT; Schema: public; Owner: poem; Tablespace: 
--

ALTER TABLE ONLY model_rating
    ADD CONSTRAINT model_rating_pkey PRIMARY KEY (id);


--
-- Name: representation_pkey; Type: CONSTRAINT; Schema: public; Owner: poem; Tablespace: 
--

ALTER TABLE ONLY representation
    ADD CONSTRAINT representation_pkey PRIMARY KEY (id);


--
-- Name: settings_pkey; Type: CONSTRAINT; Schema: public; Owner: poem; Tablespace: 
--

ALTER TABLE ONLY setting
    ADD CONSTRAINT settings_pkey PRIMARY KEY (id);


--
-- Name: structure_pkey; Type: CONSTRAINT; Schema: public; Owner: poem; Tablespace: 
--

ALTER TABLE ONLY structure
    ADD CONSTRAINT structure_pkey PRIMARY KEY (hierarchy);


--
-- Name: tag_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: poem; Tablespace: 
--

ALTER TABLE ONLY tag_definition
    ADD CONSTRAINT tag_definition_pkey PRIMARY KEY (id);


--
-- Name: tag_relation_pkey; Type: CONSTRAINT; Schema: public; Owner: poem; Tablespace: 
--

ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT tag_relation_pkey PRIMARY KEY (id);


--
-- Name: user_pkey; Type: CONSTRAINT; Schema: public; Owner: poem; Tablespace: 
--

ALTER TABLE ONLY subject
    ADD CONSTRAINT user_pkey PRIMARY KEY (ident_id);


--
-- Name: friend_identity_idx; Type: INDEX; Schema: public; Owner: poem; Tablespace: 
--

CREATE INDEX friend_identity_idx ON friend USING btree (subject_id, friend_id);


--
-- Name: object_idx; Type: INDEX; Schema: public; Owner: poem; Tablespace: 
--

CREATE INDEX object_idx ON interaction USING btree (object);


--
-- Name: poem_position; Type: INDEX; Schema: public; Owner: poem; Tablespace: 
--

CREATE INDEX poem_position ON structure USING btree (parent(hierarchy), child_position(hierarchy));


--
-- Name: rel_idx; Type: INDEX; Schema: public; Owner: poem; Tablespace: 
--

CREATE INDEX rel_idx ON plugin USING btree (rel);


--
-- Name: subject_idx; Type: INDEX; Schema: public; Owner: poem; Tablespace: 
--

CREATE INDEX subject_idx ON interaction USING btree (subject);


--
-- Name: friend_interaction; Type: TRIGGER; Schema: public; Owner: poem
--

CREATE TRIGGER friend_interaction
    AFTER INSERT OR DELETE ON interaction
    FOR EACH ROW
    EXECUTE PROCEDURE friend_trigger_interaction();


--
-- Name: comment_identity_fkey; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY comment
    ADD CONSTRAINT comment_identity_fkey FOREIGN KEY (subject_id) REFERENCES identity(id);


--
-- Name: content_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY content
    ADD CONSTRAINT content_id_fkey FOREIGN KEY (id) REFERENCES representation(id) ON DELETE CASCADE;


--
-- Name: fkey_friend_identity2; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY friend
    ADD CONSTRAINT fkey_friend_identity2 FOREIGN KEY (friend_id) REFERENCES identity(id) ON DELETE CASCADE;


--
-- Name: friends_fkey_identity1; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY friend
    ADD CONSTRAINT friends_fkey_identity1 FOREIGN KEY (subject_id) REFERENCES identity(id) ON DELETE CASCADE;


--
-- Name: interaction_object_fkey; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY interaction
    ADD CONSTRAINT interaction_object_fkey FOREIGN KEY (object) REFERENCES structure(hierarchy) ON DELETE CASCADE;


--
-- Name: interaction_subject_fkey; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY interaction
    ADD CONSTRAINT interaction_subject_fkey FOREIGN KEY (subject) REFERENCES structure(hierarchy) ON DELETE CASCADE;


--
-- Name: model_rating_object_fkey; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY model_rating
    ADD CONSTRAINT model_rating_object_fkey FOREIGN KEY (object_id) REFERENCES identity(id) ON DELETE CASCADE;


--
-- Name: model_rating_subject_fkey; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY model_rating
    ADD CONSTRAINT model_rating_subject_fkey FOREIGN KEY (subject_id) REFERENCES identity(id) ON DELETE CASCADE;


--
-- Name: representation_ident_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY representation
    ADD CONSTRAINT representation_ident_id_fkey FOREIGN KEY (ident_id) REFERENCES identity(id) ON DELETE CASCADE;


--
-- Name: structure_ident_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY structure
    ADD CONSTRAINT structure_ident_id_fkey FOREIGN KEY (ident_id) REFERENCES identity(id) ON DELETE CASCADE;


--
-- Name: subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY setting
    ADD CONSTRAINT subject_id_fkey FOREIGN KEY (subject_id) REFERENCES identity(id) ON DELETE CASCADE;


--
-- Name: tag_definition_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY tag_definition
    ADD CONSTRAINT tag_definition_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES identity(id) ON DELETE CASCADE;


--
-- Name: tag_relation_identity; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT tag_relation_identity FOREIGN KEY (object_id) REFERENCES identity(id) ON DELETE CASCADE;


--
-- Name: tag_relation_tag_pkey; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY tag_relation
    ADD CONSTRAINT tag_relation_tag_pkey FOREIGN KEY (tag_id) REFERENCES tag_definition(id) ON DELETE CASCADE;


--
-- Name: user_ident_id_pkey; Type: FK CONSTRAINT; Schema: public; Owner: poem
--

ALTER TABLE ONLY subject
    ADD CONSTRAINT user_ident_id_pkey FOREIGN KEY (ident_id) REFERENCES identity(id) ON DELETE CASCADE;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

