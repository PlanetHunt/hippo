% plot Velocities
figure
title('velocity in ECI frame')
plot(timeVector(2:end),vel(:,2:end));
legend('Veci x','Veci y','Veci z');

vel_eci_mag = sqrt(sum(abs(vel).^2,1));
delta_vel_eci_mag = vel_eci_mag(2:end)-vel_eci_mag(1:end-1);
figure
plot(timeVector(2:end),vel_eci_mag(2:end))



for kk = 1:length(vel)
    vel_LVLH(:,kk) = (LVLH2ECICharles(pos(:,kk), vel(:,kk)))'*vel(:,kk);%ECI to LVLH
end
figure
title('velocity in LVLH frame')
plot(timeVector(2:end),vel_LVLH(:,2:end))
legend('Vx','Vy','Vz')