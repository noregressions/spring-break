package com.example;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import java.util.Collections;

public class AccessApiUsage {
    public static AccessDecisionManager createManager() {
        return new AffirmativeBased(Collections.singletonList(new RoleVoter()));
    }
}
