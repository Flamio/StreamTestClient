package streamServerTestClient.Utils;

import streamServerTestClient.Models.CheckParameters;

public interface IPackageProvider {
	byte[] build(CheckParameters parameters);
	boolean isRightHeader(byte[] data);
	boolean check(CheckParameters parameters, byte[] data);
}
