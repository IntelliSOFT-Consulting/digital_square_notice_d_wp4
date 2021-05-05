import React from "react";

export const ExtensionContext = React.createContext({
  extensionSlotName: "",
  extensionId: "",
  extensionModuleName: "",
});

export const ModuleNameContext = React.createContext(null);
