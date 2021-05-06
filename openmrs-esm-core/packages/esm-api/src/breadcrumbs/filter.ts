import { getBreadcrumbs } from "./db";
import { BreadcrumbRegistration } from "./types";

function getExact(
  breadcrumbs: Array<BreadcrumbRegistration>,
  path: string
): BreadcrumbRegistration {
  const [bc] = breadcrumbs.filter((m) => m.matcher.test(path));
  return bc;
}

function getClosest(
  breadcrumbs: Array<BreadcrumbRegistration>,
  path: string
): BreadcrumbRegistration | undefined {
  const segments = path.split("/");

  while (segments.length > 1) {
    segments.pop();
    const newPath = segments.join("/");
    const next = getNext(breadcrumbs, newPath);

    if (next) {
      return next;
    }
  }

  return undefined;
}

function getNext(
  breadcrumbs: Array<BreadcrumbRegistration>,
  path: string | undefined
) {
  if (path) {
    return getExact(breadcrumbs, path) || getClosest(breadcrumbs, path);
  }

  return undefined;
}

export function filterBreadcrumbs(
  list: Array<BreadcrumbRegistration>,
  path: string
) {
  if (list.length > 0) {
    const current = getNext(list, path);

    if (current) {
      const links = [current];
      let previous = getNext(list, current.settings.parent);

      while (previous !== undefined) {
        links.push(previous);
        previous = getNext(list, previous.settings.parent);
      }

      return links.reverse();
    }
  }

  return [];
}

export function getBreadcrumbsFor(path: string) {
  const breadcrumbs = getBreadcrumbs();
  return filterBreadcrumbs(breadcrumbs, path);
}
