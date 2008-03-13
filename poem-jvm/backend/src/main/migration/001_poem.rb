class Poem < ActiveRecord::Migration
  def self.up
    create_table 'identity' do |t|
      t.column :uri, :text, :null => false
    end
    
    execute %q{
      alter table "identity" alter column uri set storage main;
      insert into "identity" (id, uri) values (0, ''); -- anon
    }
    
    create_table 'representation' do |t|
      t.column :ident_id,   :integer, :null => false
      t.column :mime_type,  :text, :null => false
      t.column :language,   :text, :null => false, :default => 'en_US'
      
      t.column :title,      :text, :null => false, :default  => ''
      t.column :summary,    :text, :null => false, :default  => ''
      t.column :created,    'timestamp with time zone', :null => false
      t.column :updated,    'timestamp with time zone', :null => false
      
      t.column :content,    :text, :null => false
      t.column :type,		    :text, :null => false, :default => 'undefined'
    end
    
    execute %q{
      alter table "representation" alter column mime_type set storage main;
      alter table "representation" alter column language set storage main;
      alter table "representation" alter column title set storage main;
      alter table "representation" alter column summary set storage main;
      
      alter table "representation" add foreign key(ident_id) references "identity";
      alter table "representation" alter column created set default now();
      alter table "representation" alter column updated set default now();
    }
    
    create_table 'link' do |t|
      t.column :rep_id,     :integer, :null => false
      t.column :href_id,    :integer, :null => false
      t.column :rel,        :text
      t.column :type,       :text
      t.column :title,      :text
    end
    
    execute %q{
      alter table "link" alter column rel set storage main;
      alter table "link" alter column type set storage main;
      alter table "link" alter column title set storage main;
      
      alter table "link" add foreign key(rep_id) references "representation" on delete cascade;
      alter table "link" add foreign key(href_id) references "identity";
    }
    
    create_table 'category' do |t|
      t.column :rep_id,     :integer, :null => false
      t.column :context_id, :integer
      t.column :scheme,     :text
      t.column :term,       :text, :null => false
    end
    
    execute %q{
      alter table "category" alter column scheme set storage main;
      alter table "category" alter column term set storage main;
      
      alter table "category" add foreign key(rep_id) references "representation" on delete cascade;
      alter table "category" add foreign key(context_id) references "identity"
    }
    
    create_table 'structure', :id => false do |t|
      t.column :hierarchy,  :text, :null => false
      t.column :ident_id,   :integer, :null => false
      t.column :visible_id, :integer, :null => false
    end
    
    execute %q{
      alter table "structure" alter column hierarchy set storage main;
      alter table "structure" add primary key(hierarchy);
      
      create sequence structure_visible_id;
      alter table "structure" alter column visible_id set default nextval('structure_visible_id'::regclass);
      create unique index visible_id on "structure"(visible_id);
    }
    
    create_table 'interaction' do |t|
      t.column :subject,    :text, :null => false
      t.column :subject_descend, :boolean, :null => false, :default => true
      
      t.column :object,     :text, :null => false
      t.column :object_descend, :boolean, :null => false, :default => true
      
      t.column :scheme,     :text, :null => false
      t.column :term,       :text, :null => false
    end
    
    execute %q{
      alter table "interaction" alter column subject set storage main;
      alter table "interaction" alter column object set storage main;
      alter table "interaction" alter column scheme set storage main;
      alter table "interaction" alter column term set storage main;
      
      alter table "interaction" add foreign key(subject) references "structure";
      alter table "interaction" add foreign key(object) references "structure";
      
      create index subject_idx  on "interaction"(subject);
      create index object_idx   on "interaction"(object);
    }
    
    
    create_table 'plugin', :id => false do |t|
      t.column :rel,		:text, :null => false
      t.column :scheme,		:text, :null => false
      t.column :term,		:text, :null => false
      t.column :title, :text, :null => false
    end
    
    execute %q{
      alter table "plugin" alter column rel set storage main;
      alter table "plugin" alter column scheme set storage main;
      alter table "plugin" alter column term set storage main; 
      alter table "plugin" alter column title set storage main;
      create index rel_idx on "plugin"(rel);
    }
    
    execute %q{
      create view access as
        select  context_name.id     as context_id,
                context_name.uri    as context_name,
                subject_name.id     as subject_id,
                subject_name.uri    as subject_name,
                object_name.id      as object_id,
                object_name.uri     as object_name,
                access.id           as access_id,
                access.scheme		    as access_scheme,	
                access.term			    as access_term,
                plugin.rel			    as plugin_relation,
                plugin.scheme		    as scheme,
                plugin.term			    as term
        from    "interaction" as access,
                "structure"   as context,
                "identity"    as context_name,
                "structure"   as subject_axis,
                "identity"    as subject_name,
                "structure"   as object_axis,
                "identity"    as object_name,
                "plugin"	    as plugin
        where   access.subject = context.hierarchy
            and context.ident_id = context_name.id
            and (     access.subject = subject_axis.hierarchy
                  or  (access.subject_descend and subject_axis.hierarchy like access.subject || '_%'))
            and subject_axis.ident_id = subject_name.id
            and (     access.object = object_axis.hierarchy
                  or  (access.object_descend and object_axis.hierarchy like access.object || '_%'))
            and object_axis.ident_id = object_name.id;
    }
  end

end