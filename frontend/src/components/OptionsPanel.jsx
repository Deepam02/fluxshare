import React from 'react';
import {
  TextField,
  MenuItem,
  FormControlLabel,
  Switch,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Typography,
  Grid,
} from '@mui/material';
import { ExpandMore } from '@mui/icons-material';
import { EXPIRY_OPTIONS } from '../utils/constants.jsx';

const OptionsPanel = ({ options, setOptions, activeTab }) => {
  const handleChange = (field, value) => {
    setOptions((prev) => ({ ...prev, [field]: value }));
  };

  return (
    <Accordion defaultExpanded sx={{ mt: 3 }}>
      <AccordionSummary expandIcon={<ExpandMore />}>
        <Typography variant="subtitle1" fontWeight="medium">
          Share Options
        </Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6}>
            <TextField
              select
              fullWidth
              label="Expiry Time"
              value={options.expiryHours}
              onChange={(e) => handleChange('expiryHours', e.target.value)}
              size="small"
            >
              {EXPIRY_OPTIONS.map((option) => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </TextField>
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField
              fullWidth
              label="Password (Optional)"
              type="password"
              value={options.password}
              onChange={(e) => handleChange('password', e.target.value)}
              size="small"
              placeholder="Leave empty for no password"
            />
          </Grid>

          <Grid item xs={12}>
            <FormControlLabel
              control={
                <Switch
                  checked={options.viewOnce}
                  onChange={(e) => handleChange('viewOnce', e.target.checked)}
                />
              }
              label="Delete after first view"
            />
          </Grid>

          {activeTab === 'file' && (
            <>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Max Downloads (Optional)"
                  type="number"
                  value={options.maxDownloads}
                  onChange={(e) => handleChange('maxDownloads', e.target.value)}
                  size="small"
                  placeholder="Unlimited"
                  inputProps={{ min: 1 }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Max Views (Optional)"
                  type="number"
                  value={options.maxViews}
                  onChange={(e) => handleChange('maxViews', e.target.value)}
                  size="small"
                  placeholder="Unlimited"
                  inputProps={{ min: 1 }}
                />
              </Grid>
            </>
          )}

          {(activeTab === 'text' || activeTab === 'code') && (
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Max Views (Optional)"
                type="number"
                value={options.maxViews}
                onChange={(e) => handleChange('maxViews', e.target.value)}
                size="small"
                placeholder="Unlimited"
                inputProps={{ min: 1 }}
              />
            </Grid>
          )}

          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Notes (Optional)"
              multiline
              rows={2}
              value={options.notes}
              onChange={(e) => handleChange('notes', e.target.value)}
              size="small"
              placeholder="Add any notes about this share"
            />
          </Grid>
        </Grid>
      </AccordionDetails>
    </Accordion>
  );
};

export default OptionsPanel;
