export const MODULES = [
  'plugpag',
  'stone',
  'paystore',
  'sunmi',
  'pax',
  'gertec',
] as const;

export type Modules = (typeof MODULES)[number];
