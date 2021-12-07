import { configureStore } from '@reduxjs/toolkit';
import listReducer from 'redux/slices/boxes/boxesSlice';
import boxReducer from 'redux/slices/box/boxSlice';
import deliveryListReducer from 'redux/slices/delivery/deliveriesSlice';
import deliveryReducer from 'redux/slices/delivery/deliverySlice';

export const store = configureStore({
  reducer: {
    boxes: listReducer,
    box: boxReducer,
    deliveries: deliveryListReducer,
    delivery: deliveryReducer,
  },
});
