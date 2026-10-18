export const MODULES = [
  'plugpag',
  'stone',
  'paystore',
  'sunmi',
  'pax',
  'gertec',
  'tectoy',
] as const;

export type Modules = (typeof MODULES)[number];
