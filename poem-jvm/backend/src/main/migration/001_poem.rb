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
      
      alter table "representation" add foreign key(ident_id) references "identity" on delete cascade;
      alter table "representation" alter column created set default now();
      alter table "representation" alter column updated set default now();
    }
    
    
    create_table 'structure', :id => false do |t|
      t.column :hierarchy,  :text, :null => false
      t.column :ident_id,   :integer, :null => false
    end
    
    execute %q{
      alter table "structure" alter column hierarchy set storage main;
      alter table "structure" add primary key(hierarchy);
      alter table "structure" add foreign key(ident_id) references "identity" on delete cascade;
    }
    
    create_table 'interaction' do |t|
      t.column :subject,                    :text, :null => false
      t.column :subject_descend,            :boolean, :null => false, :default => false
      
      t.column :object,                     :text, :null => false
      t.column :object_self,                :boolean, :null => false, :default => true
      t.column :object_descend,             :boolean, :null => false, :default => false
      t.column :object_restrict_to_parent, :boolean, :null => false, :default => false
      
      t.column :scheme,     :text, :null => false
      t.column :term,       :text, :null => false
    end
    
    execute %q{
      alter table "interaction" alter column subject set storage main;
      alter table "interaction" alter column object set storage main;
      alter table "interaction" alter column scheme set storage main;
      alter table "interaction" alter column term set storage main;
      
      alter table "interaction" add foreign key(subject) references "structure" on delete cascade;
      alter table "interaction" add foreign key(object) references "structure" on delete cascade;
      
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

  end

end