import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Landing from './pages/Landing';
import CustomerLogin from './pages/CustomerLogin';
import CustomerRegister from './pages/CustomerRegister';
import CustomerDashboard from './pages/CustomerDashboard';
import DriverLogin from './pages/DriverLogin';
import DriverRegister from './pages/DriverRegister';
import DriverDocumentVerification from './pages/DriverDocumentVerification';
import DriverVehicleDetails from './pages/DriverVehicleDetails';
import DriverDashboard from './pages/DriverDashboard';
import ValidatorLogin from './pages/ValidatorLogin';
import ValidatorDashboard from './pages/ValidatorDashboard';
import './index.css';

function App() {
  return (
    <Router>
      <Routes>
        {/* Landing */}
        <Route path="/" element={<Landing />} />

        {/* Customer Routes */}
        <Route path="/customer/login" element={<CustomerLogin />} />
        <Route path="/customer/register" element={<CustomerRegister />} />
        <Route path="/customer/dashboard" element={<CustomerDashboard />} />

        {/* Driver Routes */}
        <Route path="/driver/login" element={<DriverLogin />} />
        <Route path="/driver/register" element={<DriverRegister />} />
        <Route path="/driver/documents" element={<DriverDocumentVerification />} />
        <Route path="/driver/vehicle" element={<DriverVehicleDetails />} />
        <Route path="/driver/dashboard" element={<DriverDashboard />} />

        {/* Validator Routes (hidden from landing) */}
        <Route path="/validator/login" element={<ValidatorLogin />} />
        <Route path="/validator/dashboard" element={<ValidatorDashboard />} />
      </Routes>
    </Router>
  );
}

export default App;

