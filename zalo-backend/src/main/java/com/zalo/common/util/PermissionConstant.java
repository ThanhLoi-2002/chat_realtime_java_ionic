package com.zalo.common.util;

public final class PermissionConstant {
    public static final class APP {

    }

    public static final class OA {

    }

    public static final class ADMIN {
        public static final class OA_CATEGORY {
            public static final String READ = "oaCategory:read";
            public static final String CREATE = "oaCategory:create";
            public static final String UPDATE = "oaCategory:update";
            public static final String DELETE = "oaCategory:delete";
        }


        public static final class LANG {
            public static final String READ = "lang:read";
            public static final String CREATE = "lang:create";
            public static final String UPDATE = "lang:update";
            public static final String DELETE = "lang:delete";
        }

        public static final class STRUCTURE {
            public static final String READ = "structure:read";
            public static final String CREATE_UPDATE = "structure:create_update";
        }

        public static final class USER {
            public static final String READ = "user:read";
            public static final String CREATE = "user:create";
            public static final String UPDATE = "user:update";
            public static final String DELETE = "user:delete";
        }
    }
}
