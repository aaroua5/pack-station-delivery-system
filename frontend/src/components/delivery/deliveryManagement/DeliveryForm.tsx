import * as React from 'react';

import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Grid, TextField } from '@mui/material';
import api from 'services/api';

type FormProps = {
  isOpen: boolean,
  close: () => void,
  initialData: any,
  title: string,
  description: string,
  btnText: string,
  // eslint-disable-next-line no-unused-vars
  handler: (formData: any) => void
};

const DeliveryForm = ({ isOpen, close, initialData, title, description, btnText, handler }: FormProps) => {

  const [formData, setFormData] = React.useState(initialData);

  const [errors, setErrors] = React.useState(
    {
      boxIdErrors: '',
      delivererIdErrors: '',
      customerIdErrors: '',
      deliveryDescriptionErrors: '',
    }
  );

  const handleFormDataChange = (change: any) => {
    setFormData({ ...formData, ...change });
  };

  const checkUser = (value: string, type: string) => {
    if (!value || value === '') {
      return;
    }

    const requestCallback = (data: any) => {
      if (data.role !== type) {
        setErrors({ ...errors, ...{ customerIdErrors: 'No user with given ID' } });
        if (type === 'customer') {
          handleFormDataChange({ customerName: '' });
        } else {
          handleFormDataChange({ delivererName: '' });
        }
        return;
      }
      setErrors({ ...errors, ...{ customerIdErrors: '' } });
      if (type === 'customer') {
        handleFormDataChange({ customerName: `${data.firstName} ${data.surname}` });
      } else {
        handleFormDataChange({ delivererName: `${data.firstName} ${data.surname}` });
      }
    };
    api.getUser(value, requestCallback);
  };

  const checkBox = (value: string) => {
    if (!value || value === '') {
      return;
    }
    const requestCallback = (data: any) => {
      // TODO: check if box is free or contains only deliveries from customer

      if (!data.id) {
        setErrors({ ...errors, ...{ boxIdErrors: 'No box with given ID' } });
        return;
      }
      setErrors({ ...errors, ...{ boxIdErrors: '' } });
      handleFormDataChange({ boxName: data.name });
    };
    api.getBox(value, requestCallback);
  };

  const clicked = () => {
    let anyError = false;
    const newErrors: any[] = [];

    if (!formData.delivererId || formData.delivererId === '') {
      newErrors.push({ delivererIdErrors: 'Field is required' });
      anyError = true;
    } else {
      newErrors.push({ delivererIdErrors: '' });
    }

    if (!formData.boxId || formData.boxId === '') {
      newErrors.push({ boxIdErrors: 'Field is required' });
      anyError = true;
    } else {
      newErrors.push({ boxIdErrors: '' });
    }

    if (!formData.customerId || formData.customerId === '') {
      newErrors.push({ customerIdErrors: 'Field is required' });
      anyError = true;
    } else {
      newErrors.push({ customerIdErrors: '' });
    }

    if (anyError) {
      let newErrorObj = {};

      newErrors.forEach(
        (obj) => {
          newErrorObj = { ...newErrorObj, ...obj };
        }
      );

      setErrors({ ...errors, ...newErrorObj });
      return;
    }

    handler(formData);
    close();
  };

  return (
    <Dialog open={isOpen} onClose={close}>
      <DialogTitle>
        {title}
      </DialogTitle>
      <DialogContent>
        <DialogContentText>
          {description}
        </DialogContentText>
        <Grid container spacing={3}>
          <Grid item xs={12} sm={12}>
            <TextField
              required
              autoFocus
              margin="dense"
              id="cId"
              label="Customer ID"
              type="name"
              fullWidth
              value={formData.customerId}
              onBlur={(e) => checkUser(e.target.value, 'customer')}
              onChange={(e) => handleFormDataChange({ customerId: e.target.value })}
              variant="outlined"
              error={errors.customerIdErrors !== ''}
              helperText={errors.customerIdErrors}
            />
          </Grid>
          <Grid item xs={12} sm={12}>
            <TextField
              autoFocus
              margin="dense"
              id="cName"
              label="Customer Name"
              type="name"
              fullWidth
              value={formData.customerName}
              variant="outlined"
              disabled
            />
          </Grid>
          <Grid item xs={12} sm={12}>
            <TextField
              required
              autoFocus
              margin="dense"
              id="dId"
              label="Deliverer ID"
              type="name"
              fullWidth
              value={formData.delivererId}
              onChange={(e) => handleFormDataChange({ delivererId: e.target.value })}
              onBlur={(e) => checkUser(e.target.value, 'deliverer')}
              variant="outlined"
              error={errors.delivererIdErrors !== ''}
              helperText={errors.delivererIdErrors}
            />
          </Grid>
          <Grid item xs={12} sm={12}>
            <TextField
              autoFocus
              margin="dense"
              id="dName"
              label="Deliverer Name"
              type="name"
              fullWidth
              value={formData.delivererName}
              variant="outlined"
              disabled
            />
          </Grid>
          <Grid item xs={12} sm={12}>
            <TextField
              required
              autoFocus
              margin="dense"
              id="bId"
              label="Box ID"
              type="name"
              fullWidth
              value={formData.boxId}
              onChange={(e) => handleFormDataChange({ boxId: e.target.value })}
              onBlur={(e) => checkBox(e.target.value)}
              variant="outlined"
              error={errors.boxIdErrors !== ''}
              helperText={errors.boxIdErrors}
            />
          </Grid>
          <Grid item xs={12} sm={12}>
            <TextField
              autoFocus
              margin="dense"
              id="bName"
              label="Box Name"
              type="name"
              fullWidth
              value={formData.boxName}
              variant="outlined"
              disabled
            />
          </Grid>
          <Grid item xs={12} sm={12}>
            <TextField
              autoFocus
              margin="dense"
              id="desc"
              label="Delivery description"
              type="name"
              multiline
              fullWidth
              onChange={(e) => handleFormDataChange({ deliveryDescription: e.target.value })}
              value={formData.deliveryDescription}
              variant="outlined"
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={close}>Cancel</Button>
        <Button onClick={() => {
          clicked();
        }}
        >
          {btnText}
        </Button>
      </DialogActions>
    </Dialog>);
};

export default DeliveryForm;
