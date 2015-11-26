% Simple control:

%% Update orbit data:
a_all = [a_all; a];
e_all = [e_all;e];
in_all = [in_all; i];
omega_all = [omega_all; omega];

raan_all = [raan_all; raan];
mean_anomaly_all = [mean_anomaly_all; M_a];
matlabtime = datenum(Time(:,:),'yyyy-mm-ddTHH:MM:SS');
Time_all = [Time_all; matlabtime - datenum(date)];

% Plot orbital elements:
figure(3)
ax(1) = subplot(6,1,1);
plot(Time_all, a_all);
ylabel('semi-major axis [m]');
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
plot(Time_all, mean_anomaly_all);
ylabel('Mean Anomaly [rad]');
xlabel('Time [d]');
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

