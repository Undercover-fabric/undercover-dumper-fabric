# undercover-dumper

Java agent. Intercepts class loading, writes `.class` files to disk.

## build

```
build.bat
```

Prebuilt jar is included.

## usage

1. Add a JVM arg from `setup.bat` or `config.example.txt` to your MC profile.
2. Replace `MOD_DUMPER_DIR` with the install path.
3. Launch with the mod loaded.
4. Output: `dumped_classes/`
5. `pack_dump.bat` -> `dumped.jar`

`setup.bat local` prints filled paths for the current machine.

## modes

- `mod` - default. obfuscated runtime names + `include:` prefixes.
- `obfuscated` - short/hash-like names only.
- `include` - `include:` prefixes only.
- `all` - non-excluded classes.

Options are appended after the output dir, separated by `|`:

- `mode:all`
- `include:com/foo,dev/bar`
- `exclude:some/lib`

## requirements

JDK 8+ to build. Launcher must allow custom JVM arguments.

Site: https://undercover-fabric.github.io/undercover-dumper/
Repo: https://github.com/Undercover-fabric/undercover-dumper