SELECT hierarchy, ident_id, uri, title
  FROM structure
  JOIN identity ON ident_id=id
  LEFT JOIN representation USING (ident_id)
  ORDER BY hierarchy
;

SELECT interaction.id, subject, identity.uri, subject_descend, term, "object", identity2.uri, title, object_self, object_descend, 
       object_restrict_to_parent, scheme
  FROM interaction
  JOIN structure ON subject=hierarchy
  JOIN identity ON ident_id=identity.id
  JOIN structure AS structure2 ON "object"=structure2.hierarchy
  LEFT JOIN representation ON structure2.ident_id=representation.ident_id
  JOIN identity AS identity2 ON structure2.ident_id=identity2.id
;

-- find models containing a certain hostname
SELECT id, erdf, svg
  FROM "content"
  WHERE erdf LIKE '%oldhostname%'
;

-- replace a hostname in models
UPDATE "content"
  SET
    erdf = REPLACE(erdf, 'oldhostname', 'newhostname'),
    svg  = REPLACE(svg,  'oldhostname', 'newhostname')
;

-- obtain hierarchy key for a given OpenID (might be interesting for updates)
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://getopenid.com/user01'
  )
;


-- migrating models to a new OpenID
-- assumes that no models have been created with the new OpenID

UPDATE interaction
  SET subject = (
    SELECT hierarchy
      FROM structure
      WHERE EXISTS(
        SELECT uri
          FROM identity
          WHERE ident_id=id AND uri='http://getopenid.com/user01'
      )
	)
 WHERE subject = (
    SELECT hierarchy
      FROM structure
      WHERE EXISTS(
        SELECT uri
          FROM identity
          WHERE ident_id=id AND uri='https://www.getopenid.com/user01/'
      )
	);

--testing the update
SELECT hierarchy, ident_id,
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://getopenid.com/user02'
  )
)
||
    SUBSTR(hierarchy, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  ) + 1)
AS new_hierarchy

  FROM structure
  WHERE
    CHAR_LENGTH(hierarchy) > CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )
  AND
    SUBSTR(hierarchy, 1, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )) =
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
;

-- the update doesn't work that way
--ERROR:  update or delete on table "structure" violates foreign key constraint "interaction_object_fkey" on table "interaction"
--DETAIL:  Key (hierarchy)=(U2J0) is still referenced from table "interaction".
UPDATE structure
  SET hierarchy =
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://getopenid.com/user02'
  )
)
||
    SUBSTR(hierarchy, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  ) + 1)

  WHERE
    CHAR_LENGTH(hierarchy) > CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )
  AND
    SUBSTR(hierarchy, 1, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )) =
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
;

-- creating objects first
INSERT INTO structure (SELECT
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://getopenid.com/user02'
  )
)
||
    SUBSTR(hierarchy, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  ) + 1)
AS hierarchy, ident_id

  FROM structure
  WHERE
    CHAR_LENGTH(hierarchy) > CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )
  AND
    SUBSTR(hierarchy, 1, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )) =
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
)
;

-- testing the update of the interaction table
SELECT "object",
(SELECT
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://getopenid.com/user02'
  )
)
||
    SUBSTR("object", CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  ) + 1)
)
AS new_object
  FROM interaction
  WHERE
    CHAR_LENGTH("object") > CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )
  AND
    SUBSTR("object", 1, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )) =
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
;

--performing the update
UPDATE interaction SET "object" =
(SELECT
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://getopenid.com/user02'
  )
)
||
    SUBSTR("object", CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  ) + 1)
)


  WHERE
    CHAR_LENGTH("object") > CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )
  AND
    SUBSTR("object", 1, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )) =
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
;

--testing the delete
SELECT hierarchy, ident_id
  FROM structure
  WHERE
    CHAR_LENGTH(hierarchy) > CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )
  AND
    SUBSTR(hierarchy, 1, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )) =
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
;

-- performing the delete
DELETE
  FROM structure
  WHERE
    CHAR_LENGTH(hierarchy) > CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )
  AND
    SUBSTR(hierarchy, 1, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
  )) =
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://www.getopenid.com/user02'
  )
)
;



-- and everything again for the next user
BEGIN;

INSERT INTO structure (SELECT
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://getopenid.com/user03'
  )
)
||
    SUBSTR(hierarchy, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='https://www.getopenid.com/user03'
  )
)
  ) + 1)
AS hierarchy, ident_id

  FROM structure
  WHERE
    CHAR_LENGTH(hierarchy) > CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='https://www.getopenid.com/user03'
  )
)
  )
  AND
    SUBSTR(hierarchy, 1, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='https://www.getopenid.com/user03'
  )
)
  )) =
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='https://www.getopenid.com/user03'
  )
)
)
;


UPDATE interaction SET "object" =
(SELECT
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='http://getopenid.com/user03'
  )
)
||
    SUBSTR("object", CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='https://www.getopenid.com/user03'
  )
)
  ) + 1)
)
  WHERE
    CHAR_LENGTH("object") > CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='https://www.getopenid.com/user03'
  )
)
  )
  AND
    SUBSTR("object", 1, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='https://www.getopenid.com/user03'
  )
)
  )) =
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='https://www.getopenid.com/user03'
  )
)
;
DELETE
  FROM structure
  WHERE
    CHAR_LENGTH(hierarchy) > CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='https://www.getopenid.com/user03'
  )
)
  )
  AND
    SUBSTR(hierarchy, 1, CHAR_LENGTH(
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='https://www.getopenid.com/user03'
  )
)
  )) =
(
SELECT hierarchy
  FROM structure
  WHERE EXISTS(
    SELECT uri
      FROM identity
      WHERE ident_id=id AND uri='https://www.getopenid.com/user03'
  )
)
;

COMMIT;
