if [ ! -z $(git diff --name-only HEAD HEAD~1 | grep build.gradle) ]; then
  new_version="$(git log -n1 --format=format:"%H")"
  sed --in-place "s!^//version:.*!//version: $new_version!g" build.gradle
  git add build.gradle
  git commit -m "[ci skip] update build script version to $new_version"
  git push
  echo "Updated buildscript version to $new_version"; 
else
  echo "Ignored buildscript version update: no changes detected"
fi
