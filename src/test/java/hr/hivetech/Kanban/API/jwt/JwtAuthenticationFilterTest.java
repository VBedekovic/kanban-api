package hr.hivetech.Kanban.API.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter();
        try {
            var field = JwtAuthenticationFilter.class.getDeclaredField("jwtService");
            field.setAccessible(true);
            field.set(filter, jwtService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotFilter_returnsTrue_forNonApiPath() {
        when(request.getRequestURI()).thenReturn("/public/test");
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilter_returnsFalse_forApiPath() {
        when(request.getRequestURI()).thenReturn("/api/test");
        assertFalse(filter.shouldNotFilter(request));
    }

    @Test
    void doFilterInternal_setsAuthentication_whenValidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtService.validateToken("validtoken")).thenReturn(true);
        when(jwtService.getUsernameFromToken("validtoken")).thenReturn("user");

        filter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
        assertEquals("user", auth.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_doesNotSetAuthentication_whenNoHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_doesNotSetAuthentication_whenInvalidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidtoken");
        when(jwtService.validateToken("invalidtoken")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
