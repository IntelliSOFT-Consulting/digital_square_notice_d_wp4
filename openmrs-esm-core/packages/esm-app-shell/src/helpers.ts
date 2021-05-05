export function routePrefix(prefix: string, location: Location) {
  return location.pathname.startsWith(window.getOpenmrsSpaBase() + prefix);
}

export function routeRegex(regex: RegExp, location: Location) {
  const result = regex.test(
    location.pathname.replace(window.getOpenmrsSpaBase(), "")
  );
  return result;
}
