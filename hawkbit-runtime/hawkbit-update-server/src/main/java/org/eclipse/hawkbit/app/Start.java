/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.app;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.hawkbit.autoconfigure.security.EnableHawkbitManagedSecurityConfiguration;
import org.eclipse.hawkbit.im.authentication.MultitenancyIndicator;
import org.eclipse.hawkbit.im.authentication.SpPermission;
import org.eclipse.hawkbit.im.authentication.UserPrincipal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * A {@link SpringBootApplication} annotated class with a main method to start.
 * The minimal configuration for the stand alone hawkBit server.
 *
 */
@SpringBootApplication
@EnableHawkbitManagedSecurityConfiguration
// Exception squid:S1118 - Spring boot standard behavior
@SuppressWarnings({ "squid:S1118" })
public class Start {

    /**
     * Main method to start the spring-boot application.
     *
     * @param args
     *            the VM arguments.
     */
    // Exception squid:S2095 - Spring boot standard behavior
    @SuppressWarnings({ "squid:S2095" })
    public static void main(final String[] args) {
        SpringApplication.run(Start.class, args);
    }

    @Bean
    MultitenancyIndicator multiTenancyIndicator() {
        return () -> false;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new MyUserStore();
    }

    private static class MyUserStore implements UserDetailsService {

        // This map could also be stored in a database
        private static final Map<String, UserDetails> USER_MAP = new HashMap<>();

        static {

            // put admin user with admin password
            USER_MAP.put("admin",
                    new UserPrincipal("admin", "admin", "admin", "admin", "admin", "admin@admin.de", "DEFAULT",
                            SpPermission.getAllAuthorities().stream().map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList())));

            // put guest user with guest password
            USER_MAP.put("guest",
                    new UserPrincipal("guest", "guest", "guest", "guest", "guest", "guest@guest.de", "DEFAULT",
                            SpPermission.getAllAuthorities().stream().map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList())));
        }

        @Override
        public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

            // here you could also load user names from a database instead of a
            // static map.
            return USER_MAP.get(username);

        }

    }
}
