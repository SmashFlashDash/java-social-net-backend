DO $$
    DECLARE
        r RECORD;
        s TEXT;
        schemas TEXT[] = ARRAY ['public', 'socialnet'];
    BEGIN
        FOREACH s IN ARRAY schemas LOOP
                EXECUTE 'SET SEARCH_PATH = ' || s;
                RAISE NOTICE 'schema %', current_schema();
                FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = current_schema()) LOOP
                        RAISE NOTICE 'DROP TABLE %', r.tablename;
                        EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(r.tablename) || ' CASCADE';
                    END LOOP;
            END LOOP;
    END $$;
