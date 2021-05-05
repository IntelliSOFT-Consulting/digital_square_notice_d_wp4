import React from "react";
import styles from "./import-map.styles.css";

export default function ImportMap(props: ImportMapProps) {
  const importMapListRef = React.useRef<HTMLElement>(null);

  React.useEffect(() => {
    window.addEventListener(
      "import-map-overrides:change",
      handleImportMapChange
    );
    return () =>
      window.removeEventListener(
        "import-map-overrides:change",
        handleImportMapChange
      );

    function handleImportMapChange(evt) {
      props.toggleOverridden(importMapOverridden());
    }
  }, [importMapListRef.current]);

  return (
    <div className={styles.importMap}>
      <import-map-overrides-list
        ref={importMapListRef}
      ></import-map-overrides-list>
    </div>
  );
}

export function importMapOverridden(): boolean {
  return (
    Object.keys((window as any).importMapOverrides.getOverrideMap().imports)
      .length > 0
  );
}

export function isOverriddenInImportMap(esmName: string): boolean {
  return (window as any).importMapOverrides
    .getOverrideMap()
    .imports.hasOwnProperty(esmName);
}

type ImportMapProps = {
  toggleOverridden(overridden: boolean): void;
};
