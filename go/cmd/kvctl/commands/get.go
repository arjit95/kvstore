package commands

import (
	"errors"

	"github.com/arjit95/kvstore/go/cmd/kvctl/factory"
	"github.com/spf13/cobra"
)

var errNotFound error = errors.New("key not found")

func CreateGetCommand(f *factory.Factory) *cobra.Command {
	return &cobra.Command{
		Use:                   "get",
		Short:                 "Retrieves the key from server",
		Args:                  cobra.ExactArgs(1),
		DisableFlagsInUseLine: true,
		RunE: func(cmd *cobra.Command, args []string) error {
			key := args[0]
			var e error
			for _, client := range f.Clients {
				val, err := client.Get(key)
				if err != nil {
					e = err
					continue
				}

				if len(val) == 0 {
					e = errNotFound
					continue
				}

				cmd.Printf("%s \n", string(val))
				return nil
			}

			return e
		},
	}
}
