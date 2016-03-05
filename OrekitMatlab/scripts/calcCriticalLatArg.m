function [ theta_crit ] = calcCriticalLatArg( oeDeputy,oeerror )
%CALCCRITICALLATARG Summary of this function goes here
%   Detailed explanation goes here

global mu;
%a = oeDeputy(1);
%e = oeDeputy(2);
i = oeDeputy(3);
%omega = oeDeputy(4);
%raan = oeDeputy(5);
%true_anomaly = oeDeputy(6);
%M = oeDeputy(7);

%d_a = oeerror(1);
%d_e = oeerror(2);
d_i = oeerror(3);
%d_omega = oeerror(4);
d_raan = oeerror(5);
%d_true_anomaly = oeerror(6);
%d_M = oeerror(7);


theta_crit = atan2(d_raan*sin(i),d_i);
%%% this line may need checking
if(theta_crit<0)
    theta_crit = pi+theta_crit;
end
%theta_crit
%theta_crit_degrees=rad2deg(theta_crit)
end

