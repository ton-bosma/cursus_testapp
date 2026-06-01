# Security

**Severity: BLOCKING** unless noted. These repos handle authentication,
encryption, tenant data, and SQL — treat security issues as blocking.

## Secrets

- **No secrets in source** — no passwords, keys, tokens, keystores' passphrases.
  Use configuration/JBoss vault / environment, as the existing bootstrap code does.
- Don't log secrets, password hashes, salts, or full tokens.

## SQL / Oracle

- The query framework (`model.common.query.clause.*`) parameterizes values via
  bind clauses — **use it**. Never concatenate user input into SQL strings.
- Multi-tenant code: respect `TenantUtils` / tenant scoping; never bypass the
  tenant filter on a query that should be scoped.

## Authentication & crypto

- Use the existing `AuthenticationUtils` (SHA-256 + salt) and encryption
  utilities; don't hand-roll crypto or invent a new hashing scheme.
- Constant-time comparison for secrets where the utility provides it.

## Authorization (Angular)

- Screen/feature access goes through `AuthorizationService` and the
  `IAuthorizationSetting` flags (`canView/canAdd/canEdit/canDelete` + the
  `*Beperking` restrictions). Don't render or enable actions the user isn't
  authorized for; check on the **server** side too — client checks are UX only.

## Input validation

- Validate entities through the `EntityValidator` framework before persistence.
- TypeScript: validate/narrow `unknown` external input (HTTP responses) before
  use — don't cast to a type you haven't checked.

## Dependencies — WARNING

- New dependencies are added deliberately and downstream-conscious (see the
  `compileOnly` strategy in `nxs-model`'s readme — avoid bloating bundle size).
