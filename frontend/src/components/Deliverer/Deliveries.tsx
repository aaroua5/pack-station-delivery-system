import { Divider } from '@mui/material';
import React from 'react';
import { Deliverer, Delivery } from 'types';
import { CurrentDeliveriesTable, PastDeliveriesTable } from 'components/common/DeliveriesTable/DeliveriesTable';
import './styles.scss';
type delivererProps = {
  deliverer: Deliverer,
  deliveries: Delivery[]
}

const deliveries = ({ deliverer, deliveries }: delivererProps) => {
  return (
    <div id="customer">
      <div id="customerInfo">
        <h3 className="subTitle">
          Deliverer Information:
        </h3>
        <p>
          <b className="infoTag">Id: </b>
          {' '}
          {deliverer.id}
        </p>
        <p>
          <b className="infoTag">Name: </b>
          {' '}
          {deliverer.name}
        </p>
      </div>

      <Divider />
      <section className="currentStatus">
        <h4 className="sectionTitle">
          Active deliveries:
        </h4>

        <CurrentDeliveriesTable deliveries={deliveries} showBox={true} />

        <h4 className="sectionTitle">
          Past deliveries:
        </h4>

        <PastDeliveriesTable deliveries={deliveries} showBox={true} />

      </section >
    </div>
  );
};

export default deliveries;

