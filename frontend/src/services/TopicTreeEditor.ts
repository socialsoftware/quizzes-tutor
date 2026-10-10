// What a tree view needs: a title and the items below
export interface TreeItem {
  id: string | number;
  title: string;
  children: TreeItem[];
}

/**
 * Nests a flat list of nodes under their parents, keeping the order of the list among siblings.
 * A node whose parent is not in the list is shown at the top instead of being lost.
 */
export function buildForest<T>(
  nodes: T[],
  id: (node: T) => string | number,
  parent: (node: T) => string | number | null,
  title: (node: T) => string
): TreeItem[] {
  const items = new Map<string | number, TreeItem>();
  nodes.forEach((node) => items.set(id(node), { id: id(node), title: title(node), children: [] }));

  const roots: TreeItem[] = [];
  nodes.forEach((node) => {
    const item = items.get(id(node))!;
    const parentId = parent(node);
    const parentItem = parentId === null ? undefined : items.get(parentId);
    if (parentItem && parentItem !== item) parentItem.children.push(item);
    else roots.push(item);
  });
  return roots;
}
