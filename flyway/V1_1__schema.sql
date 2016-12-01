CREATE TABLE exifraw
(
  id serial NOT NULL,
  filename text NOT NULL,
  directory text NOT NULL,
  tagtype integer NOT NULL,
  tagname text,
  description text NOT NULL,
  insert_time timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT exifraw_pkey PRIMARY KEY (id)
);


CREATE TABLE jpeg
(
  id serial NOT NULL,
  filename text NOT NULL,
  found boolean NOT NULL,
  insert_time timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT jpeg_pkey PRIMARY KEY (id),
  CONSTRAINT jpeg_filename_key UNIQUE (filename)
);


CREATE TABLE directory
(
  id serial NOT NULL,
  name text NOT NULL,
  insert_time timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT directory_pkey PRIMARY KEY (id),
  CONSTRAINT directory_name_key UNIQUE (name)
);

CREATE TABLE tagname
(
  id serial NOT NULL,
  directory_id integer NOT NULL,
  tagtype integer NOT NULL,
  tagname text,
  insert_time timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT tagname_pkey PRIMARY KEY (id),
  CONSTRAINT tagname_directory FOREIGN KEY (directory_id)
      REFERENCES directory (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tagname_directory_id_tagtype_key UNIQUE (directory_id, tagtype)
);

CREATE TABLE exif
(
  id serial NOT NULL,
  jpeg_id integer NOT NULL,
  directory_id integer NOT NULL,
  tagtype integer NOT NULL,
  description text NOT NULL,
  insert_time timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT exif_pkey PRIMARY KEY (id),
  CONSTRAINT exif_directory FOREIGN KEY (directory_id)
      REFERENCES directory (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT exif_jpeg FOREIGN KEY (jpeg_id)
      REFERENCES jpeg (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT exif_jpeg_id_directory_id_tagtype_key UNIQUE (jpeg_id, directory_id, tagtype)
);