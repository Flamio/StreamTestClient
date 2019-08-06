package streamServerTestClient.Utils;

import streamServerTestClient.Models.CheckParameters;

public interface IPackageBuilder {
	byte[] build(CheckParameters parameters);
	boolean check(CheckParameters parameters, byte[] data);
}
