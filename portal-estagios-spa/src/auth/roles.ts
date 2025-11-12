export function hasRole(roles: string[] | undefined, role: string) {
  return roles?.includes(role) ?? false;
}