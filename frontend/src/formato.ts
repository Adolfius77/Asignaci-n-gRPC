const moneda = new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' });

export const dinero = (n: number) => moneda.format(n);
