package com.bezkoder.springjwt.security.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bezkoder.springjwt.exception.TokenRefreshExp;
import com.bezkoder.springjwt.models.RefreshToken;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.repository.RefreshTokenRepository;
import com.bezkoder.springjwt.repository.UserRepository;

@Service
public class RefreshTokenService {
	@Value("${bezkoder.app.jwtRefreshExpirationMs}")
	private Long refreshTokenDurationMs;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserRepository userRepository;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken createRefreshToken(Long userId) {

		RefreshToken refreshToken = new RefreshToken();
		User user = userRepository.findById(userId).get();
		RefreshToken findByuser_id = refreshTokenRepository.findByuser_id(user.getId());
		if (findByuser_id == null) {
			refreshToken.setUser(userRepository.findById(userId).get());

			refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
			refreshToken.setToken(UUID.randomUUID().toString());

			refreshToken = refreshTokenRepository.save(refreshToken);

		} else {
			refreshToken = findByuser_id;
			refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
			refreshToken = refreshTokenRepository.save(refreshToken);
		}
		return refreshToken;
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			String tokens = token.getToken();
			throw new TokenRefreshExp(tokens, "Refresh token was expired. Please make a new signin request");
		}

		return token;
	}

	@Transactional
	public int deleteByUserId(Long userId) {
		return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
	}
}