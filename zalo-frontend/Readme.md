debug:
npm run build
npx cap sync
ionic cap run android -l --external

chrome://inspect/#devices

export apk:
npm run build
npx cap copy android
npx cap sync android

npx cap open android  