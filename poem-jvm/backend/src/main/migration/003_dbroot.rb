class Dbroot < ActiveRecord::Migration
  def self.up 
    # create basic data tree  
    execute %q{
      insert into "identity" (uri) values ('root');
      insert into "identity" (uri) values ('public');
      insert into "identity" (uri) values ('groups');
      insert into "identity" (uri) values ('ownership');
    }
    
    execute %q{
      insert into "structure" (hierarchy, ident_id) select 'U', id from identity where uri = 'root';
      insert into "structure" (hierarchy, ident_id) select 'U1', id from identity where uri = 'public';
      insert into "structure" (hierarchy, ident_id) select 'U2', id from identity where uri = 'ownership';
      insert into "structure" (hierarchy, ident_id) select 'U3', id from identity where uri = 'groups';
      }
    
    # set owner rights: Each childnote of 'ownership' is owner of its childs
    execute %q{
      insert into "interaction" (subject, subject_descend, object, object_self, object_descend, object_restrict_to_parent, scheme, term) select 'U2',true,'U2',false,true,true,'http://b3mn.org/http','owner';
    }
    
    # create basic plugin relation
    execute %q{
      insert into "plugin"(rel, scheme, term, title)
      	select '/self', 'ruby', 'ModelHandler', 'Oryx Editor';
      insert into "plugin"(rel, scheme, term, title)
      	select '/info', 'ruby', 'InfoHandler', 'edit info';
      insert into "plugin"(rel, scheme, term, title)
        select '/access', 'ruby', 'AccessHandler', 'edit access';
      insert into "plugin"(rel, scheme, term, title)
        select '/info-access', 'ruby', 'MetaHandler', 'About';
    }

  end
end