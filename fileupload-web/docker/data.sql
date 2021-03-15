\c fileupload docker

INSERT INTO user_account(id, email, name, password, role) VALUES(gen_random_uuid(), 'local@domain', 'mu', '{noop}password', 'USER');
COMMIT;
