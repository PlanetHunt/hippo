% Simple control:

%% Update orbit data:
a_all = [a_all; a];
e_all = [e_all;e];
in_all = [in_all; in];
omega_all = [omega_all; omega];

raan_all = [raan_all; raan];
mean_anomaly_all = [mean_anomaly_all; M_a];
true_anomaly_all = [true_anomaly_all; T_a];
filter = true_anomaly_all<0;
true_anomaly_all(filter) = true_anomaly_all(filter)+2*pi;
matlabtime = datenum(Time(:,:),'yyyy-mm-ddTHH:MM:SS');
Time_all = [Time_all; matlabtime - datenum(start_date)];

% Plot orbital elements (osculating):
figure(3)
ax(1) = subplot(6,1,1);
plot(Time_all, a_all*1e-3);
ylabel('semi-major axis [km]');
ax(2) = subplot(6,1,2);
plot(Time_all, e_all);
ylabel('eccentricity');
ax(3) = subplot(6,1,3);
plot(Time_all, in_all);
ylabel('inclination [rad]');
ax(4) = subplot(6,1,4);
plot(Time_all, unwrap(omega_all));
ylabel('Perigee Argument [rad]'); 
ax(5) = subplot(6,1,5);
plot(Time_all, raan_all);
ylabel('Right Ascention of Ascending Node');
ax(6) = subplot(6,1,6);
plot(Time_all, true_anomaly_all);
ylabel('True Anomaly [rad]');
xlabel('Time [d]');
title('Osculating orbital elements');
linkaxes(ax,'x');

% Convert osculating elements to mean elements:
global req j2

req = 6378.14;
j2 = 1.08262668355e-3;
oemean = zeros(length(a_all),6);

for xa= 1:length(a_all)
    oemean(xa,:) = osc2mean([a_all(xa)*1e-3 e_all(xa) in_all(xa) rem(omega_all(xa),2*pi) raan_all(xa) true_anomaly_all(xa)]);
end

% Plot orbital elements (mean):
figure(4)
ax(1) = subplot(6,1,1);
plot(Time_all, oemean(:,1));
ylabel('semi-major axis [km]');
ax(2) = subplot(6,1,2);
plot(Time_all, oemean(:,2));
ylabel('eccentricity');
ax(3) = subplot(6,1,3);
plot(Time_all, oemean(:,3));
ylabel('inclination [rad]');
ax(4) = subplot(6,1,4);
plot(Time_all, oemean(:,4));
ylabel('Perigee Argument [rad]'); 
ax(5) = subplot(6,1,5);
plot(Time_all, oemean(:,5));
ylabel('Right Ascention of Ascending Node');
ax(6) = subplot(6,1,6);
plot(Time_all, oemean(:,6));
ylabel('True Anomaly [rad]');
xlabel('Time [d]');
title('Osculating orbital elements');
linkaxes(ax,'x');

%% Control semi major axis:

if (a_target<a_all(end)-a_tolerance)
    % Reduce velocity around Apogee and Perigee
    if (mean_anomaly_all(end)<0)
        % Perigee command:
        
    elseif (mean_anomaly_all(end)>=0)
        % Apogee command:
        [start_time, duration] = calc_apogee_command(matlabtime, M_a', per_hoh_comm)
    end
    notDone = true
else 
    notDone = false
end

