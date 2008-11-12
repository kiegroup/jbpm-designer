
ALTER TABLE friend
	ADD COLUMN model_count integer NOT NULL DEFAULT 0;

CREATE OR REPLACE FUNCTION get_identity_from_hierarchy(hierarchy text) RETURNS identity AS
$BODY$ DECLARE
	result identity;
BEGIN
	SELECT identity.* INTO result FROM identity, structure WHERE identity.id=structure.ident_id AND structure.hierarchy=hierarchy;
	RETURN result;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE
COST 100;
ALTER FUNCTION get_identity_from_hierarchy(text) OWNER TO poem;

CREATE OR REPLACE FUNCTION get_identity_id_from_hierarchy(hierarchy text) RETURNS integer AS
$BODY$ DECLARE
	result integer;
BEGIN
	SELECT identity.id INTO result FROM identity, structure WHERE identity.id=structure.ident_id AND structure.hierarchy=hierarchy;
	RETURN result;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE
COST 100;
ALTER FUNCTION get_identity_id_from_hierarchy(text) OWNER TO poem;



CREATE OR REPLACE FUNCTION friend_inc_counter(subject_id1 integer, subject_id2 integer, count integer) RETURNS VOID AS
$BODY$ DECLARE
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
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE
COST 100;
ALTER FUNCTION friend_inc_counter(integer, integer, integer) OWNER TO poem;

CREATE OR REPLACE FUNCTION friend_dec_counter(subject_id1 integer, subject_id2 integer, count integer) RETURNS VOID AS
$BODY$ DECLARE
	result friend;
BEGIN
	SELECT friend.* INTO result FROM friend WHERE 
		(friend.subject_id=subject_id1 AND friend.friend_id=subject_id2)
		OR (friend.friend_id=subject_id1 AND friend.subject_id=subject_id2);

	IF FOUND AND result.model_count > count THEN 
		UPDATE friend SET model_count=result.model_count - count 
		WHERE friend.subject_id=result.subject_id
		AND friend.friend_id=result.friend_id;
	ELSE
		UPDATE friend SET model_count=0
		WHERE friend.subject_id=result.subject_id
		AND friend.friend_id=result.friend_id;	
	END IF;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE
COST 100;
ALTER FUNCTION friend_dec_counter(integer, integer, integer) OWNER TO poem;



CREATE OR REPLACE FUNCTION friend_trigger_interaction() RETURNS TRIGGER AS
$BODY$ DECLARE
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
	
	FOR friend_id IN SELECT access.subject_id FROM access, structure
			WHERE (access.object_id=structure.ident_id AND structure.hierarchy=model_hierarchy) LOOP
		
		IF (TG_OP = 'INSERT') THEN 
			PERFORM friend_inc_counter(subject.id, friend_id, 1);
		ELSEIF (TG_OP = 'DELETE') THEN
			PERFORM friend_dec_counter(subject.id, friend_id, 1);
		END IF;		
	END LOOP;
	RETURN NULL;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE
COST 100;
ALTER FUNCTION friend_trigger_interaction() OWNER TO poem;

CREATE OR REPLACE FUNCTION friend_init() RETURNS VOID AS
$BODY$ DECLARE
	user_pair record;
BEGIN
	DELETE FROM friend;
	PERFORM friend_inc_counter(friend1.subject_id, friend2.subject_id, 1)
		FROM access as friend1, access as friend2 
		WHERE friend1.object_id=friend1.object_id AND friend1 <> friend2;
	-- halve the counter since all friends are counted twice
	UPDATE friend SET model_count=model_count / 2;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE
COST 100;
ALTER FUNCTION friend_init() OWNER TO poem;



CREATE FUNCTION is_parent(node text, subnode text) RETURNS boolean AS
$BODY$BEGIN
 PERFORM * FROM poem_path(subnode) WHERE poem_path=node;
 IF NOT FOUND THEN 
	RETURN FALSE;
 ELSE
	RETURN TRUE;
 END IF;
END;$BODY$
LANGUAGE 'plpgsql' IMMUTABLE
COST 100;
ALTER FUNCTION is_parent(text, text) OWNER TO poem;

DROP VIEW access;

CREATE OR REPLACE VIEW access AS 
 SELECT context_name.id AS context_id, context_name.uri AS context_name, subject_name.id AS subject_id, subject_name.uri AS subject_name, object_name.id AS object_id, object_name.uri AS object_name, access.id AS access_id, access.scheme AS access_scheme, access.term AS access_term
   FROM interaction access, structure context, identity context_name, structure subject_axis, identity subject_name, structure object_axis, identity object_name
  WHERE access.subject = context.hierarchy AND context.ident_id = context_name.id AND (access.subject = subject_axis.hierarchy OR access.subject_descend AND is_parent( access.subject, subject_axis.hierarchy)) AND (NOT access.object_restrict_to_parent AND access.object_self AND access.object = object_axis.hierarchy OR NOT access.object_restrict_to_parent AND access.object_descend AND is_parent(access.object, object_axis.hierarchy) OR access.object_restrict_to_parent AND access.object_self AND object_axis.hierarchy = subject_axis.hierarchy OR access.object_restrict_to_parent AND access.object_descend AND parent(object_axis.hierarchy) = subject_axis.hierarchy) AND subject_axis.ident_id = subject_name.id AND object_axis.ident_id = object_name.id;

ALTER TABLE access OWNER TO postgres;


CREATE TRIGGER friend_interaction
AFTER INSERT OR DELETE ON interaction
    FOR EACH ROW EXECUTE PROCEDURE friend_trigger_interaction();

