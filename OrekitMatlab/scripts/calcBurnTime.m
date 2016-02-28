function [ burnTime ] = calcBurnTime( dV, fullMass, Isp, thrust, burnTimeLimit, numThrusters )
%CALCBURNTIME calculates thrusting duration required (burn time) to achieve a given
%delta V (basic rocket equation)
%   dV is a vector [dVx; dVy; dVz]
%   burnTime (s)
%   Isp (s) and Thrust (N) correspond to the selected operating point of the
%   thrusters.
%   burnTimeLimit is the max burn time we allow for an 'impulse' maneuvre
%   (180seconds for LEO)? - check reference of josh newman in canX paper /
%   ask Alex
% % % % % global g
% % % % % 
% % % % % emptyMass       = fullMass/(exp(norm(dV)/(Isp*g)));
% % % % % propellantMass  = fullMass - emptyMass;
% % % % % massFlowRate    = numThrusters*thrust/(Isp*g);
% % % % % burnTime        = propellantMass/massFlowRate;
% % % % % 
% % % % % %required burn time greater than limit, set to limit
% % % % % if(burnTime > burnTimeLimit);
% % % % %     burnTime = burnTimeLimit;
% % % % % end
burnTime = 180;
end

