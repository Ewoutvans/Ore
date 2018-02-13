# --- !Ups
CREATE TABLE project_change_requests (
  id          SERIAL        PRIMARY KEY,
  project_id  INT           NOT NULL REFERENCES projects,
  comment     TEXT,
  created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
  created_by  INT           NOT NULl REFERENCES users,
  resolved_at TIMESTAMP,
  resolved_by INT           REFERENCES users
);

# --- !Downs
DROP TABLE project_change_requests;
