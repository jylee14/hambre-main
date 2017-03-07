package com.irs.server;

/**
 * Created by bryle on 3/1/2017.
 */

public class DBTagDTO {
        private int tag_id;
        private String description;

        public int tag_id() {
            return tag_id;
        }
        public String description() {
            return description;
        }

        @Override
        public String toString() {
            return "DBTagDTO{" +
                    "tagId= " + tag_id +
                    ", description= " + description +
                    '}';
        }
    }

