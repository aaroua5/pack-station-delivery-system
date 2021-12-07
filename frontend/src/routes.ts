const route = (def: string, ref?: (..._args: any[]) => string ) => ({
  def: def,
  ref: ref || (() => def),
});

const routes = {
  /* Login */
  login: route('/login'),

  /* General */
  dashboard: route('/dashboard'),

  delivery: route(
    '/delivery/:deliveryId',
    (deliveryId) => (`/delivery/${deliveryId}`)
  ),

  boxes: route(
    '/box',
  ),

  box: route(
    '/box/:boxId',
    (boxId) => (`/box/${boxId}`)
  ),

  customer: route(
    '/customer/:customerId/orders',
    (customerId) => (`/customer/${customerId}/orders`)
  ),

};

export default routes;