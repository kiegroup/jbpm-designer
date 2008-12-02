-- This scripts solve a minor bug that occurs if the poem user is not a superuser

ALTER TABLE public.access OWNER TO poem;

ALTER TABLE public.tag_definition_id_seq OWNER TO poem;