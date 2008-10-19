

-- Table: friend

-- DROP TABLE friend;

CREATE TABLE friend
(
  id serial NOT NULL,
  subject_id integer NOT NULL,
  friend_id integer NOT NULL,
  CONSTRAINT friend_pkey PRIMARY KEY (id),
  CONSTRAINT fkey_friend_identity2 FOREIGN KEY (friend_id)
      REFERENCES identity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT friends_fkey_identity1 FOREIGN KEY (subject_id)
      REFERENCES identity (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (OIDS=FALSE);
ALTER TABLE friend OWNER TO poem;

CREATE INDEX friend_identity_idx
   ON friend USING btree (subject_id, friend_id);