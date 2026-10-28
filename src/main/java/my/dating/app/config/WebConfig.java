package my.dating.app.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.dating.app.object.Profile;
import org.solarframework.auth.obj.Account_User;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final Set<String> hasProfile = ConcurrentHashMap.newKeySet();

    // AuthenticationImpl's signup creates the account only, so a member's Profile row is created the first time they are seen.
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                if (request.getUserPrincipal() == null || !hasProfile.add(request.getUserPrincipal().getName())) return true;
                Account_User user = Account_User.getByAuthentication(request.getUserPrincipal());
                if (user == null) hasProfile.remove(request.getUserPrincipal().getName());
                else Profile.ensureExists(user.getID());
                return true;
            }
        });
    }
}
