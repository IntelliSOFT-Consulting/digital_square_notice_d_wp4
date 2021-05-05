import React from "react";
import { render, cleanup, screen } from "@testing-library/react";
import {
  clearAll,
  defineConfigSchema,
  setTemporaryConfigValue,
  clearConfigCache,
  provide,
} from "@openmrs/esm-config";
import { ModuleNameContext } from "./ModuleNameContext";
import { useConfig } from "./useConfig";
import { ExtensionContext } from "./ExtensionContext";

function RenderConfig(props) {
  const config = useConfig();

  return <button>{config[props.configKey]}</button>;
}

describe(`useConfig in root context`, () => {
  afterEach(clearAll);
  afterEach(cleanup);
  afterEach(clearConfigCache);

  it(`can return config as a react hook`, async () => {
    defineConfigSchema("foo-module", {
      thing: {
        _default: "The first thing",
      },
    });

    render(
      <React.Suspense fallback={<div>Suspense!</div>}>
        <ModuleNameContext.Provider value="foo-module">
          <RenderConfig configKey="thing" />
        </ModuleNameContext.Provider>
      </React.Suspense>
    );

    expect(await screen.findByText("The first thing")).toBeTruthy();
  });

  it(`can handle multiple calls to useConfig from different modules`, async () => {
    defineConfigSchema("foo-module", {
      thing: {
        _default: "foo thing",
      },
    });

    defineConfigSchema("bar-module", {
      thing: {
        _default: "bar thing",
      },
    });

    render(
      <React.Suspense fallback={<div>Suspense!</div>}>
        <ModuleNameContext.Provider value="foo-module">
          <RenderConfig configKey="thing" />
        </ModuleNameContext.Provider>
      </React.Suspense>
    );

    expect(await screen.findByText("foo thing")).toBeTruthy();

    await cleanup();

    render(
      <React.Suspense fallback={<div>Suspense!</div>}>
        <ModuleNameContext.Provider value="bar-module">
          <RenderConfig configKey="thing" />
        </ModuleNameContext.Provider>
      </React.Suspense>
    );

    expect(await screen.findByText("bar thing")).toBeTruthy();
  });

  it("updates with a new value when the temporary config is updated", async () => {
    defineConfigSchema("foo-module", {
      thing: {
        _default: "The first thing",
      },
    });

    render(
      <React.Suspense fallback={<div>Suspense!</div>}>
        <ModuleNameContext.Provider value="foo-module">
          <RenderConfig configKey="thing" />
        </ModuleNameContext.Provider>
      </React.Suspense>
    );

    expect(await screen.findByText("The first thing")).toBeTruthy();

    setTemporaryConfigValue(["foo-module", "thing"], "A new thing");

    expect(await screen.findByText("A new thing")).toBeTruthy();
  });
});

describe(`useConfig in an extension`, () => {
  afterEach(clearAll);
  afterEach(cleanup);
  afterEach(clearConfigCache);

  it(`can return extension config as a react hook`, async () => {
    defineConfigSchema("ext-module", {
      thing: {
        _default: "The basics",
      },
    });

    render(
      <React.Suspense fallback={<div>Suspense!</div>}>
        <ExtensionContext.Provider
          value={{
            extensionModuleName: "ext-module",
            actualExtensionSlotName: "fooSlot",
            attachedExtensionSlotName: "fooSlot",
            extensionSlotModuleName: "slot-mod",
            extensionId: "barExt#id1",
          }}
        >
          <RenderConfig configKey="thing" />
        </ExtensionContext.Provider>
      </React.Suspense>
    );

    expect(await screen.findByText("The basics")).toBeTruthy();
  });

  it(`can handle multiple extensions`, async () => {
    defineConfigSchema("first-module", {
      thing: {
        _default: "first thing",
      },
    });

    defineConfigSchema("second-module", {
      thing: {
        _default: "second thing",
      },
    });

    render(
      <React.Suspense fallback={<div>Suspense!</div>}>
        <ExtensionContext.Provider
          value={{
            actualExtensionSlotName: "fooSlot",
            attachedExtensionSlotName: "fooSlot",
            extensionSlotModuleName: "slot-mod",
            extensionModuleName: "first-module",
            extensionId: "fooExt#id1",
          }}
        >
          <RenderConfig configKey="thing" />
        </ExtensionContext.Provider>
        <ExtensionContext.Provider
          value={{
            actualExtensionSlotName: "fooSlot",
            attachedExtensionSlotName: "fooSlot",
            extensionSlotModuleName: "slot-mod",
            extensionModuleName: "second-module",
            extensionId: "barExt",
          }}
        >
          <RenderConfig configKey="thing" />
        </ExtensionContext.Provider>
      </React.Suspense>
    );

    expect(await screen.findByText("first thing")).toBeTruthy();
    expect(await screen.findByText("second thing")).toBeTruthy();
  });

  it("can handle multiple extension slots", async () => {
    defineConfigSchema("extension-module", {
      thing: {
        _default: "old extension thing",
      },
    });

    provide({
      "slot-2-module": {
        extensions: {
          slot2: {
            configure: {
              fooExt: { thing: "a different thing" },
            },
          },
        },
      },
    });

    render(
      <React.Suspense fallback={<div>Suspense!</div>}>
        <ExtensionContext.Provider
          value={{
            actualExtensionSlotName: "slot1",
            attachedExtensionSlotName: "slot1",
            extensionSlotModuleName: "slot-1-module",
            extensionModuleName: "extension-module",
            extensionId: "fooExt",
          }}
        >
          <RenderConfig configKey="thing" />
        </ExtensionContext.Provider>
        <ExtensionContext.Provider
          value={{
            actualExtensionSlotName: "slot2",
            attachedExtensionSlotName: "slot2",
            extensionSlotModuleName: "slot-2-module",
            extensionModuleName: "extension-module",
            extensionId: "fooExt",
          }}
        >
          <RenderConfig configKey="thing" />
        </ExtensionContext.Provider>
      </React.Suspense>
    );

    expect(await screen.findByText("old extension thing")).toBeTruthy();
    expect(await screen.findByText("a different thing")).toBeTruthy();
  });

  it("updates with a new value when the temporary config is updated", async () => {
    defineConfigSchema("ext-module", {
      thing: {
        _default: "The first thing",
      },
    });

    render(
      <React.Suspense fallback={<div>Suspense!</div>}>
        <ExtensionContext.Provider
          value={{
            actualExtensionSlotName: "fooSlot",
            attachedExtensionSlotName: "fooSlot",
            extensionSlotModuleName: "slot-module",
            extensionModuleName: "ext-module",
            extensionId: "barExt#id1",
          }}
        >
          <RenderConfig configKey="thing" />
        </ExtensionContext.Provider>
      </React.Suspense>
    );

    expect(await screen.findByText("The first thing")).toBeTruthy();

    setTemporaryConfigValue(["ext-module", "thing"], "A new thing");

    expect(await screen.findByText("A new thing")).toBeTruthy();

    setTemporaryConfigValue(
      [
        "slot-module",
        "extensions",
        "fooSlot",
        "configure",
        "barExt#id1",
        "thing",
      ],
      "Yet another thing"
    );

    expect(await screen.findByText("Yet another thing")).toBeTruthy();
  });
});
