import React, { FunctionComponent, ReactElement } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

import routes from 'routes';

import Dashboard from 'pages/Dashboard/Dashboard';
import Boxes from 'pages/Boxes/Boxes';
import Box from 'pages/Box/Box';
import Delivery from 'pages/Delivery/Delivery';
import Orders from 'pages/Customer/Orders';

type PrivateRouteProps = {
  children: ReactElement,
}

const PrivateRoute: FunctionComponent<PrivateRouteProps> = ({ children }) => {

  const loggedIn = true; // Later use Redux to fetch logged in status
  return loggedIn
    ? children
    : <Navigate to="/login" />;
};

const Router = () => {

  // eslint-disable-next-line no-unused-vars
  const loggedIn = true; // Later use Redux to fetch logged in status

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to={routes.dashboard.def} />} />

        <Route path={routes.dashboard.def} element={
          <PrivateRoute>
            <Dashboard />
          </PrivateRoute>
        }
        />

        <Route path={routes.boxes.def} element={
          <PrivateRoute>
            <Boxes />
          </PrivateRoute>
        }
        />

        <Route path={routes.box.def} element={
          <PrivateRoute>
            <Box />
          </PrivateRoute>
        }
        />

        <Route path={routes.deliveries.def} element={
          <PrivateRoute>
            <Boxes />
          </PrivateRoute>
        }
        />

        <Route path={routes.delivery.def} element={
          <PrivateRoute>
            <Delivery />
          </PrivateRoute>
        }
        />
        <Route path={routes.customer.def} element={
          <PrivateRoute>
            <Orders></Orders>
          </PrivateRoute>
        }
        />

      </Routes>
    </BrowserRouter>
  );
};

export default Router;
