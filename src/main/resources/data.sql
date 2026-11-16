INSERT INTO client (id_client, dni, name, surname, birthdate) VALUES (1, '12345678Z', 'Laura', 'Martin', '1988-04-12');
INSERT INTO client (id_client, dni, name, surname, birthdate) VALUES (2, '87654321X', 'Carlos', 'Ruiz', '1975-11-03');

INSERT INTO policy (num_policy, branch, sub_branch, id_client) VALUES ('POL-0001', 'health', 'dental', 1);
INSERT INTO policy (num_policy, branch, sub_branch, id_client) VALUES ('POL-0002', 'home', 'contents', 1);
INSERT INTO policy (num_policy, branch, sub_branch, id_client) VALUES ('POL-0003', 'life', 'term', 2);
