# Security Policy

## Supported Versions

We are committed to maintaining the security of Sketchware Plus. The following versions are currently supported with security updates:

| Version | Supported          |
| ------- | ------------------ |
| 7.1.x   | :white_check_mark: |
| 7.0.x   | :white_check_mark: |
| < 7.0   | :x:                |

## Reporting a Vulnerability

We take the security of Sketchware Plus seriously. If you believe you have found a security vulnerability, please report it to us as described below.

### How to Report a Security Vulnerability

**Please do not report security vulnerabilities through public GitHub issues.**

Instead, please report them via one of the following methods:

1. **Email**: Send details to the project maintainers (contact information can be found in the repository)
2. **GitHub Security Advisory**: Use GitHub's private vulnerability reporting feature
3. **Discord**: Contact a moderator privately in our [Discord server](http://discord.gg/kq39yhT4rX)

### What to Include in Your Report

Please include as much of the following information as possible:

- Type of vulnerability (e.g., code injection, authentication bypass, etc.)
- Full paths of source file(s) related to the vulnerability
- Location of the affected source code (tag/branch/commit or direct URL)
- Step-by-step instructions to reproduce the issue
- Proof-of-concept or exploit code (if possible)
- Impact of the issue, including how an attacker might exploit it

### What to Expect

- **Acknowledgment**: We will acknowledge receipt of your vulnerability report within 48 hours
- **Assessment**: We will investigate and assess the vulnerability
- **Updates**: We will keep you informed about our progress
- **Resolution**: We will work to fix verified vulnerabilities as quickly as possible
- **Credit**: We will credit you in the security advisory (unless you prefer to remain anonymous)

## Security Best Practices for Contributors

### Code Review

All code changes go through review to ensure:
- No hardcoded credentials or secrets
- Proper input validation
- Secure data handling
- No SQL injection vulnerabilities
- Proper use of cryptography

### Dependencies

- We regularly update dependencies to their latest secure versions
- We use automated tools to scan for vulnerable dependencies
- Critical security updates are prioritized

### Build Security

- All releases are signed with our official keystore
- We use ProGuard/R8 for code obfuscation in release builds
- We enable security features like `debuggable=false` in production

## Security Features

### Current Implementation

1. **Code Obfuscation**: Using R8 with full mode enabled
2. **Secure Network Communication**: Enforcing HTTPS where possible
3. **Permission Management**: Requesting only necessary permissions
4. **Data Protection**: Encrypting sensitive user data
5. **Input Validation**: Sanitizing all user inputs

### Planned Enhancements

1. **Security Scanner**: Automated vulnerability scanning in builds
2. **Certificate Pinning**: For critical API endpoints
3. **Root Detection**: Optional security checks for rooted devices
4. **Code Integrity Checks**: Anti-tampering mechanisms
5. **Secure Storage**: Enhanced encryption for sensitive data

## Known Security Considerations

### Android Permissions

The app requires certain permissions for functionality:
- `READ_EXTERNAL_STORAGE` / `WRITE_EXTERNAL_STORAGE`: For project files
- `INTERNET`: For online features and updates
- `MANAGE_EXTERNAL_STORAGE`: For full access to project directories

### Third-Party Libraries

We regularly audit our dependencies. Current security-sensitive libraries:
- Firebase (Analytics, Crashlytics): Optional, can be disabled
- OkHttp: Network communication
- ProGuard/R8: Code protection

## Compliance

- We follow OWASP Mobile Security Testing Guide recommendations
- We comply with Android security best practices
- We adhere to Google Play security requirements

## Updates and Patches

- **Critical vulnerabilities**: Patched within 48-72 hours
- **High severity**: Patched within 1 week
- **Medium severity**: Patched in next regular release
- **Low severity**: Addressed as part of routine maintenance

## Security Changelog

### Version 7.1.0
- Updated Android target SDK to 35 for latest security features
- Enabled R8 full mode for better code protection
- Added GitHub Actions security scanning with Trivy
- Improved dependency management

### Version 7.0.0
- Previous security updates (refer to release notes)

## Contact

For general security inquiries (non-vulnerabilities), you can:
- Open a discussion in GitHub Discussions
- Ask in our Discord server
- Create a public issue (for non-sensitive security features requests)

## Acknowledgments

We appreciate the security research community's efforts in responsibly disclosing vulnerabilities. Contributors who report valid security issues will be acknowledged in:
- Security advisories
- Release notes
- This security policy (if desired)

---

**Last Updated**: November 2024
