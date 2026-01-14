# Design: Fix Shellcheck Warnings

## Common Fixes

### SC2086: Double Quote Variables

```bash
# Before (SC2086)
echo $VARIABLE
run: echo "value" >> $GITHUB_OUTPUT

# After
echo "$VARIABLE"
run: echo "value" >> "$GITHUB_OUTPUT"
```

### SC2046: Quote Command Substitutions

```bash
# Before (SC2046)
cmake --build build -j $(nproc)

# After
cmake --build build -j "$(nproc)"
```

### SC2035: Glob Patterns with Dashes

```bash
# Before (SC2035)
ls *.tar.gz

# After
ls ./*.tar.gz
```

---

## Affected Files and Fixes

### bazel_multi_ci.yml

```yaml
# Line 92 - Before
run: |
  bazel build $TARGETS

# After
run: |
  bazel build "$TARGETS"
```

### bazel_multi_release.yml

```yaml
# Line 99, 198 - Before
run: echo "has_nix=${{ hashFiles('flake.nix') != '' }}" >> $GITHUB_OUTPUT

# After
run: echo "has_nix=${{ hashFiles('flake.nix') != '' }}" >> "$GITHUB_OUTPUT"

# Line 129, 228 - Before
mv *.tar.gz dist/

# After
mv ./*.tar.gz dist/
```

### cmake_*_ci.yml / cmake_*_release.yml

```yaml
# Before
run: cmake --build build -j $(nproc)

# After
run: cmake --build build -j "$(nproc)"

# Before
run: cmake --build build -j $(sysctl -n hw.ncpu || nproc)

# After
run: cmake --build build -j "$(sysctl -n hw.ncpu || nproc)"
```

---

## Testing

1. Run actionlint locally:
   ```bash
   actionlint .github/workflows/*.yml
   ```

2. Verify no SC2086, SC2046, SC2035 warnings remain

3. Push and verify CI passes
